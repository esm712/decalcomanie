package com.eightlow.decalcomanie.auth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import org.aspectj.bridge.Message;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

    /*
    인증 오류가 아닌, JWT 관련 오류는 이 필터에서 따로 잡아낸다.
    이를 통해 JWT 만료 에러와 인증 에러를 따로 잡아낼 수 있다.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        try {
            chain.doFilter(request, response); // JwtFilter로 이동
        } catch (JwtException ex) {
            // JwtFilter에서 예외 발생하면 바로 setErrorResponse 호출
            setErrorResponse(request, response, ex);
        }
    }

    public void setErrorResponse(HttpServletRequest req, HttpServletResponse res, Throwable ex) throws IOException {
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);

        final Map<String, Object> body = new HashMap<>();

        // 기간 만료는 UNAUTHORIZED, 잘못된 토큰은 FORBIDDEN을 반환하여 로그아웃 처리
        if(ex.getMessage().equals("토큰 기한 만료")) {
            body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
            body.put("error", "Unauthorized");
            res.setStatus(HttpStatus.UNAUTHORIZED.value());
        } else {
            body.put("status", HttpServletResponse.SC_FORBIDDEN);
            body.put("error", "Invalid");
            res.setStatus(HttpStatus.FORBIDDEN.value());
        }

        // ex.getMessage() 에는 jwtException을 발생시키면서 입력한 메세지가 들어있다.
        body.put("message", ex.getMessage());
        body.put("path", req.getServletPath());
        final ObjectMapper mapper = new ObjectMapper();

        mapper.writeValue(res.getOutputStream(), body);
    }
}