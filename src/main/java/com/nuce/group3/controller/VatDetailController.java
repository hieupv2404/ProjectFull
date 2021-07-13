package com.nuce.group3.controller;

import com.nuce.group3.controller.dto.request.VatDetailRequest;
import com.nuce.group3.controller.dto.response.GenericResponse;
import com.nuce.group3.controller.dto.response.VatDetailResponse;
import com.nuce.group3.exception.LogicException;
import com.nuce.group3.interceptor.HasRole;
import com.nuce.group3.service.VatDetailExportService;
import com.nuce.group3.service.VatDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping(value = "/api/vat-details", headers = "Accept=application/json")
@CrossOrigin(origins = "*", maxAge = 3600, allowedHeaders = "*")
public class VatDetailController {
    @Autowired
    private VatDetailService vatDetailService;

    @Autowired
    private VatDetailExportService vatDetailExportService;

    @GetMapping
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<GenericResponse> findVatDetail(@RequestParam(name = "priceTotalFrom", required = false) BigDecimal priceTotalFrom,
                                                         @RequestParam(name = "priceTotalTo", required = false) BigDecimal priceTotalTo,
                                                         @RequestParam(name = "vatCode", required = false) String vatCode,
                                                         @RequestParam(name = "productInfo", required = false) String productInfo,
                                                         @RequestParam(name = "page", required = false) Integer page, @RequestParam(name = "size", required = false) Integer size) {
        return new ResponseEntity<>(vatDetailService.findVatDetailByFilter(priceTotalFrom, priceTotalTo, vatCode, productInfo, page - 1, size), HttpStatus.OK);

    }

    @GetMapping("/{vatDetailId}")
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<VatDetailResponse> findById(@PathVariable Integer vatDetailId) throws ResourceNotFoundException {
        return new ResponseEntity<>(vatDetailService.findVatDetailById(vatDetailId), HttpStatus.OK);
    }

    @PutMapping("/edit/{vatDetailId}")
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<VatDetailResponse> editVatDetail(@PathVariable Integer vatDetailId, @Valid @RequestBody VatDetailRequest vatDetailRequest) throws ResourceNotFoundException, LogicException {
        return new ResponseEntity<>(vatDetailService.edit(vatDetailId, vatDetailRequest), HttpStatus.OK);
    }

    @PutMapping("/delete/{vatDetailId}")
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<String> deleteVatDetail(@PathVariable Integer vatDetailId) throws ResourceNotFoundException {
        vatDetailService.delete(vatDetailId, false);
        return new ResponseEntity<>("Deleted!", HttpStatus.OK);
    }

    @GetMapping("/get-file-report")
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ByteArrayResource getFileReportTest(@RequestParam(name = "priceTotalFrom", required = false) BigDecimal priceTotalFrom,
                                               @RequestParam(name = "priceTotalTo", required = false) BigDecimal priceTotalTo,
                                               @RequestParam(name = "vatCode", required = false) String vatCode,
                                               @RequestParam(name = "productInfo", required = false) String productInfo) throws IOException {
        List<VatDetailResponse> vatDetailResponses = vatDetailService.findVatDetailToExport(priceTotalFrom, priceTotalTo, vatCode, productInfo);
        return vatDetailExportService.exportReport(vatDetailResponses);
    }

    @PostMapping("/export-excel")
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<byte[]> exportToExcel(@RequestParam(name = "priceTotalFrom", required = false) BigDecimal priceTotalFrom,
                                                @RequestParam(name = "priceTotalTo", required = false) BigDecimal priceTotalTo,
                                                @RequestParam(name = "vatCode", required = false) String vatCode,
                                                @RequestParam(name = "productInfo", required = false) String productInfo) throws IOException {
        List<VatDetailResponse> vatDetailResponses = vatDetailService.findVatDetailToExport(priceTotalFrom, priceTotalTo, vatCode, productInfo);
        ByteArrayResource resource = vatDetailExportService.exportReport(vatDetailResponses);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(resource.getByteArray());
    }

}
