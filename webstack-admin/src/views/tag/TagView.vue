<template>
  <div>
    <n-space vertical :size="16">
      <n-space justify="space-between">
        <n-h2>标签管理</n-h2>
        <n-space>
          <n-button circle quaternary @click="fetchData" title="刷新">
            <template #icon>
              <n-icon :component="RefreshCw" :stroke-width="1.5" size="16" />
            </template>
          </n-button>
          
          <n-dropdown :options="densityOptions" @select="handleDensitySelect" trigger="click">
            <n-button circle quaternary title="密度">
              <template #icon>
                <n-icon :component="Menu" :stroke-width="1.5" size="16" />
              </template>
            </n-button>
          </n-dropdown>

          <n-popover trigger="click" placement="bottom-end" :width="200">
            <template #trigger>
              <n-button circle quaternary title="列设置">
                <template #icon>
                  <n-icon :component="Settings2" :stroke-width="1.5" size="16" />
                </template>
              </n-button>
            </template>
            <div>
              <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px;">
                <span style="font-weight: 500">列展示</span>
                <n-button text size="small" @click="resetColumns">重置</n-button>
              </div>
              <n-checkbox-group v-model:value="visibleKeys">
                <div
                  v-for="key in columnOrder"
                  :key="key"
                  draggable="true"
                  @dragstart="handleDragStart(key)"
                  @dragover="handleDragOver($event, key)"
                  style="display: flex; align-items: center; padding: 4px 0;"
                >
                  <n-icon :component="GripVertical" size="14" style="cursor: move; color: #999; margin-right: 8px;" />
                  <n-checkbox :value="key" :label="allColumnMetas.find(m => m.key === key).title" />
                </div>
              </n-checkbox-group>
            </div>
          </n-popover>

          <n-divider vertical style="margin: 0 4px;" />

          <n-button
            v-if="!showSelection"
            type="error"
            @click="enterBatchMode"
          >
            批量删除
          </n-button>
          <template v-else>
            <n-button
              type="error"
              :disabled="!checkedRowKeys.length"
              @click="confirmBatchDelete"
            >
              确认删除 ({{ checkedRowKeys.length }})
            </n-button>
            <n-button @click="cancelBatchMode">取消</n-button>
          </template>
          <n-button type="primary" @click="handleAdd">新增标签</n-button>
        </n-space>
      </n-space>

      <n-space align="center">
        <n-input
          v-model:value="queryForm.tagName"
          placeholder="按标签名搜索..."
          clearable
          style="width: 240px"
        />
        <n-button type="primary" @click="handleQuery">查询</n-button>
        <n-button @click="handleReset">重置</n-button>
      </n-space>

      <n-data-table
        :columns="columns"
        :data="tableData"
        :row-key="(row) => row.id"
        v-model:checked-row-keys="checkedRowKeys"
        :pagination="pagination"
        :size="tableSize"
        remote
        @update:page="handlePageChange"
        @update:page-size="handlePageSizeChange"
        flex-height
        :scroll-x="tableScrollX"
        style="height: calc(100vh - 260px)"
      />
    </n-space>

    <TagFormModal
      v-model:show="showModal"
      :record="editRecord"
      @success="fetchData"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, h, computed, watch } from 'vue'
import { RefreshCw, Menu, Settings2, GripVertical } from 'lucide-vue-next'
import {
  NSpace,
  NButton,
  NInput,
  NDataTable,
  NPopconfirm,
  NIcon,
  NPopover,
  NDropdown,
  NCheckbox,
  NCheckboxGroup,
  NH2,
  NDivider
} from 'naive-ui'
import { createActionColumn } from '../../utils/tableActions'
import {
  getTagPage,
  deleteTag,
  deleteTagBatch
} from '../../api/tag.js'
import TagFormModal from './TagFormModal.vue'

const tableData = ref([])
const showModal = ref(false)
const editRecord = ref(null)
const checkedRowKeys = ref([])
const showSelection = ref(false)

const colWidths = {
  id: 60,
  tagName: 150,
  createTime: 140
}

const tableScrollX = computed(() => {
  let totalWidth = 0
  columnOrder.value
    .filter(key => visibleKeys.value.includes(key))
    .forEach(key => {
      totalWidth += colWidths[key] || 100
    })
  totalWidth += 110 // Actions column width (minWidth)
  if (showSelection.value) {
    totalWidth += 40 // Selection column width
  }
  return totalWidth
})

const tableSize = ref('medium')

const densityOptions = [
  { label: '紧凑', key: 'small' },
  { label: '中等', key: 'medium' },
  { label: '宽松', key: 'large' }
]

const allColumnMetas = [
  { key: 'id', title: 'ID' },
  { key: 'tagName', title: '标签名' },
  { key: 'createTime', title: '创建时间' }
]

const storedVisible = localStorage.getItem('tag-visible-keys')
const storedOrder = localStorage.getItem('tag-column-order')
const defaultKeys = allColumnMetas.map(m => m.key)
const visibleKeys = ref(storedVisible ? JSON.parse(storedVisible) : defaultKeys)
const columnOrder = ref(storedOrder ? JSON.parse(storedOrder) : defaultKeys)

watch(visibleKeys, (val) => {
  localStorage.setItem('tag-visible-keys', JSON.stringify(val))
}, { deep: true })

watch(columnOrder, (val) => {
  localStorage.setItem('tag-column-order', JSON.stringify(val))
}, { deep: true })

const handleDensitySelect = (key) => {
  tableSize.value = key
}

const resetColumns = () => {
  visibleKeys.value = allColumnMetas.map(m => m.key)
  columnOrder.value = allColumnMetas.map(m => m.key)
}

const dragKey = ref(null)

const handleDragStart = (key) => {
  dragKey.value = key
}

const handleDragOver = (e, key) => {
  e.preventDefault()
  if (!dragKey.value || dragKey.value === key) return
  const fromIndex = columnOrder.value.indexOf(dragKey.value)
  const toIndex = columnOrder.value.indexOf(key)
  if (fromIndex === -1 || toIndex === -1) return
  const newOrder = [...columnOrder.value]
  newOrder.splice(fromIndex, 1)
  newOrder.splice(toIndex, 0, dragKey.value)
  columnOrder.value = newOrder
}

const pagination = ref({
  page: 1,
  pageSize: 10,
  pageCount: 1,
  itemCount: 0,
  showSizePicker: true,
  pageSizes: [10, 20, 50]
})

const queryForm = ref({
  tagName: ''
})

const activeQuery = ref({
  tagName: ''
})

const fetchData = async () => {
  const params = {
    page: pagination.value.page,
    size: pagination.value.pageSize,
    tagName: activeQuery.value.tagName
  }
  const res = await getTagPage(params)
  tableData.value = res.records || []
  pagination.value.itemCount = res.total || 0
  pagination.value.pageCount = res.pages || 1
}

const handlePageChange = (page) => {
  pagination.value.page = page
  fetchData()
}

const handlePageSizeChange = (pageSize) => {
  pagination.value.pageSize = pageSize
  pagination.value.page = 1
  fetchData()
}

const formatDate = (datetimeStr) => {
  if (!datetimeStr) return '-'
  const [y, m, d] = datetimeStr.split(/[ T]/)[0].split('-')
  return `${y}年${m}月${d}日`
}

onMounted(fetchData)

const handleAdd = () => {
  editRecord.value = null
  showModal.value = true
}

const handleEdit = (row) => {
  editRecord.value = { ...row }
  showModal.value = true
}

const handleDelete = async (row) => {
  await deleteTag(row.id)
  window.$message?.success('删除成功')
  fetchData()
}

const enterBatchMode = () => {
  showSelection.value = true
}

const cancelBatchMode = () => {
  showSelection.value = false
  checkedRowKeys.value = []
}

const confirmBatchDelete = async () => {
  if (!checkedRowKeys.value.length) return
  await deleteTagBatch(checkedRowKeys.value)
  window.$message?.success('批量删除成功')
  checkedRowKeys.value = []
  showSelection.value = false
  fetchData()
}

const handleQuery = () => {
  activeQuery.value = { ...queryForm.value }
  pagination.value.page = 1
  fetchData()
}

const handleReset = () => {
  queryForm.value = { tagName: '' }
  activeQuery.value = { tagName: '' }
  pagination.value.page = 1
  fetchData()
}

const columns = computed(() => {
  const colMap = {
    id: { title: 'ID', key: 'id', width: 50 },
    tagName: { title: '标签名', key: 'tagName', minWidth: 120 },
    createTime: {
      title: '创建时间',
      key: 'createTime',
      minWidth: 120,
      render(row) { return formatDate(row.createTime) }
    }
  }

  const ordered = columnOrder.value
    .filter(key => visibleKeys.value.includes(key))
    .map(key => colMap[key])

  const base = [...ordered]

  base.push(createActionColumn({
    onEdit: handleEdit,
    onDelete: handleDelete,
    deleteConfirm: '确认删除该标签？',
    width: 110
  }))

  if (showSelection.value) {
    base.unshift({ type: 'selection', width: 40 })
  }
  return base
})
</script>
