package sch_helper.sch_manager.common.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sch_helper.sch_manager.common.exception.custom.JwtAuthenticationException;
import sch_helper.sch_manager.common.exception.error.ErrorCode;
import sch_helper.sch_manager.common.response.ErrorResponse;

import java.io.IOException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(JwtAuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleJwtAuthenticationException(JwtAuthenticationException ex) {

        log.error("JwtAuthentication Exception: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.of(ex.getErrorCode(), ex.getErrorCode().getMessage());
        return ResponseEntity.status(ex.getErrorCode().getStatus()).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(HttpServletRequest request, Exception ex) {
        log.error("Unhandled exception: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR, request.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

}
