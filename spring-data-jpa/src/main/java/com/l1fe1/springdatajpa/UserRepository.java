package com.l1fe1.springdatajpa;

import org.springframework.data.repository.Repository;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public interface UserRepository extends Repository<User, Long> {
    /**
     * 根据名称进行查询用户列表
     * 当我们添加 @Nullable 注解之后，参数和返回结果这个时候就都会允许为 null 了
     * @param name
     * @return
     */
    @Nullable
    List<User> findByName(@Nullable String name);

    /**
     * 返回结果允许为 null,参数不允许为 null
     * @param name
     * @return
     */
    Optional<User> findOptionalByName(String name);

    /**
     * 根据用户的邮箱和名称查询
     * @param email
     * @param name
     * @return
     */
    List<User> findByEmailAndName(String email, String name);

    @Async
    Future<User> findByFirstname(String firstname);

    @Async
    CompletableFuture<User> findOneByFirstname(String firstname);

    @Async
    ListenableFuture<User> findOneByLastname(String lastname);

    /**
     * 只返回 name和 email 的 DTO
     * @param email
     * @return
     */
    UserOnlyNameEmailDto findByEmail(String email);

    /**
     * 接口的方式返回 DTO
     * @param address
     * @return
     */
    UserOnlyName findByAddress(String address);
}