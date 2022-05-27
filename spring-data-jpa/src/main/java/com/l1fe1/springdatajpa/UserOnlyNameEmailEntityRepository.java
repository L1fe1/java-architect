package com.l1fe1.springdatajpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserOnlyNameEmailEntityRepository extends JpaRepository<UserOnlyNameEmailEntity,Long> {
}