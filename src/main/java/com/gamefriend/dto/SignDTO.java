package com.gamefriend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class SignDTO {

  private String username;
  private String nickname;
  private String password;
}