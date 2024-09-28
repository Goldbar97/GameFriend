package com.gamefriend.service;

import com.gamefriend.dto.PasswordDTO;
import com.gamefriend.dto.SignDTO;
import com.gamefriend.dto.UserDTO;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {

  void signUp(SignDTO signDTO);

  String signIn(SignDTO signDTO);

  String adminSignIn(String ip, SignDTO signDTO);

  UserDTO getProfile(UserDetails userDetails);

  void updateProfile(UserDetails userDetails, UserDTO userDTO);

  void verifyPassword(UserDetails userDetails, PasswordDTO passwordDTO);

  void updatePassword(UserDetails userDetails, PasswordDTO passwordDTO);
}