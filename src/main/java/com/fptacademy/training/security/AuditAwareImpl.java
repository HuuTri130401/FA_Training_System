package com.fptacademy.training.security;

import com.fptacademy.training.domain.User;
import com.fptacademy.training.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class AuditAwareImpl implements AuditorAware<User> {
    private final UserService userService;
    @Override
    public Optional<User> getCurrentAuditor() {
        return Optional.ofNullable(userService.getCurrentUserLogin());
    }
}
