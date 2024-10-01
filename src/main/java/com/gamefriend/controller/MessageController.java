package com.gamefriend.controller;

import com.gamefriend.dto.MessageDTO;
import com.gamefriend.service.MessageService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageController {

  private final MessageService messageService;

  @MessageMapping("/categories/{categoryId}/chatrooms/{chatroomId}")
  @SendTo("/topic/categories/{categoryId}/chatrooms/{chatroomId}")
  public MessageDTO sendMessage(@DestinationVariable("categoryId") Long categoryId,
      @DestinationVariable("chatroomId") Long chatroomId, @Payload String message,
      Principal principal) {

    System.out.println(principal);

    return messageService.sendMessage(principal, categoryId, chatroomId, message);
  }
}