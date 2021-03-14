package com.nuce.group3.data.repo;

import com.nuce.group3.data.model.Role;
import com.nuce.group3.data.model.Users;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoleRepo {
    @Query(value = "select * from role where role.active_flag=1", nativeQuery = true)
    public List<Role> getAllRoles();
}
