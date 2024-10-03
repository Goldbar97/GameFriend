package com.gamefriend.service.impl;

import com.gamefriend.component.JwtProvider;
import com.gamefriend.component.RedisComponent;
import com.gamefriend.dto.PasswordDTO;
import com.gamefriend.dto.SignInDTO;
import com.gamefriend.dto.SignUpDTO;
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
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final JwtProvider jwtProvider;
  private final PasswordEncoder passwordEncoder;
  private final RedisComponent redisComponent;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public void signUp(SignUpDTO signUpDTO) {

    checkDuplication(signUpDTO);
    checkPasswordEquality(signUpDTO);

    UserEntity userEntity = UserEntity.builder()
        .username(signUpDTO.getUsername())
        .nickname(signUpDTO.getNickname())
        .password(passwordEncoder.encode(signUpDTO.getPassword()))
        .role(UserRole.ROLE_USER)
        .build();

    userRepository.save(userEntity);
  }

  @Override
  public void checkDuplication(SignUpDTO signUpDTO) {

    if (userRepository.existsByUsername(signUpDTO.getUsername())) {
      throw new CustomException(ErrorCode.USERNAME_EXISTS);
    }
  }

  private void checkPasswordEquality(SignUpDTO signUpDTO) {

    if (!signUpDTO.getPassword().equals(signUpDTO.getPasswordVerify())) {
      throw new CustomException(ErrorCode.PASSWORD_NOT_EQUAL);
    }
  }

  @Override
  @Transactional
  public String signIn(SignInDTO SignInDTO) {

    UserEntity userEntity = userRepository.findByUsername(SignInDTO.getUsername())
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    String username = userEntity.getUsername();
    UserRole role = userEntity.getRole();

    if (!passwordEncoder.matches(SignInDTO.getPassword(), userEntity.getPassword())) {
      redisComponent.signInFailed(username);
      throw new CustomException(ErrorCode.WRONG_PASSWORD);
    }
    redisComponent.signInSuccess(username);

    return jwtProvider.generateToken(username, role);
  }

  @Override
  @Transactional
  public String adminSignIn(String ip, SignInDTO signInDTO) {

    Optional<UserEntity> userEntityOptional = userRepository.findByUsername(signInDTO.getUsername());

    if (userEntityOptional.isEmpty()) {
      redisComponent.signInFailed(ip);
      throw new CustomException(ErrorCode.USER_NOT_FOUND);
    }
    redisComponent.signInSuccess(ip);

    UserEntity userEntity = userEntityOptional.get();
    String username = userEntity.getUsername();
    UserRole role = userEntity.getRole();

    return jwtProvider.generateToken(username, role);
  }

  @Override
  @Transactional(readOnly = true)
  public UserDTO getProfile(UserDetails userDetails) {

    UserEntity userEntity = userRepository.findByUsername(userDetails.getUsername())
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    return UserDTO.builder()
        .username(userEntity.getUsername())
        .nickname(userEntity.getNickname())
        .imageUrl(userEntity.getImageUrl())
        .build();
  }

  @Override
  @Transactional
  public void updateProfile(UserDetails userDetails, UserDTO userDTO) {

    UserEntity userEntity = userRepository.findByUsername(userDetails.getUsername())
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    userEntity.update(userDTO);
  }

  @Override
  public void verifyPassword(UserDetails userDetails, PasswordDTO passwordDTO) {

    UserEntity userEntity = userRepository.findByUsername(userDetails.getUsername())
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    String username = userEntity.getUsername();

    if (!passwordEncoder.matches(passwordDTO.getPassword(), userEntity.getPassword())) {
      redisComponent.signInFailed(username);
      throw new CustomException(ErrorCode.WRONG_PASSWORD);
    }
    redisComponent.signInSuccess(username);
  }

  @Override
  @Transactional
  public void updatePassword(UserDetails userDetails, PasswordDTO passwordDTO) {

    UserEntity userEntity = userRepository.findByUsername(userDetails.getUsername())
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    if (passwordEncoder.matches(passwordDTO.getPassword(), userEntity.getPassword())) {
      throw new CustomException(ErrorCode.USING_SAME_PASSWORD);
    }

    if (!passwordDTO.getPassword().equals(passwordDTO.getConfirmPassword())) {
      throw new CustomException(ErrorCode.WRONG_PASSWORD);
    }

    userEntity.updatePassword(passwordEncoder.encode(passwordDTO.getPassword()));
  }
}