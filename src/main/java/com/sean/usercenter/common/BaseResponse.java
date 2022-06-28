package com.sean.usercenter.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回类
 * @param <T>
 */
@Data
public class BaseResponse<T> implements Serializable {

    private int code;
    private T data;
    private String message;
    private String description;


    public BaseResponse(int code, T data, String message, String description){
        this.code=code;
        this.data=data;
        this.message=message;
        this.description=description;
    }

    public BaseResponse(int code, String message, String description){
        this.code=code;
        this.data=null;
        this.message=message;
        this.description=description;
    }

    //返回错误信息
    public BaseResponse(ErrorCode errorCode){
        this(errorCode.getCode(),null,errorCode.getMessage(),errorCode.getDescription());
    }

    public BaseResponse(ErrorCode errorCode, String message){
        this(errorCode.getCode(),null,message,errorCode.getDescription());
    }

    public BaseResponse(ErrorCode errorCode, String message,String description){
        this(errorCode.getCode(),null,message,description);
    }


    public BaseResponse(T data, ErrorCode errorCode){
        this(errorCode.getCode(),data,errorCode.getMessage(),errorCode.getDescription());
    }

    public static <T> BaseResponse<T> success(T data){
        return new BaseResponse<>(0,data,"success","");
    }

    public static <T> BaseResponse<T> error(ErrorCode errorCode){
        return new BaseResponse<>(errorCode);
    }

    public static <T> BaseResponse<T> error(ErrorCode errorCode, String message){
        return new BaseResponse<>(errorCode);
    }


    public static <T> BaseResponse<T> error(int code, String message, String description){
        return new BaseResponse(code,message,description);
    }

    public static <T> BaseResponse<T> error(ErrorCode errorCode, String message, String description){
        return new BaseResponse<>(errorCode);
    }


}
