package com.example.loyalty.adapters.in.web;

import com.example.loyalty.domain.service.BusinessRuleViolationException;
import com.example.loyalty.domain.service.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(NotFoundException.class) ProblemDetail notFound(NotFoundException ex) { return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage()); }
    @ExceptionHandler(BusinessRuleViolationException.class) ProblemDetail business(BusinessRuleViolationException ex) { return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage()); }
    @ExceptionHandler(BadCredentialsException.class) ProblemDetail credentials(BadCredentialsException ex) { return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage()); }
    @ExceptionHandler(MethodArgumentNotValidException.class) ProblemDetail validation(MethodArgumentNotValidException ex) { return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed"); }
}
