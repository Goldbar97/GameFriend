package com.gamefriend.repository;

import com.gamefriend.entity.ChatEntity;
import com.gamefriend.entity.ChatroomEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<ChatEntity, Long> {

  @Query("SELECT c FROM CHAT c JOIN FETCH c.userEntity WHERE c.chatroomEntity = :chatroomEntity")
  List<ChatEntity> findAllByChatroomEntity(@Param("chatroomEntity") ChatroomEntity chatroomEntity);
}