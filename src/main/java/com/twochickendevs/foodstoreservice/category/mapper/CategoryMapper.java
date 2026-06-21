package com.twochickendevs.foodstoreservice.category.mapper;

import com.twochickendevs.foodstoreservice.category.dto.CategoryResponse;
import com.twochickendevs.foodstoreservice.category.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(source = "shop.id", target = "shopId")
    CategoryResponse toResponse(Category category);
}
