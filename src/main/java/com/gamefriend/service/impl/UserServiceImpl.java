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
  public void signUp(SignDTO request) {

    if (userRepository.existsByEmail(request.getEmail())) {
      throw new CustomException(ErrorCode.EMAIL_EXISTS);
    }

    UserEntity userEntity = UserEntity.builder()
        .name("Undefined")
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .build();

    userRepository.save(userEntity);
  }

  @Override
  public String signIn(SignDTO request) {

    UserEntity userEntity = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    final String email = userEntity.getEmail();

    if (!passwordEncoder.matches(request.getPassword(), userEntity.getPassword())) {
      redisComponent.signInFailed(email);
      throw new CustomException(ErrorCode.WRONG_PASSWORD);
    }
    redisComponent.signInSuccess(email);

    return jwtProvider.generateToken(email);
  }

  @Override
  public UserDTO getProfile(UserDetails userDetails) {

    UserEntity userEntity = userRepository.findByEmail(userDetails.getUsername())
        .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

    return UserDTO.builder()
        .name(userEntity.getName())
        .email(userEntity.getEmail())
        .imageUrl(userEntity.getImageUrl())
        .build();
  }

  @Override
  public void updateProfile(UserDetails userDetails, UserDTO request) {

    UserEntity userEntity = userRepository.findByEmail(userDetails.getUsername())
        .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

    userEntity.update(request);
  }

  @Override
  public void attemptPassword(UserDetails userDetails, PasswordDTO request) {

    UserEntity userEntity = userRepository.findByEmail(userDetails.getUsername())
        .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

    if (!passwordEncoder.matches(request.getPassword(), userEntity.getPassword())) {
      throw new CustomException(ErrorCode.UNAUTHORIZED);
    }
  }

  @Override
  public void updatePassword(UserDetails userDetails, PasswordDTO request) {

    UserEntity userEntity = userRepository.findByEmail(userDetails.getUsername())
        .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

    if (passwordEncoder.matches(request.getPassword(), userEntity.getPassword())) {
      throw new CustomException(ErrorCode.USING_SAME_PASSWORD);
    }

    if (!request.getPassword().equals(request.getConfirmPassword())) {
      throw new CustomException(ErrorCode.WRONG_PASSWORD);
    }

    userEntity.updatePassword(passwordEncoder.encode(request.getPassword()));
  }
}