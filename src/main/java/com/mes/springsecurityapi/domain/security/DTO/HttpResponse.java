package com.mes.springsecurityapi.domain.security.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

/**
 * Created by mesar on 1/4/2021
 */
@Data
@AllArgsConstructor
@Getter
@ToString
public class HttpResponse implements Serializable {

    private static final long serialVersionUID = 6134431243853127181L;

    private final HttpStatus httpStatus;
    private final ResponseType type;
    private final String message;

    public enum ResponseType {
        SUCCESS, FAILURE;
    }
}
