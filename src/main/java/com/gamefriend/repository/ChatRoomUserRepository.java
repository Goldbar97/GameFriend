package com.gamefriend.repository;

import com.gamefriend.entity.ChatRoomEntity;
import com.gamefriend.entity.ChatRoomUserEntity;
import com.gamefriend.entity.UserEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomUserRepository extends
    JpaRepository<ChatRoomUserEntity, Long> {

  Optional<ChatRoomUserEntity> findByUserEntity(UserEntity userEntity);

  List<ChatRoomUserEntity> findAllByChatRoomEntity(ChatRoomEntity chatRoomEntity);
}