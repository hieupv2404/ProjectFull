package com.nuce.group3.service.impl;

import com.nuce.group3.controller.ResourceNotFoundException;
import com.nuce.group3.controller.dto.request.UsersRequest;
import com.nuce.group3.controller.dto.response.GenericResponse;
import com.nuce.group3.controller.dto.response.UserResponse;
import com.nuce.group3.data.model.Branch;
import com.nuce.group3.data.model.Role;
import com.nuce.group3.data.model.Users;
import com.nuce.group3.data.repo.BranchRepo;
import com.nuce.group3.data.repo.RoleRepo;
import com.nuce.group3.data.repo.UserRepo;
import com.nuce.group3.exception.LogicException;
import com.nuce.group3.service.UserService;
import com.nuce.group3.utils.HashingPassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private BranchRepo branchRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Override
    public List<Users> getAllUsers() {
        return userRepo.getAllUsers();
    }

    public List<UserResponse> getUserRole() {
        List<Users> users = userRepo.getAllUsers();
        List<UserResponse> userResponses = new ArrayList<>();
        for (Users users1 : users) {
            UserResponse userResponse = new UserResponse();
            userResponse.setUserName(users1.getUserName());
            userResponse.setEmail(users1.getEmail());
            userResponse.setName(users1.getName());
            userResponse.setPhone(users1.getPhone());
            userResponse.setCreateDate(users1.getCreateDate());
            userResponse.setUpdateDate(users1.getUpdateDate());
            userResponse.setBranchName(users1.getBranch().getName());
            userResponse.setBranchId(users1.getBranch().getId());
            Set<Role> roleSet = users1.getRoles();
            for (Role role : roleSet) {
                userResponse.getRoleName().add(role.getRoleName());
            }
            userResponses.add(userResponse);
        }
        return userResponses;
    }

    @Override
    public GenericResponse findUserByFilter(String name, String phone, String branch, String userName, Integer page, Integer size) {
        if (page == null) page = 0;
        if (size == null) size = 5;
        List<UserResponse> userResponses = new ArrayList<>();
        userRepo.findUserByFilter(name, phone, branch, userName, PageRequest.of(page, size)).forEach(users -> {
            UserResponse userResponse = new UserResponse();
            userResponse.setUserName(users.getUserName());
            userResponse.setEmail(users.getEmail());
            userResponse.setName(users.getName());
            userResponse.setPhone(users.getPhone());
            userResponse.setCreateDate(users.getCreateDate());
            userResponse.setUpdateDate(users.getUpdateDate());
            userResponse.setBranchName(users.getBranch().getName());
            userResponse.setBranchId(users.getBranch().getId());
            users.getRoles().forEach(role -> {
                userResponse.getRoleName().add(role.getRoleName());
            });
            userResponses.add(userResponse);
        });
        return new GenericResponse(userResponses.size(), userResponses);
    }

    @Override
    public List<UserResponse> findUserByName(String name) {
        List<Users> usersList = userRepo.findUsersByName(name);
        List<UserResponse> userResponses = new ArrayList<>();
        for (Users users1 : usersList) {
            UserResponse userResponse = new UserResponse();
            userResponse.setUserName(users1.getUserName());
            userResponse.setEmail(users1.getEmail());
            userResponse.setName(users1.getName());
            userResponse.setPhone(users1.getPhone());
            userResponse.setCreateDate(users1.getCreateDate());
            userResponse.setUpdateDate(users1.getUpdateDate());
            userResponse.setBranchName(users1.getBranch().getName());
            userResponse.setBranchId(users1.getBranch().getId());
            Set<Role> roleSet = users1.getRoles();
            for (Role role : roleSet) {
                userResponse.getRoleName().add(role.getRoleName());
            }
            userResponses.add(userResponse);
        }
        return userResponses;
    }

    @Override
    public Optional<Users> findByUsername(String username) throws ResourceNotFoundException {
        Optional<Users> users = userRepo.findUsersByUserName(username);
        if (!users.isPresent()) {
            throw new ResourceNotFoundException("User with username " + username + " not found");
        }
        return users;
    }

    public String randomPassword() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    @Override
    public String forgetPassword(String emailReceipt) throws ResourceNotFoundException, LogicException, MessagingException {
        Optional<Users> users = userRepo.findUsersByEmailAndActiveFlag(emailReceipt, 1);
        if (!users.isPresent()) {
            throw new ResourceNotFoundException("User with email " + emailReceipt + " not found");
        }

        Properties mailServerProperties;
        Session getMailSession;
        MimeMessage mailMessage;

        mailServerProperties = System.getProperties();
        mailServerProperties.put("mail.smtp.port", "587");
        mailServerProperties.put("mail.smtp.auth", "true");
        mailServerProperties.put("mail.smtp.starttls.enable", "true");

        getMailSession = Session.getDefaultInstance(mailServerProperties, null);
        mailMessage = new MimeMessage(getMailSession);

        try {
            mailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(emailReceipt));
        } catch (MessagingException e) {
            throw new LogicException("Send to mail " + emailReceipt + " failed.", HttpStatus.BAD_REQUEST);
        }

        String newPass = randomPassword();
        mailMessage.setSubject("New Password for system");
        mailMessage.setText("New Password for login: " + newPass);

        // Step3: Send mail
        Transport transport = getMailSession.getTransport("smtp");

        transport.connect("smtp.gmail.com", "hieuphan.dev@gmail.com", "Hieukaka@24041412");
        transport.sendMessage(mailMessage, mailMessage.getAllRecipients());
        transport.close();

        users.get().setPassword(HashingPassword.encrypt(newPass));
        users.get().setUpdateDate(new Date());
        userRepo.save(users.get());
        return "Send Success";
    }

    @Override
    public void save(UsersRequest usersRequest) throws LogicException, ResourceNotFoundException {
        Optional<Users> usersOptionalByUsername = userRepo.findUsersByUserName(usersRequest.getUserName());
        if (usersOptionalByUsername.isPresent()) {
            throw new LogicException("Username " + usersRequest.getUserName() + " is existed!", HttpStatus.BAD_REQUEST);
        }
        Optional<Users> usersOptionalByEmail = userRepo.findUsersByEmailAndActiveFlag(usersRequest.getEmail(), 1);
        if (usersOptionalByEmail.isPresent()) {
            throw new LogicException("Email " + usersRequest.getEmail() + " is existed!", HttpStatus.BAD_REQUEST);
        }
        Optional<Branch> branchOptional = branchRepo.findBranchByIdAndActiveFlag(usersRequest.getBranchId(), 1);
        if (!branchOptional.isPresent()) {
            throw new ResourceNotFoundException("Branch with ID " + usersRequest.getBranchId() + " is not existed!");
        }
        Set<Role> roles = new HashSet<>();
        usersRequest.getRoles().forEach(roleId -> {
            Optional<Role> roleOptional = roleRepo.findRoleByIdAndActiveFlag(roleId, 1);
            if (!roleOptional.isPresent()) {
                try {
                    throw new ResourceNotFoundException("Role with ID " + roleId + "is not existed!");
                } catch (ResourceNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                roles.add(roleOptional.get());
            }
        });

        Users users = new Users();
        users.setName(usersRequest.getName());
        users.setUserName(usersRequest.getUserName());
        users.setPassword(HashingPassword.encrypt(usersRequest.getPassword()));
        users.setCreateDate(new Date());
        users.setUpdateDate(new Date());
        users.setActiveFlag(1);
        users.setEmail(usersRequest.getEmail());
        users.setPhone(usersRequest.getPhone());
        users.setBranch(branchOptional.get());
        users.setRoles(roles);

        userRepo.save(users);
    }

    @Override
    public UserResponse edit(Integer userId, UsersRequest usersRequest) throws ResourceNotFoundException, LogicException {
        Optional<Users> usersOptional = userRepo.findUsersByIdAndActiveFlag(userId, 1);
        if (!usersOptional.isPresent()) {
            throw new ResourceNotFoundException("User with ID: " + userId + " not found!");
        }
        Users users = usersOptional.get();

        Set<Role> roles = new HashSet<>();
        usersRequest.getRoles().forEach(roleId -> {
            Optional<Role> roleOptional = roleRepo.findRoleByIdAndActiveFlag(roleId, 1);
            if (!roleOptional.isPresent()) {
                try {
                    throw new ResourceNotFoundException("Role with ID " + roleId + "is not existed!");
                } catch (ResourceNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                roles.add(roleOptional.get());
            }
        });

        Optional<Branch> branchOptional = branchRepo.findBranchByIdAndActiveFlag(usersRequest.getBranchId(), 1);
        if (!branchOptional.isPresent()) {
            throw new ResourceNotFoundException("Branch with ID " + usersRequest.getBranchId() + " is not existed!");
        }

        users.setPhone(usersRequest.getPhone());
        users.setName(usersRequest.getName());
        users.setRoles(roles);
        users.setBranch(branchOptional.get());
        users.setUpdateDate(new Date());
        users.setPassword(HashingPassword.encrypt(usersRequest.getPassword()));
        UserResponse userResponse = new UserResponse();

        userRepo.save(users);
        userResponse.setUserName(users.getUserName());
        userResponse.setEmail(users.getEmail());
        userResponse.setName(users.getName());
        userResponse.setPhone(users.getPhone());
        userResponse.setCreateDate(users.getCreateDate());
        userResponse.setUpdateDate(users.getUpdateDate());
        userResponse.setBranchName(users.getBranch().getName());
        Set<Role> roleSet = users.getRoles();
        for (Role role : roleSet) {
            userResponse.getRoleName().add(role.getRoleName());
        }
        return userResponse;
    }

    @Override
    public void delete(Integer userId) throws ResourceNotFoundException {
        Optional<Users> usersOptional = userRepo.findUsersByIdAndActiveFlag(userId, 1);
        if (!usersOptional.isPresent()) {
            throw new ResourceNotFoundException("User with ID " + userId + " not found!");
        }
        usersOptional.get().setActiveFlag(0);
        userRepo.save(usersOptional.get());
    }

    @Override
    public UserResponse assignRole(Integer userId, List<Integer> roleIds) throws ResourceNotFoundException {
        Optional<Users> usersOptional = userRepo.findUsersByIdAndActiveFlag(userId, 1);
        if (!usersOptional.isPresent()) {
            throw new ResourceNotFoundException("User with ID " + userId + " not found!");
        }
        Set<Role> roleSet = new HashSet<>();
        roleIds.forEach(roleId -> {
            Optional<Role> roleOptional = roleRepo.findRoleByIdAndActiveFlag(roleId, 1);
            if (!roleOptional.isPresent()) {
                try {
                    throw new ResourceNotFoundException("Role with ID " + roleId + " not found!");
                } catch (ResourceNotFoundException resourceNotFoundException) {
                    resourceNotFoundException.printStackTrace();
                }
            }
            roleSet.add(roleOptional.get());
        });
        usersOptional.get().setRoles(roleSet);
        userRepo.save(usersOptional.get());
        Users users = usersOptional.get();
        UserResponse userResponse = new UserResponse();

        userResponse.setUserName(users.getUserName());
        userResponse.setEmail(users.getEmail());
        userResponse.setName(users.getName());
        userResponse.setPhone(users.getPhone());
        userResponse.setCreateDate(users.getCreateDate());
        userResponse.setUpdateDate(users.getUpdateDate());
        userResponse.setBranchName(users.getBranch().getName());
        userResponse.setBranchId(users.getBranch().getId());
        users.getRoles().forEach(role -> {
            userResponse.getRoleName().add(role.getRoleName());
        });
        return userResponse;
    }
}
