package com.gamefriend.service;

import com.gamefriend.dto.CategoryDTO;
import com.gamefriend.dto.CategoryStatsDTO;
import com.gamefriend.entity.CategoryDocument;
import java.util.List;

public interface CategoryService {

  void createCategory(CategoryDTO categoryDTO);

  List<CategoryDTO> searchCategories(String query);

  List<CategoryDocument> getCategories();

  void updateCategory(Long categoryId, CategoryDTO categoryDTO);

  void deleteCategory(Long categoryId);
}