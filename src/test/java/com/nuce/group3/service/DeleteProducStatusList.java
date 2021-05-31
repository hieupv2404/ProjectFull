package com.nuce.group3.service;

import com.nuce.group3.controller.ResourceNotFoundException;
import com.nuce.group3.data.model.ProductStatusDetail;
import com.nuce.group3.data.model.ProductStatusList;
import com.nuce.group3.data.repo.ProductStatusDetailRepo;
import com.nuce.group3.data.repo.ProductStatusListRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
//@DataJpaTest
class DeleteProductStatusList {
    @Autowired
    private ProductStatusListRepo productStatusListRepo;

    @Autowired
    private ProductStatusDetailRepo productStatusDetailRepo;

    @Transactional
    @Test
    void deleteProductStatusList() throws ResourceNotFoundException {
        Optional<ProductStatusList> productStatusList = productStatusListRepo.findProductStatusListByIdAndActiveFlag(25, 1);
        if (!productStatusList.isPresent()) {
            throw new ResourceNotFoundException("Not found");
        }
        productStatusList.get().setActiveFlag(0);
        productStatusListRepo.save(productStatusList.get());
    }

    @Transactional
    @Test
    void deleteProductStatusDetail() throws ResourceNotFoundException {
        Optional<ProductStatusDetail> productStatusDetail = productStatusDetailRepo.findProductStatusDetailByIdAndActiveFlag(100, 1);
        if (!productStatusDetail.isPresent()) {
            throw new ResourceNotFoundException("Not found");
        } else {
            System.out.println("Done");
        }
    }

}
