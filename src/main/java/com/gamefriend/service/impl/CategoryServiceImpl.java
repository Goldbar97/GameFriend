package com.gamefriend.service.impl;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.gamefriend.dto.CategoryDTO;
import com.gamefriend.dto.CategoryStatsDTO;
import com.gamefriend.entity.CategoryDocument;
import com.gamefriend.entity.CategoryEntity;
import com.gamefriend.exception.CustomException;
import com.gamefriend.exception.ErrorCode;
import com.gamefriend.repository.CategoryDocumentRepository;
import com.gamefriend.repository.CategoryRepository;
import com.gamefriend.service.CategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

  private final CategoryRepository categoryRepository;
  private final CategoryDocumentRepository categoryDocumentRepository;

  @Override
  @Transactional
  public void createCategory(CategoryDTO categoryDTO) {

    if (categoryRepository.existsByName(categoryDTO.getName())) {
      throw new CustomException(ErrorCode.CATEGORY_EXISTS);
    }

    CategoryEntity categoryEntity = CategoryEntity.builder()
        .name(categoryDTO.getName())
        .rooms(0)
        .participants(0)
        .build();

    CategoryEntity saved = categoryRepository.save(categoryEntity);

    CategoryDocument categoryDocument = CategoryDocument.builder()
        .id(saved.getId())
        .name(saved.getName())
        .rooms(0)
        .participants(0)
        .build();

    categoryDocumentRepository.save(categoryDocument);
  }

  @Override
  @Transactional(readOnly = true)
  public List<CategoryDTO> searchCategories(String query) {

    List<CategoryDocument> categoryDocuments = categoryDocumentRepository.findByNameContaining(
        query);

    return categoryDocuments.stream()
        .map(e -> CategoryDTO.builder()
            .id(e.getId())
            .name(e.getName())
            .rooms(e.getRooms())
            .participants(e.getParticipants())
            .build()
        )
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<CategoryDocument> getCategories() {

    return categoryDocumentRepository.findAllByOrderByParticipantsDesc();
  }

  @Override
  @Transactional
  public void updateCategory(Long categoryId, CategoryDTO categoryDTO) {

    CategoryEntity categoryEntity = categoryRepository.findById(categoryId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

    categoryEntity.update(categoryDTO.getName());

    CategoryDocument categoryDocument = categoryDocumentRepository.findById(categoryId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

    categoryDocument.updateName(categoryDTO.getName());
    categoryDocumentRepository.save(categoryDocument);
  }

  @Override
  @Transactional
  public void deleteCategory(Long categoryId) {

    CategoryEntity categoryEntity = categoryRepository.findById(categoryId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

    categoryRepository.delete(categoryEntity);

    CategoryDocument categoryDocument = categoryDocumentRepository.findById(categoryId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

    categoryDocumentRepository.delete(categoryDocument);
  }
}