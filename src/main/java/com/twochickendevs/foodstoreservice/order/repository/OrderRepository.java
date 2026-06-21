package com.twochickendevs.foodstoreservice.order.repository;

import com.twochickendevs.foodstoreservice.order.entity.Order;
import com.twochickendevs.foodstoreservice.order.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByShopIdOrderByCreatedAtDesc(Long shopId);

    List<Order> findAllByShopIdAndStatusOrderByCreatedAtDesc(Long shopId, OrderStatus status);

    Optional<Order> findByIdAndShopId(Long id, Long shopId);
}
