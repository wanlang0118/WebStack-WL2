<template>
  <div class="sidebar-root">
    <Teleport to="body">
      <div
        v-if="mobileOpen"
        class="sidebar-backdrop"
        @click="$emit('close-mobile')"
        aria-hidden="true"
      />
    </Teleport>

    <aside
    class="sidebar"
    :class="{
      'sidebar--collapsed': collapsed && !mobileOpen,
      'sidebar--mobile-open': mobileOpen
    }"
  >
    <!-- 头部：Logo + 折叠按钮 -->
    <div class="sidebar-header">
      <router-link to="/" class="sidebar-logo">
        <span class="logo-mark">W</span>
        <span class="logo-text" v-show="!collapsed || mobileOpen">WebStack</span>
      </router-link>
      <button
        v-if="mobileOpen"
        class="sidebar-toggle sidebar-toggle--close"
        @click="$emit('close-mobile')"
        aria-label="关闭导航"
      >
        <i class="ri-close-line"></i>
      </button>
      <button
        v-else
        class="sidebar-toggle"
        @click="$emit('toggle')"
        :aria-label="collapsed ? '展开侧边栏' : '收起侧边栏'"
      >
        <i :class="collapsed ? 'ri-arrow-right-s-line' : 'ri-arrow-left-s-line'"></i>
      </button>
    </div>

    <!-- 导航列表 -->
    <nav class="sidebar-nav" aria-label="分类导航">
      <ul class="nav-list" role="list">
        <li
          v-for="cat in categories"
          :key="cat.id"
          class="nav-item"
          :class="{
            'nav-item--active': isActive(cat),
            'nav-item--open': openIds.has(cat.id),
            'nav-item--has-children': cat.children?.length
          }"
        >
          <a
            class="nav-link"
            role="button"
            tabindex="0"
            @click.prevent="handleClick(cat)"
            @keydown.enter.prevent="handleClick(cat)"
            :title="collapsed ? cat.title : undefined"
          >
            <!-- 选中指示条 -->
            <span class="nav-indicator" v-if="isActive(cat)"></span>

            <CategoryIcon
              :icon="cat.icon"
              :title="cat.title"
              size="md"
              class="nav-icon"
            />
            <span class="nav-label" v-show="!collapsed || mobileOpen">{{ cat.title }}</span>
            <i
              v-if="cat.children?.length && (!collapsed || mobileOpen)"
              class="nav-arrow ri-arrow-down-s-line"
              :class="{ 'nav-arrow--open': openIds.has(cat.id) }"
              @click.stop="toggleOpen(cat.id)"
            ></i>
          </a>

          <!-- 子分类列表，带高度过渡 -->
          <Transition name="subnav">
            <ul
              v-if="cat.children?.length && openIds.has(cat.id) && (!collapsed || mobileOpen)"
              class="nav-sublist"
              role="list"
            >
              <li
                v-for="child in cat.children"
                :key="child.id"
                class="nav-subitem"
                :class="{ 'nav-subitem--active': activeId === child.id }"
              >
                <a
                  class="nav-sublink"
                  role="button"
                  tabindex="0"
                  @click.prevent="handleClick(child)"
                  @keydown.enter.prevent="handleClick(child)"
                >
                  <span class="nav-sub-dot"></span>
                  {{ child.title }}
                </a>
              </li>
            </ul>
          </Transition>
        </li>
      </ul>
    </nav>
    </aside>
  </div>
</template>

<script setup>
import { ref, defineProps, defineEmits } from 'vue'
import CategoryIcon from '@/components/CategoryIcon.vue'

const props = defineProps({
  categories: { type: Array, default: () => [] },
  activeId: { type: Number, default: null },
  collapsed: { type: Boolean, default: false },
  mobileOpen: { type: Boolean, default: false }
})

const emit = defineEmits(['toggle', 'select', 'close-mobile'])

const openIds = ref(new Set())

/** 判断当前分类是否选中（包含子分类选中时父级也算激活） */
function isActive(cat) {
  if (props.activeId === cat.id) return true
  if (cat.children?.length) {
    return cat.children.some(child => child.id === props.activeId)
  }
  return false
}

/** 展开/收起子分类 */
function toggleOpen(id) {
  if (openIds.value.has(id)) {
    openIds.value.delete(id)
  } else {
    openIds.value.add(id)
  }
}

/** 点击分类项 */
function handleClick(cat) {
  if (cat.children?.length) {
    toggleOpen(cat.id)
  }
  emit('select', cat)
}
</script>

<style scoped>
.sidebar-root {
  display: contents;
}

/* =============================================
   侧边栏容器
   ============================================= */
.sidebar {
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  width: var(--sidebar-width);
  background: var(--color-bg-sidebar);
  border-right: 1px solid var(--color-border-light);
  z-index: 100;
  display: flex;
  flex-direction: column;
  transition: width 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
}

.sidebar--collapsed {
  width: 60px;
}

/* 自定义滚动条 */
.sidebar-nav {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 8px 0 16px;
  scrollbar-width: thin;
  scrollbar-color: var(--color-border) transparent;
}

.sidebar-nav::-webkit-scrollbar {
  width: 4px;
}

.sidebar-nav::-webkit-scrollbar-track {
  background: transparent;
}

.sidebar-nav::-webkit-scrollbar-thumb {
  background: var(--color-border);
  border-radius: 4px;
}

.sidebar-nav::-webkit-scrollbar-thumb:hover {
  background: var(--color-text-light);
}

/* =============================================
   头部：Logo + 折叠按钮
   ============================================= */
.sidebar-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 12px;
  border-bottom: 1px solid var(--color-border-light);
  flex-shrink: 0;
  gap: 8px;
}

.sidebar-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  text-decoration: none;
  min-width: 0;
}

/* Logo 标记 — 品牌绿色圆角方块 + 白色字母 */
.logo-mark {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 38px;
  height: 38px;
  border-radius: var(--radius-md);
  background: var(--color-primary);
  color: #fff;
  font-family: var(--font-serif);
  font-weight: 700;
  font-size: 18px;
  flex-shrink: 0;
  letter-spacing: -0.02em;
}

.logo-text {
  font-family: var(--font-serif);
  font-weight: 700;
  font-size: 20px;
  color: var(--color-text);
  white-space: nowrap;
  letter-spacing: -0.01em;
}

/* 折叠按钮 */
.sidebar-toggle {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border-radius: var(--radius-sm);
  background: none;
  border: none;
  color: var(--color-text-light);
  font-size: 18px;
  cursor: pointer;
  transition: color 0.15s, background 0.15s;
  flex-shrink: 0;
  padding: 0;
}

.sidebar-toggle:hover {
  color: var(--color-primary);
  background: var(--color-tag-bg);
}

/* =============================================
   导航列表
   ============================================= */
.nav-list {
  display: flex;
  flex-direction: column;
  gap: 2px;
  padding: 0 8px;
  list-style: none;
  margin: 0;
}

/* =============================================
   导航项
   ============================================= */
.nav-link {
  position: relative;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 11px 16px;
  cursor: pointer;
  color: var(--color-text-secondary);
  font-size: 1rem;
  font-weight: 500;
  line-height: 1.4;
  transition: color 0.15s ease-out, background 0.15s ease-out;
  border-radius: var(--radius-md);
  text-decoration: none;
  user-select: none;
}

/* 选中指示条 — 左侧 2px 品牌色竖线 */
.nav-indicator {
  position: absolute;
  left: 0;
  top: 8px;
  bottom: 8px;
  width: 2px;
  border-radius: 1px;
  background: var(--color-primary);
}

/* Hover 态 */
.nav-link:hover {
  color: var(--color-text);
  background: var(--color-tag-bg);
}

/* 选中态 */
.nav-item--active > .nav-link {
  color: var(--color-primary);
  background: var(--color-tag-bg);
  font-weight: 600;
}

/* Focus 态 — 键盘导航 */
.nav-link:focus-visible {
  outline: 2px solid var(--color-primary);
  outline-offset: -2px;
}

/* 图标 */
.nav-icon {
  flex-shrink: 0;
  width: 20px;
  text-align: center;
  font-size: 16px;
  color: inherit;
  opacity: 0.7;
  transition: opacity 0.15s;
}

.nav-link:hover .nav-icon,
.nav-item--active > .nav-link .nav-icon {
  opacity: 1;
}

/* 标签文字 */
.nav-label {
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  min-width: 0;
}

/* 展开/收起箭头 */
.nav-arrow {
  flex-shrink: 0;
  font-size: 16px;
  color: var(--color-text-light);
  transition: transform 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  line-height: 1;
  padding: 2px;
  border-radius: var(--radius-sm);
}

.nav-arrow:hover {
  color: var(--color-text-secondary);
  background: rgba(0, 0, 0, 0.04);
}

.nav-arrow--open {
  transform: rotate(180deg);
}

/* =============================================
   子分类列表
   ============================================= */
.nav-sublist {
  list-style: none;
  margin: 4px 0 6px;
  padding: 0 0 0 28px;
  overflow: hidden;
}

/* 子分类过渡动画 */
.subnav-enter-active,
.subnav-leave-active {
  transition: opacity 0.2s ease-out, max-height 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  max-height: 500px;
}

.subnav-enter-from,
.subnav-leave-to {
  opacity: 0;
  max-height: 0;
}

.nav-sublink {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  cursor: pointer;
  font-size: 0.9375rem;
  color: var(--color-text-secondary);
  border-radius: var(--radius-sm);
  transition: color 0.15s, background 0.15s;
  text-decoration: none;
  user-select: none;
}

/* 子分类前面的小圆点指示器 */
.nav-sub-dot {
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background: var(--color-border);
  flex-shrink: 0;
  transition: background 0.15s;
}

.nav-sublink:hover {
  color: var(--color-text);
  background: var(--color-tag-bg);
}

.nav-sublink:hover .nav-sub-dot {
  background: var(--color-primary);
}

.nav-sublink:focus-visible {
  outline: 2px solid var(--color-primary);
  outline-offset: -2px;
}

.nav-subitem--active > .nav-sublink {
  color: var(--color-primary);
  font-weight: 550;
}

.nav-subitem--active > .nav-sublink .nav-sub-dot {
  background: var(--color-primary);
}

/* =============================================
   折叠态特殊处理
   ============================================= */
.sidebar--collapsed .nav-list {
  padding: 0 4px;
}

.sidebar--collapsed .nav-link {
  justify-content: center;
  padding: 10px 0;
}

.sidebar--collapsed .nav-indicator {
  display: none;
}

.sidebar--collapsed .nav-item--active > .nav-link {
  background: var(--color-tag-bg);
}

.sidebar--collapsed .sidebar-header {
  padding: 16px 8px;
  justify-content: center;
  flex-direction: column;
  gap: 8px;
}

.sidebar--collapsed .sidebar-logo {
  justify-content: center;
}

.sidebar--collapsed .sidebar-toggle {
  margin: 0 auto;
}

/* =============================================
   移动端：抽屉式导航
   ============================================= */
.sidebar-backdrop {
  position: fixed;
  inset: 0;
  background: oklch(0 0 0 / 0.45);
  z-index: 99;
}

@media (max-width: 768px) {
  .sidebar {
    width: min(280px, 85vw);
    transform: translateX(-100%);
    transition: transform 0.25s cubic-bezier(0.4, 0, 0.2, 1);
    box-shadow: none;
  }

  .sidebar--mobile-open {
    transform: translateX(0);
    box-shadow: 4px 0 24px oklch(0 0 0 / 0.12);
  }

  .sidebar--collapsed {
    width: min(280px, 85vw);
  }

  .sidebar--mobile-open .nav-label,
  .sidebar--mobile-open .logo-text,
  .sidebar--mobile-open .nav-arrow {
    display: inline;
  }

  .sidebar--mobile-open .nav-link {
    justify-content: flex-start;
    padding: 11px 16px;
  }

  .sidebar--mobile-open .sidebar-header {
    flex-direction: row;
    justify-content: space-between;
    padding: 16px 12px;
  }

  .sidebar--mobile-open .sidebar-logo {
    justify-content: flex-start;
  }
}

/* =============================================
   Reduced motion
   ============================================= */
@media (prefers-reduced-motion: reduce) {
  .sidebar {
    transition: none;
  }

  .nav-link,
  .nav-sublink,
  .nav-arrow,
  .nav-icon,
  .nav-sub-dot,
  .sidebar-toggle {
    transition: none;
  }

  .subnav-enter-active,
  .subnav-leave-active {
    transition: none;
  }
}
</style>
