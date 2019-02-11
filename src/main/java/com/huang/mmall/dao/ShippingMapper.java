package com.huang.mmall.dao;

import com.huang.mmall.bean.pojo.Shipping;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * ShippingMapper
 *
 * @author hxy
 * @date 2019/1/28
 */
public interface ShippingMapper extends BaseMapper<Shipping> {

    /**
     * 删除指定用户的收获地址
     *
     * @param userId     用户id
     * @param shippingId 收获地址id
     * @return
     */
    int deleteByUserIdAndShippingId(@Param("userId") Integer userId, @Param("shippingId") Integer shippingId);

    /**
     * 根据用户id修改用户地址信息
     *
     * @param record 收货地址详情
     * @return
     */
    int updateShippingByUserIdSelective(Shipping record);

    /**
     * 根据用户id和地址id查询地址信息
     *
     * @param userId
     * @param shippingId
     * @return
     */
    Shipping selectByUserIdAndShippingId(@Param("userId") Integer userId, @Param("shippingId") Integer shippingId);

    /**
     * 根据用户id查询地址详情
     *
     * @param userId 用户id
     * @return
     */
    List<Shipping> selectByUserId(Integer userId);
}