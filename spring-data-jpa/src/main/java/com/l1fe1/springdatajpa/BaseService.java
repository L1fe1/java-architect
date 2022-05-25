package com.l1fe1.springdatajpa;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface BaseService<T,ID>{
    Class<T> getDomainClass();
    <S extends T> S save(S entity);
    <S extends T> List<S> saveAll(Iterable<S> entities);
    void delete(T entity);
    void deleteById(ID id);
    void deleteAll();
    void deleteAll(Iterable<? extends T> entities);
    void deleteInBatch(Iterable<T> entities);
    void deleteAllInBatch();
    T getOne(ID id);
    <S extends T> Optional<S> findOne(Example<S> example);
    Optional<T> findById(ID id);
    List<T> findAll();
    List<T> findAll(Sort sort);
    Page<T> findAll(Pageable pageable);
    <S extends T> List<S> findAll(Example<S> example);
    <S extends T> List<S> findAll(Example<S> example, Sort sort);
    <S extends T> Page<S> findAll(Example<S> example, Pageable pageable);
    List<T> findAllById(Iterable<ID> ids);
    long count();
    <S extends T> long count(Example<S> example);
    <S extends T> boolean exists(Example<S> example);
    boolean existsById(ID id);
    void flush();
    <S extends T> S saveAndFlush(S entity);
}