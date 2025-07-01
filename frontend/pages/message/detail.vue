<template>
  <view class="container">
    <view class="announcement-detail">
      <view class="announcement-header">
        <text class="announcement-title">{{ announcement.title }}</text>
        <text class="announcement-time">{{ formatTime(announcement.createdAt) }}</text>
      </view>
      <view class="announcement-content">
        <text class="content-text">{{ announcement.content }}</text>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      announcement: {
        title: '',
        content: '',
        createdAt: ''
      }
    }
  },
  onLoad(options) {
    if (options.id) {
      this.loadAnnouncement(options.id)
    }
  },
  methods: {
    async loadAnnouncement(id) {
      try {
        const [err, res] = await uni.request({
          url: `http://localhost:8080/api/announcements/${id}`,
          method: 'GET'
        })
        
        if (err) {
          throw err
        }
        
        if (res.data.code === 200) {
          this.announcement = res.data.data
        } else {
          throw new Error(res.data.message || '获取公告详情失败')
        }
      } catch (error) {
        console.error('加载公告详情失败:', error)
        uni.showToast({
          title: '加载失败，请重试',
          icon: 'none'
        })
      }
    },
    formatTime(time) {
      const date = new Date(time)
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
    }
  }
}
</script>

<style>
.container {
  padding: 30rpx;
  background-color: #FFFFFF;
  min-height: 100vh;
}

.announcement-detail {
  background-color: #FFFFFF;
  border-radius: 8px;
  padding: 30rpx;
}

.announcement-header {
  margin-bottom: 40rpx;
}

.announcement-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333333;
  margin-bottom: 20rpx;
  display: block;
}

.announcement-time {
  font-size: 24rpx;
  color: #999999;
  display: block;
}

.announcement-content {
  margin-top: 30rpx;
}

.content-text {
  font-size: 30rpx;
  color: #666666;
  line-height: 1.6;
  white-space: pre-wrap;
}
</style> 