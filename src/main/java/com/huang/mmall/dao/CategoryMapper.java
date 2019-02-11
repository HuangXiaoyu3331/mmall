package com.huang.mmall.dao;

import com.huang.mmall.bean.pojo.Category;

import java.util.List;

public interface CategoryMapper extends BaseMapper<Category> {
    List<Category> selectCategoryChildrenByParentId(Integer parentId);

    int checkParentCategory(Integer parentId);
}