package com.gamefriend.service;

import com.gamefriend.dto.PasswordDTO;
import com.gamefriend.dto.SignDTO;
import com.gamefriend.dto.UserDTO;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

public interface UserService {

  @Transactional
  void signUp(SignDTO signDTO);

  @Transactional
  String signIn(SignDTO signDTO);

  @Transactional
  String adminSignIn(String ip, SignDTO signDTO);

  @Transactional(readOnly = true)
  UserDTO getProfile(UserDetails userDetails);

  @Transactional
  void updateProfile(UserDetails userDetails, UserDTO userDTO);

  @Transactional
  void attemptPassword(UserDetails userDetails, PasswordDTO passwordDTO);

  @Transactional
  void updatePassword(UserDetails userDetails, PasswordDTO passwordDTO);

  @Transactional
  void findPassword(UserDTO userDTO);
}