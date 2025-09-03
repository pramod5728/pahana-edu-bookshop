// exception/GlobalExceptionHandler.java
package com.pahana.edu.billing.exception;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<?> notFound(NotFoundException ex){
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
  }
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> validation(MethodArgumentNotValidException ex){
    var errors = ex.getBindingResult().getFieldErrors()
      .stream().collect(Collectors.toMap(f->f.getField(), f->f.getDefaultMessage(), (a,b)->a));
    return ResponseEntity.badRequest().body(Map.of("errors", errors));
  }
}
