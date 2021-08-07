package com.nuce.group3.controller;

import com.nuce.group3.controller.dto.request.ProductStatusListRequest;
import com.nuce.group3.controller.dto.request.VatDetailRequest;
import com.nuce.group3.controller.dto.request.VatRequest;
import com.nuce.group3.controller.dto.response.GenericResponse;
import com.nuce.group3.controller.dto.response.VatResponse;
import com.nuce.group3.exception.LogicException;
import com.nuce.group3.interceptor.HasRole;
import com.nuce.group3.service.ProductStatusListService;
import com.nuce.group3.service.VatDetailService;
import com.nuce.group3.service.VatService;
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
@RequestMapping(value = "/api/vats", headers = "Accept=application/json")
@CrossOrigin(origins = "*", maxAge = 3600, allowedHeaders = "*")
public class VatController {
    @Autowired
    private VatService vatService;

    @Autowired
    private VatDetailService vatDetailService;

    @Autowired
    private ProductStatusListService productStatusListService;

    @GetMapping
    @HasRole({"ADMIN", "ADMIN_PTTK", "MANAGER", "STAFF"})
    public ResponseEntity<GenericResponse> findVat(@RequestParam(name = "code", required = false) String code,
                                                   @RequestParam(name = "tax", required = false) String tax,
                                                   @RequestParam(name = "supplierName", required = false) String supplierName,
                                                   @RequestParam(name = "userName", required = false) String userName,
                                                   @RequestParam(name = "branchId", required = false) Integer branchId,
                                                   @RequestParam(name = "dateFrom", required = false) String dateFrom,
                                                   @RequestParam(name = "dateTo", required = false) String dateTo,
                                                   @RequestParam(name = "page", required = false) Integer page, @RequestParam(name = "size", required = false) Integer size) throws ParseException {
        Date dateFrom1 = null;
        Date dateTo1 = null;
        if (dateFrom != null) {
            dateFrom1 = new SimpleDateFormat("yyyy-MM-dd").parse(dateFrom);
        }
        if (dateTo != null) {
            dateTo1 = new SimpleDateFormat("yyyy-MM-dd").parse(dateTo);
        }
        return new ResponseEntity<>(vatService.findVatByFilter(code, tax, supplierName, userName, branchId, dateFrom1, dateTo1, page - 1, size), HttpStatus.OK);

    }


    @GetMapping("/{vatId}")
    @HasRole({"ADMIN", "ADMIN_PTTK", "MANAGER", "STAFF"})
    public ResponseEntity<VatResponse> findById(@PathVariable Integer vatId) throws ResourceNotFoundException {
        return new ResponseEntity<>(vatService.findVatById(vatId), HttpStatus.OK);
    }

    @PutMapping("/edit/{vatId}")
    @HasRole({"ADMIN", "ADMIN_PTTK", "MANAGER"})
    public ResponseEntity<VatResponse> editVat(@PathVariable Integer vatId, @Valid @RequestBody VatRequest vatRequest) throws ResourceNotFoundException, LogicException {
        return new ResponseEntity<>(vatService.edit(vatId, vatRequest), HttpStatus.OK);
    }

    @PutMapping("/delete/{vatId}")
    @HasRole({"ADMIN", "ADMIN_PTTK", "MANAGER"})
    public ResponseEntity<String> deleteVat(@PathVariable Integer vatId) throws ResourceNotFoundException {
        vatService.delete(vatId);
        return new ResponseEntity<>("Deleted!", HttpStatus.OK);
    }

    @PostMapping("/{vatId}/add-items")
    @HasRole({"ADMIN", "ADMIN_PTTK", "MANAGER"})
    public ResponseEntity<String> createVatDetail(@PathVariable Integer vatId, @Valid @RequestBody VatDetailRequest vatDetailRequest) throws ResourceNotFoundException, LogicException {
        vatDetailRequest.setVatId(vatId);
        vatDetailService.save(vatDetailRequest);
        return new ResponseEntity<>("Created", HttpStatus.OK);
    }

    @GetMapping("/{vatCode}/vat-details")
    @HasRole({"ADMIN", "ADMIN_PTTK", "MANAGER", "STAFF"})
    public ResponseEntity<GenericResponse> findVatDetail(@RequestParam(name = "priceTotalFrom", required = false) BigDecimal priceTotalFrom,
                                                         @RequestParam(name = "priceTotalTo", required = false) BigDecimal priceTotalTo,
                                                         @PathVariable(name = "vatCode", required = false) String vatCode,
                                                         @RequestParam(name = "vatCodeParam", required = false) String vatCodeParam,
                                                         @RequestParam(name = "productInfo", required = false) String productInfo,
                                                         @RequestParam(name = "branchId", required = false) Integer branchId,

                                                         @RequestParam(name = "page", required = false) Integer page, @RequestParam(name = "size", required = false) Integer size) {
        if (vatCodeParam == null) {
            vatCodeParam = vatCode;
        } else {
            vatCode = vatCodeParam;
        }
        return new ResponseEntity<>(vatDetailService.findVatDetailByFilter(priceTotalFrom, priceTotalTo, vatCode, productInfo, branchId, page - 1, size), HttpStatus.OK);
    }

    @PostMapping("/{vatId}/add-products-list")
    @HasRole({"ADMIN", "ADMIN_PTTK", "MANAGER"})
    public ResponseEntity<String> createProductStatusList(@Valid @RequestBody ProductStatusListRequest productStatusListRequest, @PathVariable Integer vatId) throws ResourceNotFoundException, LogicException {
        productStatusListService.save(vatId, productStatusListRequest);
        return new ResponseEntity<>("Created", HttpStatus.OK);
    }


}
