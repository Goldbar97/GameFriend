package com.gamefriend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
public class CategoryStatsDTO {
  private Long id;
  private String name;
  private long rooms;
  private long participants;
}