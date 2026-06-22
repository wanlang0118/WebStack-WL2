import { reactive, readonly } from 'vue'

const state = reactive({
  toasts: []
})
let nextId = 0

export function useToast() {
  function show(message, type = 'info', duration = 3000) {
    const id = nextId++
    state.toasts.push({ id, message, type })
    setTimeout(() => {
      const idx = state.toasts.findIndex(t => t.id === id)
      if (idx !== -1) state.toasts.splice(idx, 1)
    }, duration)
  }

  function remove(id) {
    const idx = state.toasts.findIndex(t => t.id === id)
    if (idx !== -1) state.toasts.splice(idx, 1)
  }

  return {
    toasts: readonly(state).toasts,
    show,
    remove
  }
}
