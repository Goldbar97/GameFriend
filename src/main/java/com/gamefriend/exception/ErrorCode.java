package com.gamefriend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
  ACCOUNT_LOCKED(423, "The account is temporary locked"),
  BAD_REQUEST(400, "Bad request"),
  CHATROOM_EXISTS(409, "Your chatroom is already exists"),
  CHATROOM_FULL(400, "The chatroom is full"),
  EXPIRED_TOKEN(401, "Token is expired"),
  INTERNAL_SERVER_ERROR(500, "Internal server error"),
  NOT_FOUND(404, "The resource doesn't exist"),
  UNAUTHORIZED(401, "Unauthorized access"),
  USERNAME_EXISTS(409, "Username is already exists"),
  USER_NOT_FOUND(404, "User doesn't exist"),
  USING_SAME_PASSWORD(409, "The password is already in use"),
  WRONG_PASSWORD(400, "Wrong password");

  private final int statusCode;
  private final String message;
}