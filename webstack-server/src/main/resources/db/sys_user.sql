-- 用户表（含扩展字段）
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id`              bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username`        varchar(50)  NOT NULL                COMMENT '登录账号',
  `password`        varchar(100) NOT NULL                COMMENT 'BCrypt 加密后的密码',
  `name`            varchar(50)  NOT NULL                COMMENT '昵称（显示在评论区）',
  `avatar`          varchar(255) DEFAULT NULL            COMMENT '头像图片 URL（存放于 OSS/COS）',
  `sex`             tinyint      DEFAULT '1'             COMMENT '性别（1=男，2=女，0=未知）',
  `email`           varchar(100) DEFAULT NULL            COMMENT '邮箱',
  `phone`           varchar(20)  DEFAULT NULL            COMMENT '手机号',
  `status`          tinyint      DEFAULT '1'             COMMENT '状态（1=正常，0=禁用）',
  `create_time`     datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `update_time`     datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_login_time` datetime     DEFAULT NULL            COMMENT '最后登录时间',
  `last_login_ip`   varchar(50)  DEFAULT NULL            COMMENT '最后登录IP',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
