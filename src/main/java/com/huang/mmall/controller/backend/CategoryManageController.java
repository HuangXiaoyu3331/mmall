package com.huang.mmall.controller.backend;

import com.huang.mmall.bean.pojo.Category;
import com.huang.mmall.common.ServerResponse;
import com.huang.mmall.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台用户模块controller
 *
 * @author hxy
 * @date 2019/01/14
 */
@RestController
@RequestMapping("/manage/category")
public class CategoryManageController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 添加商品品类
     *
     * @param categoryName 品类名称
     * @param parentId     父品类的id
     * @return ServerResponse
     */
    @PostMapping
    public ServerResponse addCategory(String categoryName, @RequestParam(value = "parentId", defaultValue = "0") Integer parentId) {
        return categoryService.addCategory(categoryName, parentId);
    }

    /**
     * 更新品类名称
     *
     * @param categoryId   品类id
     * @param categoryName 新的品类名称
     * @return ServerResponse
     */
    @PutMapping
    public ServerResponse updateCategoryName(Integer categoryId, String categoryName) {
        return categoryService.updateCategoryName(categoryId, categoryName);
    }

    /**
     * 获取平级的品类
     *
     * @param categoryId 品类id
     * @return ServerResponse
     */
    @GetMapping("/{categoryId}")
    public ServerResponse<List<Category>> getChildrenParallelCategory(@PathVariable("categoryId") Integer categoryId) {
        return categoryService.getChildrenParallelCategory(categoryId);
    }

    /**
     * 查询当前节点和递归节点的品类id
     *
     * @param categoryId ；品类id
     * @return ServerResponse
     */
    @GetMapping("/children/{categoryId}")
    public ServerResponse<List<Integer>> getCategoryAndDeepChildrenCategory(@PathVariable("categoryId") Integer categoryId) {
        return categoryService.getCategoryAndDeepChildrenById(categoryId);
    }
}
