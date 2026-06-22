<template>
  <div class="comment-form">
    <div class="form-row-top">
      <div class="form-avatar">
        <div class="avatar-circle">{{ form.guestName?.charAt(0) || '?' }}</div>
      </div>
      <textarea class="form-textarea" v-model="form.content"
                :placeholder="replyPlaceholder"
                :class="{ 'form-textarea--error': errors.content }"
                rows="3" @input="errors.content = ''"></textarea>
    </div>

    <div class="form-bottom">
      <div class="form-fields">
        <div class="field-wrap">
          <input class="form-input" v-model="form.guestName"
                 :class="{ 'form-input--error': errors.name }"
                 placeholder="昵称" @input="errors.name = ''" />
          <span class="field-error" v-if="errors.name">{{ errors.name }}</span>
        </div>
        <input class="form-input" v-model="form.guestEmail" placeholder="邮箱（选填）" />
        <input class="form-input" v-model="form.guestUrl" placeholder="网址（选填）" />
      </div>
      <div class="form-actions">
        <button v-if="parentId" class="btn-cancel" @click="$emit('cancel')">取消</button>
        <button class="btn-submit" :disabled="submitting" @click="handleSubmit">
          <span v-if="submitting" class="btn-spinner"></span>
          {{ submitting ? '提交中...' : (parentId ? '发表回复' : '发表评论') }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, computed, defineProps, defineEmits } from 'vue'
import { submitComment } from '@/api/comment'
import { useToast } from '@/composables/useToast'

const props = defineProps({
  siteId: { type: Number, required: true },
  parentId: { type: Number, default: null },
  replyToName: { type: String, default: '' }
})

const replyPlaceholder = computed(() => {
  if (!props.parentId) return '输入评论内容...'
  if (props.replyToName) return `回复 @${props.replyToName}...`
  return '写下你的回复...'
})

const emit = defineEmits(['submitted', 'cancel'])

const { show: showToast } = useToast()
const submitting = ref(false)
const errors = reactive({ name: '', content: '' })

const form = reactive({
  content: '',
  guestName: localStorage.getItem('ws_guest_name') || '',
  guestEmail: localStorage.getItem('ws_guest_email') || '',
  guestUrl: localStorage.getItem('ws_guest_url') || ''
})

function validate() {
  let valid = true
  if (!form.guestName.trim()) {
    errors.name = '请输入昵称'
    valid = false
  }
  if (!form.content.trim()) {
    errors.content = '请输入评论内容'
    valid = false
  }
  return valid
}

async function handleSubmit() {
  if (!validate()) return

  submitting.value = true
  try {
    let content = form.content.trim()
    if (props.replyToName && !content.startsWith('@')) {
      content = `@${props.replyToName} ${content}`
    }

    await submitComment({
      siteId: props.siteId,
      content,
      parentId: props.parentId,
      guestName: form.guestName,
      guestEmail: form.guestEmail,
      guestUrl: form.guestUrl
    })

    localStorage.setItem('ws_guest_name', form.guestName)
    localStorage.setItem('ws_guest_email', form.guestEmail)
    localStorage.setItem('ws_guest_url', form.guestUrl)

    form.content = ''
    errors.name = ''
    errors.content = ''
    showToast(props.parentId ? '回复发表成功' : '评论发表成功', 'success')
    emit('submitted')
  } catch (e) {
    showToast('评论提交失败，请稍后重试', 'error')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.form-row-top {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.form-avatar {
  flex-shrink: 0;
}

.avatar-circle {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: var(--color-primary);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 16px;
}

.form-textarea {
  flex: 1;
  padding: 10px 14px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  resize: vertical;
  font-size: 14px;
  color: var(--color-text);
  background: var(--color-bg);
  transition: border-color 0.2s, box-shadow 0.2s;
  min-height: 60px;
}

.form-textarea:focus {
  border-color: var(--color-primary);
  background: var(--color-bg-card);
  box-shadow: 0 0 0 3px var(--color-tag-bg);
}

.form-textarea--error {
  border-color: var(--color-danger);
}

.form-textarea--error:focus {
  box-shadow: 0 0 0 3px oklch(0.60 0.18 25 / 0.12);
}

.form-bottom {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-top: 12px;
  gap: 12px;
}

.form-fields {
  display: flex;
  gap: 8px;
  flex: 1;
  flex-wrap: wrap;
}

.field-wrap {
  position: relative;
  flex: 1;
  min-width: 120px;
}

.form-input {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  font-size: 13px;
  color: var(--color-text);
  background: var(--color-bg);
  transition: border-color 0.2s, box-shadow 0.2s;
}

.form-input:focus {
  border-color: var(--color-primary);
  background: var(--color-bg-card);
  box-shadow: 0 0 0 3px var(--color-tag-bg);
}

.form-input--error {
  border-color: var(--color-danger);
}

.form-input--error:focus {
  box-shadow: 0 0 0 3px oklch(0.60 0.18 25 / 0.12);
}

.field-error {
  position: absolute;
  left: 0;
  top: calc(100% + 2px);
  font-size: 0.75rem;
  color: var(--color-danger);
  white-space: nowrap;
}

.form-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

.btn-submit {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 20px;
  background: var(--color-primary);
  color: #fff;
  border-radius: var(--radius-sm);
  font-size: 14px;
  font-weight: 500;
  transition: background 0.2s;
  white-space: nowrap;
}

.btn-submit:hover:not(:disabled) {
  background: var(--color-primary-hover);
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-spinner {
  width: 14px;
  height: 14px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.btn-cancel {
  padding: 8px 16px;
  background: var(--color-bg);
  color: var(--color-text-secondary);
  border-radius: var(--radius-sm);
  font-size: 14px;
  transition: background 0.2s;
  white-space: nowrap;
}

.btn-cancel:hover {
  background: var(--color-border);
}

@media (prefers-reduced-motion: reduce) {
  .btn-spinner {
    animation: none;
  }
}
</style>
