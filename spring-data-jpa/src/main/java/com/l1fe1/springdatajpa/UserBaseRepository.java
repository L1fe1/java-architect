package com.l1fe1.springdatajpa;

public interface UserBaseRepository extends MyBaseRepository<User, Long> {
    User findByEmail(String emailAddress);
}