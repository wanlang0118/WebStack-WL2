<template>
  <!-- Remix Icon 类名，或者是通过标题智能匹配到的 Remix Icon -->
  <i v-if="resolvedIcon" :class="[resolvedIcon]" class="cat-icon cat-icon--ri"></i>

  <!-- 图片：完整 URL、/uploads/ 路径，或管理端上传的相对路径 (未匹配到内置图标时尝试加载) -->
  <img
    v-else-if="isImage && !imgError"
    :src="imageSrc"
    class="cat-icon cat-icon--img"
    :width="imgSize"
    :height="imgSize"
    loading="lazy"
    :alt="title || '分类图标'"
    @error="imgError = true"
  />

  <!-- 兜底：如果都没有或图片加载失败，显示文件夹图标（相比彩色文字框，大幅提升侧边栏的视觉统一性） -->
  <i v-else class="cat-icon cat-icon--ri ri-folder-line"></i>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { getImageUrl } from '@/utils/image'
import { normalizeIcon, matchIconByTitle } from '@shared/categoryIcons.js'

const props = defineProps({
  icon: { type: String, default: '' },
  size: { type: String, default: 'md' },
  title: { type: String, default: '' },
  fallback: { type: String, default: '' }
})

const imgError = ref(false)

watch(() => props.icon, () => {
  imgError.value = false
})

const isRiClass = computed(() => typeof props.icon === 'string' && props.icon.startsWith('ri-'))

const resolvedIcon = computed(() => {
  if (isRiClass.value) {
    return normalizeIcon(props.icon)
  }
  return matchIconByTitle(props.title)
})

/** 非 Remix 且有值的 icon，按图片路径处理（与管理端 getImageUrl 一致） */
const isImage = computed(() => {
  const value = props.icon?.trim()
  return Boolean(value && !isRiClass.value)
})

const imageSrc = computed(() => getImageUrl(props.icon?.trim() || ''))

const imgSize = computed(() => ({ sm: 14, md: 16, lg: 20 }[props.size] ?? 16))
</script>

<style scoped>
.cat-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.cat-icon--ri {
  font-style: normal;
  line-height: 1;
}

.cat-icon--img {
  border-radius: 3px;
  object-fit: cover;
  vertical-align: middle;
}
</style>
