package com.huang.mmall.Exception;

import com.huang.mmall.common.ResponseCode;
import com.huang.mmall.common.ServerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * GlobalExceptionHandler class
 * 全局异常类,处理全局异常统一返回格式
 *
 * @author hxy
 * @date 2019/1/22
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 全局异常捕获
     *
     * @param e       异常
     * @param request request对象
     * @return 封装后的异常信息
     */
    @ExceptionHandler(Exception.class)
    public Object handlerException(Exception e, HttpServletRequest request) {
        log.error("{} Exception", request.getRequestURI(), e);
        //使用HttpServletRequest中的header检测请求是否为ajax, 如果是ajax则返回json, 如果为非ajax则返回view(即ModelAndView)
        String contentTypeHeader = request.getHeader("Content-Type");
        String acceptHeader = request.getHeader("Accept");
        String xRequestedWith = request.getHeader("X-Requested-With");
        boolean isAjax = (contentTypeHeader != null && contentTypeHeader.contains("application/json"))
                || (acceptHeader != null && acceptHeader.contains("application/json"))
                || "XMLHttpRequest".equalsIgnoreCase(xRequestedWith);
        if (isAjax) {
            return ServerResponse.createByErrorMessage("服务器异常");
        } else {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.addObject("status", ResponseCode.ERROR.getCode());
            modelAndView.addObject("msg", "服务器异常");
            modelAndView.setViewName("error/500");
            return modelAndView;
        }
    }
}
