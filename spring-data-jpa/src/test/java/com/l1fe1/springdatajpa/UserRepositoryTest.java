package com.l1fe1.springdatajpa;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Test
    public void testSaveUser(){
        User user = new User();
        user.setName("bob");
        user.setEmail("123456@126.com");
        user = userRepository.save(user);
        Assert.assertNotNull(user);
        List<User> users= userRepository.findAll();
        System.out.println(users);
        Assert.assertNotNull(users);
    }
}