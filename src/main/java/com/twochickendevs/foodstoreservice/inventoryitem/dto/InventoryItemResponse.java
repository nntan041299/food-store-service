package com.twochickendevs.foodstoreservice.inventoryitem.dto;

import com.twochickendevs.foodstoreservice.category.dto.CategoryResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public record InventoryItemResponse(
        Long id,
        Long shopId,
        String name,
        String description,
        Set<CategoryResponse> categories,
        BigDecimal price,
        String unit,
        Integer quantity,
        Integer lowStockThreshold,
        String imageUrl,
        boolean isAvailable,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
