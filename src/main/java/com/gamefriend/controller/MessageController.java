package com.gamefriend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MessageController {

  @MessageMapping("/app/chatrooms/{chatRoomId}/sendMessage")
  @SendTo("/api/chatrooms/{chatRoomId}")
  public String sendMessage(@AuthenticationPrincipal UserDetails userDetails,
      @DestinationVariable Long chatRoomId, String message) {

    return userDetails.getUsername() + ": " + message;
  }
}