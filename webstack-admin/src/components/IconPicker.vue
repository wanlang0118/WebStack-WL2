<template>
  <div class="icon-picker">
    <!-- 搜索栏 -->
    <n-input
      v-model:value="searchQuery"
      placeholder="搜索图标 (如: 编程, 视频, chat, list)"
      clearable
      class="search-input"
    >
      <template #prefix>
        <n-icon :component="Search" />
      </template>
    </n-input>

    <!-- 分类切换标签页 -->
    <n-tabs v-model:value="activeTab" type="segment" size="small" class="category-tabs">
      <n-tab-pane name="all" tab="全部" />
      <n-tab-pane name="common" tab="常用" />
      <n-tab-pane name="media" tab="音视频" />
      <n-tab-pane name="tech" tab="开发技术" />
      <n-tab-pane name="office" tab="办公效率" />
      <n-tab-pane name="life" tab="生活社交" />
    </n-tabs>

    <!-- 图标可视化网格滚动器 -->
    <div class="icon-grid" ref="gridRef">
      <div
        v-for="item in filteredIcons"
        :key="item.class"
        class="icon-item"
        :class="{ 'icon-item--active': value === item.class }"
        :data-icon-class="item.class"
        :title="item.label"
        @click="selectIcon(item.class)"
      >
        <i :class="[item.class]" class="icon-preview"></i>
        <span class="icon-label">{{ item.label }}</span>
      </div>
      <div v-if="filteredIcons.length === 0" class="empty-state">
        无匹配图标
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, nextTick } from 'vue'
import { NInput, NIcon, NTabs, NTabPane } from 'naive-ui'
import { Search } from 'lucide-vue-next'
import { CATEGORY_ICONS } from '@shared/categoryIcons.js'

const props = defineProps({
  value: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:value'])

const searchQuery = ref('')
const activeTab = ref('all')
const gridRef = ref(null)

const filteredIcons = computed(() => {
  const query = searchQuery.value.trim().toLowerCase()
  return CATEGORY_ICONS.filter(item => {
    if (activeTab.value !== 'all' && item.category !== activeTab.value) {
      return false
    }
    if (query) {
      const matchLabel = item.label.toLowerCase().includes(query)
      const matchClass = item.class.toLowerCase().replace('ri-', '').includes(query)
      const matchTags = item.tags.some(tag => tag.toLowerCase().includes(query))
      return matchLabel || matchClass || matchTags
    }
    return true
  })
})

function selectIcon(iconClass) {
  emit('update:value', iconClass)
}

// 平滑滚动高亮项目至可视高度区域
function scrollToActiveIcon() {
  if (!props.value || !gridRef.value) return
  nextTick(() => {
    const activeEl = gridRef.value.querySelector(`[data-icon-class="${props.value}"]`)
    if (activeEl) {
      activeEl.scrollIntoView({ block: 'nearest', behavior: 'smooth' })
    }
  })
}

watch(() => props.value, () => {
  scrollToActiveIcon()
}, { immediate: true })

watch(activeTab, () => {
  if (gridRef.value) {
    gridRef.value.scrollTop = 0
  }
})
</script>

<style scoped>
.icon-picker {
  display: flex;
  flex-direction: column;
  gap: 8px;
  width: 100%;
}
.search-input {
  width: 100%;
}
.category-tabs {
  margin-bottom: 4px;
}
.icon-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 8px;
  max-height: 98px;
  overflow-y: auto;
  padding: 6px;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-sm);
  background: var(--bg-page);
  transition: border-color var(--transition-base);
}
.icon-grid:focus-within {
  border-color: var(--color-primary);
}
.icon-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 10px 4px;
  border-radius: var(--radius-sm);
  cursor: pointer;
  background: var(--bg-card);
  border: 1px solid transparent;
  transition: all var(--transition-fast);
  user-select: none;
}
.icon-item:hover {
  background: rgba(249, 115, 22, 0.08);
  border-color: var(--color-primary-hover);
  transform: translateY(-1px);
}
.icon-item:active {
  transform: translateY(0);
}
.icon-item--active {
  background: rgba(249, 115, 22, 0.12) !important;
  border-color: var(--color-primary) !important;
  color: var(--color-primary) !important;
}
.icon-preview {
  font-size: 24px;
  margin-bottom: 6px;
  color: var(--text-primary);
  transition: color var(--transition-fast);
}
.icon-item--active .icon-preview {
  color: var(--color-primary);
}
.icon-label {
  font-size: 11px;
  color: var(--text-secondary);
  text-align: center;
  width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  transition: color var(--transition-fast);
}
.icon-item--active .icon-label {
  color: var(--color-primary);
  font-weight: 600;
}
.empty-state {
  grid-column: span 6;
  text-align: center;
  padding: 32px;
  color: var(--text-placeholder);
  font-size: var(--fs-sm);
}

/* 适配全局滚动条样式 */
.icon-grid::-webkit-scrollbar {
  width: 6px;
}
.icon-grid::-webkit-scrollbar-track {
  background: transparent;
}
.icon-grid::-webkit-scrollbar-thumb {
  background: var(--scrollbar-thumb);
  border-radius: 3px;
}
.icon-grid::-webkit-scrollbar-thumb:hover {
  background: var(--scrollbar-thumb-hover);
}
</style>
