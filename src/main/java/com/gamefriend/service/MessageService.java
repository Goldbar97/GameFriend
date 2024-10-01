package com.gamefriend.service;

import com.gamefriend.dto.MessageDTO;
import java.security.Principal;

public interface MessageService {

  MessageDTO sendMessage(Principal principal, Long categoryId, Long chatroomId, String message);
}