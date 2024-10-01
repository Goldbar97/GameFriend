package com.gamefriend.controller;

import com.gamefriend.dto.ChatroomDTO;
import com.gamefriend.dto.ChatroomUserDTO;
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
  public ResponseEntity<ApiResponseBody<ChatroomDTO>> createChatRoom(
      @AuthenticationPrincipal UserDetails userDetails, @PathVariable("categoryId") Long categoryId,
      @RequestBody @Validated ChatroomDTO chatRoomDTO) {

    ChatroomDTO saved = chatroomService.createChatRoom(userDetails, categoryId, chatRoomDTO);
    return ResponseEntity.ok(ApiResponseBody.okBody(saved));
  }

  @GetMapping("/api/categories/{categoryId}/chatrooms/search")
  public ResponseEntity<ApiResponseBody<List<ChatroomDTO>>> searchChatrooms(
      @PathVariable("categoryId") Long categoryId, @RequestParam("query") String query) {

    List<ChatroomDTO> chatrooms = chatroomService.searchChatrooms(categoryId, query);
    return ResponseEntity.ok(ApiResponseBody.okBody(chatrooms));
  }

  @PostMapping("/api/categories/{categoryId}/chatrooms/{chatroomId}/enter")
  public ResponseEntity<ApiResponse> enterChatroom(@AuthenticationPrincipal UserDetails userDetails,
      @PathVariable("categoryId") Long categoryId, @PathVariable("chatroomId") Long chatroomId) {

    chatroomService.enterChatRoom(userDetails, categoryId, chatroomId);
    return ResponseEntity.ok(ApiResponse.ok());
  }

  @DeleteMapping("/api/categories/{categoryId}/chatrooms/{chatroomId}/leave")
  public ResponseEntity<ApiResponse> leaveChatroom(@AuthenticationPrincipal UserDetails userDetails,
      @PathVariable("categoryId") Long categoryId, @PathVariable("chatroomId") Long chatroomId) {

    chatroomService.leaveChatRoom(userDetails, categoryId, chatroomId);
    return ResponseEntity.ok(ApiResponse.ok());
  }

//  @MessageMapping("/app/categories/{categoryId}/chatrooms/{chatroomId}/enter")
//  @SendTo("/app/categories/{categoryId}/chatrooms/{chatroomId}")
//  public String enterChatRoom(@AuthenticationPrincipal UserDetails userDetails,
//      @DestinationVariable Long categoryId, @DestinationVariable Long chatroomId) {
//
//    chatroomService.enterChatRoom(userDetails, categoryId, chatroomId);
//
//    return userDetails.getUsername() + " has entered the room.";
//  }
//
//  @MessageMapping("/app/categories/{categoryId}/chatrooms/{chatroomId}/leave")
//  @SendTo("/app/categories/{categoryId}/chatrooms/{chatroomId}")
//  public String leaveChatRoom(@AuthenticationPrincipal UserDetails userDetails,
//      @DestinationVariable Long categoryId, @DestinationVariable Long chatroomId) {
//
//    chatroomService.leaveChatRoom(userDetails, categoryId, chatroomId);
//
//    return userDetails.getUsername() + " has left the room.";
//  }

  @GetMapping("/api/categories/{categoryId}/chatrooms/{chatroomId}")
  public ResponseEntity<ApiResponseBody<ChatroomDTO>> getChatroom(
      @PathVariable("categoryId") Long categoryId, @PathVariable("chatroomId") Long chatroomId) {

    ChatroomDTO chatRoom = chatroomService.getChatRoom(categoryId, chatroomId);
    return ResponseEntity.ok(ApiResponseBody.okBody(chatRoom));
  }

  @GetMapping("/api/categories/{categoryId}/chatrooms")
  public ResponseEntity<ApiResponseBody<List<ChatroomDTO>>> getChatrooms(
      @PathVariable("categoryId") Long categoryId) {

    List<ChatroomDTO> chatRooms = chatroomService.getChatRooms(categoryId);
    return ResponseEntity.ok(ApiResponseBody.okBody(chatRooms));
  }

  @GetMapping("/api/categories/{categoryId}/chatrooms/{chatroomId}/users")
  public ResponseEntity<ApiResponseBody<List<ChatroomUserDTO>>> getChatroomUsers(
      @PathVariable("categoryId") Long categoryId, @PathVariable("chatroomId") Long chatroomId) {

    List<ChatroomUserDTO> chatroomUserDTO = chatroomService.getChatroomUsers(categoryId,
        chatroomId);
    return ResponseEntity.ok(ApiResponseBody.okBody(chatroomUserDTO));
  }

  @PutMapping("/api/categories/{categoryId}/chatrooms/{chatroomId}")
  public ResponseEntity<ApiResponse> updateChatRoom(
      @AuthenticationPrincipal UserDetails userDetails, @PathVariable("categoryId") Long categoryId,
      @PathVariable("chatroomId") Long chatroomId,
      @RequestBody @Validated ChatroomDTO chatRoomDTO) {

    chatroomService.updateChatRoom(userDetails, categoryId, chatroomId, chatRoomDTO);
    return ResponseEntity.ok(ApiResponse.ok());
  }

  @DeleteMapping("/api/categories/{categoryId}/chatrooms/{chatroomId}")
  public ResponseEntity<ApiResponse> deleteChatRoom(
      @AuthenticationPrincipal UserDetails userDetails, @PathVariable("categoryId") Long categoryId,
      @PathVariable("chatroomId") Long chatroomId) {

    chatroomService.leaveChatRoom(userDetails, categoryId, chatroomId);
    return ResponseEntity.ok(ApiResponse.ok());
  }
}