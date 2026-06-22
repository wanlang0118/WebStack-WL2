import { defineStore } from 'pinia'
import { login, getCurrentUser } from '../api/auth'

const TOKEN_KEY = 'webstack_token'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem(TOKEN_KEY) || '',
    userInfo: null
  }),

  getters: {
    isLoggedIn: (state) => !!state.token
  },

  actions: {
    async login(credentials) {
      const res = await login(credentials)
      this.token = res.token
      this.userInfo = res.userInfo
      localStorage.setItem(TOKEN_KEY, res.token)
      return res
    },

    async fetchUserInfo() {
      if (!this.token) return
      try {
        const user = await getCurrentUser()
        this.userInfo = user
      } catch (e) {
        this.logout()
        throw e
      }
    },

    logout() {
      this.token = ''
      this.userInfo = null
      localStorage.removeItem(TOKEN_KEY)
    }
  }
})
