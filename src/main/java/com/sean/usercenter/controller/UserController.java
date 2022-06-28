package com.sean.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sean.usercenter.common.BaseResponse;
import com.sean.usercenter.common.ErrorCode;
import com.sean.usercenter.constant.UserConstant;
import com.sean.usercenter.exception.BusinessException;
import com.sean.usercenter.model.domain.User;
import com.sean.usercenter.model.request.UserLoginRequest;
import com.sean.usercenter.model.request.UserRegisterRequest;
import com.sean.usercenter.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController implements UserConstant{
    @Resource
    private UserService userService;


    @PostMapping("/register")
    public BaseResponse<Integer> userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        if(userRegisterRequest ==null){
//            return BaseResponse.fail(ErrorCode.PARAMS_ERROR);
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if(StringUtils.isAnyBlank(userAccount,userPassword,checkPassword)){
            return BaseResponse.error(ErrorCode.PARAMS_ERROR);
        }
        int result = userService.userRegister(userRegisterRequest);
        return BaseResponse.success(result);
    }


    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request){
        User user = (User)request.getSession().getAttribute(USER_LOGIN_STATE);
        if(user==null) return BaseResponse.error(ErrorCode.PARAMS_ERROR);
        int userId = user.getId();

        //TODO 校验用户是否合法

        User userById = userService.getById(userId);
        User safeUser = userService.getSafeUser(userById);
        return BaseResponse.success(safeUser);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        if(userLoginRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if(StringUtils.isAnyBlank(userAccount,userPassword)) throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号或密码为空");
        User user = userService.userLogin(userAccount, userPassword, request);
        return BaseResponse.success(user);
    }


    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request){
        if(request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        userService.userLogout(request);
        return BaseResponse.success(1);
    }



    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request){

        if(!this.isAdmin(request))  throw new BusinessException(ErrorCode.NO_AUTH,"账号无权限");

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(username), User::getUsername, username);
        List<User> list = userService.list(queryWrapper);

        //脱敏
        List<User> newList = list.stream().map(user -> userService.getSafeUser(user)).collect(Collectors.toList());
        return BaseResponse.success(newList);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody int id, HttpServletRequest request){

        if(!this.isAdmin(request)) throw new BusinessException(ErrorCode.NO_AUTH,"账号无权限");

        if(id<=0) return  BaseResponse.error(ErrorCode.PARAMS_NULL_ERROR);
        boolean result = userService.removeById(id);
        return BaseResponse.success(result);

    }

    /**
     * 判断是否为管理员
     * @param request
     * @return
     */
    private boolean isAdmin(HttpServletRequest request){
        //获取用户信息
        User user = (User)request.getSession().getAttribute(USER_LOGIN_STATE);
        //判断用户是否为管理员
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }

}
