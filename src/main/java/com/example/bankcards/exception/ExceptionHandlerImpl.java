package com.example.bankcards.exception;

import com.example.bankcards.exception.entity.EntityAlreadyExistException;
import com.example.bankcards.exception.entity.EntityNotFoundException;
import com.example.bankcards.exception.entity.InsufficientFundsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class ExceptionHandlerImpl {

    @ExceptionHandler({
            NoResourceFoundException.class, HttpRequestMethodNotSupportedException.class
    })
    public /* 405 */ ResponseEntity<?> handleHttpRequestMethodNotSupported() {
        return ApiError.METHOD_NOT_ALLOWED.build();
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public /* 400 */ ResponseEntity<?> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex
    ) {
        return ApiError.MISSING_PARAMETER.build(ex.getParameterName());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public /* 400 */ ResponseEntity<?> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex
    ) {
        return ApiError.WRONG_PARAMETER.build(ex.getName());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public /* 400 */ ResponseEntity<?> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex
    ) {
        FieldError fieldError = ex.getBindingResult().getFieldErrors().get(0);
        return ApiError.PARAMETER_NOT_VALID.build(fieldError.getField(), fieldError.getDefaultMessage());
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class, HttpMessageNotWritableException.class
    })
    public /* 400 */ ResponseEntity<?> handleHttpMessageNotReadable(
            HttpMessageConversionException ex
    ) {
        return ApiError.EXCEPTION.build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(EntityException.class)
    public /* 400 */ ResponseEntity<?> handleEntityException(
            EntityException ex
    ) {
        if (ex instanceof EntityAlreadyExistException existException) {
            return ApiError.ENTITY_ALREADY_EXISTS.build(existException.getEntity());
        } else if (ex instanceof EntityNotFoundException notFoundException) {
            return ApiError.ENTITY_NOT_FOUND.build(notFoundException.getEntity());
        } else {
            return ApiError.EXCEPTION.build(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public /* 400 */ ResponseEntity<?> handleInsufficientFunds(
            InsufficientFundsException ex
    ) {
        return ApiError.EXCEPTION.build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public /* 500 */ ResponseEntity<?> handleException(Exception ex) {
        return ApiError.EXCEPTION.build(HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }
}
