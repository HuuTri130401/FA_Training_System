package com.fptacademy.training.security.jwt;

import com.fptacademy.training.config.ApplicationProperties;
import com.fptacademy.training.domain.User;
import com.fptacademy.training.service.UserService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;

@Component
public class JwtTokenProvider {
    private final UserService userService;
    private final long accessExpireTimeInMinutes;
    private final long refreshExpireTimeInMinutes;
    private final JwtBuilder accessJwtBuilder;
    private final JwtParser accessJwtParser;
    private final JwtBuilder refreshJwtBuilder;
    private final JwtParser refreshJwtParser;

    public JwtTokenProvider(ApplicationProperties properties, UserService userService) {
        this.userService = userService;
        accessExpireTimeInMinutes = properties.getAccessExpireTimeInMinutes();
        refreshExpireTimeInMinutes = properties.getRefreshExpireTimeInMinutes();
        Key accessKey = Keys.hmacShaKeyFor(properties.getAccessSecretKey().getBytes(StandardCharsets.UTF_8));
        accessJwtBuilder = Jwts.builder()
                .signWith(accessKey, SignatureAlgorithm.HS256);
        accessJwtParser = Jwts.parserBuilder()
                .setSigningKey(accessKey).build();
        Key refreshKey = Keys.hmacShaKeyFor(properties.getRefreshSecretKey().getBytes(StandardCharsets.UTF_8));
        refreshJwtBuilder = Jwts.builder()
                .signWith(refreshKey, SignatureAlgorithm.HS256);
        refreshJwtParser = Jwts.parserBuilder()
                .setSigningKey(refreshKey).build();
    }

    private String getAccessToken(String email, String role, Collection<? extends GrantedAuthority> authorities) {
        Date expiredTime = new Date((new Date()).getTime() + 1000 * 60 * accessExpireTimeInMinutes);
        Map<String, Object> claims = new HashMap<>();
        Map<String, String> auth = new HashMap<>();
        authorities.stream().map(GrantedAuthority::getAuthority).forEach(permission -> {
            String[] s = permission.split("_");
            auth.put(s[0], s[1]);
        });
        claims.put("role", role);
        claims.put("permissions", auth);
        return accessJwtBuilder
                .setSubject(email)
                .addClaims(claims)
                .setExpiration(expiredTime)
                .compact();
    }

    public String generateAccessToken(Authentication authentication) {
        String email = authentication.getName();
        String role = userService.getUserRoleByEmail(email).getName();
        return getAccessToken(email, role, authentication.getAuthorities());
    }

    public String generateAccessToken(String refreshToken) {
        Claims refreshClaims = refreshJwtParser.parseClaimsJws(refreshToken).getBody();
        String email = refreshClaims.getSubject();
        String role = userService.getUserRoleByEmail(email).getName();
        return getAccessToken(email, role, userService.getUserPermissionsByEmail(email));
    }

    public String generateRefreshToken(String email) {
        Date expiredTime = new Date((new Date()).getTime() + 1000 * 60 * refreshExpireTimeInMinutes);
        return refreshJwtBuilder
                .setSubject(email)
                .setExpiration(expiredTime)
                .compact();
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = accessJwtParser.parseClaimsJws(accessToken).getBody();
        String email = claims.getSubject();
        Map<String, String> auth = (Map<String, String>) claims.get("permissions");
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        auth.forEach((role, permission) -> {
            authorities.add(new SimpleGrantedAuthority(role + "_" + permission));
        });
        User user = userService.getUserByEmail(email);
        return new UsernamePasswordAuthenticationToken(user, accessToken, authorities);
    }

    public boolean validateAccessToken(String accessToken) {
        try {
            accessJwtParser.parseClaimsJws(accessToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean validateRefreshToken(String refreshToken) {
        try {
            refreshJwtParser.parseClaimsJws(refreshToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}