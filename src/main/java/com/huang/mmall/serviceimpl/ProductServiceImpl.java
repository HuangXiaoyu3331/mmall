package com.huang.mmall.serviceimpl;

import com.github.dozermapper.core.Mapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.huang.mmall.bean.pojo.Category;
import com.huang.mmall.bean.pojo.OrderItem;
import com.huang.mmall.bean.pojo.Product;
import com.huang.mmall.bean.vo.ProductDetailVo;
import com.huang.mmall.bean.vo.ProductListVo;
import com.huang.mmall.common.Const;
import com.huang.mmall.common.ResponseCode;
import com.huang.mmall.common.ServerResponse;
import com.huang.mmall.dao.ProductMapper;
import com.huang.mmall.service.CategoryService;
import com.huang.mmall.service.ProductService;
import com.huang.mmall.util.DateTimeUtil;
import com.huang.mmall.util.PropertiesUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ProductServiceImpl class
 *
 * @author hxy
 * @date 2019/1/14
 */
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private Mapper mapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryService categoryService;

    @Override
    public ServerResponse addProduct(Product product) {
        if (product == null) {
            return ServerResponse.createByErrorMessage("新增产品参数不正确");
        } else {
            //子图的第一个图片作为主图
            if (StringUtils.isNotBlank(product.getSubImages())) {
                String[] subImageArray = product.getSubImages().split(",");
                if (subImageArray.length > 0) {
                    product.setMainImage(subImageArray[0]);
                }
            }
            int resultCount = productMapper.insert(product);
            if (resultCount == 0) {
                return ServerResponse.createByErrorMessage("新增产品失败");
            } else {
                return ServerResponse.createBySuccessMessage("新增产品成功");
            }
        }
    }

    @Override
    public ServerResponse updateProduct(Product product) {
        if (product == null) {
            return ServerResponse.createByErrorMessage("更新产品参数不正确");
        } else {
            //子图的第一个图片作为主图
            if (StringUtils.isNotBlank(product.getSubImages())) {
                String[] subImageArray = product.getSubImages().split(",");
                if (subImageArray.length > 0) {
                    product.setMainImage(subImageArray[0]);
                }
            }
            int resultCount = productMapper.updateByPrimaryKeySelective(product);
            if (resultCount == 0) {
                return ServerResponse.createByErrorMessage("更新产品失败");
            } else {
                return ServerResponse.createBySuccessMessage("更新产品成功");
            }
        }
    }

    @Override
    public ServerResponse setSaleStatus(Integer productId, Integer status) {
        if (productId == null || status == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int resultCount = productMapper.updateByPrimaryKeySelective(product);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("修改产品销售状态失败");
        } else {
            return ServerResponse.createBySuccessMessage("修改商品状态成功");
        }
    }

    @Override
    public ServerResponse<ProductDetailVo> getDetail(Integer productId) {
        //这里不用判断商品id是否为空，因为用restful风格的url，如果productId为空，是不会进入到controller来的
        Product product = (Product) productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.createByErrorMessage("产品已下架，或者删除");
        } else {
            //pojo --> bo(business object) --> vo(value object)
            ProductDetailVo productDetailVo = assembleProductDetailVo(product);
            return ServerResponse.createBySuccess(productDetailVo);
        }
    }

    @Override
    public ServerResponse<PageInfo> getProductList(int pageNum, int pageSize) {
        //1、 startPage -> 开始分页
        PageHelper.startPage(pageNum, pageSize);
        //2、 填充自己的sql查询逻辑
        List<Product> productList = productMapper.selectList();
        List<ProductListVo> productListVoList = Lists.newArrayList();
        productList.forEach(product -> {
            productListVoList.add(assembleProductListVo(product));
        });
        //3、 pageHelper 收尾
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    @Override
    public ServerResponse<PageInfo> searchProduct(String productName, Integer productId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        //拼接查询条件
        if (StringUtils.isNotBlank(productName)) {
            productName = new StringBuffer().append("%").append(productName).append("%").toString();
        }
        List<Product> productList = productMapper.selectByNameAndProductId(productName, productId);
        List<ProductListVo> productListVoList = Lists.newArrayList();
        productList.forEach(product -> {
            productListVoList.add(assembleProductListVo(product));
        });
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    @Override
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId) {
        Product product = (Product) productMapper.selectByPrimaryKey(productId);
        if (product != null) {
            if (product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()) {
                return ServerResponse.createByErrorMessage("商品已下架或删除");
            }
            ProductDetailVo productDetailVo = assembleProductDetailVo(product);
            return ServerResponse.createBySuccess(productDetailVo);
        } else {
            return ServerResponse.createByErrorMessage("没有找到该商品");
        }
    }

    @Override
    public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword, Integer categoryId, Integer pageNum, Integer pageSize, String orderBy) {
        if (StringUtils.isBlank(keyword) && categoryId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        List<Integer> categoryIdList = Lists.newArrayList();

        if (categoryId != null) {
            ServerResponse<Category> response = categoryService.get(categoryId);
            if (!response.isSuccess() && StringUtils.isBlank(keyword)) {
                //没有该分类，也没有该关键字,这时候返回一个空集合，不报错
                PageHelper.startPage(pageNum, pageSize);
                List<ProductListVo> productListVoList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVoList);
                return ServerResponse.createBySuccess(pageInfo);
            }
            if (response.isSuccess()) {
                Category category = response.getData();
                categoryIdList = categoryService.getCategoryAndDeepChildrenById(category.getId()).getData();
            }
        }
        if (StringUtils.isNotBlank(keyword)) {
            keyword = String.format("%%%s%%", keyword);
        }
        PageHelper.startPage(pageNum, pageSize);
        if (StringUtils.isNotBlank(orderBy)) {
            if (Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)) {
                orderBy = orderBy.replace("_", " ");
                PageHelper.orderBy(orderBy);
            }
        }
        //避免keyword为""或者categoryIdList没有数据时，sql进入了if判断导致查询不到数据
        keyword = StringUtils.isBlank(keyword) ? null : keyword;
        categoryIdList = categoryIdList.size() == 0 ? null : categoryIdList;
        List<Product> productList = productMapper.selectByNameAndCategoryIds(keyword, categoryIdList);
        List<ProductListVo> productListVoList = Lists.newArrayList();
        productList.forEach(product -> {
            ProductListVo productListVo = assembleProductListVo(product);
            productListVoList.add(productListVo);
        });
        PageInfo pageInfo = new PageInfo();
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    public ServerResponse<Product> getProductById(Integer productId) {
        if (productId == null) {
            ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = (Product) productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.createByErrorMessage("查询不到该商品");
        } else {
            return ServerResponse.createBySuccess(product);
        }
    }

    @Override
    public ServerResponse<Product> get(Integer productId) {
        Product product = (Product) productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.createByErrorMessage("商品不存在");
        }
        return ServerResponse.createBySuccess(product);
    }

    @Override
    public ServerResponse reduceProductStock(List<OrderItem> orderItemList) {
        if (CollectionUtils.isEmpty(orderItemList)) {
            return ServerResponse.createByErrorMessage("减少商品库存失败，商品列表为空");
        }
        orderItemList.forEach(item -> {
            Product product = (Product) productMapper.selectByPrimaryKey(item.getProductId());
            product.setStock(product.getStock() - item.getQuantity());
            productMapper.updateByPrimaryKeySelective(product);
        });
        return ServerResponse.createBySuccessMessage("减少商品库存成功");
    }

    /**
     * pojo -> VO 转换
     *
     * @param product
     */
    private ProductDetailVo assembleProductDetailVo(Product product) {
        ProductDetailVo productDetailVo = mapper.map(product, ProductDetailVo.class);
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.hxy.com/"));
        ServerResponse<Category> response = categoryService.get(product.getCategoryId());
        if (response.isSuccess()) {
            productDetailVo.setParentCategoryId(response.getData().getParentId());
        } else {
            productDetailVo.setParentCategoryId(0);
        }
        productDetailVo.setCreateTime(DateTimeUtil.date2Str(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.date2Str(product.getUpdateTime()));
        return productDetailVo;
    }

    /**
     * pojo -> vo 转换，只显示商品列表查询需要的字段
     *
     * @param product
     * @return
     */
    private ProductListVo assembleProductListVo(Product product) {
        ProductListVo productListVo = mapper.map(product, ProductListVo.class);
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://hxy.com"));
        return productListVo;
    }
}
