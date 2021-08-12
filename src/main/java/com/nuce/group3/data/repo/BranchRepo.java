package com.nuce.group3.data.repo;

import com.nuce.group3.data.model.Branch;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BranchRepo extends JpaRepository<Branch, Integer> {
    @Query(value = "select b.*" +
            " from branch b where b.active_flag=1 and (:name is null or b.name like %:name%)", nativeQuery = true)
    List<Branch> findBranchByNameAndActiveFlag(String name);

    Optional<Branch> findBranchByCodeAndActiveFlag(String code, int activeFlag);

    Optional<Branch> findBranchByIdAndActiveFlag(int branchId, int activeFlag);

    @Query(value = "select b.*" +
            " from branch b where b.active_flag=1 and (:name is null or b.name like %:name%)" +
            " and (:code is null or b.code like %:code%)" +
            " and (:branchId is null or b.id = : branchId)", nativeQuery = true)
    List<Branch> findBranchByFilter(@Param(value = "name") String name, @Param(value = "code") String code, @Param(value = "branchId") Integer branchId, Pageable pageable);

    List<Branch> findBranchByActiveFlag(int activeFlag);
}
