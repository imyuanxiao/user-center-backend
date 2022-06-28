package com.sean.usercenter.service;

import com.sean.usercenter.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sean.usercenter.model.request.UserRegisterRequest;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

/**
* @author Administrator
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2022-06-24 09:50:32
*/
public interface UserService extends IService<User> {

    /**
     * 用户校验
     * @return 新用户Id
     */
    int userRegister(UserRegisterRequest userRegisterRequest);


    /**
     * 返回脱敏后的用户数据
     * @param userAccount
     * @param userPassword
     * @return
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户信息脱敏
     * @param user
     * @return
     */
    User getSafeUser(User user);


    /**
     * 用户注销
     */
    int userLogout(HttpServletRequest request);



}
