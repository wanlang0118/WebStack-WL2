<template>
  <Teleport to="body">
    <div class="toast-container" aria-live="polite">
      <TransitionGroup name="toast">
        <div
          v-for="t in toasts"
          :key="t.id"
          class="toast"
          :class="[`toast--${t.type}`]"
          role="alert"
        >
          <i class="toast-icon" :class="iconClass(t.type)"></i>
          <span class="toast-text">{{ t.message }}</span>
          <button class="toast-close" @click="remove(t.id)" aria-label="关闭">&times;</button>
        </div>
      </TransitionGroup>
    </div>
  </Teleport>
</template>

<script setup>
import { useToast } from '@/composables/useToast'

const { toasts, remove } = useToast()

function iconClass(type) {
  const map = {
    success: 'ri-checkbox-circle-line',
    error: 'ri-error-warning-line',
    warning: 'ri-alert-line',
    info: 'ri-information-line'
  }
  return map[type] || map.info
}
</script>

<style scoped>
.toast-container {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 1000;
  display: flex;
  flex-direction: column;
  gap: 8px;
  pointer-events: none;
}

.toast {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  background: var(--color-bg-card);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-card-hover);
  pointer-events: auto;
  max-width: 380px;
  font-size: 0.875rem;
  color: var(--color-text);
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

.toast-icon {
  font-size: 1.25rem;
  flex-shrink: 0;
}

.toast--success .toast-icon { color: var(--color-success); }
.toast--error .toast-icon { color: var(--color-danger); }
.toast--warning .toast-icon { color: var(--color-warning); }
.toast--info .toast-icon { color: var(--color-primary); }

.toast-text {
  flex: 1;
  line-height: 1.4;
}

.toast-close {
  flex-shrink: 0;
  background: none;
  border: none;
  font-size: 1.125rem;
  color: var(--color-text-light);
  cursor: pointer;
  padding: 2px;
  line-height: 1;
  transition: color 0.15s;
}

.toast-close:hover {
  color: var(--color-text);
}

/* 入场动画 */
.toast-enter-active {
  animation: toast-in 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

.toast-leave-active {
  animation: toast-out 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes toast-in {
  from {
    opacity: 0;
    transform: translateX(100%) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateX(0) scale(1);
  }
}

@keyframes toast-out {
  from {
    opacity: 1;
    transform: translateX(0) scale(1);
  }
  to {
    opacity: 0;
    transform: translateX(100%) scale(0.95);
  }
}

@media (prefers-reduced-motion: reduce) {
  .toast {
    transition: none;
  }
  .toast-enter-active,
  .toast-leave-active {
    animation: none;
  }
}
</style>
