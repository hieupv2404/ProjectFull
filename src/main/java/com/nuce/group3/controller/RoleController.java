package com.nuce.group3.controller;

import com.nuce.group3.controller.dto.request.RoleRequest;
import com.nuce.group3.controller.dto.response.GenericResponse;
import com.nuce.group3.data.model.Role;
import com.nuce.group3.exception.LogicException;
import com.nuce.group3.interceptor.HasRole;
import com.nuce.group3.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/roles", headers = "Accept=application/json")
@CrossOrigin(origins = "*", maxAge = 3600, allowedHeaders = "*")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @GetMapping
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<GenericResponse> findRole(@RequestParam(name = "name", required = false) String name,
                                                    @RequestParam(name = "page", required = false) Integer page, @RequestParam(name = "size", required = false) Integer size) {
        return new ResponseEntity<>(roleService.findRoleByFilter(name, page - 1, size), HttpStatus.OK);

    }

    @PostMapping
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<String> createRole(@Valid @RequestBody RoleRequest roleRequest) throws ResourceNotFoundException, LogicException {
        roleService.save(roleRequest);
        return new ResponseEntity<>("Created", HttpStatus.OK);
    }

    @GetMapping("/{roleId}")
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<Role> findById(@PathVariable Integer roleId) throws ResourceNotFoundException {
        return new ResponseEntity<>(roleService.findRoleById(roleId), HttpStatus.OK);
    }

    @PutMapping("/edit/{roleId}")
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<Role> editRole(@PathVariable Integer roleId, @Valid @RequestBody RoleRequest roleRequest) throws ResourceNotFoundException, LogicException {
        return new ResponseEntity<>(roleService.edit(roleId, roleRequest), HttpStatus.OK);
    }

    @PutMapping("/delete/{roleId}")
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<String> deleteRole(@PathVariable Integer roleId) throws ResourceNotFoundException, LogicException {
        roleService.delete(roleId);
        return new ResponseEntity<>("Deleted!", HttpStatus.OK);
    }
}
