-- 用户点赞记录表（登录用户 + 游客）
DROP TABLE IF EXISTS `site_like`;
CREATE TABLE `site_like` (
  `id`          bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `site_id`     int          NOT NULL                COMMENT '被点赞的网站 ID',
  `user_id`     bigint       NULL                    COMMENT '点赞用户 ID，游客为 NULL',
  `ip`          varchar(45)  NULL                    COMMENT '游客 IP（仅 user_id 为空时使用）',
  `create_time` datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_site_user` (`site_id`, `user_id`),
  UNIQUE KEY `uk_site_guest` (`site_id`, `ip`),
  CONSTRAINT `fk_like_site` FOREIGN KEY (`site_id`) REFERENCES `site` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_like_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户点赞记录表';
