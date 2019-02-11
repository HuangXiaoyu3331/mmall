package com.huang.mmall.serviceimpl;

import com.google.common.collect.Lists;
import com.huang.mmall.bean.pojo.Category;
import com.huang.mmall.common.ResponseCode;
import com.huang.mmall.common.ServerResponse;
import com.huang.mmall.dao.CategoryMapper;
import com.huang.mmall.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ServerResponse addCategory(String categoryName, Integer parentId) {
        if (parentId == null || StringUtils.isBlank(categoryName)) {
            return ServerResponse.createByErrorMessage("添加品类参数错误");
        } else {
            //父品类是否存在
            int resultCount = categoryMapper.checkParentCategory(parentId);
            if (parentId == 0 || resultCount > 0) {
                Category category = new Category();
                category.setName(categoryName);
                category.setParentId(parentId);
                category.setStatus(true);

                resultCount = categoryMapper.insert(category);
                if (resultCount > 0) {
                    return ServerResponse.createBySuccessMessage("添加品类成功");
                } else {
                    return ServerResponse.createByErrorMessage("添加品类失败");
                }
            } else {
                return ServerResponse.createByErrorMessage("添加品类失败，父品类不存在");
            }

        }
    }

    @Override
    public ServerResponse updateCategoryName(Integer categoryId, String categoryName) {
        if (categoryId == null || StringUtils.isBlank(categoryName)) {
            return ServerResponse.createByErrorMessage("更新品类名称参数错误");
        } else {
            Category category = new Category();
            category.setId(categoryId);
            category.setName(categoryName);

            int resultCount = categoryMapper.updateByPrimaryKeySelective(category);
            if (resultCount == 0) {
                return ServerResponse.createByErrorMessage("更新品类名称错误");
            } else {
                return ServerResponse.createBySuccessMessage("更新品类名称成功");
            }
        }
    }

    @Override
    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId) {
        List<Category> list = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if (CollectionUtils.isEmpty(list)) {
            log.info("未找到当前分类的子分类，分类id：{}", categoryId);
            return ServerResponse.createByErrorMessage("当前分类没有子分类");
        }
        return ServerResponse.createBySuccess(list);
    }

    /**
     * 递归查询本节点的id及孩子节点的id
     *
     * @param categoryId
     * @return
     */
    @Override
    public ServerResponse<List<Integer>> getCategoryAndDeepChildrenById(Integer categoryId) {
        Set<Category> categorySet = new HashSet<>();
        findChildCategory(categorySet, categoryId);
        List<Integer> categoryList = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(categorySet)) {
            categorySet.forEach(item -> {
                categoryList.add(item.getId());
            });
        }
        return ServerResponse.createBySuccess(categoryList);
    }

    @Override
    public ServerResponse<Category> get(Integer categoryId) {
        if (categoryId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        } else {
            Category category = (Category) categoryMapper.selectByPrimaryKey(categoryId);
            if (category == null) {
                return ServerResponse.createByErrorMessage("找不到该分类");
            } else {
                return ServerResponse.createBySuccess(category);
            }
        }
    }

    /**
     * 递归算法，算出子节点，使用Set集合排重
     *
     * @param categorySet 接收的categorySet集合
     * @param categoryId  需要查找的品类id
     */
    private void findChildCategory(Set<Category> categorySet, Integer categoryId) {
        Category category = (Category) categoryMapper.selectByPrimaryKey(categoryId);
        if (category != null) {
            categorySet.add(category);
        }
        //查找子节点
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        //mabaits的select方法即使查不到数据，也不会放回null。所以不用做非空判断
        categoryList.forEach(item -> {
            findChildCategory(categorySet, item.getId());
        });
    }
}
