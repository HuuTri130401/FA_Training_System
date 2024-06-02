package com.fptacademy.training.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ApplicationProperties {
    @Value("${application.security.authentication.jwt.access-token.secret-key}")
    private String accessSecretKey;
    @Value("${application.security.authentication.jwt.access-token.expire-time-in-minutes}")
    private Long accessExpireTimeInMinutes;
    @Value("${application.security.authentication.jwt.refresh-token.secret-key}")
    private String refreshSecretKey;
    @Value("${application.security.authentication.jwt.refresh-token.expire-time-in-minutes}")
    private Long refreshExpireTimeInMinutes;
}
