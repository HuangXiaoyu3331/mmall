package com.huang.mmall.config;

import com.huang.mmall.interceptor.AuthorityInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebMvcConfigurerAdapter 在该版本已经被废弃，直接实现WebMvcConfigurer接口就可以了
 *
 * @author hxy
 * @date 2019/01/14
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Autowired
    private AuthorityInterceptor authorityInterceptor;

    @Value("${web.upload.path}")
    private String uploadPath;
    @Value("${web.upload.handler}")
    private String uploadHandler;

    /**
     * 拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authorityInterceptor)
                .addPathPatterns("/manage/**")
                .excludePathPatterns("/manage/user/login")
                .addPathPatterns("/cart/**")
                .excludePathPatterns("/cart/count")
                .addPathPatterns("/shipping/**")
                .addPathPatterns("/order/**")
                .excludePathPatterns("/order/alipay_callback");
    }

    /**
     * 设置静态资源路径
     * 访问localhost:8080/upload/**就能直接访问文件
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(uploadHandler)
                .addResourceLocations("file:" + uploadPath);
    }
}
