const { Document, Packer, Paragraph, TextRun, Table, TableRow, TableCell, ImageRun,
        Header, Footer, AlignmentType, LevelFormat, HeadingLevel,
        TableOfContents, PageBreak, PageNumber, BorderStyle, WidthType, ShadingType } = require('docx');
const fs = require('fs');
const { exec } = require('child_process');
const path = require('path');

// 生成图表的函数
async function generateCharts() {
  // 创建临时目录
  const tempDir = path.join(__dirname, 'temp');
  if (!fs.existsSync(tempDir)) {
    fs.mkdirSync(tempDir);
  }

  // 1. 系统架构图 - 使用Mermaid
  const architectureMermaid = `
graph TB
    subgraph 表现层
        A1[用户前端<br/>Vue3 + Vite]
        A2[管理后台<br/>Vue3 + naive-ui]
    end
    
    subgraph 业务逻辑层
        B1[Controller层<br/>RESTful API]
        B2[Service层<br/>业务逻辑]
        B3[Mapper层<br/>数据访问]
    end
    
    subgraph 数据访问层
        C1[MyBatis-Plus<br/>ORM框架]
        C2[MySQL数据库<br/>数据存储]
    end
    
    subgraph 外部服务
        D1[JWT认证<br/>身份验证]
        D2[DeepSeek API<br/>AI服务]
    end
    
    A1 --> B1
    A2 --> B1
    B1 --> B2
    B2 --> B3
    B3 --> C1
    C1 --> C2
    B2 --> D1
    B2 --> D2
  `;

  // 2. 功能模块图
  const moduleMermaid = `
graph TB
    A[WebStack-WL2<br/>网站导航系统] --> B[用户前端]
    A --> C[管理后台]
    
    B --> B1[首页展示]
    B --> B2[分类导航]
    B --> B3[网站详情]
    B --> B4[搜索功能]
    B --> B5[评论点赞]
    
    C --> C1[登录认证]
    C --> C2[网站管理]
    C --> C3[分类管理]
    C --> C4[评论管理]
    C --> C5[用户管理]
    C --> C6[数据统计]
    C --> C7[批量导入]
    C --> C8[消息通知]
  `;

  // 3. 数据库ER图
  const erMermaid = `
erDiagram
    CATEGORY {
        int id PK
        int parent_id FK
        string title
        string icon
        int sort
        boolean visible
    }
    
    SITE {
        int id PK
        int category_id FK
        string title
        string url
        string description
        string thumb
        int like_count
        int comment_count
        int click_count
        boolean is_recommended
        int sort
        boolean visible
    }
    
    TAG {
        int id PK
        string tag_name
        datetime create_time
    }
    
    SITE_TAG_RELATION {
        bigint id PK
        int site_id FK
        int tag_id FK
    }
    
    SITE_SECTION {
        int id PK
        int site_id FK
        string title
        int sort
        datetime create_time
    }
    
    SITE_SECTION_CONTENT {
        int id PK
        int section_id FK
        string content_type
        string text_content
        json json_data
        int sort
    }
    
    SYS_USER {
        bigint id PK
        string username
        string password
        string name
        string avatar
        int sex
        string email
        string phone
        string role
        int status
        datetime last_login_time
        string last_login_ip
    }
    
    SITE_COMMENT {
        bigint id PK
        int site_id FK
        bigint user_id FK
        string guest_name
        string guest_email
        string content
        bigint parent_id FK
        int status
        datetime create_time
    }
    
    SITE_LIKE {
        bigint id PK
        int site_id FK
        bigint user_id FK
        string ip
        datetime create_time
    }
    
    SITE_CLICK_LOG {
        bigint id PK
        int site_id FK
        bigint user_id FK
        string ip
        datetime create_time
    }
    
    SYS_NOTICE {
        bigint id PK
        bigint sender_id FK
        string sender_name
        bigint receiver_id FK
        string notice_type
        int source_site_id FK
        bigint source_comment_id FK
        string title
        string summary
        string content
        int is_read
        datetime read_time
    }
    
    CATEGORY ||--o{ CATEGORY : "parent_id"
    CATEGORY ||--o{ SITE : "category_id"
    SITE ||--o{ SITE_TAG_RELATION : "site_id"
    TAG ||--o{ SITE_TAG_RELATION : "tag_id"
    SITE ||--o{ SITE_SECTION : "site_id"
    SITE_SECTION ||--o{ SITE_SECTION_CONTENT : "section_id"
    SITE ||--o{ SITE_COMMENT : "site_id"
    SITE_COMMENT ||--o{ SITE_COMMENT : "parent_id"
    SYS_USER ||--o{ SITE_COMMENT : "user_id"
    SITE ||--o{ SITE_LIKE : "site_id"
    SYS_USER ||--o{ SITE_LIKE : "user_id"
    SITE ||--o{ SITE_CLICK_LOG : "site_id"
    SYS_USER ||--o{ SITE_CLICK_LOG : "user_id"
    SYS_USER ||--o{ SYS_NOTICE : "receiver_id"
    SYS_USER ||--o{ SYS_NOTICE : "sender_id"
    SITE ||--o{ SYS_NOTICE : "source_site_id"
    SITE_COMMENT ||--o{ SYS_NOTICE : "source_comment_id"
  `;

  // 4. 用户实体图
  const userEntityMermaid = `
classDiagram
    class SysUser {
        +Long id
        +String username
        +String password
        +String name
        +String avatar
        +Integer sex
        +String email
        +String phone
        +String role
        +Integer status
        +Date lastLoginTime
        +String lastLoginIp
        +Date createTime
        +Date updateTime
    }
    
    class Site {
        +Integer id
        +Integer categoryId
        +String title
        +String url
        +String description
        +String thumb
        +Integer likeCount
        +Integer commentCount
        +Integer clickCount
        +Boolean isRecommended
        +Integer sort
        +Boolean visible
        +Date createTime
        +Date updateTime
    }
    
    class Category {
        +Integer id
        +Integer parentId
        +String title
        +String enTitle
        +String icon
        +Integer sort
        +Boolean visible
        +Date createTime
        +Date updateTime
    }
    
    class Tag {
        +Integer id
        +String tagName
        +Date createTime
    }
    
    class SiteComment {
        +Long id
        +Integer siteId
        +Long userId
        +String guestName
        +String guestEmail
        +String guestUrl
        +String content
        +Long parentId
        +Integer status
        +Date createTime
    }
    
    class SiteLike {
        +Long id
        +Integer siteId
        +Long userId
        +String ip
        +Date createTime
    }
    
    SysUser "1" --> "*" SiteComment : comments
    SysUser "1" --> "*" SiteLike : likes
    Site "1" --> "*" SiteComment : has
    Site "1" --> "*" SiteLike : has
    Category "1" --> "*" Site : contains
    Site "*" --> "*" Tag : tagged
  `;

  // 保存Mermaid文件
  fs.writeFileSync(path.join(tempDir, 'architecture.mmd'), architectureMermaid);
  fs.writeFileSync(path.join(tempDir, 'module.mmd'), moduleMermaid);
  fs.writeFileSync(path.join(tempDir, 'er.mmd'), erMermaid);
  fs.writeFileSync(path.join(tempDir, 'user-entity.mmd'), userEntityMermaid);

  // 创建HTML文件用于渲染
  const htmlContent = `
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>图表渲染</title>
    <script src="https://cdn.jsdelivr.net/npm/mermaid/dist/mermaid.min.js"></script>
    <script>
        mermaid.initialize({ startOnLoad: true, theme: 'default' });
    </script>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .chart-container { margin: 20px 0; page-break-after: always; }
        h2 { color: #333; }
    </style>
</head>
<body>
    <div class="chart-container">
        <h2>图4-1 系统架构图</h2>
        <div class="mermaid">
            ${architectureMermaid}
        </div>
    </div>
    
    <div class="chart-container">
        <h2>图4-2 功能模块图</h2>
        <div class="mermaid">
            ${moduleMermaid}
        </div>
    </div>
    
    <div class="chart-container">
        <h2>图4-3 数据库ER图</h2>
        <div class="mermaid">
            ${erMermaid}
        </div>
    </div>
    
    <div class="chart-container">
        <h2>图4-4 用户实体图</h2>
        <div class="mermaid">
            ${userEntityMermaid}
        </div>
    </div>
</body>
</html>
  `;

  fs.writeFileSync(path.join(tempDir, 'charts.html'), htmlContent);

  // 创建PNG版本的图表（使用简单的SVG）
  // 由于Mermaid需要浏览器环境，我们创建SVG版本
  
  // 系统架构图SVG
  const architectureSvg = `
<svg width="600" height="400" xmlns="http://www.w3.org/2000/svg">
  <style>
    .box { fill: #e1f5fe; stroke: #0288d1; stroke-width: 2; }
    .subgraph { fill: #f3e5f5; stroke: #7b1fa2; stroke-width: 1; stroke-dasharray: 5,5; }
    .arrow { stroke: #333; stroke-width: 2; marker-end: url(#arrowhead); }
    text { font-family: Arial; font-size: 12px; text-anchor: middle; }
    .title { font-size: 16px; font-weight: bold; }
  </style>
  <defs>
    <marker id="arrowhead" markerWidth="10" markerHeight="7" refX="9" refY="3.5" orient="auto">
      <polygon points="0 0, 10 3.5, 0 7" fill="#333"/>
    </marker>
  </defs>
  
  <!-- 表现层 -->
  <rect x="50" y="20" width="500" height="80" class="subgraph"/>
  <text x="300" y="40" class="title">表现层</text>
  <rect x="80" y="50" width="200" height="40" class="box"/>
  <text x="180" y="75">用户前端 (Vue3 + Vite)</text>
  <rect x="320" y="50" width="200" height="40" class="box"/>
  <text x="420" y="75">管理后台 (Vue3 + naive-ui)</text>
  
  <!-- 业务逻辑层 -->
  <rect x="50" y="120" width="500" height="80" class="subgraph"/>
  <text x="300" y="140" class="title">业务逻辑层</text>
  <rect x="80" y="150" width="140" height="40" class="box"/>
  <text x="150" y="175">Controller层</text>
  <rect x="240" y="150" width="140" height="40" class="box"/>
  <text x="310" y="175">Service层</text>
  <rect x="400" y="150" width="140" height="40" class="box"/>
  <text x="470" y="175">Mapper层</text>
  
  <!-- 数据访问层 -->
  <rect x="50" y="220" width="500" height="80" class="subgraph"/>
  <text x="300" y="240" class="title">数据访问层</text>
  <rect x="80" y="250" width="200" height="40" class="box"/>
  <text x="180" y="275">MyBatis-Plus</text>
  <rect x="320" y="250" width="200" height="40" class="box"/>
  <text x="420" y="275">MySQL数据库</text>
  
  <!-- 外部服务 -->
  <rect x="50" y="320" width="500" height="60" class="subgraph"/>
  <text x="300" y="340" class="title">外部服务</text>
  <rect x="80" y="350" width="200" height="30" class="box"/>
  <text x="180" y="370">JWT认证</text>
  <rect x="320" y="350" width="200" height="30" class="box"/>
  <text x="420" y="370">DeepSeek API</text>
  
  <!-- 连接线 -->
  <line x1="180" y1="90" x2="150" y2="150" class="arrow"/>
  <line x1="420" y1="90" x2="150" y2="150" class="arrow"/>
  <line x1="150" y1="190" x2="310" y2="150" class="arrow"/>
  <line x1="310" y1="190" x2="470" y2="150" class="arrow"/>
  <line x1="470" y1="190" x2="180" y2="250" class="arrow"/>
  <line x1="180" y1="290" x2="420" y2="250" class="arrow"/>
  <line x1="310" y1="190" x2="180" y2="350" class="arrow"/>
  <line x1="310" y1="190" x2="420" y2="350" class="arrow"/>
</svg>
  `;

  // 功能模块图SVG
  const moduleSvg = `
<svg width="600" height="400" xmlns="http://www.w3.org/2000/svg">
  <style>
    .box { fill: #e8f5e9; stroke: #388e3c; stroke-width: 2; }
    .subbox { fill: #fff3e0; stroke: #f57c00; stroke-width: 1; }
    .arrow { stroke: #333; stroke-width: 1.5; marker-end: url(#arrowhead); }
    text { font-family: Arial; font-size: 11px; text-anchor: middle; }
    .title { font-size: 14px; font-weight: bold; }
  </style>
  <defs>
    <marker id="arrowhead" markerWidth="8" markerHeight="6" refX="7" refY="3" orient="auto">
      <polygon points="0 0, 8 3, 0 6" fill="#333"/>
    </marker>
  </defs>
  
  <!-- 主系统 -->
  <rect x="200" y="20" width="200" height="40" class="box"/>
  <text x="300" y="45" class="title">WebStack-WL2</text>
  
  <!-- 用户前端 -->
  <rect x="50" y="100" width="200" height="30" class="box"/>
  <text x="150" y="120">用户前端</text>
  
  <!-- 管理后台 -->
  <rect x="350" y="100" width="200" height="30" class="box"/>
  <text x="450" y="120">管理后台</text>
  
  <!-- 用户前端子模块 -->
  <rect x="20" y="160" width="120" height="25" class="subbox"/>
  <text x="80" y="177">首页展示</text>
  
  <rect x="150" y="160" width="120" height="25" class="subbox"/>
  <text x="210" y="177">分类导航</text>
  
  <rect x="20" y="195" width="120" height="25" class="subbox"/>
  <text x="80" y="212">网站详情</text>
  
  <rect x="150" y="195" width="120" height="25" class="subbox"/>
  <text x="210" y="212">搜索功能</text>
  
  <rect x="85" y="230" width="120" height="25" class="subbox"/>
  <text x="145" y="247">评论点赞</text>
  
  <!-- 管理后台子模块 -->
  <rect x="330" y="160" width="120" height="25" class="subbox"/>
  <text x="390" y="177">登录认证</text>
  
  <rect x="460" y="160" width="120" height="25" class="subbox"/>
  <text x="520" y="177">网站管理</text>
  
  <rect x="330" y="195" width="120" height="25" class="subbox"/>
  <text x="390" y="212">分类管理</text>
  
  <rect x="460" y="195" width="120" height="25" class="subbox"/>
  <text x="520" y="212">评论管理</text>
  
  <rect x="330" y="230" width="120" height="25" class="subbox"/>
  <text x="390" y="247">用户管理</text>
  
  <rect x="460" y="230" width="120" height="25" class="subbox"/>
  <text x="520" y="247">数据统计</text>
  
  <rect x="330" y="265" width="120" height="25" class="subbox"/>
  <text x="390" y="282">批量导入</text>
  
  <rect x="460" y="265" width="120" height="25" class="subbox"/>
  <text x="520" y="282">消息通知</text>
  
  <!-- 连接线 -->
  <line x1="250" y1="60" x2="150" y2="100" class="arrow"/>
  <line x1="350" y1="60" x2="450" y2="100" class="arrow"/>
  <line x1="150" y1="130" x2="80" y2="160" class="arrow"/>
  <line x1="150" y1="130" x2="210" y2="160" class="arrow"/>
  <line x1="150" y1="130" x2="80" y2="195" class="arrow"/>
  <line x1="150" y1="130" x2="210" y2="195" class="arrow"/>
  <line x1="150" y1="130" x2="145" y2="230" class="arrow"/>
  <line x1="450" y1="130" x2="390" y2="160" class="arrow"/>
  <line x1="450" y1="130" x2="520" y2="160" class="arrow"/>
  <line x1="450" y1="130" x2="390" y2="195" class="arrow"/>
  <line x1="450" y1="130" x2="520" y2="195" class="arrow"/>
  <line x1="450" y1="130" x2="390" y2="230" class="arrow"/>
  <line x1="450" y1="130" x2="520" y2="230" class="arrow"/>
  <line x1="450" y1="130" x2="390" y2="265" class="arrow"/>
  <line x1="450" y1="130" x2="520" y2="265" class="arrow"/>
</svg>
  `;

  // ER图SVG
  const erSvg = `
<svg width="700" height="500" xmlns="http://www.w3.org/2000/svg">
  <style>
    .entity { fill: #e3f2fd; stroke: #1565c0; stroke-width: 2; }
    .relation { fill: #fce4ec; stroke: #c62828; stroke-width: 1.5; }
    .line { stroke: #333; stroke-width: 1.5; }
    text { font-family: Arial; font-size: 10px; }
    .title { font-size: 12px; font-weight: bold; }
  </style>
  
  <!-- 实体 -->
  <rect x="50" y="50" width="120" height="60" class="entity"/>
  <text x="110" y="70" class="title">CATEGORY</text>
  <text x="110" y="85">id, parent_id</text>
  <text x="110" y="100">title, icon</text>
  
  <rect x="250" y="50" width="120" height="60" class="entity"/>
  <text x="310" y="70" class="title">SITE</text>
  <text x="310" y="85">id, category_id</text>
  <text x="310" y="100">title, url</text>
  
  <rect x="450" y="50" width="120" height="60" class="entity"/>
  <text x="510" y="70" class="title">TAG</text>
  <text x="510" y="85">id, tag_name</text>
  <text x="510" y="100">create_time</text>
  
  <rect x="50" y="150" width="120" height="60" class="entity"/>
  <text x="110" y="170" class="title">SYS_USER</text>
  <text x="110" y="185">id, username</text>
  <text x="110" y="200">password, role</text>
  
  <rect x="250" y="150" width="120" height="60" class="entity"/>
  <text x="310" y="170" class="title">SITE_COMMENT</text>
  <text x="310" y="185">id, site_id</text>
  <text x="310" y="200">user_id, content</text>
  
  <rect x="450" y="150" width="120" height="60" class="entity"/>
  <text x="510" y="170" class="title">SITE_LIKE</text>
  <text x="510" y="185">id, site_id</text>
  <text x="510" y="200">user_id, ip</text>
  
  <rect x="50" y="250" width="120" height="60" class="entity"/>
  <text x="110" y="270" class="title">SITE_SECTION</text>
  <text x="110" y="285">id, site_id</text>
  <text x="110" y="300">title, sort</text>
  
  <rect x="250" y="250" width="120" height="60" class="entity"/>
  <text x="310" y="270" class="title">SITE_SECTION_CONTENT</text>
  <text x="310" y="285">id, section_id</text>
  <text x="310" y="300">content_type</text>
  
  <rect x="450" y="250" width="120" height="60" class="entity"/>
  <text x="510" y="270" class="title">SYS_NOTICE</text>
  <text x="510" y="285">id, receiver_id</text>
  <text x="510" y="300">title, content</text>
  
  <rect x="250" y="350" width="120" height="60" class="entity"/>
  <text x="310" y="370" class="title">SITE_CLICK_LOG</text>
  <text x="310" y="385">id, site_id</text>
  <text x="310" y="400">user_id, ip</text>
  
  <rect x="450" y="350" width="120" height="60" class="entity"/>
  <text x="510" y="370" class="title">SITE_TAG_RELATION</text>
  <text x="510" y="385">id, site_id</text>
  <text x="510" y="400">tag_id</text>
  
  <!-- 关系线 -->
  <line x1="170" y1="80" x2="250" y2="80" class="line"/>
  <line x1="370" y1="80" x2="450" y2="80" class="line"/>
  <line x1="110" y1="110" x2="110" y2="150" class="line"/>
  <line x1="310" y1="110" x2="310" y2="150" class="line"/>
  <line x1="510" y1="110" x2="510" y2="150" class="line"/>
  <line x1="110" y1="210" x2="110" y2="250" class="line"/>
  <line x1="310" y1="210" x2="310" y2="250" class="line"/>
  <line x1="510" y1="210" x2="510" y2="250" class="line"/>
  <line x1="170" y1="280" x2="250" y2="280" class="line"/>
  <line x1="370" y1="280" x2="450" y2="280" class="line"/>
  <line x1="310" y1="310" x2="310" y2="350" class="line"/>
  <line x1="510" y1="310" x2="510" y2="350" class="line"/>
</svg>
  `;

  // 用户实体图SVG
  const userEntitySvg = `
<svg width="500" height="400" xmlns="http://www.w3.org/2000/svg">
  <style>
    .class { fill: #e8eaf6; stroke: #283593; stroke-width: 2; }
    .attr { fill: #f3e5f5; stroke: #7b1fa2; stroke-width: 1; }
    .line { stroke: #333; stroke-width: 1.5; }
    text { font-family: Arial; font-size: 11px; }
    .title { font-size: 13px; font-weight: bold; }
  </style>
  
  <!-- SysUser类 -->
  <rect x="50" y="50" width="200" height="250" class="class"/>
  <text x="150" y="70" class="title">SysUser</text>
  <line x1="50" y1="80" x2="250" y2="80" class="line"/>
  
  <rect x="60" y="90" width="180" height="20" class="attr"/>
  <text x="150" y="105">+Long id</text>
  
  <rect x="60" y="115" width="180" height="20" class="attr"/>
  <text x="150" y="130">+String username</text>
  
  <rect x="60" y="140" width="180" height="20" class="attr"/>
  <text x="150" y="155">+String password</text>
  
  <rect x="60" y="165" width="180" height="20" class="attr"/>
  <text x="150" y="180">+String name</text>
  
  <rect x="60" y="190" width="180" height="20" class="attr"/>
  <text x="150" y="205">+String role</text>
  
  <rect x="60" y="215" width="180" height="20" class="attr"/>
  <text x="150" y="230">+Integer status</text>
  
  <rect x="60" y="240" width="180" height="20" class="attr"/>
  <text x="150" y="255">+Date lastLoginTime</text>
  
  <rect x="60" y="265" width="180" height="20" class="attr"/>
  <text x="150" y="280">+String lastLoginIp</text>
  
  <!-- Site类 -->
  <rect x="300" y="50" width="200" height="250" class="class"/>
  <text x="400" y="70" class="title">Site</text>
  <line x1="300" y1="80" x2="500" y2="80" class="line"/>
  
  <rect x="310" y="90" width="180" height="20" class="attr"/>
  <text x="400" y="105">+Integer id</text>
  
  <rect x="310" y="115" width="180" height="20" class="attr"/>
  <text x="400" y="130">+Integer categoryId</text>
  
  <rect x="310" y="140" width="180" height="20" class="attr"/>
  <text x="400" y="155">+String title</text>
  
  <rect x="310" y="165" width="180" height="20" class="attr"/>
  <text x="400" y="180">+String url</text>
  
  <rect x="310" y="190" width="180" height="20" class="attr"/>
  <text x="400" y="205">+String description</text>
  
  <rect x="310" y="215" width="180" height="20" class="attr"/>
  <text x="400" y="230">+Integer likeCount</text>
  
  <rect x="310" y="240" width="180" height="20" class="attr"/>
  <text x="400" y="255">+Integer commentCount</text>
  
  <rect x="310" y="265" width="180" height="20" class="attr"/>
  <text x="400" y="280">+Integer clickCount</text>
  
  <!-- 关系 -->
  <line x1="250" y1="150" x2="300" y2="150" class="line"/>
  <text x="275" y="145">1</text>
  <text x="285" y="145">*</text>
  
  <!-- 标注 -->
  <text x="150" y="320">用户实体包含：ID、用户名、密码、角色、状态等属性</text>
  <text x="400" y="320">网站实体包含：ID、分类、标题、URL、描述等属性</text>
</svg>
  `;

  // 保存SVG文件
  fs.writeFileSync(path.join(tempDir, 'architecture.svg'), architectureSvg);
  fs.writeFileSync(path.join(tempDir, 'module.svg'), moduleSvg);
  fs.writeFileSync(path.join(tempDir, 'er.svg'), erSvg);
  fs.writeFileSync(path.join(tempDir, 'user-entity.svg'), userEntitySvg);

  // 将SVG转换为PNG（使用sharp或canvas，这里简化处理）
  // 由于环境限制，我们直接使用SVG格式
  
  return {
    architecture: path.join(tempDir, 'architecture.svg'),
    module: path.join(tempDir, 'module.svg'),
    er: path.join(tempDir, 'er.svg'),
    userEntity: path.join(tempDir, 'user-entity.svg')
  };
}

// 主函数
async function main() {
  try {
    console.log('开始生成图表...');
    const charts = await generateCharts();
    console.log('图表生成完成！');
    console.log('图表路径：', charts);
    
    // 读取SVG文件内容
    const architectureSvg = fs.readFileSync(charts.architecture, 'utf8');
    const moduleSvg = fs.readFileSync(charts.module, 'utf8');
    const erSvg = fs.readFileSync(charts.er, 'utf8');
    const userEntitySvg = fs.readFileSync(charts.userEntity, 'utf8');
    
    // 创建文档
    const doc = new Document({
      sections: [{
        children: [
          new Paragraph({
            heading: HeadingLevel.HEADING_1,
            children: [new TextRun("系统架构图")]
          }),
          new Paragraph({
            children: [new TextRun("图4-1 系统架构图")]
          }),
          new Paragraph({
            children: [new TextRun({
              text: architectureSvg,
              font: "Courier New",
              size: 10
            })]
          }),
          
          new Paragraph({
            heading: HeadingLevel.HEADING_1,
            children: [new TextRun("功能模块图")]
          }),
          new Paragraph({
            children: [new TextRun("图4-2 功能模块图")]
          }),
          new Paragraph({
            children: [new TextRun({
              text: moduleSvg,
              font: "Courier New",
              size: 10
            })]
          }),
          
          new Paragraph({
            heading: HeadingLevel.HEADING_1,
            children: [new TextRun("数据库ER图")]
          }),
          new Paragraph({
            children: [new TextRun("图4-3 数据库ER图")]
          }),
          new Paragraph({
            children: [new TextRun({
              text: erSvg,
              font: "Courier New",
              size: 10
            })]
          }),
          
          new Paragraph({
            heading: HeadingLevel.HEADING_1,
            children: [new TextRun("用户实体图")]
          }),
          new Paragraph({
            children: [new TextRun("图4-4 用户实体图")]
          }),
          new Paragraph({
            children: [new TextRun({
              text: userEntitySvg,
              font: "Courier New",
              size: 10
            })]
          })
        ]
      }]
    });
    
    // 生成文档
    const buffer = await Packer.toBuffer(doc);
    fs.writeFileSync('../图表文档.docx', buffer);
    console.log('图表文档生成成功！');
    
  } catch (error) {
    console.error('生成图表时出错：', error);
  }
}

// 运行主函数
main();