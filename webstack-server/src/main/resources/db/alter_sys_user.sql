-- 用户表字段扩展
ALTER TABLE sys_user
  ADD COLUMN `status`          tinyint      DEFAULT '1'     COMMENT '状态（1=正常，0=禁用）',
  ADD COLUMN `update_time`     datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  ADD COLUMN `last_login_time` datetime     DEFAULT NULL    COMMENT '最后登录时间',
  ADD COLUMN `last_login_ip`   varchar(50)  DEFAULT NULL    COMMENT '最后登录IP';
