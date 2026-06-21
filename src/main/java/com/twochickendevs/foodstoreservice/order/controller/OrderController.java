package com.twochickendevs.foodstoreservice.order.controller;

import com.twochickendevs.foodstoreservice.order.dto.OrderResponse;
import com.twochickendevs.foodstoreservice.order.dto.PlaceOrderRequest;
import com.twochickendevs.foodstoreservice.order.dto.UpdateOrderStatusRequest;
import com.twochickendevs.foodstoreservice.order.entity.OrderStatus;
import com.twochickendevs.foodstoreservice.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shops/{shopId}/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * CUSTOMER — places an order after scanning a QR code.
     * Requires an authenticated customer account; table identity comes from the request body.
     */
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderResponse> placeOrder(
            @PathVariable Long shopId,
            @Valid @RequestBody PlaceOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.placeOrder(shopId, request));
    }

    /** SHOP_OWNER — list all orders for their shop, optionally filtered by status. */
    @GetMapping
    @PreAuthorize("hasRole('SHOP_OWNER')")
    public ResponseEntity<List<OrderResponse>> getOrders(
            @PathVariable Long shopId,
            @RequestParam(required = false) OrderStatus status) {
        return ResponseEntity.ok(orderService.getOrders(shopId, status));
    }

    /** SHOP_OWNER — get a single order. */
    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('SHOP_OWNER')")
    public ResponseEntity<OrderResponse> getOrder(
            @PathVariable Long shopId,
            @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrder(shopId, orderId));
    }

    /** SHOP_OWNER — update order status (CONFIRMED → PREPARING → READY → COMPLETED, or CANCELLED). */
    @PatchMapping("/{orderId}/status")
    @PreAuthorize("hasRole('SHOP_OWNER')")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long shopId,
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        return ResponseEntity.ok(orderService.updateOrderStatus(shopId, orderId, request));
    }
}
