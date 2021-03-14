package com.nuce.group3.data.repo;

import com.nuce.group3.controller.dto.response.UserResponse;
import com.nuce.group3.data.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<Users, Integer> {
    @Query(value = "select * from users where users.active_flag=1 ", nativeQuery = true)
    public List<Users> getAllUsers();

    @Query(value = "select * from users where users.active_flag=1 and users.name  like %:name%  ", nativeQuery = true)
    public List<Users> testQuery(@Param(value="name") String query);

    @Query(value = "select * from users where users.active_flag=1 and users.user_name =?1  ", nativeQuery = true)
    public Optional<Users> findUsersByUserName(String userName);
}
