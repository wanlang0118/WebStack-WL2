import { ref, watch, onMounted, onUnmounted } from 'vue'

const MOBILE_BREAKPOINT = 768

export function useMobileLayout() {
  const isMobile = ref(false)
  const mobileNavOpen = ref(false)

  function checkMobile() {
    isMobile.value = window.innerWidth <= MOBILE_BREAKPOINT
    if (!isMobile.value) {
      mobileNavOpen.value = false
    }
  }

  function openMobileNav() {
    if (isMobile.value) {
      mobileNavOpen.value = true
    }
  }

  function closeMobileNav() {
    mobileNavOpen.value = false
  }

  watch(mobileNavOpen, (open) => {
    document.body.style.overflow = open ? 'hidden' : ''
  })

  onMounted(() => {
    checkMobile()
    window.addEventListener('resize', checkMobile)
  })

  onUnmounted(() => {
    window.removeEventListener('resize', checkMobile)
    document.body.style.overflow = ''
  })

  return { isMobile, mobileNavOpen, openMobileNav, closeMobileNav }
}
