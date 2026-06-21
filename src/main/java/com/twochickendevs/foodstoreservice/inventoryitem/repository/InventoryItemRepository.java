package com.twochickendevs.foodstoreservice.inventoryitem.repository;

import com.twochickendevs.foodstoreservice.inventoryitem.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long>,
        JpaSpecificationExecutor<InventoryItem> {

    boolean existsByShopIdAndName(Long shopId, String name);

    Optional<InventoryItem> findByIdAndShopId(Long id, Long shopId);
}
