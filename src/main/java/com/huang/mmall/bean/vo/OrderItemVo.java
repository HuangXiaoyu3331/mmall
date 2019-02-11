package com.huang.mmall.bean.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * OrderItemVo class
 *
 * @author hxy
 * @date 2019/2/1
 */
@Data
public class OrderItemVo {

    /**
     * 订单编号
     */
    private Long orderNo;
    /**
     * 商品id
     */
    private Integer productId;
    /**
     * 商品名称
     */
    private String productName;
    /**
     * 商品主图
     */
    private String productImage;
    /**
     * 商品单价
     */
    private BigDecimal currentUnitPrice;
    /**
     * 商品数量
     */
    private Integer quantity;
    /**
     * 总价
     */
    private BigDecimal totalPrice;
    /**
     * 创建时间
     */
    private String createTime;
}
