package com.gamefriend.service;

import com.gamefriend.dto.ChatroomDTO;
import com.gamefriend.dto.UserDTO;
import java.util.List;
import org.springframework.security.core.userdetails.UserDetails;

public interface ChatroomService {

  ChatroomDTO createChatRoom(UserDetails userDetails, Long categoryId, ChatroomDTO chatRoomDTO);

  ChatroomDTO getChatRoom(Long categoryId, Long chatroomId);

  List<ChatroomDTO> getChatRooms(Long categoryId);

  List<ChatroomDTO> searchChatrooms(Long categoryId, String query);

  List<UserDTO> getChatroomUsers(Long categoryId, Long chatroomId);

  void enterChatRoom(UserDetails userDetails, Long categoryId, Long chatroomId);

  void leaveChatRoom(UserDetails userDetails, Long categoryId, Long chatroomId);

  void updateChatRoom(UserDetails userDetails, Long categoryId, Long chatroomId,
      ChatroomDTO chatRoomDTO);

  void checkUser(UserDetails userDetails, Long categoryId, Long chatroomId);
}