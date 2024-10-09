package com.gamefriend.service;

import com.gamefriend.dto.PasswordDTO;
import com.gamefriend.dto.SignInDTO;
import com.gamefriend.dto.SignInSuccessDTO;
import com.gamefriend.dto.SignUpDTO;
import com.gamefriend.dto.UserDTO;
import com.gamefriend.dto.UsernameDTO;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

  String adminSignIn(String ip, UsernameDTO usernameDTO);

  SignInSuccessDTO signIn(SignInDTO SignInDTO);

  UserDTO getProfile(UserDetails userDetails);

  void checkDuplication(UsernameDTO usernameDTO);

  void signUp(SignUpDTO signUpDTO);

  void updatePassword(UserDetails userDetails, PasswordDTO passwordDTO);

  void updateProfile(UserDetails userDetails, UserDTO userDTO);

  void verifyPassword(UserDetails userDetails, PasswordDTO passwordDTO);
}