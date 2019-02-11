package com.huang.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.huang.mmall.bean.vo.OrderVo;
import com.huang.mmall.common.ServerResponse;
import com.huang.mmall.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * OrderManageController class
 *
 * @author hxy
 * @date 2019/2/9
 */
@RestController
@RequestMapping("/manage/order")
public class OrderManageController {

    @Autowired
    private OrderService orderService;

    /**
     * 分页查询所有订单
     *
     * @param pageNo   页码
     * @param pageSize 每页显示的数量
     * @return
     */
    @GetMapping("/list/{pageNo}/{pageSize}")
    public ServerResponse<PageInfo> orderList(@PathVariable("pageNo") int pageNo, @PathVariable("pageSize") int pageSize) {
        return orderService.manageList(pageNo, pageSize);
    }

    /**
     * 查询订单详情
     *
     * @param orderNo 订单号
     * @return
     */
    @GetMapping("/detail/{orderNo}")
    public ServerResponse<OrderVo> orderDetail(@PathVariable("orderNo") Long orderNo) {
        return orderService.manageDetail(orderNo);
    }

    /**
     * 根据订单号搜索商品
     *
     * @param orderNo  订单号
     * @param pageNo   页码
     * @param pageSize 每页显示的数量
     * @return
     */
    @GetMapping("/{orderNo}/{pageNo}/{pageSize}")
    public ServerResponse<PageInfo> searchOrder(Long orderNo, @PathVariable("pageNo") int pageNo, @PathVariable("pageSize") int pageSize) {
        return orderService.manageSearch(orderNo, pageNo, pageSize);
    }

    /**
     * 发货
     *
     * @param orderNo 订单号
     * @return
     */
    @PostMapping("/send_goods")
    public ServerResponse<String> orderSendGoods(Long orderNo) {
        return orderService.manageSendGoods(orderNo);
    }
}
