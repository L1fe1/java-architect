package com.l1fe1.springdatajpa;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import java.io.Serializable;

@NoRepositoryBean
public interface MyBaseRepository<T,ID extends Serializable> extends Repository<T,ID> {
	T findOne(ID id);
	T save(T entity);
}