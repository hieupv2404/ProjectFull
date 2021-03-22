package com.nuce.group3.data.repo;

import com.nuce.group3.data.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepo extends JpaRepository<Category, Integer> {
    List<Category> getAllCategoryByActiveFlag(int activeFlag);

    @Query(value = "select c.*" +
            " from category c where c.active_flag=1 and (:name is null or c.name like %:name%)" +
            " and (:code is null or c.code like %:code%)", nativeQuery = true)
    List<Category> findCategoryByFilter(@Param(value = "name") String name, @Param(value = "code") String code);

    Optional<Category> findCategoryByActiveFlagAndCode(int activeFlag, String code);

    Optional<Category> findCategoryByActiveFlagAndId(int activeFlag, Integer id);
}
