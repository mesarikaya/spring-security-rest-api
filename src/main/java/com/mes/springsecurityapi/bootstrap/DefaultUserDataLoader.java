package com.mes.springsecurityapi.bootstrap;

import com.mes.springsecurityapi.domain.security.*;
import com.mes.springsecurityapi.repositories.security.*;
import com.mes.springsecurityapi.security.services.security.RoleAuthoritiesService;
import com.mes.springsecurityapi.security.services.security.UserRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

/**
 * Created by mesar on 12/23/2020
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class DefaultUserDataLoader implements CommandLineRunner {

    public static final String ADMIN_USER = "admin@admin.com";
    public static final String CLIENT_USER = "client@client.com";
    public static final String GUEST_USER = "guest@guest.com";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRolesRepository userRolesRepository;
    private final RoleAuthoritiesRepository roleAuthoritiesRepository;
    private final RoleAuthoritiesService roleAuthoritiesService;
    private final UserRoleService userRoleService;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        loadSecurityData();
    }

    private void loadSecurityData() {
        // Create Authorities
        Authority adminAuthority = Authority.builder().permission("admin.rights").build();
        Authority clientAuthority = Authority.builder().permission("client.rights").build();
        Authority guestAuthority = Authority.builder().permission("guest.rights").build();

        authorityRepository.saveAll(List.of(adminAuthority, clientAuthority, guestAuthority)).blockLast(Duration.ofSeconds(10));
        // Create roles
        Role adminRole = Role.builder().name("ADMIN").build();
        Role clientRole = Role.builder().name("CLIENT").build();
        Role guestRole = Role.builder().name("GUEST").build();

        roleRepository.saveAll(List.of(adminRole, clientRole, guestRole)).blockLast(Duration.ofSeconds(10));

        // Create Users
        userRepository.save(User.builder()
                .username(ADMIN_USER)
                .password(passwordEncoder.encode("admin"))
                .build()).block(Duration.ofSeconds(10));

        userRepository.save(User.builder()
                .username(CLIENT_USER)
                .password(passwordEncoder.encode("client"))
                .build()).block(Duration.ofSeconds(10));

        userRepository.save(User.builder()
                .username(GUEST_USER)
                .password(passwordEncoder.encode("guest"))
                .build()).block(Duration.ofSeconds(10));

        //Mono<User> adminUserMono = userRepository.findByUsername(ADMIN_USER);
        //Flux<Role> adminRoleFlux = roleRepository.findByName("ADMIN");
        //createUserRoleData(adminUserMono, adminRoleFlux);
        userRoleService.upsert(ADMIN_USER, "ADMIN").block(Duration.ofSeconds(10));

        //Mono<User> clientUserMono  = userRepository.findByUsername(CLIENT_USER);
        //Flux<Role> clientRoleFlux = roleRepository.findByName("CLIENT");
        //createUserRoleData(clientUserMono, clientRoleFlux);
        userRoleService.upsert(CLIENT_USER, "CLIENT").block(Duration.ofSeconds(10));

        //Mono<User> guestUserMono  = userRepository.findByUsername(GUEST_USER);
        //Flux<Role> guestRoleFlux = roleRepository.findByName("GUEST");
        //createUserRoleData(guestUserMono, guestRoleFlux);
        userRoleService.upsert(GUEST_USER, "GUEST").block(Duration.ofSeconds(10));



        log.debug("Users Loaded: " + userRepository.count());
        //Flux<Authority> adminAuthorityFlux = authorityRepository.findByPermission("admin.rights");
        //createRoleAuthoritiesData(adminRoleFlux, adminAuthorityFlux);
        roleAuthoritiesService.upsert("ADMIN", "admin.rights").block(Duration.ofSeconds(10));

        //Flux<Authority> clientAuthorityFlux = authorityRepository.findByPermission("client.rights");
        //createRoleAuthoritiesData(clientRoleFlux, clientAuthorityFlux);
        roleAuthoritiesService.upsert("CLIENT", "client.rights").block(Duration.ofSeconds(10));

        //Flux<Authority> guestAuthorityFlux = authorityRepository.findByPermission("guest.rights");
        //createRoleAuthoritiesData(guestRoleFlux, guestAuthorityFlux);
        roleAuthoritiesService.upsert("GUEST", "guest.rights").block(Duration.ofSeconds(10));

        log.debug("Users Loaded: " + userRepository.count().block(Duration.ofSeconds(10)));
        log.debug("User roles are Loaded: " + userRolesRepository.count().block(Duration.ofSeconds(10)));
        log.debug("User authorities Loaded: " + roleAuthoritiesRepository.count().block(Duration.ofSeconds(10)));
    }

    private void createUserRoleData(Mono<User> userMono, Flux<Role> roleFlux) {
        roleFlux.flatMap(role -> userMono
                        .map(user -> {
                            UserRoles userRole = UserRoles.of(user, role);
                            return userRolesRepository.save(userRole).subscribe();})
        ).blockFirst(Duration.ofSeconds(10));
    }

    private void createRoleAuthoritiesData(Flux<Role> roleFlux, Flux<Authority> authorityFlux) {
        roleFlux.flatMap(role -> authorityFlux
                .map(authority -> {
                    RoleAuthorities roleAuthorities = RoleAuthorities.of(role, authority);
                    return roleAuthoritiesRepository.save(roleAuthorities).subscribe();})
        ).blockFirst(Duration.ofSeconds(10));
    }
}
