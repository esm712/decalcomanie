package com.eightlow.decalcomanie.auth.jwt;

import com.eightlow.decalcomanie.auth.service.JwtService;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(authorization == null || !authorization.startsWith("Bearer ")) {
            logger.error("Authorization을 잘못 보냈습니다");
            filterChain.doFilter(request, response);
            return;
        }

        // Token 꺼내기
        String token = authorization.split(" ")[1];

        try {
            Jws<Claims> claims = JwtUtils.parseToken(token, secretKey);
            String nickname = claims.getPayload().get("nickname", String.class);
            String userId = claims.getPayload().get("userId", String.class);

            // 권한 부여
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(nickname, null, List.of(new SimpleGrantedAuthority("USER")));

            // 상세정보 추가
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            request.setAttribute("userId", userId);

            filterChain.doFilter(request, response);
        } catch (SecurityException e) {
            logger.info("Invalid JWT signature.");
            throw new JwtException("잘못된 JWT 시그니처");
        } catch (MalformedJwtException e) {
            logger.info("Invalid JWT token.");
            throw new JwtException("유효하지 않은 JWT 토큰");
        } catch (ExpiredJwtException e) {
            logger.info("Expired JWT token.");
            throw new JwtException("토큰 기한 만료");
        } catch (UnsupportedJwtException e) {
            logger.info("Unsupported JWT token.");
        } catch (IllegalArgumentException e) {
            logger.info("JWT token compact of handler are invalid.");
            throw new JwtException("JWT token compact of handler are invalid.");
        }
    }
}
