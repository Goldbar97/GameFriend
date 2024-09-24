package com.gamefriend.controller;

import com.gamefriend.dto.ChatRoomDTO;
import com.gamefriend.response.ApiResponse;
import com.gamefriend.response.ApiResponseBody;
import com.gamefriend.service.ChatRoomService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
public class ChatRoomController {

  private final ChatRoomService chatRoomService;

  @PostMapping("/api/categories/{categoryId}")
  public ResponseEntity<ApiResponse> createChatRoom(@AuthenticationPrincipal UserDetails userDetails,
      @PathVariable Long categoryId, @RequestBody @Validated ChatRoomDTO chatRoomDTO) {

    chatRoomService.createChatRoom(userDetails, categoryId, chatRoomDTO);
    return ResponseEntity.ok(ApiResponse.ok());
  }

  @GetMapping("/api/users/chatroom")
  public ResponseEntity<ApiResponseBody<ChatRoomDTO>> getChatRoom(@AuthenticationPrincipal UserDetails userDetails) {

    ChatRoomDTO chatRoom = chatRoomService.getChatRoom(userDetails);
    return ResponseEntity.ok(ApiResponseBody.okBody(chatRoom));
  }

  @GetMapping("/api/categories/{categoryId}")
  public ResponseEntity<ApiResponseBody<List<ChatRoomDTO>>> getChatRooms(@PathVariable Long categoryId) {

    List<ChatRoomDTO> chatRooms = chatRoomService.getChatRooms(categoryId);
    return ResponseEntity.ok(ApiResponseBody.okBody(chatRooms));
  }

  @PutMapping("/api/users/chatroom")
  public ResponseEntity<ApiResponse> updateChatRoom(
      @AuthenticationPrincipal UserDetails userDetails,
      @RequestBody @Validated ChatRoomDTO chatRoomDTO) {

    chatRoomService.updateChatRoom(userDetails, chatRoomDTO);
    return ResponseEntity.ok(ApiResponse.ok());
  }

  @DeleteMapping("/api/users/chatroom")
  public ResponseEntity<ApiResponse> deleteChatRoom(@AuthenticationPrincipal UserDetails userDetails) {

    chatRoomService.deleteChatRoom(userDetails);
    return ResponseEntity.ok(ApiResponse.ok());
  }
}