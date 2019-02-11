package com.huang.mmall.dao;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * 通用Mapper，该接口不能被扫描到，否则会出错
 * Mapper<T>提供了基础的增删改查功能
 * MySqlMapper<T>提供了批量新增的功能
 *
 * @param <T>
 */
//忽略编译器警告，不加会出现Mapper xml does not exist警告
//这是个通用的mapper，不需要实现xml
@SuppressWarnings("all")
public interface BaseMapper<T> extends Mapper, MySqlMapper {
}