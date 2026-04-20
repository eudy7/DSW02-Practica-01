package com.prcatica01.empleado.config;

import com.prcatica01.empleado.auth.domain.AuthUser;
import com.prcatica01.empleado.auth.infrastructure.AuthUserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class AuthUserDetailsService implements UserDetailsService {

    private final AuthUserRepository authUserRepository;

    public AuthUserDetailsService(AuthUserRepository authUserRepository) {
        this.authUserRepository = authUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AuthUser user = authUserRepository.findByUsernameIgnoreCaseAndActiveTrue(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return new User(
            user.getUsername(),
            user.getPasswordHash(),
            List.of(
                new SimpleGrantedAuthority("ROLE_API_USER"),
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name().toUpperCase(Locale.ROOT))
            )
        );
    }
}
