package com.gamefriend.service.impl;

import com.gamefriend.dto.ChatRoomDTO;
import com.gamefriend.entity.CategoryEntity;
import com.gamefriend.entity.ChatRoomEntity;
import com.gamefriend.entity.UserEntity;
import com.gamefriend.exception.CustomException;
import com.gamefriend.exception.ErrorCode;
import com.gamefriend.repository.CategoryRepository;
import com.gamefriend.repository.ChatRoomRepository;
import com.gamefriend.repository.UserRepository;
import com.gamefriend.service.ChatRoomService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

  private final CategoryRepository categoryRepository;
  private final ChatRoomRepository chatRoomRepository;
  private final UserRepository userRepository;

  @Override
  public void createChatRoom(UserDetails userDetails, Long categoryId, ChatRoomDTO chatRoomDTO) {

    UserEntity userEntity = userRepository.findByEmail(userDetails.getUsername())
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    CategoryEntity categoryEntity = categoryRepository.findById(categoryId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

    ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
        .userEntity(userEntity)
        .categoryEntity(categoryEntity)
        .title(chatRoomDTO.getTitle())
        .capacity(chatRoomDTO.getCapacity())
        .present(0)
        .build();

    chatRoomRepository.save(chatRoomEntity);

    // TODO: enter own chatroom
  }

  @Override
  public ChatRoomDTO getChatRoom(UserDetails userDetails) {

    UserEntity userEntity = userRepository.findByEmail(userDetails.getUsername())
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
  public List<ChatRoomDTO> getChatRooms(Long categoryId) {

    CategoryEntity categoryEntity = categoryRepository.findById(categoryId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

    List<ChatRoomEntity> chatRoomEntities = chatRoomRepository.findAllByCategoryEntity(
        categoryEntity);

    return chatRoomEntities.stream()
        .map(e ->
            ChatRoomDTO.builder()
                .title(e.getTitle())
                .capacity(e.getCapacity())
                .createdBy(e.getUserEntity().getUsername())
                .present(e.getPresent())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build()
        ).collect(Collectors.toList());
  }

  @Override
  public void updateChatRoom(UserDetails userDetails, ChatRoomDTO chatRoomDTO) {

    UserEntity userEntity = userRepository.findByEmail(userDetails.getUsername())
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    ChatRoomEntity chatRoomEntity = chatRoomRepository.findByUserEntity(userEntity)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

    chatRoomEntity.update(chatRoomDTO);
  }

  @Override
  public void deleteChatRoom(UserDetails userDetails) {

    UserEntity userEntity = userRepository.findByEmail(userDetails.getUsername())
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    ChatRoomEntity chatRoomEntity = chatRoomRepository.findByUserEntity(userEntity)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

    // TODO: kick all chatters out

    chatRoomRepository.delete(chatRoomEntity);
  }
}