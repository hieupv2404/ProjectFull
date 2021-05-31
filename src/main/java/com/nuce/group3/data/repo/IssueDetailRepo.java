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

    @Query(value = "select vd.*" +
            " from issue_detail vd where v.active_flag=1 and (:priceTotalFrom is null or vd.price_one*vd.qty >= :priceTotalFrom)" +
            " and (:priceTotalTo is null or vd.price_one*vd.qty <= :priceTotalTo)" +
            " and (:issueCode is null or vd.issue_id in (select v.id from issue s where v.code like %:issueCode%))" +
            " and (:productInfo is null or v.product_id in (select p.id from product_info p where p.name like %:productInfo%)) ", nativeQuery = true)
    List<IssueDetail> findIssueDetailByFilter(@Param(value = "priceTotalFrom") BigDecimal priceTotalFrom, @Param(value = "priceTotalTo") BigDecimal priceTotalTo, @Param(value = "issueCode") String issueCode, @Param(value = "productInfo") String productInfo, Pageable pageable);

    Optional<IssueDetail> findIssueDetailByIdAndActiveFlag(int id, int activeFlag);

    @Query(value = "select vd.* from issue_detail vd where vd.issue_id=?1 and vd.product_id=?2 and vd.active_flag=1", nativeQuery = true)
    Optional<IssueDetail> findIssueDetailByIssueAndProduct(int issueId, int productId);

    @Query(value = "select vd.* from issue_detail vd where vd.issue_id=?1 and vd.active_flag=1", nativeQuery = true)
    List<IssueDetail> findIssueDetailByIssue(int issueId);

}
