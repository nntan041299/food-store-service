package com.twochickendevs.foodstoreservice.shop.repository;

import com.twochickendevs.foodstoreservice.shop.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long> {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    Optional<Shop> findByIdAndOwnerId(Long id, Long ownerId);

    List<Shop> findAllByOwnerId(Long ownerId);
}
