<template>
  <view class="container">
    <view class="header">
      <text class="title">单位管理</text>
      <button class="add-btn" @click="showAddModal">添加单位</button>
    </view>

    <view class="search-box">
      <input type="text" v-model="searchKeyword" placeholder="搜索单位名称" @input="handleSearch" />
    </view>

    <view class="unit-list">
      <view v-for="unit in units" :key="unit.hostUnitId" class="unit-item">
        <view class="unit-info">
          <text class="unit-name">{{ unit.name }}</text>
        </view>
        <view class="unit-actions">
          <button class="edit-btn" @click="handleEdit(unit)">编辑</button>
          <button class="delete-btn" @click="handleDelete(unit)">删除</button>
        </view>
      </view>
    </view>

    <!-- 添加/编辑单位弹窗 -->
    <view class="modal" v-if="showModal">
      <view class="modal-mask" @click="closeModal"></view>
      <view class="modal-content">
        <view class="modal-header">
          <text class="modal-title">{{ isEdit ? '编辑单位' : '添加单位' }}</text>
        </view>
        <view class="modal-body">
          <view class="form-item">
            <text class="label">单位名称</text>
            <input type="text" v-model="form.name" placeholder="请输入单位名称" />
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
      units: [],
      searchKeyword: '',
      isEdit: false,
      showModal: false,
      form: {
        hostUnitId: null,
        name: ''
      }
    }
  },
  computed: {
    ...mapGetters(['isLoggedIn', 'userRole'])
  },
  onLoad() {
    this.checkAdminPermission()
    this.loadUnits()
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

    // 加载单位列表
    async loadUnits() {
      try {
        const token = uni.getStorageSync('token')
        const [error, res] = await uni.request({
          url: 'http://localhost:8080/api/host-units',
          method: 'GET',
          header: {
            'Authorization': `Bearer ${token}`
          }
        })

        if (error) {
          throw new Error(error.errMsg)
        }

        if (res.data.code === 200) {
          this.units = res.data.data
        } else {
          throw new Error(res.data.message)
        }
      } catch (error) {
        console.error('Error loading units:', error)
        uni.showToast({
          title: error.message || '加载单位列表失败',
          icon: 'none'
        })
      }
    },

    // 搜索单位
    handleSearch() {
      if (!this.searchKeyword) {
        this.loadUnits()
        return
      }

      this.units = this.units.filter(unit => 
        unit.name.toLowerCase().includes(this.searchKeyword.toLowerCase())
      )
    },

    // 显示添加弹窗
    showAddModal() {
      this.isEdit = false
      this.form = {
        hostUnitId: null,
        name: ''
      }
      this.showModal = true
    },

    // 显示编辑弹窗
    handleEdit(unit) {
      this.isEdit = true
      this.form = { ...unit }
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
          throw new Error('请输入单位名称')
        }

        const token = uni.getStorageSync('token')
        const [error, res] = await uni.request({
          url: `http://localhost:8080/api/host-units${this.isEdit ? `/${this.form.hostUnitId}` : ''}`,
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
          this.loadUnits()
        } else {
          throw new Error(res.data.message)
        }
      } catch (error) {
        console.error('Error submitting unit:', error)
        uni.showToast({
          title: error.message || '操作失败',
          icon: 'none'
        })
      }
    },

    // 删除单位
    async handleDelete(unit) {
      try {
        const [error, res] = await uni.showModal({
          title: '确认删除',
          content: `确定要删除单位"${unit.name}"吗？`,
          confirmText: '删除',
          confirmColor: '#ff4d4f'
        })

        if (error || !res.confirm) {
          return
        }

        const token = uni.getStorageSync('token')
        const [deleteError, deleteRes] = await uni.request({
          url: `http://localhost:8080/api/host-units/${unit.hostUnitId}`,
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
          this.loadUnits()
        } else {
          throw new Error(deleteRes.data.message)
        }
      } catch (error) {
        console.error('Error deleting unit:', error)
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

.unit-list {
  background: #fff;
  border-radius: 16rpx;
  padding: 20rpx;
}

.unit-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx;
  border-bottom: 1rpx solid #eee;
}

.unit-item:last-child {
  border-bottom: none;
}

.unit-info {
  flex: 1;
}

.unit-name {
  font-size: 32rpx;
  color: #333;
}

.unit-actions {
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