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

import javax.validation.Valid;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping(value = "/api/issues", headers = "Accept=application/json")
@CrossOrigin(origins = "*", maxAge = 3600, allowedHeaders = "*")
public class IssueController {
    @Autowired
    private IssueService issueService;

    @Autowired
    private IssueDetailService issueDetailService;

    @GetMapping
    @HasRole({"ADMIN", "ADMIN_PTTK", "MANAGER", "STAFF"})
    public ResponseEntity<GenericResponse> findIssue(@RequestParam(name = "code", required = false) String code,
                                                     @RequestParam(name = "customerName", required = false) String customerName,
                                                     @RequestParam(name = "userName", required = false) String userName,
                                                     @RequestParam(name = "branchId", required = false) Integer branchId,
                                                     @RequestParam(name = "dateFrom", required = false) String dateFrom,
                                                     @RequestParam(name = "dateTo", required = false) String dateTo,
                                                     @RequestParam(name = "page", required = false) Integer page, @RequestParam(name = "size", required = false) Integer size) throws ParseException {
        Date dateFrom1 = null;
        Date dateTo1 = null;
        if (dateFrom != null || !dateFrom.isBlank()) {
            dateFrom1 = new SimpleDateFormat("yyyy-MM-dd").parse(dateFrom);
        }
        if (dateTo != null || !dateTo.isBlank()) {
            dateTo1 = new SimpleDateFormat("yyyy-MM-dd").parse(dateTo);
        }
        return new ResponseEntity<>(issueService.findIssueByFilter(code, customerName, userName, branchId, dateFrom1, dateTo1, page - 1, size), HttpStatus.OK);

    }

    @GetMapping("/{issueId}")
    @HasRole({"ADMIN", "ADMIN_PTTK", "MANAGER", "STAFF"})
    public ResponseEntity<IssueResponse> findById(@PathVariable Integer issueId) throws ResourceNotFoundException {
        return new ResponseEntity<>(issueService.findIssueById(issueId), HttpStatus.OK);
    }

    @PutMapping("/delete/{issueId}")
    @HasRole({"ADMIN", "ADMIN_PTTK", "MANAGER", "STAFF"})
    public ResponseEntity<String> deleteIssue(@PathVariable Integer issueId) throws ResourceNotFoundException {
        issueService.delete(issueId);
        return new ResponseEntity<>("Deleted!", HttpStatus.OK);
    }

    @PostMapping("/{issueId}/add-items")
    @HasRole({"ADMIN", "ADMIN_PTTK", "MANAGER", "STAFF"})
    public ResponseEntity<String> createIssueDetail(@PathVariable Integer issueId, @Valid @RequestBody IssueDetailRequest issueDetailRequest) throws ResourceNotFoundException, LogicException {
        issueDetailRequest.setIssueId(issueId);
        issueDetailService.save(issueDetailRequest);
        return new ResponseEntity<>("Created", HttpStatus.OK);
    }

    @GetMapping("/{issueCode}/issue-details")
    @HasRole({"ADMIN", "ADMIN_PTTK", "MANAGER", "STAFF"})
    public ResponseEntity<GenericResponse> findIssueDetail(@RequestParam(name = "priceTotalFrom", required = false) BigDecimal priceTotalFrom,
                                                           @RequestParam(name = "priceTotalTo", required = false) BigDecimal priceTotalTo,
                                                           @RequestParam(name = "imei", required = false) String imei,
                                                           @PathVariable(name = "issueCode", required = false) String issueCode,
                                                           @RequestParam(name = "issueCodeParam", required = false) String issueCodeParam,
                                                           @RequestParam(name = "productInfo", required = false) String productInfo,
                                                           @RequestParam(name = "branchId", required = false) Integer branchId,
                                                           @RequestParam(name = "page", required = false) Integer page, @RequestParam(name = "size", required = false) Integer size) {
        if (issueCodeParam == null) {
            issueCodeParam = issueCode;
        } else {
            issueCode = issueCodeParam;
        }
        return new ResponseEntity<>(issueDetailService.findIssueDetailByFilter(priceTotalFrom, priceTotalTo, imei, issueCode, productInfo, branchId, page - 1, size), HttpStatus.OK);

    }
}
