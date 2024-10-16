package com.gamefriend.service.impl;

import com.gamefriend.component.JwtProvider;
import com.gamefriend.component.RedisComponent;
import com.gamefriend.dto.ImageUrlDTO;
import com.gamefriend.dto.NicknameDTO;
import com.gamefriend.dto.PasswordDTO;
import com.gamefriend.dto.SignInDTO;
import com.gamefriend.dto.SignInSuccessDTO;
import com.gamefriend.dto.SignUpDTO;
import com.gamefriend.dto.UserDTO;
import com.gamefriend.dto.UsernameDTO;
import com.gamefriend.entity.UserEntity;
import com.gamefriend.exception.CustomException;
import com.gamefriend.exception.ErrorCode;
import com.gamefriend.repository.UserRepository;
import com.gamefriend.security.CustomUserDetails;
import com.gamefriend.service.ImageService;
import com.gamefriend.service.UserService;
import com.gamefriend.type.UserRole;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final JwtProvider jwtProvider;
  private final PasswordEncoder passwordEncoder;
  private final RedisComponent redisComponent;
  private final UserRepository userRepository;
  private final ImageService imageService;

  @Override
  @Transactional
  public String adminSignIn(String ip, UsernameDTO usernameDTO) {

    Optional<UserEntity> userEntityOptional = userRepository.findByUsername(
        usernameDTO.getUsername());

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
  @Transactional
  public SignInSuccessDTO signIn(SignInDTO SignInDTO) {

    UserEntity userEntity = userRepository.findByUsername(SignInDTO.getUsername())
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    String username = userEntity.getUsername();
    UserRole role = userEntity.getRole();

    if (!passwordEncoder.matches(SignInDTO.getPassword(), userEntity.getPassword())) {
      redisComponent.signInFailed(username);
      throw new CustomException(ErrorCode.WRONG_PASSWORD);
    }
    redisComponent.signInSuccess(username);

    UserDTO userDTO = UserDTO.builder()
        .id(userEntity.getId())
        .nickname(userEntity.getNickname())
        .imageUrl(userEntity.getImageUrl())
        .build();

    redisComponent.saveUserDTO(username, userDTO);

    return SignInSuccessDTO.builder()
        .token(jwtProvider.generateToken(username, role))
        .userDTO(userDTO)
        .build();
  }

  @Override
  @Transactional(readOnly = true)
  public UserDTO getProfile(UserDetails userDetails) {

    UserDTO userDTO = redisComponent.getUserDTO(userDetails.getUsername());

    if (userDTO == null) {
      UserEntity userEntity = userRepository.findByUsername(userDetails.getUsername())
          .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

      userDTO = UserDTO.builder()
          .id(userEntity.getId())
          .nickname(userEntity.getNickname())
          .imageUrl(userEntity.getImageUrl())
          .build();

      redisComponent.saveUserDTO(userDetails.getUsername(), userDTO);
    }

    return userDTO;
  }

  @Override
  public void checkDuplication(UsernameDTO usernameDTO) {

    if (userRepository.existsByUsername(usernameDTO.getUsername())) {
      throw new CustomException(ErrorCode.USERNAME_EXISTS);
    }
  }

  @Override
  @Transactional
  public void signUp(SignUpDTO signUpDTO) {

    checkDuplication(new UsernameDTO(signUpDTO.getUsername()));
    checkPasswordEquality(signUpDTO);

    UserEntity userEntity = UserEntity.builder()
        .username(signUpDTO.getUsername())
        .nickname(signUpDTO.getNickname())
        .imageUrl("src/default-profile-image.png")
        .password(passwordEncoder.encode(signUpDTO.getPassword()))
        .role(UserRole.ROLE_USER)
        .build();

    userRepository.save(userEntity);
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

  @Override
  @Transactional
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

  private void checkPasswordEquality(SignUpDTO signUpDTO) {

    if (!signUpDTO.getPassword().equals(signUpDTO.getPasswordVerify())) {
      throw new CustomException(ErrorCode.PASSWORD_NOT_EQUAL);
    }
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    UserEntity userEntity = userRepository.findByUsername(username)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    return new CustomUserDetails(userEntity.getUsername(),
        List.of(new SimpleGrantedAuthority(userEntity.getRole().name())));
  }

  @Override
  @Transactional
  public UserDTO uploadProfileImage(UserDetails userDetails, MultipartFile file) {

    UserEntity userEntity = userRepository.findByUsername(userDetails.getUsername())
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    String imageUrl = imageService.uploadProfileImage(file);
    userEntity.updateProfileImage(imageUrl);
    UserDTO userDTO = userEntity.toDTO();
    redisComponent.saveUserDTO(userEntity.getUsername(), userDTO);

    return userDTO;
  }

  @Override
  @Transactional
  public void deleteProfileImage(UserDetails userDetails, ImageUrlDTO imageUrlDTO) {

    UserEntity userEntity = userRepository.findByUsername(userDetails.getUsername())
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    imageService.deleteProfileImage(imageUrlDTO.getImageUrl());
    userEntity.updateProfileImage("src/default-profile-image.png");
    redisComponent.saveUserDTO(userEntity.getUsername(), userEntity.toDTO());
  }

  @Override
  @Transactional
  public NicknameDTO updateNickname(UserDetails userDetails, NicknameDTO nicknameDTO) {

    UserEntity userEntity = userRepository.findByUsername(userDetails.getUsername())
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    userEntity.updateNickname(nicknameDTO.getNickname());
    redisComponent.saveUserDTO(userEntity.getUsername(), userEntity.toDTO());

    return new NicknameDTO(userEntity.getNickname());
  }
}