package com.huang.mmall.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * 配置文件工具类，用于获取配置文件的属性值,ssm项目用，spring boot项目用PropertiesUtil类
 *
 * @author hxy
 * @date 2019/01/14
 */
@Slf4j
public class PropertiesUtilBack {
    private static Properties properties;

    static {
        String fileName = "mmall.properties";
        properties = new Properties();
        try {
            properties.load(new InputStreamReader(Properties.class.getClassLoader().getResourceAsStream(fileName), "UTF-8"));
        } catch (IOException e) {
            log.error("加载配置文件异常", e);
        }
    }

    /**
     * 获取配置文件属性的值
     *
     * @param key 属性的key
     * @return 属性的值
     */
    public static String getProperty(String key) {
        String value = properties.getProperty(key.trim());
        if (StringUtils.isBlank(value)) {
            return null;
        } else {
            return value.trim();
        }
    }

    /**
     * 获取配置文件属性的值，如果没有该属性，或者属性值为空，则返回默认值
     *
     * @param key          属性的key
     * @param defaultValue 默认值
     * @return 属性的值|默认值
     */
    public static String getProperty(String key, String defaultValue) {
        String value = properties.getProperty(key.trim());
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        } else {
            return value.trim();
        }
    }
}
