# WebStack-WL2 项目目录全解析

## 一、项目总览

**WebStack-WL2** 是一个 **网站导航/书签收藏站**（类似 AI 工具导航站）。后端提供 REST API，管理后台前端管理数据，用户前端展示导航页面。

### 技术栈

| 层级      | 技术选型                                   | 版本     |
| --------- | ------------------------------------------ | -------- |
| 后端框架  | Spring Boot                                | 3.3.5    |
| 语言      | Java                                       | 22       |
| ORM       | MyBatis-Plus                               | 3.5.10.1 |
| 数据库    | MySQL (数据库名: webstack2)                | —        |
| 安全      | Spring Security Crypto + JWT (jjwt 0.12.6) | —        |
| 前端框架  | Vue 3 + Vite 5                             | 3.4.21   |
| UI 组件库 | naive-ui（管理后台独占）                   | 2.38.1   |
| 状态管理  | Pinia + pinia-plugin-persistedstate        | 3.0.4    |
| HTTP      | Axios                                      | 1.6.8    |
| AI 集成   | DeepSeek API（通过 RestClient 调用）       | —        |

---

## 二、根目录结构

```
WebStack-WL2/
├── AGENTS.md                    # 本文件 — 项目结构与开发指引
├── CLAUDE.md                    # Claude Code 项目规则（编码风格、数据库配置等）
├── script.sql                   # 完整数据库 DDL（10 张表，230 行）
├── seed_data.sql                # 开发测试数据（524 行，含分类、网站、用户等）
├── uploads/                     # 用户上传文件存储（图片），按日期子目录组织
├── docs/
│   └── apifox/                  # Apifox API 文档导出
├── 官方网页/                     # 参考网页截图
├── webstack-server/             # Java Spring Boot 后端（端口 8180）
├── webstack-admin/              # Vue 3 + naive-ui 管理后台（端口 5173）
└── webstack-user/               # Vue 3 用户端前端（端口 5174）
```

---

## 三、webstack-server/ 后端详解

### 3.1 完整目录结构与文件说明

```
webstack-server/
├── pom.xml                                # Maven 配置（依赖版本、打包插件）
└── src/main/
    ├── java/webstack/
    │   ├── WebstackServerApplication.java    # 【全局入口】@SpringBootApplication 
    │   │
    │   ├── common/                                  # ===== 公共框架层 =====
    │   │   ├── annotation/
    │   │   │   └── RequireRole.java                 # 角色访问控制注解（可标注在类或方法上）
    │   │   ├── config/
    │   │   │   ├── CorsConfig.java                  # 跨域配置（允许前端域名访问）
    │   │   │   ├── MyMetaObjectHandler.java         # 自动填充（createTime/updateTime）
    │   │   │   └── MybatisPlusConfig.java           # MP 分页插件等配置
    │   │   ├── constant/
    │   │   │   ├── CommentStatus.java               # 评论状态常量
    │   │   │   ├── ContentType.java     # 内容类型常量（paragraph/image/list_item/table）
    │   │   │   ├── NoticeType.java               # 通知类型常量（系统公告/评论回复/点赞提醒）
    │   │   │   ├── ReadStatus.java                  # 已读状态常量
    │   │   │   ├── RoleConstant.java                # 角色常量：ADMIN / USER
    │   │   │   └── UserStatus.java                  # 用户状态常量
    │   │   ├── exception/
    │   │   │   ├── BizException.java                # 业务异常（自定义 code + message）
    │   │   │   └── GlobalExceptionHandler.java      # 【全局异常处理】
    │   │   ├── interceptor/
    │   │   │   └── AuthInterceptor.java             # JWT 解析 + @RequireRole 角色校验
    │   │   ├── result/
    │   │   │   ├── Result.java                      # 统一响应封装 {code, message, data}
    │   │   │   └── ResultCode.java               # 响应码枚举（200/400/401/403/404/500）
    │   │   ├── storage/
    │   │   │   ├── StorageStrategy.java             # 存储策略接口（策略模式）
    │   │   │   ├── LocalStorageStrategy.java        # 本地文件存储实现（UUID重命名+日期目录）
    │   │   │   └── StorageProperties.java           # 存储配置属性绑定
    │   │   └── util/
    │   │       ├── IpUtil.java                      # 客户端 IP 获取
    │   │       └── JwtUtil.java                     # JWT 工具类：生成/解析/验证 token
    │   │
    │   └── module/                                  # ===== 业务模块层（12 个模块）=====
    │       │
    │       ├── auth/                                # 认证模块
    │       │   └── controller/
    │       │       └── AuthController.java          # POST /api/auth/login（公开接口）
    │       │
    │       ├── category/                            # 分类模块
    │       │   ├── controller/CategoryController.java   # GET /tree, POST /save, PUT /update, DELETE /{id}, POST /batch-delete
    │       │   ├── service/CategoryService.java         # 接口：tree(), saveCategory(), removeCascade(), getOrCreateByTitle()
    │       │   ├── service/impl/CategoryServiceImpl.java
    │       │   ├── mapper/CategoryMapper.java           # MyBatis-Plus Mapper
    │       │   ├── entity/Category.java                 # 字段：id, parentId, title, enTitle, icon, sort, visible
    │       │   ├── dto/CategorySaveDTO.java             # 新增请求参数
    │       │   ├── dto/CategoryUpdateDTO.java           # 更新请求参数
    │       │   └── vo/CategoryTreeVO.java               # 树形响应（含 children）
    │       │
    │       ├── site/                                 # 【核心】网站模块
    │       │   ├── controller/SiteController.java      # GET /page, GET /{id}, POST /save, PUT /update, DELETE /{id}, POST /batch-delete
    │       │   ├── service/SiteService.java            # 接口
    │       │   ├── service/impl/SiteServiceImpl.java   # 实现
    │       │   ├── service/SiteTagRelationService.java # 网站-标签关联服务
    │       │   ├── service/impl/SiteTagRelationServiceImpl.java
    │       │   ├── mapper/SiteMapper.java              # 含自定义 XML 查询
    │       │   ├── mapper/SiteTagRelationMapper.java
    │       │   ├── entity/Site.java                    # 字段：id, categoryId, title, thumb, description, url, likeCount, commentCount, clickCount, isRecommended, sort, visible
    │       │   ├── entity/SiteTagRelation.java         # 中间表：id, siteId, tagId
    │       │   ├── dto/SiteSaveDTO.java
    │       │   ├── dto/SiteUpdateDTO.java
    │       │   ├── dto/SiteQueryDTO.java               # 分页查询参数（支持排序）
    │       │   └── vo/SiteVO.java                      # 响应（含 tagNameList 等）
    │       │
    │       ├── tag/                                  # 标签模块
    │       │   ├── controller/TagController.java       # CRUD
    │       │   ├── service/TagService.java             # 接口：getOrCreateByTagName()
    │       │   ├── service/impl/TagServiceImpl.java
    │       │   ├── mapper/TagMapper.java
    │       │   ├── entity/Tag.java                     # 字段：id, tagName, createTime
    │       │   ├── dto/TagSaveDTO.java + TagUpdateDTO.java + TagQueryDTO.java
    │       │   └── vo/TagVO.java
    │       │
    │       ├── siteSection/                          # 网站详情小节模块
    │       │   ├── controller/SiteSectionController.java  # GET /list, POST /save, PUT /update, DELETE /{id}
    │       │   ├── service/SiteSectionService.java
    │       │   ├── service/impl/SiteSectionServiceImpl.java
    │       │   ├── mapper/SiteSectionMapper.java
    │       │   ├── entity/SiteSection.java                # 字段：id, siteId, title, sort, createTime
    │       │   ├── dto/SiteSectionSaveDTO.java + SiteSectionUpdateDTO.java
    │       │   └── vo/SiteSectionVO.java
    │       │
    │       ├── siteSectionContent/                   # 小节内容模块
    │       │   ├── controller/SiteSectionContentController.java  # GET /list, POST /save, PUT /update, DELETE /{id}
    │       │   ├── service/SiteSectionContentService.java
    │       │   ├── service/impl/SiteSectionContentServiceImpl.java
    │       │   ├── mapper/SiteSectionContentMapper.java
    │       │   ├── entity/SiteSectionContent.java             # 字段：id, sectionId, contentType, textContent, jsonData, sort
    │       │   ├── dto/SiteSectionContentSaveDTO.java + SiteSectionContentUpdateDTO.java
    │       │   ├── vo/SiteSectionContentVO.java
    │       │   └── vo/contentdata/
    │       │       ├── ParagraphData.java              # 段落数据
    │       │       ├── TableData.java                  # 表格数据（headers + rows）
    │       │       ├── ListItemData.java               # 列表项数据
    │       │       └── ImageData.java                  # 图片数据（url + alt）
    │       │
    │       ├── siteComment/                          # 评论模块
    │       │   ├── controller/SiteCommentController.java  # GET /page, POST /save, POST /reply, DELETE /{id}, POST /batch-delete, PUT /review
    │       │   ├── service/SiteCommentService.java
    │       │   ├── service/impl/SiteCommentServiceImpl.java
    │       │   ├── mapper/SiteCommentMapper.java
    │       │   ├── entity/SiteComment.java                # 字段：id, siteId, userId, guestName, guestEmail, guestUrl, content, parentId, status, createTime
    │       │   ├── dto/SiteCommentCreateDTO.java + SiteCommentReplyDTO.java + SiteCommentQueryDTO.java
    │       │   └── vo/SiteCommentVO.java
    │       │
    │       ├── siteLike/                             # 点赞模块
    │       │   ├── controller/SiteLikeController.java     # POST /toggle, GET /status, GET /page
    │       │   ├── service/SiteLikeService.java
    │       │   ├── service/impl/SiteLikeServiceImpl.java
    │       │   ├── mapper/SiteLikeMapper.java
    │       │   ├── entity/SiteLike.java                   # 字段：id, siteId, userId, ip, createTime（IP防重+用户防重）
    │       │   ├── dto/SiteLikeQueryDTO.java
    │       │   └── vo/SiteLikeVO.java
    │       │
    │       ├── siteClickLog/                         # 点击日志模块
    │       │   ├── controller/SiteClickLogController.java # POST /{siteId}, GET /page
    │       │   ├── service/SiteClickLogService.java
    │       │   ├── service/impl/SiteClickLogServiceImpl.java
    │       │   ├── mapper/SiteClickLogMapper.java
    │       │   ├── entity/SiteClickLog.java               # 字段：id, siteId, userId, ip, createTime
    │       │   ├── dto/SiteClickLogQueryDTO.java
    │       │   └── vo/SiteClickLogVO.java
    │       │
    │       ├── siteImport/                           # 批量导入模块（AI 解析）
    │       │   ├── controller/SiteImportController.java   # POST /import, POST /ai-parse
    │       │   ├── service/SiteImportService.java
    │       │   ├── service/impl/SiteImportServiceImpl.java # 【AI集成核心】调用 DeepSeek API 解析文本为网站数据
    │       │   ├── dto/SiteImportItemDTO.java             # 导入数据结构
    │       │   ├── dto/AiParseDTO.java                    # AI 解析请求参数
    │       │   └── vo/SiteImportResultVO.java             # 导入结果
    │       │
    │       ├── sysUser/                              # 系统用户模块
    │       │   ├── controller/SysUserController.java      # GET /me, GET /page, POST /save, PUT /update, DELETE /{id}
    │       │   ├── service/SysUserService.java            # 接口：login(), getCurrentUser()
    │       │   ├── service/impl/SysUserServiceImpl.java
    │       │   ├── mapper/SysUserMapper.java
    │       │   ├── entity/SysUser.java                    # 字段：id, username, password, name, avatar, sex, email, phone, role, status, lastLoginTime, lastLoginIp
    │       │   ├── dto/LoginDTO.java + SysUserAddDTO.java + SysUserUpdateDTO.java
    │       │   └── vo/SysUserVO.java + LoginVO.java
    │       │
    │       ├── sysNotice/                            # 消息通知模块
    │       │   ├── controller/SysNoticeController.java    # POST /send, GET /page, PUT /read, PUT /read-all, GET /unread-count
    │       │   ├── service/SysNoticeService.java
    │       │   ├── service/impl/SysNoticeServiceImpl.java
    │       │   ├── mapper/SysNoticeMapper.java
    │       │   ├── entity/SysNotice.java                  # 字段：id, senderId, senderName, receiverId, noticeType, sourceSiteId, sourceCommentId, title, summary, content, isRead, readTime
    │       │   ├── dto/SysNoticeSendDTO.java + SysNoticeQueryDTO.java
    │       │   └── vo/SysNoticeVO.java
    │       │
    │       └── upload/                               # 文件上传模块
    │           └── controller/FileUploadController.java   # POST /api/upload/image（仅ADMIN）
    │
    └── resources/
        ├── application.yml                          # 主配置（端口8180、JWT、MP、存储）
        ├── application-dev.yml                      # 开发环境（MySQL连接、DeepSeek、SQL日志）
        ├── application-prod.yml                     # 生产环境
        ├── mapper/                                  # MyBatis XML 映射文件（7个）
        │   ├── site/SiteMapper.xml                  # 网站复杂查询（JOIN category、多字段排序）
        │   ├── siteComment/SiteCommentMapper.xml
        │   ├── siteClickLog/SiteClickLogMapper.xml
        │   ├── siteLike/SiteLikeMapper.xml
        │   ├── siteSection/SiteSectionMapper.xml
        │   ├── siteSectionContent/SiteSectionContentMapper.xml
        │   └── sysNotice/SysNoticeMapper.xml
        └── db/                                      # 数据库变更脚本（6个）
            ├── site_like.sql                        # 点赞表建表脚本
            ├── site_comment.sql                     # 评论表建表脚本
            ├── sys_user.sql                         # 用户表建表脚本
            ├── sys_notice.sql                       # 通知表建表脚本
            ├── migration_add_role.sql               # 权限改造：添加 role 字段
            └── alter_sys_user.sql                   # 用户表扩展字段
```

### 3.2 核心调用链

```
HTTP 请求
  → AuthInterceptor（解析 JWT、校验 @RequireRole 角色）
    → Controller（接收 DTO，调用 Service）
      → Service（业务逻辑，调用 Mapper）
        → Mapper（MyBatis-Plus 内置方法 或 XML 自定义 SQL）
          → MySQL
```

### 3.3 认证与权限机制

- **JWT 认证**：`JwtUtil` 生成/验证 token，包含 `userId` 和 `role` 两个 claim
- **拦截器**：`AuthInterceptor` 从 `Authorization: Bearer xxx` 解析 token
- **注解控制**：`@RequireRole(RoleConstant.ADMIN)` 标注在 Controller 类或方法上
- **角色体系**：`ADMIN`（管理后台全部接口）、`USER`（普通用户，部分只读接口）
- **未登录放行**：无 `@RequireRole` 注解的接口允许匿名访问
- **方法优先**：方法级注解覆盖类级注解

### 3.4 模块职责与 API 端点

| 模块               | 路径前缀                    | 角色要求 | 主要端点                                                    |
| ------------------ | --------------------------- | -------- | ----------------------------------------------------------- |
| auth               | `/api/auth`                 | 公开     | `POST /login`                                               |
| category           | `/api/category`             | ADMIN    | `GET /tree`(USER也可), POST/PUT/DELETE/batch-delete         |
| site               | `/api/site`                 | ADMIN    | `GET /page`+`/{id}`(USER也可), POST/PUT/DELETE/batch-delete |
| tag                | `/api/tag`                  | ADMIN    | CRUD                                                        |
| siteSection        | `/api/site-section`         | ADMIN    | GET /list, CRUD                                             |
| siteSectionContent | `/api/site-section-content` | ADMIN    | GET /list, CRUD                                             |
| siteComment        | `/api/site-comment`         | ADMIN    | GET /page, save/reply/review/batch-delete                   |
| siteLike           | `/api/site-like`            | 公开     | `POST /toggle`, `GET /status`, `GET /page`                  |
| siteClickLog       | `/api/site-click-log`       | ADMIN    | `POST /{siteId}`, `GET /page`                               |
| siteImport         | `/api/site-import`          | ADMIN    | `POST /import`, `POST /ai-parse`                            |
| sysUser            | `/api/user`                 | ADMIN    | GET /me, CRUD                                               |
| sysNotice          | `/api/notice`               | ADMIN    | send/page/read/read-all/unread-count                        |
| upload             | `/api/upload`               | ADMIN    | `POST /image`                                               |

### 3.5 关键设计模式

- **模块化**：每个业务模块独立包 `module.<name>/`，包含 controller/service/mapper/entity/dto/vo
- **DTO/VO 分离**：DTO 接收请求参数，VO 返回视图对象
- **MyBatis-Plus**：简单 CRUD 用 MP 内置方法，复杂查询写在 `resources/mapper/` XML 中
- **存储策略模式**：`StorageStrategy` 接口 + `LocalStorageStrategy` 实现，可扩展为 OSS
- **全局异常处理**：`GlobalExceptionHandler` 统一捕获 12 种异常，返回标准 `Result` 格式
- **自动填充**：`MyMetaObjectHandler` 自动填充 `createTime`/`updateTime`

---

## 四、webstack-admin/ 管理后台前端

### 4.1 完整目录结构与文件说明

```
webstack-admin/
├── index.html                           # HTML 入口
├── package.json                         # 依赖（naive-ui, pinia, vue-router, axios）
├── vite.config.js                       # Vite 配置（端口5173，代理/api+/uploads→8180）
└── src/
    ├── main.js                          # 【全局入口】注册 Pinia、Router、naive-ui，注入 window.$message 等
    ├── App.vue                          # 根组件
    │
    ├── router/
    │   └── index.js                     # 路由定义（登录页 + 12个管理子路由，含 beforeEach 守卫）
    │
    ├── store/                           # Pinia 状态管理
    │   ├── user.js                      # 用户登录态（token持久化、login/logout/fetchUserInfo）
    │   ├── theme.js                     # 主题切换（dark/light，persist 持久化）
    │   └── queryState.js                # 查询条件持久化（siteQuery、siteSectionQuery、siteSectionDetail）
    │
    ├── api/                             # API 请求层（14个模块）
    │   ├── request.js                   # 【核心】Axios 实例（baseURL=/api，自动带token，401跳登录，解包res.data）
    │   ├── auth.js                      # login(), getCurrentUser()
    │   ├── category.js                  # getCategoryTree(), save/update/delete/batch-delete
    │   ├── site.js                      # getSitePage(), getSiteById(), save/update/delete/batch-delete
    │   ├── tag.js                       # 标签 CRUD
    │   ├── siteSection.js               # 小节 CRUD
    │   ├── siteSectionContent.js        # 小节内容 CRUD
    │   ├── siteComment.js               # 评论管理
    │   ├── siteLike.js                  # 点赞记录
    │   ├── siteClickLog.js              # 点击记录
    │   ├── siteImport.js                # 批量导入
    │   ├── sysNotice.js                 # 消息通知
    │   ├── upload.js                    # 图片上传
    │   └── user.js                      # 用户管理
    │
    ├── components/                      # 公共组件
    │   ├── ParticlesBg.vue              # Canvas 粒子动画背景（鼠标交互，用于登录页）
    │   ├── StatCard.vue                 # 统计卡片（useCountUp 数字动画）
    │   └── UploadImage.vue              # 图片上传组件（拖拽上传+预览+删除）
    │
    ├── composables/                     # Composition API 工具
    │   └── useCountUp.js                # 数字递增动画 Hook（easeOutCubic 缓动）
    │
    ├── utils/                           # 工具函数
    │   ├── image.js                     # getImageUrl() — 拼接 /uploads/ 前缀或返回完整URL
    │   └── tableActions.js              # renderEditAction/renderDeleteAction/createActionColumn — 表格操作列
    │
    ├── theme/
    │   └── naiveTheme.js                # naive-ui 主题配置（深色/浅色，主色#F97316橙色）
    │
    ├── styles/
    │   ├── variables.css                # CSS 变量定义
    │   └── global.css                   # 全局样式
    │
    └── views/                           # 页面视图（12个目录，20个Vue文件）
        ├── layout/
        │   └── AdminLayout.vue          # 【布局框架】侧边栏导航 + 顶部栏（主题切换+用户下拉） + 内容区
        ├── login/
        │   └── LoginView.vue            # 登录页（ParticlesBg 粒子背景）
        ├── dashboard/
        │   └── DashboardView.vue        # 数据概览（StatCard 统计 + 快捷操作入口）
        ├── category/
        │   ├── CategoryView.vue         # 分类列表管理
        │   └── CategoryFormModal.vue    # 分类新增/编辑弹窗
        ├── site/
        │   ├── SiteView.vue             # 网站列表管理（分页+筛选+排序）
        │   ├── SiteFormModal.vue        # 网站新增/编辑弹窗（含 UploadImage）
        │   └── SiteImportView.vue       # 批量导入（AI 解析 + 手动导入）
        ├── tag/
        │   ├── TagView.vue              # 标签管理
        │   └── TagFormModal.vue         # 标签弹窗
        ├── siteSection/
        │   ├── SiteSectionView.vue      # 小节列表
        │   └── SiteSectionDetailView.vue # 小节内容编辑
        ├── siteComment/
        │   ├── CommentView.vue          # 评论管理（审核/删除）
        │   └── CommentFormModal.vue     # 评论编辑弹窗
        ├── siteLike/
        │   └── LikeLogView.vue          # 点赞记录查看
        ├── siteClickLog/
        │   └── ClickLogView.vue         # 点击记录查看
        ├── sysNotice/
        │   ├── NoticeView.vue           # 通知列表
        │   ├── NoticeSendModal.vue      # 发送通知弹窗
        │   └── NoticeDetailModal.vue    # 通知详情弹窗
        └── user/
            └── UserView.vue             # 用户管理
```

### 4.2 数据流

```
用户操作 → View 组件 → API 函数（api/*.js）→ request.js（Axios，自动带 JWT）
     ↓
后端返回 Result { code: 200, message, data }
     ↓
request.js 拦截器：检查 code !== 200 则报错，否则解包 res.data
     ↓
View 组件拿到 data 更新 UI
```

### 4.3 设计亮点

- **主题系统**：`theme.js` Pinia store 持久化，`naiveTheme.js` 定义深色/浅色配色（主色 #F97316 橙色），通过 `createDiscreteApi` 动态切换
- **全局 API**：`window.$message`、`window.$dialog`、`window.$notification`、`window.$loadingBar` 在 `main.js` 中注入，非组件中也可调用
- **查询持久化**：`queryState.js` 中的 store 使用 `persist: true`，页面刷新不丢失筛选条件
- **路由守卫**：`router.beforeEach` 检查 token，未登录跳转 `/login?redirect=当前路径`
- **表单模式**：View 负责列表展示，FormModal 负责新增/编辑，职责分离

---

## 五、webstack-user/ 用户前端

### 5.1 完整目录结构与文件说明

```
webstack-user/
├── index.html                           # HTML 入口
├── package.json                         # 依赖（无 naive-ui，更轻量）
├── vite.config.js                       # 端口5174，代理/api+/uploads→8180
└── src/
    ├── main.js                          # 应用入口
    ├── App.vue                          # 根组件
    │
    ├── router/
    │   └── index.js                     # 仅2个路由：/ 首页、/site/:id 详情页
    │
    ├── api/                             # API 请求层（6个模块）
    │   ├── request.js                   # Axios 实例（同管理后台，但401用console.error而非跳转）
    │   ├── site.js                      # getSitePage(), getSiteDetail(), getSiteSections(), getSectionContents(), recordClick()
    │   ├── category.js                  # getCategoryTree()
    │   ├── tag.js                       # 标签
    │   ├── comment.js                   # 评论
    │   └── like.js                      # getLikeStatus(), likeSite(), unlikeSite()
    │
    ├── components/
    │   ├── layout/
    │   │   ├── AppSidebar.vue           # 左侧分类导航栏（可折叠）
    │   │   ├── AppHeader.vue            # 顶部导航栏
    │   │   └── AppFooter.vue            # 页脚
    │   ├── SearchBox.vue                # 搜索框
    │   ├── CategorySection.vue          # 分类区块（按分类展示网站卡片网格）
    │   ├── SiteCard.vue                 # 网站卡片（缩略图+标题+描述）
    │   ├── CategoryIcon.vue             # 分类图标组件
    │   ├── detail/
    │   │   └── SectionContent.vue       # 详情页内容渲染（段落/图片/列表/表格）
    │   └── comment/
    │       └── CommentList.vue          # 评论列表组件
    │
    ├── styles/
    │   ├── variables.css                # CSS 变量（颜色、间距、圆角等）
    │   └── global.css                   # 全局样式
    │
    └── views/
        ├── HomeView.vue                 # 首页（侧边栏+Hero搜索框+按分类展示网站）
        └── SiteDetailView.vue           # 网站详情页（信息卡+小节内容+评论+点赞）
```

### 5.2 功能特性

- **首页**：按分类树展示所有网站，支持关键词搜索过滤，侧边栏快速定位分类
- **详情页**：网站信息卡片（标题/标签/描述/Logo/统计数据） + 多小节富文本内容 + 评论列表 + 点赞
- **无登录态**：用户前端不需要登录，点赞通过 IP 防重

---

## 六、数据库设计（ER 关系）

### 6.1 10 张核心表

| 表名                 | 用途                               | 主键类型              |
| -------------------- | ---------------------------------- | --------------------- |
| category             | 分类表（支持二级分类）             | int auto_increment    |
| site                 | 网站主表                           | int auto_increment    |
| tag                  | 标签主表                           | int auto_increment    |
| site_tag_relation    | 网站-标签中间表                    | bigint auto_increment |
| site_section         | 网站详情小节                       | int auto_increment    |
| site_section_content | 小节内容（段落/图片/列表/表格）    | int auto_increment    |
| site_comment         | 网站评论（支持登录用户+游客+回复） | bigint auto_increment |
| site_click_log       | 点击记录                           | bigint auto_increment |
| site_like            | 点赞记录                           | bigint auto_increment |
| sys_user             | 系统用户                           | bigint auto_increment |
| sys_notice           | 消息通知                           | bigint auto_increment |

### 6.2 关系图（文字版）

```
category (自引用, parent_id → category.id)
    │
    ▼
  site (category_id → category.id)
    │
    ├──→ site_tag_relation ──→ tag
    │
    ├──→ site_section ──→ site_section_content
    │
    ├──→ site_comment (自引用, parent_id → site_comment.id)
    │
    ├──→ site_click_log
    │
    └──→ site_like

sys_user
    │
    ├──→ site_like (user_id → sys_user.id)
    ├──→ site_comment (user_id → sys_user.id)
    └──→ sys_notice (receiver_id → sys_user.id)
```

### 6.3 关系类型总结

| 关系                                | 类型   | 外键              | 级联    |
| ----------------------------------- | ------ | ----------------- | ------- |
| category → category                 | 自引用 | parent_id         | —       |
| site → category                     | 多对一 | category_id       | FK约束  |
| site ↔ tag                          | 多对多 | site_tag_relation | CASCADE |
| site → site_section                 | 一对多 | site_id           | CASCADE |
| site_section → site_section_content | 一对多 | section_id        | CASCADE |
| site → site_comment                 | 一对多 | site_id           | CASCADE |
| site_comment → site_comment         | 自引用 | parent_id         | CASCADE |
| site → site_click_log               | 一对多 | site_id           | CASCADE |
| site → site_like                    | 一对多 | site_id           | CASCADE |
| sys_user → sys_notice               | 一对多 | receiver_id       | CASCADE |

### 6.4 site_section_content 内容类型

| contentType | json_data 格式                          | 用途     |
| ----------- | --------------------------------------- | -------- |
| paragraph   | — (使用 textContent 字段)               | 文本段落 |
| image       | `{url: string, alt: string}`            | 图片     |
| list_item   | `{items: [string]}`                     | 列表     |
| table       | `{headers: [string], rows: [[string]]}` | 表格     |

---

## 七、配置文件解读

### 7.1 application.yml（主配置）

```yaml
server:
  port: 8180 # 后端端口
spring:
  jackson:
    default-property-inclusion: non_null # 响应JSON忽略null字段
    date-format: yyyy-MM-dd HH:mm:ss # 日期格式
    time-zone: GMT+8 # 时区
jwt:
  secret: ${JWT_SECRET:dev-secret-key-do-not-use-in-production} # JWT密钥（生产环境必须设置）
  expiration: ${JWT_EXPIRATION:604800} # 过期时间：7天（秒）
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true # 下划线→驼峰自动映射
  mapper-locations: classpath:/mapper/**/*.xml # XML映射文件扫描
  global-config:
    db-config:
      id-type: auto # 全局主键策略：自增
app:
  storage:
    local:
      upload-dir: ${UPLOAD_DIR:./uploads/images} # 文件存储目录
      allowed-extensions: jpg,jpeg,png,webp,gif # 允许的扩展名
      max-file-size: 5242880 # 5MB
```

### 7.2 application-dev.yml（开发环境）

```yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:mysql://localhost:3306/webstack2?useSSL=false&serverTimezone=Asia/Shanghai}
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:a15207291068} # 默认密码，生产环境用环境变量覆盖
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdoutStdOutImpl # 控制台打印SQL
deepseek:
  base-url: ${DEEPSEEK_BASE_URL:https://api.deepseek.com} # AI接口地址
```

### 7.3 pom.xml（核心依赖）

| 依赖                              | 版本     | 用途               |
| --------------------------------- | -------- | ------------------ |
| spring-boot-starter-web           | 3.3.5    | Web框架            |
| mybatis-plus-spring-boot3-starter | 3.5.10.1 | ORM                |
| mysql-connector-j                 | runtime  | MySQL驱动          |
| spring-boot-starter-validation    | —        | 参数校验（@Valid） |
| spring-security-crypto            | —        | BCrypt密码加密     |
| jjwt-api/impl/jackson             | 0.12.6   | JWT                |
| lombok                            | optional | 简化代码           |
| spring-boot-starter-test          | test     | 测试               |

---

## 八、开发命令

```bash
# 后端启动（需要先启动 MySQL 并创建 webstack2 数据库）
cd webstack-server && mvn spring-boot:run

# 管理后台（代理 /api → localhost:8180）
cd webstack-admin && npm install && npm run dev     # → http://localhost:5173

# 用户前端（代理 /api → localhost:8180）
cd webstack-user && npm install && npm run dev      # → http://localhost:5174

# 构建
cd webstack-server && mvn clean package -DskipTests
cd webstack-admin && npm run build
cd webstack-user && npm run build

# 数据库初始化
# 1. 执行 script.sql 创建表结构
# 2. 执行 seed_data.sql 导入测试数据（会清空所有表！）
```

---

## 九、技术选型与设计思路

### 9.1 为什么选这些技术

- **Spring Boot 3 + Java 22**：最新 LTS，原生虚拟线程支持，Jakarta EE
- **MyBatis-Plus**：在 MyBatis 基础上提供 CRUD 自动化，复杂查询仍用 XML，兼顾效率和灵活性
- **Vue 3 + naive-ui**：Vue 3 Composition API + naive-ui 的完整组件生态，适合中后台
- **Pinia + persist**：替代 Vuex，持久化插件让查询条件刷新不丢失
- **DeepSeek AI**：批量导入时用 AI 解析任意格式文本为标准网站数据

### 9.2 架构设计思路

- **前后端分离**：后端纯 REST API，前端通过 Vite proxy 开发，生产环境 nginx 反代
- **模块化后端**：每个业务领域独立包，新增功能只需新增模块，不修改现有代码
- **策略模式**：存储抽象为接口，当前用本地存储，未来可换 OSS
- **注解式权限**：`@RequireRole` 声明式控制，比硬编码 if-else 更清晰
- **全局异常处理**：一处捕获所有异常，返回统一格式，前端只需处理一种响应结构

### 9.3 Gotchas（注意事项）

- Database password 在 `application-dev.yml` — 用环境变量 `DB_URL`、`DB_USERNAME`、`DB_PASSWORD` 覆盖
- JWT secret 默认 dev-only 值 — 生产环境必须设置 `JWT_SECRET` 环境变量
- MyBatis-Plus `id-type: auto` 全局 — 不要在 entity 上设 `IdType.ASSIGN_ID`
- 前端 `request.js` 拦截器检查 `code !== 200`（业务码），不是 HTTP 状态码
- `seed_data.sql` 会 TRUNCATE 所有表 — 只用于开发环境
- `sys_user` 表有 `role` 字段（ADMIN/USER），默认 USER — 初始管理员需手动设为 ADMIN
