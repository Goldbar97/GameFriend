package com.gamefriend.controller;

import com.gamefriend.dto.ChatRoomDTO;
import com.gamefriend.response.ApiResponse;
import com.gamefriend.response.ApiResponseBody;
import com.gamefriend.service.ChatroomService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatroomController {

  private final ChatroomService chatroomService;

  @PostMapping("/api/categories/{categoryId}")
  public ResponseEntity<ApiResponse> createChatRoom(
      @AuthenticationPrincipal UserDetails userDetails, @PathVariable("categoryId") Long categoryId,
      @RequestBody @Validated ChatRoomDTO chatRoomDTO) {

    chatroomService.createChatRoom(userDetails, categoryId, chatRoomDTO);
    return ResponseEntity.ok(ApiResponse.ok());
  }

  @MessageMapping("/app/chatrooms/{chatRoomId}/enter")
  @SendTo("/app/chatrooms/{chatRoomId}")
  public String enterChatRoom(@AuthenticationPrincipal UserDetails userDetails,
      @DestinationVariable Long chatRoomId) {

    chatroomService.enterChatRoom(userDetails, chatRoomId);

    return userDetails.getUsername() + " has entered the room.";
  }

  @MessageMapping("/app/chatrooms/{chatRoomId}/leave")
  @SendTo("/app/chatrooms/{chatRoomId}")
  public String leaveChatRoom(@AuthenticationPrincipal UserDetails userDetails,
      @DestinationVariable Long chatRoomId) {

    chatroomService.leaveChatRoom(userDetails, chatRoomId);

    return userDetails.getUsername() + " has left the room.";
  }

  @GetMapping("/api/users/chatroom")
  public ResponseEntity<ApiResponseBody<ChatRoomDTO>> getChatRoom(
      @AuthenticationPrincipal UserDetails userDetails) {

    ChatRoomDTO chatRoom = chatroomService.getChatRoom(userDetails);
    return ResponseEntity.ok(ApiResponseBody.okBody(chatRoom));
  }

  @GetMapping("/api/categories/{categoryId}")
  public ResponseEntity<ApiResponseBody<List<ChatRoomDTO>>> getChatRooms(
      @PathVariable Long categoryId) {

    List<ChatRoomDTO> chatRooms = chatroomService.getChatRooms(categoryId);
    return ResponseEntity.ok(ApiResponseBody.okBody(chatRooms));
  }

  @PutMapping("/api/users/chatroom")
  public ResponseEntity<ApiResponse> updateChatRoom(
      @AuthenticationPrincipal UserDetails userDetails,
      @RequestBody @Validated ChatRoomDTO chatRoomDTO) {

    chatroomService.updateChatRoom(userDetails, chatRoomDTO);
    return ResponseEntity.ok(ApiResponse.ok());
  }

  @DeleteMapping("/api/users/chatroom")
  public ResponseEntity<ApiResponse> deleteChatRoom(
      @AuthenticationPrincipal UserDetails userDetails) {

    chatroomService.deleteChatRoom(userDetails);
    return ResponseEntity.ok(ApiResponse.ok());
  }
}