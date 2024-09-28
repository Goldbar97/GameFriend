package com.gamefriend.service;

import com.gamefriend.dto.CategoryDTO;
import java.util.List;

public interface CategoryService {

  void createCategory(CategoryDTO categoryDTO);

  List<CategoryDTO> searchCategories(String query);

  List<CategoryDTO> getCategories();

  void updateCategory(Long categoryId, CategoryDTO categoryDTO);

  void deleteCategory(Long categoryId);
}