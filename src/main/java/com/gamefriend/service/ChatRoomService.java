package com.gamefriend.service;

import com.gamefriend.dto.ChatRoomDTO;
import java.util.List;
import org.springframework.security.core.userdetails.UserDetails;

public interface ChatroomService {

  void createChatRoom(UserDetails userDetails, Long categoryId, ChatRoomDTO chatRoomDTO);

  void enterChatRoom(UserDetails userDetails, Long chatRoomId);

  void leaveChatRoom(UserDetails userDetails, Long chatRoomId);

  ChatRoomDTO getChatRoom(UserDetails userDetails);

  List<ChatRoomDTO> getChatRooms(Long categoryId);

  void updateChatRoom(UserDetails userDetails, ChatRoomDTO chatRoomDTO);

  void deleteChatRoom(UserDetails userDetails);
}