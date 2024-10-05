package com.gamefriend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
public class UserDTO {

  private Long id;
  private String imageUrl;
  private String nickname;
}