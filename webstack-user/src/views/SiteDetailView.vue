<template>
  <div class="detail-layout">
    <AppSidebar
      :categories="categories"
      :collapsed="sidebarCollapsed"
      :mobile-open="mobileNavOpen"
      @toggle="sidebarCollapsed = !sidebarCollapsed"
      @select="handleCategorySelect"
      @close-mobile="closeMobileNav"
    />

    <div class="main-wrap" :class="{ 'main-wrap--collapsed': sidebarCollapsed && !isMobile }">
      <header class="detail-header">
        <div class="detail-header-inner">
          <button
            class="mobile-menu-btn"
            @click="openMobileNav"
            aria-label="打开分类导航"
          >
            <i class="ri-menu-line"></i>
          </button>
          <router-link to="/" class="back-btn">
            <i class="ri-arrow-left-line"></i> 返回首页
          </router-link>
          <router-link to="/" class="header-brand">WebStack-WL</router-link>
        </div>
      </header>

      <main class="detail-main" v-if="site">
        <!-- Site info hero card -->
        <div class="site-hero">
          <div class="site-preview" v-if="site.thumb">
            <img :src="thumbSrc" :alt="site.title" />
          </div>
          <div class="site-preview site-preview-placeholder" v-else>
            {{ site.title?.charAt(0) }}
          </div>

          <div class="site-info">
            <div class="site-tags" v-if="site.tagNameList?.length">
              <span class="site-tag" v-for="tag in site.tagNameList" :key="tag">{{ tag }}</span>
            </div>
            <h1 class="site-title">{{ site.title }}</h1>
            <p class="site-subtitle">{{ site.description }}</p>

            <div class="site-actions">
              <a class="btn-visit" :href="site.url" target="_blank" rel="noopener">
                访问官网 <i class="ri-external-link-line"></i>
              </a>
              <button class="btn-icon" @click="toggleLike"
                      :title="liked ? '取消点赞' : '点赞'">
                <span :class="liked ? 'heart-active' : 'heart'">{{ liked ? '❤️' : '🤍' }}</span>
              </button>
            </div>

            <div class="site-stats">
              <span class="stat">
                <i class="ri-eye-line"></i> {{ site.clickCount || 0 }}
              </span>
              <span class="stat">
                <i class="ri-thumb-up-line"></i> {{ likeCount }}
              </span>
              <span class="stat">
                <i class="ri-chat-3-line"></i> {{ site.commentCount || 0 }}
              </span>
            </div>
          </div>
        </div>

        <!-- Section contents -->
        <div class="detail-body" v-if="sections.length">
          <div v-for="sec in sections" :key="sec.section.id" class="section-block">
            <h2 class="section-heading">{{ sec.section.title }}</h2>
            <SectionContent
              v-for="content in sec.contents"
              :key="content.id"
              :content="content"
            />
          </div>
        </div>

        <!-- Comments -->
        <CommentList :site-id="siteId" />
      </main>

      <div class="detail-loading" v-else-if="loading">
        <div class="spinner"></div>
        <p>正在加载...</p>
      </div>

      <div class="detail-error" v-else>
        <p>网站不存在或加载失败</p>
        <router-link to="/" class="back-link">返回首页</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useMobileLayout } from '@/composables/useMobileLayout'
import AppSidebar from '@/components/layout/AppSidebar.vue'
import { getSiteDetail, getSiteSections, getSectionContents, recordClick } from '@/api/site'
import { getLikeStatus, likeSite, unlikeSite } from '@/api/like'
import { getCategoryTree } from '@/api/category'
import SectionContent from '@/components/detail/SectionContent.vue'
import CommentList from '@/components/comment/CommentList.vue'

const route = useRoute()
const router = useRouter()
const siteId = computed(() => Number(route.params.id))

const site = ref(null)
const sections = ref([])
const categories = ref([])
const loading = ref(true)
const liked = ref(false)
const likeCount = ref(0)
const sidebarCollapsed = ref(false)
const { isMobile, mobileNavOpen, openMobileNav, closeMobileNav } = useMobileLayout()

const thumbSrc = computed(() => {
  const t = site.value?.thumb
  if (!t) return ''
  if (t.startsWith('http://') || t.startsWith('https://')) return t
  return `/uploads/${t}`
})

function goCategory(cat) {
  router.push({ path: '/', hash: `#cat-${cat.id}` })
}

function handleCategorySelect(cat) {
  goCategory(cat)
  closeMobileNav()
}

async function loadSite() {
  loading.value = true
  try {
    const [data, tree] = await Promise.all([
      getSiteDetail(siteId.value),
      getCategoryTree()
    ])
    site.value = data
    categories.value = tree || []
    likeCount.value = data?.likeCount || 0

    recordClick(siteId.value).catch(() => {})
    getLikeStatus(siteId.value).then(s => { liked.value = !!s }).catch(() => {})

    const secs = await getSiteSections(siteId.value)
    if (secs?.length) {
      const loaded = await Promise.all(
        secs.map(async sec => {
          const contents = await getSectionContents(sec.id).catch(() => [])
          return { section: sec, contents: contents || [] }
        })
      )
      sections.value = loaded
    }
  } catch (e) {
    console.error('加载失败', e)
    site.value = null
  } finally {
    loading.value = false
  }
}

async function toggleLike() {
  try {
    if (liked.value) {
      await unlikeSite(siteId.value)
      liked.value = false
      likeCount.value = Math.max(0, likeCount.value - 1)
    } else {
      await likeSite(siteId.value)
      liked.value = true
      likeCount.value++
    }
  } catch (e) {
    console.error('操作失败', e)
  }
}

onMounted(loadSite)
</script>

<style scoped>
.detail-layout {
  display: flex;
  min-height: 100vh;
}

.main-wrap {
  flex: 1;
  margin-left: var(--sidebar-width);
  min-height: 100vh;
  background: var(--color-bg);
}

.main-wrap--collapsed {
  margin-left: 60px;
}

/* Header */
.detail-header {
  position: sticky;
  top: 0;
  background: var(--color-bg-header);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--color-border-light);
  z-index: 50;
}

.detail-header-inner {
  max-width: 100%;
  margin: 0 auto;
  padding: 0 24px;
  height: var(--header-height);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
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
  flex-shrink: 0;
  transition: color 0.15s, background 0.15s;
}

.mobile-menu-btn:hover {
  color: var(--color-primary);
  background: var(--color-tag-bg);
}

.back-btn {
  font-size: 14px;
  color: var(--color-text-secondary);
  transition: color 0.2s;
}

.back-btn:hover {
  color: var(--color-primary);
}

.header-brand {
  font-family: var(--font-serif);
  font-size: 18px;
  font-weight: 700;
  color: var(--color-text);
}

/* Main content */
.detail-main {
  max-width: 1200px; /* 全局最宽锁死 1200px，使页面在宽屏中优雅居中，不无限拉伸 */
  margin: 0 auto;
  padding: 32px 24px 80px; /* 间距微调，增加下方空白呼吸感 */
}

/* Site hero - matching ai-bot.cn detail page layout */
.site-hero {
  display: flex;
  gap: 36px; /* 间距拉开，更具呼吸感 */
  align-items: flex-start;
  margin-bottom: 28px;
  padding: 32px; /* 内边距调大 */
  background: var(--color-bg-card);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-card);
}

.site-preview {
  width: 320px; /* 大气化升级：由 200px 拓宽为 320px */
  height: 200px; /* 高度从 150px 等比放宽至 200px */
  border-radius: var(--radius-md);
  overflow: hidden;
  flex-shrink: 0;
  background: var(--color-bg);
  border: 1px solid var(--color-border-light);
}

.site-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.site-preview-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-primary);
  color: #fff;
  font-size: 64px; /* 占位符字体增大 */
  font-weight: 700;
}

.site-info {
  flex: 1;
  padding-top: 4px;
}

.site-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 12px;
}

.site-tag {
  padding: 4px 12px; /* padding 增大 */
  background: var(--color-tag-bg);
  color: var(--color-tag-text);
  font-size: 13px; /* 字体提升 */
  border-radius: 4px;
  font-weight: 500;
}

.site-title {
  font-family: var(--font-serif);
  font-size: 2.25rem; /* 字号由 1.5rem 提升至 2.25rem */
  font-weight: 800;
  color: var(--color-text);
  margin-bottom: 12px;
  letter-spacing: -0.02em;
}

.site-subtitle {
  font-size: 1rem; /* 字号从 14px 调大至 16px */
  color: var(--color-text-secondary);
  line-height: 1.6;
  margin-bottom: 24px;
}

.site-actions {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
}

.btn-visit {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 10px 24px;
  background: var(--color-primary); /* 改为有品牌底色的高级实色按钮 */
  color: #fff;
  border: none;
  border-radius: var(--radius-md);
  font-size: 15px; /* 字号放大 */
  font-weight: 500;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  text-decoration: none;
  box-shadow: 0 2px 8px oklch(0.72 0.14 152 / 0.15);
}

.btn-visit:hover {
  background: var(--color-primary-hover);
  color: #fff;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px oklch(0.72 0.14 152 / 0.25);
}

.btn-icon {
  width: 42px; /* 按钮大小从 36px 提升 */
  height: 42px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: var(--color-bg);
  border: 1px solid var(--color-border);
  font-size: 18px; /* 心形图标稍微调大 */
  cursor: pointer;
  transition: all 0.2s;
}

.btn-icon:hover {
  background: #fef2f2;
  border-color: #fca5a5;
  transform: translateY(-1px);
}

.site-stats {
  display: flex;
  gap: 20px;
  align-items: center;
  margin-top: 4px;
}

.stat {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--color-text-light);
}

.stat i {
  font-size: 15px;
}

/* Section blocks */
.detail-body {
  margin-bottom: 20px;
}

.section-block {
  background: var(--color-bg-card);
  border-radius: var(--radius-lg);
  padding: 24px;
  margin-bottom: 16px;
  box-shadow: var(--shadow-card);
}

.section-heading {
  font-family: var(--font-serif);
  font-size: 1.375rem; /* 字号由 1.125rem 提升至 1.375rem */
  font-weight: 700;
  color: var(--color-text);
  margin-bottom: 20px; /* 间距微调 */
  padding-bottom: 12px;
  border-bottom: 1px solid var(--color-border-light);
}

.section-block :deep(.section-content) {
  width: 100%;
}

/* Loading / Error */
.detail-loading,
.detail-error {
  text-align: center;
  padding: 120px 24px;
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

.back-link {
  display: inline-block;
  margin-top: 16px;
  color: var(--color-primary);
  font-size: 14px;
}

@media (max-width: 768px) {
  .main-wrap,
  .main-wrap--collapsed {
    margin-left: 0;
  }

  .mobile-menu-btn {
    display: flex;
  }

  .detail-header-inner {
    padding: 0 16px;
  }

  .site-hero {
    flex-direction: column;
    padding: 20px;
  }

  .site-preview {
    width: 100%;
    height: 180px;
  }

  .detail-main {
    padding: 16px 16px 40px;
  }

  .section-block {
    padding: 16px;
  }
}
</style>
