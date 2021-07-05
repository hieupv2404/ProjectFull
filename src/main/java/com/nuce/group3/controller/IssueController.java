package com.nuce.group3.controller;

import com.nuce.group3.controller.dto.request.IssueDetailRequest;
import com.nuce.group3.controller.dto.response.GenericResponse;
import com.nuce.group3.controller.dto.response.IssueResponse;
import com.nuce.group3.exception.LogicException;
import com.nuce.group3.interceptor.HasRole;
import com.nuce.group3.service.IssueDetailService;
import com.nuce.group3.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;

@RestController
@RequestMapping(value = "/api/issues", headers = "Accept=application/json")
@CrossOrigin(origins = "*", maxAge = 3600, allowedHeaders = "*")
public class IssueController {
    @Autowired
    private IssueService issueService;

    @Autowired
    private IssueDetailService issueDetailService;

    @GetMapping
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<GenericResponse> findIssue(@RequestParam(name = "code", required = false) String code,
                                                     @RequestParam(name = "customerName", required = false) String customerName,
                                                     @RequestParam(name = "userName", required = false) String userName,
                                                     @RequestParam(name = "branchId", required = false) Integer branchId,
                                                     @RequestParam(name = "page", required = false) Integer page, @RequestParam(name = "size", required = false) Integer size,
                                                     HttpServletRequest request) {
        return new ResponseEntity<>(issueService.findIssueByFilter(code, customerName, userName, branchId, page - 1, size), HttpStatus.OK);

    }

    @GetMapping("/{issueId}")
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<IssueResponse> findById(@PathVariable Integer issueId) throws ResourceNotFoundException {
        return new ResponseEntity<>(issueService.findIssueById(issueId), HttpStatus.OK);
    }

    @PutMapping("/delete/{issueId}")
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<String> deleteIssue(@PathVariable Integer issueId) throws ResourceNotFoundException {
        issueService.delete(issueId);
        return new ResponseEntity<>("Deleted!", HttpStatus.OK);
    }

    @PostMapping("/{issueId}/add-items")
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<String> createIssueDetail(@PathVariable Integer issueId, @Valid @RequestBody IssueDetailRequest issueDetailRequest) throws ResourceNotFoundException, LogicException {
        issueDetailRequest.setIssueId(issueId);
        issueDetailService.save(issueDetailRequest);
        return new ResponseEntity<>("Created", HttpStatus.OK);
    }

    @GetMapping("/{issueCode}/issue-details")
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<GenericResponse> findIssueDetail(@RequestParam(name = "priceTotalFrom", required = false) BigDecimal priceTotalFrom,
                                                           @RequestParam(name = "priceTotalTo", required = false) BigDecimal priceTotalTo,
                                                           @PathVariable(name = "issueCode", required = false) String issueCode,
                                                           @RequestParam(name = "productInfo", required = false) String productInfo,
                                                           @RequestParam(name = "page", required = false) Integer page, @RequestParam(name = "size", required = false) Integer size) {
        return new ResponseEntity<>(issueDetailService.findIssueDetailByFilter(priceTotalFrom, priceTotalTo, issueCode, productInfo, page - 1, size), HttpStatus.OK);

    }
}
