package com.huang.mmall.serviceimpl;

import com.huang.mmall.bean.pojo.OrderItem;
import com.huang.mmall.common.ServerResponse;
import com.huang.mmall.dao.OrderItemMapper;
import com.huang.mmall.service.OrderItemService;
import net.sf.jsqlparser.schema.Server;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * OrderItemServiceImpl class
 *
 * @author hxy
 * @date 2019/1/30
 */
@Service
public class OrderItemServiceImpl implements OrderItemService {

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    public ServerResponse<List<OrderItem>> getByOrderNoUserId(Long orderNo, Integer userId) {
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNoAndUserId(orderNo, userId);
        if (CollectionUtils.isEmpty(orderItemList)) {
            return ServerResponse.createByErrorMessage("订单错误，找不到订单详情");
        } else {
            return ServerResponse.createBySuccess(orderItemList);
        }
    }

    @Override
    public ServerResponse<List<OrderItem>> getByOrderNo(Long orderNo) {
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(orderNo);
        if (CollectionUtils.isEmpty(orderItemList)) {
            return ServerResponse.createByErrorMessage("订单错误，找不到订单详情");
        } else {
            return ServerResponse.createBySuccess(orderItemList);
        }
    }

    @Override
    public ServerResponse batchInsert(List<OrderItem> orderItemList) {
        int resultCount = orderItemMapper.batchInsert(orderItemList);
        if (resultCount > 0) {
            return ServerResponse.createBySuccess();
        } else {
            return ServerResponse.createByErrorMessage("批量插入失败");
        }
    }
}
