package com.gamefriend.controller;

import com.gamefriend.dto.PasswordDTO;
import com.gamefriend.dto.SignDTO;
import com.gamefriend.dto.UserDTO;
import com.gamefriend.response.ApiResponse;
import com.gamefriend.response.ApiResponseBody;
import com.gamefriend.service.UserService;
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
  public ResponseEntity<ApiResponse> signUp(
      @RequestBody @Validated SignDTO request
  ) {

    userService.signUp(request);
    return ResponseEntity.ok(ApiResponse.ok());
  }

  @PostMapping("/api/users/signin")
  public ResponseEntity<ApiResponseBody<String>> signIn(
      @RequestBody @Validated SignDTO request
  ) {

    String token = userService.signIn(request);
    return ResponseEntity.ok(ApiResponseBody.okBody(token));
  }

  @GetMapping("/api/users/profile")
  public ResponseEntity<ApiResponseBody<UserDTO>> getProfile(
      @AuthenticationPrincipal UserDetails userDetails
  ) {

    UserDTO userDTO = userService.getProfile(userDetails);
    return ResponseEntity.ok(ApiResponseBody.okBody(userDTO));
  }

  @PostMapping("/api/users/password")
  public ResponseEntity<ApiResponse> attemptPassword(
      @AuthenticationPrincipal UserDetails userDetails,
      @RequestBody @Validated PasswordDTO request
  ) {

    userService.attemptPassword(userDetails, request);
    return ResponseEntity.ok(ApiResponse.ok());
  }

  @PutMapping("/api/users/profile")
  public ResponseEntity<ApiResponse> updateProfile(
      @AuthenticationPrincipal UserDetails userDetails,
      @RequestBody @Validated UserDTO request
  ) {

    userService.updateProfile(userDetails, request);
    return ResponseEntity.ok(ApiResponse.ok());
  }
}