package com.gamefriend.entity;

import com.gamefriend.dto.ChatRoomDTO;
import com.gamefriend.exception.CustomException;
import com.gamefriend.exception.ErrorCode;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Entity(name = "CHAT_ROOM")
@Getter
@NoArgsConstructor
public class ChatRoomEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne
  private UserEntity userEntity;

  @ManyToOne
  private CategoryEntity categoryEntity;

  private String title;
  private int capacity;
  private int present;

  public void update(ChatRoomDTO chatRoomDTO) {

    if (chatRoomDTO.getCapacity() < present) {
      throw new CustomException(ErrorCode.BAD_REQUEST);
    }

    title = chatRoomDTO.getTitle();
    capacity = chatRoomDTO.getCapacity();
  }
}