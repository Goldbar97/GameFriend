package com.gamefriend.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
public class ChatroomDetailsDTO {

  private Long id;
  private String title;
  private String entranceMessage;
  private List<UserDTO> users;
  private List<ChatDTO> chats;
}