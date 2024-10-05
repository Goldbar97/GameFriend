package com.gamefriend.service;

import com.gamefriend.dto.CategoryDTO;
import com.gamefriend.dto.CategoryStatsDTO;
import java.util.List;

public interface CategoryService {

  void createCategory(CategoryDTO categoryDTO);

  List<CategoryDTO> searchCategories(String query);

  List<CategoryStatsDTO> getCategories();

  void updateCategory(Long categoryId, CategoryDTO categoryDTO);

  void deleteCategory(Long categoryId);
}