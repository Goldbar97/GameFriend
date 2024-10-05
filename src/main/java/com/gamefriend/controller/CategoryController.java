package com.gamefriend.controller;

import com.gamefriend.dto.CategoryDTO;
import com.gamefriend.dto.CategoryStatsDTO;
import com.gamefriend.response.ApiResponse;
import com.gamefriend.response.ApiResponseBody;
import com.gamefriend.service.CategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CategoryController {

  private final CategoryService categoryService;

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/api/categories")
  public ResponseEntity<ApiResponse> createCategory(
      @RequestBody @Validated CategoryDTO categoryDTO) {

    categoryService.createCategory(categoryDTO);

    return ResponseEntity.ok(ApiResponse.ok());
  }

  @GetMapping("/api/categories/search")
  public ResponseEntity<ApiResponseBody<List<CategoryDTO>>> searchCategories(
      @RequestParam("query") String query) {

    List<CategoryDTO> categories = categoryService.searchCategories(query);
    return ResponseEntity.ok(ApiResponseBody.okBody(categories));
  }

  @GetMapping("/api/categories")
  public ResponseEntity<ApiResponseBody<List<CategoryStatsDTO>>> getCategories() {

    List<CategoryStatsDTO> categories = categoryService.getCategories();
    return ResponseEntity.ok(ApiResponseBody.okBody(categories));
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/api/categories/{categoryId}")
  public ResponseEntity<ApiResponse> updateCategory(@PathVariable Long categoryId,
      @Validated CategoryDTO categoryDTO) {

    categoryService.updateCategory(categoryId, categoryDTO);
    return ResponseEntity.ok(ApiResponse.ok());
  }

  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/api/categories/{categoryId}")
  public ResponseEntity<ApiResponse> deleteCategory(@PathVariable Long categoryId) {

    categoryService.deleteCategory(categoryId);
    return ResponseEntity.ok(ApiResponse.ok());
  }
}