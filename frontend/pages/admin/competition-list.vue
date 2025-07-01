<template>
  <view class="container">
    <view class="header">
      <text class="title">竞赛管理</text>
      <button class="add-btn" @click="navigateToForm">添加竞赛</button>
    </view>
    
    <view class="competition-list">
      <view class="empty-state" v-if="competitions.length === 0">
        <image class="empty-image" src="/static/images/empty.png" mode="aspectFit"></image>
        <text class="empty-text">暂无竞赛数据</text>
      </view>
      
      <view class="competition-card" v-for="(item, index) in competitions" :key="index">
        <view class="competition-content">
          <text class="competition-title">{{ item.title }}</text>
          <text class="competition-desc">{{ item.description }}</text>
          <view class="competition-meta">
            <text class="competition-date">开始: {{ formatDate(item.startTime) }}</text>
            <text class="competition-date">结束: {{ formatDate(item.endTime) }}</text>
            <text class="competition-status" :class="getStatusClass(item)">{{ getStatusText(item) }}</text>
          </view>
        </view>
        <view class="competition-actions">
          <button class="action-btn edit" @click="editCompetition(item)">编辑</button>
          <button class="action-btn delete" @click="deleteCompetition(item)">删除</button>
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
      competitions: [
        // 示例数据，实际应该从API获取
        {
          id: 1,
          title: '2023年全国大学生数学建模竞赛',
          description: '一项面向全国大学生的数学建模竞赛，旨在提高大学生运用数学知识解决实际问题的能力。',
          startTime: '2023-09-10',
          endTime: '2023-09-13',
          status: 'completed'
        },
        {
          id: 2,
          title: '2023年全国大学生电子设计竞赛',
          description: '培养学生实践能力、创新能力、团队协作能力的重要赛事。',
          startTime: '2023-08-10',
          endTime: '2023-11-30',
          status: 'ongoing'
        },
        {
          id: 3,
          title: '2024年互联网+创新创业大赛',
          description: '以"互联网+"为主题的创新创业竞赛，鼓励高校学生创业创新。',
          startTime: '2024-03-01',
          endTime: '2024-06-30',
          status: 'upcoming'
        }
      ]
    }
  },
  computed: {
    ...mapGetters(['isLoggedIn', 'userRole'])
  },
  onLoad() {
    this.checkAdminPermission()
    // 实际项目中应从后端获取竞赛列表
    // this.fetchCompetitions()
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
        setTimeout(() => { uni.switchTab({ url: '/pages/index/index' }) }, 1500)
      }
    },
    
    // 导航到表单页面
    navigateToForm() {
      uni.navigateTo({ url: '/pages/admin/competition-form' })
    },
    
    // 编辑竞赛
    editCompetition(item) {
      uni.navigateTo({ url: `/pages/admin/competition-form?id=${item.id}` })
    },
    
    // 删除竞赛
    deleteCompetition(item) {
      uni.showModal({
        title: '确认删除',
        content: `确定要删除竞赛"${item.title}"吗？`,
        success: (res) => {
          if (res.confirm) {
            // 实际项目中应调用API删除
            this.competitions = this.competitions.filter(comp => comp.id !== item.id)
            uni.showToast({ title: '删除成功', icon: 'success' })
          }
        }
      })
    },
    
    // 获取竞赛状态样式类
    getStatusClass(item) {
      switch(item.status) {
        case 'upcoming': return 'status-upcoming'
        case 'ongoing': return 'status-ongoing'
        case 'completed': return 'status-completed'
        default: return ''
      }
    },
    
    // 获取竞赛状态文本
    getStatusText(item) {
      switch(item.status) {
        case 'upcoming': return '即将开始'
        case 'ongoing': return '进行中'
        case 'completed': return '已结束'
        default: return '未知状态'
      }
    },
    
    // 格式化日期
    formatDate(dateStr) {
      const date = new Date(dateStr)
      return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`
    }
    
    // fetchCompetitions实际项目中需要实现API调用获取数据
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
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30rpx;
}

.title {
  font-size: 40rpx;
  font-weight: bold;
  color: #333;
}

.add-btn {
  background-color: #1890ff;
  color: #fff;
  font-size: 28rpx;
  padding: 10rpx 30rpx;
  border-radius: 6rpx;
}

.competition-list {
  margin-top: 20rpx;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 0;
  background: #fff;
  border-radius: 16rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
}

.empty-image {
  width: 200rpx;
  height: 200rpx;
  margin-bottom: 30rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
}

.competition-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
  display: flex;
  justify-content: space-between;
}

.competition-content {
  flex: 1;
}

.competition-title {
  font-size: 34rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 10rpx;
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

.competition-meta {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
}

.competition-date {
  font-size: 24rpx;
  color: #888;
  margin-right: 20rpx;
}

.competition-status {
  font-size: 24rpx;
  padding: 4rpx 16rpx;
  border-radius: 20rpx;
}

.status-upcoming {
  background-color: #e6f7ff;
  color: #1890ff;
}

.status-ongoing {
  background-color: #f6ffed;
  color: #52c41a;
}

.status-completed {
  background-color: #f5f5f5;
  color: #888888;
}

.competition-actions {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 16rpx;
}

.action-btn {
  font-size: 24rpx;
  padding: 8rpx 20rpx;
  border-radius: 6rpx;
  margin: 0;
  min-width: 120rpx;
}

.edit {
  background-color: #1890ff;
  color: #fff;
}

.delete {
  background-color: #ff4d4f;
  color: #fff;
}
</style> 