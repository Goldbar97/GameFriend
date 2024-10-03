package com.gamefriend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class SignInDTO {

  @NotBlank(message = "Username cannot be blank")
  @Size(min = 2, max = 20, message = "Username must be between 2 and 20 characters")
  @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Username can only contain letters and numbers")
  private String username;

  @NotBlank(message = "Password cannot be blank")
  @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
  @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*()_+=-]*$", message = "Password can only contain letters, numbers, and special characters")
  private String password;
}