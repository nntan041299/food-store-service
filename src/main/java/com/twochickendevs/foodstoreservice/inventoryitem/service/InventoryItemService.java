package com.twochickendevs.foodstoreservice.inventoryitem.service;

import com.twochickendevs.foodstoreservice.auth.entity.User;
import com.twochickendevs.foodstoreservice.auth.repository.UserRepository;
import com.twochickendevs.foodstoreservice.category.entity.Category;
import com.twochickendevs.foodstoreservice.category.repository.CategoryRepository;
import com.twochickendevs.foodstoreservice.common.exception.ResourceNotFoundException;
import com.twochickendevs.foodstoreservice.inventoryitem.dto.CreateInventoryItemRequest;
import com.twochickendevs.foodstoreservice.inventoryitem.dto.InventoryItemResponse;
import com.twochickendevs.foodstoreservice.inventoryitem.dto.UpdateInventoryItemRequest;
import com.twochickendevs.foodstoreservice.inventoryitem.entity.InventoryItem;
import com.twochickendevs.foodstoreservice.inventoryitem.entity.InventoryUnit;
import com.twochickendevs.foodstoreservice.inventoryitem.mapper.InventoryItemMapper;
import com.twochickendevs.foodstoreservice.inventoryitem.repository.InventoryItemRepository;
import com.twochickendevs.foodstoreservice.shop.entity.Shop;
import com.twochickendevs.foodstoreservice.shop.repository.ShopRepository;
import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class InventoryItemService {

    private final InventoryItemRepository inventoryItemRepository;
    private final ShopRepository shopRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryItemMapper inventoryItemMapper;

    @Transactional
    public InventoryItemResponse createInventoryItem(Long shopId, CreateInventoryItemRequest request) {
        User owner = getAuthenticatedUser();

        Shop shop = shopRepository.findByIdAndOwnerId(shopId, owner.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found: " + shopId));

        if (inventoryItemRepository.existsByShopIdAndName(shopId, request.getName())) {
            throw new IllegalArgumentException("Item name already exists in this shop: " + request.getName());
        }

        Set<Category> categories = resolveCategories(shopId, request.getCategoryIds());

        InventoryItem inventoryItem = InventoryItem.builder()
                .shop(shop)
                .name(request.getName())
                .description(request.getDescription())
                .categories(categories)
                .price(request.getPrice())
                .unit(request.getUnit())
                .quantity(request.getQuantity())
                .lowStockThreshold(request.getLowStockThreshold())
                .imageUrl(request.getImageUrl())
                .isAvailable(true)
                .build();

        return inventoryItemMapper.toResponse(inventoryItemRepository.save(inventoryItem));
    }

    @Transactional
    public InventoryItemResponse updateInventoryItem(Long shopId, Long itemId, UpdateInventoryItemRequest request) {
        User owner = getAuthenticatedUser();

        shopRepository.findByIdAndOwnerId(shopId, owner.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found: " + shopId));

        InventoryItem item = inventoryItemRepository.findByIdAndShopId(itemId, shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found: " + itemId));

        if (StringUtils.hasText(request.getName()) && !request.getName().equals(item.getName())) {
            if (inventoryItemRepository.existsByShopIdAndName(shopId, request.getName())) {
                throw new IllegalArgumentException("Item name already exists in this shop: " + request.getName());
            }
            item.setName(request.getName());
        }

        if (request.getDescription() != null) {
            item.setDescription(request.getDescription());
        }
        if (request.getCategoryIds() != null) {
            item.setCategories(resolveCategories(shopId, request.getCategoryIds()));
        }
        if (request.getPrice() != null) {
            item.setPrice(request.getPrice());
        }
        if (request.getUnit() != null) {
            item.setUnit(request.getUnit());
        }
        if (request.getQuantity() != null) {
            item.setQuantity(request.getQuantity());
        }
        if (request.getLowStockThreshold() != null) {
            item.setLowStockThreshold(request.getLowStockThreshold());
        }
        if (request.getImageUrl() != null) {
            item.setImageUrl(request.getImageUrl());
        }
        if (request.getIsAvailable() != null) {
            item.setAvailable(request.getIsAvailable());
        }

        return inventoryItemMapper.toResponse(item);
    }

    @Transactional(readOnly = true)
    public List<InventoryItemResponse> searchInventoryItems(Long shopId, String name, InventoryUnit unit,
                                                            List<Long> categoryIds) {
        if (!shopRepository.existsById(shopId)) {
            throw new ResourceNotFoundException("Shop not found: " + shopId);
        }

        Specification<InventoryItem> spec = Specification
                .<InventoryItem>where((root, query, cb) -> cb.equal(root.get("shop").get("id"), shopId))
                .and((root, query, cb) -> cb.isTrue(root.get("isAvailable")));

        if (StringUtils.hasText(name)) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }
        if (unit != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("unit"), unit));
        }
        if (!CollectionUtils.isEmpty(categoryIds)) {
            spec = spec.and((root, query, cb) -> {
                query.distinct(true);
                return root.join("categories", JoinType.INNER).get("id").in(categoryIds);
            });
        }

        return inventoryItemRepository.findAll(spec, Sort.by("name"))
                .stream()
                .map(inventoryItemMapper::toResponse)
                .toList();
    }

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    private Set<Category> resolveCategories(Long shopId, Set<Long> categoryIds) {
        if (CollectionUtils.isEmpty(categoryIds)) {
            return new HashSet<>();
        }
        List<Category> found = categoryRepository.findAllByShopIdAndIdIn(shopId, categoryIds);
        if (found.size() != categoryIds.size()) {
            throw new IllegalArgumentException("One or more category IDs are invalid or do not belong to this shop");
        }
        return new HashSet<>(found);
    }
}
