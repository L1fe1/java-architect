package com.l1fe1.springdatajpa;

import org.springframework.data.repository.Repository;

import java.util.List;

public interface PersonRepository extends Repository<User,Long> {
	// and 的查询关系
    List<User> findByEmailAndLastname(String email, String lastname);
    // 包含 distinct 去重，or 的 sql 语法
    List<User> findDistinctPeopleByLastnameOrFirstname(String lastname, String firstname);
    // 根据 lastname 字段查询忽略大小写
    List<User> findByLastnameIgnoreCase(String lastname);
    // 根据 lastname 和 firstname 查询并且忽略大小写
    List<User> findByLastnameAndFirstnameAllIgnoreCase(String lastname, String firstname);
    // 对查询结果根据 lastname 正序排序
    List<User> findByLastnameOrderByFirstnameAsc(String lastname);
    // 对查询结果根据 lastname 倒序排序
    List<User> findByLastnameOrderByFirstnameDesc(String lastname);
}
