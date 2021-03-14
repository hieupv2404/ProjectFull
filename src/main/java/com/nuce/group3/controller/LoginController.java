package com.nuce.group3.controller;

import com.nuce.group3.controller.dto.request.Credential;
import com.nuce.group3.data.model.Role;
import com.nuce.group3.data.model.Users;
import com.nuce.group3.exception.LogicException;
import com.nuce.group3.service.UserService;
import com.nuce.group3.utils.HashingPassword;
import com.nuce.group3.utils.TokenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/")
public class LoginController {

    @Autowired
    private TokenManager tokenManager;

    @Autowired
    private UserService userService;


    @GetMapping(value = "/login")
    public String login(HttpServletRequest request) {
        return "login";
    }

    @PostMapping(value = "/doLogin")
    public Object doLogin(@RequestBody @Valid Credential credential, HttpServletRequest request) throws LogicException, ResourceNotFoundException {
        Optional<Users> optionalUser = userService.findByUsername(credential.getUserName());
        if (optionalUser.isPresent()) {
            Users user = optionalUser.get();
            String passMD = HashingPassword.encrypt(credential.getPassWord());
            if (user.getPassword().toUpperCase().equals(passMD.toUpperCase())) {
                Set<String> roleName = new HashSet<>();
                for (Role role : user.getRoles()) {
                    roleName.add(role.getRoleName());
                }
                String token = tokenManager.createToken(user.getUserName(), roleName);
                request.getSession().setAttribute("role", roleName);
                request.getSession().setAttribute("AuthToken", token);
                request.getSession().setAttribute("Username", credential.getUserName());
                user.setToken(token);
                return user;
            }
            return new LogicException("Tài khoản hoặc mật khẩu không đúng", HttpStatus.NOT_FOUND);
        }

        return new LogicException("Tài khoản hoặc mật khẩu không đúng", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/logout")
    @ResponseBody
    public ResponseEntity logout(HttpSession session) {
        session.removeAttribute("role");
        session.removeAttribute("AuthToken");
        session.removeAttribute("Username");
        return ResponseEntity.ok("Logout ");
    }
}
// set session co 3 thu: role la gi, auth cho user da dang nhap hay chua,
