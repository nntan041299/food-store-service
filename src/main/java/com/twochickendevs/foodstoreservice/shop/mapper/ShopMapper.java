package com.twochickendevs.foodstoreservice.shop.mapper;

import com.twochickendevs.foodstoreservice.shop.dto.ShopResponse;
import com.twochickendevs.foodstoreservice.shop.entity.Shop;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ShopMapper {

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "owner.username", target = "ownerUsername")
    ShopResponse toResponse(Shop shop);
}
