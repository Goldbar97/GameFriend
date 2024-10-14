package com.gamefriend.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

@AllArgsConstructor
@Builder
@Document(indexName = "chatroom")
@Getter
@NoArgsConstructor
@Setting(settingPath = "/elasticsearch/settings.json")
public class ChatroomDocument {

  @Id
  private Long id;

  @Field(type = FieldType.Keyword)
  private Long userId;

  @Field(type = FieldType.Keyword)
  private Long categoryId;

  @Field(type = FieldType.Text, analyzer = "ngram_analyzer", searchAnalyzer = "ngram_analyzer")
  private String title;

  private String createdBy;
  private Long present;
  private Long capacity;

  @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
  private LocalDateTime createdAt;

  @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
  private LocalDateTime updatedAt;

  public void updatePresent(long present) {

    this.present = present;
  }
}