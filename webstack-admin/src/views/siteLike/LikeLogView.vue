<template>
  <div>
    <n-card title="点赞记录" style="margin-bottom: 16px;">
      <n-space>
        <n-input-number
          v-model:value="query.siteId"
          placeholder="按网站 ID 筛选"
          :min="1"
          clearable
          style="width: 180px;"
        />
        <n-input-number
          v-model:value="query.userId"
          placeholder="按用户 ID 筛选"
          :min="1"
          clearable
          style="width: 180px;"
        />
        <n-button type="primary" @click="handleSearch">查询</n-button>
        <n-button @click="handleReset">重置</n-button>
        <n-button
          type="error"
          :disabled="checkedRowKeys.length === 0"
          @click="handleBatchDelete"
        >
          批量删除
        </n-button>
      </n-space>
    </n-card>

    <n-data-table
      :columns="columns"
      :data="tableData"
      :loading="loading"
      :pagination="pagination"
      :row-key="row => row.id"
      v-model:checked-row-keys="checkedRowKeys"
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
  NDataTable, NTag, NPopconfirm
} from 'naive-ui'
import { getLikePage, deleteLikeRecord, deleteLikeBatch } from '../../api/siteLike.js'

// ---------- 查询条件 ----------
const query = reactive({
  siteId: null,
  userId: null
})

// ---------- 表格数据 ----------
const loading = ref(false)
const tableData = ref([])
const checkedRowKeys = ref([])

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
  { type: 'selection' },
  { title: 'ID', key: 'id', width: 80 },
  {
    title: '网站',
    key: 'siteTitle',
    width: 200,
    render: (row) =>
      h('span', null, [
        row.siteTitle || '-',
        h('span', { style: 'color:#999; font-size:12px; margin-left:6px;' }, `#${row.siteId}`)
      ])
  },
  {
    title: '用户',
    key: 'username',
    width: 160,
    render: (row) =>
      row.userId
        ? h(NTag, { type: 'info', size: 'small' }, { default: () => `${row.username || '未知'} #${row.userId}` })
        : h(NTag, { type: 'default', size: 'small' }, { default: () => '游客' })
  },
  { title: 'IP 地址', key: 'ip', width: 160 },
  { title: '点赞时间', key: 'createTime', width: 180 },
  {
    title: '操作',
    key: 'actions',
    width: 100,
    render: (row) =>
      h(
        NPopconfirm,
        { onPositiveClick: () => handleDelete(row.id) },
        {
          trigger: () =>
            h(NButton, { size: 'small', type: 'error', tertiary: true }, { default: () => '删除' }),
          default: () => '确认删除该点赞记录？'
        }
      )
  }
]

// ---------- 数据加载 ----------
async function fetchData() {
  loading.value = true
  try {
    const res = await getLikePage({
      page: pagination.page,
      size: pagination.pageSize,
      siteId: query.siteId || undefined,
      userId: query.userId || undefined
    })
    tableData.value = res.records
    pagination.itemCount = res.total
  } finally {
    loading.value = false
  }
}

async function handleDelete(id) {
  await deleteLikeRecord(id)
  window.$message?.success('删除成功')
  fetchData()
}

function handleBatchDelete() {
  if (checkedRowKeys.value.length === 0) return
  window.$dialog?.warning({
    title: '批量删除',
    content: `确认删除选中的 ${checkedRowKeys.value.length} 条点赞记录？`,
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      await deleteLikeBatch(checkedRowKeys.value)
      window.$message?.success('删除成功')
      checkedRowKeys.value = []
      fetchData()
    }
  })
}

function handleSearch() {
  pagination.page = 1
  fetchData()
}

function handleReset() {
  query.siteId = null
  query.userId = null
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
