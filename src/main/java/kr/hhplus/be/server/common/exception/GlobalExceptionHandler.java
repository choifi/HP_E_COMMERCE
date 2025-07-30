package kr.hhplus.be.server.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외 처리기
 * 비즈니스 예외를 적절한 HTTP 응답으로 변환
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * IllegalArgumentException 처리
     * 잘못된 요청이나 비즈니스 규칙 위반
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "잘못된 요청",
            e.getMessage()
        );
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * 기타 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "서버 오류",
            "요청을 처리하는 중 오류가 발생했습니다."
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    /**
     * 에러 응답 DTO
     */
    public static class ErrorResponse {
        private final int status;
        private final String error;
        private final String message;
        
        public ErrorResponse(int status, String error, String message) {
            this.status = status;
            this.error = error;
            this.message = message;
        }
        
        public int getStatus() { return status; }
        public String getError() { return error; }
        public String getMessage() { return message; }
    }
} 