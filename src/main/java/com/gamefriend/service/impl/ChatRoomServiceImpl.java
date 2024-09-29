package com.gamefriend.service.impl;

import com.gamefriend.dto.ChatRoomDTO;
import com.gamefriend.entity.CategoryEntity;
import com.gamefriend.entity.ChatRoomEntity;
import com.gamefriend.entity.ChatRoomUserEntity;
import com.gamefriend.entity.UserEntity;
import com.gamefriend.exception.CustomException;
import com.gamefriend.exception.ErrorCode;
import com.gamefriend.repository.CategoryRepository;
import com.gamefriend.repository.ChatRoomRepository;
import com.gamefriend.repository.ChatRoomUserRepository;
import com.gamefriend.repository.UserRepository;
import com.gamefriend.service.ChatroomService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatroomServiceImpl implements ChatroomService {

  private final CategoryRepository categoryRepository;
  private final ChatRoomUserRepository chatRoomUserRepository;
  private final ChatRoomRepository chatRoomRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public ChatRoomDTO createChatRoom(UserDetails userDetails, Long categoryId, ChatRoomDTO chatRoomDTO) {

    UserEntity userEntity = userRepository.findByUsername(userDetails.getUsername())
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    CategoryEntity categoryEntity = categoryRepository.findById(categoryId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

    ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
        .userEntity(userEntity)
        .categoryEntity(categoryEntity)
        .createdBy(userEntity.getNickname())
        .title(chatRoomDTO.getTitle())
        .capacity(chatRoomDTO.getCapacity())
        .present(0)
        .build();

    ChatRoomEntity saved = chatRoomRepository.save(chatRoomEntity);
    categoryEntity.incrementRooms();

    // TODO: enter own chatroom
    enterChatRoom(userDetails, categoryId, saved.getId());

    return ChatRoomDTO.builder()
        .id(saved.getId())
        .title(saved.getTitle())
        .capacity(saved.getCapacity())
        .createdBy(saved.getCreatedBy())
        .present(saved.getPresent())
        .createdAt(saved.getCreatedAt())
        .updatedAt(saved.getUpdatedAt())
        .build();
  }

  @Override
  public List<ChatRoomDTO> searchChatrooms(Long categoryId, String query) {

    List<ChatRoomEntity> chatRoomEntities = chatRoomRepository.findByCategoryIdAndQuery(categoryId, query);

    return chatRoomEntities.stream()
        .map(e -> ChatRoomDTO.builder()
            .title(e.getTitle())
            .capacity(e.getCapacity())
            .createdBy(e.getUserEntity().getUsername())
            .present(e.getPresent())
            .createdAt(e.getCreatedAt())
            .updatedAt(e.getUpdatedAt())
            .build())
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void enterChatRoom(UserDetails userDetails, Long categoryId, Long chatRoomId) {

    UserEntity userEntity = userRepository.findByUsername(userDetails.getUsername())
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    CategoryEntity categoryEntity = categoryRepository.findById(categoryId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

    ChatRoomEntity chatRoomEntity = chatRoomRepository.findById(chatRoomId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

    if (chatRoomEntity.getPresent() == chatRoomEntity.getCapacity()) {
      throw new CustomException(ErrorCode.CHATROOM_FULL);
    }

    ChatRoomUserEntity chatRoomUserEntity = ChatRoomUserEntity.builder()
        .userEntity(userEntity)
        .chatRoomEntity(chatRoomEntity)
        .build();

    chatRoomUserRepository.save(chatRoomUserEntity);
    categoryEntity.incrementParticipants();
    chatRoomEntity.incrementPresent();
  }

  @Override
  @Transactional
  public void leaveChatRoom(UserDetails userDetails, Long categoryId, Long chatRoomId) {

    UserEntity userEntity = userRepository.findByUsername(userDetails.getUsername())
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    CategoryEntity categoryEntity = categoryRepository.findById(categoryId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

    ChatRoomEntity chatRoomEntity = chatRoomRepository.findById(chatRoomId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

    ChatRoomUserEntity chatRoomUserEntity = chatRoomUserRepository.findByUserEntity(
        userEntity).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

    chatRoomEntity.decrementPresent();
    categoryEntity.decrementParticipants();
    chatRoomUserRepository.delete(chatRoomUserEntity);

    if (chatRoomEntity.getUserEntity().equals(userEntity)) {
      List<ChatRoomUserEntity> chatRoomUserEntities = chatRoomUserRepository.findAllByChatRoomEntity(
          chatRoomEntity);

      chatRoomUserRepository.deleteAll(chatRoomUserEntities);
      chatRoomRepository.delete(chatRoomEntity);
      categoryEntity.decrementRooms();
      categoryEntity.decrementParticipants(chatRoomUserEntities.size());
    } else if (chatRoomEntity.getPresent() == 0) {
      chatRoomRepository.delete(chatRoomEntity);
      categoryEntity.decrementRooms();
    }
  }

  @Override
  @Transactional(readOnly = true)
  public ChatRoomDTO getChatRoom(UserDetails userDetails) {

    UserEntity userEntity = userRepository.findByUsername(userDetails.getUsername())
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    ChatRoomEntity chatRoomEntity = chatRoomRepository.findByUserEntity(userEntity)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

    return ChatRoomDTO.builder()
        .title(chatRoomEntity.getTitle())
        .capacity(chatRoomEntity.getCapacity())
        .createdBy(userEntity.getUsername())
        .present(chatRoomEntity.getPresent())
        .createdAt(chatRoomEntity.getCreatedAt())
        .updatedAt(chatRoomEntity.getUpdatedAt())
        .build();
  }

  @Override
  @Transactional(readOnly = true)
  public List<ChatRoomDTO> getChatRooms(Long categoryId) {

    CategoryEntity categoryEntity = categoryRepository.findById(categoryId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

    List<ChatRoomEntity> chatRoomEntities = chatRoomRepository.findAllByCategoryEntity(
        categoryEntity);

    return chatRoomEntities.stream()
        .map(e ->
            ChatRoomDTO.builder()
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
  @Transactional
  public void updateChatRoom(UserDetails userDetails, Long categoryId, Long chatroomId,
      ChatRoomDTO chatRoomDTO) {

    UserEntity userEntity = userRepository.findByUsername(userDetails.getUsername())
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    ChatRoomEntity chatRoomEntity = chatRoomRepository.findByUserEntity(userEntity)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

    chatRoomEntity.update(chatRoomDTO);
  }
}