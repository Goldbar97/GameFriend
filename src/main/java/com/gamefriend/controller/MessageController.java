package com.gamefriend.controller;

import com.gamefriend.dto.MessageDTO;
import com.gamefriend.response.ApiResponseBody;
import com.gamefriend.service.MessageService;
import com.gamefriend.utils.XSSUtils;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MessageController {

  private final MessageService messageService;

  @MessageMapping("/categories/{categoryId}/chatrooms/{chatroomId}")
  @SendTo("/topic/categories/{categoryId}/chatrooms/{chatroomId}")
  public MessageDTO sendMessage(Principal principal,
      @DestinationVariable("categoryId") Long categoryId,
      @DestinationVariable("chatroomId") Long chatroomId,
      @Payload String message) {

    message = XSSUtils.sanitize(message);
    return messageService.sendMessage(principal, categoryId, chatroomId, message);
  }
//  @GetMapping("/api/categories/{categoryId}/chatrooms/{chatroomId}/chats")
//  public ResponseEntity<ApiResponseBody<List<MessageDTO>>> getMessages(@PathVariable("categoryId") Long categoryId,
//      @PathVariable("chatroomId") Long chatroomId) {
//
//    return messageService.getMessages(categoryId, chatroomId);
//
//  }
}