package com.gamefriend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
public class ChatRoomDTO {

  @NotBlank
  private String title;

  @Min(value = 1)
  private int capacity;

  private String createdBy;
  private String password;
  private int present;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}