package com.nuce.group3.controller;

import com.nuce.group3.controller.dto.request.UserForgetPassword;
import com.nuce.group3.controller.dto.response.UserResponse;
import com.nuce.group3.data.model.Paging;
import com.nuce.group3.data.model.Users;
import com.nuce.group3.exception.LogicException;
import com.nuce.group3.interceptor.HasRole;
import com.nuce.group3.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
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

    @PostMapping("/forget-password")
    public ResponseEntity<String> forgetPassword(@RequestBody @Valid UserForgetPassword userForgetPassword) throws LogicException, MessagingException, ResourceNotFoundException {
        return new ResponseEntity<>(userService.forgetPassword(userForgetPassword.getEmail()), HttpStatus.OK);
    }
}
