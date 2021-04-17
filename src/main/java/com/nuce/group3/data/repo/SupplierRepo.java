package com.nuce.group3.data.repo;

import com.nuce.group3.data.model.ProductDetail;
import com.nuce.group3.data.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SupplierRepo  extends JpaRepository<Supplier, Integer> {
    Optional<Supplier> findSupplierByIdAndActiveFlag(int supplierId, int activeFlag);
}
