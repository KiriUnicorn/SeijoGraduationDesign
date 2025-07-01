<template>
  <view class="container">
    <!-- 搜索栏 -->
    <view class="search-bar">
      <input type="text" v-model="searchKeyword" placeholder="搜索竞赛" @input="handleSearch" />
      <button @tap="handleSearch">搜索</button>
    </view>

    <!-- 竞赛列表 -->
    <view class="competition-list">
      <view v-for="item in competitionList" :key="item.competitionId" class="competition-item" @tap="goToDetail(item.competitionId)">
        <view class="competition-header">
          <text class="title">{{ item.title }}</text>
          <text class="status" :class="item.status">{{ getStatusText(item.status) }}</text>
        </view>
        <view class="competition-info">
          <text class="description">{{ item.description }}</text>
        </view>
        <view class="competition-time">
          <view class="time-item">
            <text class="label">报名时间：</text>
            <text class="value">{{ formatDateTime(item.signupStartTime) }} 至 {{ formatDateTime(item.signupEndTime) }}</text>
          </view>
          <view class="time-item">
            <text class="label">比赛时间：</text>
            <text class="value">{{ formatDateTime(item.competitionStartTime) }} 至 {{ formatDateTime(item.competitionEndTime) }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 分页器 -->
    <view class="pagination">
      <button :disabled="currentPage === 1" @tap="handlePageChange(currentPage - 1)">上一页</button>
      <text>{{ currentPage }} / {{ totalPages }}</text>
      <button :disabled="currentPage === totalPages" @tap="handlePageChange(currentPage + 1)">下一页</button>
    </view>
  </view>
</template>

<script>
import { ref, onMounted } from 'vue'
import { getCompetitionList } from '@/api/competition'

export default {
  setup() {
    const searchKeyword = ref('')
    const competitionList = ref([])
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(0)
    const totalPages = ref(1)
    let searchTimer = null

    // 获取竞赛列表
    const loadCompetitions = async () => {
      try {
        uni.showLoading({
          title: '加载中...'
        })
        console.log('请求参数：', {
          page: currentPage.value,
          pageSize: pageSize.value,
          keyword: searchKeyword.value
        })
        const res = await getCompetitionList({
          page: currentPage.value,
          pageSize: pageSize.value,
          keyword: searchKeyword.value
        })
        console.log('响应数据：', res)
        if (res.code === 200 && res.data) {
          // 直接使用返回的数据
          competitionList.value = res.data.list
          total.value = res.data.total
          totalPages.value = res.data.totalPages
          currentPage.value = res.data.page
          
          console.log('处理后的列表数据：', competitionList.value)
          
          // 如果没有数据，显示提示
          if (competitionList.value.length === 0) {
            uni.showToast({
              title: '暂无竞赛数据',
              icon: 'none'
            })
          }
        } else {
          console.error('请求失败：', res)
          uni.showToast({
            title: res.message || '获取竞赛列表失败',
            icon: 'none'
          })
        }
      } catch (error) {
        console.error('获取竞赛列表失败：', error)
        uni.showToast({
          title: '获取竞赛列表失败',
          icon: 'none'
        })
      } finally {
        uni.hideLoading()
      }
    }

    // 搜索处理（带防抖）
    const handleSearch = () => {
      if (searchTimer) {
        clearTimeout(searchTimer)
      }
      searchTimer = setTimeout(() => {
        currentPage.value = 1
        loadCompetitions()
      }, 300)
    }

    // 页码改变
    const handlePageChange = (page) => {
      if (page < 1 || page > totalPages.value) return
      currentPage.value = page
      loadCompetitions()
    }

    // 跳转到详情页
    const goToDetail = (id) => {
      if (!id) return
      uni.navigateTo({
        url: `/pages/competition/detail?id=${id}`
      })
    }

    // 格式化日期时间
    const formatDateTime = (dateTimeStr) => {
      if (!dateTimeStr) return '未设置'
      try {
        const date = new Date(dateTimeStr)
        if (isNaN(date.getTime())) return '日期格式错误'
        return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
      } catch (error) {
        console.error('日期格式化错误：', error)
        return '日期格式错误'
      }
    }

    // 获取状态文本
    const getStatusText = (status) => {
      const statusMap = {
        'pending': '未开始',
        'ongoing': '进行中',
        'ended': '已结束',
        'cancelled': '已取消'
      }
      return statusMap[status] || '未知状态'
    }

    onMounted(() => {
      loadCompetitions()
    })

    return {
      searchKeyword,
      competitionList,
      currentPage,
      totalPages,
      handleSearch,
      handlePageChange,
      goToDetail,
      formatDateTime,
      getStatusText
    }
  }
}
</script>

<style lang="scss">
.container {
  padding: 20rpx;
}

.search-bar {
  display: flex;
  margin-bottom: 20rpx;
  padding: 20rpx;
  background-color: #fff;
  border-radius: 8rpx;
  
  input {
    flex: 1;
    height: 72rpx;
    padding: 0 20rpx;
    border: 1px solid #ddd;
    border-radius: 8rpx;
    margin-right: 20rpx;
  }
  
  button {
    width: 160rpx;
    height: 72rpx;
    line-height: 72rpx;
    text-align: center;
    background-color: #007AFF;
    color: #fff;
    border-radius: 8rpx;
  }
}

.competition-list {
  .competition-item {
    margin-bottom: 20rpx;
    padding: 20rpx;
    background-color: #fff;
    border-radius: 8rpx;
    box-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.1);
    
    .competition-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16rpx;
      
      .title {
        font-size: 32rpx;
        font-weight: bold;
        color: #333;
      }
      
      .status {
        padding: 4rpx 16rpx;
        border-radius: 4rpx;
        font-size: 24rpx;
        
        &.pending {
          background-color: #E6F7FF;
          color: #1890FF;
        }
        
        &.ongoing {
          background-color: #F6FFED;
          color: #52C41A;
        }
        
        &.ended {
          background-color: #F5F5F5;
          color: #999;
        }
        
        &.cancelled {
          background-color: #FFF1F0;
          color: #FF4D4F;
        }
      }
    }
    
    .competition-info {
      margin-bottom: 16rpx;
      
      .description {
        font-size: 28rpx;
        color: #666;
        line-height: 1.5;
      }
    }
    
    .competition-time {
      .time-item {
        display: flex;
        margin-bottom: 8rpx;
        font-size: 24rpx;
        
        .label {
          color: #999;
          width: 140rpx;
        }
        
        .value {
          color: #666;
          flex: 1;
        }
      }
    }
  }
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 30rpx;
  
  button {
    margin: 0 20rpx;
    padding: 10rpx 30rpx;
    background-color: #007AFF;
    color: #fff;
    border-radius: 8rpx;
    
    &:disabled {
      background-color: #ccc;
    }
  }
  
  text {
    font-size: 28rpx;
    color: #666;
  }
}
</style> 