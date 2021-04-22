package com.nuce.group3.service.impl;

import com.nuce.group3.controller.ResourceNotFoundException;
import com.nuce.group3.controller.dto.response.UserResponse;
import com.nuce.group3.data.model.Role;
import com.nuce.group3.data.model.Users;
import com.nuce.group3.data.repo.UserRepo;
import com.nuce.group3.exception.LogicException;
import com.nuce.group3.service.UserService;
import com.nuce.group3.utils.HashingPassword;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
    public List<UserResponse> findUserByName(String name) {
        List<Users> usersList = userRepo.findUsersByName(name);
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
            throw new ResourceNotFoundException("User with username "+ username+ " not found");
        }
        return users;
    }

    public String randomPassword(){
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }

    @Override
    public String forgetPassword(String emailReceipt) throws ResourceNotFoundException, LogicException, MessagingException {
        Optional<Users> users = userRepo.findUsersByEmailAndActiveFlag(emailReceipt,1);
        if(!users.isPresent()){
            throw new ResourceNotFoundException("User with email "+ emailReceipt + " not found");
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
            throw new LogicException("Send to mail "+ emailReceipt + " failed.", HttpStatus.BAD_REQUEST);
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


}
