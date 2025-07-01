<template>
  <view class="container">
    <view class="user-info">
      <view class="avatar">
        <text class="avatar-text">{{ userInfo.email ? userInfo.email[0].toUpperCase() : 'U' }}</text>
      </view>
      <view class="info">
        <text class="email">{{ userInfo.email }}</text>
        <text class="role">{{ roleText }}</text>
      </view>
    </view>

    <view class="menu-list">
      <view class="menu-item" @tap="handleEditProfile">
        <text class="menu-text">编辑个人信息</text>
        <text class="arrow">></text>
      </view>
      <view class="menu-item" @tap="handleMyCompetitions">
        <text class="menu-text">我的竞赛</text>
        <text class="arrow">></text>
      </view>
      <view class="menu-item" @tap="handleLogout">
        <text class="menu-text">退出登录</text>
        <text class="arrow">></text>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      userInfo: {},
      role: ''
    }
  },
  computed: {
    roleText() {
      const roleMap = {
        student: '学生',
        teacher: '教师',
        admin: '管理员'
      }
      return roleMap[this.role] || '未知角色'
    }
  },
  onLoad() {
    console.log('个人中心页面加载')
    this.checkLoginStatus()
  },
  onShow() {
    console.log('个人中心页面显示')
    this.checkLoginStatus()
  },
  methods: {
    checkLoginStatus() {
      const token = uni.getStorageSync('token')
      const userInfo = uni.getStorageSync('userInfo')
      const role = uni.getStorageSync('role')

      console.log('个人中心检查登录状态:', { 
        token: token ? '存在' : '不存在',
        userInfo: userInfo ? JSON.stringify(userInfo) : '不存在',
        role: role || '不存在'
      })

      if (!token || !userInfo || !role) {
        console.log('个人中心: 登录状态无效，跳转到登录页')
        uni.reLaunch({
          url: '/pages/user/login'
        })
        return
      }

      // 验证token是否过期
      try {
        const tokenParts = token.split('.')
        if (tokenParts.length === 3) {
          const payload = JSON.parse(atob(tokenParts[1]))
          const expirationTime = payload.exp * 1000 // 转换为毫秒
          if (Date.now() >= expirationTime) {
            console.log('个人中心: Token已过期')
            this.handleLogout()
            return
          }
          console.log('个人中心: Token有效，过期时间:', new Date(expirationTime).toLocaleString())
        }
      } catch (e) {
        console.error('个人中心: Token验证失败:', e)
        this.handleLogout()
        return
      }

      console.log('个人中心: 登录状态有效，更新用户信息')
      this.userInfo = userInfo
      this.role = role
    },
    handleEditProfile() {
      uni.navigateTo({
        url: '/pages/user/profile'
      })
    },
    handleMyCompetitions() {
      uni.navigateTo({
        url: '/pages/competition/my'
      })
    },
    handleLogout() {
      uni.showModal({
        title: '提示',
        content: '确定要退出登录吗？',
        success: (res) => {
          if (res.confirm) {
            // 清除登录信息
            uni.removeStorageSync('token')
            uni.removeStorageSync('userInfo')
            uni.removeStorageSync('role')
            
            // 跳转到登录页
            uni.reLaunch({
              url: '/pages/user/login'
            })
          }
        }
      })
    }
  }
}
</script>

<style>
.container {
  min-height: 100vh;
  background-color: #F8F8F8;
  padding: 20px;
}

.user-info {
  background-color: #FFFFFF;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.avatar {
  width: 60px;
  height: 60px;
  background-color: #7799CC;
  border-radius: 30px;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-right: 15px;
}

.avatar-text {
  color: #FFFFFF;
  font-size: 24px;
  font-weight: bold;
}

.info {
  flex: 1;
}

.email {
  font-size: 16px;
  color: #333333;
  margin-bottom: 5px;
  display: block;
}

.role {
  font-size: 14px;
  color: #666666;
}

.menu-list {
  background-color: #FFFFFF;
  border-radius: 8px;
  overflow: hidden;
}

.menu-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #F5F5F5;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-text {
  font-size: 16px;
  color: #333333;
}

.arrow {
  color: #999999;
  font-size: 16px;
}
</style> 