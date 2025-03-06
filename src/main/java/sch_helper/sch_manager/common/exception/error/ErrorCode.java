package sch_helper.sch_manager.common.exception.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    /** ========== 400 BAD_REQUEST (잘못된 요청) ========== **/
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "B001", "잘못된 요청입니다."),
    INVALID_REQUEST_DATA(HttpStatus.BAD_REQUEST, "B002", "요청 데이터가 올바르지 않습니다."),
    DATE_DAY_MISMATCH(HttpStatus.BAD_REQUEST, "B003", "요청한 날짜와 요일이 일치하지 않습니다."),
    MISSING_REQUIRED_PARAMETER(HttpStatus.BAD_REQUEST, "B004", "필수 요청 파라미터가 누락되었습니다."),
    INVALID_JSON_FORMAT(HttpStatus.BAD_REQUEST, "B005", "잘못된 JSON 형식입니다."),

    /** ========== 401 UNAUTHORIZED (인증 오류) ========== **/
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "A001", "인증이 필요합니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "A002", "아이디 또는 비밀번호가 일치하지 않습니다."),
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "A003", "Access Token이 만료되었습니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "A004", "유효하지 않은 Access Token입니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "A005", "Refresh Token이 만료되었습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "A006", "유효하지 않은 Refresh Token입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "A007", "Refresh Token이 존재하지 않습니다."),
    JWT_SIGNATURE_INVALID(HttpStatus.UNAUTHORIZED, "A008", "JWT 서명이 유효하지 않습니다."),
    JWT_MALFORMED(HttpStatus.UNAUTHORIZED, "A009", "JWT 형식이 올바르지 않습니다."),
    INVALID_TOKEN_CATEGORY(HttpStatus.UNAUTHORIZED, "A010", "토큰 카테고리가 유효하지 않습니다."),
    TOKEN_NOT_PROVIDED(HttpStatus.UNAUTHORIZED, "A011", "토큰이 제공되지 않았습니다."),

    /** ========== 403 FORBIDDEN (권한 부족) ========== **/
    FORBIDDEN(HttpStatus.FORBIDDEN, "F001", "접근이 거부되었습니다."),
    NO_PERMISSION(HttpStatus.FORBIDDEN, "F002", "이 작업을 수행할 권한이 없습니다."),
    ADMIN_ONLY(HttpStatus.FORBIDDEN, "F003", "관리자만 접근할 수 있습니다."),

    /** ========== 404 NOT_FOUND (리소스 없음) ========== **/
    NOT_FOUND(HttpStatus.NOT_FOUND, "N001", "해당 리소스를 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "N002", "해당 사용자를 찾을 수 없습니다."),
    RESTAURANT_NOT_FOUND(HttpStatus.NOT_FOUND, "N003", "해당 식당을 찾을 수 없습니다."),
    MENU_NOT_FOUND(HttpStatus.NOT_FOUND, "N004", "해당 메뉴를 찾을 수 없습니다."),
    WEEKLY_MENU_NOT_FOUND(HttpStatus.NOT_FOUND, "N005", "해당 주간 식단을 찾을 수 없습니다."),
    DAILY_MENU_NOT_FOUND(HttpStatus.NOT_FOUND, "N006", "해당 일간 식단을 찾을 수 없습니다."),

    /** ========== 405 METHOD_NOT_ALLOWED (잘못된 HTTP 메서드) ========== **/
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "M001", "해당 HTTP 메서드는 지원되지 않습니다."),

    /** ========== 409 CONFLICT (비즈니스 로직 충돌) ========== **/
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "C001", "이미 존재하는 데이터입니다."),
    MENU_ALREADY_EXISTS(HttpStatus.CONFLICT, "C002", "해당 메뉴는 이미 존재합니다."),
    INVALID_MEAL_TYPE(HttpStatus.CONFLICT, "C003", "잘못된 식단 유형입니다."),
    INVALID_OPERATION(HttpStatus.CONFLICT, "C004", "이 작업을 수행할 수 없습니다."),
    TRANSFORMATION_ERROR(HttpStatus.CONFLICT, "C005", "데이터 변환 과정에서 오류가 발생했습니다."),

    /** ========== 415 UNSUPPORTED_MEDIA_TYPE (잘못된 Content-Type) ========== **/
    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "U001", "지원되지 않는 Content-Type입니다."),

    /** ========== 500 INTERNAL_SERVER_ERROR (서버 오류) ========== **/
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S001", "서버 내부 오류가 발생했습니다."),
    UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S002", "업로드에 실패했습니다."),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S003", "데이터베이스 오류가 발생했습니다."),
    UNEXPECTED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S004", "예상치 못한 오류가 발생했습니다.");


    private final HttpStatus status;
    private final String code;
    private final String message;
}