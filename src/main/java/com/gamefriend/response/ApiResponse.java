package com.gamefriend.response;

import com.gamefriend.exception.CustomException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ApiResponse {

  private final int statusCode;
  private final String message;

  public static ApiResponse ok() {

    return new ApiResponse(200, "Successful");
  }

  public static ApiResponse ok(String message) {

    return new ApiResponse(200, message);
  }

  public static ApiResponse error(CustomException e) {

    return new ApiResponse(e.getStatusCode(), e.getMessage());
  }
}