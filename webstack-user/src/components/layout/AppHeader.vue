<template>
  <header class="header" :class="[{ 'header--scrolled': isScrolled }, collapsed ? 'header--collapsed' : '']">
    <div class="header-inner">
      <div class="header-left">
        <button
          class="mobile-menu-btn"
          @click="$emit('open-nav')"
          aria-label="打开分类导航"
        >
          <i class="ri-menu-line"></i>
        </button>
        <!-- 仅当滚动后，或者在非首页（详情页）时，淡入展示小 Logo 文字，避免与 Hero Title 产生冗余冲突 -->
        <Transition name="fade">
          <router-link to="/" class="header-brand" v-show="isScrolled || $route.path !== '/'">
            <span class="brand-logo-mark">W</span>
            <span class="brand-text">WebStack</span>
          </router-link>
        </Transition>
      </div>
      <nav class="header-nav">
        <router-link to="/" class="header-nav-link" :class="{ active: $route.path === '/' }">
          首页
        </router-link>
      </nav>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const isScrolled = ref(false)

defineProps({
  collapsed: { type: Boolean, default: false }
})

defineEmits(['open-nav'])

function handleScroll() {
  isScrolled.value = window.scrollY > 30
}

onMounted(() => {
  // 绑定滚动监听
  window.addEventListener('scroll', handleScroll, { passive: true })
  // 首次加载初始化一次
  handleScroll()
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
.header {
  position: fixed;
  top: 0;
  left: var(--sidebar-width);
  right: 0;
  height: var(--header-height);
  background: transparent;
  border-bottom: 1px solid transparent;
  box-shadow: none;
  z-index: 90;
  transition: background-color 0.25s ease, border-color 0.25s ease, box-shadow 0.25s ease, left 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

.header--collapsed {
  left: 60px;
}

/* 滚动后的高阶毛玻璃状态 */
.header--scrolled {
  background: var(--color-bg-header);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--color-border-light);
  box-shadow: var(--shadow-header);
}

.header-inner {
  max-width: 100%;
  height: 100%;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
  height: 100%;
}

.mobile-menu-btn {
  display: none;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: var(--radius-md);
  background: none;
  border: none;
  color: var(--color-text-secondary);
  font-size: 22px;
  cursor: pointer;
  transition: color 0.15s, background 0.15s;
}

.mobile-menu-btn:hover {
  color: var(--color-primary);
  background: var(--color-tag-bg);
}

.header-brand {
  display: flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
}

.brand-logo-mark {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border-radius: var(--radius-sm);
  background: var(--color-primary);
  color: #fff;
  font-family: var(--font-serif);
  font-weight: 700;
  font-size: 12px;
  letter-spacing: -0.02em;
}

.brand-text {
  font-family: var(--font-serif);
  font-size: 16px;
  font-weight: 700;
  color: var(--color-text);
  letter-spacing: -0.02em;
}

.header-nav {
  display: flex;
  align-items: center;
  gap: 24px;
}

.header-nav-link {
  font-size: 0.8125rem;
  font-weight: 500;
  color: var(--color-text-secondary);
  padding: 6px 0;
  position: relative;
  transition: color 0.15s;
  text-decoration: none;
}

.header-nav-link:hover,
.header-nav-link.active {
  color: var(--color-primary);
}

.header-nav-link.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: var(--color-primary);
  border-radius: 1px;
}

/* 渐入渐出动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(-4px);
}

@media (max-width: 768px) {
  .header,
  .header--collapsed {
    left: 0;
  }

  .mobile-menu-btn {
    display: flex;
  }
}
</style>
