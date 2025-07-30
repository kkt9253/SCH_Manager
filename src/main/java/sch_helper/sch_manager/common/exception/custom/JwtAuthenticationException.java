package sch_helper.sch_manager.common.exception.custom;

import lombok.Getter;
import sch_helper.sch_manager.common.exception.error.ErrorCode;

@Getter
public class JwtAuthenticationException extends RuntimeException {

    private final ErrorCode errorCode;

    public JwtAuthenticationException(ErrorCode errorCode) {

        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}