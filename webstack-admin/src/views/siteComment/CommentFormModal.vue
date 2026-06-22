<template>
  <n-modal
    :show="show"
    preset="card"
    :title="mode === 'reply' ? '回复评论' : '编辑评论'"
    style="width: 600px;"
    @update:show="(val) => emit('update:show', val)"
  >
    <n-form ref="formRef" :model="form" :rules="rules" label-placement="top">
      <template v-if="mode === 'reply'">
        <n-form-item label="原始评论">
          <n-input
            :value="record?.content"
            type="textarea"
            readonly
            :autosize="{ minRows: 2, maxRows: 4 }"
            placeholder=""
          />
        </n-form-item>
        <n-form-item label="回复内容" path="content">
          <n-input
            v-model:value="form.content"
            type="textarea"
            :autosize="{ minRows: 3, maxRows: 8 }"
            placeholder="请输入回复内容"
          />
        </n-form-item>
      </template>
      <template v-else>
        <n-form-item label="评论内容" path="content">
          <n-input
            v-model:value="form.content"
            type="textarea"
            :autosize="{ minRows: 3, maxRows: 8 }"
            placeholder="请输入评论内容"
          />
        </n-form-item>
      </template>
    </n-form>
    <template #footer>
      <n-space justify="end">
        <n-button @click="emit('update:show', false)">取消</n-button>
        <n-button type="primary" :loading="submitting" @click="handleSubmit">确定</n-button>
      </n-space>
    </template>
  </n-modal>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { NModal, NForm, NFormItem, NInput, NButton, NSpace } from 'naive-ui'
import { replyComment, updateCommentContent } from '../../api/siteComment.js'

const props = defineProps({
  show: Boolean,
  mode: { type: String, default: 'reply' },
  record: { type: Object, default: null }
})

const emit = defineEmits(['update:show', 'success'])

const formRef = ref(null)
const submitting = ref(false)
const form = reactive({ content: '' })

const rules = {
  content: { required: true, message: '内容不能为空', trigger: 'blur' }
}

watch(() => props.show, (val) => {
  if (val) {
    form.content = props.mode === 'edit' ? (props.record?.content || '') : ''
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
    if (props.mode === 'reply') {
      await replyComment({
        siteId: props.record.siteId,
        parentId: props.record.id,
        content: form.content,
        userId: null
      })
      window.$message?.success('回复成功')
    } else {
      await updateCommentContent(props.record.id, form.content)
      window.$message?.success('编辑成功')
    }
    emit('update:show', false)
    emit('success')
  } finally {
    submitting.value = false
  }
}
</script>
