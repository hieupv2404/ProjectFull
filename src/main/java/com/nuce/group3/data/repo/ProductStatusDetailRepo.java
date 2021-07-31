package com.nuce.group3.data.repo;

import com.nuce.group3.data.model.ProductStatusDetail;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductStatusDetailRepo extends JpaRepository<ProductStatusDetail, Integer> {

    @Query(value = "select pd.*" +
            " from product_status_detail pd where pd.active_flag=1" +
            " and (:type is null pd.product_status_list_id in (select pl.id from product_status_list pl where pl.active_flag =1 and pl.type = :type))", nativeQuery = true)
    List<ProductStatusDetail> findProductStatusDetailByActiveFlag(int type, Pageable pageable);

    @Query(value = "select pd.*" +
            " from product_status_detail pd where pd.active_flag=1 and (:priceTotalFrom is null or pd.price_one*pd.qty >= :priceTotalFrom)" +
            " and (:priceTotalTo is null or pd.price_one*pd.qty <= :priceTotalTo)" +
            " and (:productStatusListCode is null or pd.product_status_list_id in (select pl.id from product_status_list pl where pl.active_flag =1 and pl.code like %:productStatusListCode%))" +
            " and (:productInfo is null or pd.product_id in (select p.id from product_info p where p.active_flag =1 and p.name like %:productInfo%))" +
            " and (:type is null or pd.product_status_list_id in (select pl.id from product_status_list pl where pl.active_flag =1 and pl.type = :type)) " +
            " and (:branchId is null or pd.product_status_list_id in (select pl.id from product_status_list pl where pl.active_flag =1 and pl.vat_id in (select v.id from vat v where v.branch_id = :branchId)) )", nativeQuery = true)
    List<ProductStatusDetail> findProductStatusDetailByFilter(@Param(value = "priceTotalFrom") BigDecimal priceTotalFrom, @Param(value = "priceTotalTo") BigDecimal priceTotalTo, @Param(value = "productStatusListCode") String productStatusListCode, @Param(value = "productInfo") String productInfo, @Param(value = "type") int type, @Param(value = "branchId") Integer branchId, Pageable pageable);

    @Query(value = "select pd.*" +
            " from product_status_detail pd where pd.active_flag=1 and (:priceTotalFrom is null or pd.price_one*pd.qty >= :priceTotalFrom)" +
            " and (:priceTotalTo is null or pd.price_one*pd.qty <= :priceTotalTo)" +
            " and (:productStatusListCode is null or pd.product_status_list_id in (select pl.id from product_status_list pl where pl.active_flag =1 and pl.code like %:productStatusListCode%))" +
            " and (:productInfo is null or pd.product_id in (select p.id from product_info p where p.active_flag =1 and p.name like %:productInfo%))" +
            " and (:type is null or pd.product_status_list_id in (select pl.id from product_status_list pl where pl.active_flag =1 and pl.type = :type)) " +
            " and (:branchId is null or pd.product_status_list_id in (select pl.id from product_status_list pl where pl.active_flag =1 and pl.vat_id in (select v.id from vat v where v.branch_id = :branchId)) )", nativeQuery = true)
    List<ProductStatusDetail> findProductStatusDetailForExport(@Param(value = "priceTotalFrom") BigDecimal priceTotalFrom, @Param(value = "priceTotalTo") BigDecimal priceTotalTo, @Param(value = "productStatusListCode") String productStatusListCode, @Param(value = "productInfo") String productInfo, @Param(value = "type") int type, @Param(value = "branchId") Integer branchId);


    Optional<ProductStatusDetail> findProductStatusDetailByIdAndActiveFlag(int id, int activeFlag);

    @Query(value = "select pd.* from product_status_detail pd where pd.active_flag=1" +
            " and (:productStatusListId is null or pd.product_status_list_id=:productStatusListId)", nativeQuery = true)
    List<ProductStatusDetail> findProductStatusDetailByProductStatusListId(int productStatusListId);

    @Query(value = "select pd.* from product_status_detail pd where pd.product_status_list_id=?1 and pd.product_id=?2 and pd.active_flag=1", nativeQuery = true)
    Optional<ProductStatusDetail> findProductStatusDetailByProductStatusAndProduct(int productStatusListId, int productId);

    @Query(value = "SELECT COUNT(p.id) FROM product_status_detail p WHERE p.active_flag=1 " +
            " and (:type is null or p.product_status_list_id in (select pl.id from product_status_list pl where pl.active_flag =1 and pl.type = :type))", nativeQuery = true)
    long countProductStatusDetailByTypeAndActiveFlag(int type);
}
