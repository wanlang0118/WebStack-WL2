<template>
  <div class="comment-section">
    <h3 class="comment-title">{{ total }} 条评论</h3>

    <div class="comment-form-card">
      <CommentForm :site-id="siteId" @submitted="loadComments" />
    </div>

    <div class="comment-list" v-if="comments.length">
      <CommentItem
        v-for="c in comments"
        :key="c.id"
        :comment="c"
        :site-id="siteId"
        @refresh="loadComments"
      />
    </div>
    <div class="comment-empty" v-else-if="!loading">
      <p>暂无评论，来发表第一条吧！</p>
    </div>

    <div class="comment-more" v-if="hasMore">
      <button class="load-more-btn" @click="loadMore" :disabled="loading">
        {{ loading ? '加载中...' : '加载更多' }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, defineProps } from 'vue'
import CommentForm from './CommentForm.vue'
import CommentItem from './CommentItem.vue'
import { getCommentPage } from '@/api/comment'

const props = defineProps({
  siteId: { type: Number, required: true }
})

const comments = ref([])
const total = ref(0)
const page = ref(1)
const loading = ref(false)

const hasMore = computed(() => comments.value.length < total.value)

function countAllComments(list) {
  return list.reduce((sum, item) => {
    return sum + 1 + countAllComments(item.children || [])
  }, 0)
}

async function loadComments() {
  page.value = 1
  loading.value = true
  try {
    const res = await getCommentPage({ siteId: props.siteId, status: 1, page: 1, size: 20 })
    comments.value = res?.records || []
    total.value = countAllComments(comments.value)
  } catch (e) {
    console.error('加载评论失败', e)
  } finally {
    loading.value = false
  }
}

async function loadMore() {
  page.value++
  loading.value = true
  try {
    const res = await getCommentPage({ siteId: props.siteId, status: 1, page: page.value, size: 20 })
    const records = res?.records || []
    comments.value.push(...records)
    total.value = countAllComments(comments.value)
  } catch (e) {
    console.error(e)
    page.value--
  } finally {
    loading.value = false
  }
}

onMounted(loadComments)
</script>

<style scoped>
.comment-section {
  margin-top: 32px;
}

.comment-title {
  font-size: 16px;
  font-weight: 700;
  color: var(--color-text);
  margin-bottom: 16px;
}

.comment-form-card {
  background: var(--color-bg-card);
  border-radius: var(--radius-lg);
  padding: 20px;
  box-shadow: var(--shadow-card);
  margin-bottom: 20px;
}

.comment-list {
  background: var(--color-bg-card);
  border-radius: var(--radius-lg);
  padding: 4px 20px;
  box-shadow: var(--shadow-card);
}

.comment-empty {
  padding: 40px;
  text-align: center;
  color: var(--color-text-light);
  font-size: 14px;
  background: var(--color-bg-card);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-card);
}

.comment-more {
  text-align: center;
  padding: 16px 0;
}

.load-more-btn {
  padding: 8px 24px;
  background: var(--color-bg-card);
  color: var(--color-text-secondary);
  border-radius: var(--radius-sm);
  font-size: 14px;
  box-shadow: var(--shadow-card);
  transition: all 0.2s;
}

.load-more-btn:hover:not(:disabled) {
  color: var(--color-primary);
}
</style>
