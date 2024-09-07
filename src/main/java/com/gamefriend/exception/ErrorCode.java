package com.gamefriend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
  EMAIL_EXISTS(409, "Email is already exists"),
  EXPIRED_TOKEN(401, "Token is expired"),
  INTERNAL_SERVER_ERROR(500, "Internal server error"),
  UNAUTHORIZED(401, "Unauthorized access"),
  USER_NOT_FOUND(404, "User doesn't exist"),
  USING_SAME_PASSWORD(409, "The password is already in use"),
  ACCOUNT_LOCKED(423, "The account is temporary locked"),
  WRONG_PASSWORD(400, "Wrong password");

  private final int statusCode;
  private final String message;
}