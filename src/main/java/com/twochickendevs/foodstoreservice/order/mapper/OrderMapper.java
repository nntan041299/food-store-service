package com.twochickendevs.foodstoreservice.order.mapper;

import com.twochickendevs.foodstoreservice.order.dto.OrderItemResponse;
import com.twochickendevs.foodstoreservice.order.dto.OrderResponse;
import com.twochickendevs.foodstoreservice.order.entity.Order;
import com.twochickendevs.foodstoreservice.order.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "shop.id", target = "shopId")
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "customer.username", target = "customerUsername")
    OrderResponse toResponse(Order order);

    @Mapping(source = "inventoryItem.id", target = "inventoryItemId")
    OrderItemResponse toItemResponse(OrderItem orderItem);
}
