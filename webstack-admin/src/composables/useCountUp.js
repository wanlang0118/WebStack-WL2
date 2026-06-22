import { ref, onMounted, onBeforeUnmount } from 'vue'

function easeOutCubic(t) {
  return 1 - Math.pow(1 - t, 3)
}

export function useCountUp(targetValue, options = {}) {
  const {
    duration = 1500,
    observeRef = null,
    startOnMount = false
  } = options

  const current = ref(0)
  let started = false
  let rafId = null
  let observer = null

  const animate = () => {
    if (started) return
    started = true
    const start = performance.now()
    const end = typeof targetValue === 'function' ? targetValue() : targetValue

    const tick = (now) => {
      const elapsed = now - start
      const progress = Math.min(elapsed / duration, 1)
      current.value = Math.round(easeOutCubic(progress) * end)

      if (progress < 1) {
        rafId = requestAnimationFrame(tick)
      }
    }

    rafId = requestAnimationFrame(tick)
  }

  const reset = (newTarget) => {
    started = false
    if (rafId) cancelAnimationFrame(rafId)
    if (newTarget !== undefined) {
      const start = performance.now()
      const end = typeof newTarget === 'function' ? newTarget() : newTarget

      const tick = (now) => {
        const elapsed = now - start
        const progress = Math.min(elapsed / duration, 1)
        current.value = Math.round(easeOutCubic(progress) * end)
        if (progress < 1) {
          rafId = requestAnimationFrame(tick)
        }
      }
      started = true
      rafId = requestAnimationFrame(tick)
    }
  }

  onMounted(() => {
    if (startOnMount && !observeRef) {
      animate()
      return
    }

    if (observeRef?.value) {
      observer = new IntersectionObserver(
        (entries) => {
          if (entries[0].isIntersecting) {
            animate()
            observer?.disconnect()
          }
        },
        { threshold: 0.1 }
      )
      observer.observe(observeRef.value)
    } else {
      animate()
    }
  })

  onBeforeUnmount(() => {
    if (rafId) cancelAnimationFrame(rafId)
    observer?.disconnect()
  })

  return { current, reset, animate }
}
