package com.huang.mmall.controller.potral;

import com.huang.mmall.bean.pojo.User;
import com.huang.mmall.bean.vo.CartVo;
import com.huang.mmall.common.Const;
import com.huang.mmall.common.ServerResponse;
import com.huang.mmall.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * CartController class
 *
 * @author hxy
 * @date 2019/1/25
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 添加商品至购物车
     *
     * @param productId 商品id
     * @param count     添加数量
     * @param session   会话session
     * @return
     */
    @PostMapping
    public ServerResponse<CartVo> add(Integer productId, Integer count, HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        return cartService.add(user.getId(), productId, count);
    }

    /**
     * 修改购物车商品数量
     *
     * @param productId 商品id
     * @param count     修改数量
     * @param session   会话session
     * @return
     */
    @PutMapping
    public ServerResponse<CartVo> update(Integer productId, Integer count, HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        return cartService.update(user.getId(), productId, count);
    }


    /**
     * 删除购物车中的指定商品
     * 这里也可以用restful风格的api，设计为@DeleteMapping("/{productIds}),然后请求示例是localhost:8080/cart/product/11,33.44。productId用“,”分割
     * 但是这样有一个问题，就是当删除的商品比较多的时候，有可能就会草果url的长度限制。
     * 所以，个人理解，不必过分追求restful风格架构，有一些api改成restful风格反而不太合适
     *
     * @param productIds 商品id列表
     * @param session    会话session
     * @return
     */
    @DeleteMapping("/{productIds}")
    public ServerResponse<CartVo> delete(@PathVariable("productIds") String productIds, HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        return cartService.delete(user.getId(), productIds);
    }

    /**
     * 查询购物车商品
     *
     * @param session 会话session
     * @return
     */
    @GetMapping
    public ServerResponse<CartVo> list(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        return cartService.list(user.getId());
    }

    /**
     * 全选商品
     *
     * @param session 会话session
     * @return
     */
    @PutMapping("/product/checked")
    public ServerResponse<CartVo> selectAll(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        return cartService.selectOrUnSelect(user.getId(), Const.Cart.CHECKED, null);
    }

    /**
     * 全反选商品
     *
     * @param session 会话session
     * @return
     */
    @PutMapping("/product/unchecked")
    public ServerResponse<CartVo> unSelectAll(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        return cartService.selectOrUnSelect(user.getId(), Const.Cart.UN_CHECKED, null);
    }

    /**
     * 选择商品
     *
     * @param session   会话session
     * @param productId 商品id
     * @return
     */
    @PutMapping("/product/{productId}/checked")
    public ServerResponse<CartVo> select(HttpSession session, @PathVariable("productId") Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        return cartService.selectOrUnSelect(user.getId(), Const.Cart.CHECKED, productId);
    }

    /**
     * 反选商品
     *
     * @param session   会话session
     * @param productId 商品id
     * @return
     */
    @PutMapping("/product/{productId}/unchecked")
    public ServerResponse<CartVo> unSelect(HttpSession session, @PathVariable("productId") Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        return cartService.selectOrUnSelect(user.getId(), Const.Cart.UN_CHECKED, productId);
    }

    /**
     * 获得购物车中商品的数量
     *
     * @param session 会话session
     * @return
     */
    @GetMapping("/product/count")
    public ServerResponse<Integer> getProductCount(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        //用户未登录
        if (user == null) {
            return ServerResponse.createBySuccess(0);
        } else {
            return cartService.getProductCount(user.getId());
        }
    }

}