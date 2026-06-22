<template>
  <n-modal
    :show="show"
    @update:show="(val) => emit('update:show', val)"
    preset="card"
    :title="title"
    :style="modalStyle"
    :bordered="false"
  >
    <n-form :model="form" :rules="rules" ref="formRef" label-width="80">
      <n-form-item label="父分类" path="parentId">
        <n-select
          v-model:value="form.parentId"
          :options="parentOptions"
          clearable
          filterable
          placeholder="不选则为顶级分类"
        />
      </n-form-item>
      <n-form-item label="分类名" path="title">
        <n-input v-model:value="form.title" placeholder="请输入分类名" />
      </n-form-item>
      <n-form-item label="英文名" path="enTitle">
        <n-input v-model:value="form.enTitle" placeholder="请输入英文名" />
      </n-form-item>
      
      <n-form-item label="图标类型">
        <n-radio-group v-model:value="iconType" name="iconType">
          <n-space>
            <n-radio value="vector">系统图标</n-radio>
            <n-radio value="image">图片上传</n-radio>
          </n-space>
        </n-radio-group>
      </n-form-item>

      <n-form-item :label="iconType === 'vector' ? '选择图标' : '上传图片'" path="icon">
        <icon-picker v-if="iconType === 'vector'" v-model:value="form.icon" />
        <upload-image v-else v-model:value="form.icon" />
      </n-form-item>

      <n-form-item label="排序" path="sort">
        <n-input-number
          v-model:value="form.sort"
          placeholder="越大越靠前"
          style="width: 100%"
        />
      </n-form-item>
      <n-form-item label="显示状态" path="visible">
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
import { ref, watch, computed, nextTick } from 'vue'
import {
  NModal,
  NForm,
  NFormItem,
  NInput,
  NInputNumber,
  NSelect,
  NSwitch,
  NButton,
  NSpace,
  NRadioGroup,
  NRadio
} from 'naive-ui'
import UploadImage from '../../components/UploadImage.vue'
import IconPicker from '../../components/IconPicker.vue'
import { saveCategory, updateCategory } from '../../api/category.js'
import { normalizeIcon } from '@shared/categoryIcons.js'

const props = defineProps({
  show: Boolean,
  record: Object,
  treeOptions: Array
})

const emit = defineEmits(['update:show', 'success'])

const formRef = ref(null)
const submitting = ref(false)
const iconType = ref('vector') // 'vector' | 'image'

const form = ref({
  parentId: 0,
  title: '',
  enTitle: '',
  icon: '',
  sort: 0,
  visible: 1
})

const title = computed(() => (props.record ? '编辑分类' : '新增分类'))

const modalStyle = computed(() => ({
  width: iconType.value === 'vector' ? '600px' : '480px',
  transition: 'width 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
  '--n-border-radius': 'var(--radius-md)'
}))

const parentOptions = computed(() => [
  { label: '顶级分类', value: 0 },
  ...(props.treeOptions || [])
])

function resolveIconType(icon) {
  const v = (icon || '').trim()
  if (!v) return 'vector'
  if (v.startsWith('ri-')) return 'vector'
  if (v.startsWith('http://') || v.startsWith('https://') || v.startsWith('/')) return 'image'
  return 'image'
}

let skipClear = false

watch(
  () => props.show,
  (val) => {
    if (val) {
      skipClear = true
      if (props.record) {
        form.value = { ...props.record }
        form.value.icon = normalizeIcon(form.value.icon)
        iconType.value = resolveIconType(form.value.icon)
      } else {
        form.value = {
          parentId: 0,
          title: '',
          enTitle: '',
          icon: '',
          sort: 0,
          visible: 1
        }
        iconType.value = 'vector'
      }
      nextTick(() => {
        skipClear = false
      })
    }
  }
)

watch(iconType, (next, prev) => {
  if (skipClear || prev === undefined) return
  form.value.icon = ''
})

const rules = {
  title: { required: true, message: '请输入分类名', trigger: 'blur' }
}

const handleCancel = () => {
  emit('update:show', false)
}

const handleSubmit = async () => {
  await formRef.value?.validate?.()
  submitting.value = true
  try {
    if (props.record?.id) {
      await updateCategory(form.value)
      window.$message?.success('更新成功')
    } else {
      await saveCategory(form.value)
      window.$message?.success('新增成功')
    }
    emit('update:show', false)
    emit('success')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
</style>
