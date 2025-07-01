<template>
  <view class="container">
    <view class="header">
      <text class="title">{{ isEdit ? '编辑竞赛' : '添加竞赛' }}</text>
    </view>
    
    <!-- 功能暂未实现，仅占位 -->
    <view class="placeholder">
      <text class="placeholder-text">竞赛表单功能开发中...</text>
    </view>
    
    <view class="action-bar">
      <button class="action-btn" @click="goBack">返回</button>
    </view>
  </view>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  data() {
    return {
      isEdit: false,
      competitionId: null
    }
  },
  computed: {
    ...mapGetters(['isLoggedIn', 'userRole'])
  },
  onLoad(options) {
    this.checkAdminPermission()
    
    // 如果有id参数，说明是编辑模式
    if (options && options.id) {
      this.isEdit = true
      this.competitionId = options.id
      console.log('编辑竞赛ID:', this.competitionId)
    }
  },
  methods: {
    // 检查管理员权限
    checkAdminPermission() {
      if (!this.isLoggedIn) {
        uni.showToast({ title: '请先登录', icon: 'none' })
        setTimeout(() => { uni.navigateTo({ url: '/pages/user/login' }) }, 1500)
        return
      }
      
      if (this.userRole !== 'ADMIN') {
        uni.showToast({ title: '您没有管理员权限', icon: 'none' })
        setTimeout(() => { uni.navigateBack() }, 1500)
      }
    },
    
    // 返回上一页
    goBack() {
      uni.navigateBack()
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
  margin-bottom: 30rpx;
}

.title {
  font-size: 40rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
}

.placeholder {
  background: #fff;
  border-radius: 16rpx;
  padding: 100rpx 0;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 40rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
}

.placeholder-text {
  font-size: 32rpx;
  color: #999;
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