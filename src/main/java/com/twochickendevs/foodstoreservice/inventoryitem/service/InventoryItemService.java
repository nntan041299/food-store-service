package com.twochickendevs.foodstoreservice.inventoryitem.service;

import com.twochickendevs.foodstoreservice.auth.entity.User;
import com.twochickendevs.foodstoreservice.auth.repository.UserRepository;
import com.twochickendevs.foodstoreservice.category.entity.Category;
import com.twochickendevs.foodstoreservice.category.repository.CategoryRepository;
import com.twochickendevs.foodstoreservice.inventoryitem.dto.CreateInventoryItemRequest;
import com.twochickendevs.foodstoreservice.inventoryitem.dto.InventoryItemResponse;
import com.twochickendevs.foodstoreservice.inventoryitem.entity.InventoryItem;
import com.twochickendevs.foodstoreservice.inventoryitem.mapper.InventoryItemMapper;
import com.twochickendevs.foodstoreservice.inventoryitem.repository.InventoryItemRepository;
import com.twochickendevs.foodstoreservice.shop.entity.Shop;
import com.twochickendevs.foodstoreservice.shop.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
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
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Shop shop = shopRepository.findByIdAndOwnerId(shopId, owner.getId())
                .orElseThrow(() -> new IllegalArgumentException("Shop not found: " + shopId));

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
