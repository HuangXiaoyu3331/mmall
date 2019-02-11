package com.huang.mmall.service;

import com.huang.mmall.bean.pojo.User;
import com.huang.mmall.common.Const;
import com.huang.mmall.common.ServerResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void login() {
        ServerResponse<User> userIsNotExistResponse = ServerResponse.createByErrorMessage("登录失败，用户不存在");
        ServerResponse<User> passwordErrorResponse = ServerResponse.createByErrorMessage("密码错误");

        ServerResponse<User> response = userService.login("admin", "admin");
        assertThat(response.isSuccess(), equalTo(true));
        response = userService.login("admin", "1");
        assertThat(response, is(equalTo(passwordErrorResponse)));
        response = userService.login("admiqqn", "admin");
        assertThat(response, is(equalTo(userIsNotExistResponse)));
    }

    @Test
    @Transactional
    public void register() {
        ServerResponse<String> successResponse = ServerResponse.createBySuccessMessage("注册成功");
        ServerResponse<String> userIsExistResponse = ServerResponse.createByErrorMessage("用户名已经存在");
        ServerResponse<String> emailIsExistResponse = ServerResponse.createByErrorMessage("邮箱已经注册过");

        User user = new User();
        user.setUsername("admin");
        user.setPassword("123456");
        user.setEmail("hxy1@qq.com");
        user.setPhone("12266558899");
        user.setQuestion("question");
        user.setAnswer("answer");

        ServerResponse<String> response = userService.register(user);
        assertThat(response, is(equalTo(userIsExistResponse)));

        user.setUsername("hxy1");
        user.setEmail("e@qq.com");
        response = userService.register(user);
        assertThat(response, is(equalTo(emailIsExistResponse)));

        user.setUsername("hxy1");
        user.setEmail("11111@qq.com");
        response = userService.register(user);
        assertThat(response, is(equalTo(successResponse)));
    }

    @Test
    public void checkValid() {
        ServerResponse<String> paramsErrorResponse = ServerResponse.createByErrorMessage("参数错误");
        ServerResponse<String> usernameIsExistResponse = ServerResponse.createByErrorMessage("用户名已经存在");
        ServerResponse<String> emailIsExitsResponse = ServerResponse.createByErrorMessage("邮箱已经注册过");
        ServerResponse<String> successfulResponse = ServerResponse.createBySuccessMessage("校验成功");

        ServerResponse<String> response = userService.checkValid("admin", null);
        assertThat(response, is(equalTo(paramsErrorResponse)));
        response = userService.checkValid("admin", Const.USERNAME);
        assertThat(response, is(equalTo(usernameIsExistResponse)));
        response = userService.checkValid("e@qq.com", Const.EMAIL);
        assertThat(response, is(equalTo(emailIsExitsResponse)));
        response = userService.checkValid("dfdd", Const.USERNAME);
        assertThat(response, is(equalTo(successfulResponse)));
        response = userService.checkValid("aaa@qq.com", Const.EMAIL);
        assertThat(response, is(equalTo(successfulResponse)));
    }

    @Test
    public void selectQuestion() {
        ServerResponse<String> errorResponse = ServerResponse.createByErrorMessage("用户名不存在");
        ServerResponse<String> nullResponse = ServerResponse.createByErrorMessage("找回密码的问题是空的");
        ServerResponse<String> successResponse = ServerResponse.createBySuccess("问题");

        ServerResponse<String> response = userService.selectQuestion("admin");
        assertThat(response, is(equalTo(successResponse)));
        response = userService.selectQuestion("dkfj");
        assertThat(response, is(equalTo(errorResponse)));
        response = userService.selectQuestion("testtest");
        assertThat(response, is(equalTo(nullResponse)));
    }

    @Test
    public void checkAnswer() {
        ServerResponse<String> errorResponse = ServerResponse.createByErrorMessage("问题答案错误");

        ServerResponse<String> response = userService.checkAnswer("admin", "问题", "答案");
        assertThat(response.isSuccess(), is(equalTo(true)));
        response = userService.checkAnswer("admin", "问题", "错误答案");
        assertThat(response, is(equalTo(errorResponse)));
    }

//    @Test
//    public void forgetResetPassword() {
//        ServerResponse<String> errorResponse = ServerResponse.createByErrorMessage("修改密码失败，请重试");
//        ServerResponse<String> errorResponse = ServerResponse.createByErrorMessage("用户不存在");
//    }

    @Test
    @Transactional
    public void resetPassword() {
        User user = new User();
        user.setId(1);
        user.setUsername("admin");
        ServerResponse<String> passwordOldErrorResponse = ServerResponse.createByErrorMessage("旧密码错误");
        ServerResponse<String> successResponse = ServerResponse.createBySuccessMessage("密码更新成功");

        ServerResponse<String> response = userService.resetPassword("aa", "hxy", user);
        assertThat(response, is(equalTo(passwordOldErrorResponse)));
        response = userService.resetPassword("admin", "hxy", user);
        assertThat(response, is(equalTo(successResponse)));
    }

    @Test
    @Transactional
    public void updateInfo() {
        User user = new User();
        user.setId(1);
        user.setUsername("admin");
        user.setEmail("geely@qq.com");
        user.setPhone("111");
        user.setQuestion("问题");
        user.setAnswer("答案");

        ServerResponse<User> errorResponse = ServerResponse.createByErrorMessage("email已存在，请更换email再尝试更新");
        ServerResponse<User> response = userService.updateInfo(user);
        assertThat(response, is(equalTo(errorResponse)));

        user.setEmail("dfill@qq.com");
        response = userService.updateInfo(user);
        assertThat(response.isSuccess(), is(equalTo(true)));
    }
}