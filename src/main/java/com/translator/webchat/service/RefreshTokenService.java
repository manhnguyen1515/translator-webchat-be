package com.translator.webchat.service;

public interface RefreshTokenService {

    /**
     * Generate new access token from refresh token
     * @param refreshToken
     * @return
     */
    String generateAccessTokenFromRefreshToken(String refreshToken);

}
