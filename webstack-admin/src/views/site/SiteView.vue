<template>
  <div>
    <n-space vertical :size="16">
      <n-space justify="space-between">
        <n-h2>网站管理</n-h2>
        <n-space>
          <n-button circle quaternary @click="fetchData" title="刷新">
            <template #icon>
              <n-icon><span style="font-size: 16px">↻</span></n-icon>
            </template>
          </n-button>
          <n-dropdown
            :options="densityOptions"
            @select="handleDensitySelect"
            trigger="click"
          >
            <n-button circle quaternary title="密度">
              <template #icon>
                <n-icon><span style="font-size: 16px">≡</span></n-icon>
              </template>
            </n-button>
          </n-dropdown>
          <n-popover trigger="click" placement="bottom-end" :width="200">
            <template #trigger>
              <n-button circle quaternary title="列设置">
                <template #icon>
                  <n-icon><span style="font-size: 16px">⚙</span></n-icon>
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
                  <span style="cursor: move; color: #999; margin-right: 8px; user-select: none;">⋮⋮</span>
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
          <n-button type="primary" @click="handleAdd">新增网站</n-button>
        </n-space>
      </n-space>
      <n-space align="center">
        <n-input
          v-model:value="queryForm.title"
          placeholder="按网站名称搜索"
          clearable
          style="width: 220px"
        />
        <n-select
          v-model:value="queryForm.categoryId"
          :options="categoryOptions"
          clearable
          placeholder="所属分类"
          style="width: 180px"
        />
        <n-select
          v-model:value="queryForm.visible"
          :options="[
            { label: '上架', value: 1 },
            { label: '下架', value: 0 }
          ]"
          clearable
          placeholder="状态"
          style="width: 120px"
        />
        <n-select
          v-model:value="queryForm.isRecommended"
          :options="[
            { label: '推荐', value: 1 },
            { label: '普通', value: 0 }
          ]"
          clearable
          placeholder="推荐"
          style="width: 120px"
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
        @update:sorter="handleSorterChange"
        flex-height
        :scroll-x="tableScrollX"
        style="height: calc(100vh - 260px)"
      />
    </n-space>

    <SiteFormModal
      v-model:show="showModal"
      :record="editRecord"
      :category-options="categoryOptions"
      @success="fetchData"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, h, computed, watch } from 'vue'
import {
  NButton,
  NSpace,
  NH2,
  NDataTable,
  NSwitch,
  NInput,
  NSelect,
  NPopconfirm,
  NTag,
  NIcon,
  NPopover,
  NDropdown,
  NCheckbox,
  NCheckboxGroup,
  NDivider,
  NImage
} from 'naive-ui'
import { createActionColumn } from '../../utils/tableActions'
import { getImageUrl } from '../../utils/image'
import {
  getSitePage,
  deleteSite,
  deleteSiteBatch,
  updateSite
} from '../../api/site.js'
import { getCategoryTree } from '../../api/category.js'
import SiteFormModal from './SiteFormModal.vue'
import { useSiteQueryStore } from '../../store/queryState.js'

const queryStore = useSiteQueryStore()

const tableData = ref([])
const showModal = ref(false)
const editRecord = ref(null)
const checkedRowKeys = ref([])
const showSelection = ref(false)
const categoryMap = ref({})
const categoryOptions = ref([])

const tableSize = ref('medium')

const colWidths = {
  id: 60,
  title: 150,
  categoryId: 100,
  tagNameList: 100,
  description: 150,
  likeCount: 85,
  commentCount: 85,
  clickCount: 85,
  isRecommended: 90,
  sort: 75,
  visible: 90,
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

const densityOptions = [
  { label: '紧凑', key: 'small' },
  { label: '中等', key: 'medium' },
  { label: '宽松', key: 'large' }
]

const allColumnMetas = [
  { key: 'id', title: 'ID' },
  { key: 'title', title: '网站名称' },
  { key: 'categoryId', title: '分类' },
  { key: 'tagNameList', title: '标签' },
  { key: 'description', title: '简介' },
  { key: 'likeCount', title: '点赞' },
  { key: 'commentCount', title: '评论' },
  { key: 'clickCount', title: '点击' },
  { key: 'isRecommended', title: '推荐' },
  { key: 'sort', title: '排序' },
  { key: 'visible', title: '状态' },
  { key: 'createTime', title: '创建时间' }
]

const storedVisible = localStorage.getItem('site-visible-keys')
const storedOrder = localStorage.getItem('site-column-order')
const defaultKeys = allColumnMetas.map(m => m.key)
const visibleKeys = ref(storedVisible ? JSON.parse(storedVisible) : defaultKeys)
const columnOrder = ref(storedOrder ? JSON.parse(storedOrder) : defaultKeys)

watch(visibleKeys, (val) => {
  localStorage.setItem('site-visible-keys', JSON.stringify(val))
}, { deep: true })

watch(columnOrder, (val) => {
  localStorage.setItem('site-column-order', JSON.stringify(val))
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

const sortField = ref('')
const sortOrder = ref('')

const handleSorterChange = (sorter) => {
  if (!sorter || sorter.order === false) {
    sortField.value = ''
    sortOrder.value = ''
  } else {
    sortField.value = sorter.columnKey
    sortOrder.value = sorter.order
  }
  fetchData()
}

const pagination = ref({
  page: queryStore.page,
  pageSize: queryStore.pageSize,
  pageCount: 1,
  itemCount: 0,
  showSizePicker: true,
  pageSizes: [10, 20, 50]
})

const queryForm = ref({
  title: queryStore.title,
  categoryId: queryStore.categoryId,
  visible: queryStore.visible,
  isRecommended: queryStore.isRecommended
})

const activeQuery = ref({
  title: queryStore.title,
  categoryId: queryStore.categoryId,
  visible: queryStore.visible,
  isRecommended: queryStore.isRecommended
})

const fetchCategories = async () => {
  const data = await getCategoryTree()
  categoryOptions.value = flattenTree(data || [])
  const map = {}
  const walk = (list) => {
    for (const item of list) {
      map[item.id] = item.title
      if (item.children?.length) walk(item.children)
    }
  }
  walk(data || [])
  categoryMap.value = map
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

const fetchData = async () => {
  const params = {
    page: pagination.value.page,
    size: pagination.value.pageSize,
    title: activeQuery.value.title,
    categoryId: activeQuery.value.categoryId,
    visible: activeQuery.value.visible,
    isRecommended: activeQuery.value.isRecommended,
    sortField: sortField.value,
    sortOrder: sortOrder.value
  }
  const res = await getSitePage(params)
  tableData.value = res.records || []
  pagination.value.itemCount = res.total || 0
  pagination.value.pageCount = res.pages || 1
}

const handlePageChange = (page) => {
  pagination.value.page = page
  queryStore.page = page
  fetchData()
}

const handlePageSizeChange = (pageSize) => {
  pagination.value.pageSize = pageSize
  pagination.value.page = 1
  queryStore.pageSize = pageSize
  queryStore.page = 1
  fetchData()
}

const formatDate = (datetimeStr) => {
  if (!datetimeStr) return '-'
  const [y, m, d] = datetimeStr.split(/[ T]/)[0].split('-')
  return `${y}年${m}月${d}日`
}

onMounted(() => {
  fetchCategories()
  fetchData()
})

const handleAdd = () => {
  editRecord.value = null
  showModal.value = true
}

const handleEdit = (row) => {
  editRecord.value = { ...row }
  showModal.value = true
}

const handleDelete = async (row) => {
  await deleteSite(row.id)
  window.$message?.success('删除成功')
  fetchData()
}

const handleToggleVisible = async (row) => {
  const next = row.visible === 1 ? 0 : 1
  await updateSite({ id: row.id, visible: next })
  window.$message?.success('状态更新成功')
  fetchData()
}

const handleToggleRecommend = async (row) => {
  const next = row.isRecommended === 1 ? 0 : 1
  await updateSite({ id: row.id, isRecommended: next })
  window.$message?.success('推荐状态更新成功')
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
  await deleteSiteBatch(checkedRowKeys.value)
  window.$message?.success('批量删除成功')
  checkedRowKeys.value = []
  showSelection.value = false
  fetchData()
}

const handleQuery = () => {
  activeQuery.value = { ...queryForm.value }
  pagination.value.page = 1
  
  queryStore.$patch({
    ...activeQuery.value,
    page: 1
  })
  
  fetchData()
}

const handleReset = () => {
  queryForm.value = { title: '', categoryId: null, visible: null, isRecommended: null }
  activeQuery.value = { title: '', categoryId: null, visible: null, isRecommended: null }
  pagination.value.page = 1
  
  queryStore.$patch({
    title: '',
    categoryId: null,
    visible: null,
    isRecommended: null,
    page: 1
  })
  
  fetchData()
}

const columns = computed(() => {
  const colMap = {
    id: { title: 'ID', key: 'id', width: 50 },
    title: {
      title: '网站名称',
      key: 'title',
      minWidth: 120,
      sorter: 'default',
      render(row) {
        return h(NSpace, { align: 'center', size: 8 }, {
          default: () => [
            h(NImage, {
              src: getImageUrl(row.thumb),
              width: 28,
              height: 28,
              style: 'border-radius: 4px;',
              objectFit: 'cover',
              previewDisabled: true,
              fallbackSrc: ''
            }),
            h('span', {}, row.title)
          ]
        })
      }
    },
    categoryId: {
      title: '分类',
      key: 'categoryId',
      minWidth: 90,
      sorter: 'default',
      render(row) {
        return categoryMap.value[row.categoryId] || '-'
      }
    },
    tagNameList: {
      title: '标签',
      key: 'tagNameList',
      minWidth: 90,
      render(row) {
        const tags = row.tagNameList || []
        return h(NSpace, { size: 4 }, {
          default: () => tags.slice(0, 3).map(name =>
            h(NTag, { size: 'small', bordered: false }, { default: () => name })
          ).concat(tags.length > 3 ? h('span', {}, '...') : [])
        })
      }
    },
    description: { title: '简介', key: 'description', minWidth: 120, ellipsis: { tooltip: true } },
    likeCount: { title: '点赞', key: 'likeCount', width: 85, minWidth: 85, sorter: 'default' },
    commentCount: { title: '评论', key: 'commentCount', width: 85, minWidth: 85, sorter: 'default' },
    clickCount: { title: '点击', key: 'clickCount', width: 85, minWidth: 85, sorter: 'default' },
    isRecommended: {
      title: '推荐',
      key: 'isRecommended',
      width: 75,
      render(row) {
        return h(NSwitch, {
          value: row.isRecommended === 1,
          onUpdateValue: () => handleToggleRecommend(row)
        })
      }
    },
    sort: { title: '排序', key: 'sort', width: 75 },
    visible: {
      title: '状态',
      key: 'visible',
      width: 75,
      render(row) {
        return h(NSwitch, {
          value: row.visible === 1,
          onUpdateValue: () => handleToggleVisible(row)
        })
      }
    },
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
    deleteConfirm: '确认删除该网站？',
    width: 110
  }))

  if (showSelection.value) {
    base.unshift({ type: 'selection', width: 40 })
  }
  return base
})
</script>
