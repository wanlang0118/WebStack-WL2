<template>
  <n-modal
    :show="show"
    @update:show="(val) => emit('update:show', val)"
    preset="card"
    :title="title"
    style="width: 400px"
  >
    <n-form :model="form" :rules="rules" ref="formRef" label-width="80">
      <n-form-item label="标签名" path="tagName">
        <n-input v-model:value="form.tagName" placeholder="请输入标签名" />
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
  NButton,
  NSpace
} from 'naive-ui'
import { saveTag, updateTag } from '../../api/tag.js'

const props = defineProps({
  show: Boolean,
  record: Object
})

const emit = defineEmits(['update:show', 'success'])

const formRef = ref(null)
const submitting = ref(false)
const form = ref({
  tagName: ''
})

const title = computed(() => (props.record ? '编辑标签' : '新增标签'))

watch(
  () => props.show,
  (val) => {
    if (val) {
      if (props.record) {
        form.value = { ...props.record }
      } else {
        form.value = { tagName: '' }
      }
    }
  }
)

const rules = {
  tagName: { required: true, message: '请输入标签名', trigger: 'blur' }
}

const handleCancel = () => {
  emit('update:show', false)
}

const handleSubmit = async () => {
  await formRef.value?.validate?.()
  submitting.value = true
  try {
    if (props.record?.id) {
      await updateTag(form.value)
      window.$message?.success('更新成功')
    } else {
      await saveTag(form.value)
      window.$message?.success('新增成功')
    }
    emit('update:show', false)
    emit('success')
  } finally {
    submitting.value = false
  }
}
</script>
