# Design

## Mood

清晨花园 — 露水般的清新，自然的秩序感。像 Notion 的克制、Linear 的精致、Things 的条理。绿色传达信任、成长、可靠。

## Color Strategy

Committed — 柔和绿色作为主色，承载 30-60% 的视觉重量。清新、现代、自然。

## Palette

```css
:root {
  /* 背景 — 纯白，让主色做所有工作 */
  --bg: oklch(1.000 0.000 0);

  /* 表面 — 微绿色调，用于卡片、面板 */
  --surface: oklch(0.975 0.012 155);

  /* 正文色 — 近黑，带微冷，对比度 ≥ 7:1 */
  --ink: oklch(0.18 0.01 160);

  /* 主色 — 柔和绿，品牌锚点 */
  --primary: oklch(0.72 0.14 152);

  /* 强调色 — 暖珊瑚，与绿色形成对比 */
  --accent: oklch(0.65 0.10 45);

  /* 次要文字 — 墨色向背景混合 40% */
  --muted: oklch(0.55 0.02 155);

  /* 边框 */
  --border: oklch(0.92 0.01 155);

  /* 成功/错误/警告 */
  --success: oklch(0.68 0.16 145);
  --error: oklch(0.60 0.18 25);
  --warning: oklch(0.78 0.12 85);
}
```

## Typography

衬线 + 无衬线搭配，建立层次感：

- **标题/品牌名**：衬线字体（如 Playfair Display、Source Serif 4、Noto Serif SC）
- **正文/UI**：无衬线字体（如 Inter、DM Sans、Noto Sans SC）
- **代码/数据**：等宽字体（如 JetBrains Mono、Fira Code）

字体规范：
- 正文行高：1.6
- 标题行高：1.2
- 正文字号：16px
- 行宽上限：65-75ch
- 使用 `text-wrap: balance` 在 h1-h3 上

## Spacing

基于 4px 网格的间距系统：

```css
:root {
  --space-xs: 4px;
  --space-sm: 8px;
  --space-md: 16px;
  --space-lg: 24px;
  --space-xl: 32px;
  --space-2xl: 48px;
  --space-3xl: 64px;
}
```

## Border Radius

克制的圆角，避免过度圆润：

```css
:root {
  --radius-sm: 4px;
  --radius-md: 8px;
  --radius-lg: 12px;
  --radius-full: 9999px;
}
```

## Shadows

微妙的层次感，不使用明显阴影：

```css
:root {
  --shadow-sm: 0 1px 2px oklch(0 0 0 / 0.05);
  --shadow-md: 0 4px 12px oklch(0 0 0 / 0.08);
  --shadow-lg: 0 8px 24px oklch(0 0 0 / 0.12);
}
```

## Motion

- 缓动函数：ease-out-quart / quint，无弹性/弹跳
- 过渡时长：150-300ms
- 支持 `prefers-reduced-motion: reduce`

## Components

### 导航栏
- 固定顶部，毛玻璃效果（backdrop-filter）
- 品牌名使用衬线字体
- 搜索框居中或左侧

### 网站卡片
- 无边框或极细边框
- 悬停时微抬升（shadow 变化）
- 缩略图 + 标题 + 一句话描述
- 不使用卡片套卡片

### 侧边栏
- 可折叠
- 分类图标使用线性风格
- 当前分类高亮使用绿色

### 按钮
- 主要按钮：绿色背景 + 白色文字
- 次要按钮：边框样式
- 无圆角方形或微圆角（4-8px）

## Anti-Patterns (Avoid)

- ❌ 紫色渐变、AI 默认紫色
- ❌ Inter 字体独挑大梁
- ❌ 卡片套卡片
- ❌ 灰色文字配彩色背景
- ❌ 圆角方块图标瓦片
- ❌ 毛玻璃滥用
- ❌ 渐变文字（background-clip: text）
- ❌ 弹性/弹跳动画
- ❌ 数字编号章节（01/02/03）
- ❌ 过于饱和的绿色（霓虹绿、荧光绿）
- ❌ 绿色+棕色自然主题（过于常见）
