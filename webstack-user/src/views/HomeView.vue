<template>
  <div class="layout">
    <AppSidebar
      :categories="categories"
      :active-id="activeCategoryId"
      :collapsed="sidebarCollapsed"
      :mobile-open="mobileNavOpen"
      @toggle="sidebarCollapsed = !sidebarCollapsed"
      @select="handleCategorySelect"
      @close-mobile="closeMobileNav"
    />

    <div class="main-wrap" :class="{ 'main-wrap--collapsed': sidebarCollapsed && !isMobile }">
      <AppHeader :collapsed="sidebarCollapsed" @open-nav="openMobileNav" />

      <main class="main-content">
        <div class="hero">
          <div class="hero-inner">
            <h1 class="hero-title">WebStack-WL</h1>
            <p class="hero-desc">收录优质网站与工具，助你高效发现好资源</p>
            <SearchBox @search="handleSearch" />
            <div class="hero-shortcuts" v-if="!searchKeyword">
              <a v-for="link in quickLinks" :key="link.url" :href="link.url"
                 target="_blank" rel="noopener" class="shortcut-link">
                {{ link.label }}
              </a>
            </div>
          </div>
        </div>

        <div class="content-body">
          <div v-if="loading" class="loading-state">
            <div class="spinner"></div>
            <p>正在加载...</p>
          </div>

          <template v-else>
            <template v-if="searchKeyword">
              <div class="search-result-header">
                <span>搜索 "{{ searchKeyword }}" 的结果</span>
                <button class="clear-search" @click="searchKeyword = ''">清除搜索</button>
              </div>
              <div class="section-grid" v-if="filteredSites.length">
                <SiteCard v-for="site in filteredSites" :key="site.id" :site="site" />
              </div>
              <div class="section-empty" v-else>
                <div class="empty-icon"><i class="ri-search-line"></i></div>
                <p class="empty-title">未找到匹配的网站</p>
                <p class="empty-desc">尝试其他关键词，或浏览左侧分类</p>
              </div>
            </template>

            <template v-else>
              <CategorySection
                v-for="(cat, idx) in topCategories"
                :key="cat.id"
                :category="cat"
                :site-map="siteMap"
                :class="'section-reveal'"
                :style="{ '--reveal-delay': `${idx * 30}ms` }"
              />
            </template>
          </template>
        </div>
      </main>

      <AppFooter />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, computed } from 'vue'
import { useMobileLayout } from '@/composables/useMobileLayout'
import AppSidebar from '@/components/layout/AppSidebar.vue'
import AppHeader from '@/components/layout/AppHeader.vue'
import AppFooter from '@/components/layout/AppFooter.vue'
import SearchBox from '@/components/SearchBox.vue'
import CategorySection from '@/components/CategorySection.vue'
import SiteCard from '@/components/SiteCard.vue'
import { getCategoryTree } from '@/api/category'
import { getSitePage } from '@/api/site'

const categories = ref([])
const siteMap = ref({})
const loading = ref(true)
const sidebarCollapsed = ref(false)
const activeCategoryId = ref(null)
const searchKeyword = ref('')
const { isMobile, mobileNavOpen, openMobileNav, closeMobileNav } = useMobileLayout()

const quickLinks = [
  { label: '淘宝', url: 'https://www.taobao.com' },
  { label: '京东', url: 'https://www.jd.com' },
  { label: '知乎', url: 'https://www.zhihu.com' },
  { label: 'GitHub', url: 'https://github.com' },
  { label: '百度', url: 'https://www.baidu.com' }
]

const topCategories = computed(() => {
  return categories.value.filter(c => c.visible !== 0)
})

const filteredSites = computed(() => {
  if (!searchKeyword.value) return []
  const kw = searchKeyword.value.toLowerCase()
  const all = []
  for (const sites of Object.values(siteMap.value)) {
    for (const s of sites) {
      if (s.title?.toLowerCase().includes(kw) || s.description?.toLowerCase().includes(kw)) {
        all.push(s)
      }
    }
  }
  return all
})

function flattenCategoryIds(cats) {
  const ids = []
  for (const c of cats) {
    ids.push(c.id)
    if (c.children?.length) {
      ids.push(...flattenCategoryIds(c.children))
    }
  }
  return ids
}

let revealObserver = null

async function loadData() {
  loading.value = true
  try {
    const tree = await getCategoryTree()
    categories.value = tree || []

    const allCatIds = flattenCategoryIds(categories.value)

    const loadPromises = allCatIds.map(async catId => {
      try {
        const page = await getSitePage({ categoryId: catId, visible: 1, size: 100, page: 1 })
        return { catId, records: page?.records || [] }
      } catch {
        return { catId, records: [] }
      }
    })

    const results = await Promise.all(loadPromises)
    const map = {}
    for (const { catId, records } of results) {
      if (records.length) map[catId] = records
    }
    siteMap.value = map
  } catch (e) {
    console.error('加载失败', e)
  } finally {
    loading.value = false
    await nextTick()
    setupScrollReveal()
  }
}

function setupScrollReveal() {
  revealObserver?.disconnect()
  const els = document.querySelectorAll('.section-reveal')
  if (!els.length) return
  revealObserver = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('revealed')
        revealObserver.unobserve(entry.target)
      }
    })
  }, { threshold: 0.01, rootMargin: '0px 0px 200px 0px' })
  els.forEach(el => revealObserver.observe(el))
}

function scrollToCategory(cat) {
  activeCategoryId.value = cat.id
  const el = document.getElementById(`cat-${cat.id}`)
  if (el) {
    el.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }
}

function handleCategorySelect(cat) {
  scrollToCategory(cat)
  closeMobileNav()
}

function handleSearch(keyword) {
  searchKeyword.value = keyword
}

onMounted(loadData)
</script>

<style scoped>
.layout {
  display: flex;
  min-height: 100vh;
}

.main-wrap {
  flex: 1;
  margin-left: var(--sidebar-width);
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.main-wrap--collapsed {
  margin-left: 60px;
}

.main-content {
  flex: 1;
  padding-top: 0; /* 移除顶部填充，允许 Hero 纹理延伸至屏幕顶部 */
}

/* Hero section */
.hero {
  position: relative;
  background-color: var(--color-bg);
  /* 网格背景 + 弥散渐变光晕 */
  background-image: 
    radial-gradient(circle at 50% -20%, oklch(0.72 0.12 152 / 0.08), transparent 50%),
    linear-gradient(var(--color-border-light) 1px, transparent 1px), 
    linear-gradient(90deg, var(--color-border-light) 1px, transparent 1px);
  background-size: 100% 100%, 20px 20px, 20px 20px;
  padding: calc(var(--header-height) + 24px) 32px 36px; /* 压缩垂直内边距，告别空旷感 */
  text-align: center;
  border-bottom: 1px solid var(--color-border-light);
  overflow: hidden;
}

/* 渐变修饰块 */
.hero::before {
  content: '';
  position: absolute;
  top: 0;
  left: 10%;
  right: 10%;
  height: 1px;
  background: linear-gradient(90deg, transparent, var(--color-primary-light), transparent);
  opacity: 0.3;
}

.hero-inner {
  max-width: 960px; /* 配合 920px 宽度长搜索框，横向延伸 */
  margin: 0 auto;
  position: relative;
  z-index: 2;
}

.hero-title {
  font-family: var(--font-serif);
  font-size: clamp(2.75rem, 7vw, 4.25rem); /* 大气衬线体字号升级 */
  font-weight: 800;
  color: var(--color-text);
  margin-bottom: 16px;
  letter-spacing: -0.03em;
  text-wrap: balance;
}

.hero-desc {
  font-size: 1.125rem; /* 字号升级至 18px，增加大气感 */
  font-weight: 400;
  color: var(--color-text-secondary);
  margin-bottom: 40px;
  letter-spacing: 0.01em;
  line-height: 1.6;
  max-width: 65ch;
  margin-left: auto;
  margin-right: auto;
}

.hero-shortcuts {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-top: 28px;
  flex-wrap: wrap;
}

.shortcut-link {
  font-size: 0.8125rem; /* 13px */
  font-weight: 500;
  color: var(--color-text-secondary);
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  padding: 6px 14px;
  border-radius: var(--radius-full, 9999px);
  background: #fff;
  border: 1px solid var(--color-border);
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.02);
  text-decoration: none;
}

.shortcut-link:hover {
  color: var(--color-primary);
  border-color: var(--color-primary-light);
  background: var(--color-tag-bg);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px oklch(0.72 0.14 152 / 0.08);
}

/* =============================================
   滚动渐入动画
   ============================================= */
.section-reveal {
  opacity: 0;
  transform: translateY(16px);
  transition: opacity 0.25s cubic-bezier(0.22, 1, 0.36, 1),
              transform 0.25s cubic-bezier(0.22, 1, 0.36, 1);
  transition-delay: var(--reveal-delay, 0ms);
}

.section-reveal.revealed {
  opacity: 1;
  transform: translateY(0);
}

/* Content area */
.content-body {
  padding: 24px 40px;
  max-width: 2000px;
  margin: 0 auto;
  width: 100%;
  box-sizing: border-box;
  container-type: inline-size;
}


.loading-state {
  text-align: center;
  padding: 80px 0;
  color: var(--color-text-light);
}

.spinner {
  width: 36px;
  height: 36px;
  border: 3px solid var(--color-border);
  border-top-color: var(--color-primary);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto 12px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.search-result-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  font-size: 15px;
  color: var(--color-text-secondary);
}

.clear-search {
  background: none;
  color: var(--color-primary);
  font-size: 13px;
}

.section-empty {
  padding: 48px;
  text-align: center;
}

.empty-icon {
  font-size: 2.5rem;
  margin-bottom: 12px;
  color: var(--color-text-light);
}

.empty-title {
  font-size: 1rem;
  font-weight: 600;
  color: var(--color-text-secondary);
  margin-bottom: 4px;
}

.empty-desc {
  font-size: 0.875rem;
  color: var(--color-text-light);
}

.section-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 14px;
}

@media (prefers-reduced-motion: reduce) {
  .section-reveal {
    opacity: 1;
    transform: none;
    transition: none;
  }
}

@media (max-width: 768px) {
  .main-wrap,
  .main-wrap--collapsed {
    margin-left: 0;
  }

  .content-body {
    padding: 16px;
  }

  .hero {
    padding: 24px 16px 20px;
  }

  .hero-title {
    font-size: 1.5rem;
  }

}
</style>

