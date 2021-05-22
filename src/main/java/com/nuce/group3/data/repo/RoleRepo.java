package com.nuce.group3.data.repo;

import com.nuce.group3.data.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RoleRepo extends JpaRepository<Role, Integer> {
    @Query(value = "select * from role where role.active_flag=1", nativeQuery = true)
    List<Role> getAllRoles();

    Optional<Role> findRoleByIdAndActiveFlag(int roleId, int activeFlag);
}
