package com.eightlow.decalcomanie.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtUtils {

    public static Jws<Claims> parseToken(String token, String secretKey) {
        SecretKey key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
    }

    public static String createJwt(String nickname, String userId, String secretKey, int expiredMs) {
        SecretKey key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        return Jwts.builder()
                .claim("nickname", nickname)
                .claim("userId", userId)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs*1000L))
                .signWith(key)
                .compact();
    }


}
