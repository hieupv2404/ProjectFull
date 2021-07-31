package com.nuce.group3.controller;

import com.nuce.group3.controller.dto.response.GenericResponse;
import com.nuce.group3.controller.dto.response.IssueDetailResponse;
import com.nuce.group3.interceptor.HasRole;
import com.nuce.group3.service.IssueDetailExportService;
import com.nuce.group3.service.IssueDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping(value = "/api/issue-details", headers = "Accept=application/json")
@CrossOrigin(origins = "*", maxAge = 3600, allowedHeaders = "*")
public class IssueDetailController {
    @Autowired
    private IssueDetailService issueDetailService;

    @Autowired
    private IssueDetailExportService issueDetailExportService;

    @GetMapping
    @HasRole({"ADMIN", "ADMIN_PTTK", "MANAGER", "STAFF"})
    public ResponseEntity<GenericResponse> findIssueDetail(@RequestParam(name = "priceTotalFrom", required = false) BigDecimal priceTotalFrom,
                                                           @RequestParam(name = "priceTotalTo", required = false) BigDecimal priceTotalTo,
                                                           @RequestParam(name = "imei", required = false) String imei,
                                                           @RequestParam(name = "issueCode", required = false) String issueCode,
                                                           @RequestParam(name = "productInfo", required = false) String productInfo,
                                                           @RequestParam(name = "page", required = false) Integer page, @RequestParam(name = "size", required = false) Integer size) {
        return new ResponseEntity<>(issueDetailService.findIssueDetailByFilter(priceTotalFrom, priceTotalTo, imei, issueCode, productInfo, page - 1, size), HttpStatus.OK);

    }

    @GetMapping("/{issueDetailId}")
    @HasRole({"ADMIN", "ADMIN_PTTK", "MANAGER", "STAFF"})
    public ResponseEntity<IssueDetailResponse> findById(@PathVariable Integer issueDetailId) throws ResourceNotFoundException {
        return new ResponseEntity<>(issueDetailService.findIssueDetailById(issueDetailId), HttpStatus.OK);
    }

    @PutMapping("/delete/{issueDetailId}")
    @HasRole({"ADMIN", "ADMIN_PTTK", "MANAGER", "STAFF"})
    public ResponseEntity<String> deleteIssueDetail(@PathVariable Integer issueDetailId) throws ResourceNotFoundException {
        issueDetailService.delete(issueDetailId, false);
        return new ResponseEntity<>("Deleted!", HttpStatus.OK);
    }

    @GetMapping("/get-file-report")
    @HasRole({"ADMIN", "ADMIN_PTTK", "MANAGER", "STAFF"})
    public ByteArrayResource getFileReportTest(@RequestParam(name = "priceTotalFrom", required = false) BigDecimal priceTotalFrom,
                                               @RequestParam(name = "priceTotalTo", required = false) BigDecimal priceTotalTo,
                                               @RequestParam(name = "imei", required = false) String imei,
                                               @RequestParam(name = "issueCode", required = false) String issueCode,
                                               @RequestParam(name = "productInfo", required = false) String productInfo) throws IOException {
        List<IssueDetailResponse> issueDetailResponses = issueDetailService.findIssueDetailForExport(priceTotalFrom, priceTotalTo, imei, issueCode, productInfo);
        return issueDetailExportService.exportReport(issueDetailResponses);
    }

    @PostMapping("/export-excel")
    @HasRole({"ADMIN", "ADMIN_PTTK", "MANAGER", "STAFF"})
    public ResponseEntity<byte[]> exportToExcel(@RequestParam(name = "priceTotalFrom", required = false) BigDecimal priceTotalFrom,
                                                @RequestParam(name = "priceTotalTo", required = false) BigDecimal priceTotalTo,
                                                @RequestParam(name = "imei", required = false) String imei,
                                                @RequestParam(name = "issueCode", required = false) String issueCode,
                                                @RequestParam(name = "productInfo", required = false) String productInfo) throws IOException {
        List<IssueDetailResponse> issueDetailResponses = issueDetailService.findIssueDetailForExport(priceTotalFrom, priceTotalTo, imei, issueCode, productInfo);
        ByteArrayResource resource = issueDetailExportService.exportReport(issueDetailResponses);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(resource.getByteArray());
    }

}
