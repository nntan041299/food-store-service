package com.twochickendevs.foodstoreservice.order.dto;

import com.twochickendevs.foodstoreservice.order.entity.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private Long shopId;
    private Long customerId;
    private String customerUsername;
    private String tableNumber;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private String note;
    private List<OrderItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
