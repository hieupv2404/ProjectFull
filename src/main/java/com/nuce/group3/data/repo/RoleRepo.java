package com.nuce.group3.data.repo;

import com.nuce.group3.data.model.Role;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RoleRepo extends JpaRepository<Role, Integer> {
    @Query(value = "select * from role where role.active_flag=1", nativeQuery = true)
    List<Role> getAllRoles(PageRequest pageRequest);

    Optional<Role> findRoleByIdAndActiveFlag(int roleId, int activeFlag);

    Optional<Role> findRoleByRoleNameAndActiveFlag(String name, int activeFlag);

    @Query(value = "select * from role r where r.active_flag=1 and (:name is null or r.role_name like %:name%)", nativeQuery = true)
    List<Role> findRoleByFilter(String name, Pageable pageable);
}
