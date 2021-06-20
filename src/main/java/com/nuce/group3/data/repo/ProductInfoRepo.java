package com.nuce.group3.data.repo;

import com.nuce.group3.controller.dto.response.ProductInfoResponseTest;
import com.nuce.group3.data.model.ProductInfo;
import com.nuce.group3.utils.Constant;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductInfoRepo extends JpaRepository<ProductInfo, Integer> {
    Optional<ProductInfo> findProductInfoByNameAndActiveFlag(String name, int activeFlag);
    List<ProductInfo> findProductInfoByActiveFlag(int activeFlag);

    @Query(value = "select p.*" +
            " from product_info p where p.active_flag=1 and (:name is null or p.name like %:name%)" +
            " and (:categoryName is null or p.cate_id  = (select c.id from category c where c.active_flag=1 and c.name like %:categoryName%)) " +
            " and (:qtyTo is null or p.qty <= :qtyTo) and (:qtyFrom is null or p.qty >= :qtyFrom)" +
            " and (:priceFrom is null or p.price_out >= :priceFrom) and (:priceTo is null or p.price_out <= :priceTo)", nativeQuery = true)
    List<ProductInfo> findProductInfoByFilter(@Param(value = "name") String name, @Param(value = "categoryName") String categoryName,
                                              @Param(value = "qtyFrom") Integer qtyFrom, @Param(value = "qtyTo") Integer qtyTo,
                                              @Param(value = "priceFrom") BigDecimal priceFrom, @Param(value = "priceTo") BigDecimal priceTo, Pageable pageable);

    @Cacheable(cacheNames = Constant.CACHE_PRODUCT_INFO_BY_ID)
    Optional<ProductInfo> findProductInfoByIdAndActiveFlag(int id, int activeFlag);

    @Modifying
    @Query("select new com.nuce.group3.controller.dto.response.ProductInfoResponseTest(p.name, p.description) " +
            "from ProductInfo p where p.activeFlag=1")
    List<ProductInfoResponseTest> test();

}
