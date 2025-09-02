package com.eightlow.decalcomanie.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CustomErrorCode {
    TOKEN_NOT_FOUND(HttpStatus.FORBIDDEN, "토큰 정보가 존재하지 않습니다"),
    INVALID_AUTH_TOKEN(HttpStatus.FORBIDDEN, "잘못된 토큰입니다"),
    EXPIRED_AUTH_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다"),
    PERFUME_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 향수입니다"),
    ARTICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 게시물입니다"),
    SCENT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 향입니다"),
    ALREADY_FOLLOWING(HttpStatus.BAD_REQUEST, "이미 팔로우하고 있습니다"),
    ALREADY_UNFOLLOWING(HttpStatus.BAD_REQUEST, "이미 언팔로우하고 있습니다");

    private final HttpStatus httpStatus;
    private final String message;
}

