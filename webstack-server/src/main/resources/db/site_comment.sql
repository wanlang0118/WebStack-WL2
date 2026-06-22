DROP TABLE IF EXISTS `site_comment`;

CREATE TABLE `site_comment` (
  `id`          bigint       NOT NULL AUTO_INCREMENT  COMMENT '评论 ID',
  `site_id`     int          NOT NULL                 COMMENT '关联网站 ID',
  `user_id`     bigint       DEFAULT NULL             COMMENT '登录用户 ID（游客为 NULL）',
  `guest_name`  varchar(50)  DEFAULT NULL             COMMENT '游客昵称',
  `guest_email` varchar(100) DEFAULT NULL             COMMENT '游客邮箱',
  `guest_url`   varchar(255) DEFAULT NULL             COMMENT '游客个人网址',
  `content`     text         NOT NULL                 COMMENT '评论正文',
  `parent_id`   bigint       DEFAULT NULL             COMMENT '父评论 ID（NULL=顶级评论）',
  `status`      tinyint      DEFAULT '0'              COMMENT '审核状态（0=待审核，1=正常显示）',
  `create_time` datetime     DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_comment_site`   FOREIGN KEY (`site_id`)   REFERENCES `site`         (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_comment_user`   FOREIGN KEY (`user_id`)   REFERENCES `sys_user`     (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_comment_parent` FOREIGN KEY (`parent_id`) REFERENCES `site_comment` (`id`) ON DELETE CASCADE,
  KEY `idx_site_parent_time` (`site_id`, `parent_id`, `create_time` DESC),
  KEY `idx_parent_id`        (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='网站评论表（支持登录用户与游客）';
