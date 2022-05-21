package com.l1fe1.springdatajpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    /**
     * 保存用户
     * @param user
     * @return
     */
    @PostMapping(path = "/user", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public User addNewUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    /**
     * 根据分页信息查询用户
     * @param request
     * @return
     */
    @GetMapping("/users")
    @ResponseBody
    public Page<User> getAllUsers(Pageable request) {
        return userRepository.findAll(request);
    }
}
