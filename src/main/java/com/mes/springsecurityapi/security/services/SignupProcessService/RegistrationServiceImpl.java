package com.mes.springsecurityapi.security.services.SignupProcessService;

import com.mes.springsecurityapi.domain.security.Authority;
import com.mes.springsecurityapi.domain.security.DTO.HttpResponse;
import com.mes.springsecurityapi.domain.security.DTO.UserDTO;
import com.mes.springsecurityapi.domain.security.Role;
import com.mes.springsecurityapi.domain.security.User;
import com.mes.springsecurityapi.repositories.security.UserRepository;
import com.mes.springsecurityapi.security.services.email.EmailService;
import com.mes.springsecurityapi.security.services.security.AuthorityService;
import com.mes.springsecurityapi.security.services.security.RoleAuthoritiesService;
import com.mes.springsecurityapi.security.services.security.RoleService;
import com.mes.springsecurityapi.security.services.security.UserRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

/**
 * Created by mesar on 12/28/2020
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class RegistrationServiceImpl implements RegistrationService {

    private final UserRepository userRepository;
    private final AuthorityService authorityService;
    private final RoleService roleService;
    private final RoleAuthoritiesService roleAuthoritiesService;
    private final UserRoleService userRolesService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public Mono<HttpResponse> registerClient(@NotNull UserDTO userDTO, ServerHttpRequest serverHttpRequest) {

        String origin = serverHttpRequest.getHeaders().getOrigin();
        return userRepository.findByUsername(userDTO.getEmail())
                .hasElement()
                .flatMap( hasElement -> {
                    if (hasElement){
                        log.debug("User: {} already exists.");
                        return createHttpResponse(HttpStatus.IM_USED,
                                HttpResponse.ResponseType.FAILURE,
                                "User already exists in our records!");
                    }else{
                        log.debug("Creating a new User: {}", userDTO);
                        return this.createRegistration(userDTO, "CLIENT", "client.rights")
                                .flatMap(user -> Mono.just(emailService.sendEmail(user, origin)));
                    }
                });
    }

    private Mono<User> createRegistration(UserDTO userDTO, String role, String authority) {

        Authority clientAuthority = Authority.builder().permission(authority).build();
        Role clientRole = Role.builder().name(role).build();

        String address = userDTO.getStreetName().strip() + ", "
                + userDTO.getHouseNumber().strip() + ", "
                + userDTO.getCity().strip() + ", "
                + userDTO.getCountry().strip() + ", "
                + userDTO.getZipcode().strip();


        String verificationToken = UUID.randomUUID().toString();
        Timestamp currentTime = Timestamp.from(Instant.now());
        Timestamp verificationTokenExpiresAt = Timestamp.from(currentTime.toInstant().plusSeconds(180));

        User user = User.builder()
                .username(userDTO.getEmail())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .firstName(userDTO.getFirstName())
                .middleName(userDTO.getMiddleName())
                .lastName(userDTO.getLastName())
                .createdDate(Timestamp.from(Instant.now()))
                .verificationToken(verificationToken)
                .verificationExpiresAt(verificationTokenExpiresAt)
                .isVerified(false)
                .lastModifiedDate(Timestamp.from(Instant.now()))
                .address(address)
                .build();

        roleService.saveOrUpdate(clientRole).subscribe();
        authorityService.saveOrUpdate(clientAuthority).subscribe();
        roleAuthoritiesService.upsert(role, authority).subscribe();
        userRolesService.upsert(user.getUsername(), role).subscribe();

        return userRepository.save(user);
    }


    private Mono<HttpResponse> createHttpResponse(HttpStatus httpStatus, HttpResponse.ResponseType type, String message){
        return Mono.just(new HttpResponse(httpStatus, type, message));
    }
}
