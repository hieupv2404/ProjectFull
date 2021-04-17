package com.nuce.group3.data.repo;

import com.nuce.group3.controller.dto.response.ProductDetailResponse;
import com.nuce.group3.data.model.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductDetailRepo extends JpaRepository<ProductDetail, Integer> {
    Optional<ProductDetail> findProductDetailByImeiAndActiveFlag(String imei, int activeFlag);

    List<ProductDetail> findProductDetailByActiveFlag(int activeFlag);

    @Query(value = "select p.*" +
            " from product_detail p where p.active_flag=1 and (:name is null or p.product_id  = (select pi.id from product_info pi where pi.name like %:name%))" +
            " and (:supplierName is null or p.supplier_id  = (select s.id from supplier s where s.name like %:supplierName%)) " +
            " and (:imei is null or p.imei like %:imei%)", nativeQuery = true)
    List<ProductDetail> findProductDetailByFilter(@Param(value = "name") String name, @Param(value = "supplierName") String supplierName, @Param(value = "imei") String imei);

    Optional<ProductDetail> findProductDetailByIdAndActiveFlag(int id, int activeFlag);

//    @Modifying
//    @Query("select new com.nuce.group3.controller.dto.response.ProductDetailResponseTest(p.name, p.description) " +
//            "from ProductDetail p where p.activeFlag=1")
//    List<ProductDetailResponseTest> test();

}
