package com.nuce.group3.data.repo;

import com.nuce.group3.data.model.Auth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface AuthRepo extends JpaRepository<Auth, Integer> {
    @Query(value = "select a.menu_id from auth a where a.role_id=?1 " +
            "and a.permission=1 and a.active_flag=1", nativeQuery = true)
    Set<Integer> findIdMenu(int roleId);

//    @Query(value="select * from customer s where s.name like %:name% and s.email like %:email%",nativeQuery=true)
//    List<Customer> findCustomerByProperty(@Param(value="name") String name, @Param(value="email") String email);
}
