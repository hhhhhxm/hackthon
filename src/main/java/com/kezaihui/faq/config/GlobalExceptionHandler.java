package com.kezaihui.faq.config;

import com.kezaihui.faq.response.ResultData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author penghao
 * @description: 全局捕获异常
 * @date 2022/11/10
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResultData error(Exception e) {
        log.error("捕获异常", e);
        return ResultData.error().message(e.getMessage()).build();
    }
}
