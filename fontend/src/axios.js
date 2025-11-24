// import axios from 'axios'

// const api = axios.create({
//   baseURL: 'http://localhost:8080',
//   timeout: 10000
// })

// api.interceptors.request.use(
//   (config) => {
//     const token = localStorage.getItem('token')
//     if (token) {
//       config.headers.Authorization = `Bearer ${token}`
//     }
//     return config
//   },
//   (error) => Promise.reject(error)
// )

// export default api
import axios from 'axios'
import router from '@/router' // import router để chuyển trang

const api = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000
})

api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

// ✅ Xử lý lỗi 401/403 - chỉ chuyển về login cho một số API cụ thể
api.interceptors.response.use(
  response => response,
  error => {
    const status = error.response?.status
    const url = error.config?.url || ''

    // Chỉ chuyển hướng về login cho các API yêu cầu authentication
    // Không chuyển hướng cho các API public như news, products, etc.
    if (status === 401 || status === 403) {
      // Danh sách các API không cần chuyển hướng khi lỗi 401/403
      const publicApis = [
        '/api/news',
        '/api/products',
        '/api/home',
        '/api/categories',
        '/api/search',
        '/api/news-comments',
        '/api/favorites'
      ]
      
      // API users/profile cần xử lý đặc biệt - không chuyển hướng ngay
      const isProfileApi = url.includes('/api/users/profile')
      const isPublicApi = publicApis.some(api => url.includes(api))
      
      const shouldRedirect = !isPublicApi && !isProfileApi
      
      if (shouldRedirect) {
        localStorage.removeItem('token') // xoá token nếu cần
        router.push('/dangNhap')           // chuyển hướng về login
      }
    }

    return Promise.reject(error)
  }
)

export default api
