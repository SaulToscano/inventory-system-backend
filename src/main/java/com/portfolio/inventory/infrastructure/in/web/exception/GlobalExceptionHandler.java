package com.portfolio.inventory.infrastructure.in.web.exception;

import com.portfolio.inventory.domain.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

  // 1. Manejar recursos no encontrados (Error 404)
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorMessage> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
    ErrorMessage message = new ErrorMessage(
      LocalDateTime.now(),
      HttpStatus.NOT_FOUND.value(),
      HttpStatus.NOT_FOUND.getReasonPhrase(),
      ex.getMessage(),
      request.getDescription(false).replace("uri=", ""),
      null
    );
    return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
  }

  // 2. Manejar reglas de negocio rotas, como categoría duplicada (Error 400)
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorMessage> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
    ErrorMessage message = new ErrorMessage(
      LocalDateTime.now(),
      HttpStatus.BAD_REQUEST.value(),
      HttpStatus.BAD_REQUEST.getReasonPhrase(),
      ex.getMessage(),
      request.getDescription(false).replace("uri=", ""),
      null
    );
    return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
  }

  // 3. Manejar errores de validación de DTOs con @Valid (Error 400)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorMessage> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
    List<String> errors = new ArrayList<>();
    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
      errors.add(error.getField() + ": " + error.getDefaultMessage());
    }

    ErrorMessage message = new ErrorMessage(
      LocalDateTime.now(),
      HttpStatus.BAD_REQUEST.value(),
      HttpStatus.BAD_REQUEST.getReasonPhrase(),
      "Error en la validación de los datos enviados",
      request.getDescription(false).replace("uri=", ""),
      errors // Aquí enviamos la lista de campos que fallaron
    );
    return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
  }

  // 4. Manejo global para cualquier otro error (Error 500)
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorMessage> handleGlobalException(Exception ex, WebRequest request) {
    ex.printStackTrace();

    ErrorMessage message = new ErrorMessage(
      LocalDateTime.now(),
      HttpStatus.INTERNAL_SERVER_ERROR.value(),
      HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
      "Ha ocurrido un error inesperado en el servidor",
      request.getDescription(false).replace("uri=", ""),
      null
    );
    // Aquí podrías usar un logger (ej. log.error("Error global", ex)) para no perder el rastro del fallo real
    return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}