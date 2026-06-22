DROP TABLE IF EXISTS `sys_notice`;

CREATE TABLE `sys_notice` (
  `id`                bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `sender_id`         bigint       DEFAULT '0'             COMMENT '发送者 ID（0=系统）',
  `sender_name`       varchar(50)  DEFAULT NULL            COMMENT '发送时的昵称快照',
  `receiver_id`       bigint       NOT NULL                COMMENT '接收者用户 ID',
  `notice_type`       tinyint      NOT NULL DEFAULT '1'    COMMENT '类型（1=系统公告，2=评论回复，3=点赞提醒）',
  `source_site_id`    int          DEFAULT NULL            COMMENT '关联网站 ID（点击后跳转）',
  `source_comment_id` bigint       DEFAULT NULL            COMMENT '关联评论 ID',
  `title`             varchar(150) DEFAULT NULL            COMMENT '消息标题',
  `summary`           varchar(200) NOT NULL                COMMENT '列表页单行摘要',
  `content`           text         NOT NULL                COMMENT '完整正文',
  `is_read`           tinyint      DEFAULT '0'             COMMENT '是否已读',
  `create_time`       datetime     DEFAULT CURRENT_TIMESTAMP,
  `read_time`         datetime     DEFAULT NULL            COMMENT '阅读时间',
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_notice_receiver` FOREIGN KEY (`receiver_id`)     REFERENCES `sys_user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_notice_site`     FOREIGN KEY (`source_site_id`) REFERENCES `site`     (`id`) ON DELETE CASCADE,
  KEY `idx_receiver_read_list` (`receiver_id`, `is_read`, `create_time` DESC, `notice_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息通知表';
