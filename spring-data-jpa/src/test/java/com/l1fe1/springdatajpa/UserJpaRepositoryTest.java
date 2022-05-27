package com.l1fe1.springdatajpa;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.util.Streamable;

import java.util.List;
import java.util.stream.Stream;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserJpaRepositoryTest {
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private UserOnlyNameEmailEntityRepository userOnlyNameEmailEntityRepository;
    @Autowired
    private UserRepository userRepository;
    @Test
    public void testSaveUser() throws JsonProcessingException {
        User user = new User();
        user.setName("bob");
        user.setEmail("123456@126.com");
        user = userJpaRepository.save(user);
        Assert.assertNotNull(user);
        List<User> users= userJpaRepository.findAll();
        System.out.println(users);
        Assert.assertNotNull(users);

        User usr = userJpaRepository.save(User.builder()
                .name("alice")
                .email("123456@126.com")
                .address("shanghai").build());
        Assert.assertNotNull(usr);
        Streamable<User> userStreamable = userJpaRepository.findAll(
                PageRequest.of(0,10)).and(User.builder().name("jack222").build());
        userStreamable.forEach(System.out::println);

        // 新增 7 条数据方便测试分页结果
        userJpaRepository.save(User.builder().name("jack1").email("123456@126.com").address("shanghai").build());
        userJpaRepository.save(User.builder().name("jack2").email("123456@126.com").address("shanghai").build());
        userJpaRepository.save(User.builder().name("jack3").email("123456@126.com").address("shanghai").build());
        userJpaRepository.save(User.builder().name("jack4").email("123456@126.com").address("shanghai").build());
        userJpaRepository.save(User.builder().name("jack5").email("123456@126.com").address("shanghai").build());
        userJpaRepository.save(User.builder().name("jack6").email("123456@126.com").address("shanghai").build());
        userJpaRepository.save(User.builder().name("jack7").email("123456@126.com").address("shanghai").build());
        ObjectMapper objectMapper = new ObjectMapper();
        // 返回 Stream 类型结果（1）
        Stream<User> userStream = userJpaRepository.findAllByCustomQueryAndStream(PageRequest.of(1,3));
        userStream.forEach(System.out::println);
        // 返回分页数据（2）
        Page<User> userPage = userJpaRepository.findAll(PageRequest.of(0,3));
        System.out.println(objectMapper.writeValueAsString(userPage));
        // 返回 Slice 结果（3）
        Slice<User> userSlice = userJpaRepository.findAllByCustomQueryAndSlice(PageRequest.of(0,3));
        System.out.println(objectMapper.writeValueAsString(userSlice));
        // 返回 List 结果（4）
        List<User> userList = userJpaRepository.findAllById(Lists.newArrayList(1L, 2L));
        System.out.println(objectMapper.writeValueAsString(userList));
    }

    @Test
    public void testProjections(){
        User user = userJpaRepository.getReferenceById(1L);
        System.out.println(user);
        UserOnlyNameEmailEntity userOnlyNameEmailEntity = userOnlyNameEmailEntityRepository.getReferenceById(1L);
        System.out.println(userOnlyNameEmailEntity);

        userJpaRepository.save(User.builder().id(1L).name("xiaoming").email("123456@126.com").address("shanghai").build());
        UserOnlyNameEmailDto userOnlyNameEmailDto = userRepository.findByEmail("123456@126.com");
        System.out.println(userOnlyNameEmailDto);

        UserOnlyName userOnlyName = userRepository.findByAddress("shanghai");
        System.out.println(userOnlyName);
    }
}