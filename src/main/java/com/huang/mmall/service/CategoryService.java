package com.huang.mmall.service;

import com.huang.mmall.bean.pojo.Category;
import com.huang.mmall.common.ServerResponse;

import java.util.List;

public interface CategoryService {
    ServerResponse addCategory(String categoryName, Integer parentId);

    ServerResponse updateCategoryName(Integer categoryId, String categoryName);

    ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);

    ServerResponse<List<Integer>> getCategoryAndDeepChildrenById(Integer categoryId);

    /**
     * 根据分类id获取分类
     *
     * @param categoryId 分类id
     * @return
     */
    ServerResponse<Category> get(Integer categoryId);
}
