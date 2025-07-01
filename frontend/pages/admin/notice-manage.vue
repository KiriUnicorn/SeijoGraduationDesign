<template>
  <view class="container">
    <view class="header">
      <text class="title">公告管理</text>
      <button class="add-btn" @click="showAddModal">发布公告</button>
    </view>

    <view class="search-box">
      <input type="text" v-model="searchKeyword" placeholder="搜索公告标题" @input="handleSearch" />
    </view>

    <view class="notice-list">
      <view v-for="notice in notices" :key="notice.announcementId" class="notice-item">
        <view class="notice-info">
          <text class="notice-title">{{ notice.title }}</text>
          <text class="notice-time">发布时间：{{ formatDate(notice.createdAt) }}</text>
        </view>
        <view class="notice-content">{{ notice.content }}</view>
        <view class="notice-actions">
          <button class="edit-btn" @click="handleEdit(notice)">编辑</button>
          <button class="delete-btn" @click="handleDelete(notice)">删除</button>
        </view>
      </view>
    </view>

    <!-- 添加/编辑公告弹窗 -->
    <view class="modal" v-if="showModal">
      <view class="modal-mask" @click="closeModal"></view>
      <view class="modal-content">
        <view class="modal-header">
          <text class="modal-title">{{ isEdit ? '编辑公告' : '发布公告' }}</text>
        </view>
        <view class="modal-body">
          <view class="form-item">
            <text class="label">公告标题</text>
            <input type="text" v-model="form.title" placeholder="请输入公告标题" />
          </view>
          <view class="form-item">
            <text class="label">公告内容</text>
            <textarea v-model="form.content" placeholder="请输入公告内容" class="content-textarea" />
          </view>
        </view>
        <view class="modal-footer">
          <button class="cancel-btn" @click="closeModal">取消</button>
          <button class="confirm-btn" @click="handleSubmit">确定</button>
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
      notices: [],
      searchKeyword: '',
      isEdit: false,
      showModal: false,
      form: {
        announcementId: null,
		adminId: '',
        title: '',
        content: ''
      }
    }
  },
  computed: {
    ...mapGetters(['isLoggedIn', 'userRole'])
  },
  onLoad() {
    this.checkAdminPermission()
    this.loadNotices()
	this.getAdminId()

  },
  methods: {
    // 检查管理员权限
    checkAdminPermission() {
      if (!this.isLoggedIn) {
        uni.showToast({
          title: '请先登录',
          icon: 'none'
        })
        setTimeout(() => {
          uni.navigateTo({
            url: '/pages/user/login'
          })
        }, 1500)
        return
      }
      
      if (this.userRole !== 'admin') {
        uni.showToast({
          title: '您没有管理员权限',
          icon: 'none'
        })
        setTimeout(() => {
          uni.navigateBack()
        }, 1500)
      }
    },

    // 加载公告列表
    async loadNotices() {
      try {
        const token = uni.getStorageSync('token')
        const [error, res] = await uni.request({
          url: 'http://localhost:8080/api/announcements',
          method: 'GET',
          header: {
            'Authorization': `Bearer ${token}`
          }
        })

        if (error) {
          throw new Error(error.errMsg)
        }

        if (res.data.code === 200) {
          this.notices = res.data.data
        } else {
          throw new Error(res.data.message)
        }
      } catch (error) {
        console.error('Error loading notices:', error)
        uni.showToast({
          title: error.message || '加载公告列表失败',
          icon: 'none'
        })
      }
    },

    // 搜索公告
    handleSearch() {
      if (!this.searchKeyword) {
        this.loadNotices()
        return
      }

      this.notices = this.notices.filter(notice => 
        notice.title.toLowerCase().includes(this.searchKeyword.toLowerCase())
      )
    },

    // 格式化日期
    formatDate(dateStr) {
      const date = new Date(dateStr)
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
    },

    // 显示添加弹窗
    showAddModal() {
      this.isEdit = false
      this.form = {
        announcementId: null,
        title: '',
        content: ''
      }
      this.showModal = true
    },

    // 显示编辑弹窗
    handleEdit(notice) {
      this.isEdit = true
      this.form = { ...notice }
      this.showModal = true
    },

    // 关闭弹窗
    closeModal() {
      this.showModal = false
    },
	getAdminId(){
		const userInfo = uni.getStorageSync('userInfo')
		this.form.adminId = userInfo.userId
		console.log('userId:'+JSON.stringify(userInfo))
		console.log('form!:'+ JSON.stringify(this.form))
	},
    // 提交表单
    async handleSubmit() {
		const userInfo = uni.getStorageSync('userInfo')
		this.form.adminId = userInfo.userId
      try {
        if (!this.form.title) {
          throw new Error('请输入公告标题')
        }
        if (!this.form.content) {
          throw new Error('请输入公告内容')
        }
		
        const token = uni.getStorageSync('token')
		const userInfo = uni.getStorageSync('userInfo')
        const [error, res] = await uni.request({
          url: `http://localhost:8080/api/announcements${this.isEdit ? `/${this.form.announcementId}` : ''}`,
          method: this.isEdit ? 'PUT' : 'POST',
          header: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          },
          data: this.form,
        })

        if (error) {
          throw new Error(error.errMsg)
        }

        if (res.data.code === 200) {
          uni.showToast({
            title: this.isEdit ? '更新成功' : '发布成功',
            icon: 'success'
          })
          this.closeModal()
          this.loadNotices()
        } else {
          throw new Error(res.data.message)
        }
      } catch (error) {
        console.error('Error submitting notice:', error)
        uni.showToast({
          title: error.message || '操作失败',
          icon: 'none'
        })
      }
    },

    // 删除公告
    async handleDelete(notice) {
      try {
        const [error, res] = await uni.showModal({
          title: '确认删除',
          content: `确定要删除公告"${notice.title}"吗？`,
          confirmText: '删除',
          confirmColor: '#ff4d4f'
        })

        if (error || !res.confirm) {
          return
        }

        const token = uni.getStorageSync('token')
        const [deleteError, deleteRes] = await uni.request({
          url: `http://localhost:8080/api/announcements/${notice.announcementId}`,
          method: 'DELETE',
          header: {
            'Authorization': `Bearer ${token}`
          }
        })

        if (deleteError) {
          throw new Error(deleteError.errMsg)
        }

        if (deleteRes.data.code === 200) {
          uni.showToast({
            title: '删除成功',
            icon: 'success'
          })
          this.loadNotices()
        } else {
          throw new Error(deleteRes.data.message)
        }
      } catch (error) {
        console.error('Error deleting notice:', error)
        uni.showToast({
          title: error.message || '删除失败',
          icon: 'none'
        })
      }
    }
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
  background: #1890ff;
  color: #fff;
  font-size: 28rpx;
  padding: 10rpx 30rpx;
  border-radius: 8rpx;
}

.search-box {
  margin-bottom: 30rpx;
}

.search-box input {
  width: 100%;
  height: 80rpx;
  background: #fff;
  border-radius: 8rpx;
  padding: 0 20rpx;
  font-size: 28rpx;
}

.notice-list {
  background: #fff;
  border-radius: 16rpx;
  padding: 20rpx;
}

.notice-item {
  padding: 20rpx;
  border-bottom: 1rpx solid #eee;
}

.notice-item:last-child {
  border-bottom: none;
}

.notice-info {
  margin-bottom: 10rpx;
}

.notice-title {
  font-size: 32rpx;
  color: #333;
  margin-bottom: 10rpx;
  display: block;
}

.notice-time {
  font-size: 26rpx;
  color: #666;
}

.notice-content {
  font-size: 28rpx;
  color: #666;
  margin: 20rpx 0;
  line-height: 1.6;
}

.notice-actions {
  display: flex;
  justify-content: flex-end;
  gap: 20rpx;
}

.edit-btn, .delete-btn {
  font-size: 26rpx;
  padding: 10rpx 20rpx;
  border-radius: 6rpx;
}

.edit-btn {
  background: #e6f7ff;
  color: #1890ff;
}

.delete-btn {
  background: #fff1f0;
  color: #ff4d4f;
}

/* 弹窗样式 */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 999;
}

.modal-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
}

.modal-content {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: #fff;
  border-radius: 16rpx;
  width: 600rpx;
  padding: 30rpx;
}

.modal-header {
  margin-bottom: 30rpx;
}

.modal-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.form-item {
  margin-bottom: 20rpx;
}

.label {
  font-size: 28rpx;
  color: #666;
  margin-bottom: 10rpx;
  display: block;
}

input {
  width: 100%;
  height: 80rpx;
  background: #f5f7fa;
  border-radius: 8rpx;
  padding: 0 20rpx;
  font-size: 28rpx;
}

.content-textarea {
  width: 100%;
  height: 300rpx;
  background: #f5f7fa;
  border-radius: 8rpx;
  padding: 20rpx;
  font-size: 28rpx;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 20rpx;
  margin-top: 30rpx;
}

.cancel-btn, .confirm-btn {
  font-size: 28rpx;
  padding: 10rpx 30rpx;
  border-radius: 8rpx;
}

.cancel-btn {
  background: #f5f5f5;
  color: #666;
}

.confirm-btn {
  background: #1890ff;
  color: #fff;
}
</style> 