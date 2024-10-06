package com.gamefriend.entity;

import com.gamefriend.dto.ChatroomDTO;
import com.gamefriend.exception.CustomException;
import com.gamefriend.exception.ErrorCode;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Entity(name = "CHATROOM")
@Getter
@NoArgsConstructor
public class ChatroomEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne
  @JoinColumn(name = "user_entity_id")
  private UserEntity userEntity;

  @ManyToOne
  @JoinColumn(name = "category_entity_id")
  private CategoryEntity categoryEntity;

  private String title;
  private String entranceMessage;
  private String createdBy;
  private long capacity;
  private long present;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof UserEntity user)) return false;
    return id != null && id.equals(user.getId());
  }

  public void update(ChatroomDTO chatRoomDTO) {

    if (chatRoomDTO.getCapacity() < present) {
      throw new CustomException(ErrorCode.BAD_REQUEST);
    }

    title = chatRoomDTO.getTitle();
    capacity = chatRoomDTO.getCapacity();
  }

  public void update(Long count) {

    present = count;
  }
}