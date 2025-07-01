<template>
  <view class="container">
    <!-- 返回箭头 -->
    <view class="back-arrow" @tap="handleBack">
      <text class="arrow">←</text>
    </view>

    <view class="login-box">
      <text class="title">竞赛管理系统</text>
      
      <!-- 角色选择 -->
      <view class="role-select">
        <view 
          v-for="item in roles" 
          :key="item.value"
          :class="['role-item', selectedRole === item.value ? 'active' : '']"
          @tap="selectedRole = item.value"
        >
          {{ item.label }}
        </view>
      </view>
      
      <!-- 登录表单 -->
      <view class="form">
        <input 
          class="input" 
          type="text" 
          v-model="email" 
          placeholder="请输入邮箱"
        />
        <input 
          class="input" 
          type="password" 
          v-model="password" 
          placeholder="请输入密码"
        />
        <!-- 验证码输入框和显示区域 -->
        <view class="captcha-container">
          <input 
            class="input captcha-input" 
            type="text" 
            v-model="captcha" 
            placeholder="请输入验证码"
            maxlength="4"
          />
          <view class="captcha-display" @tap="refreshCaptcha">
            {{ captchaText }}
          </view>
        </view>
        <button class="btn" @tap="handleLogin">登录</button>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      roles: [
        { label: '学生', value: 'student' },
        { label: '教师', value: 'teacher' },
        { label: '管理员', value: 'admin' }
      ],
      selectedRole: 'student',
      email: '',
      password: '',
      captcha: '', // 用户输入的验证码
      captchaText: '', // 显示的验证码
      captchaLength: 4 // 验证码长度
    }
  },
  created() {
    // 页面创建时生成验证码
    this.refreshCaptcha()
  },
  methods: {
    // 生成随机验证码
    generateCaptcha() {
      const chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'
      let result = ''
      for (let i = 0; i < this.captchaLength; i++) {
        result += chars.charAt(Math.floor(Math.random() * chars.length))
      }
      return result
    },
    
    // 刷新验证码
    refreshCaptcha() {
      this.captchaText = this.generateCaptcha()
      this.captcha = '' // 清空用户输入的验证码
    },

    handleBack() {
      uni.switchTab({
        url: '/pages/index/index'
      })
    },

    // 验证码验证
    validateCaptcha() {
      if (!this.captcha) {
        uni.showToast({
          title: '请输入验证码',
          icon: 'none'
        })
        return false
      }
      
      if (this.captcha.toLowerCase() !== this.captchaText.toLowerCase()) {
        uni.showToast({
          title: '验证码错误',
          icon: 'none'
        })
        this.refreshCaptcha() // 验证失败时刷新验证码
        return false
      }
      
      return true
    },

    async handleLogin() {
      // 表单验证
      if (!this.email || !this.password) {
        uni.showToast({
          title: '请填写完整信息',
          icon: 'none'
        })
        return
      }

      // 验证码验证
      if (!this.validateCaptcha()) {
        return
      }

      try {
        // 根据角色选择不同的登录接口
        const apiUrl = `http://localhost:8080/api/auth/${this.selectedRole}/login`
        console.log('登录请求URL:', apiUrl)
        console.log('登录请求数据:', {
          email: this.email,
          password: this.password
        })

        const [error, response] = await uni.request({
          url: apiUrl,
          method: 'POST',
          data: {
            email: this.email,
            password: this.password
          },
          header: {
            'Content-Type': 'application/json'
          }
        })

        console.log('登录响应:', response)

        if (error) {
          throw new Error(error.errMsg || '网络请求失败')
        }

        const resData = response.data
        if (resData.code === 200) {
          // 保存登录信息
          const token = resData.data
          // 确保token格式正确
          const formattedToken = token.startsWith('Bearer ') ? token : `Bearer ${token}`
          uni.setStorageSync('token', formattedToken)
          uni.setStorageSync('role', this.selectedRole)
          
          // 解析JWT token获取用户信息
          const tokenParts = token.split('.')
          if (tokenParts.length === 3) {
            try {
              // 使用decodeURIComponent和atob组合来正确解码中文
              const base64Url = tokenParts[1]
              const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
              const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
                return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)
              }).join(''))
              
              const payload = JSON.parse(jsonPayload)
              const userInfo = {
                userId: payload.userId,
                email: payload.email,
                role: payload.role,
                name: payload.name,
                departmentId: payload.departmentId
              }
              uni.setStorageSync('userInfo', userInfo)
              console.log('保存的用户信息:', userInfo)

              uni.showToast({
                title: '登录成功',
                icon: 'success'
              })

              // 确保数据保存完成后再跳转
              setTimeout(() => {
                // 先跳转到首页
                uni.switchTab({
                  url: '/pages/index/index',
                  success: () => {
                    console.log('跳转到首页成功')
                    // 延迟一下再跳转到个人中心
                    setTimeout(() => {
                      uni.switchTab({
                        url: '/pages/user/profile',
                        success: () => {
                          console.log('跳转到个人中心成功')
                        },
                        fail: (err) => {
                          console.error('跳转到个人中心失败:', err)
                        }
                      })
                    }, 100)
                  },
                  fail: (err) => {
                    console.error('跳转到首页失败:', err)
                  }
                })
              }, 1500)
            } catch (e) {
              console.error('解析token失败:', e)
              uni.showToast({
                title: '登录失败，请重试',
                icon: 'none'
              })
            }
          } else {
            throw new Error('无效的token格式')
          }
        } else {
          throw new Error(resData.message || '登录失败')
        }
      } catch (error) {
        console.error('登录错误:', error)
		this.refreshCaptcha()
        uni.showToast({
          title: error.message || '网络错误，请稍后再试',
          icon: 'none',
          duration: 2000
        })
      }
    }
  }
}
</script>

<style>
.container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #F8F8F8;
  position: relative;
}

.back-arrow {
  position: absolute;
  top: 20px;
  left: 20px;
  width: 40px;
  height: 40px;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #FFFFFF;
  border-radius: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  z-index: 100;
}

.arrow {
  font-size: 24px;
  color: #333333;
  font-weight: bold;
}

.back-arrow:active {
  opacity: 0.8;
}

.login-box {
  width: 90%;
  max-width: 400px;
  padding: 30px;
  background-color: #FFFFFF;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.title {
  font-size: 24px;
  font-weight: bold;
  text-align: center;
  margin-bottom: 30px;
  color: #333333;
}

.role-select {
  display: flex;
  justify-content: space-between;
  margin-bottom: 30px;
}

.role-item {
  flex: 1;
  text-align: center;
  padding: 10px;
  margin: 0 5px;
  border-radius: 4px;
  background-color: #F5F5F5;
  color: #666666;
  font-size: 14px;
}

.role-item.active {
  background-color: #7799CC;
  color: #FFFFFF;
}

.form {
  display: flex;
  flex-direction: column;
}

.input {
  height: 45px;
  background-color: #F5F5F5;
  border-radius: 4px;
  padding: 0 15px;
  margin-bottom: 15px;
  font-size: 14px;
}

/* 验证码相关样式 */
.captcha-container {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
}

.captcha-input {
  flex: 1;
}

.captcha-display {
  width: 120px;
  height: 45px;
  background-color: #F5F5F5;
  border-radius: 4px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 20px;
  font-weight: bold;
  color: #333333;
  letter-spacing: 2px;
  user-select: none;
  cursor: pointer;
}

.captcha-display:active {
  opacity: 0.8;
}

.btn {
  height: 45px;
  background-color: #7799CC;
  color: #FFFFFF;
  border-radius: 4px;
  font-size: 16px;
  margin-top: 10px;
}

.btn:active {
  opacity: 0.8;
}
</style> 