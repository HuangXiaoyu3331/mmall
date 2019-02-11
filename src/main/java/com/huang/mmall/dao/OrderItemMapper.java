package com.huang.mmall.dao;

import com.huang.mmall.bean.pojo.OrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * OrderItemMapper
 *
 * @author hxy
 * @date 2019/1/29
 */
public interface OrderItemMapper extends BaseMapper<OrderItem> {
    /**
     * 根据订单号和用户id查询订单详情
     *
     * @param orderNo 订单号
     * @param userId  用户id
     * @return
     */
    List<OrderItem> selectByOrderNoAndUserId(@Param("orderNo") Long orderNo, @Param("userId") Integer userId);

    /**
     * 根据订单号查询订单详情
     *
     * @param orderNo 订单号
     * @return
     */
    List<OrderItem> selectByOrderNo(Long orderNo);

    /**
     * 批量插入
     *
     * @param orderItemList
     */
    int batchInsert(@Param("orderItemList") List<OrderItem> orderItemList);


}