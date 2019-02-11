package com.huang.mmall.service;

import com.github.pagehelper.PageInfo;
import com.huang.mmall.bean.pojo.Shipping;
import com.huang.mmall.common.ServerResponse;

import java.util.Map;

/**
 * ShippingServiceImpl interface
 *
 * @author hxy
 * @date 2019/1/28
 */
public interface ShippingService {

    /**
     * 新增用户收获地址
     *
     * @param userId   用户id
     * @param shipping 收获地址信息
     * @return
     */
    ServerResponse<Map> add(Integer userId, Shipping shipping);

    /**
     * 删除用户地址信息
     *
     * @param userId     用户id
     * @param shippingId 收获地址id
     * @return
     */
    ServerResponse<String> delete(Integer userId, Integer shippingId);

    /**
     * 更新用户地址
     *
     * @param userId   用户id
     * @param shipping 用户地址详情
     * @return
     */
    ServerResponse update(Integer userId, Shipping shipping);

    /**
     * 查询用户地址
     *
     * @param userId     用户id
     * @param shippingId 商品id
     * @return
     */
    ServerResponse<Shipping> select(Integer userId, Integer shippingId);

    /**
     * 分页查询用户地址
     *
     * @param userId   用户id
     * @param pageNum  页码
     * @param pageSize 每页显示的数量
     * @return
     */
    ServerResponse<PageInfo> list(Integer userId, Integer pageNum, Integer pageSize);

    /**
     * 根据shippingId获取地址信息详情
     *
     * @param shippingId shippingId
     * @return
     */
    ServerResponse<Shipping> get(Integer shippingId);
}
