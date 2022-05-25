package com.l1fe1.springdatajpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl {
    @Autowired
    private UserCrudRepository userCrudRepository;
    @Autowired
    private UserPagingAndSortingRepository userPagingAndSortingRepository;

    public void testDQM() {
        userCrudRepository.findAll();
        userCrudRepository.findByEmail("abc@126.com");
        userCrudRepository.deleteAll();

        //查询 user里面的 lastname=jk 的第一页，每页大小是 20 条；并会返回一共有多少页的信息
        Page<User> pageUsers = userPagingAndSortingRepository.findByLastname("jk", PageRequest.of(0,20));
        //查询 user 里面的 firstname=jk 的第一页的 20 条数据，不知道一共多少条
        Slice<User> sliceUsers = userPagingAndSortingRepository.findByFirstname("jk", PageRequest.of(0,20));
        //查询所有的 user 里面的 lastname=jk 的数据，并按照 lastname 正序返回 List
        List<User> ascUsers = userPagingAndSortingRepository.findByLastname("jk",
                Sort.by(new Sort.Order(Sort.Direction.ASC,"name")));
        // 按照 createdAt 倒序，查询前一百条 User 数据
        List<User> users = userPagingAndSortingRepository.findByName("jk",
                PageRequest.of(0,100, Sort.Direction.DESC,"createdAt"));
    }
}
