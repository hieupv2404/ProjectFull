package com.nuce.group3.data.repo;

import com.nuce.group3.data.model.ProductDetail;
import com.nuce.group3.enums.EnumStatus;
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
            " from product_detail p where p.active_flag=1 and (:name is null or p.product_id  in (select pi.id from product_info pi where pi.active_flag=1 and pi.name like %:name%))" +
            " and (:imei is null or p.imei like %:imei%)" +
            " and (:status is null or p.status = :status)" +
            " and (:branchId is null or p.shelf_id in (select s.id from shelf s where s.branch_id = :branchId))", nativeQuery = true)
    List<ProductDetail> findProductDetailByFilter(@Param(value = "name") String name, @Param(value = "imei") String imei, @Param(value = "branchId") Integer branchId, @Param(value = "status") String status, Pageable pageable);

    Optional<ProductDetail> findProductDetailByIdAndActiveFlag(int id, int activeFlag);

    @Query(value = "select p.*" +
            " from product_detail p where p.active_flag=1 and (:productStatusListId is null or p.product_status_list_id = :productStatusListId)", nativeQuery = true)
    List<ProductDetail> findProductDetailsByProductStatusList(int productStatusListId);

    Optional<ProductDetail> findProductDetailByImeiAndStatusAndActiveFlag(String imei, EnumStatus status, int activeFlag);

    @Query(value = "select p.*" +
            " from product_detail p where p.active_flag=1 and (:productInfoId is null or p.product_id = :productInfoId) " +
            " and p.status= :status", nativeQuery = true)
    List<ProductDetail> findProductDetailByProductInfoAndStatus(Integer productInfoId, String status);

    @Query(value = "select p.*" +
            " from product_detail p where p.active_flag=1 " +
            " and (:branchId is null or p.shelf_id in (select s.id from shelf s where s.active_flag=1 and s.branch_id = :branchId))", nativeQuery = true)
    List<ProductDetail> findProductDetailByBranch(Integer branchId);

    @Query(value = "select p.*" +
            " from product_detail p where p.active_flag=1 " +
            " and (:shelf_id is null or p.shelf_id = :shelfId)", nativeQuery = true)
    List<ProductDetail> findProductDetailByShelf(Integer shelfId);
}
