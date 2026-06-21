package com.twochickendevs.foodstoreservice.inventoryitem.mapper;

import com.twochickendevs.foodstoreservice.category.mapper.CategoryMapper;
import com.twochickendevs.foodstoreservice.inventoryitem.dto.InventoryItemResponse;
import com.twochickendevs.foodstoreservice.inventoryitem.entity.InventoryItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = CategoryMapper.class)
public interface InventoryItemMapper {

    @Mapping(source = "shop.id", target = "shopId")
    InventoryItemResponse toResponse(InventoryItem inventoryItem);
}
