package com.nuce.group3.data.repo;

import com.nuce.group3.data.model.ProductDetail;
import com.nuce.group3.data.model.Shelf;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShelfRepo  extends JpaRepository<Shelf, Integer> {
    String CACHE_FIND_SHELF_BY_ID = "findShelfById";

    @Cacheable(cacheNames = "findProductInfoById")
    Optional<Shelf> findShelfByIdAndActiveFlag(int shelfId, int activeFlag);
}
