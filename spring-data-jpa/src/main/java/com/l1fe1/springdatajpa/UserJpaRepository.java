package com.l1fe1.springdatajpa;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.stream.Stream;

public interface UserJpaRepository extends JpaRepository<User, Long> {
    // 自定义一个查询方法，返回 Stream 对象，并且有分页属性
	@Query("select u from User u")
    Stream<User> findAllByCustomQueryAndStream(Pageable pageable);
    // 自定义一个查询方法，返回 Slice 对象，并且有分页属性
    @Query("select u from User u")
    Slice<User> findAllByCustomQueryAndSlice(Pageable pageable);
}