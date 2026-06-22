<template>
  <router-link :to="`/site/${site.id}`" class="site-card">
    <div class="card-icon">
      <img v-if="site.thumb && !imgError" :src="thumbSrc" :alt="site.title" loading="lazy"
           @error="imgError = true" />
      <div v-else class="card-icon-placeholder">
        {{ site.title?.charAt(0) || '?' }}
      </div>
    </div>
    <div class="card-info">
      <h3 class="card-title">{{ site.title }}</h3>
      <p class="card-desc">{{ site.description || '暂无描述' }}</p>
    </div>
  </router-link>
</template>

<script setup>
import { computed, ref, defineProps } from 'vue'

const props = defineProps({
  site: { type: Object, required: true }
})

const imgError = ref(false)

const thumbSrc = computed(() => {
  const t = props.site.thumb
  if (!t) return ''
  if (t.startsWith('http://') || t.startsWith('https://')) return t
  return `/uploads/${t}`
})
</script>

<style scoped>
.site-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 18px;
  background: var(--color-bg-card);
  border: 1px solid var(--color-border-light);
  border-radius: var(--radius-md);
  transition: box-shadow 0.2s cubic-bezier(0.22, 1, 0.36, 1),
              transform 0.2s cubic-bezier(0.22, 1, 0.36, 1),
              border-color 0.2s ease;
  cursor: pointer;
  overflow: hidden;
}

.site-card:hover {
  box-shadow: var(--shadow-card-hover);
  border-color: var(--color-primary-light);
  transform: translateY(-3px);
}

.card-icon {
  width: 42px; /* 图标等比调整为 42px */
  height: 42px;
  border-radius: var(--radius-md);
  overflow: hidden;
  flex-shrink: 0;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
}

.card-icon img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.card-icon-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  color: var(--color-primary);
  font-size: 1.1875rem; /* 等比调整至 1.1875rem */
  font-weight: 700;
}

.card-info {
  flex: 1;
  min-width: 0;
}

.card-title {
  font-size: 0.9375rem; /* 标题大小调为 15px */
  font-weight: 500; /* 字重降为更秀气的 500 */
  color: var(--color-text);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 2px; /* 间距微调 */
  line-height: 1.3;
}

@media (prefers-reduced-motion: reduce) {
  .site-card {
    transition: none;
  }
}

.card-desc {
  font-size: 0.8125rem; /* 描述大小调为 13px */
  color: var(--color-text-secondary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 1.4;
}
</style>
