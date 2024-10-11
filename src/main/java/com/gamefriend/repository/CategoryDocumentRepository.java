package com.gamefriend.repository;

import com.gamefriend.entity.CategoryDocument;
import java.util.List;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CategoryDocumentRepository extends ElasticsearchRepository<CategoryDocument, Long> {

  List<CategoryDocument> findByNameContaining(String name);

  List<CategoryDocument> findAllByOrderByParticipantsDesc();
}