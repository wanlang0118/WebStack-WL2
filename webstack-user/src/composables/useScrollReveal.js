import { ref, onMounted, onUnmounted } from 'vue'

/**
 * 轻量滚动渐入 composable
 * @param {Function} selectorFn - 返回要观察的元素列表的函数
 * @param {Object} options - IntersectionObserver 选项
 */
export function useScrollReveal(selectorFn, options = {}) {
  const observer = ref(null)

  const defaultOptions = {
    threshold: 0.08,
    rootMargin: '0px 0px -40px 0px',
    ...options
  }

  onMounted(() => {
    observer.value = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          entry.target.classList.add('revealed')
          observer.value.unobserve(entry.target)
        }
      })
    }, defaultOptions)

    const els = selectorFn()
    if (els?.length) {
      els.forEach(el => observer.value.observe(el))
    }
  })

  onUnmounted(() => {
    if (observer.value) {
      observer.value.disconnect()
    }
  })
}
