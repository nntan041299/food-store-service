package com.twochickendevs.foodstoreservice.shop.service;

import com.twochickendevs.foodstoreservice.auth.entity.User;
import com.twochickendevs.foodstoreservice.auth.repository.UserRepository;
import com.twochickendevs.foodstoreservice.shop.dto.CreateShopRequest;
import com.twochickendevs.foodstoreservice.shop.dto.ShopResponse;
import com.twochickendevs.foodstoreservice.shop.dto.UpdateShopRequest;
import com.twochickendevs.foodstoreservice.shop.entity.Shop;
import com.twochickendevs.foodstoreservice.shop.mapper.ShopMapper;
import com.twochickendevs.foodstoreservice.shop.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;
    private final UserRepository userRepository;
    private final ShopMapper shopMapper;

    @Transactional
    public ShopResponse createShop(CreateShopRequest request) {
        if (shopRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Shop name already exists: " + request.getName());
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Shop shop = Shop.builder()
                .name(request.getName())
                .description(request.getDescription())
                .address(request.getAddress())
                .phoneNumber(request.getPhoneNumber())
                .isActive(true)
                .owner(owner)
                .build();

        return shopMapper.toResponse(shopRepository.save(shop));
    }

    @Transactional
    public ShopResponse updateShop(Long shopId, UpdateShopRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Shop shop = shopRepository.findByIdAndOwnerId(shopId, owner.getId())
                .orElseThrow(() -> new IllegalArgumentException("Shop not found: " + shopId));

        if (StringUtils.hasText(request.getName()) && !request.getName().equals(shop.getName())) {
            if (shopRepository.existsByNameAndIdNot(request.getName(), shopId)) {
                throw new IllegalArgumentException("Shop name already exists: " + request.getName());
            }
            shop.setName(request.getName());
        }

        if (request.getDescription() != null) {
            shop.setDescription(request.getDescription());
        }

        if (request.getAddress() != null) {
            shop.setAddress(request.getAddress());
        }

        if (request.getPhoneNumber() != null) {
            shop.setPhoneNumber(request.getPhoneNumber());
        }

        return shopMapper.toResponse(shopRepository.save(shop));
    }
}
