package com.gamefriend.controller;

import com.gamefriend.dto.ChatroomDTO;
import com.gamefriend.dto.ChatroomDetailsDTO;
import com.gamefriend.response.ApiResponse;
import com.gamefriend.response.ApiResponseBody;
import com.gamefriend.service.ChatroomService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
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

  @PutMapping("/api/categories/{categoryId}/chatrooms/{chatroomId}")
  public ResponseEntity<ApiResponse> updateChatRoom(
      @AuthenticationPrincipal UserDetails userDetails, @PathVariable("categoryId") Long categoryId,
      @PathVariable("chatroomId") Long chatroomId,
      @RequestBody @Validated ChatroomDTO chatRoomDTO) {

    chatroomService.updateChatRoom(userDetails, categoryId, chatroomId, chatRoomDTO);
    return ResponseEntity.ok(ApiResponse.ok());
  }

  @GetMapping("/api/categories/{categoryId}/chatrooms/{chatroomId}/details")
  public ResponseEntity<ApiResponseBody<ChatroomDetailsDTO>> getChatroomDetails(
      @AuthenticationPrincipal UserDetails userDetails, @PathVariable("categoryId") Long categoryId,
      @PathVariable("chatroomId") Long chatroomId) {

    ChatroomDetailsDTO chatroomDetails = chatroomService.getChatroomDetails(userDetails, categoryId,
        chatroomId);
    return ResponseEntity.ok(ApiResponseBody.okBody(chatroomDetails));
  }
}