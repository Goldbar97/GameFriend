package com.gamefriend.repository;

import com.gamefriend.dto.CategoryStatsDTO;
import com.gamefriend.entity.CategoryEntity;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

  @Query("SELECT COUNT(c) > 0 FROM CATEGORY c WHERE LOWER(REPLACE(c.name, ' ', '')) = LOWER(REPLACE(:name, ' ', ''))")
  boolean existsByName(@Param("name") String name);

  @Query(
      "SELECT new com.gamefriend.dto.CategoryStatsDTO(c.id, c.name, COUNT(cr), COALESCE(SUM(cr.present), 0)) "
          +
          "FROM CATEGORY c " +
          "LEFT JOIN CHATROOM cr ON cr.categoryEntity = c " +
          "GROUP BY c.id " +
          "ORDER BY SUM(cr.present) DESC")
  List<CategoryStatsDTO> findCategoryStats();

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT c FROM CATEGORY c WHERE c.id = :id")
  Optional<CategoryEntity> findByIdWithLock(@Param("id") Long id);
}