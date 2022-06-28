package com.sean.usercenter.service;
import java.util.Date;

import com.sean.usercenter.model.domain.User;
import com.sean.usercenter.model.request.UserRegisterRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户服务测试
 *
 * @author sean
 */

@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;

//    @Test
//    public void testAddUser(){
//        User user = new User();
//        user.setId(0);
//        user.setUsername("testUser");
//        user.setUserAccount("123456");
//        user.setAvatarUrl("https://ts1.cn.mm.bing.net/th?id=OIP-C.hqD6F7mzf_dRi1UuJRegrgAAAA&w=208&h=208&c=8&rs=1&qlt=90&o=6&dpr=1.25&pid=3.1&rm=2");
//        user.setGender(0);
//        user.setUserPassword("123456");
//        user.setPhone("223456");
//        user.setEmail("123@123.com");
//        boolean save = userService.save(user);
//        System.out.println(user.getId());
//        //断言，测试预期结果和实际结果是否一致
//        assertTrue(save);
//    }
//
//    @Test
//    void userRegister() {
//        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
//        userRegisterRequest.setUserAccount("test9529");
//        userRegisterRequest.setUserPassword("12345678");
//        userRegisterRequest.setCheckPassword("12345678");
//        userRegisterRequest.setPlanetCode("9527");
//        int result = userService.userRegister(userRegisterRequest);
//        System.out.println(result);
////        Assertions.assertTrue(result>0);
//        Assertions.assertEquals(-1,result);
//
//    }
}