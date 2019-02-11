package com.huang.mmall.dao;

import com.huang.mmall.bean.pojo.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * ProductMapper
 *
 * @author hxy
 * @date 2019/01/14
 */
public interface ProductMapper extends BaseMapper<Product> {
    /**
     * 查询所有商品
     *
     * @return
     */
    List<Product> selectList();

    List<Product> selectByNameAndProductId(@Param("productName") String productName, @Param("productId") Integer productId);

    /**
     * 搜索商品
     *
     * @param keyword    商品名称关键字
     * @param categoryIdList 商品分类列表
     * @return
     */
    List<Product> selectByNameAndCategoryIds(@Param("keyword") String keyword, @Param("categoryIdList") List<Integer> categoryIdList);
}