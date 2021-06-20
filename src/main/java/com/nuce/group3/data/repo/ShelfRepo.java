package com.nuce.group3.data.repo;

import com.nuce.group3.data.model.Shelf;
import com.nuce.group3.utils.Constant;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ShelfRepo  extends JpaRepository<Shelf, Integer> {

    @Cacheable(cacheNames = Constant.CACHE_FIND_SHELF_BY_ID)
    Optional<Shelf> findShelfByIdAndActiveFlag(int shelfId, int activeFlag);

    Optional<Shelf> findShelfByNameAndActiveFlag(String name, int activeFlag);

    List<Shelf> findShelfByActiveFlag(int activeFlag, Pageable pageable);

    @Query(value = "select s.*" +
            " from shelf s where s.active_flag=1 and (:name is null or s.name like %:name%)" +
            " and (:qtyFrom is null or s.qty >= :qtyFrom) and (:qtyTo is null or s.qty <= :qtyTo)" +
            " and (:qtyRestFrom is null or s.total-s.qty >= :qtyRestFrom) and (:qtyRestTo is null or s.total-s.qty <= :qtyRestTo)" +
            " and (:branchName is null or s.branch_id in (select b.id from branch b where b.active_flag=1 and b.name like %:branchName%))", nativeQuery = true)
    List<Shelf> findShelfByFilter(@Param(value = "name") String name,
                                  @Param(value = "qtyFrom") Integer qtyFrom, @Param(value = "qtyTo") Integer qtyTo,
                                  @Param(value = "qtyRestFrom") Integer qtyRestFrom, @Param(value = "qtyRestTo") Integer qtyRestTo,
                                  @Param(value = "branchName") String branchName, Pageable pageable);
    
}
