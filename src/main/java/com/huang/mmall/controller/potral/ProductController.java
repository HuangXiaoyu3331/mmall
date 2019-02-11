package com.huang.mmall.controller.potral;

import com.github.pagehelper.PageInfo;
import com.huang.mmall.bean.vo.ProductDetailVo;
import com.huang.mmall.common.ServerResponse;
import com.huang.mmall.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ProductController class
 *
 * @author hxy
 * @date 2019/1/23
 */
@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 根据商品id获取商品信息
     *
     * @param productId 商品id
     * @return
     */
    @GetMapping("/{productId}")
    public ServerResponse<ProductDetailVo> detail(@PathVariable("productId") Integer productId) {
        return productService.getProductDetail(productId);
    }

    /**
     * 根据商品名称关键字搜索商品
     *
     * @param keyword  商品名称关键字
     * @param pageNum  页码
     * @param pageSize 每页显示大小
     * @param orderBy  排序规则
     * @return
     */
    @GetMapping("/keyword/{keyword}/{pageNum}/{pageSize}/{orderBy}")
    public ServerResponse<PageInfo> searchProductByKeyWord(@PathVariable("keyword") String keyword,
                                                           @PathVariable("pageNum") Integer pageNum,
                                                           @PathVariable("pageSize") Integer pageSize,
                                                           @PathVariable("orderBy") String orderBy) {
        return productService.getProductByKeywordCategory(keyword, null, pageNum, pageSize, orderBy);
    }

    /**
     * 根据商品分类id搜索商品
     *
     * @param categoryId 分类id
     * @param pageNum    页码
     * @param pageSize   每页显示大小
     * @param orderBy    排序规则
     * @return
     */
    @GetMapping("/category/{categoryId}/{pageNum}/{pageSize}/{orderBy}")
    public ServerResponse<PageInfo> searchProductByCategoryId(@PathVariable("categoryId") Integer categoryId,
                                                              @PathVariable("pageNum") Integer pageNum,
                                                              @PathVariable("pageSize") Integer pageSize,
                                                              @PathVariable("orderBy") String orderBy) {
        return productService.getProductByKeywordCategory(null, categoryId, pageNum, pageSize, orderBy);
    }

    /**
     * 根据商品名称关键字跟商品分类id搜索商品
     *
     * @param keyword    商品名称关键字
     * @param categoryId 分类id
     * @param pageNum    页码
     * @param pageSize   每页显示大小
     * @param orderBy    排序规则
     * @return
     */
    @GetMapping("/{keyword}/{categoryId}/{pageNum}/{pageSize}/{orderBy}")
    public ServerResponse<PageInfo> searchProductByKeywordAndCategoryId(@PathVariable("keyword") String keyword,
                                                                        @PathVariable("categoryId") Integer categoryId,
                                                                        @PathVariable("pageNum") Integer pageNum,
                                                                        @PathVariable("pageSize") Integer pageSize,
                                                                        @PathVariable("orderBy") String orderBy) {
        return productService.getProductByKeywordCategory(keyword, categoryId, pageNum, pageSize, orderBy);
    }
}
