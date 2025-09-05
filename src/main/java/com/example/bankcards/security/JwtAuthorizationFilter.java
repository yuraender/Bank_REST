package com.example.bankcards.security;

import com.example.bankcards.entity.User;
import com.example.bankcards.exception.ApiError;
import com.example.bankcards.util.ResponseUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    public static final String HEADER = "Authorization";
    public static final String PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String token = request.getHeader(HEADER);
            if (token != null && token.startsWith(PREFIX)) {
                Claims claims = jwtService.parseToken(token.replace(PREFIX, ""));

                User user = userDetailsService.loadUserByUsername(claims.getSubject());
                if (user != null
                        && user.getPassword().equals(claims.get("pwd", String.class))
                        && user.isEnabled()) {
                    UsernamePasswordAuthenticationToken authentication
                            = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (JwtException ex) {
            if (ex instanceof MalformedJwtException || ex instanceof SecurityException) {
                ResponseUtil.sendResponse(ApiError.TOKEN_INVALID.build(), response);
            } else if (ex instanceof ExpiredJwtException) {
                ResponseUtil.sendResponse(ApiError.TOKEN_EXPIRED.build(), response);
            }
            return;
        }
        filterChain.doFilter(request, response);
    }
}
