package com.twochickendevs.foodstoreservice.order.dto;

import com.twochickendevs.foodstoreservice.order.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrderStatusRequest {

    @NotNull(message = "status is required")
    private OrderStatus status;
}
