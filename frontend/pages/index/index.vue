<template>
  <view class="container">
    <!-- 轮播图 -->
    <swiper class="banner" circular autoplay interval="3000" duration="500">
      <swiper-item v-for="(item, index) in banners" :key="index" @tap="goToCompetitions">
        <image :src="item.image" mode="aspectFill"></image>
      </swiper-item>
    </swiper>

    <!-- 公告区域 -->
    <view class="section">
      <view class="section-header">
        <text class="section-title">最新公告</text>
        <text class="more" @tap="goToAnnouncements">更多</text>
      </view>
      <view class="announcement-list">
        <view 
          v-for="item in announcements" 
          :key="item.id" 
          class="announcement-item"
          @tap="viewAnnouncement(item)"
        >
          <text class="announcement-title">{{ item.title }}</text>
          <text class="announcement-time">{{ formatTime(item.createdAt) }}</text>
        </view>
      </view>
    </view>

    <!-- 竞赛区域 -->
    <view class="section">
      <view class="section-header">
        <text class="section-title">最新竞赛</text>
        <text class="more" @tap="goToCompetitions">更多</text>
      </view>
      <view class="competition-list">
        <view v-if="competitions && competitions.length > 0">
          <view 
            v-for="item in competitions" 
            :key="item.id" 
            class="competition-item"
            @tap="viewCompetition(item)"
          >
            <view class="competition-info">
              <text class="competition-title">{{ item.title }}</text>
              <text class="competition-level">{{ getLevelName(item.levelId) }}</text>
            </view>
            <view class="competition-status" :class="item.status">
              {{ getStatusText(item.status) }}
            </view>
          </view>
        </view>
        <view v-else class="empty-state">
          <text class="empty-text">暂无竞赛数据</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import request from '@/utils/request'

export default {
  data() {
    return {
      banners: [
        { image: '/static/images/banner1.jpg' },
        { image: '/static/images/banner2.jpg' },
        { image: '/static/images/banner3.jpg' }
      ],
      announcements: [],
      competitions: [],
      isLoading: false,
      loadError: false,
      errorMsg: '加载失败，请重试'
    }
  },
  onLoad() {
    this.loadData()
  },
  onPullDownRefresh() {
    this.loadData()
  },
  methods: {
    async loadData() {
      try {
        // 获取公告列表
        const [announcementErr, announcementRes] = await uni.request({
          url: 'http://localhost:8080/api/announcements/latest',
          method: 'GET',
          data: {
            limit: 3
          }
        })
        
        console.log('公告数据:', announcementRes)
        
        if (announcementErr) {
          throw announcementErr
        }
        
        if (announcementRes.data.code === 200) {
          this.announcements = announcementRes.data.data || []
          console.log('设置后的公告数据:', this.announcements)
        }
        
        // 获取竞赛列表
        const [competitionErr, competitionRes] = await uni.request({
          url: 'http://localhost:8080/api/competitions/hot',
          method: 'GET',
          data: {
            limit: 3
          }
        })
        
        console.log('竞赛原始数据:', competitionRes)
        
        if (competitionErr) {
          throw competitionErr
        }
        
        if (competitionRes.data.code === 200) {
          const competitionData = competitionRes.data.data || []
          console.log('竞赛数据解析:', competitionData)
          // 检查数据结构
          if (Array.isArray(competitionData)) {
            this.competitions = competitionData.map(item => ({
              ...item,
              level: item.level || { name: '未知级别' }
            }))
            console.log('设置后的竞赛数据:', this.competitions)
          } else {
            console.error('竞赛数据格式错误:', competitionData)
            this.competitions = []
          }
        } else {
          console.error('竞赛数据返回错误:', competitionRes.data)
        }
        
        uni.stopPullDownRefresh()
      } catch (error) {
        console.error('加载数据失败:', error)
        uni.showToast({
          title: '加载失败，请重试',
          icon: 'none'
        })
        uni.stopPullDownRefresh()
      }
    },
    formatTime(time) {
      const date = new Date(time)
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
    },
    getStatusText(status) {
      const statusMap = {
        pending: '未开始',
        active: '进行中',
        ended: '已结束'
      }
      return statusMap[status] || status
    },
    getLevelName(levelId) {
      const levelMap = {
        1: '国际级',
        2: '国家级A',
        3: '国家级B',
        4: '省级A',
        5: '校级C'
      }
      return levelMap[levelId] || '未知级别'
    },
    viewAnnouncement(item) {
      uni.navigateTo({
        url: `/pages/message/announcement?id=${item.announcementId}`
      })
    },
    viewCompetition(item) {
      uni.navigateTo({
        url: `/pages/competition/detail?id=${item.competitionId}`
      })
    },
    goToAnnouncements() {
      uni.navigateTo({
        url: '/pages/message/inbox'
      })
    },
    goToCompetitions() {
      uni.switchTab({
        url: '/pages/competition/list'
      })
    }
  }
}
</script>

<style>
.container {
  padding-bottom: 20px;
}

.banner {
  width: 100%;
  height: 360rpx;
}

.banner image {
  width: 100%;
  height: 100%;
}

.section {
  margin: 20px;
  background-color: #FFFFFF;
  border-radius: 8px;
  padding: 15px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.section-title {
  font-size: 18px;
  font-weight: bold;
  color: #333333;
}

.more {
  font-size: 14px;
  color: #7799CC;
}

.announcement-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.announcement-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #EEEEEE;
}

.announcement-item:last-child {
  border-bottom: none;
}

.announcement-title {
  font-size: 16px;
  color: #333333;
  margin-bottom: 5px;
}

.announcement-time {
  font-size: 12px;
  color: #999999;
}

.competition-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.competition-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #EEEEEE;
}

.competition-item:last-child {
  border-bottom: none;
}

.competition-info {
  flex: 1;
}

.competition-title {
  font-size: 16px;
  color: #333333;
  margin-bottom: 5px;
}

.competition-level {
  font-size: 12px;
  color: #666666;
  background-color: #F5F5F5;
  padding: 2px 6px;
  border-radius: 4px;
}

.competition-status {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 4px;
}

.competition-status.pending {
  background-color: #FFF3E0;
  color: #FF9800;
}

.competition-status.active {
  background-color: #E8F5E9;
  color: #4CAF50;
}

.competition-status.ended {
  background-color: #F5F5F5;
  color: #9E9E9E;
}

.empty-state, .error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 30px 0;
}

.empty-text, .error-text {
  font-size: 14px;
  color: #999999;
  margin-bottom: 15px;
}

.retry-btn {
  font-size: 14px;
  color: #FFFFFF;
  background-color: #007AFF;
  padding: 5px 15px;
  border-radius: 4px;
}
</style> 