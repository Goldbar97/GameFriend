package com.gamefriend.repository;

import com.gamefriend.entity.CategoryEntity;
import com.gamefriend.entity.ChatRoomEntity;
import com.gamefriend.entity.UserEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {

  @Query("SELECT c FROM CHATROOM c JOIN FETCH c.userEntity WHERE c.categoryEntity = :categoryEntity")
  List<ChatRoomEntity> findAllByCategoryEntity(CategoryEntity categoryEntity);

  Optional<ChatRoomEntity> findByUserEntity(UserEntity userEntity);
}