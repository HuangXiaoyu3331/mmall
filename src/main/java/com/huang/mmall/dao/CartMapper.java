package com.huang.mmall.dao;

import com.huang.mmall.bean.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通用mapper
 *
 * @author hxy
 * @date 2019/01/14
 */
public interface CartMapper extends BaseMapper<Cart> {
    /**
     * 根据用户id跟商品id查询
     *
     * @param userId    用户id
     * @param productId 商品id
     * @return
     */
    Cart selectByUserIdAndProductId(@Param("userId") Integer userId, @Param("productId") Integer productId);

    /**
     * 根据用户id查询用户的购物车
     *
     * @param userId
     * @return
     */
    List<Cart> selectCartByUserId(Integer userId);

    /**
     * 根据用户id查询购物车是否全选
     *
     * @param userId 用户id
     * @return
     */
    int selectCartProductCheckedStatusByUserId(Integer userId);

    /**
     * 删除用户购物车的指定商品
     *
     * @param userId        用户id
     * @param productIdList 商品列表
     * @return
     */
    int deleteByUserIdProductIds(@Param("userId") Integer userId, @Param("productIdList") List<String> productIdList);

    /**
     * 更改商品选中状态
     *
     * @param userId    用户id
     * @param checked   全选|反选
     * @param productId 商品id
     * @return
     */
    int checkedOrUncheckedProduct(@Param("userId") Integer userId, @Param("checked") Integer checked, @Param("productId") Integer productId);

    /**
     * 查询用户的购物车中商品数量
     *
     * @param userId 用户id
     * @return
     */
    int selectProductCountByUserId(Integer userId);

    /**
     * 查询已经勾选的商品
     *
     * @param userId 用户id
     * @return
     */
    List<Cart> selectCheckedCartByUserId(Integer userId);
}