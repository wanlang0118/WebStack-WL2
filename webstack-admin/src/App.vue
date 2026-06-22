<template>
  <n-config-provider :theme="theme" :theme-overrides="themeOverrides">
    <n-global-style />
    <n-message-provider>
      <n-dialog-provider>
        <n-notification-provider>
          <router-view />
        </n-notification-provider>
      </n-dialog-provider>
    </n-message-provider>
  </n-config-provider>
</template>

<script setup>
import { computed } from 'vue'
import { NConfigProvider, NGlobalStyle, NMessageProvider, NDialogProvider, NNotificationProvider, darkTheme } from 'naive-ui'
import { useThemeStore } from './store/theme'
import { darkOverrides, lightOverrides } from './theme/naiveTheme'

const themeStore = useThemeStore()

const theme = computed(() => themeStore.isDark ? darkTheme : null)
const themeOverrides = computed(() => themeStore.isDark ? darkOverrides : lightOverrides)
</script>
