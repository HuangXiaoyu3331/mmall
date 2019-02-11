package com.huang.mmall;

import com.huang.mmall.dao.BaseMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RequestMapping;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * spring boot启动类
 * <p>
 * EnableTransactionManagement：启用事务注解,等同于xml配置方式的 <tx:annotation-driven />
 * markerInteface：扫描basePackages下的所有继承BaseMapper.class的接口。没有继承的不扫描
 * 需要用tk.mybatis.spring.annotation包下的注解，不然会报import org.mybatis.spring.annotation.MapperScan错误
 *
 * @author hxy
 * @date 2019/01/14
 */
@SpringBootApplication
@MapperScan(basePackages = "com.huang.mmall.dao", markerInterface = BaseMapper.class)
@EnableTransactionManagement
public class MmallApplication {

    public static void main(String[] args) {
        SpringApplication.run(MmallApplication.class, args);
    }
}