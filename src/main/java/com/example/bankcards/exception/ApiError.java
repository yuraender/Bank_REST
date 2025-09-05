package com.example.bankcards.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum ApiError {

    AUTH_BAD_LOGIN("Token is not present", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID("Token is invalid", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED("Token is expired", HttpStatus.UNAUTHORIZED),

    AUTH_ACCESS_DENIED("Insufficient permissions for this method", HttpStatus.FORBIDDEN);

    private final String message;
    private final HttpStatus httpStatus;

    public ResponseEntity<?> build(Object... params) {
        return ResponseEntity.status(httpStatus).body(buildBody(params));
    }

    private Map<String, Object> buildBody(Object... params) {
        Map<String, Object> body = new HashMap<>();
        body.put("code", httpStatus.value());
        body.put("message", String.format(message, params));
        return body;
    }
}
