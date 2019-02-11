package com.huang.mmall.controller.potral;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.huang.mmall.bean.pojo.User;
import com.huang.mmall.bean.vo.OrderVo;
import com.huang.mmall.common.Const;
import com.huang.mmall.common.ServerResponse;
import com.huang.mmall.service.OrderService;
import com.huang.mmall.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * OrderController class
 *
 * @author hxy
 * @date 2019/1/29
 */
@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 创建订单
     *
     * @param session    会话session
     * @param shippingId 地址id
     * @return
     */
    @PostMapping("/{shippingId}")
    public ServerResponse<OrderVo> createOrder(HttpSession session, @PathVariable("shippingId") Integer shippingId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        return orderService.createOrder(user.getId(), shippingId);
    }

    /**
     * 取消订单
     *
     * @param session 会话sesison
     * @param orderNo 订单号
     * @return
     */
    @DeleteMapping("/{orderNo}")
    public ServerResponse<String> cancel(HttpSession session, @PathVariable("orderNo") Long orderNo) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        return orderService.cancel(user.getId(), orderNo);
    }

    /**
     * 获取购物车中已经选中的商品
     *
     * @param session 会话session
     * @return
     */
    @GetMapping("/cart")
    public ServerResponse getOrderCartProduct(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        return orderService.getOrderCartProduct(user.getId());
    }

    /**
     * 支付订单接口
     *
     * @param orderNo 订单号
     * @param session 会话session
     * @return
     */
    @PostMapping("/pay")
    public ServerResponse<Map<String, String>> pay(Long orderNo, HttpSession session) {
        String path = PropertiesUtil.getProperty("web.upload.path");
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        return orderService.pay(user.getId(), orderNo, path);
    }

    /**
     * 获取订单详情
     *
     * @param orderNo 订单号
     * @return
     */
    @GetMapping("/{orderNo}")
    public ServerResponse<OrderVo> detail(HttpSession session, @PathVariable("orderNo") Long orderNo) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        return orderService.getOrderDetail(user.getId(), orderNo);
    }

    /**
     * 获取用户订单
     *
     * @param session  会话id
     * @param pageNo   页码
     * @param pageSize 每页显示的数量
     * @return
     */
    @GetMapping("/list/{pageNo}/{pageSize}")
    public ServerResponse<PageInfo> list(HttpSession session, @PathVariable("pageNo") int pageNo,
                                         @PathVariable("pageSize") int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        return orderService.getOrderList(user.getId(), pageNo, pageSize);
    }

    /**
     * 支付宝回调接口
     *
     * @param request
     * @return
     */
    @PostMapping("/alipay_callback")
    public Object alipayCallback(HttpServletRequest request) {
        Map<String, String> params = Maps.newHashMap();
        Map<String, String[]> requestParams = request.getParameterMap();
//        for (Iterator iterator = requestParams.keySet().iterator(); iterator.hasNext(); ) {
//
//        }
        //推荐使用java8的lambda表达式
        requestParams.forEach((key, values) -> {
            // 也可以使用这种方法，方便点 String valueStr = StringUtils.join(value, ",")
            // 使用下面的方法存粹是练习的时候使用
            String valueStr = StringUtils.EMPTY;
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(key, valueStr);
        });
        log.info("支付宝回调，sign:{}，trade_status:{}，参数:{}", params.get("sign"), params.get("trade_status"), params.toString());
        //非常重要，验证回调的正确性，并避免重复通知
        //详情请看支付宝官方文档https://docs.open.alipay.com/194/103296/
        params.remove("sign_type");
        try {
            boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(), "utf-8", Configs.getSignType());
            if (!alipayRSACheckedV2) {
                return ServerResponse.createByErrorMessage("非法请求，验证不通过");
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        //TODO 验证各种数据（可方service中）

        ServerResponse serverResponse = orderService.alipayCallback(params);
        if (serverResponse.isSuccess()) {
            return Const.AlipayCallback.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallback.RESPONSE_FAILED;
    }

    /**
     * 查询订单支付状态
     *
     * @param session 会话session
     * @param orderNo 订单号
     * @return
     */
    @GetMapping("/status/{orderNo}")
    public ServerResponse<Boolean> queryOrderPayStatus(HttpSession session, @PathVariable("orderNo") Long orderNo) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        ServerResponse serverResponse = orderService.queryOrderPayStatus(user.getId(), orderNo);
        if (serverResponse.isSuccess()) {
            return ServerResponse.createBySuccess(true);
        } else {
            return ServerResponse.createBySuccess(false);
        }
    }
}
