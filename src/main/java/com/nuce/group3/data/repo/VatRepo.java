package com.nuce.group3.data.repo;

import com.nuce.group3.data.model.Vat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VatRepo extends JpaRepository<Vat, Integer> {
    Optional<Vat> findVatByCodeAndActiveFlag(String code, int activeFlag);

    List<Vat> findVatByActiveFlag(int activeFlag, Pageable pageable);

    @Query(value = "select v.*" +
            " from vat v where v.active_flag=1 and (:code is null or v.code like %:code%)" +
            " and (:tax is null or v.tax like %:tax%) " +
            " and (:supplierName is null or v.supplier_id in (select s.id from supplier s where s.active_flag =1 and s.name like %:supplierName%))" +
            " and (:userName is null or v.user_id in (select u.id from users u where u.active_flag =1 and u.name like %:userName%)) " +
            " and (:branchName is null or v.branch_id in (select b.id from branch b where b.active_flag=1 and b.name like %:branchName%)) ", nativeQuery = true)
    List<Vat> findVatByFilter(@Param(value = "code") String code, @Param(value = "tax") String tax, @Param(value = "supplierName") String supplierName, @Param(value = "userName") String userName, @Param(value = "branchName") String branchName, Pageable pageable);

    Optional<Vat> findVatByIdAndActiveFlag(int id, int activeFlag);

}
