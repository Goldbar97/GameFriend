package com.gamefriend.repository;

import com.gamefriend.entity.CategoryEntity;
import com.gamefriend.entity.ChatroomEntity;
import com.gamefriend.entity.UserEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
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
  Optional<ChatroomEntity> findByIdAndCategoryId(@Param("id") Long id,@Param("categoryId") Long categoryId);

  boolean existsByUserEntity(UserEntity userEntity);
}