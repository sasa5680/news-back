package com.example.news.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    NO_AUTHENTICATION_ERROR("로그인 사용자만 사용할 수 있습니다."),
    NO_AUTHORIZATION_ERROR("접근 권한이 없습니다."),
    NO_TOKEN_ERROR("토큰이 존재하지 않습니다."),
    NO_MATCH_ITEM_ERROR("일치하는 정보가 없습니다."),
    WRONG_SIGNUP_CODE("인증 코드가 잘못되었습니다."),
    ALREADY_EMAIL_ERROR("이미 사용중인 이메일입니다."),
    ALREADY_NICKNAME_ERROR("이미 사용중인 닉네임입니다."),
    INVALID_INPUT_ERROR("입력 값이 잘못되었습니다."),
    TOKEN_EXPIRATION_ERROR("로그인 정보가 만료되었습니다."),
    TOKEN_MAL_ERROR("잘못된 토큰입니다."),
    NOT_AVAILABLE_ACCESS("불가능한 접근입니다."),
    NO_IPBANLIST_ERROR("IP 밴 리스트에 없는 아이피입니다."),
    NO_EMAIL_PATTERN("이메일 형식이 맞지 않습니다."),
    EMAIL_NOT_CERTIFIED("이메일 인증이 완료되지 않았습니다."),
    LOGIN_FAIL_ERROR("일치하는 유저 정보가 없습니다!")
    ;
    private String message;
}
