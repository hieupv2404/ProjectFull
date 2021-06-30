package com.nuce.group3.controller;

import com.nuce.group3.controller.dto.request.ShelfRequest;
import com.nuce.group3.controller.dto.response.GenericResponse;
import com.nuce.group3.controller.dto.response.ShelfResponse;
import com.nuce.group3.exception.LogicException;
import com.nuce.group3.interceptor.HasRole;
import com.nuce.group3.service.ShelfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/shelves", headers = "Accept=application/json")
@CrossOrigin(origins = "*", maxAge = 3600, allowedHeaders = "*")
public class ShelfController {
    @Autowired
    private ShelfService shelfService;

    @GetMapping
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<GenericResponse> findShelf(@RequestParam(name = "name", required = false) String name,
                                                     @RequestParam(name = "qtyFrom", required = false) Integer qtyFrom,
                                                     @RequestParam(name = "qtyTo", required = false) Integer qtyTo,
                                                     @RequestParam(name = "qtyRestFrom", required = false) Integer qtyRestFrom,
                                                     @RequestParam(name = "qtyRestTo", required = false) Integer qtyRestTo,
                                                     @RequestParam(name = "branchName", required = false) String branchName,
                                                     @RequestParam(name = "page", required = false) Integer page, @RequestParam(name = "size", required = false) Integer size) {
        return new ResponseEntity<>(shelfService.findShelfByFilter(name, qtyFrom, qtyTo, qtyRestFrom, qtyRestTo, branchName, page, size), HttpStatus.OK);
    }

    @PostMapping
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<String> createShelf(@Valid @RequestBody ShelfRequest shelfRequest) throws ResourceNotFoundException, LogicException {
        shelfService.save(shelfRequest);
        return new ResponseEntity<>("Created", HttpStatus.OK);
    }

    @GetMapping("/{shelfId}")
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<ShelfResponse> findById(@PathVariable Integer shelfId) throws ResourceNotFoundException {
        return new ResponseEntity<>(shelfService.findShelfById(shelfId), HttpStatus.OK);
    }

    @PutMapping("/edit/{shelfId}")
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<ShelfResponse> editShelf(@PathVariable Integer shelfId, @Valid @RequestBody ShelfRequest shelfRequest) throws ResourceNotFoundException, LogicException {
        return new ResponseEntity<>(shelfService.edit(shelfId, shelfRequest), HttpStatus.OK);
    }

    @PutMapping("/delete/{shelfId}")
    @HasRole({"ADMIN", "ADMIN_PTTK"})
    public ResponseEntity<String> deleteShelf(@PathVariable Integer shelfId) throws ResourceNotFoundException, LogicException {
        shelfService.delete(shelfId);
        return new ResponseEntity<>("Deleted!", HttpStatus.OK);
    }
}
