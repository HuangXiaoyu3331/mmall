package com.huang.mmall.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 * 配置文件工具类，用于获取配置文件的属性值
 *
 * @author hxy
 * @date 2019/01/14
 */
@Configuration
@PropertySource("classpath:application.yml")
public class PropertiesUtil implements EnvironmentAware {

    private static Environment env;

    @Override
    public void setEnvironment(Environment environment) {
        PropertiesUtil.env = environment;
    }

    /**
     * 获取配置文件属性的值
     *
     * @param key 属性的key
     * @return 属性值
     */
    public static String getProperty(String key) {
        String value = env.getProperty(key.trim());
        if (StringUtils.isBlank(value)) {
            return null;
        } else {
            return value.trim();
        }
    }

    /**
     * 获取配置文件属性的值，可指定默认值
     *
     * @param key          属性的key
     * @param defaultValue 默认值
     * @return 属性值|默认值
     */
    public static String getProperty(String key, String defaultValue) {
        String value = env.getProperty(key.trim());
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        } else {
            return value.trim();
        }
    }
}
