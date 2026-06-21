package com.twochickendevs.foodstoreservice.category.dto;

import java.time.LocalDateTime;

public record CategoryResponse(
        Long id,
        Long shopId,
        String name,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
