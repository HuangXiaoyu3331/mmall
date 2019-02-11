package com.huang.mmall.serviceimpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.huang.mmall.bean.pojo.Shipping;
import com.huang.mmall.common.ResponseCode;
import com.huang.mmall.common.ServerResponse;
import com.huang.mmall.dao.ShippingMapper;
import com.huang.mmall.service.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * ShippingServiceImpl class
 *
 * @author hxy
 * @date 2019/1/28
 */
@Service
public class ShippingServiceImpl implements ShippingService {

    @Autowired
    private ShippingMapper shippingMapper;


    @Override
    public ServerResponse<Map> add(Integer userId, Shipping shipping) {
        shipping.setUserId(userId);
        int resultCount = shippingMapper.insert(shipping);
        if (resultCount > 0) {
            Map result = Maps.newHashMap();
            result.put("shippingId", shipping.getId());
            return ServerResponse.createBySuccess("新建地址成功", result);
        } else {
            return ServerResponse.createByErrorMessage("新建地址失败");
        }
    }

    @Override
    public ServerResponse<String> delete(Integer userId, Integer shippingId) {
        if (shippingId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        int resultCount = shippingMapper.deleteByUserIdAndShippingId(userId, shippingId);
        if (resultCount > 0) {
            return ServerResponse.createBySuccess("删除地址成功");
        } else {
            return ServerResponse.createByErrorMessage("删除地址失败");
        }
    }

    @Override
    public ServerResponse update(Integer userId, Shipping shipping) {
        //shipping的userId必须重新设置一下，否则可能是从前端床过来的userId，可能产生横向越权问题
        shipping.setUserId(userId);
        int resultCount = shippingMapper.updateShippingByUserId(shipping);
        if (resultCount > 0) {
            return ServerResponse.createBySuccess("更新地址成功");
        } else {
            return ServerResponse.createByErrorMessage("更新地址失败");
        }
    }

    @Override
    public ServerResponse<Shipping> select(Integer userId, Integer shippingId) {
        Shipping shipping = shippingMapper.selectByUserIdAndShippingId(userId,shippingId);
        if (shipping == null) {
            return ServerResponse.createByErrorMessage("查询地址失败");
        } else {
            return ServerResponse.createBySuccess("查询地址成功", shipping);
        }
    }

    @Override
    public ServerResponse<PageInfo> list(Integer userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Shipping> shippingList = shippingMapper.selectByUserId(userId);
        PageInfo pageInfo = new PageInfo(shippingList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    public ServerResponse<Shipping> get(Integer shippingId) {
        Shipping shipping = (Shipping) shippingMapper.selectByPrimaryKey(shippingId);
        if (shipping == null) {
            return ServerResponse.createByErrorMessage("获取地址信息失败");
        } else {
            return ServerResponse.createBySuccess(shipping);
        }
    }
}
