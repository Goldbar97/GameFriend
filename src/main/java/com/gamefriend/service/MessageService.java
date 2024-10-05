package com.gamefriend.service;

import com.gamefriend.dto.ChatDTO;
import com.gamefriend.dto.MessageDTO;
import com.gamefriend.dto.UserDTO;
import java.security.Principal;

public interface MessageService {

  MessageDTO<ChatDTO> sendMessage(Principal principal, Long categoryId, Long chatroomId, String message);

  MessageDTO<UserDTO> sendUserInfo(UserDTO userDTO);
}