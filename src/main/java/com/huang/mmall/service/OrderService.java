package com.huang.mmall.service;

import com.github.pagehelper.PageInfo;
import com.huang.mmall.bean.pojo.Cart;
import com.huang.mmall.bean.pojo.OrderItem;
import com.huang.mmall.bean.vo.OrderProductVo;
import com.huang.mmall.bean.vo.OrderVo;
import com.huang.mmall.common.ServerResponse;

import java.util.List;
import java.util.Map;

/**
 * OrderService class
 *
 * @author hxy
 * @date 2019/1/29
 */
public interface OrderService {

    /**
     * 创建订单
     *
     * @param userId     用户id
     * @param shippingId 地址id
     * @return
     */
    ServerResponse<OrderVo> createOrder(Integer userId, Integer shippingId);

    /**
     * 取消订单
     *
     * @param userId  用户id
     * @param orderNo 订单号
     * @return
     */
    ServerResponse<String> cancel(Integer userId, Long orderNo);

    /**
     * 获取购物车中已经选中的商品
     *
     * @param userId 用户id
     * @return
     */
    ServerResponse<OrderProductVo> getOrderCartProduct(Integer userId);

    /**
     * 根据购物车商品获取订单明细
     *
     * @param userId   用户id
     * @param cartList 购物车选中商品
     * @return
     */
    ServerResponse<List<OrderItem>> getCartOrderItem(Integer userId, List<Cart> cartList);

    ServerResponse<PageInfo> getOrderList(Integer userId, int pageNo, int pageSizs);

    /**
     * 获取订单详情
     *
     * @param userId  用户id
     * @param orderNo 订单号
     * @return
     */
    ServerResponse<OrderVo> getOrderDetail(Integer userId, Long orderNo);

    /**
     * 支付
     *
     * @param userId  用户id
     * @param orderNo 订单号
     * @param path    二维码保存路径
     * @return
     */
    ServerResponse<Map<String, String>> pay(Integer userId, Long orderNo, String path);

    /**
     * 支付宝回调
     *
     * @param params
     * @return
     */
    ServerResponse alipayCallback(Map<String, String> params);

    /**
     * 查询订单的支付状态
     *
     * @param userId  用户id
     * @param orderNo 订单id
     * @return
     */
    ServerResponse queryOrderPayStatus(Integer userId, Long orderNo);

    //backend

    /**
     * 查询所有订单
     *
     * @param pageNo   页码
     * @param pageSize 每页显示的数量
     * @return
     */
    ServerResponse<PageInfo> manageList(int pageNo, int pageSize);

    /**
     * 获取订单详情
     *
     * @param orderNo 订单号
     * @return
     */
    ServerResponse<OrderVo> manageDetail(Long orderNo);

    /**
     * 根据订单号搜索订单
     *
     * @param orderNo  订单号
     * @param pageNo   页码
     * @param pageSize 每页显示的数量
     * @return
     */
    ServerResponse<PageInfo> manageSearch(Long orderNo, int pageNo, int pageSize);


    /**
     * 发货
     *
     * @param orderNo 订单号
     * @return
     */
    ServerResponse<String> manageSendGoods(Long orderNo);
}
