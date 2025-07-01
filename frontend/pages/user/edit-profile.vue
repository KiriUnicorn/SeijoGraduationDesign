<template>
  <view class="container">
    <view class="form-container">
      <view class="form-title">编辑个人资料</view>
      
      
      <view class="form-item">
        <text class="label">真实姓名</text>
        <input class="input" type="text" v-model="userInfo.name" placeholder="请输入真实姓名" />
      </view>
      
      <view class="form-item">
        <text class="label">电子邮箱</text>
        <input class="input" type="text" v-model="userInfo.email" placeholder="请输入电子邮箱" />
      </view>
      
      <view class="form-item">
        <text class="label">角色</text>
        <input class="input" type="text" :value="getRoleText(userInfo.role)" disabled />
      </view>
      
      <view class="button-group">
        <button class="btn btn-primary" @click="handleUpdate">保存修改</button>
        <button class="btn btn-default" @click="goBack">返回</button>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      userInfo: null
    }
  },
  onLoad() {
    this.loadUserInfo()
  },
  methods: {
    loadUserInfo() {
      try {
        const userInfo = uni.getStorageSync('userInfo')
        if (!userInfo) {
          throw new Error('未找到用户信息')
        }
        this.userInfo = userInfo
      } catch (error) {
        console.error('获取用户信息失败:', error)
        uni.showToast({
          title: '获取用户信息失败',
          icon: 'none'
        })
      }
    },

    getRoleText(role) {
      const roleMap = {
        'student': '学生',
        'teacher': '教师',
        'admin': '管理员'
      }
      return roleMap[role] || role
    },

    async handleUpdate() {
      try {
        // 验证真实姓名
        if (!this.userInfo.name) {
          uni.showToast({
            title: '请输入真实姓名',
            icon: 'none'
          })
          return
        }

        // 验证电子邮箱
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
        if (!this.userInfo.email || !emailRegex.test(this.userInfo.email)) {
          uni.showToast({
            title: '请输入有效的电子邮箱',
            icon: 'none'
          })
          return
        }

        const token = uni.getStorageSync('token')
        const [error, res] = await uni.request({
          url: `http://localhost:8080/api/users/${this.userInfo.userId}`,
          method: 'PUT',
          header: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          },
          data: {
            name: this.userInfo.name,
            email: this.userInfo.email,
            role: this.userInfo.role
          }
        })

        if (error) {
          throw new Error(error.errMsg)
        }

        if (res.data.code === 200) {
          // 更新本地存储的用户信息
          uni.setStorageSync('userInfo', {
            ...this.userInfo,
            name: this.userInfo.name,
            email: this.userInfo.email
          })
          
          uni.showToast({
            title: '更新成功',
            icon: 'success'
          })
          setTimeout(() => {
            uni.navigateBack()
          }, 1500)
        } else {
          throw new Error(res.data.message)
        }
      } catch (error) {
        console.error('更新用户信息失败:', error)
        uni.showToast({
          title: error.message || '更新失败',
          icon: 'none'
        })
      }
    },

    goBack() {
      uni.navigateBack()
    }
  }
}
</script>

<style>
.container {
  padding: 20px;
  background-color: #F8F8F8;
  min-height: 100vh;
}

.form-container {
  background-color: #FFFFFF;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.form-title {
  font-size: 20px;
  font-weight: bold;
  color: #333333;
  margin-bottom: 20px;
  text-align: center;
}

.form-item {
  margin-bottom: 20px;
}

.label {
  display: block;
  font-size: 14px;
  color: #666666;
  margin-bottom: 8px;
}

.input {
  width: 100%;
  height: 40px;
  border: 1px solid #DDDDDD;
  border-radius: 4px;
  padding: 0 12px;
  font-size: 14px;
  color: #333333;
  background-color: #FFFFFF;
}

.input[disabled] {
  background-color: #F5F5F5;
  color: #999999;
}

.button-group {
  margin-top: 30px;
  display: flex;
  justify-content: space-between;
}

.btn {
  flex: 1;
  margin: 0 10px;
  height: 40px;
  line-height: 40px;
  text-align: center;
  border-radius: 4px;
  font-size: 16px;
}

.btn-primary {
  background-color: #007AFF;
  color: #FFFFFF;
}

.btn-default {
  background-color: #F5F5F5;
  color: #666666;
}
</style> 