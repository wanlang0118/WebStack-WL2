import axios from 'axios'

const TOKEN_KEY = 'webstack_token'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

request.interceptors.request.use(
  config => {
    const token = localStorage.getItem(TOKEN_KEY)
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      if (res.code === 401) {
        localStorage.removeItem(TOKEN_KEY)
        const currentPath = window.location.pathname + window.location.search
        if (currentPath !== '/login') {
          window.location.href = `/login?redirect=${encodeURIComponent(currentPath)}`
        }
      }
      window.$message?.error?.(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res.data
  },
  error => {
    const message = error.response?.data?.message || error.message || '网络错误'
    if (error.response?.status === 401) {
      localStorage.removeItem(TOKEN_KEY)
      const currentPath = window.location.pathname + window.location.search
      if (currentPath !== '/login') {
        window.location.href = `/login?redirect=${encodeURIComponent(currentPath)}`
      }
    }
    window.$message?.error?.(message)
    return Promise.reject(error)
  }
)

export default request
