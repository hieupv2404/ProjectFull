package com.nuce.group3.controller;

import com.nuce.group3.controller.dto.request.Credential;
import com.nuce.group3.controller.dto.response.MenuResponseLogin;
import com.nuce.group3.controller.dto.response.UserResponseLogin;
import com.nuce.group3.data.model.Menu;
import com.nuce.group3.data.model.Role;
import com.nuce.group3.data.model.Users;
import com.nuce.group3.data.repo.AuthRepo;
import com.nuce.group3.data.repo.MenuRepo;
import com.nuce.group3.exception.LogicException;
import com.nuce.group3.service.UserService;
import com.nuce.group3.utils.HashingPassword;
import com.nuce.group3.utils.TokenManager;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value="/api/",  headers = "Accept=application/json")
@CrossOrigin(origins = "*", maxAge = 3600, allowedHeaders = "*")
public class LoginController {

    @Autowired
    private TokenManager tokenManager;

    @Autowired
    private UserService userService;

    @Autowired
    private MenuRepo menuRepo;

    @Autowired
    private AuthRepo authRepo;


    @GetMapping(value = "/login")
    public String login(HttpServletRequest request) {
        return "login";
    }

    @PostMapping(value = "/doLogin")
    public ResponseEntity<Object> doLogin(@RequestBody @Valid Credential credential, HttpServletRequest request) throws LogicException, ResourceNotFoundException {
        Optional<Users> optionalUser = userService.findByUsername(credential.getUserName());
        if (optionalUser.isPresent()) {
            Users user = optionalUser.get();
            String passMD = HashingPassword.encrypt(credential.getPassWord());
            if (user.getPassword().toUpperCase().equals(passMD.toUpperCase())) {
                Set<String> roleName = new HashSet<>();
                Set<MenuResponseLogin> menuResponseLogins = new HashSet<>();
                Set<Integer> menuIds = new HashSet<>();
                for (Role role : user.getRoles()) {
                    roleName.add(role.getRoleName());
                    menuIds = authRepo.findIdMenu(role.getId());
                }
                    for(int menuId: menuIds)
                    {
                            Optional<Menu> menu = menuRepo.findMenuByIdAndActiveFlag(menuId, 1);
                            if(menu.isPresent() && menu.get().getOrderIndex()>=0) {
                                MenuResponseLogin menuResponseLogin = new MenuResponseLogin();
                                menuResponseLogin.setName(menu.get().getName());
                                menuResponseLogin.setParentId(menu.get().getParentId());
                                menuResponseLogin.setUrl(menu.get().getUrl());
                                menuResponseLogin.setOrderIndex(menu.get().getOrderIndex());
                                menuResponseLogins.add(menuResponseLogin);
                            }
                    }
                String token = tokenManager.createToken(user.getUserName(), roleName);
                request.getSession().setAttribute("role", roleName);
                request.getSession().setAttribute("AuthToken", token);
                request.getSession().setAttribute("Username", credential.getUserName());
                UserResponseLogin userResponseLogin = new UserResponseLogin();
                userResponseLogin.setToken(token);
                userResponseLogin.setName(user.getName());
                userResponseLogin.setUserName(user.getUserName());
                userResponseLogin.setEmail(user.getEmail());
                userResponseLogin.setRoleName(roleName);
                userResponseLogin.setMenuSet(menuResponseLogins);
                return new ResponseEntity<>(userResponseLogin, HttpStatus.OK);
            }
            return new ResponseEntity<>(new LogicException("Tài khoản hoặc mật khẩu không đúng", HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(new LogicException("Tài khoản hoặc mật khẩu không đúng", HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
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
