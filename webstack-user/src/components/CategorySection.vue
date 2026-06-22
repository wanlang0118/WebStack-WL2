<template>
  <section :id="`cat-${category.id}`" class="category-section">
    <div class="section-header">
      <h2 class="section-title">
        <CategoryIcon :icon="category.icon" :title="category.title" size="lg" class="section-icon" />
        {{ category.title }}
      </h2>
    </div>

    <div class="section-subtabs" v-if="category.children?.length">
      <button class="subtab" :class="{ active: activeChild === null }"
              @click="activeChild = null">
        {{ category.title }}
      </button>
      <button class="subtab" v-for="child in category.children" :key="child.id"
              :class="{ active: activeChild === child.id }"
              @click="activeChild = child.id">
        {{ child.title }}
      </button>
    </div>

    <div class="section-grid" v-if="displaySites.length">
      <div v-for="(site, idx) in displaySites" :key="site.id" class="card-cell" :style="{ '--i': idx }">
        <SiteCard :site="site" />
      </div>
    </div>
    <div class="section-empty" v-else>
      <div class="empty-icon"><i class="ri-folder-line"></i></div>
      <p class="empty-title">暂无网站</p>
      <p class="empty-desc">该分类下还没有收录网站</p>
    </div>
  </section>
</template>

<script setup>
import { computed, ref, defineProps } from 'vue'
import SiteCard from './SiteCard.vue'
import CategoryIcon from './CategoryIcon.vue'

const props = defineProps({
  category: { type: Object, required: true },
  siteMap: { type: Object, default: () => ({}) }
})

const activeChild = ref(null)

const displaySites = computed(() => {
  if (activeChild.value !== null) {
    return props.siteMap[activeChild.value] || []
  }
  // 只返回当前主分类自己的数据，不整合子分类数据
  return props.siteMap[props.category.id] || []
})
</script>

<style scoped>
.category-section {
  margin-bottom: 48px;
  container-type: inline-size;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.section-title {
  font-family: var(--font-serif);
  font-size: 1.375rem;
  font-weight: 700;
  color: var(--color-text);
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-icon {
  font-size: 18px;
}

.section-subtabs {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
  margin-bottom: 14px;
}

.subtab {
  padding: 5px 14px;
  background: var(--color-bg);
  color: var(--color-text-secondary);
  font-size: 0.8125rem;
  border-radius: var(--radius-full, 9999px);
  transition: color 0.15s, background 0.15s;
}

.subtab:hover {
  color: var(--color-primary);
  background: var(--color-tag-bg);
}

.subtab.active {
  background: var(--color-primary);
  color: #fff;
}

.section-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 14px;
}

/* 卡片入场交错动画 */
.card-cell {
  opacity: 0;
  transform: translateY(12px);
  animation: card-enter 0.35s cubic-bezier(0.22, 1, 0.36, 1) forwards;
  animation-delay: calc(var(--i, 0) * 40ms);
}

@keyframes card-enter {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (prefers-reduced-motion: reduce) {
  .card-cell {
    opacity: 1;
    transform: none;
    animation: none;
  }
}

.section-empty {
  padding: 32px;
  text-align: center;
}

.empty-icon {
  font-size: 2rem;
  margin-bottom: 8px;
  color: var(--color-text-light);
}

.empty-title {
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--color-text-secondary);
  margin-bottom: 2px;
}

.empty-desc {
  font-size: 0.75rem;
  color: var(--color-text-light);
}
</style>
