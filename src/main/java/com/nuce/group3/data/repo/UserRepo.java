package com.nuce.group3.data.repo;

import com.nuce.group3.data.model.Users;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<Users, Integer> {
    @Query(value = "select * from users where users.active_flag=1 ", nativeQuery = true)
    List<Users> getAllUsers();

    @Query(value = "select * from users where users.active_flag=1 and users.name  like %:name%  ", nativeQuery = true)
    List<Users> findUsersByName(@Param(value = "name") String name);

    @Query(value = "select * from users where users.active_flag=1 and users.user_name =?1  ", nativeQuery = true)
    Optional<Users> findUsersByUserName(String userName);

    @Query(value = "select c.*" +
            " from users c where c.active_flag=1 and (:name is null or c.name like %:name%)" +
            " and (:phone is null or c.phone like %:phone%) " +
            " and (:branch is null or c.branch_id in (select b.id from branch b where b.active_flag=1 and b.name like %:branch%))" +
            " and (:userName is null or c.user_name like %:userName%) ", nativeQuery = true)
    List<Users> findUserByFilter(@Param(value = "name") String name, @Param(value = "phone") String phone, @Param(value = "branch") String branch, @Param(value = "userName") String userName, Pageable pageable);


    Optional<Users> findUsersByEmailAndActiveFlag(String email, int activeFlag);

    Optional<Users> findUsersByIdAndActiveFlag(int id, int activeFlag);
}
