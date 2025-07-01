<template>
  <view class="container">
    <!-- 状态筛选栏 -->
    <view class="filter-bar">
      <view 
        v-for="(item, index) in filterOptions" 
        :key="index"
        :class="['filter-item', currentFilter === item.value ? 'active' : '']"
        @click="handleFilterChange(item.value)"
      >
        {{ item.label }}
      </view>
    </view>

    <!-- 报名记录列表 -->
    <view class="registration-list">
      <view 
        v-for="(item, index) in filteredRegistrations" 
        :key="index"
        class="registration-item"
      >
        <view class="registration-header">
          <text class="competition-name">{{ item.competitionName }}</text>
          <text :class="['status-tag', item.status]">{{ getStatusText(item.status) }}</text>
        </view>
        
        <view class="registration-info">
          <view class="info-row">
            <text class="label">报名时间：</text>
            <text class="value">{{ formatDate(item.registrationTime) }}</text>
          </view>
          <view class="info-row">
            <text class="label">报名编号：</text>
            <text class="value">{{ item.registrationNumber }}</text>
          </view>
          <view class="info-row">
            <text class="label">参赛形式：</text>
            <text class="value">{{ item.teamSize === 1 ? '个人参赛' : `${item.teamSize}人团队` }}</text>
          </view>
        </view>

        <view class="registration-actions">
          <button 
            class="action-btn detail" 
            @click="navigateToDetail(item.competitionId)"
          >查看竞赛</button>
          <button 
            v-if="item.status === 'PENDING'" 
            class="action-btn cancel" 
            @click="handleCancel(item.id)"
          >取消报名</button>
        </view>
      </view>
    </view>

    <!-- 空状态 -->
    <view v-if="filteredRegistrations.length === 0" class="empty-state">
      <image class="empty-icon" src="/static/images/empty.png" mode="aspectFit"></image>
      <text class="empty-text">暂无报名记录</text>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      currentFilter: 'all',
      filterOptions: [
        { label: '全部', value: 'all' },
        { label: '待审核', value: 'PENDING' },
        { label: '已通过', value: 'APPROVED' },
        { label: '已拒绝', value: 'REJECTED' },
        { label: '已取消', value: 'CANCELLED' }
      ],
      registrations: []
    }
  },
  computed: {
    filteredRegistrations() {
      if (this.currentFilter === 'all') {
        return this.registrations
      }
      return this.registrations.filter(item => item.status === this.currentFilter)
    }
  },
  onShow() {
    this.loadRegistrations()
  },
  methods: {
    async loadRegistrations() {
      try {
        uni.showLoading({
          title: '加载中...'
        })

        // TODO: 调用后端API获取用户的报名记录
        const response = await this.$http.get('/api/registration/my-registrations')

        uni.hideLoading()

        if (response.data.code === 0) {
          this.registrations = response.data.data
        } else {
          uni.showToast({
            title: response.data.message,
            icon: 'none'
          })
        }
      } catch (error) {
        uni.hideLoading()
        uni.showToast({
          title: '加载失败，请重试',
          icon: 'none'
        })
      }
    },
    handleFilterChange(filter) {
      this.currentFilter = filter
    },
    getStatusText(status) {
      const statusMap = {
        'PENDING': '待审核',
        'APPROVED': '已通过',
        'REJECTED': '已拒绝',
        'CANCELLED': '已取消'
      }
      return statusMap[status] || status
    },
    formatDate(date) {
      return new Date(date).toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    },
    navigateToDetail(id) {
      uni.navigateTo({
        url: `/pages/competition/detail?id=${id}`
      })
    },
    async handleCancel(id) {
      try {
        const [error, res] = await uni.showModal({
          title: '提示',
          content: '确定要取消报名吗？',
          confirmText: '确定',
          cancelText: '取消'
        })

        if (error || !res.confirm) {
          return
        }

        uni.showLoading({
          title: '取消中...'
        })

        // TODO: 调用后端API取消报名
        const response = await this.$http.post(`/api/registration/${id}/cancel`)

        uni.hideLoading()

        if (response.data.code === 0) {
          uni.showToast({
            title: '取消成功',
            icon: 'success'
          })
          this.loadRegistrations()
        } else {
          uni.showToast({
            title: response.data.message,
            icon: 'none'
          })
        }
      } catch (error) {
        uni.hideLoading()
        uni.showToast({
          title: '取消失败，请重试',
          icon: 'none'
        })
      }
    }
  }
}
</script>

<style>
.container {
  padding: 20px;
  background-color: #F8F8F8;
  min-height: 100vh;
}

.filter-bar {
  display: flex;
  background-color: #FFFFFF;
  padding: 10px;
  border-radius: 12px;
  margin-bottom: 20px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.filter-item {
  flex: 1;
  text-align: center;
  padding: 8px 0;
  font-size: 14px;
  color: #666666;
  position: relative;
}

.filter-item.active {
  color: #007AFF;
  font-weight: bold;
}

.filter-item.active::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 50%;
  transform: translateX(-50%);
  width: 20px;
  height: 2px;
  background-color: #007AFF;
  border-radius: 1px;
}

.registration-list {
  margin-bottom: 20px;
}

.registration-item {
  background-color: #FFFFFF;
  border-radius: 12px;
  margin-bottom: 15px;
  padding: 15px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.registration-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.competition-name {
  font-size: 16px;
  font-weight: bold;
  color: #333333;
}

.status-tag {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.status-tag.PENDING {
  background-color: #FFF7E6;
  color: #FA8C16;
}

.status-tag.APPROVED {
  background-color: #F6FFED;
  color: #52C41A;
}

.status-tag.REJECTED {
  background-color: #FFF1F0;
  color: #F5222D;
}

.status-tag.CANCELLED {
  background-color: #F5F5F5;
  color: #999999;
}

.registration-info {
  margin-bottom: 15px;
}

.info-row {
  display: flex;
  margin-bottom: 8px;
}

.info-row:last-child {
  margin-bottom: 0;
}

.label {
  width: 80px;
  font-size: 14px;
  color: #666666;
}

.value {
  flex: 1;
  font-size: 14px;
  color: #333333;
}

.registration-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.action-btn {
  padding: 6px 12px;
  border-radius: 4px;
  font-size: 14px;
}

.action-btn.detail {
  background-color: #E6F7FF;
  color: #1890FF;
}

.action-btn.cancel {
  background-color: #FFF1F0;
  color: #F5222D;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
}

.empty-icon {
  width: 120px;
  height: 120px;
  margin-bottom: 20px;
}

.empty-text {
  font-size: 14px;
  color: #999999;
}
</style> 