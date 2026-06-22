<template>
  <div class="site-section-view">
    <n-space vertical :size="24">
      <!-- 页面头部 -->
      <n-space justify="space-between" align="center">
        <n-h2 style="margin: 0;">小节内容管理</n-h2>
        <div style="color: #666;">请选择网站管理其小节及详细内容</div>
      </n-space>

      <!-- 搜索与筛选区域 -->
      <n-card :bordered="false" size="small" class="filter-card">
        <n-space vertical :size="16">
          <!-- 搜索输入框 -->
          <div class="search-bar">
            <div style="display: flex; justify-content: center; gap: 8px; max-width: 500px; margin: 0 auto;">
              <n-input
                v-model:value="queryParams.title"
                placeholder="请输入网站名称搜索"
                clearable
                size="large"
                @keyup.enter="handleSearch"
                @clear="handleSearch"
              />
              <n-button type="primary" size="large" @click="handleSearch">
                搜索
              </n-button>
            </div>
          </div>

          <!-- 分类过滤标签 -->
          <div class="category-filter">
            <span class="filter-label">所属分类：</span>
            <div class="filter-tags">
              <n-tag
                :checked="queryParams.categoryId === null"
                checkable
                @update:checked="handleCategorySelect(null)"
              >
                全部
              </n-tag>
              <n-tag
                v-for="cat in categories"
                :key="cat.id"
                :checked="queryParams.categoryId === cat.id"
                checkable
                @update:checked="handleCategorySelect(cat.id)"
              >
                {{ cat.title }}
              </n-tag>
            </div>
          </div>
        </n-space>
      </n-card>

      <!-- 网站卡片列表网格 -->
      <n-spin :show="loading">
        <div v-if="sites.length === 0" style="padding: 60px 0; text-align: center;">
          <n-empty description="暂无符合条件的网站数据" />
        </div>
        <n-grid v-else cols="1 s:2 m:3 l:4" :x-gap="16" :y-gap="16" responsive="screen">
          <n-grid-item v-for="site in sites" :key="site.id">
            <n-card
              hoverable
              class="site-card"
              content-style="padding: 16px;"
              @click="goToDetail(site.id)"
            >
              <!-- 卡片头部 (Logo/初始字 + 标题) -->
              <div class="site-card-header">
                <n-avatar
                  v-if="site.thumb"
                  :src="getImageUrl(site.thumb)"
                  :size="48"
                  round
                  fallback-src=""
                />
                <n-avatar
                  v-else
                  :size="48"
                  round
                  :style="{ backgroundColor: getAvatarColor(site.title) }"
                >
                  <span class="avatar-text">{{ getFirstChar(site.title) }}</span>
                </n-avatar>
                <div class="site-info">
                  <div class="site-title">{{ site.title }}</div>
                  <div class="site-category">{{ getCategoryName(site.categoryId) }}</div>
                </div>
              </div>

              <!-- 卡片内容 (描述) -->
              <div class="site-card-body">
                <p class="site-desc" :title="site.description">
                  {{ site.description || '暂无描述信息...' }}
                </p>
              </div>

              <!-- 卡片底部 (操作/统计) -->
              <div class="site-card-footer">
                <span class="url-text" :title="site.url">{{ getDomain(site.url) }}</span>
                <n-button text type="primary" size="small" class="manage-btn">
                  管理小节 ({{ site.sectionCount || 0 }}) →
                </n-button>
              </div>
            </n-card>
          </n-grid-item>
        </n-grid>
      </n-spin>

      <!-- 分页组件 -->
      <div class="pagination-container" v-if="pagination.pageCount > 1">
        <n-pagination
          v-model:page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :page-count="pagination.pageCount"
          show-size-picker
          :page-sizes="[12, 24, 48]"
          @update:page="handlePageChange"
          @update:page-size="handlePageSizeChange"
        />
      </div>
    </n-space>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  NSpace,
  NH2,
  NCard,
  NInput,
  NButton,
  NTag,
  NGrid,
  NGridItem,
  NAvatar,
  NSpin,
  NEmpty,
  NPagination
} from 'naive-ui'
import { getSitePage } from '../../api/site.js'
import { getCategoryTree } from '../../api/category.js'
import { getImageUrl } from '../../utils/image'
import { useSiteSectionQueryStore } from '../../store/queryState.js'

const router = useRouter()
const queryStore = useSiteSectionQueryStore()
const loading = ref(false)
const sites = ref([])
const categories = ref([])
const categoryMap = ref({})

// 查询参数
const queryParams = ref({
  title: queryStore.title,
  categoryId: queryStore.categoryId
})

// 分页参数
const pagination = ref({
  page: queryStore.page,
  pageSize: queryStore.pageSize,
  pageCount: 1,
  total: 0
})

// 加载分类树并扁平化第一层以做标签展示
const fetchCategories = async () => {
  try {
    const data = await getCategoryTree()
    categories.value = data || []
    
    // 构建分类ID与名称的Map
    const map = {}
    const walk = (list) => {
      for (const item of list) {
        map[item.id] = item.title
        if (item.children?.length) walk(item.children)
      }
    }
    walk(data || [])
    categoryMap.value = map
  } catch (error) {
    console.error('获取分类失败', error)
  }
}

// 查询网站列表
const fetchSites = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.value.page,
      size: pagination.value.pageSize,
      title: queryParams.value.title,
      categoryId: queryParams.value.categoryId,
      visible: 1 // 只展示已上架的网站
    }
    const res = await getSitePage(params)
    sites.value = res.records || []
    pagination.value.total = res.total || 0
    pagination.value.pageCount = res.pages || 1
  } catch (error) {
    console.error('获取网站列表失败', error)
  } finally {
    loading.value = false
  }
}

// 搜索操作
const handleSearch = () => {
  pagination.value.page = 1
  queryStore.$patch({
    title: queryParams.value.title,
    page: 1
  })
  fetchSites()
}

// 选择分类
const handleCategorySelect = (catId) => {
  queryParams.value.categoryId = catId
  pagination.value.page = 1
  queryStore.$patch({
    categoryId: catId,
    page: 1
  })
  fetchSites()
}

// 分页变化
const handlePageChange = (page) => {
  pagination.value.page = page
  queryStore.page = page
  fetchSites()
}

// 每页条数变化
const handlePageSizeChange = (pageSize) => {
  pagination.value.pageSize = pageSize
  pagination.value.page = 1
  queryStore.$patch({
    pageSize: pageSize,
    page: 1
  })
  fetchSites()
}

// 跳转到详情管理页面
const goToDetail = (siteId) => {
  router.push(`/site-section/detail/${siteId}`)
}

// 辅助方法：生成首字母
const getFirstChar = (title) => {
  return title ? title.charAt(0).toUpperCase() : 'W'
}

// 辅助方法：随机漂亮的背景色给头像
const getAvatarColor = (title) => {
  if (!title) return '#1890ff'
  const colors = [
    '#f5222d', '#fa541c', '#fa8c16', '#faad14', '#a0d911',
    '#52c41a', '#13c2c2', '#1890ff', '#2f54eb', '#722ed1', '#eb2f96'
  ]
  let hash = 0
  for (let i = 0; i < title.length; i++) {
    hash = title.charCodeAt(i) + ((hash << 5) - hash)
  }
  return colors[Math.abs(hash) % colors.length]
}

// 辅助方法：获取分类名称
const getCategoryName = (catId) => {
  return categoryMap.value[catId] || '未分类'
}

// 辅助方法：提取主域名
const getDomain = (url) => {
  if (!url) return ''
  try {
    const domain = url.replace(/^(https?:\/\/)?(www\.)?/, '')
    return domain.split('/')[0]
  } catch (e) {
    return url
  }
}

onMounted(() => {
  fetchCategories()
  fetchSites()
})
</script>

<style scoped>
.site-section-view {
  padding: 12px 0;
}

.filter-card {
  border-radius: 8px;
  background-color: #fafafc;
}

.search-bar {
  margin-bottom: 8px;
}

.category-filter {
  display: flex;
  align-items: flex-start;
  flex-wrap: wrap;
}

.filter-label {
  font-weight: 500;
  color: #333;
  margin-top: 4px;
  margin-right: 8px;
  white-space: nowrap;
}

.filter-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.site-card {
  border-radius: 8px;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.site-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.08);
}

.site-card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.avatar-text {
  font-size: 20px;
  font-weight: bold;
  color: #fff;
}

.site-info {
  flex-grow: 1;
  overflow: hidden;
}

.site-title {
  font-size: 16px;
  font-weight: 600;
  color: #18a058;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.site-category {
  font-size: 12px;
  color: #999;
  margin-top: 2px;
}

.site-card-body {
  flex-grow: 1;
  margin-bottom: 16px;
}

.site-desc {
  font-size: 13px;
  color: #666;
  line-height: 1.5;
  margin: 0;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
  height: 38px;
}

.site-card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 1px solid #f0f0f0;
  padding-top: 12px;
  margin-top: auto;
}

.url-text {
  font-size: 12px;
  color: #999;
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.manage-btn {
  font-size: 13px;
  font-weight: 500;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
