package com.nuce.group3.data.repo;

import com.nuce.group3.data.model.VatDetail;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface VatDetailRepo extends JpaRepository<VatDetail, Integer> {

    List<VatDetail> findVatDetailByActiveFlag(int activeFlag, Pageable pageable);

    @Query(value = "select vd.*" +
            " from vat_detail vd where vd.active_flag=1 and (:priceTotalFrom is null or vd.price_one*vd.qty >= :priceTotalFrom)" +
            " and (:priceTotalTo is null or vd.price_one*vd.qty <= :priceTotalTo)" +
            " and (:vatCode is null or vd.vat_id in (select v.id from vat v where v.active_flag =1 and v.code like %:vatCode%))" +
            " and (:productInfo is null or vd.product_id in (select p.id from product_info p where p.active_flag =1 and p.name like %:productInfo%)) ", nativeQuery = true)
    List<VatDetail> findVatDetailByFilter(@Param(value = "priceTotalFrom") BigDecimal priceTotalFrom, @Param(value = "priceTotalTo") BigDecimal priceTotalTo, @Param(value = "vatCode") String vatCode, @Param(value = "productInfo") String productInfo, Pageable pageable);

    Optional<VatDetail> findVatDetailByIdAndActiveFlag(int id, int activeFlag);

    @Query(value = "select vd.* from vat_detail vd where vd.vat_id=?1 and vd.product_id=?2 and vd.active_flag=1", nativeQuery = true)
    Optional<VatDetail> findVatDetailByVatAndProduct(int vatId, int productId);

    @Query(value = "select vd.* from vat_detail vd where vd.vat_id=?1 and vd.active_flag=1", nativeQuery = true)
    List<VatDetail> findVatDetailByVat(int vatId);

}
