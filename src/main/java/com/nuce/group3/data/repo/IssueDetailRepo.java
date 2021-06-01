package com.nuce.group3.data.repo;

import com.nuce.group3.data.model.IssueDetail;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface IssueDetailRepo extends JpaRepository<IssueDetail, Integer> {

    List<IssueDetail> findIssueDetailByActiveFlag(int activeFlag, Pageable pageable);

    @Query(value = "select i.*" +
            " from issue_detail i where v.active_flag=1 and (:priceTotalFrom is null or i.price_one*i.qty >= :priceTotalFrom)" +
            " and (:priceTotalTo is null or i.price_one*i.qty <= :priceTotalTo)" +
            " and (:imei is null or i.imei= :imei)" +
            " and (:issueCode is null or i.issue_id in (select s.id from issue s where s.code like %:issueCode%))" +
            " and (:productInfo is null or v.product_id in (select p.id from product_info p where p.name like %:productInfo%)) ", nativeQuery = true)
    List<IssueDetail> findIssueDetailByFilter(@Param(value = "priceTotalFrom") BigDecimal priceTotalFrom, @Param(value = "priceTotalTo") BigDecimal priceTotalTo, @Param(value = "issueCode") String issueCode, @Param(value = "productInfo") String productInfo, Pageable pageable);

    Optional<IssueDetail> findIssueDetailByIdAndActiveFlag(int i, int activeFlag);

    @Query(value = "select i.* from issue_detail i where i.issue_i=?1 and i.product_i=?2 and i.imei=?3 and i.active_flag=1", nativeQuery = true)
    Optional<IssueDetail> findIssueDetailByIssueAndProductAndImei(int issueId, int productId, String imei);

    @Query(value = "select i.* from issue_detail i where i.issue_i=?1 and i.active_flag=1", nativeQuery = true)
    List<IssueDetail> findIssueDetailByIssue(int issueId);

}
