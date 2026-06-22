<template>
  <n-layout has-sider class="admin-layout">
    <n-layout-sider
      class="admin-sider"
      collapse-mode="width"
      :collapsed-width="64"
      :width="240"
      :collapsed="collapsed"
      show-trigger
      @collapse="collapsed = true"
      @expand="collapsed = false"
    >
      <div class="sider-logo" :class="{ 'is-collapsed': collapsed }">
        <div class="logo-icon">W</div>
        <Transition name="logo-text">
          <span v-if="!collapsed" class="logo-text">WebStack</span>
        </Transition>
      </div>
      <n-menu
        :options="menuOptions"
        :value="activeKey"
        :collapsed="collapsed"
        :collapsed-width="64"
        :collapsed-icon-size="20"
        @update:value="handleMenuSelect"
      />
    </n-layout-sider>

    <n-layout>
      <n-layout-header class="admin-header">
        <span class="header-title">{{ pageTitle }}</span>
        <n-space align="center" :size="12">
          <n-button quaternary circle class="theme-toggle" @click="themeStore.toggle" title="切换主题">
            <template #icon>
              <n-icon size="18">
                <Sun v-if="!themeStore.isDark" :stroke-width="1.5" />
                <Moon v-else :stroke-width="1.5" />
              </n-icon>
            </template>
          </n-button>
          <n-dropdown :options="dropdownOptions" @select="handleDropdownSelect">
            <n-space align="center" style="cursor: pointer;" :size="8">
              <n-avatar round :size="28" :src="getImageUrl(userStore.userInfo?.avatar)" />
              <span class="header-user">{{ userStore.userInfo?.name || userStore.userInfo?.username || '用户' }}</span>
            </n-space>
          </n-dropdown>
        </n-space>
      </n-layout-header>

      <n-layout-content class="admin-content">
        <router-view v-slot="{ Component }">
          <Transition name="fade-slide" mode="out-in">
            <component :is="Component" />
          </Transition>
        </router-view>
      </n-layout-content>
    </n-layout>
  </n-layout>
</template>

<script setup>
import { h, ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../../store/user'
import { useThemeStore } from '../../store/theme'
import { getImageUrl } from '../../utils/image'
import {
  NLayout,
  NLayoutSider,
  NLayoutHeader,
  NLayoutContent,
  NMenu,
  NDropdown,
  NSpace,
  NAvatar,
  NButton,
  NIcon
} from 'naive-ui'
import { 
  LayoutDashboard, 
  FolderTree, 
  Globe, 
  Tags, 
  FileDown, 
  FileText, 
  Users, 
  MousePointerClick, 
  Heart, 
  MessageSquare, 
  Bell,
  Sun,
  Moon
} from 'lucide-vue-next'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const themeStore = useThemeStore()
const collapsed = ref(false)

onMounted(() => {
  if (!userStore.userInfo) {
    userStore.fetchUserInfo()
  }
})

const renderIcon = (icon) => {
  return () => h(NIcon, null, { default: () => h(icon, { strokeWidth: 1.5 }) })
}

const menuItems = [
  { label: '数据概览', key: 'dashboard', icon: LayoutDashboard },
  { label: '分类管理', key: 'category', icon: FolderTree },
  { label: '网站管理', key: 'site', icon: Globe },
  { label: '标签管理', key: 'tag', icon: Tags },
  { label: '批量导入', key: 'site-import', icon: FileDown },
  { label: '小节管理', key: 'site-section', icon: FileText },
  { label: '用户管理', key: 'user', icon: Users },
  { label: '点击记录', key: 'click-log', icon: MousePointerClick },
  { label: '点赞记录', key: 'like-log', icon: Heart },
  { label: '评论管理', key: 'comment', icon: MessageSquare },
  { label: '消息通知', key: 'notice', icon: Bell }
]

const pageTitleMap = {
  dashboard: '数据概览',
  category: '分类管理',
  site: '网站管理',
  tag: '标签管理',
  'site-import': '批量导入',
  'site-section': '小节管理',
  user: '用户管理',
  'click-log': '点击记录',
  'like-log': '点赞记录',
  comment: '评论管理',
  notice: '消息通知'
}

const activeKey = computed(() => {
  const path = route.path.replace(/^\//, '').split('/')[0]
  return path || 'dashboard'
})

const pageTitle = computed(() => pageTitleMap[activeKey.value] || '管理后台')

const menuOptions = menuItems.map(item => ({
  label: item.label,
  key: item.key,
  icon: renderIcon(item.icon)
}))

const handleMenuSelect = (key) => {
  router.push('/' + key)
}

const dropdownOptions = [
  { label: '退出登录', key: 'logout' }
]

const handleDropdownSelect = (key) => {
  if (key === 'logout') {
    userStore.logout()
    router.push('/login')
  }
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
}

/* ---- Sidebar ---- */
.admin-sider {
  background: var(--bg-sidebar) !important;
  border-right: 1px solid var(--border-color) !important;
  transition: width var(--transition-fast) ease, background var(--transition-fast) ease;
}

.admin-sider :deep(.n-layout-sider-scroll-container) {
  background: transparent;
}

.admin-sider :deep(.n-layout-toggle-button) {
  transition: background var(--transition-fast) ease;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
}

/* Menu selected indicator: NO gradient lines, just pure subtle background and text highlight */
.admin-sider :deep(.n-menu-item-content--selected) {
  background-color: rgba(249, 115, 22, 0.08) !important;
}

.admin-sider :deep(.n-menu-item-content--selected::before) {
  display: none; /* Kill the ugly gradient border */
}

.admin-sider :deep(.n-menu-item-content) {
  position: relative;
  border-radius: 6px;
  margin: 2px 8px;
  transition: background var(--transition-fast) ease, color var(--transition-fast) ease;
}

/* ---- Logo ---- */
.sider-logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 0 16px;
  border-bottom: 1px solid var(--border-color);
  overflow: hidden;
  transition: padding var(--transition-fast) ease;
}

.sider-logo.is-collapsed {
  padding: 0;
  justify-content: center;
}

.logo-icon {
  width: 24px;
  height: 24px;
  background: var(--text-primary);
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--bg-page);
  font-size: 14px;
  font-weight: bold;
}

.logo-text {
  font-size: 18px;
  font-weight: 700;
  white-space: nowrap;
  color: var(--text-primary);
}

.logo-text-enter-active { transition: opacity 0.1s ease; }
.logo-text-leave-active { transition: opacity 0.1s ease; }
.logo-text-enter-from { opacity: 0; }
.logo-text-leave-to { opacity: 0; }

/* ---- Header ---- */
.admin-header {
  height: 64px;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid var(--border-color);
  background: var(--bg-header);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  transition: background var(--transition-fast) ease;
}

.header-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

.header-user {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
}

/* ---- Content ---- */
.admin-content {
  padding: 24px;
  background: var(--bg-page);
  transition: background var(--transition-fast) ease;
}

.admin-content :deep(.n-layout-scroll-container) {
  padding: 0;
}
</style>
