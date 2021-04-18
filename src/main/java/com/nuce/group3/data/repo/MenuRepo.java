package com.nuce.group3.data.repo;

import com.nuce.group3.data.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MenuRepo extends JpaRepository<Menu, Integer> {
    @Query(value = "select * from menu m where m.id=(select a.menu_id from auth a where a.role_id=?1 " +
            "and a.permission=1 and a.active_flag=1) and m.active_flag=1 and m.order_index>=0", nativeQuery = true)
    List<Menu> findMenuByRole(Integer roleId);

    Optional<Menu> findMenuByIdAndActiveFlag(int id, int activeFlag);


}
