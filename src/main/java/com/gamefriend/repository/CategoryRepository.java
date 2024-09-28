package com.gamefriend.repository;

import com.gamefriend.entity.CategoryEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

  @Query("SELECT COUNT(c) > 0 FROM CATEGORY c WHERE LOWER(REPLACE(c.name, ' ', '')) = LOWER(REPLACE(:name, ' ', ''))")
  boolean existsByName(@Param("name") String name);

  @Query(value = "SELECT * FROM CATEGORY WHERE MATCH(name) AGAINST(:query IN BOOLEAN MODE)", nativeQuery = true)
  List<CategoryEntity> findByQuery(@Param("query") String query);
}