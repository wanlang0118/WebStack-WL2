# WebStack-WL2

WebStack 网址导航站（书签管理），前后端分离架构。

## 项目结构

```
WebStack-WL2/
├── webstack-server/     # Java 后端 (Spring Boot 3.3.5 + MyBatis-Plus + MySQL)
├── webstack-admin/      # 管理后台前端 (Vue 3 + naive-ui)
└── webstack-user/       # 用户端前台 (Vue 3，骨架工程)
```

## 快速启动

### 后端

```bash
cd webstack-server
mvn spring-boot:run
```

后端运行在 `http://localhost:8180`。

### 管理后台

```bash
cd webstack-admin
npm install
npm run dev
```

管理后台运行在 `http://localhost:5173`，自动代理 `/api` 到后端 8180。

### 用户端

```bash
cd webstack-user
npm install
npm run dev
```

用户端运行在 `http://localhost:5174`。

## 技术栈

| 层 | 技术 |
|------|------|
| 后端 | Java 22, Spring Boot 3.3.5, MyBatis-Plus 3.5.10.1, MySQL |
| 管理后台 | Vue 3 (Composition API), Vite 5, naive-ui |
| 用户端 | Vue 3 (Composition API), Vite 5 |

## 数据库

- 数据库名：`webstack2`，字符集：utf8mb4
- 数据库用户：`root`，密码在 `application-dev.yml` 中配置

## License

MIT
