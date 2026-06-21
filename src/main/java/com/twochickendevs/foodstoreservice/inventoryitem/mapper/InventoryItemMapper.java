package com.twochickendevs.foodstoreservice.inventoryitem.mapper;

import com.twochickendevs.foodstoreservice.category.dto.CategoryResponse;
import com.twochickendevs.foodstoreservice.category.entity.Category;
import com.twochickendevs.foodstoreservice.inventoryitem.dto.InventoryItemResponse;
import com.twochickendevs.foodstoreservice.inventoryitem.entity.InventoryItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InventoryItemMapper {

    @Mapping(source = "shop.id", target = "shopId")
    InventoryItemResponse toResponse(InventoryItem inventoryItem);

    default CategoryResponse categoryToResponse(Category category) {
        if (category == null) return null;
        return new CategoryResponse(category.getId(), category.getName());
    }
}
