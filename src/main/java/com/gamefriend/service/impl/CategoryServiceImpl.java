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

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

  private final CategoryRepository categoryRepository;

  @Override
  public void createCategory(CategoryDTO categoryDTO) {

    CategoryEntity categoryEntity = CategoryEntity.builder()
        .name(categoryDTO.getName())
        .build();

    categoryRepository.save(categoryEntity);
  }

  @Override
  public List<String> getCategories() {

    List<CategoryEntity> categoryEntities = categoryRepository.findAll();

    return categoryEntities.stream()
        .map(CategoryEntity::getName)
        .collect(Collectors.toList());
  }

  @Override
  public void updateCategory(String categoryName, CategoryDTO categoryDTO) {

    CategoryEntity categoryEntity = categoryRepository.findByName(categoryName)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

    categoryEntity.update(categoryDTO.getName());
  }

  @Override
  public void deleteCategory(String categoryName) {

    CategoryEntity categoryEntity = categoryRepository.findByName(categoryName)
        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

    categoryRepository.delete(categoryEntity);
  }
}