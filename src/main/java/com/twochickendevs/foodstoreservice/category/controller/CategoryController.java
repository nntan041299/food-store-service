package com.twochickendevs.foodstoreservice.category.controller;

import com.twochickendevs.foodstoreservice.category.dto.CategoryResponse;
import com.twochickendevs.foodstoreservice.category.dto.CreateCategoryRequest;
import com.twochickendevs.foodstoreservice.category.dto.UpdateCategoryRequest;
import com.twochickendevs.foodstoreservice.category.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/shops/{shopId}/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasRole('SHOP_OWNER')")
    public ResponseEntity<CategoryResponse> createCategory(@PathVariable Long shopId,
                                                           @Valid @RequestBody CreateCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(shopId, request));
    }

    @PutMapping("/{categoryId}")
    @PreAuthorize("hasRole('SHOP_OWNER')")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long shopId,
                                                           @PathVariable Long categoryId,
                                                           @Valid @RequestBody UpdateCategoryRequest request) {
        return ResponseEntity.ok(categoryService.updateCategory(shopId, categoryId, request));
    }
}
