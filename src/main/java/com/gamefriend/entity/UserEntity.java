package com.gamefriend.entity;

import com.gamefriend.dto.UserDTO;
import com.gamefriend.type.UserRole;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Entity(name = "USERS")
@Getter
@NoArgsConstructor
public class UserEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String username;
  private String nickname;
  private String imageUrl;
  private String password;

  @Enumerated(EnumType.STRING)
  private UserRole role;

  public void updatePassword(String password) {

    this.password = password;
  }

  public void updateProfileImage(String imageUrl) {

    this.imageUrl = imageUrl;
  }

  public void updateNickname(String nickname) {

    this.nickname = nickname;
  }

  public UserDTO toDTO() {

    return UserDTO.builder()
        .id(id)
        .nickname(nickname)
        .imageUrl(imageUrl)
        .build();
  }
}