package com.huang.mmall.bean.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * CartVo class
 *
 * @author hxy
 * @date 2019/1/25
 */
@Data
public class CartVo {
    /**
     * 购物车商品集合
     */
    private List<CartProductVo> cartProductVoList;
    /**
     * 购物车总价
     */
    private BigDecimal cartTotalPrice;
    /**
     * 是否已经都勾选
     */
    private Boolean allChecked;
    /**
     * 购物车图片地址
     */
    private String imageHost;
}
