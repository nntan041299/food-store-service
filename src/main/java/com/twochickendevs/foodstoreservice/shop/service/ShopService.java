package com.twochickendevs.foodstoreservice.shop.service;

import com.twochickendevs.foodstoreservice.auth.entity.User;
import com.twochickendevs.foodstoreservice.auth.repository.UserRepository;
import com.twochickendevs.foodstoreservice.shop.dto.CreateShopRequest;
import com.twochickendevs.foodstoreservice.shop.dto.ShopResponse;
import com.twochickendevs.foodstoreservice.shop.entity.Shop;
import com.twochickendevs.foodstoreservice.shop.mapper.ShopMapper;
import com.twochickendevs.foodstoreservice.shop.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
