<template>
  <view class="container">
    <view class="header">
      <text class="title">管理后台</text>
      <text class="subtitle">欢迎，{{adminInfo.name}}</text>
    </view>

    <view class="menu-grid">
      <view class="menu-item" @click="navigateTo('/pages/competition/competition-manage')">
        <text class="menu-icon">🏆🏆🏆</text>
        <text class="menu-title">竞赛管理</text>
        <text class="menu-desc">添加、编辑和删除竞赛</text>
      </view>
	  
	  <view class="menu-item" @click="navigateTo('/pages/admin/adduser')">
	    <text class="menu-icon">➕🥷➕</text>
	    <text class="menu-title">用户添加</text>
	    <text class="menu-desc">添加用户账户</text>
	  </view>
      
      <view class="menu-item" @click="navigateTo('/pages/admin/user-manage')">
        <text class="menu-icon">👮🧑‍🎓🧑‍🏫</text>
        <text class="menu-title">用户管理</text>
        <text class="menu-desc">管理系统用户</text>
      </view>
      
      <view class="menu-item" @click="navigateTo('/pages/admin/department-manage')">
        <text class="menu-icon">📝📝📝</text>
        <text class="menu-title">院系管理</text>
        <text class="menu-desc">添加修改删除院系</text>
      </view>
      

	  
	  <view class="menu-item" @click="navigateTo('/pages/admin/unit-manage')">
	    <text class="menu-icon">📇📇📇</text>
	    <text class="menu-title">单位管理</text>
	    <text class="menu-desc">添加修改删除单位</text>
	  </view>
      
<!-- 	  <view class="menu-item" @click="navigateTo('/pages/admin/score-manage')">
	    <text class="menu-icon">📊</text>
	    <text class="menu-title">成绩管理</text>
	    <text class="menu-desc">录入和管理竞赛成绩</text>
	  </view> -->
	  
      <view class="menu-item" @click="navigateTo('/pages/admin/notice-manage')">
        <text class="menu-icon">📢📢📢</text>
        <text class="menu-title">公告管理</text>
        <text class="menu-desc">发布和管理系统公告</text>
      </view>
      
      <view class="menu-item" @click="navigateTo('/pages/admin/system-settings')">
        <text class="menu-icon">⚙️</text>
        <text class="menu-title">系统设置</text>
        <text class="menu-desc">配置系统参数</text>
      </view>
    </view>

    <view class="stats-section">
      <view class="stats-title">系统概览</view>
      <view class="stats-grid">
        <view class="stats-item">
          <text class="stats-value">{{stats.competitionCount || 0}}</text>
          <text class="stats-label">竞赛总数</text>
        </view>
        <view class="stats-item">
          <text class="stats-value">{{stats.userCount || 0}}</text>
          <text class="stats-label">用户总数</text>
        </view>
        <view class="stats-item">
          <text class="stats-value">{{stats.registrationCount || 0}}</text>
          <text class="stats-label">报名人次</text>
        </view>
        <view class="stats-item">
          <text class="stats-value">{{stats.activeCompetitionCount || 0}}</text>
          <text class="stats-label">进行中竞赛</text>
        </view>
      </view>
    </view>

    <view class="recent-section">
      <view class="recent-title">最近活动</view>
      <view class="empty-tip" v-if="recentActivities.length === 0">
        <text>暂无最近活动</text>
      </view>
      <view class="activity-list" v-else>
        <view class="activity-item" v-for="(item, index) in recentActivities" :key="index">
          <text class="activity-time">{{formatTime(item.time)}}</text>
          <text class="activity-content">{{item.content}}</text>
        </view>
      </view>
    </view>

    <view class="action-bar">
      <button class="action-btn" @click="goBack">返回个人中心</button>
    </view>
  </view>
</template>

<script>
import { mapState, mapGetters } from 'vuex'
import request from '@/utils/request'

export default {
  data() {
    return {
      adminInfo: {
        username: '管理员',
        role: 'admin'
      },
      stats: {
        competitionCount: 0,
        userCount: 0,
        registrationCount: 0,
        activeCompetitionCount: 0
      },
      recentActivities: []
    }
  },
  computed: {
    ...mapGetters(['isLoggedIn', 'userRole']),
  },
  onLoad() {
    //验证是否已登录
    this.checkLogin() 
    // 验证用户是否为管理员
    this.checkAdminPermission()
    // 加载管理员信息
    this.loadAdminInfo()
    // 加载系统统计数据
    this.loadSystemStats()
    // 加载最近活动
    this.loadRecentActivities()
  },
  methods: {

    //检查登录状态
    checkLogin() {
      const userInfo = uni.getStorageSync('userInfo')
      this.isLoggedIn = true
      if (userInfo) {
        console.log('manage页面显示-1，登录状态:', this.isLoggedIn)
        this.isLoggedIn = true
		console.log('manage页面显示0，登录状态:', userInfo)
      }
    },
    // 检查管理员权限
    checkAdminPermission() {
		this.isLoggedIn = true
	  console.log('manage页面显示1，登录状态:', this.isLoggedIn)
      if (!this.isLoggedIn) {
		  console.log('manage页面显示2，登录状态:', this.isLoggedIn)
		  
        uni.showToast({
          title: '请先登录',
          icon: 'none'
        })
        setTimeout(() => {
          uni.navigateTo({
            url: '/pages/user/login'
          })
        }, 1500)
        return
      }
      
      if (this.userRole !== 'admin') {
        uni.showToast({
          title: '您没有管理员权限',
          icon: 'none'
        })
        setTimeout(() => {
          uni.navigateBack()
        }, 1500)
      }
    },
    
    // 加载管理员信息
    loadAdminInfo() {
      const userInfo = uni.getStorageSync('userInfo')
      if (userInfo) {
        this.adminInfo = {
          ...userInfo,
          role: userInfo.role || 'admin'
        }
      }
    },
    
    // 加载系统统计数据
    async loadSystemStats() {
      try {
        // TODO: 替换为真实API
        // const response = await request.get('/api/admin/stats')
        // if (response && response.code === 200) {
        //   this.stats = response.data
        // }
        
        // 模拟数据
        this.stats = {
          competitionCount: 6,
          userCount: 23,
          registrationCount: 16,
          activeCompetitionCount: 1
        }
      } catch (error) {
        console.error('获取系统统计数据失败:', error)
        uni.showToast({
          title: '获取数据失败',
          icon: 'none'
        })
      }
    },
    
    // 加载最近活动
    async loadRecentActivities() {
      try {
        // TODO: 替换为真实API
        // const response = await request.get('/api/admin/recent-activities')
        // if (response && response.code === 200) {
        //   this.recentActivities = response.data
        // }
        
        // 模拟数据
        this.recentActivities = [
          { time: new Date(Date.now() - 30 * 60 * 1000), content: '用户李四报名了"2024年创新创业大赛"' },
          { time: new Date(Date.now() - 2 * 60 * 60 * 1000), content: '管理员添加了新竞赛"2024年人工智能应用大赛"' },
          { time: new Date(Date.now() - 1 * 24 * 60 * 60 * 1000), content: '系统成功处理了25条报名申请' }
        ]
      } catch (error) {
        console.error('获取最近活动失败:', error)
      }
    },
    
    // 导航到其他管理页面
    navigateTo(url) {
      uni.navigateTo({ url })
    },
    
    // 返回个人中心
    goBack() {
      uni.navigateBack()
    },
    
    // 格式化时间
    formatTime(dateTime) {
      if (!dateTime) return ''
      
      const date = new Date(dateTime)
      const now = new Date()
      const diffMs = now - date
      
      // 小于1小时，显示xx分钟前
      if (diffMs < 60 * 60 * 1000) {
        const minutes = Math.floor(diffMs / (60 * 1000))
        return `${minutes}分钟前`
      }
      
      // 小于24小时，显示xx小时前
      if (diffMs < 24 * 60 * 60 * 1000) {
        const hours = Math.floor(diffMs / (60 * 60 * 1000))
        return `${hours}小时前`
      }
      
      // 小于7天，显示x天前
      if (diffMs < 7 * 24 * 60 * 60 * 1000) {
        const days = Math.floor(diffMs / (24 * 60 * 60 * 1000))
        return `${days}天前`
      }
      
      // 其他情况显示具体日期
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
    }
  }
}
</script>

<style>
.container {
  padding: 30rpx;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.header {
  margin-bottom: 40rpx;
}

.title {
  font-size: 40rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 10rpx;
}

.subtitle {
  font-size: 28rpx;
  color: #666;
}

.menu-grid {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  margin-bottom: 40rpx;
}

.menu-item {
  width: 330rpx;
  height: 200rpx;
  background-color: #fff;
  border-radius: 16rpx;
  margin-bottom: 20rpx;
  padding: 30rpx;
  display: flex;
  flex-direction: column;
  justify-content: center;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
  transition: transform 0.2s;
}

.menu-item:active {
  transform: scale(0.98);
}

.menu-icon {
  font-size: 48rpx;
  margin-bottom: 20rpx;
}

.menu-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 10rpx;
}

.menu-desc {
  font-size: 24rpx;
  color: #999;
}

.stats-section {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 40rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
}

.stats-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 30rpx;
}

.stats-grid {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
}

.stats-item {
  width: 48%;
  text-align: center;
  margin-bottom: 20rpx;
}

.stats-value {
  font-size: 40rpx;
  font-weight: bold;
  color: #1890ff;
  margin-bottom: 10rpx;
  display: block;
}

.stats-label {
  font-size: 24rpx;
  color: #666;
}

.recent-section {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 40rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
}

.recent-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 30rpx;
}

.empty-tip {
  text-align: center;
  color: #999;
  padding: 30rpx 0;
}

.activity-list {
  max-height: 400rpx;
  overflow-y: auto;
}

.activity-item {
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.activity-time {
  font-size: 24rpx;
  color: #999;
  margin-bottom: 10rpx;
  display: block;
}

.activity-content {
  font-size: 28rpx;
  color: #333;
}

.action-bar {
  margin-top: 40rpx;
}

.action-btn {
  background-color: #1890ff;
  color: #fff;
  font-size: 32rpx;
  padding: 20rpx 0;
  border-radius: 8rpx;
}
</style> 