<template>
  <view class="container">
    <view class="header">
      <text class="title">我的竞赛</text>
    </view>

    <view class="tab-bar">
      <view 
        v-for="(tab, index) in tabs" 
        :key="index"
        :class="['tab-item', { active: currentTab === index }]"
        @click="switchTab(index)"
      >
        <text>{{ tab }}</text>
      </view>
    </view>

    <view class="competition-list">
      <view v-if="loading" class="loading">
        <text>加载中...</text>
      </view>
      <view v-else-if="competitions.length === 0" class="empty">
        <text>暂无相关竞赛</text>
      </view>
      <view v-else>
        <view 
          v-for="competition in competitions" 
          :key="competition.id"
          class="competition-card"
          @click="showDetail(competition)"
        >
          <view class="competition-info">
            <text class="competition-name">{{ competition.competitionTitle }}</text>
          </view>
          <view class="status-tag" :class="getStatusClass(competition.registrationStatus)">
            {{ getStatusText(competition.registrationStatus) }}
          </view>
        </view>
      </view>
    </view>

    <!-- 详情弹窗 -->
    <view class="modal" v-if="showModal" @click.self="closeModal">
      <view class="modal-content">
        <view class="modal-header">
          <text class="modal-title">竞赛详情</text>
          <text class="close-btn" @click="closeModal">×</text>
        </view>
        <view class="modal-body">
          <view class="detail-section">
            <text class="section-title">竞赛信息</text>
            <view class="info-item">
              <text class="label">竞赛名称：</text>
              <text class="value">{{ currentCompetition.competitionTitle }}</text>
            </view>
			
			<view class="info-item">
			  <text class="label">审核教师：</text>
			  <text class="value">{{ currentCompetition.teacherName }}</text>
			</view>
			
			<view class="info-item">
			  <text class="label">队伍名字：</text>
			  <text class="value">{{ currentCompetition.teamName }}</text>
			</view>
			
			
			
            <view class="info-item">
              <text class="label">报名状态：</text>
              <text class="value" :class="getStatusClass(currentCompetition.registrationStatus)">{{ getStatusText(currentCompetition.registrationStatus) }}</text>
            </view>
          </view>
          <view class="detail-section" v-if="currentCompetition.team">
            <text class="section-title">队伍信息</text>
            <view class="info-item">
              <text class="label">队伍名称：</text>
              <text class="value">{{ currentCompetition.team.teamName }}</text>
            </view>
            <view class="info-item">
              <text class="label">指导教师：</text>
              <text class="value">{{ currentCompetition.team.teacherName || '暂无' }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      tabs: ['全部'],
      currentTab: 0,
      competitions: [],
      loading: false,
      showModal: false,
      currentCompetition: null
    }
  },

  onLoad() {
    this.loadCompetitions()
  },

  methods: {
    async loadCompetitions() {
      this.loading = true
      try {
        const token = uni.getStorageSync('token')
		console.log('token:',token)
        if (!token) {
          throw new Error('未登录或登录已过期')
        }

        const statusMap = {
          0: 'all',
          1: 'pending',
          2: 'approved',
          3: 'rejected'
        }
        const status = statusMap[this.currentTab]
        
        const [error, res] = await uni.request({
          url: 'http://localhost:8080/api/competitions/user/registrations',
          method: 'GET',
          header: {
            'Authorization': `${token}`,
          },
          data: {
            status: status === 'all' ? '' : status
          }
        })

        if (error) {
          throw new Error(error.errMsg)
        }

        if (res.statusCode === 401) {
          // token过期或无效
          uni.removeStorageSync('token')
          uni.removeStorageSync('userInfo')
          uni.removeStorageSync('role')
          uni.showToast({
            title: '登录已过期，请重新登录',
            icon: 'none'
          })
          setTimeout(() => {
            uni.reLaunch({
              url: '/pages/user/login'
            })
          }, 1500)
          return
        }

        if (res.data.code === 200) {
          this.competitions = res.data.data
        } else {
          throw new Error(res.data.message)
        }
      } catch (error) {
        console.error('加载竞赛列表失败:', error)
        uni.showToast({
          title: error.message || '加载失败',
          icon: 'none'
        })
      } finally {
        this.loading = false
      }
    },

    switchTab(index) {
      this.currentTab = index
      // 直接重新加载数据，而不是在前端筛选
      this.loadCompetitions()
    },

    showDetail(competition) {
      this.currentCompetition = competition
      this.showModal = true
    },

    closeModal() {
      this.showModal = false
      this.currentCompetition = null
    },

    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
    },

    getStatusText(status) {
      const statusMap = {
        'pending': '待审核',
        'approved': '已通过',
        'rejected': '已拒绝'
      }
      return statusMap[status] || status
    },

    getStatusClass(status) {
      const classMap = {
        'pending': 'status-pending',
        'approved': 'status-approved',
        'rejected': 'status-rejected'
      }
      return classMap[status] || ''
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

.tab-bar {
  display: flex;
  background: #fff;
  padding: 20rpx;
  border-radius: 12rpx;
  margin-bottom: 30rpx;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 16rpx 0;
  font-size: 28rpx;
  color: #666;
  position: relative;
}

.tab-item.active {
  color: #1890ff;
  font-weight: bold;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 40rpx;
  height: 4rpx;
  background: #1890ff;
  border-radius: 2rpx;
}

.competition-list {
  min-height: 400rpx;
}

.competition-card {
  background: #fff;
  border-radius: 12rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.competition-info {
  flex: 1;
}

.competition-name {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 10rpx;
  display: block;
}

.competition-time {
  font-size: 24rpx;
  color: #999;
}

.status-tag {
  padding: 8rpx 20rpx;
  border-radius: 8rpx;
  font-size: 24rpx;
}

.status-pending {
  background: #e6f7ff;
  color: #1890ff;
}

.status-approved {
  background: #f6ffed;
  color: #52c41a;
}

.status-rejected {
  background: #fff1f0;
  color: #f5222d;
}

.loading, .empty {
  text-align: center;
  padding: 60rpx 0;
  color: #999;
  font-size: 28rpx;
}

/* 弹窗样式 */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  z-index: 999;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-content {
  width: 90%;
  max-width: 600rpx;
  background: #fff;
  border-radius: 16rpx;
  overflow: hidden;
}

.modal-header {
  padding: 30rpx;
  border-bottom: 1rpx solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.close-btn {
  font-size: 40rpx;
  color: #999;
  padding: 0 20rpx;
}

.modal-body {
  padding: 30rpx;
  max-height: 70vh;
  overflow-y: auto;
}

.detail-section {
  margin-bottom: 30rpx;
}

.section-title {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
  display: block;
}

.info-item {
  display: flex;
  margin-bottom: 16rpx;
  font-size: 28rpx;
}

.label {
  color: #999;
  width: 160rpx;
}

.value {
  color: #333;
  flex: 1;
}

.member-list {
  background: #f8f9fa;
  border-radius: 12rpx;
  padding: 20rpx;
}

.member-item {
  display: flex;
  justify-content: space-between;
  padding: 16rpx 0;
  border-bottom: 1rpx solid #eee;
}

.member-item:last-child {
  border-bottom: none;
}

.member-name {
  font-size: 28rpx;
  color: #333;
}

.member-id {
  font-size: 28rpx;
  color: #666;
}
</style> 