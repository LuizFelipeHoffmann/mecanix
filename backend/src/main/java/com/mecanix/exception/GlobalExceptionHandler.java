package com.mecanix.exception;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String,String>> handleNotFound(ResourceNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro",ex.getMessage()));
    }
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String,String>> handleBusiness(BusinessException ex){
        return ResponseEntity.badRequest().body(Map.of("erro",ex.getMessage()));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,String>> handleGeneral(Exception ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("erro","Erro interno: "+ex.getMessage()));
    }
}
