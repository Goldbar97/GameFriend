package com.gamefriend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PasswordDTO {

  private String password;
  private String confirmPassword;
}