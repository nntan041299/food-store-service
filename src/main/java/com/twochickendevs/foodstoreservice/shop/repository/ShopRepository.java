package com.twochickendevs.foodstoreservice.shop.repository;

import com.twochickendevs.foodstoreservice.shop.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShopRepository extends JpaRepository<Shop, Long> {

    boolean existsByName(String name);

    List<Shop> findAllByOwnerId(Long ownerId);
}
