//package com.nuce.group3.service;
//
//import com.nuce.group3.controller.ResourceNotFoundException;
//import com.nuce.group3.controller.dto.request.RoleRequest;
//import com.nuce.group3.controller.dto.response.GenericResponse;
//import com.nuce.group3.controller.dto.response.RoleResponse;
//import com.nuce.group3.exception.LogicException;
//
//import java.util.List;
//
//public interface RoleService {
//    List<RoleResponse> getAll(Integer page, Integer size);
//
//    GenericResponse findRoleByFilter(String name, Integer qtyFrom, Integer qtyTo, Integer qtyRestFrom, Integer qtyRestTo, String branchName, Integer page, Integer size);
//
//    void save(RoleRequest RoleRequest, int branchId) throws LogicException, ResourceNotFoundException;
//
//    RoleResponse edit(Integer RoleId, RoleRequest RoleRequest) throws ResourceNotFoundException, LogicException;
//
//    void delete(Integer RoleId) throws ResourceNotFoundException;
//
//    RoleResponse findRoleById(Integer RoleId) throws ResourceNotFoundException;
//}
