<template>
  <div>
    <n-space vertical :size="16">
      <n-space justify="space-between">
        <n-h2>分类管理</n-h2>
        <n-space>
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
          <n-button type="primary" @click="handleAdd">新增分类</n-button>
        </n-space>
      </n-space>
      <n-space align="center">
        <n-input
          v-model:value="queryForm.keyword"
          placeholder="按分类名/英文名搜索"
          clearable
          style="width: 260px"
        />
        <n-select
          v-model:value="queryForm.visible"
          :options="[
            { label: '显示', value: 1 },
            { label: '隐藏', value: 0 }
          ]"
          clearable
          placeholder="状态"
          style="width: 120px"
        />
        <n-date-picker
          v-model:value="queryForm.date"
          type="date"
          clearable
          placeholder="选择日期"
        />
        <n-button type="primary" @click="handleQuery">查询</n-button>
        <n-button @click="handleReset">重置</n-button>
      </n-space>
      <n-data-table
        :columns="columns"
        :data="filteredTreeData"
        :row-key="(row) => row.id"
        v-model:checked-row-keys="checkedRowKeys"
        default-expand-all
        :scroll-x="tableScrollX"
      />
    </n-space>

    <CategoryFormModal
      v-model:show="showModal"
      :record="editRecord"
      :tree-options="treeOptions"
      @success="fetchData"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, h, computed } from 'vue'
import {
  NButton,
  NSpace,
  NH2,
  NDataTable,
  NSwitch,
  NPopconfirm,
  NInput,
  NSelect,
  NDatePicker,
  NImage
} from 'naive-ui'
import { createActionColumn } from '../../utils/tableActions'
import { getImageUrl } from '../../utils/image'
import {
  getCategoryTree,
  deleteCategory,
  updateCategory,
  deleteCategoryBatch
} from '../../api/category.js'
import CategoryFormModal from './CategoryFormModal.vue'

const treeData = ref([])
const showModal = ref(false)
const editRecord = ref(null)
const treeOptions = ref([])
const checkedRowKeys = ref([])
const showSelection = ref(false)

const tableScrollX = computed(() => {
  let width = 120 + 100 + 60 + 60 + 120 + 120 + 80 + 110 // Base minimum width = 770
  if (showSelection.value) {
    width += 40
  }
  return width
})

const queryForm = ref({
  keyword: '',
  visible: null,
  date: null
})

const activeQuery = ref({
  keyword: '',
  visible: null,
  date: null
})

const fetchData = async () => {
  const data = await getCategoryTree()
  treeData.value = data || []
  treeOptions.value = flattenTree(data || [])
}

const formatDate = (datetimeStr) => {
  if (!datetimeStr) return '-'
  const [y, m, d] = datetimeStr.split(/[ T]/)[0].split('-')
  return `${y}年${m}月${d}日`
}

const filterTree = (nodes) => {
  const keyword = activeQuery.value.keyword.trim().toLowerCase()
  const visible = activeQuery.value.visible
  const date = activeQuery.value.date

  if (!keyword && visible === null && !date) return nodes

  const pickDate = (str) => str ? str.split(' ')[0] : null
  const queryDate = date ? new Date(date).toISOString().split('T')[0] : null

  return nodes.reduce((acc, node) => {
    const matchKeyword = !keyword ||
      (node.title || '').toLowerCase().includes(keyword) ||
      (node.enTitle || '').toLowerCase().includes(keyword)

    const matchVisible = visible === null || node.visible === visible

    const nodeCreateDate = pickDate(node.createTime)
    const nodeUpdateDate = pickDate(node.updateTime)
    const matchDate = !queryDate || nodeCreateDate === queryDate || nodeUpdateDate === queryDate

    const filteredChildren = node.children?.length ? filterTree(node.children) : []

    if ((matchKeyword && matchVisible && matchDate) || filteredChildren.length) {
      acc.push({ ...node, children: filteredChildren })
    }
    return acc
  }, [])
}

const filteredTreeData = computed(() => filterTree(treeData.value))

const handleQuery = () => {
  activeQuery.value = { ...queryForm.value }
}

const handleReset = () => {
  queryForm.value = { keyword: '', visible: null, date: null }
  activeQuery.value = { keyword: '', visible: null, date: null }
}

const flattenTree = (list) => {
  const result = []
  const queue = list.map(item => ({ node: item, level: 0 }))
  while (queue.length) {
    const { node, level } = queue.shift()
    result.push({ label: '  '.repeat(level) + node.title, value: node.id })
    if (node.children?.length) {
      queue.push(...node.children.map(child => ({ node: child, level: level + 1 })))
    }
  }
  return result
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
  await deleteCategory(row.id)
  window.$message?.success('删除成功')
  fetchData()
}

const handleToggleVisible = async (row) => {
  const next = row.visible === 1 ? 0 : 1
  await updateCategory({ ...row, visible: next })
  window.$message?.success('状态更新成功')
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
  await deleteCategoryBatch(checkedRowKeys.value)
  window.$message?.success('批量删除成功')
  checkedRowKeys.value = []
  showSelection.value = false
  fetchData()
}

const columns = computed(() => {
  const base = [
    { title: '分类名', key: 'title', minWidth: 120 },
    { title: '英文名', key: 'enTitle', minWidth: 100 },
    { title: '图标', key: 'icon', width: 60, render(row) {
      if (row.icon && row.icon.startsWith('ri-')) {
        return h('i', { class: row.icon, style: 'font-size: 24px; color: #555;' })
      }
      return h(NImage, {
        src: getImageUrl(row.icon),
        width: 40,
        height: 40,
        style: 'border-radius: 4px;',
        objectFit: 'cover',
        previewDisabled: true,
        fallbackSrc: ''
      })
    }},
    { title: '排序', key: 'sort', width: 60 },
    {
      title: '创建时间',
      key: 'createTime',
      minWidth: 120,
      render(row) { return formatDate(row.createTime) }
    },
    {
      title: '修改时间',
      key: 'updateTime',
      minWidth: 120,
      render(row) { return formatDate(row.updateTime) }
    },
    {
      title: '显示状态',
      key: 'visible',
      width: 80,
      render(row) {
        return h(NSwitch, {
          value: row.visible === 1,
          onUpdateValue: () => handleToggleVisible(row)
        })
      }
    },
    createActionColumn({
      onEdit: handleEdit,
      onDelete: handleDelete,
      deleteConfirm: '确认删除该分类？',
      width: 110
    })
  ]
  if (showSelection.value) {
    base.unshift({ type: 'selection', width: 40 })
  }
  return base
})
</script>
