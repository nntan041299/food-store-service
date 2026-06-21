package com.twochickendevs.foodstoreservice.category.service;

import com.twochickendevs.foodstoreservice.auth.entity.User;
import com.twochickendevs.foodstoreservice.auth.repository.UserRepository;
import com.twochickendevs.foodstoreservice.category.dto.CategoryResponse;
import com.twochickendevs.foodstoreservice.category.dto.CreateCategoryRequest;
import com.twochickendevs.foodstoreservice.category.dto.UpdateCategoryRequest;
import com.twochickendevs.foodstoreservice.category.entity.Category;
import com.twochickendevs.foodstoreservice.category.mapper.CategoryMapper;
import com.twochickendevs.foodstoreservice.category.repository.CategoryRepository;
import com.twochickendevs.foodstoreservice.shop.entity.Shop;
import com.twochickendevs.foodstoreservice.shop.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ShopRepository shopRepository;
    private final UserRepository userRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    public CategoryResponse createCategory(Long shopId, CreateCategoryRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Shop shop = shopRepository.findByIdAndOwnerId(shopId, owner.getId())
                .orElseThrow(() -> new IllegalArgumentException("Shop not found: " + shopId));

        if (categoryRepository.existsByShopIdAndName(shopId, request.getName())) {
            throw new IllegalArgumentException("Category already exists in this shop: " + request.getName());
        }

        Category category = Category.builder()
                .shop(shop)
                .name(request.getName())
                .build();

        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse updateCategory(Long shopId, Long categoryId, UpdateCategoryRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        shopRepository.findByIdAndOwnerId(shopId, owner.getId())
                .orElseThrow(() -> new IllegalArgumentException("Shop not found: " + shopId));

        Category category = categoryRepository.findById(categoryId)
                .filter(c -> c.getShop().getId().equals(shopId))
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + categoryId));

        if (categoryRepository.existsByShopIdAndNameAndIdNot(shopId, request.getName(), categoryId)) {
            throw new IllegalArgumentException("Category already exists in this shop: " + request.getName());
        }

        category.setName(request.getName());

        return categoryMapper.toResponse(categoryRepository.save(category));
    }
}
