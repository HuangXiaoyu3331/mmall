package com.huang.mmall.controller.potral;

import com.huang.mmall.bean.pojo.User;
import com.huang.mmall.common.Const;
import com.huang.mmall.common.ResponseCode;
import com.huang.mmall.common.ServerResponse;
import com.huang.mmall.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * 前台用户模块controller
 *
 * @author hxy
 * @date 2019/01/14
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 登录，登录成功将用户信息放进session里面
     *
     * @param username 用户名
     * @param password 密码
     * @param session  会话session
     * @return ServerResponse
     */
    @PostMapping("/login")
    public ServerResponse<User> login(String username, String password, HttpSession session) {
        log.info("登录->用户：{}", username);
        ServerResponse<User> response = userService.login(username, password);
        if (response.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER, response.getData());
            log.info("用户：{}登录成功", username);
        }
        return response;
    }

    /**
     * 登出，将session里面的用户信息删除掉
     *
     * @param session 会话session
     * @return ServerResponse
     */
    @GetMapping("/logout")
    public ServerResponse<String> logout(HttpSession session) {
        log.info("用户登出");
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    /**
     * 用户注册
     *
     * @param user 用户信息
     * @return ServerResponse
     */
    @PostMapping("/register")
    public ServerResponse<String> register(@RequestBody User user) {
        log.info("注册->用户{}", user.getUsername());
        return userService.register(user);
    }

    /**
     * 校验邮箱|用户名是否可用
     *
     * @param str  邮箱或用户名
     * @param type 校验的类型（Const.USERNAME|Const.EMAIL）
     * @return ServerResponse
     */
    @PostMapping("/check_valid")
    public ServerResponse<String> checkValid(String str, String type) {
        log.info("校验参数是否可用->校验类型：{}，校验参数：{}", type, str);
        return userService.checkValid(str, type);
    }

    /**
     * 获取用户的信息
     *
     * @param session 会话session
     * @return 用户信息
     */
    @GetMapping("/get_user_info")
    public ServerResponse<User> getUserInfo(HttpSession session) {
        log.info("获取用户信息");
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            log.warn("获取用户信息失败，用户未登录");
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "用户未登录,无法获取用户的信息。");
        } else {
            user.setPassword(StringUtils.EMPTY);
            log.info("获取用户信息成功，用户:{}", user);
            return ServerResponse.createBySuccess(user);
        }
    }

    /**
     * 忘记密码，返回密保问题
     *
     * @param username 用户名
     * @return 密保问题
     */
    @GetMapping("/forget_get_question")
    public ServerResponse<String> forgetGetQuestion(String username) {
        log.info("查找密保问题->用户：{}", username);
        return userService.selectQuestion(username);
    }

    /**
     * 使用本地缓存，检查问题答案。答案对的话，返回一个token，并把token写到本地缓存中
     *
     * @param username 用户名
     * @param question 问题
     * @param answer   答案
     * @return ServerResponse
     */
    @PostMapping("/forget_check_answer")
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer) {
        log.info("校验问题答案。用户:{}，问题:{}，答案:{}", username, question, answer);
        return userService.checkAnswer(username, question, answer);
    }

    /**
     * 未登录状态下修改密码
     *
     * @param username    用户名
     * @param passwordNew 新密码
     * @param forgetToken 回答密保问题成功服务器返回的token
     * @return ServerResponse
     */
    @PostMapping("/forget_reset_password")
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        log.info("未登录状态修改密码->用户：{}，新密码：{}，token：{}", username, passwordNew, forgetToken);
        return userService.forgetResetPassword(username, passwordNew, forgetToken);
    }

    /**
     * 登录状态修改密码
     *
     * @param session     会话session
     * @param passwordOld 旧密码
     * @param passwordNew 新密码
     * @return ServerResponse
     */
    @PostMapping("/reset_password")
    public ServerResponse<String> resetPassword(HttpSession session, String passwordOld, String passwordNew) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        log.info("登录状态修改用户密码");
        if (user == null) {
            log.warn("登录状态修改密码失败，用户未登录");
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        return userService.resetPassword(passwordOld, passwordNew, user);
    }

    @PutMapping("/user")
    public ServerResponse<User> updateInfo(HttpSession session, @RequestBody User user) {
        log.info("修改用户信息");
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null) {
            log.warn("修改用户信息失败，用户未登录");
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        //由于更新的信息的user是没有id的，所以需要赋值。也可以防止而已修改导致越权
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServerResponse<User> response = userService.updateInfo(user);
        if (response.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }
        return response;
    }
}