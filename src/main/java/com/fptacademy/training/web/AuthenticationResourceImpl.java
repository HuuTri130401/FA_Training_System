package com.fptacademy.training.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import com.fptacademy.training.domain.User;
import com.fptacademy.training.security.jwt.JwtTokenProvider;
import com.fptacademy.training.service.UserService;
import com.fptacademy.training.web.api.AuthenticationResource;
import com.fptacademy.training.web.vm.AccountVM;
import com.fptacademy.training.web.vm.LoginVM;
import com.fptacademy.training.web.vm.TokenVM;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class AuthenticationResourceImpl implements AuthenticationResource {
    private final UserService userService;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    @Override
    public ResponseEntity<AccountVM> login(LoginVM loginVM) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginVM.email(),
                loginVM.password()
        );
        Authentication authentication = authenticationManagerBuilder.getOrBuild().authenticate(authenticationToken);
        TokenVM tokenVM = new TokenVM(
                tokenProvider.generateAccessToken(authentication),
                tokenProvider.generateRefreshToken(authentication.getName()));
        User user = userService.getUserByEmail(loginVM.email());
        AccountVM accountVM = new AccountVM(user.getFullName(), user.getAvatarUrl(), tokenVM);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(accountVM);
    }

    @Override
    public ResponseEntity<TokenVM> getAccessTokenFromRefreshToken(String refreshToken) {
        if (StringUtils.hasText(refreshToken) && tokenProvider.validateRefreshToken(refreshToken)) {
            return ResponseEntity
                    .ok(new TokenVM(tokenProvider.generateAccessToken(refreshToken), refreshToken));
        } else {
            throw new BadCredentialsException("Invalid or expired refresh token");
        }
    }
}
