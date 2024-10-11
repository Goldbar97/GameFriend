package com.gamefriend.repository;

import com.gamefriend.entity.ChatroomDocument;
import java.util.List;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ChatroomDocumentRepository extends ElasticsearchRepository<ChatroomDocument, Long> {

  List<ChatroomDocument> findByCategoryIdAndTitleContaining(long categoryId, String title);

  List<ChatroomDocument> findByCategoryId(long categoryId);
}