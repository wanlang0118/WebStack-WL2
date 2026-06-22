<template>
  <div>
    <n-card title="点击记录" style="margin-bottom: 16px;">
      <n-space>
        <n-input-number
          v-model:value="query.siteId"
          placeholder="按网站 ID 筛选"
          :min="1"
          clearable
          style="width: 180px;"
        />
        <n-button type="primary" @click="handleSearch">查询</n-button>
        <n-button @click="handleReset">重置</n-button>
      </n-space>
    </n-card>

    <n-data-table
      :columns="columns"
      :data="tableData"
      :loading="loading"
      :pagination="pagination"
      :row-key="row => row.id"
      remote
      @update:page="handlePageChange"
      @update:page-size="handlePageSizeChange"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, h } from 'vue'
import {
  NCard, NSpace, NInputNumber, NButton,
  NDataTable, NTag
} from 'naive-ui'
import { getClickLogPage } from '../../api/siteClickLog.js'

// ---------- 查询条件 ----------
const query = reactive({
  siteId: null
})

// ---------- 表格数据 ----------
const loading = ref(false)
const tableData = ref([])

const pagination = reactive({
  page: 1,
  pageSize: 10,
  itemCount: 0,
  showSizePicker: true,
  pageSizes: [10, 20, 50],
  showQuickJumper: true,
  prefix: ({ itemCount }) => `共 ${itemCount} 条`
})

// ---------- 列定义 ----------
const columns = [
  { title: 'ID', key: 'id', width: 80 },
  {
    title: '网站',
    key: 'siteTitle',
    width: 180,
    render: (row) =>
      h('span', null, [
        row.siteTitle || '-',
        h('span', { style: 'color:#999; font-size:12px; margin-left:6px;' }, `#${row.siteId}`)
      ])
  },
  {
    title: '用户',
    key: 'userId',
    width: 100,
    render: (row) =>
      row.userId
        ? h(NTag, { type: 'info', size: 'small' }, { default: () => `用户 ${row.userId}` })
        : h(NTag, { type: 'default', size: 'small' }, { default: () => '匿名' })
  },
  { title: 'IP 地址', key: 'ip', width: 160 },
  { title: '点击时间', key: 'createTime', width: 180 }
]

// ---------- 数据加载 ----------
async function fetchData() {
  loading.value = true
  try {
    const res = await getClickLogPage({
      page: pagination.page,
      size: pagination.pageSize,
      siteId: query.siteId || undefined
    })
    tableData.value = res.records
    pagination.itemCount = res.total
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pagination.page = 1
  fetchData()
}

function handleReset() {
  query.siteId = null
  pagination.page = 1
  fetchData()
}

function handlePageChange(page) {
  pagination.page = page
  fetchData()
}

function handlePageSizeChange(pageSize) {
  pagination.pageSize = pageSize
  pagination.page = 1
  fetchData()
}

onMounted(fetchData)
</script>
