package com.gamefriend.repository;

import com.gamefriend.entity.ChatroomEntity;
import com.gamefriend.entity.ChatroomUserEntity;
import com.gamefriend.entity.UserEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatroomUserRepository extends
    JpaRepository<ChatroomUserEntity, Long> {

  Optional<ChatroomUserEntity> findByUserEntity(UserEntity userEntity);

  List<ChatroomUserEntity> findAllByChatRoomEntity(ChatroomEntity chatRoomEntity);

  boolean existsByUserEntityAndChatRoomEntity(UserEntity userEntity, ChatroomEntity chatroomEntity);
}