package com.l1fe1.springdatajpa;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserCrudRepository extends CrudRepository<User, Long> {
    User findByEmail(String emailAddress);
    // 查询总数
    long countByLastname(String lastname);
    // 根据 lastname 进行删除操作，并返回删除行数
    long deleteByLastname(String lastname);
    //根据 lastname 删除一堆 User,并返回删除的 User
    List<User> removeByLastname(String lastname);
}