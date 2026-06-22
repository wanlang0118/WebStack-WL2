create table category
(
    id          int auto_increment comment '主键'
        primary key,
    parent_id   int      default 0                 null comment '父分类 ID（0=顶级一级分类）',
    title       varchar(100)                       not null comment '分类名（如：AI写作工具）',
    en_title    varchar(100)                       null comment '英文分类名',
    icon        varchar(100)                       null comment '分类图标（图标类名或图片 URL）',
    sort        int      default 0                 null comment '排序权重（越大越靠前）',
    visible     tinyint  default 1                 null comment '显示状态（0=隐藏，1=正常）',
    create_time datetime default CURRENT_TIMESTAMP null,
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
)
    comment '分类表' charset = utf8mb4;

create index idx_parent_id
    on category (parent_id);

create table site
(
    id             int auto_increment comment '主键'
        primary key,
    category_id    int                                not null comment '所属分类 ID',
    title          varchar(100)                       not null comment '网站名称',
    thumb          varchar(255)                       null comment '网站 Logo URL',
    description    varchar(255)                       null comment '一句话简介',
    url            varchar(255)                       null comment '跳转链接',
    like_count     int      default 0                 null comment '总点赞数',
    comment_count  int      default 0                 null comment '总评论数',
    click_count    int      default 0                 null comment '历史总点击数',
    is_recommended tinyint  default 0                 null comment '管理员推荐（0=否，1=是）',
    sort           int      default 0                 null comment '排序权重',
    visible        tinyint  default 1                 null comment '展示状态（0=下架，1=正常）',
    create_time    datetime default CURRENT_TIMESTAMP null,
    update_time    datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint fk_site_category
        foreign key (category_id) references category (id)
)
    comment '网站主表' charset = utf8mb4;

create fulltext index ft_search
    on site (title, description);

create index idx_click_count
    on site (click_count desc);

create index idx_recommend_like_sort
    on site (is_recommended desc, like_count desc, sort desc);

create table site_click_log
(
    id          bigint auto_increment
        primary key,
    site_id     int                                not null comment '被点击的网站 ID',
    user_id     int                                null comment '登录用户 ID（未登录为 NULL）',
    ip          varchar(64)                        null comment '客户端 IP',
    create_time datetime default CURRENT_TIMESTAMP null,
    constraint fk_click_site
        foreign key (site_id) references site (id)
            on delete cascade
)
    comment '点击记录表' charset = utf8mb4;

create index idx_site_id
    on site_click_log (site_id);

create table site_like
(
    id          bigint auto_increment
        primary key,
    site_id     int                                not null comment '被点赞的网站 ID',
    user_id     int                                null comment '登录用户 ID，游客为 NULL',
    ip          varchar(64)                        null comment '客户端 IP，游客防重用',
    create_time datetime default CURRENT_TIMESTAMP null,
    constraint uk_site_user
        unique (site_id, user_id)
)
    comment '用户点赞记录表' charset = utf8mb4;

create index idx_site_ip
    on site_like (site_id, ip);

create table site_section
(
    id          int auto_increment comment '小节 ID'
        primary key,
    site_id     int                                not null comment '所属网站 ID',
    title       varchar(255)                       not null comment '小节标题',
    sort        int      default 0                 null comment '详情页内小节顺序',
    create_time datetime default CURRENT_TIMESTAMP null,
    constraint fk_section_site
        foreign key (site_id) references site (id)
            on delete cascade
)
    comment '网站详情小节表' charset = utf8mb4;

create index idx_site_id
    on site_section (site_id);

create table site_section_content
(
    id           int auto_increment
        primary key,
    section_id   int           not null comment '所属小节 ID',
    content_type varchar(30)   not null comment '组件类型：paragraph / image / list_item / table',
    text_content text          null comment '文本内容',
    json_data    json          null comment '结构化数据',
    sort         int default 0 null comment '同小节内多组件渲染顺序',
    constraint fk_content_section
        foreign key (section_id) references site_section (id)
            on delete cascade
)
    comment '小节内容详情表' charset = utf8mb4;

create index idx_section_id
    on site_section_content (section_id);

create table sys_user
(
    id              bigint auto_increment
        primary key,
    username        varchar(50)                        not null,
    password        varchar(100)                       not null,
    name            varchar(50)                        not null,
    avatar          varchar(255)                       null,
    sex             tinyint  default 1                 null,
    email           varchar(100)                       null,
    phone           varchar(20)                        null,
    status          tinyint  default 1                 null,
    create_time     datetime default CURRENT_TIMESTAMP null,
    update_time     datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    last_login_time datetime                           null,
    last_login_ip   varchar(50)                        null,
    constraint uk_username
        unique (username)
)
    charset = utf8mb4;

create table site_comment
(
    id          bigint auto_increment comment '评论 ID'
        primary key,
    site_id     int                                not null comment '关联网站 ID',
    user_id     bigint                             null comment '登录用户 ID（游客为 NULL）',
    guest_name  varchar(50)                        null comment '游客昵称',
    guest_email varchar(100)                       null comment '游客邮箱',
    guest_url   varchar(255)                       null comment '游客个人网址',
    content     text                               not null comment '评论正文',
    parent_id   bigint                             null comment '父评论 ID（NULL=顶级评论）',
    status      tinyint  default 0                 null comment '审核状态（0=待审核，1=正常显示）',
    create_time datetime default CURRENT_TIMESTAMP null,
    constraint fk_comment_parent
        foreign key (parent_id) references site_comment (id)
            on delete cascade,
    constraint fk_comment_site
        foreign key (site_id) references site (id)
            on delete cascade,
    constraint fk_comment_user
        foreign key (user_id) references sys_user (id)
            on delete set null
)
    comment '网站评论表（支持登录用户与游客）' charset = utf8mb4;

create index idx_parent_id
    on site_comment (parent_id);

create index idx_site_parent_time
    on site_comment (site_id asc, parent_id asc, create_time desc);

create table sys_notice
(
    id                bigint auto_increment comment '主键'
        primary key,
    sender_id         bigint   default 0                 null comment '发送者 ID（0=系统）',
    sender_name       varchar(50)                        null comment '发送时的昵称快照',
    receiver_id       bigint                             not null comment '接收者用户 ID',
    notice_type       tinyint  default 1                 not null comment '类型（1=系统公告，2=评论回复，3=点赞提醒）',
    source_site_id    int                                null comment '关联网站 ID（点击后跳转）',
    source_comment_id bigint                             null comment '关联评论 ID',
    title             varchar(150)                       null comment '消息标题',
    summary           varchar(200)                       not null comment '列表页单行摘要',
    content           text                               not null comment '完整正文',
    is_read           tinyint  default 0                 null comment '是否已读',
    create_time       datetime default CURRENT_TIMESTAMP null,
    read_time         datetime                           null comment '阅读时间',
    constraint fk_notice_receiver
        foreign key (receiver_id) references sys_user (id)
            on delete cascade,
    constraint fk_notice_site
        foreign key (source_site_id) references site (id)
            on delete cascade
)
    comment '消息通知表' charset = utf8mb4;

create index idx_receiver_read_list
    on sys_notice (receiver_id asc, is_read asc, create_time desc, notice_type asc);

create table tag
(
    id          int auto_increment
        primary key,
    tag_name    varchar(50)                        not null comment '标签名称',
    create_time datetime default CURRENT_TIMESTAMP null,
    constraint uk_tag_name
        unique (tag_name)
)
    comment '标签主表' charset = utf8mb4;

create table site_tag_relation
(
    id      bigint auto_increment
        primary key,
    site_id int not null,
    tag_id  int not null,
    constraint uk_site_tag
        unique (site_id, tag_id),
    constraint fk_rel_site
        foreign key (site_id) references site (id)
            on delete cascade,
    constraint fk_rel_tag
        foreign key (tag_id) references tag (id)
            on delete cascade
)
    comment '工具与标签中间表' charset = utf8mb4;

create index idx_tag_id
    on site_tag_relation (tag_id)
    comment '反向索引：支持点击标签秒级拉出相关的工具列表';


