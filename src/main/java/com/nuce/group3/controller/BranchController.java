package com.nuce.group3.controller;

import com.nuce.group3.controller.dto.request.BranchRequest;
import com.nuce.group3.controller.dto.response.BranchResponse;
import com.nuce.group3.controller.dto.response.GenericResponse;
import com.nuce.group3.exception.LogicException;
import com.nuce.group3.interceptor.HasRole;
import com.nuce.group3.service.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/branches", headers = "Accept=application/json")
@CrossOrigin(origins = "*", maxAge = 3600, allowedHeaders = "*")
public class BranchController {
    @Autowired
    private BranchService branchService;

    @GetMapping
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<GenericResponse> findBranchByFilter(@RequestParam(name = "name", required = false) String name,
                                                              @RequestParam(name = "code", required = false) String code,
                                                              @RequestParam(name = "page", required = false) Integer page, @RequestParam(name = "size", required = false) Integer size) {
        return new ResponseEntity<>(branchService.findBranchByFilter(name, code, page, size), HttpStatus.OK);
    }

    @PostMapping
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<String> createBranch(@RequestBody @Valid BranchRequest branchRequest) throws LogicException {
        branchService.save(branchRequest);
        return new ResponseEntity<>("Created!", HttpStatus.OK);
    }

    @GetMapping("/{branchId}")
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<BranchResponse> findBranchById(@PathVariable Integer branchId) throws ResourceNotFoundException {
        return new ResponseEntity<>(branchService.findById(branchId), HttpStatus.OK);
    }


    @PutMapping("/edit/{branchId}")
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<BranchResponse> updateBranch(@RequestBody @Valid BranchRequest branchRequest,
                                                       @PathVariable Integer branchId) throws LogicException, ResourceNotFoundException {
        if (branchId == null)
            throw new LogicException("Branch is null", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(branchService.edit(branchId, branchRequest), HttpStatus.OK);
    }

    @PutMapping("/delete/{branchId}")
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<String> deleteBranch(@PathVariable Integer branchId) throws ResourceNotFoundException, LogicException {
        if (branchId == null)
            throw new LogicException("Branch is null", HttpStatus.BAD_REQUEST);
        branchService.delete(branchId);
        return new ResponseEntity<>("Deleted branch id " + branchId, HttpStatus.OK);
    }
}
