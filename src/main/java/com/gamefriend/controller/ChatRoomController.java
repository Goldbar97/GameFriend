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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatroomController {

  private final ChatroomService chatroomService;

  @PostMapping("/api/categories/{categoryId}/chatrooms")
  public ResponseEntity<ApiResponseBody<ChatRoomDTO>> createChatRoom(
      @AuthenticationPrincipal UserDetails userDetails, @PathVariable("categoryId") Long categoryId,
      @RequestBody @Validated ChatRoomDTO chatRoomDTO) {

    ChatRoomDTO saved = chatroomService.createChatRoom(userDetails, categoryId, chatRoomDTO);
    return ResponseEntity.ok(ApiResponseBody.okBody(saved));
  }

  @GetMapping("/api/categories/{categoryId}/chatrooms/search")
  public ResponseEntity<ApiResponseBody<List<ChatRoomDTO>>> searchChatrooms(
      @PathVariable("categoryId") Long categoryId, @RequestParam("query") String query) {

    List<ChatRoomDTO> chatrooms = chatroomService.searchChatrooms(categoryId, query);
    return ResponseEntity.ok(ApiResponseBody.okBody(chatrooms));
  }

  @MessageMapping("/app/categories/{categoryId}/chatrooms/{chatRoomId}/enter")
  @SendTo("/app/categories/{categoryId}/chatrooms/{chatRoomId}")
  public String enterChatRoom(@AuthenticationPrincipal UserDetails userDetails,
      @DestinationVariable Long categoryId, @DestinationVariable Long chatRoomId) {

    chatroomService.enterChatRoom(userDetails, categoryId, chatRoomId);

    return userDetails.getUsername() + " has entered the room.";
  }

  @MessageMapping("/app/categories/{categoryId}/chatrooms/{chatRoomId}/leave")
  @SendTo("/app/categories/{categoryId}/chatrooms/{chatRoomId}")
  public String leaveChatRoom(@AuthenticationPrincipal UserDetails userDetails,
      @DestinationVariable Long categoryId, @DestinationVariable Long chatRoomId) {

    chatroomService.leaveChatRoom(userDetails, categoryId, chatRoomId);

    return userDetails.getUsername() + " has left the room.";
  }

  @GetMapping("/api/users/chatroom")
  public ResponseEntity<ApiResponseBody<ChatRoomDTO>> getChatRoom(
      @AuthenticationPrincipal UserDetails userDetails) {

    ChatRoomDTO chatRoom = chatroomService.getChatRoom(userDetails);
    return ResponseEntity.ok(ApiResponseBody.okBody(chatRoom));
  }

  @GetMapping("/api/categories/{categoryId}/chatrooms")
  public ResponseEntity<ApiResponseBody<List<ChatRoomDTO>>> getChatRooms(
      @PathVariable("categoryId") Long categoryId) {

    List<ChatRoomDTO> chatRooms = chatroomService.getChatRooms(categoryId);
    return ResponseEntity.ok(ApiResponseBody.okBody(chatRooms));
  }

  @PutMapping("/api/categories/{categoryId}/chatrooms/{chatroomId}")
  public ResponseEntity<ApiResponse> updateChatRoom(
      @AuthenticationPrincipal UserDetails userDetails, @PathVariable("categoryId") Long categoryId,
      @PathVariable("chatroomId") Long chatroomId, @RequestBody @Validated ChatRoomDTO chatRoomDTO) {

    chatroomService.updateChatRoom(userDetails, categoryId, chatroomId, chatRoomDTO);
    return ResponseEntity.ok(ApiResponse.ok());
  }

  @DeleteMapping("/api/categories/{categoryId}/chatrooms/{chatRoomId}")
  public ResponseEntity<ApiResponse> deleteChatRoom(
      @AuthenticationPrincipal UserDetails userDetails, @PathVariable("categoryId") Long categoryId,
      @PathVariable("chatRoomId") Long chatRoomId) {

    chatroomService.leaveChatRoom(userDetails, categoryId, chatRoomId);
    return ResponseEntity.ok(ApiResponse.ok());
  }
}