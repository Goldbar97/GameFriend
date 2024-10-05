package com.gamefriend.controller;

import com.gamefriend.dto.PasswordDTO;
import com.gamefriend.dto.SignInDTO;
import com.gamefriend.dto.SignUpDTO;
import com.gamefriend.dto.UserDTO;
import com.gamefriend.dto.UsernameDTO;
import com.gamefriend.response.ApiResponse;
import com.gamefriend.response.ApiResponseBody;
import com.gamefriend.service.UserService;
import com.gamefriend.utils.ClientUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping("/api/users/signup")
  public ResponseEntity<ApiResponse> signUp(@RequestBody @Validated SignUpDTO signUpDTO) {

    userService.signUp(signUpDTO);
    return ResponseEntity.ok(ApiResponse.ok());
  }

  @PostMapping("/api/users/check-duplication")
  public ResponseEntity<ApiResponse> checkDuplication(@RequestBody @Validated UsernameDTO usernameDTO) {

    userService.checkDuplication(usernameDTO);
    return ResponseEntity.ok(ApiResponse.ok());
  }

  @PostMapping("/api/users/signin")
  public ResponseEntity<ApiResponseBody<String>> signIn(@RequestBody @Validated SignInDTO signInDTO) {

    String token = userService.signIn(signInDTO);
    return ResponseEntity.ok(ApiResponseBody.okBody(token));
  }

  @PostMapping("/api/admins/signin")
  public ResponseEntity<ApiResponseBody<String>> adminSignIn(HttpServletRequest request,
      @RequestBody @Validated UsernameDTO usernameDTO) {

    String ip = ClientUtils.getIP(request);
    String token = userService.adminSignIn(ip, usernameDTO);
    return ResponseEntity.ok(ApiResponseBody.okBody(token));
  }

  @GetMapping("/api/users/profile")
  public ResponseEntity<ApiResponseBody<UserDTO>> getProfile(
      @AuthenticationPrincipal UserDetails userDetails) {

    UserDTO userDTO = userService.getProfile(userDetails);
    return ResponseEntity.ok(ApiResponseBody.okBody(userDTO));
  }

  @PostMapping("/api/users/password")
  public ResponseEntity<ApiResponse> verifyPassword(
      @AuthenticationPrincipal UserDetails userDetails,
      @RequestBody @Validated PasswordDTO passwordDTO) {

    userService.verifyPassword(userDetails, passwordDTO);
    return ResponseEntity.ok(ApiResponse.ok());
  }

  @PutMapping("/api/users/profile")
  public ResponseEntity<ApiResponse> updateProfile(@AuthenticationPrincipal UserDetails userDetails,
      @RequestBody @Validated UserDTO userDTO) {

    userService.updateProfile(userDetails, userDTO);
    return ResponseEntity.ok(ApiResponse.ok());
  }

  @PutMapping("/api/users/password")
  public ResponseEntity<ApiResponse> updatePassword(
      @AuthenticationPrincipal UserDetails userDetails,
      @RequestBody @Validated PasswordDTO passwordDTO) {

    userService.updatePassword(userDetails, passwordDTO);
    return ResponseEntity.ok(ApiResponse.ok());
  }
}