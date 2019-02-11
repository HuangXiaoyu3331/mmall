package com.huang.mmall.service;

import com.github.pagehelper.PageInfo;
import com.huang.mmall.bean.vo.ProductListVo;
import com.huang.mmall.common.ResponseCode;
import com.huang.mmall.common.ServerResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Test
    public void addProduct() {
    }

    @Test
    public void updateProduct() {
    }

    @Test
    public void setSaleStatus() {
    }

    @Test
    public void getDetail() {
    }

    @Test
    public void getProductList() {
    }

    @Test
    public void searchProduct() {
    }

    @Test
    public void getProductDetail() {
    }

    @Test
    public void getProductByKeywordCategory() {
        ServerResponse illegalResponse = ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());

        ServerResponse<PageInfo> response = productService.getProductByKeywordCategory(null, null, 1, 10, "price_asc");
        assertThat(response, is(equalTo(illegalResponse)));

        response = productService.getProductByKeywordCategory("手机", null, 1, 10, "price_desc");
        assertThat(response.getData().getList().size(), is(greaterThan(0)));

        response = productService.getProductByKeywordCategory(null, 100012, 1, 10, "price_desc");
        assertThat(response.getData().getList().size(), is(greaterThan(0)));

        response = productService.getProductByKeywordCategory(null, 1, 1, 10, "price_desc");
        assertThat(response.getData().getList().size(), is(equalTo(0)));

    }
}