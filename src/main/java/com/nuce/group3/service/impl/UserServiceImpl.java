package com.nuce.group3.service.impl;

import com.nuce.group3.controller.ResourceNotFoundException;
import com.nuce.group3.controller.dto.response.UserResponse;
import com.nuce.group3.data.model.Role;
import com.nuce.group3.data.model.Users;
import com.nuce.group3.data.repo.UserRepo;
import com.nuce.group3.exception.LogicException;
import com.nuce.group3.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepo userRepo;

//    @Autowired
//    private UserDAO<Users> userDAO;

//    @Override
//    public List<Users> getAllUsers(Users users, Paging paging) {
//        StringBuilder queryStr = new StringBuilder();
//        Map<String, Object> mapParams = new HashMap<>();
//        if (users != null) {
//            if (!StringUtils.isEmpty(users.getName())) {
//                queryStr.append(" and model.name like :name");
//                mapParams.put("name", "%" + users.getName() + "%");
//            }
//            if (!StringUtils.isEmpty(users.getUserName())) {
//                queryStr.append(" and model.userName like :userName");
//                mapParams.put("userName", "%" + users.getUserName() + "%");
//            }
//        }
//        return userDAO.findAll(queryStr.toString(), mapParams, paging);
//    }


    @Override
    public List<Users> getAllUsers() {
        return userRepo.getAllUsers();
    }

    public List<UserResponse> getUserRole() {
        List<Users> users= userRepo.getAllUsers();
        List<UserResponse> userResponses = new ArrayList<>();
        for (Users users1: users){
            UserResponse userResponse = new UserResponse();
            userResponse.setUserName(users1.getUserName());
            userResponse.setEmail(users1.getEmail());
            userResponse.setName(users1.getName());
            userResponse.setCreateDate(users1.getCreateDate());
            userResponse.setUpdateDate(users1.getUpdateDate());
            Set<Role> roleSet = users1.getRoles();
            for (Role role:roleSet)
            {
                userResponse.getRoleName().add(role.getRoleName());
            }
            userResponses.add(userResponse);
        }
        return userResponses;
    }

    @Override
    public List<UserResponse> testQuery(String query) {
        List<Users> usersList = userRepo.testQuery(query);
        List<UserResponse> userResponses = new ArrayList<>();
        for (Users users1: usersList){
            UserResponse userResponse = new UserResponse();
            userResponse.setUserName(users1.getUserName());
            userResponse.setEmail(users1.getEmail());
            userResponse.setName(users1.getName());
            userResponse.setCreateDate(users1.getCreateDate());
            userResponse.setUpdateDate(users1.getUpdateDate());
            Set<Role> roleSet = users1.getRoles();
            for (Role role:roleSet)
            {
                userResponse.getRoleName().add(role.getRoleName());
            }
            userResponses.add(userResponse);
        }
        return userResponses;
    }

    @Override
    public Optional<Users> findByUsername(String username) throws ResourceNotFoundException {
        Optional<Users> users = userRepo.findUsersByUserName(username);
        if(!users.isPresent())
        {
            throw new ResourceNotFoundException("User with "+ username+ " not found");
        }
        return users;
    }
}
