package com.gamefriend.service.impl;

import com.gamefriend.dto.ChatDTO;
import com.gamefriend.dto.ChatroomDTO;
import com.gamefriend.dto.MessageDTO;
import com.gamefriend.dto.UserDTO;
import com.gamefriend.entity.CategoryEntity;
import com.gamefriend.entity.ChatEntity;
import com.gamefriend.entity.ChatroomEntity;
import com.gamefriend.entity.ChatroomUserEntity;
import com.gamefriend.entity.UserEntity;
import com.gamefriend.exception.CustomException;
import com.gamefriend.exception.ErrorCode;
import com.gamefriend.repository.CategoryRepository;
import com.gamefriend.repository.ChatRepository;
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
  private final ChatRepository chatRepository;
  private final ChatroomRepository chatroomRepository;
  private final ChatroomUserRepository chatroomUserRepository;
  private final SimpMessagingTemplate simpMessagingTemplate;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public ChatroomDTO createChatRoom(UserDetails userDetails, Long categoryId,
      ChatroomDTO chatRoomDTO) {

    UserEntity userEntity = userRepository.findByUsername(userDetails.getUsername())
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    if (chatroomRepository.existsByUserEntity(userEntity)) {
      throw new CustomException(ErrorCode.CHATROOM_EXISTS);
    }

    CategoryEntity categoryEntity = categoryRepository.findById(categoryId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

    ChatroomEntity chatroomEntity = ChatroomEntity.builder()
        .userEntity(userEntity)
        .categoryEntity(categoryEntity)
        .title(chatRoomDTO.getTitle())
        .entranceMessage(chatRoomDTO.getEntranceMessage())
        .createdBy(userEntity.getNickname())
        .capacity(chatRoomDTO.getCapacity())
        .present(0)
        .build();

    ChatroomEntity saved = chatroomRepository.save(chatroomEntity);

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

    ChatroomEntity chatroomEntity = chatroomRepository.findByIdAndCategoryId(chatroomId, categoryId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

    return ChatroomDTO.builder()
        .id(chatroomEntity.getId())
        .title(chatroomEntity.getTitle())
        .capacity(chatroomEntity.getCapacity())
        .entranceMessage(chatroomEntity.getEntranceMessage())
        .createdBy(chatroomEntity.getCreatedBy())
        .present(chatroomEntity.getPresent())
        .createdAt(chatroomEntity.getCreatedAt())
        .updatedAt(chatroomEntity.getUpdatedAt())
        .build();
  }

  @Override
  @Transactional(readOnly = true)
  public List<ChatroomDTO> getChatRooms(Long categoryId) {

    CategoryEntity categoryEntity = categoryRepository.findById(categoryId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

    List<ChatroomEntity> chatRoomEntities = chatroomRepository.findAllByCategoryEntity(
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

    List<ChatroomEntity> chatRoomEntities = chatroomRepository.findByCategoryIdAndQuery(categoryId,
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

    ChatroomEntity chatroomEntity = chatroomRepository.findByIdAndCategoryId(chatroomId, categoryId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

    List<ChatroomUserEntity> chatroomUserEntities = chatroomUserRepository.findAllByChatroomEntity(
        chatroomEntity);

    return chatroomUserEntities.stream()
        .map(e -> UserDTO.builder()
            .id(e.getUserEntity().getId())
            .nickname(e.getUserEntity().getNickname())
            .imageUrl(e.getUserEntity().getImageUrl())
            .build())
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public synchronized void enterChatRoom(UserDetails userDetails, Long categoryId, Long chatroomId) {

    UserEntity userEntity = userRepository.findByUsername(userDetails.getUsername())
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    ChatroomEntity chatroomEntity = chatroomRepository.findById(chatroomId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

    if (chatroomEntity.getPresent() == chatroomEntity.getCapacity()) {
      throw new CustomException(ErrorCode.CHATROOM_FULL);
    }

    ChatroomUserEntity chatRoomUserEntity = ChatroomUserEntity.builder()
        .userEntity(userEntity)
        .chatroomEntity(chatroomEntity)
        .build();

    chatroomUserRepository.save(chatRoomUserEntity);
    chatroomEntity.update(chatroomUserRepository.countByChatroomEntity(chatroomEntity));
  }



  @Override
  @Transactional
  public void updateChatRoom(UserDetails userDetails, Long categoryId, Long chatroomId,
      ChatroomDTO chatRoomDTO) {

    UserEntity userEntity = userRepository.findByUsername(userDetails.getUsername())
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    ChatroomEntity chatroomEntity = chatroomRepository.findByUserEntity(userEntity)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

    if (chatRoomDTO.getCapacity() < chatroomEntity.getPresent()) {
      throw new CustomException(ErrorCode.CHATROOM_FULL);
    }

    chatroomEntity.update(chatRoomDTO);
  }

  @Override
  @Transactional(readOnly = true)
  public void checkUser(UserDetails userDetails, Long categoryId, Long chatroomId) {

    UserEntity userEntity = userRepository.findByUsername(userDetails.getUsername())
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    ChatroomEntity chatroomEntity = chatroomRepository.findByIdAndCategoryId(chatroomId,
        categoryId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

    if (!chatroomUserRepository.existsByUserEntityAndChatroomEntity(userEntity, chatroomEntity)) {
      throw new CustomException(ErrorCode.WRONG_CHATROOM_USER);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public List<ChatDTO> getChat(Long categoryId, Long chatroomId) {

    ChatroomEntity chatroomEntity = chatroomRepository.findByIdAndCategoryId(chatroomId, categoryId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

    List<ChatEntity> chatEntities = chatRepository.findAllByChatroomEntity(chatroomEntity);

    return chatEntities.stream()
        .map(e -> ChatDTO.builder()
            .id(e.getId())
            .nickname(e.getUserEntity().getNickname())
            .imageUrl(e.getUserEntity().getImageUrl())
            .message(e.getMessage())
            .createdAt(e.getCreatedAt())
            .build()
        ).collect(Collectors.toList());
  }
}