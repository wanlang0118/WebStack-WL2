<template>
  <div>
    <n-card title="消息通知" style="margin-bottom: 16px;">
      <n-space>
        <n-input-number
          v-model:value="query.receiverId"
          placeholder="按接收者 ID 筛选"
          :min="1"
          clearable
          style="width: 180px;"
        />
        <n-select
          v-model:value="query.noticeType"
          placeholder="通知类型"
          clearable
          :options="typeOptions"
          style="width: 150px;"
        />
        <n-select
          v-model:value="query.isRead"
          placeholder="已读状态"
          clearable
          :options="readOptions"
          style="width: 130px;"
        />
        <n-input
          v-model:value="query.keyword"
          placeholder="搜索标题/摘要"
          clearable
          style="width: 200px;"
        />
        <n-button type="primary" @click="handleSearch">查询</n-button>
        <n-button @click="handleReset">重置</n-button>
        <n-button type="info" @click="showSendModal = true">发布公告</n-button>
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

    <NoticeSendModal
      v-model:show="showSendModal"
      @success="fetchData"
    />

    <NoticeDetailModal
      v-model:show="showDetailModal"
      :record="detailRecord"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, h } from 'vue'
import {
  NCard, NSpace, NInputNumber, NButton, NInput, NSelect,
  NDataTable, NTag, NPopconfirm, NEllipsis
} from 'naive-ui'
import {
  getNoticePage, markNoticeRead,
  deleteNotice, deleteNoticeBatch
} from '../../api/sysNotice.js'
import NoticeSendModal from './NoticeSendModal.vue'
import NoticeDetailModal from './NoticeDetailModal.vue'

const typeOptions = [
  { label: '系统公告', value: 1 },
  { label: '评论回复', value: 2 }
]

const readOptions = [
  { label: '未读', value: 0 },
  { label: '已读', value: 1 }
]

const typeTagMap = {
  1: { label: '系统公告', type: 'warning' },
  2: { label: '评论回复', type: 'info' },
  3: { label: '点赞提醒', type: 'success' }
}

const query = reactive({
  receiverId: null,
  noticeType: null,
  isRead: null,
  keyword: null
})

const loading = ref(false)
const tableData = ref([])
const checkedRowKeys = ref([])
const showSendModal = ref(false)
const showDetailModal = ref(false)
const detailRecord = ref(null)

const pagination = reactive({
  page: 1,
  pageSize: 10,
  itemCount: 0,
  showSizePicker: true,
  pageSizes: [10, 20, 50],
  showQuickJumper: true,
  prefix: ({ itemCount }) => `共 ${itemCount} 条`
})

const columns = [
  { type: 'selection' },
  { title: 'ID', key: 'id', width: 80 },
  {
    title: '接收者',
    key: 'receiverName',
    width: 140,
    render: (row) =>
      h('span', null, [
        row.receiverName || '-',
        h('span', { style: 'color:#999; font-size:12px; margin-left:6px;' }, `#${row.receiverId}`)
      ])
  },
  {
    title: '类型',
    key: 'noticeType',
    width: 100,
    render: (row) => {
      const tag = typeTagMap[row.noticeType] || { label: '未知', type: 'default' }
      return h(NTag, { type: tag.type, size: 'small' }, { default: () => tag.label })
    }
  },
  { title: '标题', key: 'title', width: 160 },
  {
    title: '摘要',
    key: 'summary',
    minWidth: 200,
    render: (row) =>
      h(NEllipsis, { lineClamp: 1, style: 'max-width: 300px;' }, { default: () => row.summary })
  },
  {
    title: '已读',
    key: 'isRead',
    width: 80,
    render: (row) =>
      h(NTag, {
        type: row.isRead === 1 ? 'success' : 'warning',
        size: 'small'
      }, { default: () => row.isRead === 1 ? '已读' : '未读' })
  },
  { title: '创建时间', key: 'createTime', width: 180 },
  {
    title: '操作',
    key: 'actions',
    width: 220,
    render: (row) =>
      h(NSpace, { size: 'small' }, {
        default: () => [
          h(NButton, {
            size: 'small',
            type: 'primary',
            tertiary: true,
            onClick: () => handleDetail(row)
          }, { default: () => '查看' }),
          row.isRead === 0
            ? h(NButton, {
                size: 'small',
                type: 'info',
                tertiary: true,
                onClick: () => handleMarkRead(row)
              }, { default: () => '标记已读' })
            : null,
          h(
            NPopconfirm,
            { onPositiveClick: () => handleDelete(row.id) },
            {
              trigger: () =>
                h(NButton, { size: 'small', type: 'error', tertiary: true }, { default: () => '删除' }),
              default: () => '确认删除该通知？'
            }
          )
        ].filter(Boolean)
      })
  }
]

async function fetchData() {
  loading.value = true
  try {
    const res = await getNoticePage({
      page: pagination.page,
      size: pagination.pageSize,
      receiverId: query.receiverId || undefined,
      noticeType: query.noticeType != null ? query.noticeType : undefined,
      isRead: query.isRead != null ? query.isRead : undefined,
      keyword: query.keyword || undefined
    })
    tableData.value = res.records
    pagination.itemCount = res.total
  } finally {
    loading.value = false
  }
}

function handleDetail(row) {
  detailRecord.value = row
  showDetailModal.value = true
}

async function handleMarkRead(row) {
  await markNoticeRead(row.id)
  row.isRead = 1
  window.$message?.success('已标记为已读')
}

async function handleDelete(id) {
  await deleteNotice(id)
  window.$message?.success('删除成功')
  fetchData()
}

function handleBatchDelete() {
  if (checkedRowKeys.value.length === 0) return
  window.$dialog?.warning({
    title: '批量删除',
    content: `确认删除选中的 ${checkedRowKeys.value.length} 条通知？`,
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      await deleteNoticeBatch(checkedRowKeys.value)
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
  query.receiverId = null
  query.noticeType = null
  query.isRead = null
  query.keyword = null
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
