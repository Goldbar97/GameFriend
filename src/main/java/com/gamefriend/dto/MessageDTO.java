package com.gamefriend.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
public class MessageDTO {

  private String nickname;
  private String imageUrl;
  private String message;
  private LocalDateTime createdAt;
}