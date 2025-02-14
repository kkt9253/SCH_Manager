package sch_helper.sch_manager.common.exception.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 인증 및 권한 관련 에러
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "A001", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "A002", "접근이 거부되었습니다."),

    // 사용자 관련 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "해당 사용자를 찾을 수 없습니다."),
    INVALID_USER_INPUT(HttpStatus.BAD_REQUEST, "U002", "잘못된 사용자 입력입니다."),

    // 토큰 관련 에러 추가
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "T001", "Access Token이 만료되었습니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "T002", "유효하지 않은 Access Token입니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "T003", "Refresh Token이 만료되었습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "T004", "유효하지 않은 Refresh Token입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "T005", "Refresh Token이 존재하지 않습니다."),
    JWT_SIGNATURE_INVALID(HttpStatus.UNAUTHORIZED, "T006", "JWT 서명이 유효하지 않습니다."),
    JWT_MALFORMED(HttpStatus.UNAUTHORIZED, "T007", "JWT 형식이 올바르지 않습니다."),
    INVALID_TOKEN_CATEGORY(HttpStatus.UNAUTHORIZED, "T008", "토큰 카테고리가 유효하지 않습니다."),
    FORBIDDEN_ERROR(HttpStatus.FORBIDDEN, "T009", "권한 없음"),
    UNAUTHORIZED_ERROR(HttpStatus.UNAUTHORIZED, "T010", "인증 안 됨"),
    TOKEN_NOT_PROVIDED(HttpStatus.UNAUTHORIZED, "T011", "토큰이 제공되지 않았습니다."),

    // 일반 서버 오류
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S001", "서버 내부 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}