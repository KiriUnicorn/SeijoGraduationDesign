import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

const store = new Vuex.Store({
  state: {
    token: uni.getStorageSync('token') || '',
    userInfo: uni.getStorageSync('userInfo') || null,
    role: uni.getStorageSync('role') || ''
  },
  mutations: {
    setToken(state, token) {
      console.log('设置token:', token)
      state.token = token
      uni.setStorageSync('token', token)
      console.log('token已保存到state和storage')
    },
    setUserInfo(state, userInfo) {
      console.log('设置用户信息:', userInfo)
      state.userInfo = userInfo
      uni.setStorageSync('userInfo', userInfo)
      console.log('用户信息已保存到state和storage')
    },
    setRole(state, role) {
      console.log('设置角色:', role)
      state.role = role
      uni.setStorageSync('role', role)
      console.log('角色已保存到state和storage')
    },
    logout(state) {
      console.log('执行登出操作')
      state.token = ''
      state.userInfo = null
      state.role = ''
      uni.removeStorageSync('token')
      uni.removeStorageSync('userInfo')
      uni.removeStorageSync('role')
      console.log('登出完成，状态已清除')
    }
  },
  actions: {
    login({ commit }, { token, userInfo, role }) {
      console.log('执行登录操作，参数:', { token, userInfo, role })
      try {
        if (!token || !userInfo || !role) {
          throw new Error('登录参数不完整')
        }
        
        commit('setToken', token)
        commit('setUserInfo', userInfo)
        commit('setRole', role)
        
        // 验证状态是否已更新
        const currentToken = uni.getStorageSync('token')
        const currentUserInfo = uni.getStorageSync('userInfo')
        const currentRole = uni.getStorageSync('role')
        
        console.log('验证当前token:', currentToken)
        console.log('验证当前用户信息:', currentUserInfo)
        console.log('验证当前角色:', currentRole)
        
        if (!currentToken || !currentUserInfo || !currentRole) {
          throw new Error('登录状态保存失败')
        }
        
        console.log('登录状态已更新')
      } catch (error) {
        console.error('登录操作失败:', error)
        throw error
      }
    },
    logout({ commit }) {
      console.log('执行登出操作')
      try {
        commit('logout')
        console.log('登出状态已更新')
      } catch (error) {
        console.error('登出操作失败:', error)
        throw error
      }
    }
  },
  getters: {
    isLoggedIn: state => !!state.token && !!state.userInfo && !!state.role,
    userRole: state => state.role
  }
})

export default store 