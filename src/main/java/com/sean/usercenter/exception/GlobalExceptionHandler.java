package com.sean.usercenter.exception;

import com.sean.usercenter.common.BaseResponse;
import com.sean.usercenter.common.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse businessExceptionHandler(BusinessException e){
        log.error("businessException："+e.getMessage(),e);
        return BaseResponse.error(e.getCode(),e.getMessage(),e.getDescription());

    }


    @ExceptionHandler(RuntimeException.class)
    public BaseResponse runtimeExceptionHandler(RuntimeException e){
        log.error("runtimeException",e);
        return BaseResponse.error(ErrorCode.SYSTEM_ERROR,e.getMessage(),"");
    }

}
