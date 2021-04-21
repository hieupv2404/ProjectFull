package com.nuce.group3.data.repo;

import com.nuce.group3.data.model.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BranchRepo extends JpaRepository<Branch, Integer> {
    Optional<Branch> findBranchByNameAndActiveFlag(String branchName, int activeFlag);
    Optional<Branch> findBranchByIdAndActiveFlag(int branchId, int activeFlag);
}
