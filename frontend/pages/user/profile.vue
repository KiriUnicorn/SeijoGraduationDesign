<template>
  <view class="container">
    <view class="profile-header">
      <view class="avatar-section">
        <image 
          class="avatar" 
          src="/static/images/default-avatar.png" 
          mode="aspectFill"
        />
      </view>
      <view class="user-info">
        <text class="username">{{ userInfo.name || userInfo.username || '未登录' }}</text>
        <text class="student-id">{{ idLabel }}：{{ userInfo.userId || '未设置' }}</text>
      </view>
    </view>

    <view class="info-section">
      <view class="info-item" v-if="userInfo.role !== 'admin'">
        <text class="label">所属院系</text>
        <text class="value">{{ departmentName || '未设置' }}</text>
      </view>
      <view class="info-item">
        <text class="label">登录账户类型</text>
        <text class="value">{{ roleLabel }}</text>
      </view>
      <view class="info-item">
        <text class="label">电子邮箱</text>
        <text class="value">{{ userInfo.email || '未设置' }}</text>
      </view>
    </view>

    <view class="function-buttons">
      <template v-if="userInfo.role === 'teacher'">
        <view class="function-button" @click="goToRegistrationReview">
          <text class="button-icon">📝</text>
          <text class="button-text">报名审核</text>
        </view>
        <view class="function-button" @click="goToCompetitionManage">
          <text class="button-icon">🏆</text>
          <text class="button-text">竞赛管理</text>
        </view>
        <view class="function-button" @click="goToScoreManage">
          <text class="button-icon">🧮</text>
          <text class="button-text">成绩管理</text>
        </view>
        <view class="function-button" @click="goToStatistical">
          <text class="button-icon">️📊</text>
          <text class="button-text">统计数据</text>
        </view>
      </template>
	  <template v-else-if="userInfo.role === 'admin'">
		<view class="function-button" @click="goToCompetitionManage">
          <text class="button-icon">🏆</text>
          <text class="button-text">竞赛管理</text>
        </view>
	    <view class="function-button" @click="navigateTo('/pages/admin/user-manage')" >
	      <text class="button-icon">🧑‍🎓</text>
	      <text class="button-text">用户管理</text>
	    </view>
	    <view class="function-button" @click="goToStatistical">
	      <text class="button-icon">📋</text>
	      <text class="button-text">统计数据</text>
	    </view>
	    <view class="function-button" @click="goToAdminManage">
	      <text class="button-icon">⚙️</text>
	      <text class="button-text">管理后台</text>
	    </view>
	  </template>
      <template v-else>
        <view class="function-button" @click="goToMyCompetitions">
          <text class="button-icon">🏆</text>
          <text class="button-text">我的竞赛</text>
        </view>
		<view class="function-button" @click="goToMyScoreList">
		  <text class="button-icon">🔍</text>
		  <text class="button-text">成绩查询</text>
		</view>
	    <view class="function-button" @click="goToStatistical">
	      <text class="button-icon">📋</text>
	      <text class="button-text">统计数据</text>
	    </view>

<!--        <view class="function-button" @click="goToAdminManage">
          <text class="button-icon">⚙️</text>
          <text class="button-text">待定</text>
        </view> -->
      </template>
    </view>

    <view class="action-section">
      <button class="action-btn" @click="goToEditProfile">编辑资料</button>
      <button class="action-btn" @click="goToChangePassword">修改密码</button>
      <button class="action-btn logout" @click="handleLogout">退出登录</button>
    </view>
  </view>
</template>

<script>
import { mapState, mapGetters } from 'vuex'

export default {
  data() {
    return {
      userInfo: {
        avatar: '',
        name: '',
        username: '',
        userId: '',
        realName: '',
        phoneNumber: '',
        email: '',
        role: '',
        departmentId: ''
      },
      departmentName: ''
    }
  },
  computed: {
    ...mapGetters(['isLoggedIn']),
    idLabel() {
      const role = this.userInfo.role
      switch(role) {
        case 'student':
          return '学号ID'
        case 'teacher':
          return '工号ID'
        case 'admin':
          return '管理员ID'
        default:
          return '用户ID'
      }
    },
    roleLabel() {
      const role = this.userInfo.role
      switch(role) {
        case 'student':
          return '学生用户'
        case 'teacher':
          return '教师用户'
        case 'admin':
          return '系统管理员'
        default:
          return '未设置'
      }
    }
  },
  onShow() {
    console.log('profile页面显示，登录状态:', this.isLoggedIn)
    // 直接从本地存储检查登录状态
    const token = uni.getStorageSync('token')
    const userInfo = uni.getStorageSync('userInfo')
    
    if (token && userInfo) {
      console.log('检测到登录状态，用户信息:', userInfo)
      this.loadUserInfo()
    } else {
      console.log('未检测到登录状态')
      uni.showToast({
        title: '请先登录',
        icon: 'none'
      })
      setTimeout(() => {
        uni.navigateTo({
          url: '/pages/user/login'
        })
      }, 1500)
    }
  },
  methods: {
    async loadUserInfo() {
      console.log('开始加载用户信息')
      const userInfo = uni.getStorageSync('userInfo')
      console.log('从本地存储获取的用户信息:', userInfo)
      
      if (userInfo) {
        this.userInfo = {
          ...userInfo,
          userId: userInfo.userId || '未设置',
          name: userInfo.name || userInfo.username || '未设置'
        }
        console.log('用户信息已更新:', this.userInfo)
        
        // 如果是教师或学生，获取院系信息
        if (this.userInfo.role === 'teacher' || this.userInfo.role === 'student') {
          await this.loadDepartmentInfo()
        }
      } else {
        console.log('本地存储中没有用户信息')
        // 如果本地存储中没有用户信息，清除登录状态
        this.$store.dispatch('logout')
        uni.showToast({
          title: '登录状态异常，请重新登录',
          icon: 'none'
        })
        setTimeout(() => {
          uni.navigateTo({
            url: '/pages/user/login'
          })
        }, 1500)
      }
    },
    
    async loadDepartmentInfo() {
      try {
        const token = uni.getStorageSync('token')
        const [error, res] = await uni.request({
          url: 'http://localhost:8080/api/departments',
          method: 'GET',
          header: {
            'Authorization': `Bearer ${token}`
          }
        })

        if (error) {
          throw new Error(error.errMsg)
        }

        if (res.data.code === 200) {
          const departments = res.data.data
          // 根据用户类型和院系ID找到对应的院系
          if (this.userInfo.role === 'student' && this.userInfo.departmentId) {
            const department = departments.find(d => d.departmentId === this.userInfo.departmentId)
            if (department) {
              this.departmentName = department.name
            }
          } else if (this.userInfo.role === 'teacher' && this.userInfo.departmentId) {
            const department = departments.find(d => d.departmentId === this.userInfo.departmentId)
            if (department) {
              this.departmentName = department.name
            }
          }
        } else {
          throw new Error(res.data.message)
        }
      } catch (error) {
        console.error('获取院系信息失败:', error)
        uni.showToast({
          title: '获取院系信息失败',
          icon: 'none'
        })
      }
    },
	// 导航到其他管理页面
	navigateTo(url) {
	  uni.navigateTo({ url })
	},
    goToEditProfile() {
      uni.navigateTo({
        url: '/pages/user/edit-profile'
      })
    },
    goToChangePassword() {
      uni.navigateTo({
        url: '/pages/user/change-password'
      })
    },
    goToMyCompetitions() {
      uni.navigateTo({
        url: '/pages/competition/my-competitions'
      })
    },
	goToMyScoreList() {
	  uni.navigateTo({
	    url: '/pages/score/list'
	  })
	},
	goToUserManage() {
	  uni.navigateTo({
	    url: '/pages/score/list'
	  })
	},
    goToMessages() {
      uni.navigateTo({
        url: '/pages/message/list'
      })
    },
    goToStatistical() {
      uni.navigateTo({
        url: '/pages/statistics/department-registration'
      })
    },
    goToFunction4() {
      uni.showToast({
        title: '功能开发中',
        icon: 'none'
      })
    },
	goToAdminManage() {
      uni.navigateTo({
        url: '/pages/admin/manage'
      })
	},
    goToRegistrationReview() {
      uni.navigateTo({
        url: '/pages/teacher/registration-review'
      })
    },
    goToCompetitionManage() {
      uni.navigateTo({
        url: '/pages/competition/competition-manage'
      })
    },
    goToScoreManage() {
      uni.navigateTo({
        url: '/pages/teacher/score-manage'
      })
    },
    async handleLogout() {
      try {
        await uni.showModal({
          title: '提示',
          content: '确定要退出登录吗？'
        })
        
        await this.$store.dispatch('logout')
        uni.showToast({
          title: '已退出登录',
          icon: 'success'
        })
        
        setTimeout(() => {
          uni.reLaunch({
            url: '/pages/user/login'
          })
        }, 1500)
      } catch (error) {
        console.error('退出登录失败:', error)
      }
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

.profile-header {
  background-color: #FFFFFF;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-right: 20px;
}

.avatar {
  width: 80px;
  height: 80px;
  border-radius: 40px;
  margin-bottom: 8px;
}

.user-info {
  flex: 1;
}

.username {
  font-size: 20px;
  font-weight: bold;
  color: #333333;
  margin-bottom: 8px;
  display: block;
}

.student-id {
  font-size: 14px;
  color: #666666;
}

.info-section {
  background-color: #FFFFFF;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #F0F0F0;
}

.info-item:last-child {
  border-bottom: none;
}

.label {
  font-size: 16px;
  color: #333333;
}

.value {
  font-size: 16px;
  color: #666666;
}

.function-buttons {
  display: flex;
  justify-content: space-between;
  background-color: #FFFFFF;
  border-radius: 12px;
  padding: 15px;
  margin-bottom: 20px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.function-button {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
}

.button-icon {
  font-size: 24px;
  margin-bottom: 8px;
}

.button-text {
  font-size: 14px;
  color: #333333;
}

.action-section {
  padding: 20px;
}

.action-btn {
  width: 100%;
  height: 44px;
  background-color: #FFFFFF;
  color: #333333;
  font-size: 16px;
  border-radius: 8px;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.action-btn.logout {
  background-color: #FF4D4F;
  color: #FFFFFF;
  margin-top: 20px;
}

.action-btn:active {
  opacity: 0.8;
}
</style>
