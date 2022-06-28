package com.sean.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = -1765150562248184864L;
    private String userAccount;
    private String userPassword;

}

