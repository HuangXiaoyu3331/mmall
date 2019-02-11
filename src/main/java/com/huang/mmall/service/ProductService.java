package com.huang.mmall.service;

import com.github.pagehelper.PageInfo;
import com.huang.mmall.bean.pojo.OrderItem;
import com.huang.mmall.bean.pojo.Product;
import com.huang.mmall.bean.vo.ProductDetailVo;
import com.huang.mmall.bean.vo.ProductListVo;
import com.huang.mmall.common.ServerResponse;

import java.util.List;

/**
 * 商品Service接口
 *
 * @author hxy
 * @date 2019/01/14
 */
public interface ProductService {

    /**
     * 添加商品
     *
     * @param product
     * @return
     */
    ServerResponse addProduct(Product product);

    /**
     * 更新商品信息
     *
     * @param product
     * @return
     */
    ServerResponse updateProduct(Product product);

    /**
     * 更新商品销售状态
     *
     * @param productId
     * @param status
     * @return
     */
    ServerResponse setSaleStatus(Integer productId, Integer status);

    /**
     * 获取商品详情
     *
     * @param productId 商品id
     * @return
     */
    ServerResponse<ProductDetailVo> getDetail(Integer productId);

    /**
     * 获取商品列表，分页
     *
     * @param pageNum  页码
     * @param pageSize 一页要显示的数量
     * @return
     */
    ServerResponse<PageInfo> getProductList(int pageNum, int pageSize);

    /**
     * 搜索商品
     *
     * @param productName 商品名称
     * @param productId   商品id
     * @param pageNum     页码
     * @param pageSize    每页显示数量
     * @return
     */
    ServerResponse<PageInfo> searchProduct(String productName, Integer productId, int pageNum, int pageSize);

    /**
     * 查询商品详情
     *
     * @param productId 商品id
     * @return
     */
    ServerResponse<ProductDetailVo> getProductDetail(Integer productId);

    /**
     * 搜索商品
     *
     * @param keyword    商品关键字
     * @param categoryId 分类id
     * @param pageNum    页码
     * @param pageSize   每页显示的数量
     * @param orderBy    排序规则
     * @return
     */
    ServerResponse<PageInfo> getProductByKeywordCategory(String keyword, Integer categoryId, Integer pageNum, Integer pageSize, String orderBy);

    /**
     * 根据商品id获取商品
     *
     * @param productId 商品id
     * @return
     */
    ServerResponse<Product> getProductById(Integer productId);

    /**
     * 获取商品详情
     *
     * @param productId 商品id
     * @return
     */
    ServerResponse<Product> get(Integer productId);

    /**
     * 更新商品库存
     *
     * @param orderItemList 商品列表
     * @return
     */
    ServerResponse reduceProductStock(List<OrderItem> orderItemList);
}
