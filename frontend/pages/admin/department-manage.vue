<template>
  <view class="container">
    <view class="header">
      <text class="title">院系管理</text>
      <button class="add-btn" @click="showAddModal">添加院系</button>
    </view>

    <view class="search-box">
      <input type="text" v-model="searchKeyword" placeholder="搜索院系名称" @input="handleSearch" />
    </view>

    <view class="department-list">
      <view v-for="dept in departments" :key="dept.departmentId" class="department-item">
        <view class="department-info">
          <text class="department-name">{{ dept.name }}</text>
          <text class="department-contact">联系方式：{{ dept.contact }}</text>
        </view>
        <view class="department-actions">
          <button class="edit-btn" @click="handleEdit(dept)">编辑</button>
          <button class="delete-btn" @click="handleDelete(dept)">删除</button>
        </view>
      </view>
    </view>

    <!-- 添加/编辑院系弹窗 -->
    <view class="modal" v-if="showModal">
      <view class="modal-mask" @click="closeModal"></view>
      <view class="modal-content">
        <view class="modal-header">
          <text class="modal-title">{{ isEdit ? '编辑院系' : '添加院系' }}</text>
        </view>
        <view class="modal-body">
          <view class="form-item">
            <text class="label">院系名称</text>
            <input type="text" v-model="form.name" placeholder="请输入院系名称" />
          </view>
          <view class="form-item">
            <text class="label">联系方式</text>
            <input type="text" v-model="form.contact" placeholder="请输入联系方式" />
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
      departments: [],
      searchKeyword: '',
      isEdit: false,
      showModal: false,
      form: {
        departmentId: null,
        name: '',
        contact: ''
      }
    }
  },
  computed: {
    ...mapGetters(['isLoggedIn', 'userRole'])
  },
  onLoad() {
    this.checkAdminPermission()
    this.loadDepartments()
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

    // 加载院系列表
    async loadDepartments() {
      try {
        const token = uni.getStorageSync('token')
        const [error, res] = await uni.request({
          url: 'http://localhost:8080/api/departments',
          method: 'GET',
          header: {
            'Authorization': `Bearer ${token}`
          }
        })

        if (error) {
          throw new Error(error.errMsg)
        }

        if (res.data.code === 200) {
          this.departments = res.data.data
        } else {
          throw new Error(res.data.message)
        }
      } catch (error) {
        console.error('Error loading departments:', error)
        uni.showToast({
          title: error.message || '加载院系列表失败',
          icon: 'none'
        })
      }
    },

    // 搜索院系
    handleSearch() {
      if (!this.searchKeyword) {
        this.loadDepartments()
        return
      }

      this.departments = this.departments.filter(dept => 
        dept.name.toLowerCase().includes(this.searchKeyword.toLowerCase())
      )
    },

    // 显示添加弹窗
    showAddModal() {
      this.isEdit = false
      this.form = {
        departmentId: null,
        name: '',
        contact: ''
      }
      this.showModal = true
    },

    // 显示编辑弹窗
    handleEdit(dept) {
      this.isEdit = true
      this.form = { ...dept }
      this.showModal = true
    },

    // 关闭弹窗
    closeModal() {
      this.showModal = false
    },

    // 提交表单
    async handleSubmit() {
      try {
        if (!this.form.name) {
          throw new Error('请输入院系名称')
        }
        if (!this.form.contact) {
          throw new Error('请输入联系方式')
        }

        const token = uni.getStorageSync('token')
        const [error, res] = await uni.request({
          url: `http://localhost:8080/api/departments${this.isEdit ? `/${this.form.departmentId}` : ''}`,
          method: this.isEdit ? 'PUT' : 'POST',
          header: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          },
          data: this.form
        })

        if (error) {
          throw new Error(error.errMsg)
        }

        if (res.data.code === 200) {
          uni.showToast({
            title: this.isEdit ? '更新成功' : '添加成功',
            icon: 'success'
          })
          this.closeModal()
          this.loadDepartments()
        } else {
          throw new Error(res.data.message)
        }
      } catch (error) {
        console.error('Error submitting department:', error)
        uni.showToast({
          title: error.message || '操作失败',
          icon: 'none'
        })
      }
    },

    // 删除院系
    async handleDelete(dept) {
      try {
        const [error, res] = await uni.showModal({
          title: '确认删除',
          content: `确定要删除院系"${dept.name}"吗？`,
          confirmText: '删除',
          confirmColor: '#ff4d4f'
        })

        if (error || !res.confirm) {
          return
        }

        const token = uni.getStorageSync('token')
        const [deleteError, deleteRes] = await uni.request({
          url: `http://localhost:8080/api/departments/${dept.departmentId}`,
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
          this.loadDepartments()
        } else {
          throw new Error(deleteRes.data.message)
        }
      } catch (error) {
        console.error('Error deleting department:', error)
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

.department-list {
  background: #fff;
  border-radius: 16rpx;
  padding: 20rpx;
}

.department-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx;
  border-bottom: 1rpx solid #eee;
}

.department-item:last-child {
  border-bottom: none;
}

.department-info {
  flex: 1;
}

.department-name {
  font-size: 32rpx;
  color: #333;
  margin-bottom: 10rpx;
  display: block;
}

.department-contact {
  font-size: 26rpx;
  color: #666;
}

.department-actions {
  display: flex;
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