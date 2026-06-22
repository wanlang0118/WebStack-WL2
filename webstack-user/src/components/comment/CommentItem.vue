<template>
  <div class="comment-thread">
    <!-- 第 1 层：顶级评论 -->
    <div class="comment-item comment-item--top">
      <div class="comment-avatar">
        <div class="avatar-circle avatar-circle--lg" :class="{ 'avatar-admin': comment.userId }">
          {{ topName.charAt(0) }}
        </div>
      </div>
      <div class="comment-main">
        <div class="comment-header">
          <div class="comment-meta">
            <span class="comment-author">{{ topName }}</span>
            <span class="comment-badge badge-admin" v-if="comment.userId">管理</span>
            <span class="comment-badge badge-guest" v-else>游客</span>
          </div>
          <button class="reply-btn" @click="toggleReply('top')">回复</button>
        </div>
        <div class="comment-content">{{ comment.content }}</div>
        <div class="comment-time">● {{ formatTime(comment.createTime) }}</div>

        <CommentForm
          v-if="replyTarget === 'top'"
          class="inline-reply-form"
          :site-id="siteId"
          :parent-id="comment.id"
          @submitted="onReplied"
          @cancel="replyTarget = null"
        />
      </div>
    </div>

    <!-- 第 2 层：所有回复平铺展示（可继续回复，不再嵌套第 3 层） -->
    <div class="comment-replies" v-if="comment.children?.length">
      <div
        v-for="reply in comment.children"
        :key="reply.id"
        class="comment-item comment-item--reply"
      >
        <div class="comment-avatar">
          <div class="avatar-circle" :class="{ 'avatar-admin': reply.userId }">
            {{ getDisplayName(reply).charAt(0) }}
          </div>
        </div>
        <div class="comment-main">
          <div class="comment-header">
            <div class="comment-meta">
              <span class="comment-author">{{ getDisplayName(reply) }}</span>
              <span class="comment-badge badge-admin" v-if="reply.userId">管理</span>
              <span class="comment-badge badge-guest" v-else>游客</span>
            </div>
            <button class="reply-btn" @click="toggleReply(reply.id)">回复</button>
          </div>
          <div class="comment-content" v-html="formatReplyContent(reply.content)"></div>
          <div class="comment-time">● {{ formatTime(reply.createTime) }}</div>

          <CommentForm
            v-if="replyTarget === reply.id"
            class="inline-reply-form"
            :site-id="siteId"
            :parent-id="comment.id"
            :reply-to-name="getDisplayName(reply)"
            @submitted="onReplied"
            @cancel="replyTarget = null"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, defineProps, defineEmits } from 'vue'
import CommentForm from './CommentForm.vue'

const props = defineProps({
  comment: { type: Object, required: true },
  siteId: { type: Number, required: true }
})

const emit = defineEmits(['refresh'])

const replyTarget = ref(null)

const topName = computed(() => getDisplayName(props.comment))

function getDisplayName(item) {
  return item.username || item.guestName || '匿名用户'
}

function toggleReply(target) {
  replyTarget.value = replyTarget.value === target ? null : target
}

function formatReplyContent(content) {
  if (!content) return ''
  const escaped = content
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
  return escaped.replace(
    /^(@[^\s]+)\s/,
    '<span class="reply-at">$1</span> '
  )
}

function formatTime(dt) {
  if (!dt) return ''
  const d = new Date(dt)
  const now = new Date()
  const diff = now - d
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)} 分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)} 小时前`
  if (diff < 2592000000) return `${Math.floor(diff / 86400000)} 天前`
  const y = d.getFullYear()
  return `${y === now.getFullYear() ? '' : y + '年'}${d.getMonth() + 1}月${d.getDate()}日`
}

function onReplied() {
  replyTarget.value = null
  emit('refresh')
}
</script>

<style scoped>
.comment-thread {
  padding: 18px 0;
}

.comment-thread + .comment-thread {
  border-top: 1px solid var(--color-border-light);
}

.comment-item {
  display: flex;
  gap: 12px;
}

.comment-item--reply + .comment-item--reply {
  margin-top: 14px;
}

.comment-replies {
  margin-top: 12px;
  margin-left: 52px;
  padding-left: 16px;
  border-left: 2px solid var(--color-border-light);
}

.avatar-circle {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #e5e7eb;
  color: #9ca3af;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 13px;
  flex-shrink: 0;
}

.avatar-circle--lg {
  width: 40px;
  height: 40px;
  font-size: 15px;
}

.avatar-admin {
  background: var(--color-primary);
  color: #fff;
}

.comment-main {
  flex: 1;
  min-width: 0;
}

.comment-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 6px;
}

.comment-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.comment-author {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text);
}

.comment-item--reply .comment-author {
  font-size: 13px;
}

.comment-badge {
  font-size: 11px;
  padding: 1px 8px;
  border-radius: 4px;
  font-weight: 500;
}

.badge-admin {
  background: var(--color-primary);
  color: #fff;
}

.badge-guest {
  background: var(--color-tag-bg);
  color: var(--color-tag-text);
}

.reply-btn {
  background: none;
  color: var(--color-text-light);
  font-size: 13px;
  transition: color 0.2s;
}

.reply-btn:hover {
  color: var(--color-primary);
}

.comment-content {
  font-size: 14px;
  line-height: 1.7;
  color: var(--color-text);
  word-break: break-word;
  margin-bottom: 4px;
}

.comment-item--reply .comment-content {
  font-size: 13px;
}

.comment-content :deep(.reply-at) {
  color: var(--color-primary);
  font-weight: 500;
}

.comment-time {
  font-size: 12px;
  color: var(--color-text-light);
}

.inline-reply-form {
  margin-top: 12px;
}
</style>
