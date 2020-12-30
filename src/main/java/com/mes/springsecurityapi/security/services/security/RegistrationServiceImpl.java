package com.mes.springsecurityapi.security.services.security;

import com.mes.springsecurityapi.domain.security.Authority;
import com.mes.springsecurityapi.domain.security.DTO.UserDTO;
import com.mes.springsecurityapi.domain.security.Role;
import com.mes.springsecurityapi.domain.security.User;
import com.mes.springsecurityapi.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.Instant;

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
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public Mono<ResponseEntity<?>> registerClient(@NotNull UserDTO userDTO) {

        return userRepository.findByUsername(userDTO.getEmail())
                .map(userInDb -> {
                    log.debug("User: {} already exists.", userInDb);
                    return ResponseEntity.status(HttpStatus.IM_USED).build();
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.debug("Creating a new User: {}", userDTO);
                    return this.createRegistration(userDTO, "CLIENT", "client.rights")
                            .thenReturn(ResponseEntity.status(HttpStatus.OK).build());
                })).map(objectResponseEntity -> objectResponseEntity);
    }

    private Mono<User> createRegistration(UserDTO userDTO, String role, String authority) {

        Authority clientAuthority = Authority.builder().permission(authority).build();
        Role clientRole = Role.builder().name(role).build();

        String address = userDTO.getStreetName().strip() + ", "
                + userDTO.getHouseNumber().strip() + ", "
                + userDTO.getCity().strip() + ", "
                + userDTO.getCountry().strip() + ", "
                + userDTO.getZipcode().strip();

        User user = User.builder()
                .username(userDTO.getEmail())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .firstName(userDTO.getFirstName())
                .middleName(userDTO.getMiddleName())
                .lastName(userDTO.getLastName())
                .createdDate(Timestamp.from(Instant.now()))
                .lastModifiedDate(Timestamp.from(Instant.now()))
                .address(address)
                .build();

        roleService.saveOrUpdate(clientRole).subscribe();
        authorityService.saveOrUpdate(clientAuthority).subscribe();
        roleAuthoritiesService.upsert(role, authority).subscribe();
        userRolesService.upsert(user.getUsername(), role).subscribe();

        return userRepository.save(user);

                /*.thenMany(subscriber -> {
            log.debug("User {} is successfully saved.", user.getUsername());
            roleAuthoritiesService.upsert(role, authority).subscribe();
            userRolesService.insert(userDTO.getEmail(), role).subscribe();
            log.debug("Saving junction tables.");
        });*/
    }
}
