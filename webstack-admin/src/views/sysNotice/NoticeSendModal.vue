<template>
  <n-modal
    :show="show"
    preset="card"
    title="发布系统公告"
    style="width: 600px;"
    @update:show="(val) => emit('update:show', val)"
  >
    <n-form ref="formRef" :model="form" :rules="rules" label-placement="top">
      <n-form-item label="标题" path="title">
        <n-input
          v-model:value="form.title"
          placeholder="请输入公告标题"
        />
      </n-form-item>
      <n-form-item label="摘要" path="summary">
        <n-input
          v-model:value="form.summary"
          placeholder="列表页展示的单行摘要（最多 200 字）"
          maxlength="200"
          show-count
        />
      </n-form-item>
      <n-form-item label="正文" path="content">
        <n-input
          v-model:value="form.content"
          type="textarea"
          :autosize="{ minRows: 4, maxRows: 10 }"
          placeholder="请输入公告正文"
        />
      </n-form-item>
    </n-form>
    <template #footer>
      <n-space justify="end">
        <n-button @click="emit('update:show', false)">取消</n-button>
        <n-button type="primary" :loading="submitting" @click="handleSubmit">
          发布给所有用户
        </n-button>
      </n-space>
    </template>
  </n-modal>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { NModal, NForm, NFormItem, NInput, NButton, NSpace } from 'naive-ui'
import { broadcastNotice } from '../../api/sysNotice.js'

const props = defineProps({
  show: Boolean
})

const emit = defineEmits(['update:show', 'success'])

const formRef = ref(null)
const submitting = ref(false)
const form = reactive({
  title: '',
  summary: '',
  content: ''
})

const rules = {
  title: { required: true, message: '标题不能为空', trigger: 'blur' },
  summary: { required: true, message: '摘要不能为空', trigger: 'blur' },
  content: { required: true, message: '正文不能为空', trigger: 'blur' }
}

watch(() => props.show, (val) => {
  if (val) {
    form.title = ''
    form.summary = ''
    form.content = ''
  }
})

async function handleSubmit() {
  try {
    await formRef.value?.validate()
  } catch {
    return
  }

  submitting.value = true
  try {
    await broadcastNotice({ ...form })
    window.$message?.success('已向全部用户发送公告')
    emit('update:show', false)
    emit('success')
  } finally {
    submitting.value = false
  }
}
</script>
