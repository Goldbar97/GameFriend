package com.gamefriend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
public class CategoryDTO {

  private Long id;

  @NotBlank
  private String name;

  private long rooms;
  private long participants;
}