package com.gamefriend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
  ACCOUNT_LOCKED(423, "The account is temporary locked"),
  BAD_REQUEST(400, "Bad request"),
  CATEGORY_EXISTS(409, "The category already exists"),
  CHATROOM_EXISTS(409, "Your chatroom already exists"),
  CHATROOM_FULL(400, "The chatroom is full"),
  EXPIRED_TOKEN(401, "Token is expired"),
  INTERNAL_SERVER_ERROR(500, "Internal server error"),
  NICKNAME_EXISTS(409, "The nickname already exists"),
  NOT_FOUND(404, "The resource doesn't exist"),
  PASSWORD_NOT_EQUAL(400, "The password is not equal"),
  UNAUTHORIZED(401, "Unauthorized access"),
  USERNAME_EXISTS(409, "Username already exists"),
  USER_NOT_FOUND(404, "User doesn't exist"),
  USING_SAME_PASSWORD(409, "The password is already in use"),
  WRONG_PASSWORD(400, "Wrong password");

  private final int statusCode;
  private final String message;
}