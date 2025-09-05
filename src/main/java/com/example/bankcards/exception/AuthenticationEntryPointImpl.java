package com.example.bankcards.exception;

import com.example.bankcards.entity.User;
import com.example.bankcards.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

@RestControllerAdvice
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException authException
    ) throws IOException {
        ResponseUtil.sendResponse(ApiError.AUTH_BAD_LOGIN.build(), response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public void handleAccessDenied(
            HttpServletRequest request, HttpServletResponse response
    ) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            String format = "%s was trying to access %s%n";
            if (auth instanceof AnonymousAuthenticationToken) {
                System.out.printf(format, auth.getName(), request.getRequestURI());
            } else if (auth instanceof UsernamePasswordAuthenticationToken) {
                System.out.printf(format, ((User) auth.getPrincipal()).getUsername(), request.getRequestURI());
            }
        }
        ResponseUtil.sendResponse(ApiError.AUTH_ACCESS_DENIED.build(), response);
    }
}
