package com.mes.springsecurityapi.controllers;

import com.mes.springsecurityapi.domain.security.User;
import com.mes.springsecurityapi.security.permissions.AdminPermission;
import com.mes.springsecurityapi.security.services.security.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Created by mesar on 12/30/2020
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@PreAuthorize("hasAnyRole('ADMIN', 'CLIENT', 'GUEST')")
public class UserController {

    private final UserService userService;

    @GetMapping("/allUsers")
    @PreAuthorize("hasAnyRole('CLIENT')")
    @ResponseStatus(HttpStatus.OK)
    public Flux<User> getAllUsers(){
        return userService.findAll();
    }

    @AdminPermission
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/user")
    public Mono<User> getUser(@RequestParam("username") String username){
        return userService.findByUserName(username);
    }
}