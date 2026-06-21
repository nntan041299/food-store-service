package com.twochickendevs.foodstoreservice.inventoryitem.controller;

import com.twochickendevs.foodstoreservice.inventoryitem.dto.CreateInventoryItemRequest;
import com.twochickendevs.foodstoreservice.inventoryitem.dto.InventoryItemResponse;
import com.twochickendevs.foodstoreservice.inventoryitem.dto.UpdateInventoryItemRequest;
import com.twochickendevs.foodstoreservice.inventoryitem.service.InventoryItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/shops/{shopId}/inventory-items")
@RequiredArgsConstructor
public class InventoryItemController {

    private final InventoryItemService inventoryItemService;

    @PostMapping
    @PreAuthorize("hasRole('SHOP_OWNER')")
    public ResponseEntity<InventoryItemResponse> createInventoryItem(@PathVariable Long shopId,
                                                                     @Valid @RequestBody CreateInventoryItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryItemService.createInventoryItem(shopId, request));
    }

    @PatchMapping("/{itemId}")
    @PreAuthorize("hasRole('SHOP_OWNER')")
    public ResponseEntity<InventoryItemResponse> updateInventoryItem(@PathVariable Long shopId,
                                                                     @PathVariable Long itemId,
                                                                     @Valid @RequestBody UpdateInventoryItemRequest request) {
        return ResponseEntity.ok(inventoryItemService.updateInventoryItem(shopId, itemId, request));
    }
}
