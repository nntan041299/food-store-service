package com.twochickendevs.foodstoreservice.order.service;

import com.twochickendevs.foodstoreservice.common.exception.InsufficientStockException;
import com.twochickendevs.foodstoreservice.common.exception.ResourceNotFoundException;
import com.twochickendevs.foodstoreservice.inventoryitem.entity.InventoryItem;
import com.twochickendevs.foodstoreservice.inventoryitem.repository.InventoryItemRepository;
import com.twochickendevs.foodstoreservice.order.dto.OrderItemRequest;
import com.twochickendevs.foodstoreservice.order.dto.OrderResponse;
import com.twochickendevs.foodstoreservice.order.dto.PlaceOrderRequest;
import com.twochickendevs.foodstoreservice.order.dto.UpdateOrderStatusRequest;
import com.twochickendevs.foodstoreservice.order.entity.Order;
import com.twochickendevs.foodstoreservice.order.entity.OrderItem;
import com.twochickendevs.foodstoreservice.order.entity.OrderStatus;
import com.twochickendevs.foodstoreservice.order.mapper.OrderMapper;
import com.twochickendevs.foodstoreservice.order.repository.OrderRepository;
import com.twochickendevs.foodstoreservice.shop.repository.ShopRepository;
import com.twochickendevs.foodstoreservice.shop.entity.Shop;
import com.twochickendevs.foodstoreservice.auth.repository.UserRepository;
import com.twochickendevs.foodstoreservice.auth.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ShopRepository shopRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;

    /**
     * CUSTOMER — called after scanning a QR code; requires an authenticated customer account.
     * Validates availability and stock, deducts quantities, and persists the order atomically.
     */
    @Transactional
    public OrderResponse placeOrder(Long shopId, PlaceOrderRequest request) {
        User customer = getAuthenticatedUser();

        Shop shop = shopRepository.findById(shopId)
                .filter(Shop::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found: " + shopId));

        // Collect unique item IDs from request (merge duplicate entries)
        Map<Long, Integer> quantityByItemId = request.getItems().stream()
                .collect(Collectors.toMap(
                        OrderItemRequest::getInventoryItemId,
                        OrderItemRequest::getQuantity,
                        Integer::sum
                ));

        Set<Long> itemIds = quantityByItemId.keySet();
        // Use a locking query (SELECT ... FOR UPDATE, ordered by id) to prevent
        // concurrent orders from creating a race condition on shared inventory quantities.
        List<InventoryItem> inventoryItems = inventoryItemRepository.findAllByIdForUpdate(itemIds);

        // Validate all requested items exist and belong to this shop
        if (inventoryItems.size() != itemIds.size()) {
            throw new ResourceNotFoundException("One or more items not found");
        }
        for (InventoryItem item : inventoryItems) {
            if (!item.getShop().getId().equals(shopId)) {
                throw new ResourceNotFoundException("Item " + item.getId() + " does not belong to this shop");
            }
            if (!item.isAvailable()) {
                throw new IllegalArgumentException("Item '" + item.getName() + "' is currently unavailable");
            }
        }

        // Validate stock and build order items
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (InventoryItem inventoryItem : inventoryItems) {
            int requested = quantityByItemId.get(inventoryItem.getId());
            if (inventoryItem.getQuantity() < requested) {
                throw new InsufficientStockException(
                        "Insufficient stock for '" + inventoryItem.getName() + "': available=" +
                        inventoryItem.getQuantity() + ", requested=" + requested);
            }

            BigDecimal unitPrice = inventoryItem.getPrice();
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(requested));
            total = total.add(subtotal);

            // Deduct stock
            inventoryItem.setQuantity(inventoryItem.getQuantity() - requested);

            orderItems.add(OrderItem.builder()
                    .inventoryItem(inventoryItem)
                    .itemName(inventoryItem.getName())
                    .quantity(requested)
                    .unitPrice(unitPrice)
                    .subtotal(subtotal)
                    .build());
        }

        Order order = Order.builder()
                .shop(shop)
                .customer(customer)
                .tableNumber(request.getTableNumber())
                .note(request.getNote())
                .totalAmount(total)
                .status(OrderStatus.PENDING)
                .build();

        // Link items to the order
        for (OrderItem item : orderItems) {
            item.setOrder(order);
        }
        order.setItems(orderItems);

        return orderMapper.toResponse(orderRepository.save(order));
    }

    /** SHOP_OWNER — list orders for their shop, optionally filtered by status. */
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrders(Long shopId, OrderStatus status) {
        validateShopOwnership(shopId);

        List<Order> orders = (status != null)
                ? orderRepository.findAllByShopIdAndStatusOrderByCreatedAtDesc(shopId, status)
                : orderRepository.findAllByShopIdOrderByCreatedAtDesc(shopId);

        return orders.stream().map(orderMapper::toResponse).toList();
    }

    /** SHOP_OWNER — get a single order. */
    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long shopId, Long orderId) {
        validateShopOwnership(shopId);

        Order order = orderRepository.findByIdAndShopId(orderId, shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        return orderMapper.toResponse(order);
    }

    /** SHOP_OWNER — advance or cancel an order. */
    @Transactional
    public OrderResponse updateOrderStatus(Long shopId, Long orderId, UpdateOrderStatusRequest request) {
        validateShopOwnership(shopId);

        Order order = orderRepository.findByIdAndShopId(orderId, shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        if (order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalArgumentException(
                    "Cannot update status of a " + order.getStatus().name().toLowerCase() + " order");
        }

        // If cancelling, restore inventory stock
        if (request.getStatus() == OrderStatus.CANCELLED && order.getStatus() != OrderStatus.CANCELLED) {
            for (OrderItem item : order.getItems()) {
                InventoryItem inv = item.getInventoryItem();
                inv.setQuantity(inv.getQuantity() + item.getQuantity());
            }
        }

        order.setStatus(request.getStatus());
        return orderMapper.toResponse(order);
    }

    private void validateShopOwnership(Long shopId) {
        User owner = getAuthenticatedUser();

        shopRepository.findByIdAndOwnerId(shopId, owner.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found: " + shopId));
    }

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
