package com.mes.springsecurityapi.security.services.SignupProcessService;

import com.mes.springsecurityapi.domain.security.DTO.HttpResponse;
import com.mes.springsecurityapi.domain.security.DTO.LogoutForm;
import com.mes.springsecurityapi.security.jwt.JWTUtil;
import com.mes.springsecurityapi.security.services.security.SecurityUserLibraryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

/**
 * Created by mesar on 1/5/2021
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class LogoutServiceImpl implements LogoutService{

    private final JWTUtil jwtUtil;
    private final SecurityUserLibraryService securityUserLibraryService;

    @Value("${cookie.secure}")
    private boolean isCookieSecure = false;

    @Override
    public Mono<HttpResponse> logout(@NotNull LogoutForm logoutForm, ServerHttpResponse serverHttpResponse) {


        String username = logoutForm.getUsername();
        String token = logoutForm.getToken();
        deleteCookie(token, serverHttpResponse);

        return securityUserLibraryService.findByUsername(username)
                .flatMap( (userDetails) -> {
                    if (userDetails.getUsername().equals(jwtUtil.getUsernameFromToken(token))){
                        log.debug("Logging out user: " + jwtUtil.getUsernameFromToken(token));
                        return createHttpResponse(HttpStatus.OK, HttpResponse.ResponseType.SUCCESS, "Logout is successful!");
                    } else {
                        log.debug("Invalid username is provided during logout process. Returning unauthorized and logged out.");
                        return createHttpResponse(HttpStatus.UNAUTHORIZED, HttpResponse.ResponseType.FAILURE, "Token is invalid!");
                    }
                }).defaultIfEmpty(new HttpResponse(HttpStatus.BAD_REQUEST, HttpResponse.ResponseType.FAILURE, "Logout is not successful!"))
                .onErrorResume(ex -> createHttpResponse(HttpStatus.BAD_REQUEST, HttpResponse.ResponseType.FAILURE, ex.getLocalizedMessage()));
    }

    private void deleteCookie(String token, ServerHttpResponse serverHttpResponse) {
        ResponseCookie cookie = ResponseCookie.from("System", token)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .secure(isCookieSecure)
                .httpOnly(true)
                .build();
        serverHttpResponse.addCookie(cookie);
        log.debug("Cookie that was created during login is deleted.");
    }

    private Mono<HttpResponse> createHttpResponse(HttpStatus httpStatus, HttpResponse.ResponseType type, String message){
        return Mono.just(new HttpResponse(httpStatus, type, message));
    }
}
