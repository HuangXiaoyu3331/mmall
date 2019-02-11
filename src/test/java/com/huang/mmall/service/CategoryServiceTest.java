package com.huang.mmall.service;

import com.google.common.collect.Lists;
import com.huang.mmall.bean.pojo.Category;
import com.huang.mmall.common.ServerResponse;
import com.huang.mmall.serviceimpl.CategoryServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    @Test
    @Transactional
    public void addCategory() {
        String categoryName = "新增品类名称";
        Integer errorParentId = 617;
        Integer successParentId = 100001;

        ServerResponse successResponse = ServerResponse.createBySuccessMessage("添加品类成功");
        ServerResponse paramsErrorResponse = ServerResponse.createByErrorMessage("添加品类参数错误");
        ServerResponse parentCategoryNotExistResponse = ServerResponse.createByErrorMessage("添加品类失败，父品类不存在");

        ServerResponse response = categoryService.addCategory(categoryName, errorParentId);
        assertThat(response, is(equalTo(parentCategoryNotExistResponse)));
        response = categoryService.addCategory(null, null);
        assertThat(response, is(equalTo(paramsErrorResponse)));
        response = categoryService.addCategory(categoryName, successParentId);
        assertThat(response, is(equalTo(successResponse)));

    }

    @Test
    @Transactional
    public void updateCategoryName() {
        String categoryName = "更新品类名称";
        Integer errorCategoryId = 617;
        Integer successCategoryId = 100001;

        ServerResponse successResponse = ServerResponse.createBySuccessMessage("更新品类名称成功");
        ServerResponse paramsErrorResponse = ServerResponse.createByErrorMessage("更新品类名称参数错误");
        ServerResponse errorResponse = ServerResponse.createByErrorMessage("更新品类名称错误");

        ServerResponse response = categoryService.updateCategoryName(errorCategoryId, categoryName);
        assertThat(response, is(equalTo(errorResponse)));
        response = categoryService.updateCategoryName(null, categoryName);
        assertThat(response, is(equalTo(paramsErrorResponse)));
        response = categoryService.updateCategoryName(successCategoryId, categoryName);
        assertThat(response, is(equalTo(successResponse)));
    }

    @Test
    public void getChildrenParallelCategory() {
        Integer errorCategoryId = 617;
        Integer successCategoryId = 100001;

        ServerResponse errorResponse = ServerResponse.createByErrorMessage("当前分类没有子分类");

        ServerResponse response = categoryService.getChildrenParallelCategory(errorCategoryId);
        assertThat(response, is(equalTo(errorResponse)));
        response = categoryService.getChildrenParallelCategory(successCategoryId);
        assertThat(response.isSuccess(), is(equalTo(true)));
    }

    @Test
    public void getCategoryAndDeepChildrenById() {
        Set<Category> categorySet = new HashSet<>();
        List<Integer> resultList = Lists.newArrayList();
        Integer errorCategoryId = 617;
        Integer successCategoryId = 100001;

        ServerResponse response = categoryService.getCategoryAndDeepChildrenById(errorCategoryId);
        resultList = (List<Integer>) response.getData();
        assertThat(resultList.size(), is(equalTo(0)));

        response = categoryService.getCategoryAndDeepChildrenById(successCategoryId);
        resultList = (List<Integer>) response.getData();
        assertThat(resultList.size(), is(greaterThan(0)));
    }

    @Test
    public void findChildCategory() {
        Integer errorCategoryId = 617;
        Integer successCategoryId = 100001;
        Set<Category> categorySet = new HashSet<>();
        String methodName = "findChildCategory";

        try {
            Class<CategoryServiceImpl> clazz = CategoryServiceImpl.class;
            Method method = clazz.getDeclaredMethod(methodName, Set.class, Integer.class);
            method.setAccessible(true);//抑制访问修饰符，使得私有方法变得可访问
            method.invoke(categoryService, categorySet, errorCategoryId);
            assertThat(categorySet.size(), is(equalTo(0)));
            method.invoke(categoryService, categorySet, successCategoryId);
            assertThat(categorySet.size(), is(greaterThan(0)));
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}