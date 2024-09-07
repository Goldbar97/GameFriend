package com.gamefriend.service;

import com.gamefriend.dto.PasswordDTO;
import com.gamefriend.dto.SignDTO;
import com.gamefriend.dto.UserDTO;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

public interface UserService {

  @Transactional
  void signUp(SignDTO request);

  @Transactional
  String signIn(SignDTO request);

  @Transactional(readOnly = true)
  UserDTO getProfile(UserDetails userDetails);

  @Transactional
  void updateProfile(UserDetails userDetails, UserDTO request);

  @Transactional
  void attemptPassword(UserDetails userDetails, PasswordDTO request);

  @Transactional
  void updatePassword(UserDetails userDetails, PasswordDTO request);
}