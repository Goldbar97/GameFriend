package com.gamefriend.service.impl;

import com.gamefriend.dto.ChatroomDTO;
import com.gamefriend.dto.MessageDTO;
import com.gamefriend.dto.UserDTO;
import com.gamefriend.entity.CategoryEntity;
import com.gamefriend.entity.ChatroomEntity;
import com.gamefriend.entity.ChatroomUserEntity;
import com.gamefriend.entity.UserEntity;
import com.gamefriend.exception.CustomException;
import com.gamefriend.exception.ErrorCode;
import com.gamefriend.repository.CategoryRepository;
import com.gamefriend.repository.ChatroomRepository;
import com.gamefriend.repository.ChatroomUserRepository;
import com.gamefriend.repository.UserRepository;
import com.gamefriend.service.ChatroomService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatroomServiceImpl implements ChatroomService {

  private final CategoryRepository categoryRepository;
  private final ChatroomUserRepository chatRoomUserRepository;
  private final ChatroomRepository chatRoomRepository;
  private final UserRepository userRepository;
  private final SimpMessagingTemplate simpMessagingTemplate;

  @Override
  @Transactional
  public ChatroomDTO createChatRoom(UserDetails userDetails, Long categoryId,
      ChatroomDTO chatRoomDTO) {

    UserEntity userEntity = userRepository.findByUsername(userDetails.getUsername())
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    if (chatRoomRepository.existsByUserEntity(userEntity)) {
      throw new CustomException(ErrorCode.CHATROOM_EXISTS);
    }

    CategoryEntity categoryEntity = categoryRepository.findById(categoryId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

    ChatroomEntity chatRoomEntity = ChatroomEntity.builder()
        .userEntity(userEntity)
        .categoryEntity(categoryEntity)
        .title(chatRoomDTO.getTitle())
        .entranceMessage(chatRoomDTO.getEntranceMessage())
        .createdBy(userEntity.getNickname())
        .capacity(chatRoomDTO.getCapacity())
        .present(0)
        .build();

    ChatroomEntity saved = chatRoomRepository.save(chatRoomEntity);

    // TODO: enter own chatroom
    enterChatRoom(userDetails, categoryId, saved.getId());

    return ChatroomDTO.builder()
        .id(saved.getId())
        .title(saved.getTitle())
        .capacity(saved.getCapacity())
        .entranceMessage(saved.getEntranceMessage())
        .createdBy(saved.getCreatedBy())
        .present(saved.getPresent())
        .createdAt(saved.getCreatedAt())
        .updatedAt(saved.getUpdatedAt())
        .build();
  }

  @Override
  @Transactional(readOnly = true)
  public ChatroomDTO getChatRoom(Long categoryId, Long chatroomId) {

    ChatroomEntity chatRoomEntity = chatRoomRepository.findByIdAndCategoryId(chatroomId, categoryId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

    return ChatroomDTO.builder()
        .id(chatRoomEntity.getId())
        .title(chatRoomEntity.getTitle())
        .capacity(chatRoomEntity.getCapacity())
        .entranceMessage(chatRoomEntity.getEntranceMessage())
        .createdBy(chatRoomEntity.getCreatedBy())
        .present(chatRoomEntity.getPresent())
        .createdAt(chatRoomEntity.getCreatedAt())
        .updatedAt(chatRoomEntity.getUpdatedAt())
        .build();
  }

  @Override
  @Transactional(readOnly = true)
  public List<ChatroomDTO> getChatRooms(Long categoryId) {

    CategoryEntity categoryEntity = categoryRepository.findById(categoryId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

    List<ChatroomEntity> chatRoomEntities = chatRoomRepository.findAllByCategoryEntity(
        categoryEntity);

    return chatRoomEntities.stream()
        .map(e ->
            ChatroomDTO.builder()
                .id(e.getId())
                .title(e.getTitle())
                .capacity(e.getCapacity())
                .createdBy(e.getCreatedBy())
                .present(e.getPresent())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build()
        ).collect(Collectors.toList());
  }

  @Override
  public List<ChatroomDTO> searchChatrooms(Long categoryId, String query) {

    List<ChatroomEntity> chatRoomEntities = chatRoomRepository.findByCategoryIdAndQuery(categoryId,
        query);

    return chatRoomEntities.stream()
        .map(e -> ChatroomDTO.builder()
            .title(e.getTitle())
            .capacity(e.getCapacity())
            .entranceMessage(e.getEntranceMessage())
            .createdBy(e.getUserEntity().getUsername())
            .present(e.getPresent())
            .createdAt(e.getCreatedAt())
            .updatedAt(e.getUpdatedAt())
            .build())
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserDTO> getChatroomUsers(Long categoryId, Long chatroomId) {

    ChatroomEntity chatroomEntity = chatRoomRepository.findByIdAndCategoryId(chatroomId, categoryId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

    List<ChatroomUserEntity> chatroomUserEntities = chatRoomUserRepository.findAllByChatRoomEntity(
        chatroomEntity);

    return chatroomUserEntities.stream()
        .map(e -> UserDTO.builder()
            .nickname(e.getUserEntity().getNickname())
            .imageUrl(e.getUserEntity().getImageUrl())
            .build())
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void enterChatRoom(UserDetails userDetails, Long categoryId, Long chatroomId) {

    UserEntity userEntity = userRepository.findByUsername(userDetails.getUsername())
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    CategoryEntity categoryEntity = categoryRepository.findById(categoryId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

    ChatroomEntity chatRoomEntity = chatRoomRepository.findById(chatroomId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

    if (chatRoomEntity.getPresent() == chatRoomEntity.getCapacity()) {
      throw new CustomException(ErrorCode.CHATROOM_FULL);
    }

    ChatroomUserEntity chatRoomUserEntity = ChatroomUserEntity.builder()
        .userEntity(userEntity)
        .chatRoomEntity(chatRoomEntity)
        .build();

    chatRoomUserRepository.save(chatRoomUserEntity);
  }

  @Override
  @Transactional
  public void leaveChatRoom(UserDetails userDetails, Long categoryId, Long chatroomId) {

    UserEntity userEntity = userRepository.findByUsername(userDetails.getUsername())
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    ChatroomEntity chatRoomEntity = chatRoomRepository.findByIdAndCategoryId(chatroomId, categoryId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

    ChatroomUserEntity chatRoomUserEntity = chatRoomUserRepository.findByUserEntity(
        userEntity).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

    chatRoomUserRepository.delete(chatRoomUserEntity);

    if (chatRoomEntity.getUserEntity().equals(userEntity)) {
      simpMessagingTemplate.convertAndSend(
          String.format("/topic/categories/%s/chatrooms/%s", categoryId, chatroomId),
          MessageDTO.<String>builder()
              .type("delete")
              .responseBody("The creator has left the room.")
              .build()
      );
      List<ChatroomUserEntity> chatRoomUserEntities = chatRoomUserRepository.findAllByChatRoomEntity(
          chatRoomEntity);

      chatRoomUserRepository.deleteAll(chatRoomUserEntities);
      chatRoomRepository.delete(chatRoomEntity);
    } else if (chatRoomEntity.getPresent() == 0) {
      chatRoomRepository.delete(chatRoomEntity);
    }
  }

  @Override
  @Transactional
  public void updateChatRoom(UserDetails userDetails, Long categoryId, Long chatroomId,
      ChatroomDTO chatRoomDTO) {

    UserEntity userEntity = userRepository.findByUsername(userDetails.getUsername())
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    ChatroomEntity chatRoomEntity = chatRoomRepository.findByUserEntity(userEntity)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

    chatRoomEntity.update(chatRoomDTO);
  }

  @Override
  @Transactional(readOnly = true)
  public void checkUser(UserDetails userDetails, Long categoryId, Long chatroomId) {

    UserEntity userEntity = userRepository.findByUsername(userDetails.getUsername())
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    ChatroomEntity chatroomEntity = chatRoomRepository.findByIdAndCategoryId(chatroomId,
        categoryId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

    if (!chatRoomUserRepository.existsByUserEntityAndChatRoomEntity(userEntity, chatroomEntity)) {
      throw new CustomException(ErrorCode.WRONG_CHATROOM_USER);
    }
  }
}