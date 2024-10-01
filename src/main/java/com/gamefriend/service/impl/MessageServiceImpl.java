package com.gamefriend.service.impl;

import com.gamefriend.dto.MessageDTO;
import com.gamefriend.entity.ChatroomEntity;
import com.gamefriend.entity.MessageEntity;
import com.gamefriend.entity.UserEntity;
import com.gamefriend.exception.CustomException;
import com.gamefriend.exception.ErrorCode;
import com.gamefriend.repository.MessageRepository;
import com.gamefriend.repository.ChatroomRepository;
import com.gamefriend.repository.UserRepository;
import com.gamefriend.service.MessageService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

  private final MessageRepository messageRepository;
  private final ChatroomRepository chatroomRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public MessageDTO sendMessage(Principal principal, Long categoryId, Long chatroomId,
      String message) {

    Authentication authentication = (Authentication) principal;
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();

    UserEntity userEntity = userRepository.findByUsername(userDetails.getUsername())
        .orElseThrow(() -> new CustomException(
            ErrorCode.USER_NOT_FOUND));

    ChatroomEntity chatroomEntity = chatroomRepository.findByIdAndCategoryId(chatroomId, categoryId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

    MessageEntity messageEntity = MessageEntity.builder()
        .userEntity(userEntity)
        .chatRoomEntity(chatroomEntity)
        .message(message)
        .build();

    MessageEntity saved = messageRepository.save(messageEntity);

    return MessageDTO.builder()
        .nickname(userEntity.getNickname())
        .imageUrl(userEntity.getImageUrl())
        .message(saved.getMessage())
        .createdAt(saved.getCreatedAt())
        .build();
  }
}