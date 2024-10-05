package com.gamefriend.service.impl;

import com.gamefriend.component.RedisComponent;
import com.gamefriend.dto.ChatDTO;
import com.gamefriend.dto.MessageDTO;
import com.gamefriend.dto.UserDTO;
import com.gamefriend.entity.ChatroomEntity;
import com.gamefriend.entity.MessageEntity;
import com.gamefriend.entity.UserEntity;
import com.gamefriend.exception.CustomException;
import com.gamefriend.exception.ErrorCode;
import com.gamefriend.repository.ChatroomRepository;
import com.gamefriend.repository.MessageRepository;
import com.gamefriend.repository.UserRepository;
import com.gamefriend.service.MessageService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

  private final ChatroomRepository chatroomRepository;
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final RedisComponent redisComponent;

  @Override
  @Transactional
  public MessageDTO<ChatDTO> sendMessage(Principal principal, Long categoryId, Long chatroomId,
      String message) {

    UserEntity userEntity = userRepository.findByUsername(principal.getName())
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    ChatroomEntity chatroomEntity = chatroomRepository.findByIdAndCategoryId(chatroomId, categoryId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

    MessageEntity messageEntity = MessageEntity.builder()
        .userEntity(userEntity)
        .chatRoomEntity(chatroomEntity)
        .message(message)
        .build();

    MessageEntity saved = messageRepository.save(messageEntity);

    return MessageDTO.<ChatDTO>builder()
        .type("chat")
        .responseBody(ChatDTO.builder()
            .id(userEntity.getId())
            .nickname(userEntity.getNickname())
            .imageUrl(userEntity.getImageUrl())
            .message(saved.getMessage())
            .createdAt(saved.getCreatedAt())
            .build())
        .build();
  }

  @Override
  public MessageDTO<UserDTO> sendUserInfo(UserDTO userDTO) {

    return MessageDTO.<UserDTO>builder()
        .type("user")
        .responseBody(userDTO)
        .build();
  }
}