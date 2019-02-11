package com.huang.mmall.test;

import com.github.dozermapper.core.Mapping;
import lombok.Data;

/**
 * UserAnnotationDestinationObject class
 *
 * @author hxy
 * @date 2019/1/17
 */
@Data
public class UserAnnotationDestinationObject {
    @Mapping("name")
    private String username;
    @Mapping("password")
    private String psw;
}
