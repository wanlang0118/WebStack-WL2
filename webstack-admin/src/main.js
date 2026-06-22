import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import naive, { createDiscreteApi, darkTheme } from 'naive-ui'
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import { darkOverrides, lightOverrides } from './theme/naiveTheme'

import './styles/variables.css'
import './styles/global.css'

const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)

const getThemeConfig = () => {
  const stored = localStorage.getItem('theme')
  const isDark = stored ? JSON.parse(stored).isDark : true
  return {
    theme: isDark ? darkTheme : null,
    themeOverrides: isDark ? darkOverrides : lightOverrides
  }
}

const config = getThemeConfig()

const { message, dialog, notification, loadingBar } = createDiscreteApi(
  ['message', 'dialog', 'notification', 'loadingBar'],
  {
    configProviderProps: {
      theme: config.theme,
      themeOverrides: config.themeOverrides
    },
    messageProviderProps: {
      duration: 3000,
      keepAliveOnHover: true
    }
  }
)

window.$message = message
window.$dialog = dialog
window.$notification = notification
window.$loadingBar = loadingBar

const app = createApp(App)
app.use(pinia)
app.use(router)
app.use(naive)
app.mount('#app')
