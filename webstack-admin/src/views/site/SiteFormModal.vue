<template>
  <n-modal
    :show="show"
    @update:show="(val) => emit('update:show', val)"
    preset="card"
    :title="title"
    style="width: 560px; --n-border-radius: var(--radius-md)"
    :bordered="false"
  >
    <n-form :model="form" :rules="rules" ref="formRef" label-width="90">
      <n-form-item label="网站名称" path="title">
        <n-input v-model:value="form.title" placeholder="请输入网站名称" />
      </n-form-item>
      <n-form-item label="所属分类" path="categoryId">
        <n-select
          v-model:value="form.categoryId"
          :options="categoryOptions"
          filterable
          placeholder="请选择所属分类"
        />
      </n-form-item>
      <n-form-item label="Logo" path="thumb">
        <upload-image v-model:value="form.thumb" />
      </n-form-item>
      <n-form-item label="简介" path="description">
        <n-input
          v-model:value="form.description"
          type="textarea"
          :rows="2"
          placeholder="请输入一句话简介"
        />
      </n-form-item>
      <n-form-item label="跳转链接" path="url">
        <n-input v-model:value="form.url" placeholder="请输入官网链接" />
      </n-form-item>
      <n-form-item label="标签" path="tagIds">
        <n-select
          v-model:value="form.tagIds"
          :options="tagOptions"
          multiple
          filterable
          tag
          placeholder="请选择或输入新标签"
        />
      </n-form-item>
      <n-form-item label="排序" path="sort">
        <n-input-number
          v-model:value="form.sort"
          placeholder="越大越靠前"
          style="width: 100%"
        />
      </n-form-item>
      <n-form-item label="推荐" path="isRecommended">
        <n-switch
          v-model:value="form.isRecommended"
          :checked-value="1"
          :unchecked-value="0"
        />
      </n-form-item>
      <n-form-item label="状态" path="visible">
        <n-switch
          v-model:value="form.visible"
          :checked-value="1"
          :unchecked-value="0"
        />
      </n-form-item>
    </n-form>
    <template #footer>
      <n-space justify="end">
        <n-button @click="handleCancel">取消</n-button>
        <n-button type="primary" :loading="submitting" @click="handleSubmit">
          {{ submitting ? '提交中...' : '确定' }}
        </n-button>
      </n-space>
    </template>
  </n-modal>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import {
  NModal,
  NForm,
  NFormItem,
  NInput,
  NInputNumber,
  NSelect,
  NSwitch,
  NButton,
  NSpace
} from 'naive-ui'
import UploadImage from '../../components/UploadImage.vue'
import { saveSite, updateSite } from '../../api/site.js'
import { getTagList, saveTag } from '../../api/tag.js'

const props = defineProps({
  show: Boolean,
  record: Object,
  categoryOptions: Array
})

const emit = defineEmits(['update:show', 'success'])

const formRef = ref(null)
const submitting = ref(false)
const form = ref({
  title: '',
  categoryId: null,
  thumb: '',
  description: '',
  url: '',
  tagIds: [],
  sort: 0,
  isRecommended: 0,
  visible: 1
})

const tagOptions = ref([])

const title = computed(() => (props.record ? '编辑网站' : '新增网站'))

const loadTags = async () => {
  const list = await getTagList()
  tagOptions.value = (list || []).map(t => ({ label: t.tagName, value: t.id }))
}

watch(
  () => props.show,
  async (val) => {
    if (val) {
      await loadTags()
      if (props.record) {
        form.value = {
          title: props.record.title || '',
          categoryId: props.record.categoryId || null,
          thumb: props.record.thumb || '',
          description: props.record.description || '',
          url: props.record.url || '',
          tagIds: props.record.tagIds || [],
          sort: props.record.sort ?? 0,
          isRecommended: props.record.isRecommended ?? 0,
          visible: props.record.visible ?? 1
        }
      } else {
        form.value = {
          title: '',
          categoryId: null,
          thumb: '',
          description: '',
          url: '',
          tagIds: [],
          sort: 0,
          isRecommended: 0,
          visible: 1
        }
      }
    }
  }
)

watch(
  () => form.value.tagIds,
  async (newVal) => {
    if (!newVal) return
    const strings = newVal.filter(v => typeof v === 'string')
    for (const tagName of strings) {
      const exists = tagOptions.value.some(opt => opt.label === tagName && typeof opt.value === 'number')
      if (exists) continue
      try {
        const res = await saveTag({ tagName })
        tagOptions.value.push({ label: res.tagName, value: res.id })
        const idx = form.value.tagIds.indexOf(tagName)
        if (idx !== -1) {
          form.value.tagIds.splice(idx, 1, res.id)
        }
      } catch {
        form.value.tagIds = form.value.tagIds.filter(v => v !== tagName)
      }
    }
  },
  { deep: true }
)

const rules = {
  title: { required: true, message: '请输入网站名称', trigger: 'blur' },
  categoryId: { required: true, type: 'number', message: '请选择所属分类', trigger: 'change' }
}

const handleCancel = () => {
  emit('update:show', false)
}

const handleSubmit = () => {
  formRef.value?.validate(async (errors) => {
    if (!errors) {
      submitting.value = true
      try {
        if (props.record?.id) {
          await updateSite({ id: props.record.id, ...form.value })
          window.$message?.success('更新成功')
        } else {
          await saveSite(form.value)
          window.$message?.success('新增成功')
        }
        emit('update:show', false)
        emit('success')
      } finally {
        submitting.value = false
      }
    }
  })
}
</script>
