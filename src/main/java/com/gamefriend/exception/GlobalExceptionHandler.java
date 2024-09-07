package com.gamefriend.exception;

import com.gamefriend.response.ApiResponse;
import com.gamefriend.response.ApiResponseBody;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(CustomException.class)
  public ResponseEntity<ApiResponse> handleCustomException(CustomException e) {

    return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.error(e));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponseBody<Map<String, String>>> handleValidationException(
      MethodArgumentNotValidException e) {

    Map<String, String> errors = new HashMap<>();
    e.getBindingResult().getAllErrors().forEach((error) -> {
      String name = ((FieldError) error).getField();
      String message = error.getDefaultMessage();
      errors.put(name, message);
    });
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ApiResponseBody<>(400, "Wrong Input", errors));
  }
}