package com.nuce.group3.service.impl;

import com.nuce.group3.controller.ResourceNotFoundException;
import com.nuce.group3.controller.dto.request.AssignRoleRequest;
import com.nuce.group3.controller.dto.request.ChangePassRequest;
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
            userResponse.setId(users1.getId());
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
    public GenericResponse findUserByFilter(String name, String phone, Integer branchId, String userName, Integer page, Integer size) {
        if (page == null) page = 0;
        if (size == null) size = 5;
        List<UserResponse> userResponses = new ArrayList<>();
        userRepo.findUserByFilter(name, phone, branchId, userName, PageRequest.of(page, size)).forEach(users -> {
            UserResponse userResponse = new UserResponse();
            userResponse.setId(users.getId());
            userResponse.setUserName(users.getUserName());
            userResponse.setEmail(users.getEmail());
            userResponse.setName(users.getName());
            userResponse.setPhone(users.getPhone());
            userResponse.setCreateDate(users.getCreateDate());
            userResponse.setUpdateDate(users.getUpdateDate());
            userResponse.setBranchName(users.getBranch().getName());
            userResponse.setBranchId(users.getBranch().getId());
            userResponse.setPassword(users.getPassword());
            users.getRoles().forEach(role -> {
                userResponse.getRoleName().add(role.getRoleName());
            });
            userResponses.add(userResponse);
        });
        return new GenericResponse(userRepo.findUserByFilter(name, phone, branchId, userName, PageRequest.of(0, 1000)).size(), userResponses);
    }

    @Override
    public List<UserResponse> findUserByName(String name) {
        List<Users> usersList = userRepo.findUsersByName(name);
        List<UserResponse> userResponses = new ArrayList<>();
        for (Users users1 : usersList) {
            UserResponse userResponse = new UserResponse();
            userResponse.setId(users1.getId());
            userResponse.setUserName(users1.getUserName());
            userResponse.setEmail(users1.getEmail());
            userResponse.setName(users1.getName());
            userResponse.setPhone(users1.getPhone());
            userResponse.setCreateDate(users1.getCreateDate());
            userResponse.setUpdateDate(users1.getUpdateDate());
            userResponse.setBranchName(users1.getBranch().getName());
            userResponse.setPassword(users1.getPassword());
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
        mailServerProperties.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        getMailSession = Session.getDefaultInstance(mailServerProperties, null);
        mailMessage = new MimeMessage(getMailSession);

        try {
            mailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(emailReceipt));
        } catch (MessagingException e) {
            throw new LogicException("Send to mail " + emailReceipt + " failed.", HttpStatus.BAD_REQUEST);
        }

        String newPass = randomPassword();
        mailMessage.setSubject("New password for " + users.get().getUserName());
        mailMessage.setContent("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\"\n" +
                "xmlns:th=\"http://www.thymeleaf.org\">\n" +
                "\n" +
                "<head>\n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n" +
                "    <title>Forget Password</title>\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n" +
                "</head>\n" +
                "\n" +
                "<body style=\" background: #d8d8d8;\">\n" +
                "\n" +
                "\n" +
                "<!-- Recovery Password -->\n" +
                "<div style=\"width: 100%;margin: 0; padding: 0; font-family: Arial, sans-serif; background: #d8d8d8; font-size: 12px;\">\n" +
                "    <div style=\"max-width: 600px; background: #fff;margin: 0 auto\">\n" +
                "        <table border=\"0\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" >\n" +
                "            <tr style=\"\">\n" +
                "                <td width=\"50%\" colspan=\"2\" style=\"padding: 40px 20px 20px 20px;text-align: center;\">\n" +
                "                    <a href=\"#\" target=\"_blank\" style=\"text-decoration: none\">\n" +
//                "            \t\t\t<img src=\"https://api.vimo.vn/media/files/MjAyMTA1MjQtNDk1NDQxOjUwNDQ0MS0wLTE0OjExOjU5LjA4MS1yaXNrX2NvbG9y.png\" height=\"90px\">\n" +
                "                    </a>\n" +
                "                </td>\n" +
                "            </tr>\n" +
                "\n" +
                "            <tr>\n" +
                "                <td colspan=\"2\" style=\"padding: 20px; text-align: center;background: #006ee3;\">\n" +
                "                    <h2 style=\"color: #fff;margin: 0;text-transform: uppercase;\">RECOVERY PASSWORD</h2>\n" +
                "                </td>\n" +
                "            </tr>\n" +
                "            <tr>\n" +
                "                <td colspan=\"2\" style=\" vertical-align: top;color: #333\">\n" +
                "                    <p style=\"text-align: left;margin-top: 20px;padding: 0 20px;line-height: 22px;\"><label style=\"margin-bottom: 0;font-size: 17px; font-weight: bold;\">\n" +
                "                        Hello: <span>" + users.get().getName() + "</span></label>\n" +
                "                        <br/>\n" +
                "                        <span>The new password: <strong>" + newPass + "</strong></span></p>\n" +
                "\n" +
                "\n" +
                "                </td>\n" +
                "            </tr>\n" +
                "            <tr>\n" +
                "                <td colspan=\"2\" style=\"padding: 20px 20px;text-align: center;\">\n" +
                "                    <a href=\"http://localhost:8080/\" style=\"text-align:center;display: inline-block;text-decoration: none;border: 1px solid #006ee3;border-radius: 5px;padding: 10px 50px;background: #006ee3;color: #fff;font-size: 15px;\">Inventory Management</a>\n" +
                "                    <p style=\"margin-top: 20px;\">Return the System</p>\n" +
                "                </td>\n" +
                "            </tr>\n" +
                "            <tr>\n" +
                "                <td colspan=\"2\" style=\"padding: 20px;\">\n" +
                "                    <p><b>Thanks and Best Regards</b></p>\n" +
                "                    <p style=\"color:#f1726f;font-style: italic; \">This is auto mail for recovery password. Please don't reply this mail. Thanks!</p>\n" +
                "                </td>\n" +
                "            </tr>\n" +
                "            <tr>\n" +
                "                <td colspan=\"2\" style=\"background: #f6f6f6; color: #7c7c7c; text-align: left;padding: 0 15px;\">\n" +
                "                    <table width=\"100%\" style=\"padding: 0px 0 0;\">\n" +
                "                        <tr>\n" +
                "                            <td colspan=\"3\">\n" +
                "                                <p style=\"text-transform: uppercase; \">Copyright © 2021 Inventory Management</p>\n" +
                "                            </td>\n" +
                "                        </tr>\n" +
                "\n" +
                "                    </table>\n" +
                "\n" +
                "                </td>\n" +
                "            </tr>\n" +
                "        </table>\n" +
                "    </div>\n" +
                "</div>\n" +
                "\n" +
                "</body>\n" +
                "\n" +
                "</html>\n" +
                "\n" +
                "\n", "text/html");

//        mailMessage.setText("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
//                "<html xmlns=\"http://www.w3.org/1999/xhtml\"\n" +
//                "xmlns:th=\"http://www.thymeleaf.org\">\n" +
//                "\n" +
//                "<head>\n" +
//                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n" +
//                "    <title>Forget Password</title>\n" +
//                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n" +
//                "</head>\n" +
//                "\n" +
//                "<body style=\" background: #d8d8d8;\">\n" +
//                "\n" +
//                "\n" +
//                "<!-- Recovery Password -->\n" +
//                "<div style=\"width: 100%;margin: 0; padding: 0; font-family: Arial, sans-serif; background: #d8d8d8; font-size: 12px;\">\n" +
//                "    <div style=\"max-width: 600px; background: #fff;margin: 0 auto\">\n" +
//                "        <table border=\"0\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" >\n" +
//                "            <tr style=\"\">\n" +
//                "                <td width=\"50%\" colspan=\"2\" style=\"padding: 40px 20px 20px 20px;text-align: center;\">\n" +
//                "                    <a href=\"#\" target=\"_blank\" style=\"text-decoration: none\">\n" +
////                "            \t\t\t<img src=\"https://api.vimo.vn/media/files/MjAyMTA1MjQtNDk1NDQxOjUwNDQ0MS0wLTE0OjExOjU5LjA4MS1yaXNrX2NvbG9y.png\" height=\"90px\">\n" +
//                "                    </a>\n" +
//                "                </td>\n" +
//                "            </tr>\n" +
//                "\n" +
//                "            <tr>\n" +
//                "                <td colspan=\"2\" style=\"padding: 20px; text-align: center;background: #006ee3;\">\n" +
//                "                    <h2 style=\"color: #fff;margin: 0;text-transform: uppercase;\">RECOVERY PASSWORD</h2>\n" +
//                "                </td>\n" +
//                "            </tr>\n" +
//                "            <tr>\n" +
//                "                <td colspan=\"2\" style=\" vertical-align: top;color: #333\">\n" +
//                "                    <p style=\"text-align: left;margin-top: 20px;padding: 0 20px;line-height: 22px;\"><label style=\"margin-bottom: 0;font-size: 17px; font-weight: bold;\">\n" +
//                "                        Hello: <span>"+ users.get().getName() +"</span></label>\n" +
//                "                        <br/>\n" +
//                "                        <span>The new password: <strong>"+newPass+"</strong></span></p>\n" +
//                "\n" +
//                "\n" +
//                "                </td>\n" +
//                "            </tr>\n" +
//                "            <tr>\n" +
//                "                <td colspan=\"2\" style=\"padding: 20px 20px;text-align: center;\">\n" +
//                "                    <a href=\"http://localhost:8080/\" style=\"text-align:center;display: inline-block;text-decoration: none;border: 1px solid #006ee3;border-radius: 5px;padding: 10px 50px;background: #006ee3;color: #fff;font-size: 15px;\">Inventory Management</a>\n" +
//                "                    <p style=\"margin-top: 20px;\">Return the System</p>\n" +
//                "                </td>\n" +
//                "            </tr>\n" +
//                "            <tr>\n" +
//                "                <td colspan=\"2\" style=\"padding: 20px;\">\n" +
//                "                    <p><b>Thanks and Best Regards</b></p>\n" +
//                "                    <p style=\"color:#f1726f;font-style: italic; \">This is auto mail for recovery password. Please don't reply this mail. Thanks!</p>\n" +
//                "                </td>\n" +
//                "            </tr>\n" +
//                "            <tr>\n" +
//                "                <td colspan=\"2\" style=\"background: #f6f6f6; color: #7c7c7c; text-align: left;padding: 0 15px;\">\n" +
//                "                    <table width=\"100%\" style=\"padding: 0px 0 0;\">\n" +
//                "                        <tr>\n" +
//                "                            <td colspan=\"3\">\n" +
//                "                                <p style=\"text-transform: uppercase; \">Copyright © 2021 Inventory Management</p>\n" +
//                "                            </td>\n" +
//                "                        </tr>\n" +
//                "\n" +
//                "                    </table>\n" +
//                "\n" +
//                "                </td>\n" +
//                "            </tr>\n" +
//                "        </table>\n" +
//                "    </div>\n" +
//                "</div>\n" +
//                "\n" +
//                "</body>\n" +
//                "\n" +
//                "</html>\n" +
//                "\n" +
//                "\n");

        // Step3: Send mail
        Transport transport = getMailSession.getTransport("smtp");

        transport.connect("smtp.gmail.com", "hieuphan.dev@gmail.com", "qggiklbiszxhecoe");
        transport.sendMessage(mailMessage, mailMessage.getAllRecipients());
        transport.close();

        users.get().setPassword(HashingPassword.encrypt(newPass));
        users.get().setUpdateDate(new Date());
        userRepo.save(users.get());
        return "Send successfully";
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

        Optional<Users> usersOptionalByUsername = userRepo.findUsersByUserName(usersRequest.getUserName());
        if (!usersOptional.get().getUserName().equals(usersRequest.getUserName()) && usersOptionalByUsername.isPresent()) {
            throw new ResourceNotFoundException("User with ID: " + userId + " is existed!");
        }

        Users users = usersOptional.get();


        Optional<Branch> branchOptional = branchRepo.findBranchByIdAndActiveFlag(usersRequest.getBranchId(), 1);
        if (!branchOptional.isPresent()) {
            throw new ResourceNotFoundException("Branch with ID " + usersRequest.getBranchId() + " is not existed!");
        }

        users.setPhone(usersRequest.getPhone());
        users.setName(usersRequest.getName());
        users.setBranch(branchOptional.get());
        users.setUpdateDate(new Date());
        UserResponse userResponse = new UserResponse();

        userRepo.save(users);
        userResponse.setId(users.getId());
        userResponse.setUserName(users.getUserName());
        userResponse.setEmail(users.getEmail());
        userResponse.setName(users.getName());
        userResponse.setPhone(users.getPhone());
        userResponse.setCreateDate(users.getCreateDate());
        userResponse.setUpdateDate(users.getUpdateDate());
        userResponse.setBranchName(users.getBranch().getName());
        userResponse.setPassword(users.getPassword());
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
    public UserResponse assignRole(Integer userId, AssignRoleRequest roleIds) throws ResourceNotFoundException {
        Optional<Users> usersOptional = userRepo.findUsersByIdAndActiveFlag(userId, 1);
        if (!usersOptional.isPresent()) {
            throw new ResourceNotFoundException("User with ID " + userId + " not found!");
        }
        Set<Role> roleSet = new HashSet<>();
        for (Integer roleId : roleIds.getRoleIds()) {
            Optional<Role> roleOptional = roleRepo.findRoleByIdAndActiveFlag(roleId, 1);
            if (!roleOptional.isPresent()) {
                throw new ResourceNotFoundException("Role with ID " + roleId + " not found!");
            }
            roleSet.add(roleOptional.get());
        }
        usersOptional.get().setRoles(roleSet);
        userRepo.save(usersOptional.get());
        Users users = usersOptional.get();
        UserResponse userResponse = new UserResponse();

        userResponse.setUserName(users.getUserName());
        userResponse.setId(users.getId());
        userResponse.setEmail(users.getEmail());
        userResponse.setName(users.getName());
        userResponse.setPhone(users.getPhone());
        userResponse.setCreateDate(users.getCreateDate());
        userResponse.setUpdateDate(users.getUpdateDate());
        userResponse.setBranchName(users.getBranch().getName());
        userResponse.setBranchId(users.getBranch().getId());
        userResponse.setPassword(users.getPassword());
        users.getRoles().forEach(role -> {
            userResponse.getRoleName().add(role.getRoleName());
        });
        return userResponse;
    }

    @Override
    public UserResponse findUserById(Integer userId) throws ResourceNotFoundException {
        Optional<Users> usersOptional = userRepo.findUsersByIdAndActiveFlag(userId, 1);
        if (!usersOptional.isPresent()) {
            throw new ResourceNotFoundException("User with ID " + userId + " not found!");
        }

        List<Integer> roleIds = new ArrayList<>();
        Users users = usersOptional.get();
        UserResponse userResponse = new UserResponse();

        userResponse.setUserName(users.getUserName());
        userResponse.setId(users.getId());
        userResponse.setEmail(users.getEmail());
        userResponse.setName(users.getName());
        userResponse.setPhone(users.getPhone());
        userResponse.setCreateDate(users.getCreateDate());
        userResponse.setUpdateDate(users.getUpdateDate());
        userResponse.setBranchName(users.getBranch().getName());
        userResponse.setPassword(users.getPassword());
        users.getRoles().forEach(role -> {
            roleIds.add(role.getId());
        });
        userResponse.setRoleIds(roleIds);
        userResponse.setBranchId(users.getBranch().getId());
        users.getRoles().forEach(role -> {
            userResponse.getRoleName().add(role.getRoleName());
        });
        return userResponse;
    }

    @Override
    public void changePass(int userId, ChangePassRequest changePassRequest) throws ResourceNotFoundException, LogicException {
        Optional<Users> usersOptional = userRepo.findUsersByIdAndActiveFlag(userId, 1);
        if (!usersOptional.isPresent()) {
            throw new ResourceNotFoundException("User with ID " + userId + " not found!");
        }
        if (!changePassRequest.getNewPassOnce().equals(changePassRequest.getNewPassTwice())) {
            throw new LogicException("Re-entered password not match!", HttpStatus.BAD_REQUEST);
        }
        String passMDOld = HashingPassword.encrypt(changePassRequest.getOldPass());
        if (usersOptional.get().getPassword().toUpperCase().equals(passMDOld.toUpperCase())) {
            usersOptional.get().setPassword(HashingPassword.encrypt(changePassRequest.getNewPassOnce()));
            userRepo.save(usersOptional.get());
        } else {
            throw new LogicException("Wrong Password!", HttpStatus.BAD_REQUEST);
        }
    }
}
