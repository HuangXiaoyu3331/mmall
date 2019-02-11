package com.huang.mmall.service;

import com.huang.mmall.bean.pojo.Shipping;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ShippingServiceTest {

    @Autowired
    private ShippingService shippingService;

    @Test
    @Transactional
    public void add() {
        Shipping shipping = new Shipping();
        shipping.setReceiverMobile("15626212222");
        shipping.setReceiverAddress("这是收获地址");
        shipping.setReceiverCity("广州");
        shipping.setReceiverProvince("天河");
        shipping.setReceiverDistrict("员村");
        shipping.setReceiverPhone("2222222");
        shipping.setReceiverName("hxy");
        shipping.setReceiverZip("222225");
        shippingService.add(1,shipping );
    }
}