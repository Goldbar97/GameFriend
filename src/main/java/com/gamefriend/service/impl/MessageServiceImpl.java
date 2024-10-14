package com.gamefriend.service.impl;

import com.gamefriend.component.RedisComponent;
import com.gamefriend.dto.ChatDTO;
import com.gamefriend.dto.MessageDTO;
import com.gamefriend.dto.UserDTO;
import com.gamefriend.entity.CategoryDocument;
import com.gamefriend.entity.ChatEntity;
import com.gamefriend.entity.ChatroomDocument;
import com.gamefriend.entity.ChatroomEntity;
import com.gamefriend.entity.ChatroomUserEntity;
import com.gamefriend.entity.UserEntity;
import com.gamefriend.exception.CustomException;
import com.gamefriend.exception.ErrorCode;
import com.gamefriend.repository.CategoryDocumentRepository;
import com.gamefriend.repository.ChatroomDocumentRepository;
import com.gamefriend.repository.ChatroomRepository;
import com.gamefriend.repository.ChatroomUserRepository;
import com.gamefriend.repository.MessageRepository;
import com.gamefriend.repository.UserRepository;
import com.gamefriend.service.MessageService;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

  private final CategoryDocumentRepository categoryDocumentRepository;
  private final ChatroomDocumentRepository chatroomDocumentRepository;
  private final ChatroomRepository chatroomRepository;
  private final ChatroomUserRepository chatroomUserRepository;
  private final MessageRepository messageRepository;
  private final RedisComponent redisComponent;
  private final SimpMessagingTemplate simpMessagingTemplate;
  private final UserRepository userRepository;

  private final Map<String, Principal> sessionPrincipals = new ConcurrentHashMap<>();

  @Override
  @Transactional
  public MessageDTO<ChatDTO> sendMessage(Principal principal, Long categoryId, Long chatroomId,
      String message) {

    UserEntity userEntity = userRepository.findByUsername(principal.getName())
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    ChatroomEntity chatroomEntity = chatroomRepository.findByIdAndCategoryId(chatroomId, categoryId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

    ChatEntity chatEntity = ChatEntity.builder()
        .userEntity(userEntity)
        .chatroomEntity(chatroomEntity)
        .message(message)
        .build();

    ChatEntity saved = messageRepository.save(chatEntity);

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
  public MessageDTO<UserDTO> sendUserInfo(Principal principal, String sessionId) {

    sessionPrincipals.put(sessionId, principal);
    UserDTO userDTO = redisComponent.getUserDTO(principal.getName());

    if (userDTO == null) {
      UserEntity userEntity = userRepository.findByUsername(principal.getName())
          .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

      userDTO = UserDTO.builder()
          .id(userEntity.getId())
          .nickname(userEntity.getNickname())
          .imageUrl(userEntity.getImageUrl())
          .build();

      redisComponent.saveUserDTO(principal.getName(), userDTO);
    }

    return MessageDTO.<UserDTO>builder()
        .type("user")
        .responseBody(userDTO)
        .build();
  }

  @Override
  public MessageDTO<UserDTO> sendLeaveInfo(Principal principal) {

    UserDTO userDTO = redisComponent.getUserDTO(principal.getName());

    if (userDTO == null) {
      UserEntity userEntity = userRepository.findByUsername(principal.getName())
          .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

      userDTO = UserDTO.builder()
          .id(userEntity.getId())
          .nickname(userEntity.getNickname())
          .imageUrl(userEntity.getImageUrl())
          .build();

      redisComponent.saveUserDTO(principal.getName(), userDTO);
    }

    return MessageDTO.<UserDTO>builder()
        .type("leave")
        .responseBody(userDTO)
        .build();
  }

  @Override
  @Transactional
  public void handleUserDisconnection(String sessionId) {

    Principal principal = sessionPrincipals.get(sessionId);
    sessionPrincipals.remove(sessionId);
    UserEntity userEntity = userRepository.findByUsername(principal.getName())
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    Optional<ChatroomUserEntity> optionalChatroomUserEntity = chatroomUserRepository.findByUserEntity(
        userEntity);

    if (optionalChatroomUserEntity.isEmpty()) {
      return;
    }
    ChatroomUserEntity chatroomUserEntity = optionalChatroomUserEntity.get();

    ChatroomEntity chatroomEntity = chatroomRepository.findByChatroomUserIdWithLock(
        chatroomUserEntity.getId()).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

    ChatroomDocument chatroomDocument = chatroomDocumentRepository.findById(chatroomEntity.getId())
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

    Long chatroomId = chatroomDocument.getId();
    Long categoryId = chatroomDocument.getCategoryId();

    CategoryDocument categoryDocument = categoryDocumentRepository.findById(categoryId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

    chatroomUserRepository.delete(chatroomUserEntity);
    chatroomEntity.update(chatroomEntity.getPresent() - 1);
    chatroomDocument.updatePresent(chatroomDocument.getPresent() - 1);
    chatroomDocumentRepository.save(chatroomDocument);
    categoryDocument.updateParticipants(categoryDocument.getParticipants() - 1);
    categoryDocumentRepository.save(categoryDocument);

    // 채팅방 구독자에게 퇴장 메시지 전송
    simpMessagingTemplate.convertAndSend(
        String.format("/topic/categories/%s/chatrooms/%s", categoryId, chatroomId),
        sendLeaveInfo(principal)
    );

    // 나간 사람이 채팅방 소유자일 때
    // 전부 퇴장
    if (chatroomEntity.getUserEntity().equals(userEntity)) {
      simpMessagingTemplate.convertAndSend(
          String.format("/topic/categories/%s/chatrooms/%s", categoryId, chatroomId),
          MessageDTO.<String>builder()
              .type("delete")
              .responseBody("The creator has left the room.")
              .build()
      );
      List<ChatroomUserEntity> chatroomUserEntities = chatroomUserRepository.findAllByChatroomEntity(
          chatroomEntity);
      chatroomUserRepository.deleteAll(chatroomUserEntities);
      chatroomRepository.delete(chatroomEntity);
      categoryDocument.updateRooms(categoryDocument.getRooms() - 1);
      categoryDocument.updateParticipants(
          categoryDocument.getParticipants() - chatroomUserEntities.size());
      categoryDocumentRepository.save(categoryDocument);
      chatroomDocumentRepository.delete(chatroomDocument);
    }
  }
}