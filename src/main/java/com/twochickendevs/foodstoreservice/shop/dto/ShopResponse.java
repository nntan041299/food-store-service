package com.twochickendevs.foodstoreservice.shop.dto;

import java.time.LocalDateTime;

public record ShopResponse(
        Long id,
        String name,
        String description,
        String address,
        String phoneNumber,
        boolean isActive,
        Long ownerId,
        String ownerUsername,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
