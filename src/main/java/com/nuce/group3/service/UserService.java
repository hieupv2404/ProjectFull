package com.nuce.group3.service;

import com.nuce.group3.controller.ResourceNotFoundException;
import com.nuce.group3.controller.dto.response.UserResponse;
import com.nuce.group3.data.model.Users;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserService {

    //    public List<Users> getAllUsers(Users users, Paging paging);
    public List<Users> getAllUsers();
    public List<UserResponse> getUserRole();
    public List<UserResponse> testQuery(String query);
    public Optional<Users> findByUsername(String username) throws ResourceNotFoundException;


}
