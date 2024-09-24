package com.gamefriend.service.impl;

import com.gamefriend.component.JwtProvider;
import com.gamefriend.component.RedisComponent;
import com.gamefriend.dto.PasswordDTO;
import com.gamefriend.dto.SignDTO;
import com.gamefriend.dto.UserDTO;
import com.gamefriend.entity.UserEntity;
import com.gamefriend.exception.CustomException;
import com.gamefriend.exception.ErrorCode;
import com.gamefriend.repository.UserRepository;
import com.gamefriend.service.UserService;
import com.gamefriend.type.UserRole;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final JwtProvider jwtProvider;
  private final PasswordEncoder passwordEncoder;
  private final RedisComponent redisComponent;
  private final UserRepository userRepository;

  @Override
  public void signUp(SignDTO signDTO) {

    if (userRepository.existsByEmail(signDTO.getEmail())) {
      throw new CustomException(ErrorCode.EMAIL_EXISTS);
    }

    UserEntity userEntity = UserEntity.builder()
        .email(signDTO.getEmail())
        .password(passwordEncoder.encode(signDTO.getPassword()))
        .role(UserRole.ROLE_USER)
        .build();

    userRepository.save(userEntity);
  }

  @Override
  public String signIn(SignDTO signDTO) {

    UserEntity userEntity = userRepository.findByEmail(signDTO.getEmail())
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    String email = userEntity.getEmail();
    UserRole role = userEntity.getRole();

    if (!passwordEncoder.matches(signDTO.getPassword(), userEntity.getPassword())) {
      redisComponent.signInFailed(email);
      throw new CustomException(ErrorCode.WRONG_PASSWORD);
    }
    redisComponent.signInSuccess(email);

    return jwtProvider.generateToken(email, role);
  }

  @Override
  public String adminSignIn(String ip, SignDTO signDTO) {

    Optional<UserEntity> userEntityOptional = userRepository.findByEmail(signDTO.getEmail());

    if (userEntityOptional.isEmpty()) {
      redisComponent.signInFailed(ip);
      throw new CustomException(ErrorCode.USER_NOT_FOUND);
    }
    redisComponent.signInSuccess(ip);

    UserEntity userEntity = userEntityOptional.get();
    String email = userEntity.getEmail();
    UserRole role = userEntity.getRole();

    return jwtProvider.generateToken(email, role);
  }

  @Override
  public UserDTO getProfile(UserDetails userDetails) {

    UserEntity userEntity = userRepository.findByEmail(userDetails.getUsername())
        .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

    return UserDTO.builder()
        .email(userEntity.getEmail())
        .imageUrl(userEntity.getImageUrl())
        .build();
  }

  @Override
  public void updateProfile(UserDetails userDetails, UserDTO userDTO) {

    UserEntity userEntity = userRepository.findByEmail(userDetails.getUsername())
        .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

    userEntity.update(userDTO);
  }

  @Override
  public void verifyPassword(UserDetails userDetails, PasswordDTO passwordDTO) {

    UserEntity userEntity = userRepository.findByEmail(userDetails.getUsername())
        .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));
    String email = userEntity.getEmail();

    if (!passwordEncoder.matches(passwordDTO.getPassword(), userEntity.getPassword())) {
      redisComponent.signInFailed(email);
      throw new CustomException(ErrorCode.UNAUTHORIZED);
    }
    redisComponent.signInSuccess(email);
  }

  @Override
  public void updatePassword(UserDetails userDetails, PasswordDTO passwordDTO) {

    UserEntity userEntity = userRepository.findByEmail(userDetails.getUsername())
        .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

    if (passwordEncoder.matches(passwordDTO.getPassword(), userEntity.getPassword())) {
      throw new CustomException(ErrorCode.USING_SAME_PASSWORD);
    }

    if (!passwordDTO.getPassword().equals(passwordDTO.getConfirmPassword())) {
      throw new CustomException(ErrorCode.WRONG_PASSWORD);
    }

    userEntity.updatePassword(passwordEncoder.encode(passwordDTO.getPassword()));
  }

  @Override
  public void findPassword(UserDTO userDTO) {

  }
}