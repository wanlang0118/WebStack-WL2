# Admin Design System (Taste & UI-UX Pro Max)

## Register
`product` (SaaS Admin Dashboard)

## Mood
高效、克制、聚焦数据。类似于 Vercel 或 Stripe 的现代控制台体验。去除所有不必要的装饰，让数据本身成为界面焦点。

## Color Strategy
**Restrained (克制)** — 使用带微小色温的灰度（Slate/Zinc）构建 90% 的界面，仅在主操作（Primary Actions）、状态指示（活跃/选定）和数据图表中使用品牌高亮色（橙色 #F97316）。

### Palette (基于现有的 naiveTheme 提取并提升)
- **品牌高亮 (Primary)**: `#F97316` (橘色) — 活力、醒目，仅用于最核心的 CTA 和侧边栏激活状态。
- **纯粹背景 (Light Mode)**: `#F7F7F8` 底色配合 `#FFFFFF` 的纯白卡片，通过极淡的边框区分层级，而非厚重的阴影。
- **沉浸暗黑 (Dark Mode)**: `#0E0E11` 极深背景，配合 `#17171C` 的卡片色。避免纯黑，加入极微弱的紫色/冷色相。
- **交互状态 (States)**: 
  - 成功: `#22C55E`
  - 警告: `#F59E0B`
  - 错误: `#EF4444`

## Typography
- **字体栈**: 系统默认 UI 字体优先（San Francisco, Inter, Segoe UI）。
- **层级**: 
  - 避免过大的 H1，Dashboard 的最高字号控制在 `24px (1.5rem)`。
  - 数据表格使用 `tabular-nums`，确保数字对齐。
  - 大量使用字重（Medium 500, Semibold 600）来做信息层级划分，而不是单纯放大字号。

## Spacing & Layout
- **密度（Density）**: 管理端需要一定的信息密度，行高控制在 `1.4` 左右。
- **圆角（Radius）**: 采用混合圆角策略。卡片和模态框使用较大圆角（`12px - 16px`），内部组件如按钮和输入框使用克制圆角（`6px`）。
- **无边框卡片**: 减少外围阴影（drop-shadow），使用 `1px` 极浅边框（`rgba(0,0,0,0.06)`）来界定内容区域。

## Components & Interaction Rules
1. **数据表格 (DataTable)**:
   - 去除强烈的斑马纹，使用悬停（Hover）整行高亮 `rgba(0,0,0,0.03)`。
   - 保证表头极度干净，使用微弱背景色 `rgba(0,0,0,0.02)` 区分。
2. **表单输入 (Inputs)**:
   - Focus 状态必须具有清晰的高亮环（Focus Ring），不能只变边框颜色，确保键盘导航的无障碍体验（A11y）。
3. **按钮体系 (Buttons)**:
   - 页面内最多只有一个实体背景的主按钮（Primary Button）。其他操作一律降级为次级（Outline/Ghost）或图标按钮。
4. **弹窗 (Modals)**:
   - 加入适当的背景模糊（Backdrop Blur），内部 padding 需要充足（至少 `24px`），避免拥挤。

## Anti-Patterns (坚决避免)
- ❌ 滥用紫色渐变色和卡片大阴影（显得廉价）。
- ❌ 过度使用 `margin` 撑开布局，使用 Flex/Grid gap 替代。
- ❌ 任何缺乏悬停状态（Hover）或焦点状态（Focus）的可交互元素。
- ❌ 把警告色（红色/黄色）大面积涂满卡片，应当只用于文字或 Icon。
