package com.huang.mmall.test;

import com.github.dozermapper.core.DozerBeanMapper;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.huang.mmall.bean.pojo.Order;
import com.huang.mmall.bean.pojo.OrderItem;
import com.huang.mmall.bean.pojo.Shipping;
import com.huang.mmall.bean.vo.OrderVo;
import com.huang.mmall.dao.OrderItemMapper;
import com.huang.mmall.dao.OrderMapper;
import com.huang.mmall.dao.ShippingMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

/**
 * DozerTest class
 *
 * @author hxy
 * @date 2019/1/16
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DozerTest {

    @Autowired
    private Mapper mapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private ShippingMapper shippingMapper;

    @Test
    public void apiCopy() {
        Mapper mapper = DozerBeanMapperBuilder.buildDefault();
        User user = new User();
        user.setName("黄晓宇");
        user.setPassword(123456);
        user.setDate(new Date());
        UserApiDestinationObject destinationObject = mapper.map(user, UserApiDestinationObject.class);
        System.out.println(destinationObject);
    }

    @Test
    public void annotationTest() {
        User user = new User();
        user.setName("黄晓宇");
        user.setPassword(33);
        UserAnnotationDestinationObject destinationObject = mapper.map(user, UserAnnotationDestinationObject.class);
        System.out.println(destinationObject);
    }

    @Test
    public void xmlTest() {
        User user = new User();
        user.setName("黄晓宇");
        user.setPassword(33);
        user.setDate(new Date());
        UserXmlDestinationObject destinationObject = mapper.map(user, UserXmlDestinationObject.class);
        System.out.println(destinationObject);
    }

    @Test
    public void orderVoTest() {
        Order order = (Order) orderMapper.selectByPrimaryKey(104);
        OrderItem orderItem = (OrderItem) orderItemMapper.selectByPrimaryKey(113);
        Shipping shipping = (Shipping) shippingMapper.selectByPrimaryKey(4);
        OrderVo orderVo = mapper.map(order, OrderVo.class);
        mapper.map(orderItem, orderVo);
        mapper.map(shipping, orderVo);
    }
}
