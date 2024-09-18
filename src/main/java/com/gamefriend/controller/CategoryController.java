package com.gamefriend.controller;

import com.gamefriend.dto.CategoryDTO;
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

  @GetMapping("/api/categories")
  public ResponseEntity<ApiResponseBody<List<String>>> getCategories() {

    List<String> categories = categoryService.getCategories();
    return ResponseEntity.ok(ApiResponseBody.okBody(categories));
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/api/categories/{categoryName}")
  public void updateCategory(@PathVariable String categoryName,
      @Validated CategoryDTO categoryDTO) {

    categoryService.updateCategory(categoryName, categoryDTO);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/api/categories/{categoryName}")
  public void deleteCategory(@PathVariable String categoryName) {

    categoryService.deleteCategory(categoryName);
  }
}