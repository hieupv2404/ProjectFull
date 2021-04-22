package com.nuce.group3.data.repo;

import com.nuce.group3.data.model.Customer;
import com.nuce.group3.data.model.Customer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CustomerRepo extends JpaRepository<Customer, Integer> {
    Optional<Customer> findCustomerByIdAndActiveFlag(int supplierId, int activeFlag);

    List<Customer> findCustomerByActiveFlag(int activeFlag, Pageable pageable);

    @Query(value = "select c.*" +
            " from customer c where c.active_flag=1 and (:name is null or c.name like %:name%)" +
            " and (:phone is null or c.phone like %:phone%) " +
            " and (:address is null or c.address like %:address%)", nativeQuery = true)
    List<Customer> findCustomerByFilter(@Param(value = "name") String name, @Param(value = "phone") String phone, @Param(value = "address") String address, Pageable pageable);

    Optional<Customer> findCustomerByNameAndActiveFlag(String name, int activeFlag);

}
