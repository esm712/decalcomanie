package com.eightlow.decalcomanie.auth.service;

import com.eightlow.decalcomanie.auth.jwt.JwtUtils;
import com.eightlow.decalcomanie.auth.respository.OAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-expire}")
    private int accessTokenExpire;

    @Value("${jwt.refresh-token-expire}")
    private int refreshTokenExpire;

    private final OAuthRepository oAuthRepository;

    public String generateAccessToken(String nickname, String userId) {
        return JwtUtils.createJwt(nickname, userId, secretKey, accessTokenExpire);
    }

    public String generateRefreshToken(String nickname, String userId) {
        return JwtUtils.createJwt(nickname, userId, secretKey, refreshTokenExpire);
    }

    public boolean isValidToken(String refreshToken) {
        try {
            String userId = JwtUtils.parseToken(refreshToken, secretKey).getPayload().get("userId", String.class);

            String checkToken = oAuthRepository.findByUserId(userId).getRefreshToken();
            return refreshToken.equals(checkToken);
        } catch (Exception e) {
            return false;
        }
    }
}
