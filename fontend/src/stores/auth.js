import { defineStore } from 'pinia'
import { jwtDecode } from 'jwt-decode'  // Sửa lại dòng này

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    user: null
  }),
  actions: {
    setToken(token) {
      this.token = token
      localStorage.setItem('token', token)
      this.user = jwtDecode(token)  // dùng jwtDecode
    },
    logout() {
      this.token = ''
      this.user = null
      localStorage.removeItem('token')
    },
    loadUserFromToken() {
      const token = localStorage.getItem('token')
      if (token) {
        this.token = token
        this.user = jwtDecode(token)  // dùng jwtDecode
      }
    }
  }
})
