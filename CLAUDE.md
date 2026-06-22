# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

WebStack 是一个网址导航站（书签管理），采用前后端分离架构。后端提供 RESTful API，管理后台用于增删改数据，用户端前台展示导航页面（用户端目前为骨架工程）。

## Architecture

```
WebStack-WL2/
├── webstack-server/     # Java 后端 (Spring Boot + MyBatis-Plus + MySQL)
├── webstack-admin/      # 管理后台前端 (Vue 3 + naive-ui)
├── webstack-user/       # 用户端前台 (Vue 3，当前为骨架工程)
```

### 后端分层结构 (`webstack-server`)

包路径：`webstack`

```
common/
├── config/              # 全局配置（CORS、MyBatis-Plus 分页插件）
├── exception/           # BizException + 全局异常处理器
└── result/              # 统一响应体 Result<T> + 状态码枚举 ResultCode

module/
├── category/            # 网站分类管理
│   ├── controller/      # CategoryController — /api/category/*
│   ├── entity/          # Category (分类)
│   ├── mapper/
│   ├── service/         # CategoryService — 含级联删除、树形构建
│   └── vo/              # CategoryTreeVO — 树形分类 VO
├── site/                # 网站管理
│   ├── controller/      # SiteController — /api/site/*
│   ├── dto/             # SiteQueryDTO — 分页查询参数
│   ├── entity/          # Site, SiteTagRelation
│   ├── mapper/          # SiteMapper + SiteMapper.xml (动态 SQL)
│   ├── service/         # SiteService — 批量查询标签关联
│   └── vo/              # SiteVO — 含 tagIds + tagNameList
└── tag/                 # 标签管理
    ├── controller/      # TagController — /api/tag/*
    ├── entity/          # Tag
    ├── mapper/
    └── service/         # TagService — 分页、全量列表
```

- 标准三层架构：Controller → Service(Impl) → Mapper → Entity
- Service 层继承 MyBatis-Plus `IService` / `ServiceImpl`，实现类扩展自定义业务方法
- 统一响应封装为 `Result<T>` （code/message/data），状态码枚举 `ResultCode`
- 全局异常处理通过 `@RestControllerAdvice` 的 `GlobalExceptionHandler`
- 事务管理：写操作（save/update）通过 `@Transactional(rollbackFor = Exception.class)`
- 自定义 SQL 存放在 `src/main/resources/mapper/**/*.xml` 中，支持动态排序

### 管理后台前端 (`webstack-admin`)

```
src/
├── main.js              # Vue 3 入口
├── App.vue              # 根组件（仅 router-view）
├── router/index.js      # 路由配置（/site, /category, /tag）
├── api/                 # axios 封装（request.js）+ 各模块 API
│   ├── request.js       # baseURL=/api，统一响应拦截（解包 data）
│   ├── category.js
│   ├── site.js
│   └── tag.js
└── views/
    ├── layout/AdminLayout.vue   # 侧边栏 + 顶栏 + 内容区布局
    ├── category/                # 分类管理（树形表格 + 行内开关）
    ├── site/                    # 网站管理（分页表格 + 列设置 + 拖拽排序）
    └── tag/                     # 标签管理（分页表格 + 列设置）
```

- 使用 Vue 3 `<script setup>` + 组合式 API
- naive-ui 组件库（NDataTable, NSwitch, NModal 等）
- axios 拦截器自动解包 `res.data`，直接在组件层拿到业务数据
- 管理后台包含三个页面：网站管理、分类管理、标签管理
- 所有表单用 Modal 弹窗（SiteFormModal, CategoryFormModal, TagFormModal）
- 标签支持即时创建（在 SiteFormModal 中输入新标签名自动调用 save 并回填 ID）

### 用户端 (`webstack-user`)

当前为 Vue 3 + Vite 骨架工程，仅含 HelloWorld 组件。

### 数据库

- 数据库名：`webstack2`，字符集：utf8mb4
- 核心表：category（分类树）、site（网站）、site_category_relation（N:N 关联）
- 互动表：site_like（点赞）、site_comment（评论）、site_click_log（点击日志）
- 内容表：site_section + site_section_content（详情页分段内容）
- 通知表：sys_notice，用户表：sys_user

## Development Commands

### 后端

```bash
# 启动后端服务（端口 8180）
cd webstack-server && mvn spring-boot:run

# 编译打包
mvn clean package -DskipTests



```

### 管理后台

```bash
cd webstack-admin

# 安装依赖
npm install

# 启动开发服务器（端口 5173，自动代理 /api 到 8180）
npm run dev

# 构建生产包
npm run build

# 预览构建结果
npm run preview
```

### 用户端

```bash
cd webstack-user
npm install
npm run dev
```

## Key Conventions

- 后端使用 Java 22、Spring Boot 3.3.5、MyBatis-Plus 3.5.10.1
- 后端实体类使用 Lombok `@Data`，主键统一 `@TableId(type = IdType.AUTO)`
- 后端模块间通过 Maven（单模块`webstack-server/pom.xml`）管理
- 数据源配置在 `application-dev.yml` — 数据库用户 `root`，密码 `a15207291068`
- 前端纯 ES Module（`"type": "module"`），使用 `<script setup>` 组合式 API
- 前端组件名使用 PascalCase 多词命名
- 管理后台的 `/api` 路径在 vite.config.js 中代理到 `http://localhost:8180`
