package com.gamefriend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Entity(name = "CATEGORY")
@Getter
@NoArgsConstructor
public class CategoryEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private int rooms;
  private int participants;

  public void update(String name) {

    this.name = name;
  }

  public void incrementRooms() {

    rooms++;
  }

  public void decrementRooms() {

    if (rooms == 0) {
      return;
    }
    rooms--;
  }

  public void incrementParticipants() {
    participants++;
  }

  public void decrementParticipants() {
    if (participants == 0) {
      return;
    }
    participants--;
  }

  public void decrementParticipants(int i) {
    if (participants < i) {
      return;
    }
    participants = participants - i;
  }
}