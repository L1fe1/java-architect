package com.l1fe1.springdatajpa;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class BaseServiceImpl<T, ID, R extends JpaRepository<T,ID>> implements BaseService<T, ID> {
    private static final Map<Class, Class> DOMAIN_CLASS_CACHE = new ConcurrentHashMap<>();
    private final R repository;

    public BaseServiceImpl(R repository){
        this.repository = repository;
    }

    @Override
    public Class<T> getDomainClass() {
        Class thisClass = getClass();
        Class<T> domainClass = DOMAIN_CLASS_CACHE.get(thisClass);
        if (Objects.isNull(domainClass)) {
            domainClass = GenericsUtils.getGenericClass(thisClass, 0);
            DOMAIN_CLASS_CACHE.putIfAbsent(thisClass, domainClass);
        }
        return domainClass;
    }

    protected R getRepository(){
        return repository;
    }

    @Override
    public <S extends T> S save(S entity) {
        return repository.save(entity);
    }

    @Override
    public <S extends T> List<S> saveAll(Iterable<S> entities) {
        return repository.saveAll(entities);
    }

    @Override
    public void delete(T entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteById(ID id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }

    @Override
    public void deleteAll(Iterable<? extends T> entities) {
        repository.deleteAll(entities);
    }

    @Override
    public void deleteInBatch(Iterable<T> entities) {
        repository.deleteAllInBatch(entities);
    }

    @Override
    public void deleteAllInBatch() {
        repository.deleteAllInBatch();
    }

    @Override
    public T getOne(ID id) {
        return repository.getReferenceById(id);
    }

    @Override
    public <S extends T> Optional<S> findOne(Example<S> example) {
        return repository.findOne(example);
    }

    @Override
    public Optional<T> findById(ID id) {
        return repository.findById(id);
    }

    @Override
    public List<T> findAll() {
        return repository.findAll();
    }

    @Override
    public List<T> findAll(Sort sort) {
        return repository.findAll(sort);
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public <S extends T> List<S> findAll(Example<S> example) {
        return repository.findAll(example);
    }

    @Override
    public <S extends T> List<S> findAll(Example<S> example, Sort sort) {
        return repository.findAll(example, sort);
    }

    @Override
    public <S extends T> Page<S> findAll(Example<S> example, Pageable pageable) {
        return repository.findAll(example, pageable);
    }

    @Override
    public List<T> findAllById(Iterable<ID> ids) {
        return repository.findAllById(ids);
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public <S extends T> long count(Example<S> example) {
        return repository.count(example);
    }

    @Override
    public <S extends T> boolean exists(Example<S> example) {
        return repository.exists(example);
    }

    @Override
    public boolean existsById(ID id) {
        return repository.existsById(id);
    }

    @Override
    public void flush() {
        repository.flush();
    }

    @Override
    public <S extends T> S saveAndFlush(S entity) {
        return repository.saveAndFlush(entity);
    }
}
