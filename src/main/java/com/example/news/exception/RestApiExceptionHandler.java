package com.example.news.exception;

import com.example.news.dto.res.ErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestControllerAdvice
@Slf4j
public class RestApiExceptionHandler {

    @ExceptionHandler(value = {BadRequestException.class})
    public ResponseEntity<Object> handleBadRequestException(BadRequestException ex) {
        log.info("!!!Bad Request Error, [{}], message={}", MDC.get("UUID"), ex.getMessage());
        ErrorDto errorDto = new ErrorDto(Stream.of(ex.getMessage()).collect(Collectors.toSet()));
        return new ResponseEntity<>(
                errorDto,
                HttpStatus.BAD_REQUEST
        );
    }

    //권한이 없는 경우에 call 되는 에러 핸들러
    @ExceptionHandler(value = {NoAuthException.class})
    public ResponseEntity<Object> handleNoAuthException(NoAuthException ex) {
        log.info("!!!UnAuth Error, [{}], message={}", MDC.get("UUID"), ex.getMessage());

        ErrorDto errorDto = new ErrorDto(Stream.of(ex.getMessage()).collect(Collectors.toSet()));
        return new ResponseEntity<>(
                errorDto,
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(value = {ForbiddenException.class})
    public ResponseEntity<Object> handleForbiddenException(ForbiddenException ex) {
        log.info("!!!Forbidden Error, [{}], message={}", MDC.get("UUID"), ex.getMessage());
        ErrorDto errorDto = new ErrorDto(Stream.of(ex.getMessage()).collect(Collectors.toSet()));
        return new ResponseEntity<>(
                errorDto,
                HttpStatus.BAD_REQUEST
        );
    }

    //Request Body의 Valid 조건을 통과하지 못하면 call 됨
    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<Object> handleApiRequestErrorException(MethodArgumentNotValidException ex) {

        System.out.println("not valid");

        String msg = Objects.requireNonNull(ex.getMessage());
        log.info("!!!Parameter Error, [{}], message={}", MDC.get("UUID"), msg);

        ErrorDto errorDto = new ErrorDto();
        Set<String> messages = new HashSet<>();

        //valid 에러난 메시지들을 순회하며 에러 리스트에 담는다.
        BindingResult bindingResult = ex.getBindingResult();
        bindingResult.getAllErrors().forEach(error -> {
            FieldError fieldError = (FieldError) error;

            String fieldName = fieldError.getField();
            String message = fieldError.getDefaultMessage();
            String value = fieldError.getRejectedValue().toString();

            messages.add(message);
        });

        errorDto.setMessages(messages);
        return new ResponseEntity<>(
                errorDto,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(value = {RuntimeException.class})
    public ResponseEntity<Object> handleBadRequestErrorException(RuntimeException ex) {
        String msg = ErrorCode.INVALID_INPUT_ERROR.getMessage();
        log.info("!!!Runtime Error, [{}], message={}", MDC.get("UUID"), ex.getMessage());
        ErrorDto errorDto = new ErrorDto(Stream.of(ex.getMessage()).collect(Collectors.toSet()));
        return new ResponseEntity<>(
                errorDto,
                HttpStatus.BAD_REQUEST
        );
    }
}
