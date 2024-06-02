package com.fptacademy.training.security;

import com.fptacademy.training.domain.User;
import com.fptacademy.training.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;

@RequiredArgsConstructor
@Component
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .map(this::createSecurityUser)
                .orElseThrow(() -> new UsernameNotFoundException("Email not found"));
    }

    private org.springframework.security.core.userdetails.User createSecurityUser(User user) {
        Collection<? extends GrantedAuthority> authorities = user.getRole().getPermissions()
                .stream().map(SimpleGrantedAuthority::new).toList();
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getActivated(),
                true,
                true,
                true,
                authorities
                );
    }
}
