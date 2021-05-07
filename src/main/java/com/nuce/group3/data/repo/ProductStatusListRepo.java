package com.nuce.group3.data.repo;

import com.nuce.group3.data.model.ProductStatusList;
import com.nuce.group3.data.model.ProductStatusList;
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
public interface ProductStatusListRepo extends JpaRepository<ProductStatusList, Integer> {
    Optional<ProductStatusList> findProductStatusListByCodeAndActiveFlag(String code, int activeFlag);
    List<ProductStatusList> findProductStatusListByActiveFlag(int activeFlag, Pageable pageable);

    @Query(value = "select p.*" +
            " from product_status_list p where p.active_flag=1 and (:code is null or p.code like %:code%)" +
            " and (:vatCode is null or p.vat_id  in (select v.id from vat v where v.code like %:vatCode%)) " +
            " and (:priceFrom is null or p.price >= :priceFrom) and (:priceTo is null or p.price <= :priceTo)" +
            " and (:type is null or p.type = :type)", nativeQuery = true)
    List<ProductStatusList> findProductStatusListByFilter(@Param(value = "code") String code, @Param(value = "vatCode") String vatCode,
                                              @Param(value = "priceFrom")BigDecimal priceFrom, @Param(value = "priceTo") BigDecimal priceTo, int type, Pageable pageable);

    @Cacheable(cacheNames = Constant.CACHE_PRODUCT_STATUS_LIST_BY_ID)
    Optional<ProductStatusList> findProductStatusListByIdAndActiveFlag(int id, int activeFlag);


}