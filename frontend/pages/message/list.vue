<template>
  <view class="container">
    <!-- 分类筛选栏 -->
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

    <!-- 消息列表 -->
    <view class="message-list">
      <view 
        v-for="(item, index) in filteredMessages" 
        :key="index"
        class="message-item"
        :class="{ unread: !item.isRead }"
        @click="handleMessageClick(item)"
      >
        <view class="message-header">
          <text class="message-title">{{ item.title }}</text>
          <text class="message-time">{{ formatDate(item.createTime) }}</text>
        </view>
        
        <view class="message-content">
          <text class="message-text">{{ item.content }}</text>
        </view>

        <view class="message-footer">
          <text class="message-type">{{ getTypeText(item.type) }}</text>
          <view class="message-actions">
            <button 
              class="action-btn delete" 
              @click.stop="handleDelete(item.id)"
            >删除</button>
          </view>
        </view>
      </view>
    </view>

    <!-- 空状态 -->
    <view v-if="filteredMessages.length === 0" class="empty-state">
      <image class="empty-icon" src="/static/images/empty.png" mode="aspectFit"></image>
      <text class="empty-text">暂无消息</text>
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
        { label: '系统通知', value: 'SYSTEM' },
        { label: '竞赛通知', value: 'COMPETITION' },
        { label: '成绩通知', value: 'SCORE' }
      ],
      messages: []
    }
  },
  computed: {
    filteredMessages() {
      if (this.currentFilter === 'all') {
        return this.messages
      }
      return this.messages.filter(item => item.type === this.currentFilter)
    }
  },
  onShow() {
    this.loadMessages()
  },
  methods: {
    async loadMessages() {
      try {
        uni.showLoading({
          title: '加载中...'
        })

        // TODO: 调用后端API获取用户的消息列表
        const response = await this.$http.get('/api/message/list')

        uni.hideLoading()

        if (response.data.code === 0) {
          this.messages = response.data.data
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
    getTypeText(type) {
      const typeMap = {
        'SYSTEM': '系统通知',
        'COMPETITION': '竞赛通知',
        'SCORE': '成绩通知'
      }
      return typeMap[type] || type
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
    async handleMessageClick(message) {
      if (!message.isRead) {
        try {
          // TODO: 调用后端API标记消息为已读
          await this.$http.post(`/api/message/${message.id}/read`)
          message.isRead = true
        } catch (error) {
          console.error('标记消息已读失败:', error)
        }
      }

      // 根据消息类型处理点击事件
      if (message.type === 'COMPETITION' && message.competitionId) {
        uni.navigateTo({
          url: `/pages/competition/detail?id=${message.competitionId}`
        })
      } else if (message.type === 'SCORE' && message.scoreId) {
        uni.navigateTo({
          url: `/pages/score/detail?id=${message.scoreId}`
        })
      }
    },
    async handleDelete(id) {
      try {
        const [error, res] = await uni.showModal({
          title: '提示',
          content: '确定要删除这条消息吗？',
          confirmText: '确定',
          cancelText: '取消'
        })

        if (error || !res.confirm) {
          return
        }

        uni.showLoading({
          title: '删除中...'
        })

        // TODO: 调用后端API删除消息
        const response = await this.$http.delete(`/api/message/${id}`)

        uni.hideLoading()

        if (response.data.code === 0) {
          uni.showToast({
            title: '删除成功',
            icon: 'success'
          })
          this.messages = this.messages.filter(item => item.id !== id)
        } else {
          uni.showToast({
            title: response.data.message,
            icon: 'none'
          })
        }
      } catch (error) {
        uni.hideLoading()
        uni.showToast({
          title: '删除失败，请重试',
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

.message-list {
  margin-bottom: 20px;
}

.message-item {
  background-color: #FFFFFF;
  border-radius: 12px;
  margin-bottom: 15px;
  padding: 15px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.message-item.unread {
  background-color: #F0F7FF;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.message-title {
  font-size: 16px;
  font-weight: bold;
  color: #333333;
}

.message-time {
  font-size: 12px;
  color: #999999;
}

.message-content {
  margin-bottom: 10px;
}

.message-text {
  font-size: 14px;
  color: #666666;
  line-height: 1.5;
}

.message-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.message-type {
  font-size: 12px;
  color: #999999;
  padding: 2px 8px;
  background-color: #F5F5F5;
  border-radius: 10px;
}

.message-actions {
  display: flex;
  gap: 10px;
}

.action-btn {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.action-btn.delete {
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