package com.nuce.group3.controller;

import com.nuce.group3.controller.dto.request.UserForgetPassword;
import com.nuce.group3.controller.dto.request.UsersRequest;
import com.nuce.group3.controller.dto.response.UserResponse;
import com.nuce.group3.data.model.Users;
import com.nuce.group3.exception.LogicException;
import com.nuce.group3.interceptor.HasRole;
import com.nuce.group3.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/api/users", headers = "Accept=application/json")
@CrossOrigin(origins = "*", maxAge = 3600, allowedHeaders = "*")

public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("")
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<List<UserResponse>> getUsersByFilter(@RequestParam(name = "name", required = false) String name,
                                                               @RequestParam(name = "phone", required = false) String phone,
                                                               @RequestParam(name = "branch", required = false) String branch,
                                                               @RequestParam(name = "userName", required = false) String userName,
                                                               @RequestParam(name = "page", required = false) Integer page, @RequestParam(name = "size", required = false) Integer size) {
        return new ResponseEntity<>(userService.findUserByFilter(name, phone, branch, userName, page, size), HttpStatus.OK);
    }

    @GetMapping("/get-all")
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<List<UserResponse>> getAll() {
        return new ResponseEntity<>(userService.getUserRole(), HttpStatus.OK);
    }

    @GetMapping("/testQuery")
    public ResponseEntity<List<UserResponse>> testQuery() {
        Users users = new Users();
        users.setName("");
        return new ResponseEntity<>(userService.findUserByName(users.getName()), HttpStatus.OK);
    }

    @PostMapping("/forget-password")
    public ResponseEntity<String> forgetPassword(@RequestBody @Valid UserForgetPassword userForgetPassword) throws LogicException, MessagingException, ResourceNotFoundException {
        return new ResponseEntity<>(userService.forgetPassword(userForgetPassword.getEmail()), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody @Valid UsersRequest usersRequest) throws ResourceNotFoundException, LogicException {
        userService.save(usersRequest);
        return new ResponseEntity<>("Created!", HttpStatus.OK);
    }

    @PutMapping("/edit/{userId}")
    public ResponseEntity<UserResponse> editUser(@RequestBody @Valid UsersRequest usersRequest, @PathVariable("userId") int userId) throws ResourceNotFoundException, LogicException {
        return new ResponseEntity<>(userService.edit(userId, usersRequest), HttpStatus.OK);
    }

    @PutMapping("/delete/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable("userId") int userId) throws ResourceNotFoundException, LogicException {
        userService.delete(userId);
        return new ResponseEntity<>("Deleted!", HttpStatus.OK);
    }
}
