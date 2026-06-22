<template>
  <div class="search-box">
    <!-- 滑块式分段控制器 Segmented Control -->
    <div class="search-tabs-container">
      <div class="search-tabs-slider" :style="sliderStyle"></div>
      <button 
        v-for="(tab, index) in tabs" 
        :key="tab.key"
        ref="tabButtons"
        class="search-tab" 
        :class="{ active: activeTab === tab.key }"
        @click="selectTab(tab.key, index)"
      >
        {{ tab.label }}
      </button>
    </div>

    <!-- 搜索输入框包裹容器 -->
    <div class="search-input-wrap">
      <span class="search-icon-prefix">
        <i class="ri-search-line"></i>
      </span>
      <input 
        ref="searchInputRef"
        class="search-input" 
        type="text" 
        v-model="keyword"
        :placeholder="currentPlaceholder"
        @keydown.enter="doSearch"
        @focus="isFocused = true"
        @blur="isFocused = false"
      />
      
      <!-- 键盘快捷键提示 Badge -->
      <span class="shortcut-hint" v-show="!isFocused && !keyword">
        <kbd>/</kbd>
      </span>
      
      <!-- 清除搜索内容按钮 -->
      <button 
        class="clear-btn" 
        v-show="keyword" 
        @click="keyword = ''" 
        title="清空输入"
      >
        <i class="ri-close-circle-fill"></i>
      </button>

      <button class="search-btn" @click="doSearch" aria-label="执行搜索">
        <i class="ri-arrow-right-line"></i>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, defineEmits, onMounted, onUnmounted, nextTick } from 'vue'

const emit = defineEmits(['search'])

const tabs = [
  { key: 'site', label: '站内', url: '' },
  { key: 'baidu', label: '百度', url: 'https://www.baidu.com/s?wd=' },
  { key: 'google', label: 'Google', url: 'https://www.google.com/search?q=' },
  { key: 'bing', label: 'Bing', url: 'https://www.bing.com/search?q=' },
  { key: 'github', label: 'GitHub', url: 'https://github.com/search?q=' }
]

const activeTab = ref('site')
const activeIndex = ref(0)
const keyword = ref('')
const isFocused = ref(false)
const searchInputRef = ref(null)
const tabButtons = ref([])

// 滑块位置样式
const sliderWidth = ref(0)
const sliderLeft = ref(0)

const sliderStyle = computed(() => ({
  width: `${sliderWidth.value}px`,
  transform: `translateX(${sliderLeft.value}px)`
}))

const currentPlaceholder = computed(() => {
  const t = tabs.find(t => t.key === activeTab.value)
  return t?.key === 'site' ? '搜索站内网站...' : `在 ${t?.label} 中搜索...`
})

function updateSlider() {
  const el = tabButtons.value[activeIndex.value]
  if (el) {
    sliderWidth.value = el.offsetWidth
    sliderLeft.value = el.offsetLeft
  }
}

function selectTab(key, index) {
  activeTab.value = key
  activeIndex.value = index
  updateSlider()
  // 切换分类时自动 focus 输入框，方便用户继续输入
  searchInputRef.value?.focus()
}

function doSearch() {
  const q = keyword.value.trim()
  if (!q) return
  const tab = tabs.find(t => t.key === activeTab.value)
  if (tab.key === 'site') {
    emit('search', q)
  } else {
    window.open(tab.url + encodeURIComponent(q), '_blank')
  }
}

// 快捷键支持：按 '/' 聚焦搜索框，按 'Esc' 取消聚焦
function handleGlobalKeyDown(e) {
  if (e.key === '/' && document.activeElement !== searchInputRef.value) {
    // 阻止默认行为（比如在浏览器中触发默认搜索）
    e.preventDefault()
    searchInputRef.value?.focus()
  } else if (e.key === 'Escape' && document.activeElement === searchInputRef.value) {
    searchInputRef.value?.blur()
  }
}

onMounted(() => {
  // 等待渲染后初始化滑块位置
  nextTick(() => {
    updateSlider()
  })
  window.addEventListener('keydown', handleGlobalKeyDown)
  // 窗口 resize 时更新滑块位置
  window.addEventListener('resize', updateSlider)
})

onUnmounted(() => {
  window.removeEventListener('keydown', handleGlobalKeyDown)
  window.removeEventListener('resize', updateSlider)
})
</script>

<style scoped>
.search-box {
  max-width: 920px; /* 超长超大气 */
  margin: 0 auto;
}

/* =============================================
   滑块分段控制器 (Segmented Control)
   ============================================= */
.search-tabs-container {
  position: relative;
  display: inline-flex;
  background: rgba(0, 0, 0, 0.03);
  padding: 4px;
  border-radius: var(--radius-full, 9999px);
  margin-bottom: 24px;
  border: 1px solid var(--color-border-light);
}

.search-tabs-slider {
  position: absolute;
  top: 4px;
  bottom: 4px;
  left: 0;
  background: #fff;
  border-radius: var(--radius-full, 9999px);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08), 0 1px 2px rgba(0, 0, 0, 0.04);
  transition: transform 0.25s cubic-bezier(0.4, 0, 0.2, 1), width 0.2s ease-out;
  z-index: 1;
}

.search-tab {
  position: relative;
  z-index: 2;
  padding: 8px 22px;
  background: transparent;
  border: none;
  color: var(--color-text-secondary);
  font-size: 0.875rem;
  font-weight: 500;
  border-radius: var(--radius-full, 9999px);
  cursor: pointer;
  transition: color 0.2s;
  user-select: none;
}

.search-tab:hover {
  color: var(--color-text);
}

.search-tab.active {
  color: var(--color-primary);
  font-weight: 600;
}

/* =============================================
   搜索输入框
   ============================================= */
.search-input-wrap {
  position: relative;
  display: flex;
  align-items: center;
  background: #fff;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm, 0 1px 2px rgba(0,0,0,0.05));
  border: 1px solid var(--color-border);
  transition: border-color 0.2s, box-shadow 0.2s;
  padding: 4px;
}

.search-input-wrap:focus-within {
  border-color: var(--color-primary);
  box-shadow: 0 8px 30px oklch(0 0 0 / 0.02), 0 0 0 3px var(--color-tag-bg);
}

.search-icon-prefix {
  display: flex;
  align-items: center;
  justify-content: center;
  padding-left: 16px;
  color: var(--color-text-light);
  font-size: 20px;
}

.search-input {
  flex: 1;
  padding: 14px 12px;
  border: none;
  font-size: 1.0625rem;
  font-weight: 450;
  background: transparent;
  color: var(--color-text);
  font-family: var(--font-sans);
  outline: none;
}

.search-input::placeholder {
  color: var(--color-text-light);
}

/* 快捷键提示 */
.shortcut-hint {
  position: absolute;
  right: 68px;
  display: flex;
  align-items: center;
}

.shortcut-hint kbd {
  font-family: inherit;
  font-size: 10px;
  font-weight: 600;
  color: var(--color-text-light);
  background: rgba(0, 0, 0, 0.04);
  border: 1px solid var(--color-border);
  padding: 2px 6px;
  border-radius: 4px;
  box-shadow: 0 1px 0 rgba(0, 0, 0, 0.05);
}

/* 清空按钮 */
.clear-btn {
  background: none;
  border: none;
  color: var(--color-text-light);
  font-size: 18px;
  cursor: pointer;
  padding: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: color 0.15s;
}

.clear-btn:hover {
  color: var(--color-text-secondary);
}

/* 搜索动作按钮 */
.search-btn {
  width: 42px;
  height: 42px;
  background: var(--color-primary);
  color: #fff;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: background 0.2s;
  font-size: 18px;
  flex-shrink: 0;
}

.search-btn:hover {
  background: var(--color-primary-hover);
}

/* Reduced Motion */
@media (prefers-reduced-motion: reduce) {
  .search-tabs-slider {
    transition: none;
  }
  .search-input-wrap {
    transition: none;
  }
}
</style>
