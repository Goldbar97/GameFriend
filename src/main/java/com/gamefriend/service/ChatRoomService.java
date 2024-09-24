package com.gamefriend.service;

import com.gamefriend.dto.ChatRoomDTO;
import java.util.List;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

public interface ChatRoomService {

  @Transactional
  void createChatRoom(UserDetails userDetails, Long categoryId, ChatRoomDTO chatRoomDTO);

  @Transactional(readOnly = true)
  ChatRoomDTO getChatRoom(UserDetails userDetails);

  @Transactional(readOnly = true)
  List<ChatRoomDTO> getChatRooms(Long categoryId);

  @Transactional
  void updateChatRoom(UserDetails userDetails, ChatRoomDTO chatRoomDTO);

  @Transactional
  void deleteChatRoom(UserDetails userDetails);
}