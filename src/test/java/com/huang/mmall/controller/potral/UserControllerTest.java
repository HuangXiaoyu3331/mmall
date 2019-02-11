package com.huang.mmall.controller.potral;

import com.alibaba.fastjson.JSONObject;
import com.huang.mmall.bean.pojo.User;
import com.huang.mmall.common.Const;
import com.huang.mmall.common.ResponseCode;
import com.huang.mmall.dao.UserMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserControllerTest {

    @Autowired
    private WebApplicationContext context;
    private MockMvc mvc;
    private MockHttpSession session;

    @Autowired
    private UserMapper userMapper;

    @Before
    public void setupMockMvc() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
        session = new MockHttpSession();
        User user = (User) userMapper.selectByPrimaryKey(1);
        session.setAttribute(Const.CURRENT_USER, user);
    }

    @Test
    public void login() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/login")
                .param("username", "admin")
                .param("password", "admin")
                .contentType(MediaType.APPLICATION_JSON_UTF8)//参数类型
                .accept(MediaType.APPLICATION_JSON_UTF8)//客户端希望接收的数据类型
        ).andExpect(MockMvcResultMatchers.status().isOk())//添加执行完成后的断言，isOK()判断请求状态码是否为200
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("登录成功"))
                .andDo(MockMvcResultHandlers.print());//添加结果处理，这里只是简单的输出响应结果信息
    }

    @Test
    public void logout() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/user/logout")
                .session(session)
                .accept(MediaType.APPLICATION_JSON_UTF8)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(ResponseCode.SUCCESS.getCode()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Transactional//用于测试完回滚，避免产生垃圾数据
    public void register() throws Exception {
        User user = new User();
        user.setUsername("hxy1");
        user.setPassword("123456");
        user.setEmail("hxy1@qq.com");
        user.setPhone("12266558899");
        user.setQuestion("question");
        user.setAnswer("answer");
        String requestJson = JSONObject.toJSONString(user);
        mvc.perform(MockMvcRequestBuilders.post("/user/register")
                .content(requestJson)//controller中使用@RequestBody注解接收参数是，用content传递json格式的参数
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(ResponseCode.SUCCESS.getCode()))
                .andDo(MockMvcResultHandlers.print());
    }
}