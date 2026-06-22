<template>
  <div ref="cardRef" class="stat-card">
    <div class="stat-header">
      <span class="stat-label">{{ label }}</span>
      <n-icon :component="icon" class="stat-icon" :stroke-width="1.5" />
    </div>
    <div class="stat-body">
      <div class="stat-value" v-if="!loading">{{ formattedValue }}</div>
      <div class="stat-value skeleton-num" v-else>
        <span class="skeleton" style="width: 80px; height: 32px; display: block;" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useCountUp } from '../composables/useCountUp'
import { NIcon } from 'naive-ui'

const props = defineProps({
  label: { type: String, required: true },
  value: { type: Number, default: 0 },
  icon: { type: Object, required: true },
  loading: { type: Boolean, default: false }
})

const cardRef = ref(null)
const { current, reset } = useCountUp(() => props.value, {
  observeRef: cardRef
})

watch(() => props.value, (val) => {
  if (val > 0) reset(val)
})

const formattedValue = computed(() => {
  return current.value.toLocaleString()
})
</script>

<style scoped>
.stat-card {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 24px;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  box-shadow: none;
  cursor: default;
  transition: border-color var(--transition-fast) ease, background var(--transition-fast) ease;
}

.stat-card:hover {
  background: var(--table-row-hover); /* Gentle background change instead of border flash */
}

.stat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-label {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-secondary);
}

.stat-icon {
  font-size: 18px;
  color: var(--text-placeholder); /* Neutral icon color */
}

.stat-body {
  width: 100%;
}

.stat-value {
  font-size: 32px;
  font-weight: 600;
  color: var(--text-primary);
  line-height: 1;
  letter-spacing: -0.03em;
}
</style>
