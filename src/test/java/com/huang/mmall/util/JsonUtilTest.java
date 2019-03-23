package com.huang.mmall.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.huang.mmall.bean.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class JsonUtilTest {

    @Test
    public void obj2String() {
        User user = new User();
        user.setId(1);
        user.setUsername("admin");
        user.setPassword("123456");
        String str = JsonUtil.obj2String(user);
        System.out.println(str);
    }

    @Test
    public void obj2StringPretty() {
    }

    @Test
    public void string2Obj() {
        User user1 = new User();
        user1.setId(1);
        user1.setUsername("admin");
        user1.setPassword("123456");

        User user2 = new User();
        user2.setId(2);
        user2.setUsername("admin2");
        user2.setPassword("16");

        List<User> userList = new ArrayList<>();
        userList.add(user1);
        userList.add(user2);

        String str = JsonUtil.obj2String(userList);

        List<User> list = JsonUtil.string2Obj(str, List.class);

        List<User> list2 = JsonUtil.string2Obj(str, new TypeReference<List<User>>() {
        });
        List<User> list3 = JsonUtil.string2Obj(str, List.class, User.class);
        System.out.println(list);
    }
}