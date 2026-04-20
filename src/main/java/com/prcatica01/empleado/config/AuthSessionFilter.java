package com.prcatica01.empleado.config;

import com.prcatica01.empleado.auth.application.AuthService;
import com.prcatica01.empleado.auth.domain.AuthSession;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

@Component
public class AuthSessionFilter extends OncePerRequestFilter {

    private final AuthService authService;

    public AuthSessionFilter(AuthService authService) {
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization != null && authorization.startsWith("Bearer ")
            && SecurityContextHolder.getContext().getAuthentication() == null) {

            String token = authorization.substring(7).trim();
            if (!token.isEmpty()) {
                try {
                    AuthSession session = authService.validateAndTouchSession(token);
                    String role = authService.resolveRoleForSession(session);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        session.getUsername(),
                        token,
                        List.of(
                            new SimpleGrantedAuthority("ROLE_API_USER"),
                            new SimpleGrantedAuthority("ROLE_" + role.toUpperCase(Locale.ROOT))
                        )
                    );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (RuntimeException exception) {
                    SecurityContextHolder.clearContext();
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
