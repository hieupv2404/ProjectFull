package com.nuce.group3.service.impl;

import com.nuce.group3.controller.ResourceNotFoundException;
import com.nuce.group3.controller.dto.request.RoleRequest;
import com.nuce.group3.controller.dto.response.GenericResponse;
import com.nuce.group3.data.model.Role;
import com.nuce.group3.data.repo.RoleRepo;
import com.nuce.group3.exception.LogicException;
import com.nuce.group3.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(rollbackFor = Exception.class)
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleRepo roleRepo;

    @Override
    public GenericResponse getAll(Integer page, Integer size) {
        if (page == null) page = 0;
        if (size == null) size = 5;
        List<Role> roles = roleRepo.getAllRoles(PageRequest.of(page, size));
        return new GenericResponse(roles.size(), roles);
    }

    @Override
    public GenericResponse findRoleByFilter(String name, Integer page, Integer size) {
        if (page == null) page = 0;
        if (size == null) size = 5;
        List<Role> roles = roleRepo.findRoleByFilter(name, PageRequest.of(page, size));
        return new GenericResponse(roleRepo.findRoleByFilter(name, PageRequest.of(page, 1000)).size(), roles);
    }


    @Override
    public void save(RoleRequest roleRequest) throws LogicException, ResourceNotFoundException {
        Optional<Role> roleOptinal = roleRepo.findRoleByRoleNameAndActiveFlag(roleRequest.getName(), 1);
        if (roleOptinal.isPresent()) {
            throw new LogicException("Role with name: " + roleRequest.getName() + " existed!", HttpStatus.BAD_REQUEST);
        }
        Role role = new Role();
        role.setRoleName(roleRequest.getName());
        role.setDescription(roleRequest.getDescription());
        role.setActiveFlag(1);
        role.setCreateDate(new Date());
        role.setUpdateDate(new Date());
        roleRepo.save(role);
    }

    @Override
    public Role edit(Integer RoleId, RoleRequest roleRequest) throws ResourceNotFoundException, LogicException {
        Optional<Role> roleOptional = roleRepo.findRoleByIdAndActiveFlag(RoleId, 1);
        if (!roleOptional.isPresent()) {
            throw new ResourceNotFoundException("Role with ID: " + RoleId + " not found!");
        }
        Optional<Role> roleOptionalByName = roleRepo.findRoleByRoleNameAndActiveFlag(roleRequest.getName(), 1);
        if (!roleRequest.getName().equals(roleOptional.get().getRoleName()) && roleOptionalByName.isPresent()) {
            throw new LogicException("Role with name: " + roleRequest.getName() + " is existed!", HttpStatus.BAD_REQUEST);
        }
        Role role = roleOptional.get();
        role.setRoleName(roleRequest.getName());
        role.setDescription(roleRequest.getDescription());
        role.setUpdateDate(new Date());
        return roleRepo.save(role);
    }

    @Override
    public void delete(Integer roleId) throws ResourceNotFoundException {
        Optional<Role> roleOptional = roleRepo.findRoleByIdAndActiveFlag(roleId, 1);
        if (!roleOptional.isPresent()) {
            throw new ResourceNotFoundException("Role with ID: " + roleId + " not found!");
        }
        roleOptional.get().setActiveFlag(0);
        roleRepo.save(roleOptional.get());
    }

    @Override
    public Role findRoleById(Integer roleId) throws ResourceNotFoundException {
        if (roleId == null) {
            throw new ResourceNotFoundException("Role ID is null");
        }
        Optional<Role> RoleOptional = roleRepo.findRoleByIdAndActiveFlag(roleId, 1);
        if (!RoleOptional.isPresent()) {
            throw new ResourceNotFoundException("Role with ID: " + roleId + " not found!");
        }
        return RoleOptional.get();
    }
}
