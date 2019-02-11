package com.huang.mmall.interceptor;

import com.huang.mmall.bean.pojo.User;
import com.huang.mmall.common.Const;
import com.huang.mmall.common.ResponseCode;
import com.huang.mmall.common.ServerResponse;
import com.huang.mmall.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 * 登录拦截器
 *
 * @author hxy
 * @date 2019/01/14
 */
@Slf4j
@Component
public class AuthorityInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("preHandle");
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        //解析handlerMethod
        String methodName = handlerMethod.getMethod().getName();
        String className = handlerMethod.getBean().getClass().getSimpleName();

        StringBuffer requestParamBuffer = new StringBuffer();

        request.getParameterMap().forEach((key, value) -> {
            //request这个参数的map，里面的value返回的是一个String[]
            if (value != null) {
                requestParamBuffer.append(key).append("=").append(Arrays.toString(value));
            }
        });

        log.info("拦截器拦截到请求，className:{},methodName:{},param:{}", className, methodName, requestParamBuffer);

        //判断用户是否登录
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null || user.getRole() != Const.Role.ROLE_ADMIN) {
            //把response进行重置，否则会报 getWriter() has already been called for this response.异常
            response.reset();
            //这里要设置编码，否则会乱码
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            //用户未登录
            if (user == null) {
                out.print(JsonUtil.obj2String(ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "拦截器拦截,用户未登录")));
            } else { //用户不是管理员角色
                out.print(JsonUtil.obj2String(ServerResponse.createByErrorMessage("拦截器拦截，用户无权限操作")));
            }
            out.flush();
            out.close();
            return false;
        } else {
            return true;
        }
    }

    /**
     * 处理之后
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        log.info("postHandle");
    }

    /**
     * 完成之后(如果不是前后端分离，是返回ModelAndView，该方法是在视图呈现之后调用)
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        log.info("afterCompletion");
    }
}
