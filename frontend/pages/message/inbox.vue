<template>
  <view class="container">
    <view class="announcement-list">
      <view 
        v-for="item in announcements" 
        :key="item.id" 
        class="announcement-item"
        @tap="viewAnnouncement(item)"
      >
        <view class="announcement-content">
          <text class="announcement-title">{{ item.title }}</text>
          <text class="announcement-time">{{ formatTime(item.createdAt) }}</text>
        </view>
        <text class="announcement-arrow">></text>
      </view>
    </view>
    
    <!-- 加载更多 -->
    <view v-if="hasMore" class="load-more" @tap="loadMore">
      <text class="load-more-text">加载更多</text>
    </view>
    <view v-else class="no-more">
      <text class="no-more-text">没有更多了</text>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      announcements: [],
      page: 1,
      pageSize: 10,
      hasMore: true,
      isLoading: false
    }
  },
  onLoad() {
    this.loadData()
  },
  onPullDownRefresh() {
    this.page = 1
    this.announcements = []
    this.hasMore = true
    this.loadData()
  },
  methods: {
    async loadData() {
      if (this.isLoading) return
      this.isLoading = true
      
      try {
        const [err, res] = await uni.request({
          url: 'http://localhost:8080/api/announcements/findAll',
          method: 'GET',
          data: {
            page: this.page,
            pageSize: this.pageSize
          }
        })
        
        if (err) {
          throw err
        }
        
        if (res.data.code === 200) {
          const { list, total } = res.data.data
          if (this.page === 1) {
            this.announcements = list
          } else {
            this.announcements = [...this.announcements, ...list]
          }
          
          this.hasMore = this.announcements.length < total
          console.log('加载的公告数据:', this.announcements)
        }
      } catch (error) {
        console.error('加载公告失败:', error)
        uni.showToast({
          title: '加载失败，请重试',
          icon: 'none'
        })
      } finally {
        this.isLoading = false
        uni.stopPullDownRefresh()
      }
    },
    loadMore() {
      if (this.hasMore && !this.isLoading) {
        this.page++
        this.loadData()
      }
    },
    formatTime(time) {
      const date = new Date(time)
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
    },
    viewAnnouncement(item) {
      console.log('点击公告:', item)
      if (!item || !item.announcementId) {
        console.error('公告数据无效:', item)
        uni.showToast({
          title: '公告数据无效',
          icon: 'none'
        })
        return
      }
      
      uni.navigateTo({
        url: `/pages/message/announcement?id=${item.announcementId}`,
        success: () => {
          console.log('跳转成功')
        },
        fail: (err) => {
          console.error('跳转失败:', err)
          uni.showToast({
            title: '跳转失败',
            icon: 'none'
          })
        }
      })
    }
  }
}
</script>

<style>
.container {
  padding: 20rpx;
  background-color: #F5F5F5;
  min-height: 100vh;
}

.announcement-list {
  background-color: #FFFFFF;
  border-radius: 8px;
  overflow: hidden;
}

.announcement-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx;
  border-bottom: 1px solid #EEEEEE;
}

.announcement-item:last-child {
  border-bottom: none;
}

.announcement-content {
  flex: 1;
  margin-right: 20rpx;
}

.announcement-title {
  font-size: 32rpx;
  color: #333333;
  margin-bottom: 10rpx;
  display: block;
}

.announcement-time {
  font-size: 24rpx;
  color: #999999;
  display: block;
}

.announcement-arrow {
  font-size: 32rpx;
  color: #CCCCCC;
}

.load-more, .no-more {
  text-align: center;
  padding: 30rpx 0;
}

.load-more-text {
  font-size: 28rpx;
  color: #7799CC;
}

.no-more-text {
  font-size: 28rpx;
  color: #999999;
}
</style> 