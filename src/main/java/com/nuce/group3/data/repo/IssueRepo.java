package com.nuce.group3.data.repo;

import com.nuce.group3.data.model.Issue;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface IssueRepo extends JpaRepository<Issue, Integer> {
    Optional<Issue> findIssueByCodeAndActiveFlag(String code, int activeFlag);

    List<Issue> findIssueByActiveFlag(int activeFlag, Pageable pageable);

    @Query(value = "select i.*" +
            " from issue i where i.active_flag=1 and (:code is null or i.code like %:code%)" +
            " and (:customerName is null or i.customer_id in (select c.id from customer c where c.active_flag=1 and c.name like %:customerName%))" +
            " and (:userName is null or i.user_id in (select u.id from users u where u.active_flag=1 and u.name like %:userName% and u.branch_id = :branchId))" +
            " and (:dateFrom is null or i.create_date >= :dateFrom) and (:dateTo is null or i.create_date <= :dateTo) " +
            " and (:branchId is null or i.user_id in (select u.id from users u where u.active_flag=1 and u.branch_id = :branchId))", nativeQuery = true)
    List<Issue> findIssueByFilter(@Param(value = "code") String code, @Param(value = "customerName") String customerName, @Param(value = "userName") String userName, @Param(value = "branchId") Integer branchId,
                                  @Param(value = "dateFrom") Date dateFrom, @Param(value = "dateTo") Date dateTo, Pageable pageable);

    Optional<Issue> findIssueByIdAndActiveFlag(int id, int activeFlag);

    @Query(value = "SELECT COUNT(i.id) FROM issue i WHERE i.active_flag=1", nativeQuery = true)
    long countIssue();

}
