package com.nuce.group3.data.repo;

import com.nuce.group3.data.model.ProductInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductInfoRepo extends JpaRepository<ProductInfo, Integer> {
    Optional<ProductInfo> findProductInfoByNameAndActiveFlag(String name, int activeFlag);
}
