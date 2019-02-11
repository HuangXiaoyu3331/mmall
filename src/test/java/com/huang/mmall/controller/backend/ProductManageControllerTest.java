package com.huang.mmall.controller.backend;

import com.huang.mmall.bean.pojo.Product;
import com.huang.mmall.bean.pojo.User;
import com.huang.mmall.common.Const;
import com.huang.mmall.common.ResponseCode;
import com.huang.mmall.dao.UserMapper;
import com.huang.mmall.util.JsonUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductManageControllerTest {

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
    @Transactional
    public void saveProduct() {
    }

    @Test
    @Transactional
    public void updateProduct() throws Exception {
        Product product = new Product();
        product.setId(28);
        product.setName("这是修改后的名字");
        String requestJson = JsonUtil.obj2String(product);
        mvc.perform(MockMvcRequestBuilders.put("/manage/product")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson)
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(ResponseCode.SUCCESS.getCode()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void setSaleStatus() {
    }

    @Test
    public void getDetail() {
    }

    @Test
    public void getList() {
    }

    @Test
    public void productSearch() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/manage/product/手机/28/1/10")
                .characterEncoding("utf-8")
                .session(session)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(ResponseCode.SUCCESS.getCode()))
                .andDo(MockMvcResultHandlers.print());

        mvc.perform(MockMvcRequestBuilders.get("/manage/product/28/1/10")
                .characterEncoding("utf-8")
                .session(session)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(ResponseCode.SUCCESS.getCode()))
                .andDo(MockMvcResultHandlers.print());

        mvc.perform(MockMvcRequestBuilders.get("/manage/product/keyword/手机/1/10")
                .characterEncoding("utf-8")
                .session(session)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(ResponseCode.SUCCESS.getCode()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void upload() throws Exception {
        //这里，使用junit测试，request.getSession.getServletContext().getRealPath("/upload")获取不到路径，原因有待深究;
        File file = new File("G:/Photos/we/455226143037519934.jpg");
        MockMultipartFile multipartFile = new MockMultipartFile("file", "hahah.jpg",
                "multipart/form-data", new FileInputStream(file));
        mvc.perform(MockMvcRequestBuilders.multipart("/manage/product/upload")
                .file(multipartFile)
                .session(session)
        ).andDo(MockMvcResultHandlers.print()).andReturn();
    }
}