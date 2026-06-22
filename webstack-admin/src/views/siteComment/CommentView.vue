<template>
  <div>
    <n-card title="评论管理" style="margin-bottom: 16px;">
      <n-space>
        <n-input-number
          v-model:value="query.siteId"
          placeholder="按网站 ID 筛选"
          :min="1"
          clearable
          style="width: 180px;"
        />
        <n-select
          v-model:value="query.status"
          placeholder="审核状态"
          clearable
          :options="statusOptions"
          style="width: 150px;"
        />
        <n-input
          v-model:value="query.keyword"
          placeholder="搜索评论内容/游客昵称"
          clearable
          style="width: 220px;"
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
        <n-button
          type="warning"
          :disabled="checkedRowKeys.length === 0"
          @click="handleBatchApprove"
        >
          批量通过
        </n-button>
      </n-space>
    </n-card>

    <n-data-table
      :columns="columns"
      :data="tableData"
      :loading="loading"
      :pagination="pagination"
      :row-key="row => row.id"
      children-key="children"
      default-expand-all
      v-model:checked-row-keys="checkedRowKeys"
      remote
      @update:page="handlePageChange"
      @update:page-size="handlePageSizeChange"
    />

    <CommentFormModal
      v-model:show="modalVisible"
      :mode="modalMode"
      :record="modalRecord"
      @success="fetchData"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, h } from 'vue'
import {
  NCard, NSpace, NInputNumber, NButton, NInput, NSelect,
  NDataTable, NTag, NPopconfirm, NSwitch, NEllipsis, NTooltip
} from 'naive-ui'
import {
  getCommentPage, updateCommentStatus,
  deleteComment, deleteCommentBatch
} from '../../api/siteComment.js'
import CommentFormModal from './CommentFormModal.vue'

const statusOptions = [
  { label: '待审核', value: 0 },
  { label: '已通过', value: 1 }
]

const query = reactive({
  siteId: null,
  status: null,
  keyword: null
})

const loading = ref(false)
const tableData = ref([])
const checkedRowKeys = ref([])
const modalVisible = ref(false)
const modalMode = ref('reply')
const modalRecord = ref(null)

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
    title: '网站',
    key: 'siteTitle',
    width: 160,
    render: (row) =>
      h('span', null, [
        row.siteTitle || '-',
        h('span', { style: 'color:#999; font-size:12px; margin-left:6px;' }, `#${row.siteId}`)
      ])
  },
  {
    title: '评论者',
    key: 'commenter',
    width: 150,
    render: (row) =>
      row.userId
        ? h(NTag, { type: 'info', size: 'small' }, { default: () => `${row.username || '未知'} #${row.userId}` })
        : h(NTag, { type: 'default', size: 'small' }, { default: () => row.guestName || '匿名游客' })
  },
  {
    title: '评论内容',
    key: 'content',
    minWidth: 200,
    render: (row) =>
      h(NTooltip, { trigger: 'hover', width: 400 }, {
        trigger: () => h(NEllipsis, { lineClamp: 2, style: 'max-width: 300px;' }, { default: () => row.content }),
        default: () => row.content
      })
  },
  {
    title: '审核状态',
    key: 'status',
    width: 120,
    render: (row) =>
      h(NSwitch, {
        value: row.status === 1,
        onUpdateValue: (val) => handleStatusChange(row, val),
        checkedChildren: '已通过',
        uncheckedChildren: '待审核'
      })
  },
  { title: '创建时间', key: 'createTime', width: 180 },
  {
    title: '操作',
    key: 'actions',
    width: 200,
    render: (row) =>
      h(NSpace, { size: 'small' }, {
        default: () => [
          h(NButton, {
            size: 'small',
            type: 'primary',
            tertiary: true,
            onClick: () => handleReply(row)
          }, { default: () => '回复' }),
          h(NButton, {
            size: 'small',
            type: 'info',
            tertiary: true,
            onClick: () => handleEdit(row)
          }, { default: () => '编辑' }),
          h(
            NPopconfirm,
            { onPositiveClick: () => handleDelete(row.id) },
            {
              trigger: () =>
                h(NButton, { size: 'small', type: 'error', tertiary: true }, { default: () => '删除' }),
              default: () => '确认删除该评论？子评论也会被一并删除。'
            }
          )
        ]
      })
  }
]

async function fetchData() {
  loading.value = true
  try {
    const res = await getCommentPage({
      page: pagination.page,
      size: pagination.pageSize,
      siteId: query.siteId || undefined,
      status: query.status != null ? query.status : undefined,
      keyword: query.keyword || undefined
    })
    tableData.value = res.records
    pagination.itemCount = res.total
  } finally {
    loading.value = false
  }
}

async function handleStatusChange(row, val) {
  const newStatus = val ? 1 : 0
  await updateCommentStatus(row.id, newStatus)
  row.status = newStatus
  window.$message?.success(val ? '已通过审核' : '已设为待审核')
}

function handleReply(row) {
  modalMode.value = 'reply'
  modalRecord.value = row
  modalVisible.value = true
}

function handleEdit(row) {
  modalMode.value = 'edit'
  modalRecord.value = row
  modalVisible.value = true
}

async function handleDelete(id) {
  await deleteComment(id)
  window.$message?.success('删除成功')
  fetchData()
}

function handleBatchDelete() {
  if (checkedRowKeys.value.length === 0) return
  window.$dialog?.warning({
    title: '批量删除',
    content: `确认删除选中的 ${checkedRowKeys.value.length} 条评论？关联的子评论也会被一并删除。`,
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      await deleteCommentBatch(checkedRowKeys.value)
      window.$message?.success('删除成功')
      checkedRowKeys.value = []
      fetchData()
    }
  })
}

async function handleBatchApprove() {
  if (checkedRowKeys.value.length === 0) return
  window.$dialog?.info({
    title: '批量审核',
    content: `确认将选中的 ${checkedRowKeys.value.length} 条评论设为已通过？`,
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      for (const id of checkedRowKeys.value) {
        await updateCommentStatus(id, 1)
      }
      window.$message?.success('批量审核完成')
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
  query.status = null
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
