package com.nuce.group3.service.impl;

import com.nuce.group3.controller.ResourceNotFoundException;
import com.nuce.group3.controller.dto.response.GenericResponse;
import com.nuce.group3.controller.dto.response.IssueResponse;
import com.nuce.group3.data.model.Customer;
import com.nuce.group3.data.model.Issue;
import com.nuce.group3.data.model.Users;
import com.nuce.group3.data.repo.CustomerRepo;
import com.nuce.group3.data.repo.IssueDetailRepo;
import com.nuce.group3.data.repo.IssueRepo;
import com.nuce.group3.data.repo.UserRepo;
import com.nuce.group3.exception.LogicException;
import com.nuce.group3.service.IssueDetailService;
import com.nuce.group3.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class IssueServiceImpl implements IssueService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private IssueRepo issueRepo;

    @Autowired
    private IssueDetailRepo issueDetailRepo;

    @Autowired
    private IssueDetailService issueDetailService;

    @Autowired
    private CustomerRepo customerRepo;

    @Override
    public List<IssueResponse> getAll(Integer page, Integer size) {
        List<IssueResponse> issueResponses = new ArrayList<>();
        if (page == null) page = 0;
        if (size == null) size = 5;
        issueRepo.findIssueByActiveFlag(1, PageRequest.of(page, size)).forEach(issue -> {
            IssueResponse issueResponse = IssueResponse.builder()
                    .id(issue.getId())
                    .code(issue.getCode())
                    .price(issue.getPrice())
                    .createDate(issue.getCreateDate())
                    .userName(issue.getUser().getName())
                    .customerName(issue.getCustomer().getName())
                    .updateDate(issue.getUpdateDate())
                    .build();
            issueResponses.add(issueResponse);
        });
        return issueResponses;
    }

    @Override
    public GenericResponse findIssueByFilter(String code, String customerName, String userName, Integer branchId, Date dateFrom, Date dateTo, Integer page, Integer size) {
        List<IssueResponse> issueResponses = new ArrayList<>();
        if (page == null) page = 0;
        if (size == null) size = 5;
        issueRepo.findIssueByFilter(code, customerName, userName, branchId, dateFrom, dateTo, PageRequest.of(page, size)).forEach(issue -> {
            IssueResponse issueResponse = IssueResponse.builder()
                    .id(issue.getId())
                    .code(issue.getCode())
                    .price(issue.getPrice())
                    .createDate(issue.getCreateDate())
                    .userName(issue.getUser().getName())
                    .customerName(issue.getCustomer().getName())
                    .updateDate(issue.getUpdateDate())
                    .build();
            issueResponses.add(issueResponse);
        });
        return new GenericResponse(issueResponses.size(), issueResponses);
    }

    @Override
    public IssueResponse findIssueById(Integer issueId) throws ResourceNotFoundException {
        if (issueId == null) {
            throw new ResourceNotFoundException("Id not found!");
        }
        Optional<Issue> issueOptional = issueRepo.findIssueByIdAndActiveFlag(issueId, 1);
        if (!issueOptional.isPresent()) {
            throw new ResourceNotFoundException("Issue with " + issueId + " not found!");
        }
        Issue issue = issueOptional.get();
        return IssueResponse.builder()
                .id(issue.getId())
                .code(issue.getCode())
                .price(issue.getPrice())
                .createDate(issue.getCreateDate())
                .userName(issue.getUser().getName())
                .customerName(issue.getCustomer().getName())
                .updateDate(issue.getUpdateDate())
                .build();
    }

    @Override
    public void save(int customerId, String userName) throws LogicException, ResourceNotFoundException {
        Optional<Customer> customerOptional = customerRepo.findCustomerByIdAndActiveFlag(customerId, 1);
        if (!customerOptional.isPresent()) {
            throw new ResourceNotFoundException("Customer with id " + customerId + " not found");
        }

        Optional<Users> usersOptional = userRepo.findUsersByUserName(userName);
        if (!usersOptional.isPresent()) {
            throw new ResourceNotFoundException("User with user name: " + userName + " not found");
        }

        Calendar calendar = Calendar.getInstance();
        long maxId = issueRepo.countIssue() + 1;
        Issue issue = new Issue();
        issue.setCode("ISSUE" + calendar.get(Calendar.DAY_OF_MONTH) + calendar.get(Calendar.MONTH + 1) + calendar.get(Calendar.YEAR) + maxId);
        issue.setCustomer(customerOptional.get());
        issue.setUser(usersOptional.get());
        issue.setActiveFlag(1);
        issue.setCreateDate(new Date());
        issue.setUpdateDate(new Date());
        issue.setPrice(new BigDecimal(0));
        issueRepo.save(issue);
    }

    @Override
    public void delete(Integer issueId) throws ResourceNotFoundException {
        Optional<Issue> issueOptional = issueRepo.findIssueByIdAndActiveFlag(issueId, 1);
        if (!issueOptional.isPresent()) {
            throw new ResourceNotFoundException("Issue with " + issueId + " not found!");
        }
        issueOptional.get().setActiveFlag(0);
        issueRepo.save(issueOptional.get());
        issueDetailRepo.findIssueDetailByIssue(issueId).forEach(issueDetail -> {
            try {
                issueDetailService.delete(issueDetail.getId(), true);
            } catch (ResourceNotFoundException resourceNotFoundException) {
                resourceNotFoundException.printStackTrace();
            }
        });
    }
}
