package com.sean.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sean.usercenter.common.ErrorCode;
import com.sean.usercenter.constant.UserConstant;
import com.sean.usercenter.exception.BusinessException;
import com.sean.usercenter.model.domain.User;
import com.sean.usercenter.model.request.UserRegisterRequest;
import com.sean.usercenter.service.UserService;
import com.sean.usercenter.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* @author Administrator
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2022-06-24 09:50:32
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService, UserConstant{

    /**
     * 盐值，用于混淆密码
     */
    private static final String SALT = "sean";


    @Resource
    private UserMapper userMapper;

    @Override
    public int userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {

        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String planetCode = userRegisterRequest.getPlanetCode();


        //校验
        if(StringUtils.isAnyBlank(userAccount,userPassword,checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");

        }
        if(userAccount.length() <4 || userAccount.length() > 30){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号长度异常");
        }


        if(userPassword.length() <8 || checkPassword.length() <8 || userPassword.length() >32 || checkPassword.length() >32){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号长度异常");
        }
        //账户不包含字符
        String regEx = ".*[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？\\\\]+.*";
        Matcher matcher = Pattern.compile(regEx).matcher(userAccount);
        if(matcher.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号含有特殊字符");

        }

        //密码不同
        if(!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"确认密码错误");

        }
        //账户不能重复
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getUserAccount, userAccount);
        long count = userMapper.selectCount(lambdaQueryWrapper);
        if(count>0){
            //返回账户重复的提示
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号重复");

        }

        //星球编号不能重复
        lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getPlanetCode, planetCode);
        count = userMapper.selectCount(lambdaQueryWrapper);
        if(count>0){
            //返回星球编号重复的提示
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"星球编号重复");
        }

        //加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT+userPassword).getBytes());

        //插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUsername(userAccount);//默认用户名为账户
        user.setPlanetCode(planetCode);
        boolean saveResult = this.save(user);
        return !saveResult ? -1: user.getId();
    }


    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {

        //校验
        if(StringUtils.isAnyBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号或密码为空");
        }
        if(userAccount.length() <4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号长度异常");
        }
        if(userPassword.length() <8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码长度异常");
        }

        //账户不包含字符
        String regEx = ".*[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？\\\\]+.*";
        Matcher matcher = Pattern.compile(regEx).matcher(userAccount);
        if(matcher.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号含有特殊字符");
        }


        //加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT+userPassword).getBytes());

        //查询
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount, userAccount);
        User user = userMapper.selectOne(queryWrapper);

        //用户不存在
        if(user == null) {
            throw new BusinessException(ErrorCode.PARAMS_NULL_ERROR,"用户不存在");}

        //密码错误
        queryWrapper.eq(User::getUserPassword, encryptPassword);
        user = userMapper.selectOne(queryWrapper);

        //用户不存在
        if(user == null) {
            throw new BusinessException(ErrorCode.PARAMS_NULL_ERROR,"密码错误");}

        //脱敏
        User safeUser = getSafeUser(user);

        //记录用户登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, safeUser);

        return safeUser;

    }

    /**
     * 用户脱敏
     * @param user
     * @return
     */
    @Override
    public User getSafeUser(User user){
        if(user==null) throw new BusinessException(ErrorCode.PARAMS_NULL_ERROR,"用户不存在");;
        User safeUser = new User();
        safeUser.setId(user.getId());
        safeUser.setUsername(user.getUsername());
        safeUser.setUserAccount(user.getUserAccount());
        safeUser.setAvatarUrl(user.getAvatarUrl());
        safeUser.setGender(user.getGender());
        safeUser.setPhone(user.getPhone());
        safeUser.setEmail(user.getEmail());
        safeUser.setUserStatus(user.getUserStatus());
        safeUser.setCreateTime(user.getCreateTime());
        safeUser.setUserRole(user.getUserRole());
        safeUser.setPlanetCode(user.getPlanetCode());
        return safeUser;
    }

    /**
     * 用户注销
     * @param request
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

}




