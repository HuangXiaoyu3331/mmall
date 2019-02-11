package com.huang.mmall.bean.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * CartProductVo class
 * 结合了商品跟购物车的一个vo对象
 *
 * @author hxy
 * @date 2019/1/25
 */
@Data
public class CartProductVo {
    /**
     * 购物车id
     */
    private Integer id;
    /**
     * 用户id
     */
    private Integer userId;
    /**
     * 商品id
     */
    private Integer productId;
    /**
     * 商品数量
     */
    private Integer quantity;
    /**
     * 商品名称
     */
    private String productName;
    /**
     * 商品标题
     */
    private String productSubtitle;
    /**
     * 商品主图
     */
    private String productMainImage;
    /**
     * 商品价格
     */
    private BigDecimal productPrice;
    /**
     * 商品状态
     */
    private Integer productStatus;
    /**
     * 商品总价
     */
    private BigDecimal productTotalPrice;
    /**
     * 商品库存
     */
    private Integer productStock;
    /**
     * 此商品是否勾选
     */
    private Integer productChecked;
    /**
     * 限制数量的一个返回结果
     */
    private String limitQuantity;
}
