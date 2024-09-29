package com.gamefriend.service;

import com.gamefriend.dto.ChatRoomDTO;
import java.util.List;
import org.springframework.security.core.userdetails.UserDetails;

public interface ChatroomService {

  ChatRoomDTO createChatRoom(UserDetails userDetails, Long categoryId, ChatRoomDTO chatRoomDTO);

  List<ChatRoomDTO> searchChatrooms(Long categoryId, String query);

  void enterChatRoom(UserDetails userDetails, Long categoryId, Long chatRoomId);

  void leaveChatRoom(UserDetails userDetails, Long categoryId, Long chatRoomId);

  ChatRoomDTO getChatRoom(UserDetails userDetails);

  List<ChatRoomDTO> getChatRooms(Long categoryId);

  void updateChatRoom(UserDetails userDetails, Long categoryId, Long chatroomId,
      ChatRoomDTO chatRoomDTO);
}