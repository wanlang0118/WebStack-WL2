<template>
  <div>
    <n-space vertical :size="16">
      <n-space justify="space-between" align="center">
        <n-h2>批量导入网站</n-h2>
      </n-space>

      <!-- 步骤条 -->
      <n-steps :current="currentStep" size="small">
        <n-step title="输入数据" />
        <n-step title="解析预览" />
        <n-step title="导入结果" />
      </n-steps>

      <!-- Step 1: 输入数据 -->
      <template v-if="currentStep === 1">
        <n-card title="DeepSeek API Key（仅 AI 格式识别时使用）" size="small">
          <n-input
            v-model:value="apiKey"
            type="password"
            show-password-on="click"
            placeholder="输入 DeepSeek API Key，仅在非标准格式需要 AI 识别时使用"
            style="max-width: 600px;"
          />
          <template #footer>
            <n-text depth="3" style="font-size: 12px;">
              Key 仅用于本次请求转发到 DeepSeek，不会被服务端存储。标准 JSON 格式可直接导入无需填写。
            </n-text>
          </template>
        </n-card>

        <n-card title="导入数据" size="small">
          <n-tabs type="segment" v-model:value="inputTab">
            <n-tab-pane name="paste" tab="粘贴文本">
              <n-input
                v-model:value="rawText"
                type="textarea"
                placeholder="粘贴 JSON 数组或其他格式的网站数据..."
                :autosize="{ minRows: 12, maxRows: 24 }"
                style="font-family: monospace;"
              />
            </n-tab-pane>
            <n-tab-pane name="file" tab="上传文件">
              <n-upload
                :max="1"
                :default-upload="false"
                accept=".json,.txt,.csv,.md"
                @change="handleFileChange"
                :file-list="fileList"
              >
                <n-upload-dragger>
                  <div style="padding: 24px 0;">
                    <n-text style="font-size: 16px; color: #666;">
                      点击或拖拽文件到此区域
                    </n-text>
                    <br />
                    <n-text depth="3" style="font-size: 12px;">
                      支持 .json / .txt / .csv / .md 格式
                    </n-text>
                  </div>
                </n-upload-dragger>
              </n-upload>
            </n-tab-pane>
          </n-tabs>

          <n-space style="margin-top: 16px;">
            <n-button type="primary" :loading="parsing" @click="handleParse" :disabled="!hasInput">
              解析预览
            </n-button>
          </n-space>
        </n-card>
      </template>

      <!-- Step 2: 解析预览 -->
      <template v-if="currentStep === 2">
        <n-card size="small">
          <template #header>
            <n-space align="center">
              <span>解析结果预览</span>
              <n-tag type="info" size="small">共 {{ parsedItems.length }} 条</n-tag>
            </n-space>
          </template>
          <template #header-extra>
            <n-space>
              <n-button @click="currentStep = 1">返回修改</n-button>
              <n-button type="primary" :loading="importing" @click="handleImport">
                确认导入
              </n-button>
            </n-space>
          </template>

          <n-data-table
            :columns="previewColumns"
            :data="parsedItems"
            :max-height="500"
            :scroll-x="900"
            size="small"
            :row-key="(row, idx) => idx"
          />
        </n-card>
      </template>

      <!-- Step 3: 导入结果 -->
      <template v-if="currentStep === 3">
        <n-card size="small">
          <template #header>
            <n-space align="center">
              <span>导入完成</span>
            </n-space>
          </template>
          <template #header-extra>
            <n-space>
              <n-button @click="handleReset">继续导入</n-button>
              <n-button type="primary" @click="$router.push('/site')">查看网站列表</n-button>
            </n-space>
          </template>

          <n-result
            :status="importResult.failCount === 0 ? 'success' : (importResult.successCount === 0 ? 'error' : 'warning')"
            :title="resultTitle"
            :description="resultDescription"
          />

          <n-data-table
            v-if="importResult.errors && importResult.errors.length > 0"
            :columns="errorColumns"
            :data="importResult.errors"
            size="small"
            style="margin-top: 16px;"
            :max-height="300"
          />
        </n-card>
      </template>
    </n-space>
  </div>
</template>

<script setup>
import { ref, computed, h } from 'vue'
import { NTag, NSpace, NText, NEllipsis, useMessage } from 'naive-ui'
import { importSites, aiParseSites } from '@/api/siteImport.js'
import { getCategoryTree } from '@/api/category.js'
import { getTagList } from '@/api/tag.js'

const message = useMessage()

const API_KEY_STORAGE = 'webstack_deepseek_key'
const apiKey = ref(localStorage.getItem(API_KEY_STORAGE) || '')
const rawText = ref('')
const fileList = ref([])
const inputTab = ref('paste')
const currentStep = ref(1)
const parsing = ref(false)
const importing = ref(false)
const parsedItems = ref([])
const importResult = ref({ total: 0, successCount: 0, failCount: 0, errors: [] })

const existingCategories = ref([])
const existingTags = ref([])

const hasInput = computed(() => {
  if (inputTab.value === 'paste') return rawText.value.trim().length > 0
  return fileList.value.length > 0
})

const resultTitle = computed(() => {
  const r = importResult.value
  if (r.failCount === 0) return `全部导入成功（${r.successCount} 条）`
  if (r.successCount === 0) return `全部导入失败（${r.failCount} 条）`
  return `部分导入成功`
})

const resultDescription = computed(() => {
  const r = importResult.value
  if (r.failCount === 0) return ''
  return `成功 ${r.successCount} 条，失败 ${r.failCount} 条`
})

const previewColumns = [
  { title: '序号', key: 'index', width: 60, render: (_, idx) => idx + 1 },
  {
    title: '网站名称',
    key: 'name',
    width: 180,
    ellipsis: { tooltip: true },
    render: (row) => row.name || row.title || '-'
  },
  {
    title: '分类',
    key: 'category',
    width: 140,
    render: (row) => {
      if (!row.category) return '-'
      const isNew = !existingCategories.value.includes(row.category)
      return h(NSpace, { size: 4, align: 'center' }, () => [
        h(NText, null, () => row.category),
        isNew ? h(NTag, { size: 'small', type: 'warning' }, () => '新建') : null
      ])
    }
  },
  {
    title: '标签',
    key: 'tags',
    width: 200,
    render: (row) => {
      if (!row.tags || row.tags.length === 0) return '-'
      return h(NSpace, { size: 4 }, () =>
        row.tags.map(t => {
          const isNew = !existingTags.value.includes(t)
          return h(NTag, {
            size: 'small',
            type: isNew ? 'warning' : 'default',
            bordered: true
          }, () => t + (isNew ? '(新)' : ''))
        })
      )
    }
  },
  {
    title: '简介',
    key: 'shortDescription',
    width: 200,
    ellipsis: { tooltip: true },
    render: (row) => row.shortDescription || row.short_description || row.description || '-'
  },
  {
    title: '小节数',
    key: 'sections',
    width: 80,
    render: (row) => (row.sections && row.sections.length) || 0
  },
  {
    title: '网址',
    key: 'officialUrl',
    width: 200,
    ellipsis: { tooltip: true },
    render: (row) => row.officialUrl || row.official_url || row.url || '-'
  }
]

const errorColumns = [
  { title: '序号', key: 'index', width: 60, render: (row) => row.index + 1 },
  { title: '网站名称', key: 'name', width: 180, ellipsis: { tooltip: true } },
  { title: '失败原因', key: 'reason', ellipsis: { tooltip: true } }
]

function handleFileChange({ fileList: newList }) {
  fileList.value = newList
  if (newList.length > 0 && newList[0].file) {
    const reader = new FileReader()
    reader.onload = (e) => {
      rawText.value = e.target.result
    }
    reader.readAsText(newList[0].file)
  }
}

function tryParseStandard(text) {
  // 尝试多种常见 JSON 变体
  const attempts = [
    text,                          // 原始文本（标准 JSON 数组或单对象）
    '[' + text + ']',              // 缺少外层方括号的逗号分隔对象
    '[' + text.replace(/,\s*$/, '') + ']'  // 末尾多余逗号
  ]

  for (const attempt of attempts) {
    try {
      const parsed = JSON.parse(attempt)
      const arr = Array.isArray(parsed) ? parsed : [parsed]
      if (arr.length === 0) continue

      const first = arr[0]
      if (typeof first !== 'object' || first === null) continue
      const hasName = !!(first.name || first.title)
      const hasCategory = !!first.category
      if (!hasName || !hasCategory) continue

      return arr.map(item => ({
        ...item,
        name: item.name || item.title,
        logo: item.logo || item.thumb,
        shortDescription: item.shortDescription || item.short_description || item.description,
        officialUrl: item.officialUrl || item.official_url || item.url,
        category: item.category,
        tags: item.tags,
        sections: item.sections
      }))
    } catch {
      // 继续尝试下一种变体
    }
  }
  return null
}

async function fetchExistingData() {
  try {
    const [catTree, tagList] = await Promise.all([getCategoryTree(), getTagList()])
    const catNames = []
    const flatten = (nodes) => {
      for (const n of nodes) {
        catNames.push(n.title)
        if (n.children) flatten(n.children)
      }
    }
    flatten(catTree || [])
    existingCategories.value = catNames
    existingTags.value = (tagList || []).map(t => t.tagName)
  } catch {
    // ignore
  }
}

async function handleParse() {
  const text = rawText.value.trim()
  if (!text) {
    message.warning('请输入或上传数据')
    return
  }

  parsing.value = true
  try {
    await fetchExistingData()

    const standard = tryParseStandard(text)
    if (standard) {
      parsedItems.value = standard
      currentStep.value = 2
      message.success(`成功解析 ${standard.length} 条标准格式数据`)
      return
    }

    if (!apiKey.value.trim()) {
      message.warning('非标准格式数据，请填写 DeepSeek API Key 以启用 AI 智能识别')
      return
    }
    localStorage.setItem(API_KEY_STORAGE, apiKey.value)

    message.info('非标准格式，正在调用 AI 识别...')
    const result = await aiParseSites({
      apiKey: apiKey.value,
      rawText: text
    })
    parsedItems.value = result
    currentStep.value = 2
    message.success(`AI 识别完成，共解析 ${result.length} 条数据`)
  } catch (e) {
    message.error(e.message || '解析失败')
  } finally {
    parsing.value = false
  }
}

async function handleImport() {
  if (parsedItems.value.length === 0) return

  importing.value = true
  try {
    const result = await importSites(parsedItems.value)
    importResult.value = result
    currentStep.value = 3
    if (result.failCount === 0) {
      message.success(`全部 ${result.successCount} 条导入成功！`)
    } else {
      message.warning(`成功 ${result.successCount} 条，失败 ${result.failCount} 条`)
    }
  } catch (e) {
    message.error(e.message || '导入失败')
  } finally {
    importing.value = false
  }
}

function handleReset() {
  currentStep.value = 1
  rawText.value = ''
  fileList.value = []
  parsedItems.value = []
  importResult.value = { total: 0, successCount: 0, failCount: 0, errors: [] }
}
</script>
