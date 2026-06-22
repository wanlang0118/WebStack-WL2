<template>
  <div class="login-page">
    <div class="login-split">
      
      <!-- Left: Brand / Editorial side -->
      <div class="login-brand-panel">
        <div class="brand-content">
          <div class="brand-logo">W</div>
          <h1 class="brand-title">WebStack</h1>
          <p class="brand-desc">现代化网址导航管理平台<br/>高效管理内容、分类与标签</p>
        </div>
        <div class="brand-footer">
          <p>© 2026 WebStack Admin</p>
        </div>
      </div>

      <!-- Right: Form side -->
      <div class="login-form-panel">
        <div class="login-card">
          <div class="login-header">
            <h2 class="card-title">欢迎回来</h2>
            <p class="card-subtitle">登录您的管理账户</p>
          </div>

          <n-form
            ref="formRef"
            :model="form"
            :rules="rules"
            :show-label="false"
            size="large"
          >
            <n-form-item path="username">
              <n-input
                v-model:value="form.username"
                placeholder="Username"
                class="login-input"
                @keyup.enter="handleLogin"
              >
                <template #prefix>
                  <n-icon :component="User" />
                </template>
              </n-input>
            </n-form-item>
            <n-form-item path="password">
              <n-input
                v-model:value="form.password"
                type="password"
                placeholder="Password"
                show-password-on="mousedown"
                class="login-input"
                @keyup.enter="handleLogin"
              >
                <template #prefix>
                  <n-icon :component="Lock" />
                </template>
              </n-input>
            </n-form-item>
            
            <n-form-item>
              <n-button 
                type="primary" 
                block 
                size="large" 
                :loading="loading" 
                @click="handleLogin"
                class="login-btn"
              >
                登录
              </n-button>
            </n-form-item>
          </n-form>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../../store/user'
import { NForm, NFormItem, NInput, NButton, NIcon } from 'naive-ui'
import { User, Lock } from 'lucide-vue-next'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: { required: true, message: '请输入用户名', trigger: 'blur' },
  password: { required: true, message: '请输入密码', trigger: 'blur' }
}

const handleLogin = () => {
  formRef.value?.validate(async (errors) => {
    if (errors) return
    loading.value = true
    try {
      await userStore.login(form)
      const redirect = route.query.redirect || '/'
      router.push(redirect)
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: var(--bg-page);
  display: flex;
}

.login-split {
  display: flex;
  width: 100%;
  min-height: 100vh;
}

/* ---- Left: Brand Panel (Dark/Accent) ---- */
.login-brand-panel {
  flex: 1;
  background: #0E0E11; /* Always dark for contrast */
  color: #FFFFFF;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 64px;
  position: relative;
  overflow: hidden;
}

.brand-logo {
  width: 48px;
  height: 48px;
  background: #F97316;
  color: #FFFFFF;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 32px;
}

.brand-title {
  font-size: 32px;
  font-weight: 600;
  letter-spacing: -0.02em;
  margin-bottom: 16px;
  color: #FFFFFF;
}

.brand-desc {
  font-size: 16px;
  line-height: 1.6;
  color: rgba(255, 255, 255, 0.6);
  max-width: 400px;
}

.brand-footer p {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.4);
}

/* ---- Right: Form Panel (Light/Theme) ---- */
.login-form-panel {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-page);
  padding: 48px;
}

.login-card {
  width: 100%;
  max-width: 380px;
}

.login-header {
  margin-bottom: 40px;
}

.card-title {
  font-size: 24px;
  font-weight: 600;
  letter-spacing: -0.01em;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.card-subtitle {
  font-size: 15px;
  color: var(--text-secondary);
}

/* ---- Form Overrides ---- */
.login-input :deep(.n-input) {
  border-radius: 6px;
}

.login-btn {
  margin-top: 8px;
  font-weight: 600;
}

/* ---- Responsive ---- */
@media (max-width: 900px) {
  .login-brand-panel {
    display: none; /* Hide brand side on small screens */
  }
}
</style>
