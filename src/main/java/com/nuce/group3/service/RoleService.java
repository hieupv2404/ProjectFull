package com.nuce.group3.service;

import com.nuce.group3.controller.ResourceNotFoundException;
import com.nuce.group3.controller.dto.request.RoleRequest;
import com.nuce.group3.controller.dto.response.GenericResponse;
import com.nuce.group3.data.model.Role;
import com.nuce.group3.exception.LogicException;

public interface RoleService {
    GenericResponse getAll(Integer page, Integer size);

    GenericResponse findRoleByFilter(String name, Integer page, Integer size);

    void save(RoleRequest RoleRequest) throws LogicException, ResourceNotFoundException;

    Role edit(Integer RoleId, RoleRequest RoleRequest) throws ResourceNotFoundException, LogicException;

    void delete(Integer RoleId) throws ResourceNotFoundException;

    Role findRoleById(Integer RoleId) throws ResourceNotFoundException;
}
