package com.l1fe1.springdatajpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private UserPagingAndSortingRepository userPagingAndSortingRepository;

    /**
     * 保存用户
     * @param user
     * @return
     */
    @PostMapping(path = "/user", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public User addNewUser(@RequestBody User user) {
        return userJpaRepository.save(user);
    }

    /**
     * 根据分页信息查询用户
     * @param request
     * @return
     */
    @GetMapping("/users")
    @ResponseBody
    public Page<User> getAllUsers(Pageable request) {
        return userJpaRepository.findAll(request);
    }

    /**
     * 排序和分页查询方法，Pageable 的默认实现类：PageRequest
     * @return
     */
    @GetMapping("/page")
    @ResponseBody
    public Page<User> getAllUserByPage() {
        return userPagingAndSortingRepository.findAll(
                PageRequest.of(0, 10, Sort.by(new Sort.Order(Sort.Direction.DESC,"id"))));
    }

    /**
     * 排序查询方法，使用 Sort 对象
     * @return
     */
    @GetMapping("/sort")
    @ResponseBody
    public Iterable<User> getAllUsersWithSort() {
        return userPagingAndSortingRepository.findAll(Sort.by(new Sort.Order(Sort.Direction.ASC, "id")));
    }
}
