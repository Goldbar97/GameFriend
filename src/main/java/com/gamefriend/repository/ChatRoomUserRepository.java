package com.gamefriend.repository;

import com.gamefriend.entity.ChatroomEntity;
import com.gamefriend.entity.ChatroomUserEntity;
import com.gamefriend.entity.UserEntity;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatroomUserRepository extends
    JpaRepository<ChatroomUserEntity, Long> {

  Optional<ChatroomUserEntity> findByUserEntity(UserEntity userEntity);

  @Query("SELECT cu FROM CHATROOM_USER cu JOIN FETCH cu.userEntity WHERE cu.chatroomEntity = :chatroomEntity")
  List<ChatroomUserEntity> findAllByChatroomEntity(
      @Param("chatroomEntity") ChatroomEntity chatroomEntity);

  boolean existsByUserEntityAndChatroomEntity(UserEntity userEntity, ChatroomEntity chatroomEntity);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  long countByChatroomEntity(ChatroomEntity chatroomEntity);
}