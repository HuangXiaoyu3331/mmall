# Spring Boot 商城应用
使用通用 Mapper 插件，create_time、update_time自动更新需要在建库的时候设置 create_time 字段的默认值为CURRENT_TIMESTAMP
update_time 字段更新的时候的默认值为 CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT，如：

CREATE TABLE `mmall_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户表id',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(50) NOT NULL COMMENT '用户密码，MD5加密',
  `email` varchar(50) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `question` varchar(100) DEFAULT NULL COMMENT '找回密码问题',
  `answer` varchar(100) DEFAULT NULL COMMENT '找回密码答案',
  `role` int(4) NOT NULL COMMENT '角色0-管理员,1-普通用户',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_name_unique` (`username`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8;

然后在 pojo 类中需要加上这两个注解：
    @Column(name = "create_time", insertable = false)
    private Date createTime;
    @Column(name = "update_time", updatable = false, insertable = false)
    private Date updateTime;