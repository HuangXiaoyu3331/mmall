package com.huang.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.huang.mmall.bean.pojo.Product;
import com.huang.mmall.bean.vo.ProductDetailVo;
import com.huang.mmall.bean.vo.ProductListVo;
import com.huang.mmall.common.ServerResponse;
import com.huang.mmall.service.FileService;
import com.huang.mmall.service.ProductService;
import com.huang.mmall.util.FtpUtil;
import com.huang.mmall.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 后台商品模块controller
 *
 * @author hxy
 * @date 2019/01/14
 */
@RestController
@RequestMapping("/manage/product")
public class ProductManageController {

    @Autowired
    private ProductService productService;
    @Autowired
    private FileService fileService;

    /**
     * 新增商品
     *
     * @param product
     * @return
     */
    @PostMapping
    public ServerResponse saveProduct(@RequestBody Product product) {
        return productService.addProduct(product);
    }

    /**
     * 更新商品
     *
     * @param product
     * @return
     */
    @PutMapping
    public ServerResponse updateProduct(@RequestBody Product product) {
        return productService.updateProduct(product);
    }

    /**
     * 更新商品状态
     *
     * @param productId
     * @param status
     * @return
     */
    @PutMapping("/status/{productId}")
    public ServerResponse setSaleStatus(@PathVariable("productId") Integer productId, Integer status) {
        return productService.setSaleStatus(productId, status);
    }

    /**
     * 获取商品详情
     *
     * @param productId 商品id
     * @return
     */
    @GetMapping("/{productId}")
    public ServerResponse<ProductDetailVo> getDetail(@PathVariable("productId") Integer productId) {
        return productService.getDetail(productId);
    }

    /**
     * 查询商品列表
     *
     * @param pageNum  页码
     * @param pageSize 每页显示的条数
     * @return
     */
    @GetMapping("/list/{pageNum}/{pageSize}")
    public ServerResponse<PageInfo> getList(@PathVariable int pageNum, @PathVariable int pageSize) {
        return productService.getProductList(pageNum, pageSize);
    }

    /**
     * 按商品名称和商品id搜索商品
     *
     * @param productName 商品名称
     * @param productId   商品id
     * @param pageNum     页码
     * @param pageSize    每页显示条数
     * @return
     */
    @GetMapping("/{productName}/{productId}/{pageNum}/{pageSize}")
    public ServerResponse<PageInfo> productSearch(@PathVariable("productName") String productName,
                                                  @PathVariable("productId") Integer productId,
                                                  @PathVariable("pageNum") Integer pageNum,
                                                  @PathVariable("pageSize") Integer pageSize) {
        return productService.searchProduct(productName, productId, pageNum, pageSize);
    }

    /**
     * 按商品名称搜索商品
     *
     * @param productName 商品名称
     * @param pageNum     页码
     * @param pageSize    每页显示条数
     * @return
     */
    @GetMapping("/keyword/{productName}/{pageNum}/{pageSize}")
    public ServerResponse<PageInfo> productSearch(@PathVariable("productName") String productName,
                                                  @PathVariable("pageNum") Integer pageNum,
                                                  @PathVariable("pageSize") Integer pageSize) {
        return productService.searchProduct(productName, null, pageNum, pageSize);
    }

    /**
     * 按商品id搜索商品
     *
     * @param productId 商品id
     * @param pageNum   页码
     * @param pageSize  每页显示条数
     * @return
     */
    @GetMapping("/{productId}/{pageNum}/{pageSize}")
    public ServerResponse<PageInfo> productSearch(@PathVariable("productId") Integer productId,
                                                  @PathVariable("pageNum") Integer pageNum,
                                                  @PathVariable("pageSize") Integer pageSize) {
        return productService.searchProduct(null, productId, pageNum, pageSize);
    }

    @PostMapping("/upload")
    public ServerResponse upload(MultipartFile file, HttpServletRequest request) {
//        String path = request.getSession().getServletContext().getRealPath("upload");
        String path = PropertiesUtil.getProperty("web.upload.path");
        String targetFileName = fileService.upload(file, path);
        if (StringUtils.isBlank(targetFileName)) {
            return ServerResponse.createByErrorMessage("上传文件失败");
        }
        String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
        Map map = Maps.newHashMap();
        map.put("uri", targetFileName);
        map.put("url", url);
        return ServerResponse.createBySuccess(map);
    }

    @PostMapping("rich_text_img_upload")
    public Map richTextImgUpload(MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        //富文本上传前端用的是simditor插件，该插件对富文本上传返回的数据格式有自己的规定，详情可以看官网https://simditor.tower.im/docs/doc-config.html
        //返回值示例
//        {
//            "success": true/false,
//            "msg": "error message", # optional
//            "file_path": "[real file path]"
//        }
        Map map = Maps.newHashMap();
        String path = request.getSession().getServletContext().getRealPath("upload");
        String targetFileName = fileService.upload(file, path);
        if (StringUtils.isBlank(targetFileName)) {
            map.put("success", false);
            map.put("msg", "上传文件失败");
            return map;
        }
        String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
        map.put("success", true);
        map.put("msg", "上传文件成功");
        map.put("file_path", url);
        //simditor上传成功需要返回这个返回头
        response.addHeader("Access-Control-Allow-Headers", "X-File-Name");
        return map;
    }


}