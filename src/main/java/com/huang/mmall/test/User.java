package com.huang.mmall.test;

import lombok.Data;

import java.util.Date;

/**
 * User class
 *
 * @author hxy
 * @date 2019/1/16
 */
@Data
public class User {
    private String name;
    private Integer password;
    private Date date;
}
