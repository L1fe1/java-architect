package com.l1fe1.springdatajpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface UserPagingAndSortingRepository extends PagingAndSortingRepository<User, Long> {
    // 根据分页参数查询 User，返回一个带分页结果的 Page
    Page<User> findByLastname(String lastname, Pageable pageable);
    // 根据分页参数返回一个 Slice 的 user 结果
    Slice<User> findByFirstname(String firstName, Pageable pageable);
    // 根据排序结果返回一个 List
    List<User> findByLastname(String lastname, Sort sort);
    // 根据分页参数返回一个 List 对象
    List<User> findByName(String name, Pageable pageable);
    // 查询按 lastname 正序排序的第一条记录
    User findFirstByOrderByLastnameAsc();
    // 查询按 age 倒序排序的第一条记录
    User findTopByOrderByAgeDesc();
    // 根据分页参数查询去重的前 3 条记录
    List<User> findDistinctUserTop3ByLastname(String lastname, Pageable pageable);
    // 根据排序参数查询前 10 条记录
    List<User> findFirst10ByLastname(String lastname, Sort sort);
    // 根据分页参数查询前 10 条记录
    List<User> findTop10ByLastname(String lastname, Pageable pageable);
}