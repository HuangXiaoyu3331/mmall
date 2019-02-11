package com.huang.mmall.bean.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * ProductDetailVo class
 *
 * @author hxy
 * @date 2019/1/14
 */
@Data
public class ProductDetailVo {
    private Integer id;
    private Integer categoryId;
    private String name;
    private String subtitle;
    private String mainImage;
    private String subImage;
    private String detail;
    private BigDecimal price;
    private Integer stock;
    private Integer status;
    private String createTime;
    private String updateTime;
    private Integer parentCategoryId;
    /**
     * 图片服务器地址
     */
    private String imageHost;
}
