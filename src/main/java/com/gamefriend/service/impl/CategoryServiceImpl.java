package com.gamefriend.service.impl;

import com.gamefriend.dto.CategoryDTO;
import com.gamefriend.entity.CategoryEntity;
import com.gamefriend.exception.CustomException;
import com.gamefriend.exception.ErrorCode;
import com.gamefriend.repository.CategoryRepository;
import com.gamefriend.service.CategoryService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

  private final CategoryRepository categoryRepository;

  @Override
  @Transactional
  public void createCategory(CategoryDTO categoryDTO) {

    CategoryEntity categoryEntity = CategoryEntity.builder()
        .name(categoryDTO.getName())
        .build();

    categoryRepository.save(categoryEntity);
  }

  @Override
  @Transactional(readOnly = true)
  public List<CategoryDTO> getCategories() {

    List<CategoryEntity> categoryEntities = categoryRepository.findAll();

    return categoryEntities.stream()
        .map(e ->
            CategoryDTO.builder()
                .id(e.getId())
                .name(e.getName())
                .build()
        )
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public void updateCategory(Long categoryId, CategoryDTO categoryDTO) {

    CategoryEntity categoryEntity = categoryRepository.findById(categoryId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

    categoryEntity.update(categoryDTO.getName());
  }

  @Override
  @Transactional
  public void deleteCategory(Long categoryId) {

    CategoryEntity categoryEntity = categoryRepository.findById(categoryId)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

    categoryRepository.delete(categoryEntity);
  }
}