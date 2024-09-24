package com.gamefriend.service;

import com.gamefriend.dto.CategoryDTO;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public interface CategoryService {

  @Transactional
  void createCategory(CategoryDTO categoryDTO);

  @Transactional(readOnly = true)
  List<CategoryDTO> getCategories();

  @Transactional
  void updateCategory(Long categoryId, CategoryDTO categoryDTO);

  @Transactional
  void deleteCategory(Long categoryId);
}