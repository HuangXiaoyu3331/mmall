package com.huang.mmall.serviceimpl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.huang.mmall.bean.pojo.Cart;
import com.huang.mmall.bean.pojo.Product;
import com.huang.mmall.bean.vo.CartProductVo;
import com.huang.mmall.bean.vo.CartVo;
import com.huang.mmall.common.Const;
import com.huang.mmall.common.ResponseCode;
import com.huang.mmall.common.ServerResponse;
import com.huang.mmall.dao.CartMapper;
import com.huang.mmall.service.CartService;
import com.huang.mmall.service.ProductService;
import com.huang.mmall.util.BigDecimalUtil;
import com.huang.mmall.util.PropertiesUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * CartServiceImpl class
 *
 * @author hxy
 * @date 2019/1/25
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductService productService;

    @Override
    public ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count) {
        if (productId == null || count == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectByUserIdAndProductId(userId, productId);
        //商品不在购物车中，需要新增一个
        if (cart == null) {
            Cart cartItem = new Cart();
            cartItem.setQuantity(count);
            //默认选中状态
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);
            cartMapper.insert(cartItem);
        } else { //商品在购物车中，数量相加
            cart.setQuantity(count + cart.getQuantity());
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        //转成vo对象返回给前端
        return list(userId);
    }

    @Override
    public ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count) {
        if (productId == null || count == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectByUserIdAndProductId(userId, productId);
        if (cart != null) {
            cart.setQuantity(count);
        }
        cartMapper.updateByPrimaryKeySelective(cart);
        return list(userId);
    }

    @Override
    public ServerResponse<CartVo> delete(Integer userId, String productIds) {
        //guava中的工具类，直接分割后转成数组
        List<String> productList = Splitter.on(",").splitToList(productIds);
        if (CollectionUtils.isEmpty(productList)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        int resultCount = cartMapper.deleteByUserIdProductIds(userId, productList);
        if (resultCount > 0) {
            return list(userId);
        }
        return ServerResponse.createByErrorMessage("删除商品失败，请联系管理员");
    }

    @Override
    public ServerResponse<CartVo> list(Integer userId) {
        CartVo cartVo = getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    @Override
    public ServerResponse<CartVo> selectOrUnSelect(Integer userId, Integer checked, Integer productId) {
        int resultCount = cartMapper.checkedOrUncheckedProduct(userId, checked, productId);
        if (resultCount > 0) {
            return list(userId);
        } else {
            return ServerResponse.createByErrorMessage("全选或反选商品失败！请联系管理员");
        }
    }

    @Override
    public ServerResponse<Integer> getProductCount(Integer userId) {
        int resultCount = cartMapper.selectProductCountByUserId(userId);
        return ServerResponse.createBySuccess(resultCount);
    }

    @Override
    public ServerResponse<List<Cart>> getCheckedCartByUserId(Integer userId) {
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);
        if (CollectionUtils.isEmpty(cartList)) {
            return ServerResponse.createByErrorMessage("购物车为空");
        }
        return ServerResponse.createBySuccess(cartList);
    }

    @Override
    public ServerResponse cleanCart(List<Cart> cartList) {
        if (CollectionUtils.isEmpty(cartList)) {
            return ServerResponse.createByErrorMessage("清空购物车失败！购物车为空");
        }
        cartList.forEach(cart -> {
            cartMapper.deleteByPrimaryKey(cart);
        });
        return ServerResponse.createBySuccessMessage("清空购物车成功");
    }

    /**
     * 构建购物车对象，如果商品超出库存，则返回库存数量
     *
     * @param userId 用户id
     * @return
     */
    private CartVo getCartVoLimit(Integer userId) {
        CartVo cartVo = new CartVo();
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);
        List<CartProductVo> cartProductVoList = Lists.newArrayList();
        //初始化购物车的总价
        BigDecimal cartTotalPrice = new BigDecimal("0");

        if (CollectionUtils.isNotEmpty(cartList)) {
            for (Cart cartItem : cartList) {
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setUserId(cartItem.getUserId());
                cartProductVo.setProductId(cartItem.getProductId());

                ServerResponse<Product> response = productService.getProductById(cartItem.getProductId());
                //如果查找得到该商品
                if (response.isSuccess()) {
                    Product product = response.getData();
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStock(product.getStock());
                    //判断库存
                    int buyLimitCount = 0;
                    //商品库存 > 要购买的数量
                    if (product.getStock() > cartItem.getQuantity()) {
                        buyLimitCount = cartItem.getQuantity();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    } else {
                        buyLimitCount = product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        //购物车中更新有效库存
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(cartItem.getId());
                        cartForQuantity.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }
                    cartProductVo.setQuantity(buyLimitCount);
                    //计算某个商品总价
                    cartProductVo.setProductPrice(BigDecimalUtil.multiply(product.getPrice().doubleValue(), cartProductVo.getQuantity()));
                    cartProductVo.setProductChecked(cartItem.getChecked());
                }
                //如果商品是勾选的，增加到购物车总价中
                if (cartItem.getChecked() == Const.Cart.CHECKED) {
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(), cartProductVo.getProductTotalPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);
            }
        }
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setAllChecked(getAllCheckedStatus(userId));
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return cartVo;
    }

    /**
     * 根据用户id获取购物车商品是否是全选状态
     *
     * @param userId 用户id
     * @return
     */
    private boolean getAllCheckedStatus(Integer userId) {
        if (userId == null) {
            return false;
        }
        return cartMapper.selectCartProductCheckedStatusByUserId(userId) == 0;
    }
}
