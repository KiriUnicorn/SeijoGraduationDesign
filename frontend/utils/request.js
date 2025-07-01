import axios from 'axios'
import store from '../store'

// 创建axios实例
const service = axios.create({
  baseURL: 'http://localhost:8080/api', // API的base_url
  timeout: 5000 // 请求超时时间
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 在发送请求之前做些什么
    const token = store.state.token
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  error => {
    // 对请求错误做些什么
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    // 对响应数据做点什么
    const res = response.data
    
    // 检查响应状态
    if (response.status === 200 && res.code === 200) {
      return res.data
    }
    
    if (res.code !== 0 && res.code !== 200) {
      uni.showToast({
        title: res.message || '请求失败',
        icon: 'none',
        duration: 2000
      })
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    
    return res.data
  },
  error => {
    // 对响应错误做点什么
    console.error('响应错误:', error)
    let errorMessage = '请求失败'
    if (error.response) {
      switch (error.response.status) {
        case 401:
          errorMessage = '未登录或登录已过期'
          break
        case 403:
          errorMessage = '没有权限访问'
          break
        case 404:
          errorMessage = '请求的资源不存在'
          break
        case 500:
          errorMessage = '服务器错误，请稍后重试'
          break
        default:
          errorMessage = error.response.data?.message || '请求失败'
      }
    }
    uni.showToast({
      title: errorMessage,
      icon: 'none',
      duration: 2000
    })
    return Promise.reject(error)
  }
)

export default service 