package com.gamefriend.repository;

import com.gamefriend.entity.CategoryEntity;
import com.gamefriend.entity.ChatroomEntity;
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
public interface ChatroomRepository extends JpaRepository<ChatroomEntity, Long> {

  @Query("SELECT c FROM CHATROOM c JOIN FETCH c.userEntity WHERE c.categoryEntity = :categoryEntity")
  List<ChatroomEntity> findAllByCategoryEntity(
      @Param("categoryEntity") CategoryEntity categoryEntity);

  Optional<ChatroomEntity> findByUserEntity(UserEntity userEntity);

  @Query(value = "SELECT c.* FROM CHATROOM c "
      + "WHERE MATCH(title) "
      + "AGAINST(:query IN BOOLEAN MODE) "
      + "AND c.category_entity_id = :categoryId", nativeQuery = true)
  List<ChatroomEntity> findByCategoryIdAndQuery(@Param("categoryId") Long categoryId,
      @Param("query") String query);

  @Query("SELECT c FROM CHATROOM c WHERE c.id = :id AND c.categoryEntity.id = :categoryId")
  Optional<ChatroomEntity> findByIdAndCategoryId(@Param("id") Long id,
      @Param("categoryId") Long categoryId);

  boolean existsByUserEntity(UserEntity userEntity);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT c FROM CHATROOM c WHERE c.id = :chatroomId")
  Optional<ChatroomEntity> findByIdWithLock(@Param("chatroomId") Long chatroomId);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT c FROM CHATROOM c JOIN CHATROOM_USER cu ON c.id = cu.chatroomEntity.id WHERE cu.id = :chatroomUserId")
  Optional<ChatroomEntity> findByChatroomUserIdWithLock(
      @Param("chatroomUserId") Long chatroomUserId);
}