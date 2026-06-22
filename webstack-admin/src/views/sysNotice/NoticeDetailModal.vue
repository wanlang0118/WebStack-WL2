<template>
  <n-modal
    :show="show"
    preset="card"
    title="通知详情"
    style="width: 600px;"
    @update:show="(val) => emit('update:show', val)"
  >
    <n-descriptions bordered :column="1" label-placement="left">
      <n-descriptions-item label="标题">
        {{ record?.title || '-' }}
      </n-descriptions-item>
      <n-descriptions-item label="类型">
        <n-tag :type="typeTagMap[record?.noticeType]?.type || 'default'" size="small">
          {{ typeTagMap[record?.noticeType]?.label || '未知' }}
        </n-tag>
      </n-descriptions-item>
      <n-descriptions-item label="发送者">
        {{ record?.senderName || '系统' }}
      </n-descriptions-item>
      <n-descriptions-item label="接收者">
        {{ record?.receiverName || '-' }} #{{ record?.receiverId }}
      </n-descriptions-item>
      <n-descriptions-item v-if="record?.siteTitle" label="关联网站">
        {{ record.siteTitle }} #{{ record.sourceSiteId }}
      </n-descriptions-item>
      <n-descriptions-item label="摘要">
        {{ record?.summary || '-' }}
      </n-descriptions-item>
      <n-descriptions-item label="正文">
        <div style="white-space: pre-wrap;">{{ record?.content || '-' }}</div>
      </n-descriptions-item>
      <n-descriptions-item label="状态">
        <n-tag :type="record?.isRead === 1 ? 'success' : 'warning'" size="small">
          {{ record?.isRead === 1 ? '已读' : '未读' }}
        </n-tag>
      </n-descriptions-item>
      <n-descriptions-item label="创建时间">
        {{ record?.createTime || '-' }}
      </n-descriptions-item>
      <n-descriptions-item v-if="record?.readTime" label="阅读时间">
        {{ record.readTime }}
      </n-descriptions-item>
    </n-descriptions>
    <template #footer>
      <n-space justify="end">
        <n-button @click="emit('update:show', false)">关闭</n-button>
      </n-space>
    </template>
  </n-modal>
</template>

<script setup>
import { NModal, NDescriptions, NDescriptionsItem, NTag, NButton, NSpace } from 'naive-ui'

const typeTagMap = {
  1: { label: '系统公告', type: 'warning' },
  2: { label: '评论回复', type: 'info' },
  3: { label: '点赞提醒', type: 'success' }
}

defineProps({
  show: Boolean,
  record: { type: Object, default: null }
})

const emit = defineEmits(['update:show'])
</script>
