package com.nuce.group3.data.repo;

import com.nuce.group3.data.model.ProductDetail;
import com.nuce.group3.data.model.Supplier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SupplierRepo  extends JpaRepository<Supplier, Integer> {
    Optional<Supplier> findSupplierByIdAndActiveFlag(int supplierId, int activeFlag);

    List<Supplier> findSupplierByActiveFlag(int activeFlag, Pageable pageable);

    @Query(value = "select s.*" +
            " from supplier s where s.active_flag=1 and (:name is null or s.name like %:name%)" +
            " and (:phone is null or s.phone like %:phone%) " +
            " and (:address is null or s.address like %:address%)", nativeQuery = true)
    List<Supplier> findSupplierByFilter(@Param(value = "name") String name, @Param(value = "phone") String phone, @Param(value = "address") String address, Pageable pageable);

    Optional<Supplier> findSupplierByNameAndActiveFlag(String name, int activeFlag);

}
