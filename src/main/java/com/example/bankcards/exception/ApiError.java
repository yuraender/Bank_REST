package com.example.bankcards.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum ApiError {

    AUTH_BAD_LOGIN("Token is not present", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID("Token is invalid", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED("Token is expired", HttpStatus.UNAUTHORIZED),

    AUTH_ACCESS_DENIED("Insufficient permissions for this method", HttpStatus.FORBIDDEN),
    METHOD_NOT_ALLOWED("Method not allowed", HttpStatus.METHOD_NOT_ALLOWED),
    MISSING_PARAMETER("Required request parameter '%s' is missing", HttpStatus.BAD_REQUEST),
    WRONG_PARAMETER("Request parameter '%s' has wrong type", HttpStatus.BAD_REQUEST),
    PARAMETER_NOT_VALID("Request parameter '%s' is invalid: %s", HttpStatus.BAD_REQUEST),

    ENTITY_NOT_FOUND("%s not found", HttpStatus.BAD_REQUEST),
    ENTITY_ALREADY_EXISTS("%s already exists", HttpStatus.BAD_REQUEST),

    EXCEPTION("%s", HttpStatus.INTERNAL_SERVER_ERROR) {
        @Override
        public ResponseEntity<?> build(HttpStatus httpStatus, Object... params) {
            ResponseEntity<Map<String, Object>> response
                    = (ResponseEntity<Map<String, Object>>) super.build(httpStatus, params);
            if (params.length == 0) {
                return response;
            }
            if (params[0] instanceof String message) {
                response.getBody().put("message", message);
            } else if (params[0] instanceof Exception ex) {
                response.getBody().put("message", ex.getMessage());
                StringWriter sw = new StringWriter();
                try (PrintWriter writer = new PrintWriter(sw)) {
                    ex.printStackTrace(writer);
                }
                response.getBody().put("trace", sw.toString().replace("\t", "  ").split(System.lineSeparator()));
            }
            return response;
        }
    };

    private final String message;
    private final HttpStatus httpStatus;

    public ResponseEntity<?> build(Object... params) {
        return build(httpStatus, params);
    }

    public ResponseEntity<?> build(HttpStatus httpStatus, Object... params) {
        return ResponseEntity.status(httpStatus).body(buildBody(httpStatus, params));
    }

    private Map<String, Object> buildBody(HttpStatus httpStatus, Object... params) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", httpStatus.value());
        body.put("message", String.format(message, params));
        return body;
    }
}
