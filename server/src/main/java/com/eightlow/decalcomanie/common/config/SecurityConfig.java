package com.eightlow.decalcomanie.common.config;

import com.eightlow.decalcomanie.auth.security.JwtExceptionFilter;
import com.eightlow.decalcomanie.auth.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.filter.CorsFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    private final JwtExceptionFilter jwtExceptionFilter;
    private final CorsFilter corsFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        // Basic 인증 비활성화
        httpSecurity
                .httpBasic().disable();

        // REST API 서버이므로 csrf 비활성화
        httpSecurity
                .csrf().disable();

        // CORS 정책 추가
        httpSecurity
                .addFilterBefore(corsFilter, ChannelProcessingFilter.class);

        // HTTP 요청 권한 추가
        httpSecurity
                .authorizeHttpRequests()
                .requestMatchers("/oauth/**").permitAll()
                .anyRequest().authenticated();

        // HTTP Session 정책
        httpSecurity
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // JWT 필터 추가
        httpSecurity
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtExceptionFilter, JwtFilter.class);

        return httpSecurity.build();
    }

    @Bean
    public HttpFirewall getHttpFirewall() {
        StrictHttpFirewall strictHttpFirewall = new StrictHttpFirewall();
        strictHttpFirewall.setAllowUrlEncodedSlash(true);
        strictHttpFirewall.setAllowBackSlash(true);
        strictHttpFirewall.setAllowUrlEncodedDoubleSlash(true);

        return strictHttpFirewall;
    }
}
