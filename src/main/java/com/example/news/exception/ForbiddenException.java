package com.example.news.exception;

public class ForbiddenException extends RuntimeException{
    public ForbiddenException(ErrorCode errorCode) {
        super(errorCode.getMessage());
    }
}
