<template>
  <view class="container">
    <view class="header">
      <text class="title">我的成绩</text>
    </view>

    <view class="score-list">
      <view v-if="scores.length === 0" class="empty-tip">
        <text>暂无成绩信息</text>
      </view>
      
      <view v-else class="score-item" v-for="score in scores" :key="score.scoreId" @click="viewScoreDetail(score.scoreId)">
        <view class="score-header">
          <text class="competition-name">{{ score.competitionName }}</text>
          <text class="award-level" :class="getAwardClass(score.awardLevel)">{{ score.awardLevel }}</text>
        </view>
        
        <view class="score-info">
          <view class="info-item">
            <text class="label">队伍名称：</text>
            <text class="value">{{ score.teamName }}</text>
          </view>
          <view class="info-item">
            <text class="label">队长：</text>
            <text class="value">{{ score.captainName }}</text>
          </view>
          <view class="info-item">
            <text class="label">获奖时间：</text>
            <text class="value">{{ formatDate(score.createdAt) }}</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  data() {
    return {
      scores: []
    }
  },
  
  computed: {
    ...mapGetters(['isLoggedIn', 'userRole', 'userId'])
  },
  
  onLoad() {
    this.checkLogin()
    this.loadScores()
	
  },
  onShow() {
    console.log('profile页面显示，登录状态:', this.isLoggedIn)
    // 直接从本地存储检查登录状态
    const token = uni.getStorageSync('token')
	console.log('token:',token)
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
    checkLogin() {
      if (!this.isLoggedIn) {
        uni.showToast({
          title: '请先登录',
          icon: 'none'
        })
        setTimeout(() => {
          uni.navigateBack()
        }, 1500)
        return
      }
      
      if (this.userRole !== 'student') {
        uni.showToast({
          title: '只有学生可以查看成绩',
          icon: 'none'
        })
        setTimeout(() => {
          uni.navigateBack()
        }, 1500)
      }
    },
    
    async loadScores() {
      const userInfo = uni.getStorageSync('userInfo')
      const studentId = userInfo.userId
      console.log('当前学生ID:', studentId)
      
      try {
        const [error, res] = await uni.request({
          url: `http://localhost:8080/api/scores/student/${studentId}`,
          method: 'GET'
        })

        if (error) {
          throw new Error(error.errMsg)
        }

        if (res.data.code === 200) {
          this.scores = res.data.data
        } else {
          throw new Error(res.data.message)
        }
      } catch (error) {
        console.error('加载成绩失败:', error)
        uni.showToast({
          title: error.message || '加载成绩失败',
          icon: 'none'
        })
      }
    },
    
    viewScoreDetail(scoreId) {
      uni.navigateTo({
        url: `/pages/score/detail?id=${scoreId}`
      })
    },
    
    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
    },
    
    getAwardClass(level) {
      const map = {
        '一等奖': 'first-prize',
        '二等奖': 'second-prize',
        '三等奖': 'third-prize',
        '优秀奖': 'excellent-prize'
      }
      return map[level] || ''
    }
  }
}
</script>

<style>
.container {
  padding: 30rpx;
}

.header {
  margin-bottom: 30rpx;
}

.title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
}

.score-list {
  background: #fff;
  border-radius: 16rpx;
  padding: 20rpx;
}

.empty-tip {
  text-align: center;
  padding: 60rpx 0;
  color: #999;
  font-size: 28rpx;
}

.score-item {
  background: #f8f9fa;
  border-radius: 12rpx;
  padding: 20rpx;
  margin-bottom: 20rpx;
}

.score-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.competition-name {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.award-level {
  font-size: 28rpx;
  padding: 4rpx 16rpx;
  border-radius: 8rpx;
}

.first-prize {
  background: #f5222d;
  color: #fff;
}

.second-prize {
  background: #fa8c16;
  color: #fff;
}

.third-prize {
  background: #faad14;
  color: #fff;
}

.excellent-prize {
  background: #52c41a;
  color: #fff;
}

.score-info {
  font-size: 28rpx;
  color: #666;
}

.info-item {
  margin-bottom: 10rpx;
}

.label {
  color: #999;
}

.value {
  color: #333;
}
</style> 
