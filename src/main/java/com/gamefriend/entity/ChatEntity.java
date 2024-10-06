package com.gamefriend.entity;

import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Entity(name = "CHAT")
@Getter
@NoArgsConstructor
public class ChatEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_entity_id")
  private UserEntity userEntity;

  @ManyToOne
  @JoinColumn(name = "chatroom_entity_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  private ChatroomEntity chatroomEntity;

  private String message;
}