package com.authorization.controller;

import com.authorization.error.ErrorDetails;
import com.authorization.exception.UnauthorizedException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
@Slf4j
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

    ObjectMapper mapper = new ObjectMapper();

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoDataFound(NoSuchElementException e)
            throws JsonProcessingException {
        log.error("no data found", e);
        ErrorDetails errorDetails =
                new ErrorDetails(HttpStatus.NOT_FOUND.toString(), e.getMessage(), "No data found");
        String body = mapper.writeValueAsString(errorDetails);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<String> handleDatabaseError(DataAccessException e) throws JsonProcessingException {
        log.error("an internal server error occurred", e);
        ErrorDetails errorDetails = new ErrorDetails(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                e.getMessage(), "There was an error processing your request");
        String body = mapper.writeValueAsString(errorDetails);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<String> handleDuplicates(DuplicateKeyException e) throws JsonProcessingException {
        log.error("an internal server error occurred", e);
        ErrorDetails errorDetails = new ErrorDetails(HttpStatus.CONFLICT.toString(),
                e.getMessage(), "An instance with the specified fields already exists. No duplicates allowed");
        String body = mapper.writeValueAsString(errorDetails);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> handleUnauthorizedException(UnauthorizedException e) throws JsonProcessingException {
        log.error("an internal server error occurred", e);
        ErrorDetails errorDetails = new ErrorDetails(HttpStatus.UNAUTHORIZED.toString(),
                e.getMessage(), "User doesn't have required rights");
        String body = mapper.writeValueAsString(errorDetails);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpHeaders headers,
                                                                  HttpStatus status, WebRequest request) {
        log.error("invalid arguments", e);
        ErrorDetails errorDetails =
                new ErrorDetails(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), "Invalid request body");
        return handleExceptionInternal(e, errorDetails, headers, HttpStatus.BAD_REQUEST, request);
    }
}
