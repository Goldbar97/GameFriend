package com.gamefriend.repository;

import com.gamefriend.entity.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

  boolean existsByUsername(String username);

  Optional<UserEntity> findByUsername(String username);
}