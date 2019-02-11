package com.huang.mmall.service;

import com.huang.mmall.bean.pojo.OrderItem;
import com.huang.mmall.common.ServerResponse;

import java.util.List;

/**
 * OrderService interface
 *
 * @author hxy
 * @date 2019/1/29
 */
public interface OrderItemService {
    /**
     * 查询用户某订单的商品详情
     *
     * @param orderNo 订单号
     * @param userId  用户id
     * @return
     */
    ServerResponse<List<OrderItem>> getByOrderNoUserId(Long orderNo, Integer userId);

    /**
     * 查询用户某订单的商品详情
     *
     * @param orderNo 订单号
     * @return
     */
    ServerResponse<List<OrderItem>> getByOrderNo(Long orderNo);

    /**
     * 批量插入
     *
     * @param orderItemList 订单明细item
     * @return
     */
    ServerResponse batchInsert(List<OrderItem> orderItemList);
}
