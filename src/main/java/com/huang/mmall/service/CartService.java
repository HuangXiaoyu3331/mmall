package com.huang.mmall.service;

import com.huang.mmall.bean.pojo.Cart;
import com.huang.mmall.bean.vo.CartVo;
import com.huang.mmall.common.ServerResponse;

import java.util.List;

/**
 * CartService class
 *
 * @author hxy
 * @date 2019/1/25
 */
public interface CartService {

    /**
     * 添加商品至购物车
     *
     * @param userId    用户id
     * @param productId 商品id
     * @param count     商品数量
     * @return
     */
    ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count);

    /**
     * 更新购物车中商品的数量
     *
     * @param userId    用户id
     * @param productId 商品id
     * @param count     商品数量
     * @return
     */
    ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count);

    /**
     * 删除购物车商品
     *
     * @param userId     用户id
     * @param productIds 商品id列表，用“,”分割
     * @return
     */
    ServerResponse<CartVo> delete(Integer userId, String productIds);

    /**
     * 获取用户购物车中商品
     *
     * @param userId 用户id
     * @return
     */
    ServerResponse<CartVo> list(Integer userId);

    /**
     * 更改商品选择状态
     *
     * @param userId    用户id
     * @param checked   全选|反选
     * @param productId 商品id
     * @return
     */
    ServerResponse<CartVo> selectOrUnSelect(Integer userId, Integer checked, Integer productId);

    /**
     * 获取用户购物车中商品的数量
     *
     * @param userId 用户id
     * @return
     */
    ServerResponse<Integer> getProductCount(Integer userId);

    /**
     * 获取用户购物车中选中的商品
     *
     * @param userId 用户id
     * @return
     */
    ServerResponse<List<Cart>> getCheckedCartByUserId(Integer userId);

    /**
     * 清空购物车
     *
     * @param cartList 购物车集合
     * @return
     */
    ServerResponse cleanCart(List<Cart> cartList);
}
