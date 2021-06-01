package com.nuce.group3.data.repo;

import com.nuce.group3.data.model.ProductDetail;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductDetailRepo extends JpaRepository<ProductDetail, Integer> {
    Optional<ProductDetail> findProductDetailByImeiAndActiveFlag(String imei, int activeFlag);

    List<ProductDetail> findProductDetailByActiveFlag(int activeFlag, Pageable pageable);

    @Query(value = "select p.*" +
            " from product_detail p where p.active_flag=1 and (:name is null or p.product_id  in (select pi.id from product_info pi where pi.name like %:name%))" +
            " and (:supplierName is null or p.supplier_id  in (select s.id from supplier s where s.name like %:supplierName%)) " +
            " and (:imei is null or p.imei like %:imei%)", nativeQuery = true)
    List<ProductDetail> findProductDetailByFilter(@Param(value = "name") String name, @Param(value = "supplierName") String supplierName, @Param(value = "imei") String imei, Pageable pageable);

    Optional<ProductDetail> findProductDetailByIdAndActiveFlag(int id, int activeFlag);

    @Query(value = "select p.*" +
            " from product_detail p where p.active_flag=1 and (:productStatusListId is null or p.product_status_list_id = :productStatusListId)", nativeQuery = true)
    List<ProductDetail> findProductDetailsByProductStatusList(int productStatusListId);

    Optional<ProductDetail> findProductDetailByImeiAndStatusAndActiveFlag(String imei, String stauts, int activeFlag);

}
