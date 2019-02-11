package com.huang.mmall.controller.potral;

import com.github.pagehelper.PageInfo;
import com.huang.mmall.bean.pojo.Shipping;
import com.huang.mmall.bean.pojo.User;
import com.huang.mmall.common.Const;
import com.huang.mmall.common.ServerResponse;
import com.huang.mmall.service.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * ShippingController class
 *
 * @author hxy
 * @date 2019/1/28
 */
@RestController
@RequestMapping("/shipping")
public class ShippingController {

    @Autowired
    private ShippingService shippingService;

    @PostMapping
    public ServerResponse<Map> add(HttpSession session, @RequestBody Shipping shipping) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        return shippingService.add(user.getId(), shipping);
    }

    @DeleteMapping("/{shippingId}")
    public ServerResponse<String> add(HttpSession session, @PathVariable("shippingId") Integer shippingId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        return shippingService.delete(user.getId(), shippingId);
    }

    /**
     * 更新用户地址
     *
     * @param session  会话session
     * @param shipping 用户地址详情
     * @return
     */
    @PutMapping
    public ServerResponse update(HttpSession session, @RequestBody Shipping shipping) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        return shippingService.update(user.getId(), shipping);
    }

    /**
     * 查询地址详情
     *
     * @param session
     * @param shippingId
     * @return
     */
    @GetMapping("/{shippingId}")
    public ServerResponse<Shipping> select(HttpSession session, @PathVariable("shippingId") Integer shippingId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        return shippingService.select(user.getId(), shippingId);
    }

    @GetMapping("/{pageNum}/{pageSize}")
    public ServerResponse<PageInfo> list(@PathVariable("pageNum") Integer pageNum,
                                         @PathVariable("pageSize") Integer pageSize,
                                         HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        return shippingService.list(user.getId(), pageNum, pageSize);
    }

}
