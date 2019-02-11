package com.huang.mmall.dao;

import com.huang.mmall.bean.pojo.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 根据订单号和用户id查询订单是否存在
     *
     * @param userId  用户id
     * @param orderNo 订单号
     * @return
     */
    Order selectByUserIdAndOrderNo(@Param("userId") Integer userId, @Param("orderNo") Long orderNo);

    /**
     * 根据订单编号查找订单
     *
     * @param orderNo 订单编号
     * @return
     */
    Order selectByOrderNo(Long orderNo);

    /**
     * 查询用户订单
     *
     * @param userId 用户id
     * @return
     */
    List<Order> selectByUserId(Integer userId);

    /**
     * 查询所有订单
     *
     * @return
     */
    @Override
    List<Order> selectAll();
}