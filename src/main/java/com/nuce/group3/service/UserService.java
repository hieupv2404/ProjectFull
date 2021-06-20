package com.nuce.group3.service;

import com.nuce.group3.controller.ResourceNotFoundException;
import com.nuce.group3.controller.dto.request.UsersRequest;
import com.nuce.group3.controller.dto.response.UserResponse;
import com.nuce.group3.data.model.Users;
import com.nuce.group3.exception.LogicException;

import javax.mail.MessagingException;
import java.util.List;
import java.util.Optional;

public interface UserService {

    List<Users> getAllUsers();

    List<UserResponse> getUserRole();

    List<UserResponse> findUserByFilter(String name, String phone, String address, String userName, Integer page, Integer size);

    List<UserResponse> findUserByName(String query);

    Optional<Users> findByUsername(String username) throws ResourceNotFoundException;

    String forgetPassword(String email) throws ResourceNotFoundException, LogicException, MessagingException;

    void save(UsersRequest usersRequest) throws LogicException, ResourceNotFoundException;

    UserResponse edit(Integer userId, UsersRequest usersRequest) throws ResourceNotFoundException, LogicException;

    void delete(Integer userId) throws ResourceNotFoundException;


}
