package com.gamefriend.controller;

import com.gamefriend.dto.ChatDTO;
import com.gamefriend.dto.MessageDTO;
import com.gamefriend.dto.UserDTO;
import com.gamefriend.service.MessageService;
import com.gamefriend.utils.XSSUtils;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MessageController {

  private final MessageService messageService;

  @MessageMapping("/categories/{categoryId}/chatrooms/{chatroomId}/chat")
  @SendTo("/topic/categories/{categoryId}/chatrooms/{chatroomId}")
  public MessageDTO<ChatDTO> sendMessage(Principal principal,
      @DestinationVariable("categoryId") Long categoryId,
      @DestinationVariable("chatroomId") Long chatroomId,
      @Payload String message) {

    message = XSSUtils.sanitize(message);
    return messageService.sendMessage(principal, categoryId, chatroomId, message);
  }

  @MessageMapping("/categories/{categoryId}/chatrooms/{chatroomId}/user")
  @SendTo("/topic/categories/{categoryId}/chatrooms/{chatroomId}")
  public MessageDTO<UserDTO> sendUserInfo(@DestinationVariable("categoryId") Long categoryId,
      @DestinationVariable("chatroomId") Long chatroomId, @Payload UserDTO userDTO) {

    return messageService.sendUserInfo(userDTO);
  }
}