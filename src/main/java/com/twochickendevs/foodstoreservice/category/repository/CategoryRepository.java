package com.twochickendevs.foodstoreservice.category.repository;

import com.twochickendevs.foodstoreservice.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByShopIdAndName(Long shopId, String name);

    List<Category> findAllByShopIdAndIdIn(Long shopId, Set<Long> ids);
}
