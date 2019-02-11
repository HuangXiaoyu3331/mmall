package com.huang.mmall.bean.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * ProductListVo class
 *
 * @author hxy
 * @date 2019/1/14
 */
@Data
public class ProductListVo {
    private Integer id;
    private String name;
    private Integer CategoryId;
    private String subtitle;
    private String mainImage;
    private BigDecimal price;
    private Integer status;
    private String imageHost;
}
