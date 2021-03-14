//package com.nuce.group3.dao;
//
//import com.nuce.group3.data.model.Paging;
//
//import java.io.Serializable;
//import java.util.List;
//import java.util.Map;
//
//public interface BaseDAO<E> {
//    List<E> findAll(String queryStr, Map<String, Object> mapParams, Paging paging);
//
//    E findById(Class<E> e, Serializable id);
//
//    List<E> findByProperty(String property, Object value);
//
//    void save(E instance);
//
//    int insert(E instance);
//
//    void update(E instance);
//
//    void deleteDone(E instance);
//}
