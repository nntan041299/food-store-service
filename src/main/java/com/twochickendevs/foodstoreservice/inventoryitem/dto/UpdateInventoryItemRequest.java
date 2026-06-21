package com.twochickendevs.foodstoreservice.inventoryitem.dto;

import com.twochickendevs.foodstoreservice.inventoryitem.entity.InventoryUnit;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateInventoryItemRequest {

    @Size(max = 150, message = "Item name must not exceed 150 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    private Set<Long> categoryIds;

    @DecimalMin(value = "0.0", message = "Price must not be negative")
    private BigDecimal price;

    private InventoryUnit unit;

    @Min(value = 0, message = "Quantity must not be negative")
    private Integer quantity;

    @Min(value = 0, message = "Low stock threshold must not be negative")
    private Integer lowStockThreshold;

    @Size(max = 255, message = "Image URL must not exceed 255 characters")
    private String imageUrl;

    private Boolean isAvailable;
}
