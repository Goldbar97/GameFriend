package com.gamefriend.security;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {

  private final String email;
  private final Collection<? extends GrantedAuthority> authorities;

  public CustomUserDetails(String email, Collection<? extends GrantedAuthority> authorities) {

    this.email = email;
    this.authorities = authorities;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {

    return authorities;
  }

  @Override
  public String getPassword() {

    return null;
  }

  @Override
  public String getUsername() {

    return email;
  }

  @Override
  public boolean isAccountNonExpired() {

    return true;
  }

  @Override
  public boolean isAccountNonLocked() {

    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {

    return true;
  }

  @Override
  public boolean isEnabled() {

    return true;
  }
}