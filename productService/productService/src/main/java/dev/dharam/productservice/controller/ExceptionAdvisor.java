package dev.dharam.productservice.controller;

import dev.dharam.productservice.exceptions.ExceptionResponseDto;
import dev.dharam.productservice.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionAdvisor {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExceptionResponseDto> handleResourceNotFoundException(ResourceNotFoundException exception){
        ExceptionResponseDto ex = new ExceptionResponseDto();
        ex.setMessage(exception.getMessage());
        return new ResponseEntity<>(ex, HttpStatus.NOT_FOUND);
    }
}
