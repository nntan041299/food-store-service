package com.twochickendevs.foodstoreservice.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class PlaceOrderRequest {

    @NotBlank(message = "tableNumber is required")
    private String tableNumber;

    private String note;

    @NotEmpty(message = "items must not be empty")
    @Valid
    private List<OrderItemRequest> items;
}
