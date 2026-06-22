<template>
  <div class="site-section-detail-view">
    <n-space vertical :size="20">
      <!-- 页面头部 -->
      <n-space justify="space-between" align="center" class="detail-header">
        <n-space align="center">
          <n-button secondary @click="goBack">
            ← 返回卡片列表
          </n-button>
          <n-divider vertical />
          <n-avatar
            v-if="siteInfo?.thumb"
            :src="getImageUrl(siteInfo.thumb)"
            :size="36"
            round
            fallback-src=""
          />
          <span class="site-name">{{ siteInfo?.title || '加载中...' }} - 小节内容配置</span>
        </n-space>
        <span style="color: #999; font-size: 13px;">编辑该网站的具体功能小节和分段内容</span>
      </n-space>

      <!-- 双栏布局：左侧小节，右侧内容 -->
      <n-grid cols="24" :x-gap="16" item-responsive>
        <!-- 左侧栏：小节树/列表 -->
        <n-grid-item span="6">
          <n-card title="页面小节目录" size="small" class="sidebar-card">
            <template #header-extra>
              <n-button size="tiny" type="primary" @click="openSectionModal(null)">
                + 新增小节
              </n-button>
            </template>
            
            <n-spin :show="sectionsLoading">
              <div v-if="sections.length === 0" class="empty-placeholder">
                <n-empty description="暂无小节目录" />
              </div>
              
              <n-list v-else hoverable clickable class="section-list">
                <n-list-item
                  v-for="(sec, idx) in sections"
                  :key="sec.id"
                  :class="['section-item', { active: activeSectionId === sec.id }]"
                  @click="selectSection(sec.id)"
                >
                  <div class="section-item-content">
                    <span class="section-title" :title="sec.title">
                      {{ sec.title }}
                    </span>
                    
                    <!-- 小节操作按钮 -->
                    <div class="section-actions" @click.stop>
                      <n-button text type="primary" size="tiny" @click="openSectionModal(sec)">
                        改名
                      </n-button>
                      <n-button text type="error" size="tiny" @click="handleDeleteSection(sec.id)">
                        删除
                      </n-button>
                      <!-- 排序按钮 -->
                      <n-button text :disabled="idx === 0" size="tiny" @click="moveSection(idx, -1)">
                        ↑
                      </n-button>
                      <n-button text :disabled="idx === sections.length - 1" size="tiny" @click="moveSection(idx, 1)">
                        ↓
                      </n-button>
                    </div>
                  </div>
                </n-list-item>
              </n-list>
            </n-spin>
          </n-card>
        </n-grid-item>

        <!-- 右侧栏：小节具体内容列表 -->
        <n-grid-item span="18">
          <n-card :title="activeSectionTitle ? `【${activeSectionTitle}】小节内容列表` : '小节详细内容'" size="small" class="content-card">
            <template #header-extra>
              <n-dropdown
                v-if="activeSectionId"
                trigger="click"
                :options="addContentOptions"
                @select="handleSelectAddContentType"
              >
                <n-button size="small" type="primary">
                  + 添加分段内容
                </n-button>
              </n-dropdown>
            </template>

            <n-spin :show="contentsLoading">
              <div v-if="!activeSectionId" class="empty-placeholder">
                <n-empty description="请选择左侧的小节查看详细内容" />
              </div>
              <div v-else-if="contents.length === 0" class="empty-placeholder">
                <n-empty description="当前小节尚无内容，请点击右上角添加" />
              </div>
              
              <!-- 内容渲染列表 -->
              <div v-else class="content-list">
                <n-card
                  v-for="(content, idx) in contents"
                  :key="content.id"
                  size="small"
                  class="content-item-card"
                  hoverable
                >
                  <!-- 顶部类型标签与操作 -->
                  <div class="content-item-header">
                    <n-space align="center">
                      <n-tag :type="getTypeTagType(content.contentType)" size="small">
                        {{ getContentTypeLabel(content.contentType) }}
                      </n-tag>
                      <span class="sort-tag">排序值: {{ content.sort }}</span>
                    </n-space>
                    
                    <n-space size="small">
                      <n-button size="tiny" @click="openContentFormModal(content)">
                        编辑
                      </n-button>
                      <n-button size="tiny" type="error" @click="handleDeleteContent(content.id)">
                        删除
                      </n-button>
                      <n-button size="tiny" :disabled="idx === 0" @click="moveContent(idx, -1)">
                        上移
                      </n-button>
                      <n-button size="tiny" :disabled="idx === contents.length - 1" @click="moveContent(idx, 1)">
                        下移
                      </n-button>
                    </n-space>
                  </div>

                  <!-- 渲染层：根据 contentType 渲染 -->
                  <div class="content-render-body">
                    <!-- Paragraph 段落 -->
                    <div v-if="content.contentType === 'paragraph'" class="render-paragraph">
                      {{ content.textContent }}
                    </div>

                    <!-- Image 图片 -->
                    <div v-else-if="content.contentType === 'image'" class="render-image">
                      <n-space vertical :size="8">
                        <div class="image-wrapper">
                          <img
                            :src="getImageUrl(safeParseJson(content.jsonData).url)"
                            :alt="safeParseJson(content.jsonData).alt || '图片'"
                            :style="{
                              maxWidth: '100%',
                              maxHeight: '300px',
                              borderRadius: '4px',
                              boxShadow: '0 2px 8px rgba(0,0,0,0.1)'
                            }"
                          />
                        </div>
                        <div class="image-caption" v-if="content.textContent">
                          🖼️ 图片说明：{{ content.textContent }}
                        </div>
                      </n-space>
                    </div>

                    <!-- ListItem 列表 -->
                    <div v-else-if="content.contentType === 'list_item'" class="render-list">
                      <h4 class="list-title" v-if="content.textContent">{{ content.textContent }}</h4>
                      <ul class="list-ul">
                        <li v-for="(item, i) in safeParseJson(content.jsonData).items" :key="i">
                          • {{ item }}
                        </li>
                      </ul>
                    </div>

                    <!-- Table 表格 -->
                    <div v-else-if="content.contentType === 'table'" class="render-table">
                      <h4 class="table-title" v-if="content.textContent">{{ content.textContent }}</h4>
                      <n-table :bordered="true" :single-line="false" size="small" class="custom-render-table">
                        <thead>
                          <tr>
                            <th v-for="(h, i) in safeParseJson(content.jsonData).headers" :key="i">
                              {{ h }}
                            </th>
                          </tr>
                        </thead>
                        <tbody>
                          <tr v-for="(row, rIdx) in safeParseJson(content.jsonData).rows" :key="rIdx">
                            <td v-for="(cell, cIdx) in row" :key="cIdx">
                              {{ cell }}
                            </td>
                          </tr>
                        </tbody>
                      </n-table>
                    </div>
                  </div>
                </n-card>
              </div>
            </n-spin>
          </n-card>
        </n-grid-item>
      </n-grid>
    </n-space>

    <!-- 弹窗1：小节修改/新增弹窗 -->
    <n-modal
      v-model:show="showSectionModal"
      preset="dialog"
      :title="editingSection?.id ? '修改小节名称' : '新增小节'"
      positive-text="确认"
      negative-text="取消"
      @positive-click="handleSaveSection"
      @negative-click="showSectionModal = false"
    >
      <n-form style="margin-top: 16px;">
        <n-form-item label="小节名称" required>
          <n-input v-model:value="sectionForm.title" placeholder="请输入小节名称" />
        </n-form-item>
        <n-form-item label="排序值">
          <n-input-number v-model:value="sectionForm.sort" :min="0" />
        </n-form-item>
      </n-form>
    </n-modal>

    <!-- 弹窗2：小节内容（段落/图片/列表/表格）新增及修改弹窗 -->
    <n-modal
      v-model:show="showContentFormModal"
      preset="card"
      :title="editingContent?.id ? '编辑分段内容' : '添加分段内容'"
      style="width: 700px;"
      @close="showContentFormModal = false"
    >
      <n-form label-placement="left" label-width="90" style="max-height: 550px; overflow-y: auto; padding-right: 8px;">
        <!-- 只读或不可更改类型 -->
        <n-form-item label="内容类型">
          <n-tag :type="getTypeTagType(contentForm.contentType)">
            {{ getContentTypeLabel(contentForm.contentType) }}
          </n-tag>
        </n-form-item>

        <!-- 排序值 -->
        <n-form-item label="排序值">
          <n-input-number v-model:value="contentForm.sort" :min="0" />
        </n-form-item>

        <!-- 1. 段落表单 -->
        <template v-if="contentForm.contentType === 'paragraph'">
          <n-form-item label="段落文本" required>
            <n-input
              v-model:value="contentForm.textContent"
              type="textarea"
              placeholder="请输入详细的段落文字内容"
              :autosize="{ minRows: 4, maxRows: 8 }"
            />
          </n-form-item>
        </template>

        <!-- 2. 图片表单 -->
        <template v-else-if="contentForm.contentType === 'image'">
          <n-form-item label="图片说明">
            <n-input v-model:value="contentForm.textContent" placeholder="请输入图片下方展示的文字描述（可选）" />
          </n-form-item>
          <n-form-item label="上传图片" required>
            <upload-image v-model:value="contentForm.imageObj.url" />
          </n-form-item>
          <n-form-item label="替代文本">
            <n-input v-model:value="contentForm.imageObj.alt" placeholder="图片加载失败时的替代文字 (Alt)" />
          </n-form-item>
          <n-grid cols="2" :x-gap="12">
            <n-grid-item>
              <n-form-item label="显示宽度">
                <n-input-number v-model:value="contentForm.imageObj.width" placeholder="像素 (px)" :min="1" />
              </n-form-item>
            </n-grid-item>
            <n-grid-item>
              <n-form-item label="显示高度">
                <n-input-number v-model:value="contentForm.imageObj.height" placeholder="像素 (px)" :min="1" />
              </n-form-item>
            </n-grid-item>
          </n-grid>
        </template>

        <!-- 3. 列表项表单 -->
        <template v-else-if="contentForm.contentType === 'list_item'">
          <n-form-item label="列表标题">
            <n-input v-model:value="contentForm.textContent" placeholder="请输入列表的总结/标题（可选）" />
          </n-form-item>
          <n-form-item label="列表项" required>
            <n-space vertical style="width: 100%;">
              <div v-for="(item, idx) in contentForm.listObj.items" :key="idx" style="display: flex; gap: 8px;">
                <n-input v-model:value="contentForm.listObj.items[idx]" :placeholder="`请输入第 ${idx + 1} 项内容`" />
                <n-button type="error" ghost @click="removeListItem(idx)">删除</n-button>
              </div>
              <n-button dashed type="primary" block @click="addListItem">
                + 新增一行列表项
              </n-button>
            </n-space>
          </n-form-item>
        </template>

        <!-- 4. 表格表单 -->
        <template v-else-if="contentForm.contentType === 'table'">
          <n-form-item label="表格标题">
            <n-input v-model:value="contentForm.textContent" placeholder="请输入表格的说明/标题（可选）" />
          </n-form-item>
          
          <!-- 表头动态配置 -->
          <n-form-item label="表格表头" required>
            <n-space vertical style="width: 100%;">
              <div style="display: flex; flex-wrap: wrap; gap: 8px;">
                <div v-for="(h, idx) in contentForm.tableObj.headers" :key="idx" style="display: flex; align-items: center; gap: 4px;">
                  <n-input
                    v-model:value="contentForm.tableObj.headers[idx]"
                    :placeholder="`列 ${idx + 1}`"
                    style="width: 100px;"
                    @update:value="handleHeaderChange"
                  />
                  <n-button size="tiny" type="error" circle @click="removeTableHeader(idx)">-</n-button>
                </div>
                <n-button size="small" dashed type="primary" @click="addTableHeader">
                  + 添加列
                </n-button>
              </div>
            </n-space>
          </n-form-item>

          <!-- 表格行数据动态配置 -->
          <n-form-item label="表格行数据" required>
            <n-space vertical style="width: 100%;">
              <n-table :bordered="true" size="small">
                <thead>
                  <tr>
                    <th v-for="(h, idx) in contentForm.tableObj.headers" :key="idx">
                      {{ h || `列 ${idx+1}` }}
                    </th>
                    <th style="width: 60px;">操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(row, rIdx) in contentForm.tableObj.rows" :key="rIdx">
                    <td v-for="(cell, cIdx) in contentForm.tableObj.headers" :key="cIdx">
                      <n-input v-model:value="contentForm.tableObj.rows[rIdx][cIdx]" placeholder="内容" />
                    </td>
                    <td>
                      <n-button size="tiny" type="error" ghost @click="removeTableRow(rIdx)">
                        删除
                      </n-button>
                    </td>
                  </tr>
                </tbody>
              </n-table>
              <n-button dashed type="primary" block @click="addTableRow" :disabled="contentForm.tableObj.headers.length === 0">
                + 新增一行数据
              </n-button>
            </n-space>
          </n-form-item>
        </template>
      </n-form>

      <template #footer>
        <n-space justify="end">
          <n-button @click="showContentFormModal = false">取消</n-button>
          <n-button type="primary" @click="handleSaveContent">保存</n-button>
        </n-space>
      </template>
    </n-modal>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  NSpace,
  NButton,
  NDivider,
  NAvatar,
  NGrid,
  NGridItem,
  NCard,
  NSpin,
  NEmpty,
  NList,
  NListItem,
  NDropdown,
  NTag,
  NImage,
  NTable,
  NModal,
  NForm,
  NFormItem,
  NInput,
  NInputNumber
} from 'naive-ui'
import { getImageUrl } from '../../utils/image'
import UploadImage from '../../components/UploadImage.vue'
import { getSiteById } from '../../api/site.js'
import {
  getSiteSectionList,
  saveSiteSection,
  updateSiteSection,
  deleteSiteSection
} from '../../api/siteSection.js'
import {
  getSiteSectionContentList,
  saveSiteSectionContent,
  updateSiteSectionContent,
  deleteSiteSectionContent
} from '../../api/siteSectionContent.js'
import { useSiteSectionDetailStore } from '../../store/queryState.js'

const route = useRoute()
const router = useRouter()
const siteId = parseInt(route.params.siteId)
const detailStore = useSiteSectionDetailStore()

// 基础数据状态
const siteInfo = ref(null)
const sections = ref([])
const contents = ref([])

// Loading 状态
const sectionsLoading = ref(false)
const contentsLoading = ref(false)

// 当前选中的小节
const activeSectionId = ref(null)
const activeSectionTitle = ref('')

// 1. 小节新增/改名弹窗控制
const showSectionModal = ref(false)
const editingSection = ref(null)
const sectionForm = ref({
  title: '',
  sort: 1
})

// 2. 小节内容弹窗控制与表单
const showContentFormModal = ref(false)
const editingContent = ref(null)
const contentForm = ref({
  id: null,
  sectionId: null,
  contentType: 'paragraph',
  textContent: '',
  sort: 1,
  // 临时存储 JSON 对象的变量
  imageObj: { url: '', alt: '', width: null, height: null },
  listObj: { items: [''] },
  tableObj: { headers: [''], rows: [['']] }
})

// 添加内容的下拉菜单选项
const addContentOptions = [
  { label: '添加文本段落', key: 'paragraph' },
  { label: '添加图片内容', key: 'image' },
  { label: '添加列表清单', key: 'list_item' },
  { label: '添加数据表格', key: 'table' }
]

// 返回卡片列表
const goBack = () => {
  router.push('/site-section')
}

// 获取网站详情
const fetchSiteInfo = async () => {
  try {
    const res = await getSiteById(siteId)
    siteInfo.value = res
  } catch (error) {
    console.error('获取网站详情失败', error)
  }
}

// 获取小节列表
const fetchSections = async (autoSelect = false) => {
  sectionsLoading.value = true
  try {
    const res = await getSiteSectionList(siteId)
    sections.value = res || []
    
    // 如果需要自动选中第一个小节
    if (autoSelect && sections.value.length > 0) {
      if (detailStore.siteId === siteId && detailStore.activeSectionId) {
        const exists = sections.value.find(s => s.id === detailStore.activeSectionId)
        if (exists) {
          selectSection(exists.id)
        } else {
          selectSection(sections.value[0].id)
        }
      } else {
        selectSection(sections.value[0].id)
      }
    }
  } catch (error) {
    console.error('获取小节列表失败', error)
  } finally {
    sectionsLoading.value = false
  }
}

// 选中某个小节并加载内容
const selectSection = (secId) => {
  activeSectionId.value = secId
  const section = sections.value.find(s => s.id === secId)
  activeSectionTitle.value = section ? section.title : ''
  
  detailStore.$patch({
    siteId: siteId,
    activeSectionId: secId,
    activeSectionTitle: activeSectionTitle.value
  })
  
  fetchContents()
}

// 获取小节内容列表
const fetchContents = async () => {
  if (!activeSectionId.value) return
  contentsLoading.value = true
  try {
    const res = await getSiteSectionContentList(activeSectionId.value)
    contents.value = res || []
  } catch (error) {
    console.error('获取小节内容失败', error)
  } finally {
    contentsLoading.value = false
  }
}

// ---------------- 小节（Section）操作部分 ----------------

// 打开小节新增/修改弹窗
const openSectionModal = (sec = null) => {
  editingSection.value = sec
  if (sec) {
    sectionForm.value = {
      title: sec.title,
      sort: sec.sort
    }
  } else {
    sectionForm.value = {
      title: '',
      sort: sections.value.length + 1
    }
  }
  showSectionModal.value = true
}

// 保存小节
const handleSaveSection = async () => {
  if (!sectionForm.value.title.trim()) {
    window.$message?.warning('小节名称不能为空')
    return false
  }
  
  try {
    if (editingSection.value?.id) {
      // 更新小节
      await updateSiteSection({
        id: editingSection.value.id,
        siteId: siteId,
        title: sectionForm.value.title,
        sort: sectionForm.value.sort
      })
      window.$message?.success('小节名称修改成功')
    } else {
      // 新增小节
      await saveSiteSection({
        siteId: siteId,
        title: sectionForm.value.title,
        sort: sectionForm.value.sort
      })
      window.$message?.success('新增小节成功')
    }
    showSectionModal.value = false
    fetchSections()
  } catch (error) {
    console.error('保存小节失败', error)
    window.$message?.error('操作失败')
  }
  return true
}

// 删除小节
const handleDeleteSection = (secId) => {
  window.$dialog?.warning({
    title: '警告',
    content: '确定要删除该小节吗？删除小节将会同时级联删除小节下的所有分段内容！',
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await deleteSiteSection(secId)
        window.$message?.success('删除成功')
        if (activeSectionId.value === secId) {
          activeSectionId.value = null
          activeSectionTitle.value = ''
          contents.value = []
        }
        fetchSections()
      } catch (error) {
        console.error('删除小节失败', error)
        window.$message?.error('删除失败')
      }
    }
  })
}

// 移动小节排序
const moveSection = async (index, direction) => {
  const targetIndex = index + direction
  if (targetIndex < 0 || targetIndex >= sections.value.length) return
  
  const current = sections.value[index]
  const target = sections.value[targetIndex]
  
  // 交换 sort 值
  const tempSort = current.sort
  current.sort = target.sort
  target.sort = tempSort
  
  try {
    await updateSiteSection({ id: current.id, sort: current.sort })
    await updateSiteSection({ id: target.id, sort: target.sort })
    fetchSections()
  } catch (e) {
    console.error('更新小节排序失败', e)
  }
}

// ---------------- 小节内容（Content）操作部分 ----------------

// 选择添加的分段内容类型
const handleSelectAddContentType = (type) => {
  editingContent.value = null
  
  // 初始化不同类型的表单对象
  contentForm.value = {
    id: null,
    sectionId: activeSectionId.value,
    contentType: type,
    textContent: '',
    sort: contents.value.length + 1,
    imageObj: { url: '', alt: '', width: null, height: null },
    listObj: { items: [''] },
    tableObj: { headers: ['列 1', '列 2'], rows: [['', '']] }
  }
  showContentFormModal.value = true
}

// 打开小节内容编辑表单弹窗
const openContentFormModal = (item) => {
  editingContent.value = item
  const parsedJson = safeParseJson(item.jsonData)
  
  contentForm.value = {
    id: item.id,
    sectionId: item.sectionId,
    contentType: item.contentType,
    textContent: item.textContent || '',
    sort: item.sort,
    // 解析对应类型的 JSON 字段
    imageObj: item.contentType === 'image' ? { ...parsedJson } : { url: '', alt: '', width: null, height: null },
    listObj: item.contentType === 'list_item' ? { ...parsedJson } : { items: [''] },
    tableObj: item.contentType === 'table' ? { ...parsedJson } : { headers: [''], rows: [['']] }
  }
  showContentFormModal.value = true
}

// 列表项表单交互方法
const addListItem = () => {
  contentForm.value.listObj.items.push('')
}
const removeListItem = (idx) => {
  if (contentForm.value.listObj.items.length > 1) {
    contentForm.value.listObj.items.splice(idx, 1)
  }
}

// 表格表单交互方法
const addTableHeader = () => {
  const newHeaderName = `列 ${contentForm.value.tableObj.headers.length + 1}`
  contentForm.value.tableObj.headers.push(newHeaderName)
  // 每一行追加一列空单元格
  contentForm.value.tableObj.rows.forEach(row => {
    row.push('')
  })
}
const removeTableHeader = (idx) => {
  if (contentForm.value.tableObj.headers.length > 1) {
    contentForm.value.tableObj.headers.splice(idx, 1)
    // 每一行删除对应单元格
    contentForm.value.tableObj.rows.forEach(row => {
      row.splice(idx, 1)
    })
  }
}
const handleHeaderChange = () => {
  // 维持表格头部渲染即可
}
const addTableRow = () => {
  const colCount = contentForm.value.tableObj.headers.length
  contentForm.value.tableObj.rows.push(Array(colCount).fill(''))
}
const removeTableRow = (rIdx) => {
  if (contentForm.value.tableObj.rows.length > 1) {
    contentForm.value.tableObj.rows.splice(rIdx, 1)
  }
}

// 保存小节内容
const handleSaveContent = async () => {
  const type = contentForm.value.contentType
  let jsonStr = '{}'
  
  // 表单基础校验与对象序列化
  if (type === 'paragraph') {
    if (!contentForm.value.textContent.trim()) {
      window.$message?.warning('段落内容不能为空')
      return
    }
  } else if (type === 'image') {
    if (!contentForm.value.imageObj.url.trim()) {
      window.$message?.warning('图片链接不能为空')
      return
    }
    jsonStr = JSON.stringify(contentForm.value.imageObj)
  } else if (type === 'list_item') {
    const validItems = contentForm.value.listObj.items.filter(item => item.trim())
    if (validItems.length === 0) {
      window.$message?.warning('至少需要填写一项列表内容')
      return
    }
    jsonStr = JSON.stringify({ items: validItems })
  } else if (type === 'table') {
    if (contentForm.value.tableObj.headers.length === 0) {
      window.$message?.warning('表格必须至少有一列')
      return
    }
    jsonStr = JSON.stringify(contentForm.value.tableObj)
  }
  
  const payload = {
    sectionId: activeSectionId.value,
    contentType: type,
    textContent: contentForm.value.textContent,
    jsonData: jsonStr,
    sort: contentForm.value.sort
  }
  
  try {
    if (editingContent.value?.id) {
      payload.id = editingContent.value.id
      await updateSiteSectionContent(payload)
      window.$message?.success('修改内容成功')
    } else {
      await saveSiteSectionContent(payload)
      window.$message?.success('添加内容成功')
    }
    showContentFormModal.value = false
    fetchContents()
  } catch (error) {
    console.error('保存小节内容失败', error)
    window.$message?.error('操作失败')
  }
}

// 删除小节内容
const handleDeleteContent = (contentId) => {
  window.$dialog?.warning({
    title: '确认',
    content: '确定要删除该内容段落吗？此操作不可撤销！',
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await deleteSiteSectionContent(contentId)
        window.$message?.success('删除成功')
        fetchContents()
      } catch (error) {
        console.error('删除内容失败', error)
        window.$message?.error('删除失败')
      }
    }
  })
}

// 移动小节内容排序
const moveContent = async (index, direction) => {
  const targetIndex = index + direction
  if (targetIndex < 0 || targetIndex >= contents.value.length) return
  
  const current = contents.value[index]
  const target = contents.value[targetIndex]
  
  // 交换 sort 值
  const tempSort = current.sort
  current.sort = target.sort
  target.sort = tempSort
  
  try {
    // 后端传 String 类型的 jsonData，原样回传即可
    await updateSiteSectionContent({ id: current.id, sort: current.sort })
    await updateSiteSectionContent({ id: target.id, sort: target.sort })
    fetchContents()
  } catch (e) {
    console.error('更新内容排序失败', e)
  }
}

// ---------------- 常用辅助工具 ----------------

// 安全解析 JSON
const safeParseJson = (jsonStr) => {
  if (!jsonStr) return {}
  if (typeof jsonStr === 'object') return jsonStr
  try {
    return JSON.parse(jsonStr)
  } catch (e) {
    console.error('解析 JSON 失败', e, jsonStr)
    return {}
  }
}

// 类型标签样式
const getTypeTagType = (type) => {
  const types = {
    paragraph: 'default',
    image: 'info',
    list_item: 'success',
    table: 'warning'
  }
  return types[type] || 'default'
}

// 类型文字描述
const getContentTypeLabel = (type) => {
  const labels = {
    paragraph: '文字段落',
    image: '图片媒体',
    list_item: '特性列表',
    table: '数据表格'
  }
  return labels[type] || type
}

onMounted(() => {
  fetchSiteInfo()
  fetchSections(true) // 默认拉取后自动激活第一个小节
})
</script>

<style scoped>
.site-section-detail-view {
  padding: 12px 0;
}

.detail-header {
  border-bottom: 1px solid #efeff5;
  padding-bottom: 16px;
}

.site-name {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.sidebar-card, .content-card {
  border-radius: 8px;
  min-height: calc(100vh - 220px);
}

.empty-placeholder {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 250px;
}

.section-list {
  margin: 0 -12px;
}

.section-item {
  padding: 12px 16px !important;
  border-left: 3px solid transparent;
  transition: all 0.2s;
}

.section-item:hover {
  background-color: #f7f9fa;
}

.section-item.active {
  background-color: #e8f5e9;
  border-left-color: #18a058;
}

.section-item.active .section-title {
  color: #18a058;
  font-weight: bold;
}

.section-item-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.section-title {
  flex-grow: 1;
  font-size: 14px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-right: 8px;
}

.section-actions {
  display: flex;
  gap: 6px;
  opacity: 0.3;
  transition: opacity 0.2s;
}

.section-item:hover .section-actions,
.section-item.active .section-actions {
  opacity: 1;
}

.content-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.content-item-card {
  border-radius: 6px;
  border: 1px solid #efeff5;
}

.content-item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px dashed #efeff5;
  padding-bottom: 8px;
  margin-bottom: 12px;
}

.sort-tag {
  color: #999;
  font-size: 12px;
}

.content-render-body {
  padding: 4px 8px;
}

.render-paragraph {
  font-size: 14px;
  line-height: 1.6;
  color: #333;
  white-space: pre-wrap;
}

.render-image {
  text-align: left;
}

.image-wrapper {
  margin-bottom: 4px;
}

.image-caption {
  font-size: 12px;
  color: #666;
  font-style: italic;
}

.render-list {
  text-align: left;
}

.list-title {
  margin: 0 0 8px 0;
  font-size: 15px;
  font-weight: 600;
  color: #333;
}

.list-ul {
  list-style-type: none;
  padding-left: 0;
  margin: 0;
}

.list-ul li {
  font-size: 14px;
  line-height: 1.6;
  color: #444;
  margin-bottom: 4px;
}

.render-table {
  text-align: left;
}

.table-title {
  margin: 0 0 8px 0;
  font-size: 15px;
  font-weight: 600;
  color: #333;
}

.custom-render-table th {
  background-color: #fafafc !important;
  font-weight: 600 !important;
}

.custom-render-table td, .custom-render-table th {
  padding: 8px 12px !important;
}
</style>
