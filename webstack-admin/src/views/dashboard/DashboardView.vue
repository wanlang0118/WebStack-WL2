<template>
  <div class="dashboard">
    <!-- Top Stats Row (Reduced from 7 to 4 key metrics for better grid layout) -->
    <div class="stats-grid">
      <StatCard
        v-for="stat in topStats"
        :key="stat.key"
        :label="stat.label"
        :value="stat.value"
        :icon="stat.icon"
        :loading="loading"
      />
    </div>

    <!-- Charts Row -->
    <div class="charts-row">
      <!-- Main Trend Chart -->
      <div class="chart-panel trend-panel">
        <div class="panel-header">
          <h3 class="panel-title">近 7 日活跃趋势</h3>
          <span class="panel-subtitle">访问点击与点赞数据</span>
        </div>
        <div class="panel-body">
          <TrendChart v-if="!loading" :data="trendData" />
          <div v-else class="skeleton chart-skeleton"></div>
        </div>
      </div>
    </div>

    <!-- Secondary Row -->
    <div class="charts-row secondary-row">
      <!-- Category Donut Chart -->
      <div class="chart-panel donut-panel">
        <div class="panel-header">
          <h3 class="panel-title">站点分类占比</h3>
          <span class="panel-subtitle">收录的网站分布情况</span>
        </div>
        <div class="panel-body">
          <CategoryDonutChart v-if="!loading" :data="categoryData" />
          <div v-else class="skeleton chart-skeleton"></div>
        </div>
      </div>

      <!-- Quick Actions (Moved here) -->
      <div class="chart-panel actions-panel">
        <div class="panel-header">
          <h3 class="panel-title">快捷操作</h3>
          <span class="panel-subtitle">常用管理功能入口</span>
        </div>
        <div class="panel-body">
          <div class="quick-actions">
            <div class="action-card" @click="$router.push('/site')">
              <n-icon :component="Globe" class="action-icon" :stroke-width="1.5" />
              <span class="action-label">管理网站</span>
            </div>
            <div class="action-card" @click="$router.push('/category')">
              <n-icon :component="FolderTree" class="action-icon" :stroke-width="1.5" />
              <span class="action-label">管理分类</span>
            </div>
            <div class="action-card" @click="$router.push('/tag')">
              <n-icon :component="Tags" class="action-icon" :stroke-width="1.5" />
              <span class="action-label">管理标签</span>
            </div>
            <div class="action-card" @click="$router.push('/comment')">
              <n-icon :component="MessageSquare" class="action-icon" :stroke-width="1.5" />
              <span class="action-label">管理评论</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import StatCard from '../../components/StatCard.vue'
import TrendChart from '../../components/charts/TrendChart.vue'
import CategoryDonutChart from '../../components/charts/CategoryDonutChart.vue'
import { getSitePage } from '../../api/site'
import { getCategoryTree } from '../../api/category'
import { getClickLogPage } from '../../api/siteClickLog'
import { getLikePage } from '../../api/siteLike'
import { getDashboardTrend } from '../../api/dashboard'
import { NIcon } from 'naive-ui'
import { Globe, FolderTree, Tags, Users, MessageSquare, MousePointerClick, Heart } from 'lucide-vue-next'

const loading = ref(true)

const counts = reactive({
  sites: 0,
  categories: 0,
  clicks: 0,
  likes: 0
})

const topStats = ref([
  { key: 'sites', label: '收录网站', icon: Globe, get value() { return counts.sites } },
  { key: 'categories', label: '网站分类', icon: FolderTree, get value() { return counts.categories } },
  { key: 'clicks', label: '今日点击', icon: MousePointerClick, get value() { return counts.clicks } },
  { key: 'likes', label: '今日点赞', icon: Heart, get value() { return counts.likes } }
])

const trendData = ref({
  dates: [],
  clicks: [],
  likes: []
})

const categoryData = ref([])

const countTreeNodes = (nodes) => {
  let count = 0
  for (const n of nodes) {
    count++
    if (n.children?.length) count += countTreeNodes(n.children)
  }
  return count
}

onMounted(async () => {
  try {
    const results = await Promise.allSettled([
      getSitePage({ page: 1, size: 100 }),
      getCategoryTree(),
      getClickLogPage({ page: 1, size: 1 }),
      getLikePage({ page: 1, size: 1 }),
      getDashboardTrend({ days: 7 })
    ])

    const val = (r) => r.status === 'fulfilled' ? r.value : null

    const siteRes = val(results[0])
    counts.sites = siteRes?.total || 0
    
    const catTree = val(results[1])
    counts.categories = catTree ? countTreeNodes(catTree) : 0

    const clickRes = val(results[2])
    counts.clicks = clickRes?.total || 0

    const likeRes = val(results[3])
    counts.likes = likeRes?.total || 0

    const trendRes = val(results[4])
    if (trendRes) {
      trendData.value = trendRes
    }

    // Mock category distribution based on real top-level categories if available
    if (catTree && catTree.length > 0) {
      categoryData.value = catTree.slice(0, 5).map(cat => ({
        name: cat.title,
        value: Math.floor(Math.random() * 50) + 10 // Mock count per category
      }))
    } else {
      categoryData.value = [
        { value: 40, name: 'AI 工具' },
        { value: 30, name: '设计资源' },
        { value: 20, name: '开发文档' },
        { value: 10, name: '效率插件' }
      ]
    }

  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.dashboard {
  max-width: 1400px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* ---- Stats Grid ---- */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

/* ---- Charts Area ---- */
.charts-row {
  display: grid;
  grid-template-columns: 1fr;
  gap: 24px;
}

.secondary-row {
  grid-template-columns: 2fr 1fr;
}

/* ---- Panels ---- */
.chart-panel {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  padding: 24px;
  display: flex;
  flex-direction: column;
}

.panel-header {
  margin-bottom: 24px;
}

.panel-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 4px 0;
  letter-spacing: -0.01em;
}

.panel-subtitle {
  font-size: 13px;
  color: var(--text-secondary);
}

.panel-body {
  flex: 1;
  position: relative;
}

.chart-skeleton {
  width: 100%;
  height: 300px;
  border-radius: var(--radius-sm);
}

/* ---- Quick Actions ---- */
.quick-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.action-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: transparent;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: background var(--transition-fast) ease, border-color var(--transition-fast) ease;
}

.action-card:hover {
  background: var(--table-row-hover);
  border-color: var(--color-primary-suppl);
}

.action-icon {
  font-size: 18px;
  color: var(--text-secondary);
}

.action-card:hover .action-icon {
  color: var(--color-primary);
}

.action-label {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
}

/* ---- Responsive ---- */
@media (max-width: 1024px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .secondary-row {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>
