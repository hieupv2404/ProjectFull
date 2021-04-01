package com.nuce.group3.data.repo;

import com.nuce.group3.data.model.Category;
import com.nuce.group3.data.model.ProductInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductInfoRepo extends JpaRepository<ProductInfo, Integer> {
    Optional<ProductInfo> findProductInfoByNameAndActiveFlag(String name, int activeFlag);
    List<ProductInfo> findProductInfoByActiveFlag(int activeFlag);

    @Query(value = "select p.*" +
            " from product_info p where p.active_flag=1 and (:name is null or p.name like %:name%)" +
            " and (:categoryName is null or p.cate_id  = (select c.id from category c where c.name like %:categoryName%)) " +
            " and (:qtyTo is null or p.qty <= :qtyTo) and (:qtyFrom is null or p.qty >= :qtyFrom)", nativeQuery = true)
    List<ProductInfo> findProductInfoByFilter(@Param(value = "name") String name, @Param(value = "categoryName") String categoryName,
                                           @Param(value = "qtyFrom") int qtyFrom, @Param(value = "qtyTo") int  qtyTo );

    Optional<ProductInfo> findProductInfoByActiveFlagAndId(int activeFlag, int id);

}
