<template>
  <view class="container">
    <!-- 搜索栏 -->
    <view class="search-bar">
      <input 
        type="text" 
        v-model="searchKeyword"
        placeholder="搜索比赛" 
        @confirm="handleSearch"
      />
      <button @tap="handleSearch">搜索</button>
    </view>

    <!-- 状态筛选 -->
    <view class="status-filters">
      <view 
        v-for="filter in statusFilters" 
        :key="filter.value"
        :class="['filter-item', { active: currentStatus === filter.value }]"
        @tap="handleStatusFilter(filter.value)"
      >
        {{ filter.label }}
      </view>
    </view>

    <!-- 比赛列表 -->
    <scroll-view 
      scroll-y 
      class="competition-list"
      @scrolltolower="loadMore"
      refresher-enabled
      :refresher-triggered="isLoading"
      @refresherrefresh="loadCompetitions"
    >
      <view 
        v-for="item in competitions" 
        :key="item.competitionId"
        class="competition-item"
        @tap="viewCompetition(item)"
      >
        <view class="competition-title">{{ item.title }}</view>
        <view class="competition-desc">{{ item.description }}</view>
        <view class="competition-info">
          <view class="info-item">
            <text class="label">报名时间：</text>
            <text>{{ formatTime(item.signupStartTime) }} 至 {{ formatTime(item.signupEndTime) }}</text>
          </view>
          <view class="info-item">
            <text class="label">比赛时间：</text>
            <text>{{ formatTime(item.competitionStartTime) }} 至 {{ formatTime(item.competitionEndTime) }}</text>
          </view>
          <view class="info-item">
            <text class="label">状态：</text>
            <text :class="['status', item.status]">{{ getStatusText(item.status) }}</text>
          </view>
        </view>
      </view>

      <!-- 加载更多 -->
      <view v-if="isLoading" class="loading">加载中...</view>
      <view v-if="!hasMore && competitions.length > 0" class="no-more">没有更多数据了</view>
      <view v-if="!isLoading && competitions.length === 0" class="empty">暂无比赛数据</view>
    </scroll-view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      searchKeyword: '',
      currentStatus: '',
      statusFilters: [
        { label: '全部', value: '' },
        { label: '未开始', value: 'pending' },
		{ label: '报名中', value: 'available' },
        { label: '进行中', value: 'active' },
        { label: '已结束', value: 'ended' }
      ],
      competitions: [],
      page: 1,
      pageSize: 10,
      hasMore: true,
      isLoading: false
    }
  },

  onLoad() {
    this.loadCompetitions()
  },

  onPullDownRefresh() {
    this.page = 1
    this.competitions = []
    this.hasMore = true
    this.loadCompetitions()
  },

  methods: {
    async loadCompetitions() {
      if (this.isLoading || !this.hasMore) return
      
      this.isLoading = true
      try {
        const [error, res] = await uni.request({
          url: 'http://localhost:8080/api/competitions',
          method: 'GET',
          data: {
            page: this.page,
            pageSize: this.pageSize,
            status: this.currentStatus || null,
            keyword: this.searchKeyword || ''
          },
          header: {
            'Content-Type': 'application/json'
          }
        })
        
        console.log('请求响应：', res)
        
        if (error) {
          throw error
        }
        
        if (res.statusCode === 200 && res.data.code === 200) {
          const responseData = res.data.data
          if (!responseData) {
            this.competitions = []
            this.hasMore = false
            return
          }
          
          const list = Array.isArray(responseData) ? responseData : []
          
          if (this.page === 1) {
            this.competitions = list
          } else {
            this.competitions = [...this.competitions, ...list]
          }
          
          this.hasMore = list.length === this.pageSize
        } else {
          uni.showToast({
            title: res.data.message || '加载失败，请重试',
            icon: 'none'
          })
        }
      } catch (error) {
        console.error('请求错误：', error)
        uni.showToast({
          title: '加载失败，请重试',
          icon: 'none'
        })
      } finally {
        this.isLoading = false
        uni.stopPullDownRefresh()
      }
    },

    handleSearch() {
      this.page = 1
      this.competitions = []
      this.hasMore = true
      this.loadCompetitions()
    },

    handleStatusFilter(status) {
		console.log('当前状态：',status)
      this.currentStatus = status
      this.page = 1
      this.competitions = []
      this.hasMore = true
      this.loadCompetitions()
    },

    loadMore() {
      if (!this.isLoading && this.hasMore) {
        this.page++
        this.loadCompetitions()
      }
    },

    formatTime(time) {
      if (!time) return '未设置'
      const date = new Date(time)
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
    },

    getStatusText(status) {
      const statusMap = {
        pending: '未开始',
		available: '报名中',
        active: '进行中',
        ended: '已结束'
      }
      return statusMap[status] || status
    },

    viewCompetition(item) {
      uni.navigateTo({
        url: `/pages/competition/detail?id=${item.competitionId}`
      })
    }
  }
}
</script>

<style>
.container {
  padding: 20rpx;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.search-bar {
  display: flex;
  margin-bottom: 20rpx;
  padding: 20rpx;
  background-color: #fff;
  border-radius: 10rpx;
}

.search-bar input {
  flex: 1;
  height: 70rpx;
  padding: 0 20rpx;
  border: 1px solid #ddd;
  border-radius: 35rpx;
  margin-right: 20rpx;
}

.search-bar button {
  width: 160rpx;
  height: 70rpx;
  line-height: 70rpx;
  text-align: center;
  background-color: #007AFF;
  color: #fff;
  border-radius: 35rpx;
  font-size: 28rpx;
}

.status-filters {
  display: flex;
  margin-bottom: 20rpx;
  padding: 20rpx;
  background-color: #fff;
  border-radius: 10rpx;
}

.filter-item {
  padding: 10rpx 30rpx;
  margin-right: 20rpx;
  border-radius: 30rpx;
  font-size: 28rpx;
  color: #666;
  background-color: #f5f5f5;
}

.filter-item.active {
  background-color: #007AFF;
  color: #fff;
}

.competition-list {
  height: calc(100vh - 240rpx);
}

.competition-item {
  margin-bottom: 20rpx;
  padding: 30rpx;
  background-color: #fff;
  border-radius: 10rpx;
}

.competition-title {
  font-size: 32rpx;
  font-weight: bold;
  margin-bottom: 20rpx;
  color: #333;
}

.competition-desc {
  font-size: 28rpx;
  color: #666;
  margin-bottom: 20rpx;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}

.competition-info {
  font-size: 26rpx;
  color: #999;
}

.info-item {
  margin-bottom: 10rpx;
}

.label {
  color: #666;
}

.status {
  padding: 4rpx 12rpx;
  border-radius: 4rpx;
  font-size: 24rpx;
}

.status.pending {
  background-color: #f0f0f0;
  color: #666;
}

.status.active {
  background-color: #e6f7ff;
  color: #1890ff;
}

.status.ended {
  background-color: #f5f5f5;
  color: #999;
}

.loading, .no-more, .empty {
  text-align: center;
  padding: 30rpx;
  color: #999;
  font-size: 28rpx;
}
</style> 