package com.gamefriend.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

  private final int statusCode;
  private final String message;

  public CustomException(ErrorCode errorCode) {

    super(errorCode.getMessage());
    this.statusCode = errorCode.getStatusCode();
    this.message = errorCode.getMessage();
  }
}