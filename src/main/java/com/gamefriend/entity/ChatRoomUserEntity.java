package com.gamefriend.entity;

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
@Entity(name = "CHATROOM_USER")
@Getter
@NoArgsConstructor
public class ChatroomUserEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne
  @JoinColumn(name = "user_entity_id")
  private UserEntity userEntity;

  @ManyToOne
  @JoinColumn(name = "chatroom_entity_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  private ChatroomEntity chatroomEntity;
}