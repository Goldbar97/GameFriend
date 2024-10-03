package com.gamefriend.service;

import com.gamefriend.dto.PasswordDTO;
import com.gamefriend.dto.SignInDTO;
import com.gamefriend.dto.SignUpDTO;
import com.gamefriend.dto.UserDTO;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {

  void signUp(SignUpDTO signUpDTO);

  void checkDuplication(SignUpDTO signUpDTO);

  String signIn(SignInDTO SignInDTO);

  String adminSignIn(String ip, SignInDTO signInDTO);

  UserDTO getProfile(UserDetails userDetails);

  void updateProfile(UserDetails userDetails, UserDTO userDTO);

  void verifyPassword(UserDetails userDetails, PasswordDTO passwordDTO);

  void updatePassword(UserDetails userDetails, PasswordDTO passwordDTO);
}