package com.gamefriend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

@AllArgsConstructor
@Builder
@Document(indexName = "category")
@Getter
@NoArgsConstructor
@Setting(settingPath = "/elasticsearch/settings.json")
public class CategoryDocument {

  @Id
  private Long id;

  @Field(type = FieldType.Text, analyzer = "ngram_analyzer", searchAnalyzer = "ngram_analyzer")
  private String name;

  private long rooms;
  private long participants;

  public void updateName(String name) {

    this.name = name;
  }
}