package com.nuce.group3.controller;

import com.nuce.group3.controller.dto.response.UserResponse;
import com.nuce.group3.data.model.Paging;
import com.nuce.group3.data.model.Users;
import com.nuce.group3.interceptor.HasRole;
import com.nuce.group3.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    @HasRole({"ADMIN","ADMIN_PTTK"})
    public ResponseEntity<List<UserResponse>> getAll(){
        return new ResponseEntity<>(userService.getUserRole(),HttpStatus.OK);
//        return new ResponseEntity<>(userService.getAllUsers(),HttpStatus.OK);
    }

    @GetMapping("/testQuery")
    public ResponseEntity<List<UserResponse>> testQuery(){
        Users users= new Users();
        users.setName("");
        return new ResponseEntity<>(userService.testQuery(users.getName()),HttpStatus.OK);
    }
}
