package com.huang.mmall.bean.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * OrderProductVo class
 *
 * @author hxy
 * @date 2019/2/9
 */
@Data
public class OrderProductVo {
    /**
     * 商品详情
     */
    private List<OrderItemVo> orderItemVoList;
    /**
     * 总价
     */
    private BigDecimal productTotalPrice;
    /**
     * 图片服务器地址
     */
    private String imageHost;
}
