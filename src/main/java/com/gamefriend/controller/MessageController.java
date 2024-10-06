package com.gamefriend.controller;

import com.gamefriend.dto.ChatDTO;
import com.gamefriend.dto.MessageDTO;
import com.gamefriend.dto.UserDTO;
import com.gamefriend.service.MessageService;
import com.gamefriend.utils.XSSUtils;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

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
  public MessageDTO<UserDTO> sendUserInfo(Principal principal,
      @Header("simpSessionId") String sessionId) {

    return messageService.sendUserInfo(principal, sessionId);
  }

  @MessageMapping("/categories/{categoryId}/chatrooms/{chatroomId}/leave")
  @SendTo("/topic/categories/{categoryId}/chatrooms/{chatroomId}")
  public MessageDTO<UserDTO> sendLeaveInfo(Principal principal) {

    return messageService.sendLeaveInfo(principal);
  }

  @EventListener
  public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {

    messageService.handleUserDisconnection(event.getSessionId());
  }
}