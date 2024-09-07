package com.gamefriend.response;

import com.gamefriend.exception.CustomException;
import lombok.Getter;

@Getter
public class ApiResponseBody<T> extends ApiResponse {

  private final T responseBody;

  public ApiResponseBody(int statusCode, String message, T responseBody) {

    super(statusCode, message);
    this.responseBody = responseBody;
  }

  public static <T> ApiResponseBody<T> okBody(T responseBody) {

    return new ApiResponseBody<>(200, "Ok", responseBody);
  }

  public static <T> ApiResponseBody<T> errorBody(CustomException e, T responseBody) {

    return new ApiResponseBody<>(e.getStatusCode(), e.getMessage(), responseBody);
  }
}