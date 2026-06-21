package com.twochickendevs.foodstoreservice.inventoryitem.repository;

import com.twochickendevs.foodstoreservice.inventoryitem.entity.InventoryItem;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long>,
        JpaSpecificationExecutor<InventoryItem> {

    boolean existsByShopIdAndName(Long shopId, String name);

    Optional<InventoryItem> findByIdAndShopId(Long id, Long shopId);

    /**
     * Fetches inventory items with a PESSIMISTIC_WRITE lock (SELECT ... FOR UPDATE).
     * Ordered by id to guarantee a consistent lock acquisition order across transactions,
     * which prevents deadlocks when two orders race on overlapping item sets.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM InventoryItem i WHERE i.id IN :ids ORDER BY i.id")
    List<InventoryItem> findAllByIdForUpdate(@Param("ids") Collection<Long> ids);
}
