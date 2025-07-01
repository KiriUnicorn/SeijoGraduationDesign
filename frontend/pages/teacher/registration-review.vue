<template>
  <view class="container">
    <view class="header">
      <text class="title">报名审核</text>
      <view class="filter-section">
        <picker mode="selector" :range="statusOptions" @change="handleStatusChange">
          <view class="picker">
            状态：{{ statusOptions[statusIndex] }}
          </view>
        </picker>
      </view>
    </view>

    <view class="registration-list">
      <view v-if="registrations.length === 0" class="empty-tip">
        <text>暂无待审核的报名信息</text>
      </view>
      <view v-else class="registration-item" v-for="item in registrations" :key="item.registrationId" @click="handleItemClick(item)">
        <view class="item-header">
          <text class="competition-name">{{ item.competitionName }}</text>
          <text :class="['status-tag', getStatusClass(item.status)]">{{ getStatusText(item.status) }}</text>
        </view>
        
        <view class="item-content">
          <view class="info-row">
            <text class="label">队伍名称：</text>
            <text class="value">{{ item.teamName }}</text>
          </view>
		  <view class="info-row">
		    <text class="label">报名者姓名：</text>
		    <text class="value">{{ item.registrationName }}</text>
		  </view>
          <view class="info-row">
            <text class="label">队长：</text>
            <text class="value">{{ item.captainName }}</text>
          </view>
          <view class="info-row">
            <text class="label">队员：</text>
            <text class="value">{{ item.memberNames }}</text>
          </view>
          <view class="info-row">
            <text class="label">指导教师：</text>
            <text class="value">{{ item.teacherName }}</text>
          </view>
          <view class="info-row">
            <text class="label">报名时间：</text>
            <text class="value">{{ item.createdAt }}</text>
          </view>
        </view>

        <view class="item-footer" v-if="item.status === 'PENDING'">
          <button class="action-btn reject" @click="handleReject(item)">拒绝</button>
          <button class="action-btn approve" @click="handleApprove(item)">通过</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      statusOptions: ['全部', '待审核', '已通过', '已拒绝'],
      statusIndex: 0,
      registrations: [],
      statusMap: {
        'PENDING': '待审核',
        'APPROVED': '已通过',
        'REJECTED': '已拒绝',
        'pending': '待审核',
        'approved': '已通过',
        'rejected': '已拒绝'
      }
    }
  },
  onShow() {
    this.loadRegistrations()
  },
  methods: {
    async loadRegistrations() {
      try {
        const token = uni.getStorageSync('token')
        console.log('Token:', token)
        if (!token) {
          uni.showToast({
            title: '请先登录',
            icon: 'none'
          })
          return
        }

        const status = this.statusIndex === 0 ? '' : 
                      this.statusIndex === 1 ? 'PENDING' :
                      this.statusIndex === 2 ? 'APPROVED' : 'REJECTED'
        console.log('Request status:', status)

        const requestUrl = `http://localhost:8080/api/registrations/teacher/review`
        console.log('Request URL:', requestUrl)
        
        // 确保token格式正确
        const authToken = token.startsWith('Bearer ') ? token : `Bearer ${token}`
        console.log('Request headers:', {
          'Content-Type': 'application/json',
          'Authorization': authToken
        })

        const [error, res] = await uni.request({
          url: requestUrl,
          method: 'GET',
          header: {
            'Content-Type': 'application/json',
            'Authorization': authToken
          },
          data: { status }
        })

        console.log('Response:', res)
        console.log('Response status:', res.statusCode)
        console.log('Response data:', res.data)

        if (error) {
          console.error('Request error:', error)
          throw new Error(error.errMsg)
        }

        if (res.statusCode === 200 && res.data.code === 200) {
          // 检查并处理返回的数据
          const registrations = res.data.data
          console.log('处理前的报名列表:', registrations)
          
          // 确保每个报名项都有必要的字段
          this.registrations = registrations.map(item => ({
            registrationId: item.registrationId || item.registration_id,
            studentId: item.studentId || item.student_id,
            competitionId: item.competitionId || item.competition_id,
            status: item.status,
            teacherApproverId: item.teacherApproverId || item.teacher_approver_id,
            createAt: item.createAt || item.created_at,
            teamId: item.teamId || item.team_id,
            competitionName: item.competitionName || item.competition_name,
            teamName: item.teamName || item.team_name,
			registrationName: item.registrationName || item.registration_name,
            captainName: item.captainName || item.captain_name,
            memberNames: item.memberNames || item.member_names,
            teacherName: item.teacherName || item.teacher_name
          }))
          
          console.log('处理后的报名列表:', this.registrations)
        } else {
          console.error('Response error:', res.data)
          throw new Error(res.data.message || '加载失败')
        }
      } catch (error) {
        console.error('Catch error:', error)
        uni.showToast({
          title: error.message || '加载失败',
          icon: 'none'
        })
      }
    },

    handleStatusChange(e) {
      this.statusIndex = e.detail.value
      this.loadRegistrations()
    },

    getStatusText(status) {
      return this.statusMap[status?.toUpperCase()] || status
    },

    getStatusClass(status) {
      const upperStatus = status?.toUpperCase()
      return {
        'PENDING': 'status-pending',
        'APPROVED': 'status-approved',
        'REJECTED': 'status-rejected'
      }[upperStatus] || ''
    },

    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
    },

    async handleApprove(item) {
      try {
        const [error, res] = await uni.showModal({
          title: '确认通过',
          content: '确定要通过该报名申请吗？'
        })

        if (error || !res.confirm) return

        const token = uni.getStorageSync('token')
        const authToken = token.startsWith('Bearer ') ? token : `Bearer ${token}`
        const [requestError, response] = await uni.request({
          url: `http://localhost:8080/api/registrations/${item.registrationId}/approve`,
          method: 'POST',
          header: {
            'Content-Type': 'application/json',
            'Authorization': authToken
          }
        })

        if (requestError) {
          throw new Error(requestError.errMsg)
        }

        if (response.data.code === 200) {
          uni.showToast({
            title: '审核通过成功',
            icon: 'success'
          })
          this.loadRegistrations()
        } else {
          throw new Error(response.data.message)
        }
      } catch (error) {
        uni.showToast({
          title: error.message || '操作失败',
          icon: 'none'
        })
      }
    },

    async handleReject(item) {
      try {
        const [error, res] = await uni.showModal({
          title: '确认拒绝',
          content: '确定要拒绝该报名申请吗？',
          confirmText: '确定拒绝',
          confirmColor: '#ff4d4f'
        })

        if (error || !res.confirm) return

        const token = uni.getStorageSync('token')
        const authToken = token.startsWith('Bearer ') ? token : `Bearer ${token}`
        const [requestError, response] = await uni.request({
          url: `http://localhost:8080/api/registrations/${item.registrationId}/reject`,
          method: 'POST',
          header: {
            'Content-Type': 'application/json',
            'Authorization': authToken
          }
        })

        if (requestError) {
          throw new Error(requestError.errMsg)
        }

        if (response.data.code === 200) {
          uni.showToast({
            title: '已拒绝该申请',
            icon: 'success'
          })
          this.loadRegistrations()
        } else {
          throw new Error(response.data.message)
        }
      } catch (error) {
        uni.showToast({
          title: error.message || '操作失败',
          icon: 'none'
        })
      }
    },

    handleItemClick(item) {
      console.log('点击报名项:', item)
      console.log('报名项状态:', item.status)
      console.log('报名项ID:', item.registrationId)
      
      if (!item.registrationId) {
        console.error('报名项ID为空')
        uni.showToast({
          title: '报名信息不完整',
          icon: 'none'
        })
        return
      }
      
      if (item.status?.toUpperCase() === 'PENDING') {
        uni.showModal({
          title: '审核操作',
          content: '请选择审核操作',
          showCancel: true,
          cancelText: '拒绝',
          confirmText: '通过',
          success: (res) => {
            if (res.confirm) {
              this.handleApprove(item)
            } else if (res.cancel) {
              this.handleReject(item)
            }
          }
        })
      }
    }
  }
}
</script>

<style>
.container {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.header {
  margin-bottom: 20px;
}

.title {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 15px;
}

.filter-section {
  background-color: #fff;
  padding: 10px;
  border-radius: 8px;
  margin-bottom: 15px;
}

.picker {
  padding: 8px;
  font-size: 16px;
  color: #333;
}

.registration-list {
  padding-bottom: 20px;
}

.empty-tip {
  text-align: center;
  padding: 40px 0;
  color: #999;
  font-size: 16px;
}

.registration-item {
  background-color: #fff;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 15px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.competition-name {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.status-tag {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 14px;
}

.status-pending {
  background-color: #e6f7ff;
  color: #1890ff;
}

.status-approved {
  background-color: #f6ffed;
  color: #52c41a;
}

.status-rejected {
  background-color: #fff2f0;
  color: #ff4d4f;
}

.item-content {
  padding: 10px 0;
}

.info-row {
  display: flex;
  margin-bottom: 8px;
  font-size: 14px;
  line-height: 1.5;
}

.label {
  color: #666;
  width: 80px;
  flex-shrink: 0;
}

.value {
  color: #333;
  flex: 1;
}

.item-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #f0f0f0;
}

.action-btn {
  padding: 6px 16px;
  border-radius: 4px;
  font-size: 14px;
  border: none;
}

.action-btn.approve {
  background-color: #52c41a;
  color: #fff;
}

.action-btn.reject {
  background-color: #ff4d4f;
  color: #fff;
}
</style> 