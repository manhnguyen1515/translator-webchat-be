package com.translator.webchat.service.impl;

import com.translator.webchat.config.UserAuthProvider;
import com.translator.webchat.entities.User;
import com.translator.webchat.exception.TokenRefreshException;
import com.translator.webchat.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

   private final UserAuthProvider userAuthProvider;

    /**
     * ${inheritDoc}
     */
    @Override
    public String generateAccessTokenFromRefreshToken(String refreshToken) {
        Optional<User> userOpt = userAuthProvider.getUserFromRefreshToken(refreshToken);
        if (userOpt.isEmpty()) {
            throw new TokenRefreshException(HttpStatus.UNAUTHORIZED.value(), "Invalid refresh token", "1");
        }

        User user = userOpt.get();
        return userAuthProvider.createAccessToken(user.getUsername());
    }
}
