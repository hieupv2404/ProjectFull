package com.nuce.group3.service.impl;

import com.nuce.group3.controller.ResourceNotFoundException;
import com.nuce.group3.controller.dto.request.ShelfRequest;
import com.nuce.group3.controller.dto.response.ShelfResponse;
import com.nuce.group3.data.model.Branch;
import com.nuce.group3.data.model.Shelf;
import com.nuce.group3.data.repo.BranchRepo;
import com.nuce.group3.data.repo.ShelfRepo;
import com.nuce.group3.exception.LogicException;
import com.nuce.group3.service.ShelfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ShelfServiceImpl implements ShelfService {
    @Autowired
    private ShelfRepo shelfRepo;

    @Autowired
    private BranchRepo branchRepo;

    @Override
    public List<ShelfResponse> getAll(Integer page, Integer size) {
        if (page == null) page = 0;
        if (size == null) size = 5;
        List<ShelfResponse> shelfResponses = new ArrayList<>();
        shelfRepo.findShelfByActiveFlag(1, PageRequest.of(page, size)).forEach(shelf -> {
            ShelfResponse shelfResponse = ShelfResponse.builder()
                    .id(shelf.getId())
                    .name(shelf.getName())
                    .description(shelf.getDescription())
                    .total(shelf.getTotal())
                    .qty(shelf.getQty())
                    .qtyRest(shelf.getTotal() - shelf.getQty())
                    .createDate(shelf.getCreateDate())
                    .updateDate(shelf.getUpdateDate())
                    .branchName(shelf.getBranch().getName())
                    .build();
            shelfResponses.add(shelfResponse);
        });
        return shelfResponses;
    }

    @Override
    public List<ShelfResponse> findShelfByFilter(String name, int qtyFrom, int qtyTo, int qtyRestFrom, int qtyRestTo, String branchName, Integer page, Integer size) {
        if (page == null) page = 0;
        if (size == null) size = 5;
        List<ShelfResponse> shelfResponses = new ArrayList<>();
        shelfRepo.findShelfByFilter(name, qtyFrom, qtyTo, qtyRestFrom, qtyRestTo, branchName, PageRequest.of(page, size)).forEach(shelf -> {
            ShelfResponse shelfResponse = ShelfResponse.builder()
                    .id(shelf.getId())
                    .name(shelf.getName())
                    .description(shelf.getDescription())
                    .total(shelf.getTotal())
                    .qty(shelf.getQty())
                    .qtyRest(shelf.getTotal() - shelf.getQty())
                    .createDate(shelf.getCreateDate())
                    .updateDate(shelf.getUpdateDate())
                    .branchName(shelf.getBranch().getName())
                    .build();
            shelfResponses.add(shelfResponse);
        });
        return shelfResponses;
    }

    @Override
    public void save(ShelfRequest shelfRequest, int branchId) throws LogicException, ResourceNotFoundException {
        Optional<Shelf> shelfOptional = shelfRepo.findShelfByNameAndActiveFlag(shelfRequest.getName(), 1);
        if (shelfOptional.isPresent()) {
            throw new LogicException("Shelf with name: " + shelfRequest.getName() + " existed!", HttpStatus.BAD_REQUEST);
        }

        Optional<Branch> branchOptional = branchRepo.findBranchByIdAndActiveFlag(branchId, 1);
        if (!branchOptional.isPresent()) {
            throw new ResourceNotFoundException("Branch with ID: " + shelfRequest.getBranchId() + " not found!");
        }

        Shelf shelf = new Shelf();
        shelf.setName(shelfRequest.getName());
        shelf.setBranch(branchOptional.get());
        shelf.setDescription(shelfRequest.getDescription());
        shelf.setTotal(shelfRequest.getTotal());
        shelf.setQty(0);
        shelf.setActiveFlag(1);
        shelf.setCreateDate(new Date());
        shelf.setUpdateDate(new Date());
        shelfRepo.save(shelf);
    }

    @Override
    public ShelfResponse edit(Integer shelfId, ShelfRequest shelfRequest) throws ResourceNotFoundException, LogicException {
        Optional<Shelf> shelfOptional = shelfRepo.findShelfByIdAndActiveFlag(shelfId, 1);
        if (!shelfOptional.isPresent()) {
            throw new ResourceNotFoundException("Shelf with ID: " + shelfId + " not found!");
        }

        Optional<Branch> branchOptional = branchRepo.findBranchByIdAndActiveFlag(shelfRequest.getBranchId(), 1);
        if (!branchOptional.isPresent()) {
            throw new ResourceNotFoundException("Branch with ID: " + shelfRequest.getBranchId() + " not found!");
        }
        Shelf shelf = shelfOptional.get();
        shelf.setName(shelfRequest.getName());
        shelf.setBranch(branchOptional.get());
        shelf.setDescription(shelfRequest.getDescription());
        shelf.setTotal(shelfRequest.getTotal());
        shelf.setActiveFlag(1);
        shelf.setUpdateDate(new Date());
        shelfRepo.save(shelf);
       return ShelfResponse.builder()
                .id(shelfId)
                .name(shelf.getName())
                .description(shelf.getDescription())
                .qty(shelf.getQty())
                .total(shelf.getTotal())
                .qtyRest(shelf.getTotal() - shelf.getQty())
                .createDate(shelf.getCreateDate())
                .updateDate(shelf.getUpdateDate())
                .branchName(branchOptional.get().getName())
                .build();
    }

    @Override
    public void delete(Integer shelfId) throws ResourceNotFoundException {
        Optional<Shelf> shelfOptional = shelfRepo.findShelfByIdAndActiveFlag(shelfId, 1);
        if (!shelfOptional.isPresent()) {
            throw new ResourceNotFoundException("Shelf with ID: " + shelfId + " not found!");
        }
        shelfOptional.get().setActiveFlag(0);
        shelfRepo.save(shelfOptional.get());
    }

    @Override
    public ShelfResponse findShelfById(Integer shelfId) throws ResourceNotFoundException {
        if (shelfId == null) {
            throw new ResourceNotFoundException("Shelf ID is null");
        }
        Optional<Shelf> shelfOptional = shelfRepo.findShelfByIdAndActiveFlag(shelfId, 1);
        if (!shelfOptional.isPresent()) {
            throw new ResourceNotFoundException("Shelf with ID: " + shelfId + " not found!");
        }
        Shelf shelf = shelfOptional.get();
        return ShelfResponse.builder()
                .id(shelf.getId())
                .name(shelf.getName())
                .description(shelf.getDescription())
                .qty(shelf.getQty())
                .total(shelf.getTotal())
                .qtyRest(shelf.getTotal() - shelf.getQty())
                .createDate(shelf.getCreateDate())
                .updateDate(shelf.getUpdateDate())
                .branchName(shelf.getBranch().getName())
                .build();
    }
}
