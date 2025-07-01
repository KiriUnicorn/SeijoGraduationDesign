<template>
  <view class="container">
    <view class="header">
      <text class="title">用户管理</text>
      <view class="search-bar">
        <input type="text" v-model="searchQuery" placeholder="搜索ID/用户名/姓名" @input="handleSearch" />
        <button class="search-btn" @click="handleSearch">🔍</button>
      </view>
    </view>
    
    <view class="filter-bar">
      <view class="filter-item" :class="{ active: currentFilter === 'all' }" @click="setFilter('all')">全部</view>
      <view class="filter-item" :class="{ active: currentFilter === 'student' }" @click="setFilter('student')">学生用户</view>
      <view class="filter-item" :class="{ active: currentFilter === 'teacher' }" @click="setFilter('teacher')">教师用户</view>
      <view class="filter-item" :class="{ active: currentFilter === 'admin' }" @click="setFilter('admin')">管理员</view>
    </view>
    
    <view class="user-list">
      <view v-if="filteredUsers.length === 0" class="empty-tip">
        <text>暂无用户数据</text>
      </view>
      <view v-else class="user-item" v-for="user in filteredUsers" :key="user.id" @click="showUserDetail(user)">
        <view class="user-info">
          <text class="user-name">{{ user.name }}</text>
          <text class="user-role" :class="getRoleClass(user.role)">{{ getRoleText(user.role) }}</text>
          <text class="user-id">学号：{{ user.id }}</text>
		  <text class="user-dpname">{{ user.departmentName }}</text>
		  <view><text class="user-username">账号：{{ user.email }}</text></view>
		  
        </view>
        <text class="arrow">></text>
      </view>
    </view>

    <!-- 分页组件 -->
    <view class="pagination">
      <view class="page-info">
        <text>共 {{ total }} 条</text>
        <text>第 {{ currentPage }}/{{ totalPages }} 页</text>
      </view>
      <view class="page-buttons">
        <button 
          class="page-btn" 
          :disabled="currentPage === 1"
          @click="handlePageChange(currentPage - 1)"
        >上一页</button>
        <button 
          class="page-btn" 
          :disabled="currentPage === totalPages"
          @click="handlePageChange(currentPage + 1)"
        >下一页</button>
      </view>
    </view>

    <!-- 用户详情弹窗 -->
    <view v-if="currentUser" class="modal" :class="{ 'modal-show': showModal }">
      <view class="modal-mask" @click="closeModal"></view>
      <view class="modal-content">
        <view class="modal-header">
          <text class="modal-title">用户详情</text>
        </view>
        <view class="modal-body">
          <view class="form-item">
            <text class="label">用户名：</text>
            <input type="text" v-model="currentUser.email"  />
          </view>
          <view class="form-item">
            <text class="label">姓名：</text>
            <input type="text" v-model="currentUser.name" />
          </view>
          <view class="form-item">
            <text class="label">角色：</text>
            <picker :range="roleOptions" :value="roleIndex" disabled>
              <view class="picker-value">{{ roleOptions[roleIndex] }}</view>
            </picker>
          </view>
          <view class="form-item" v-if="currentUser.role === 'student'">
            <text class="label">学号：</text>
            <input type="text" v-model="currentUser.studentId" />
          </view>
          <view class="form-item" v-if="currentUser.role === 'teacher'">
            <text class="label">工号：</text>
            <input type="text" v-model="currentUser.teacherId" />
          </view>
          <view class="form-item">
            <text class="label">院系：</text>
            <picker :range="departments" range-key="name" :value="departmentIndex" @change="handleDepartmentChange">
              <view class="picker-value">{{ departments[departmentIndex] ? departments[departmentIndex].name : currentUser.departmentName }}</view>
            </picker>
          </view>
        </view>
        <view class="modal-footer">
          <button class="delete-btn" @click="handleDelete">删除</button>
          <button class="cancel-btn" @click="closeModal">取消</button>
          <button class="confirm-btn" @click="handleUpdate">保存</button>
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
      searchQuery: '',
      currentFilter: 'all',
      users: [],
      allUsers: [], // 存储所有用户数据
      isLoading: false,
      currentUser: null,
      roleOptions: ['学生', '教师', '管理员'],
      roleIndex: 0,
      showModal: false,
      // 分页相关数据
      currentPage: 1,
      pageSize: 8,
      total: 0,
      totalPages: 0,
      // 院系相关数据
      departments: [],
      departmentIndex: 0
    }
  },
  computed: {
    ...mapGetters(['isLoggedIn', 'userRole']),
    filteredUsers() {
      if (!this.allUsers) return []
      
      let filtered = [...this.allUsers]
      
      // 按角色筛选
      if (this.currentFilter !== 'all') {
        filtered = filtered.filter(user => user.role === this.currentFilter)
      }
      
      // 按关键词搜索
      if (this.searchQuery) {
        const keyword = this.searchQuery.toLowerCase()
        filtered = filtered.filter(user => 
          (user.email && user.email.toLowerCase().includes(keyword)) ||
          (user.name && user.name.toLowerCase().includes(keyword)) ||
          (user.id && user.id.toString().includes(keyword))
        )
      }

      // 更新总数和总页数
      this.total = filtered.length
      this.totalPages = Math.ceil(this.total / this.pageSize)
      
      // 计算当前页的数据
      const start = (this.currentPage - 1) * this.pageSize
      const end = start + this.pageSize
      return filtered.slice(start, end)
    }
  },
  onLoad() {
    this.checkAdminPermission()
    this.loadUsers()
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
    
    // 加载用户列表
    async loadUsers() {
      try {
        const token = uni.getStorageSync('token')
        const [error, res] = await uni.request({
          url: 'http://localhost:8080/api/users',
          method: 'GET',
          header: {
            'Authorization': `Bearer ${token}`
          }
        })
        
        if (error) {
          throw new Error(error.errMsg)
        }
        
        if (res.data.code === 200) {
          this.allUsers = res.data.data
          this.total = this.allUsers.length
          this.totalPages = Math.ceil(this.total / this.pageSize)
        } else {
          uni.showToast({
            title: res.data.message || '加载用户列表失败',
            icon: 'none'
          })
        }
      } catch (error) {
        console.error('加载用户列表失败:', error)
        uni.showToast({
          title: error.message || '加载用户列表失败',
          icon: 'none'
        })
      }
    },
    
    // 搜索用户
    handleSearch() {
      this.currentPage = 1 // 搜索时重置到第一页
    },
    
    // 设置筛选条件
    setFilter(filter) {
      if (this.currentFilter === filter) return
      this.currentFilter = filter
      this.currentPage = 1 // 切换筛选条件时重置到第一页
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
          uni.showToast({
            title: res.data.message || '加载院系列表失败',
            icon: 'none'
          })
        }
      } catch (error) {
        console.error('加载院系列表失败:', error)
        uni.showToast({
          title: error.message || '加载院系列表失败',
          icon: 'none'
        })
      }
    },

    // 处理院系变更
    handleDepartmentChange(e) {
      this.departmentIndex = e.detail.value
      this.currentUser.departmentId = this.departments[this.departmentIndex].id
      this.currentUser.departmentName = this.departments[this.departmentIndex].name
    },

    // 显示用户详情
    showUserDetail(user) {
      this.currentUser = { ...user }
      this.roleIndex = this.roleOptions.findIndex(role => 
        this.getRoleText(user.role) === role
      )
      // 设置当前院系索引
      this.departmentIndex = this.departments.findIndex(dept => 
        dept.id === user.departmentId
      ) || 0
      this.showModal = true
    },

    // 关闭弹窗
    closeModal() {
      this.showModal = false
      setTimeout(() => {
        this.currentUser = null
      }, 300)
    },

    // 处理角色变更
    handleRoleChange(e) {
      this.roleIndex = e.detail.value
      const roleMap = {
        '学生': 'student',
        '教师': 'teacher',
        '管理员': 'admin'
      }
      this.currentUser.role = roleMap[this.roleOptions[this.roleIndex]]
    },

    // 更新用户信息
    async handleUpdate() {
      try {
        const token = uni.getStorageSync('token')
        const [error, res] = await uni.request({
          url: `http://localhost:8080/api/users/${this.currentUser.id}`,
          method: 'PUT',
          header: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          },
          data: this.currentUser
        })

        if (error) {
          throw new Error(error.errMsg)
        }

        if (res.data.code === 200) {
          uni.showToast({
            title: '更新成功',
            icon: 'success'
          })
          this.closeModal()
          this.loadUsers()
        } else {
          throw new Error(res.data.message)
        }
      } catch (error) {
        console.error('Error updating user:', error)
        uni.showToast({
          title: error.message || '更新失败',
          icon: 'none'
        })
      }
    },

    // 删除用户
    async handleDelete() {
      try {
        const [confirmError, confirmRes] = await uni.showModal({
          title: '确认删除',
          content: '确定要删除这个用户吗？',
          confirmText: '确定',
          cancelText: '取消'
        })

        if (confirmError || !confirmRes.confirm) {
          return
        }

        const token = uni.getStorageSync('token')
        const [error, res] = await uni.request({
          url: `http://localhost:8080/api/users/${this.currentUser.id}?role=${this.currentUser.role}`,
          method: 'DELETE',
          header: {
            'Authorization': `Bearer ${token}`
          }
        })

        if (error) {
          throw new Error(error.errMsg)
        }

        if (res.data.code === 200) {
          uni.showToast({
            title: '删除成功',
            icon: 'success'
          })
          this.closeModal()
          this.loadUsers()
        } else {
          throw new Error(res.data.message)
        }
      } catch (error) {
        console.error('Error deleting user:', error)
        uni.showToast({
          title: error.message || '删除失败',
          icon: 'none'
        })
      }
    },

    // 获取角色文本
    getRoleText(role) {
      const roleMap = {
        'student': '学生',
        'teacher': '教师',
        'admin': '管理员'
      }
      return roleMap[role] || role
    },

    // 获取角色样式类
    getRoleClass(role) {
      const classMap = {
        'student': 'role-student',
        'teacher': 'role-teacher',
        'admin': 'role-admin'
      }
      return classMap[role] || ''
    },

    handlePageChange(page) {
      this.currentPage = page
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
  margin-bottom: 30rpx;
}

.title {
  font-size: 40rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
}

.search-bar {
  position: relative;
  margin-bottom: 20rpx;
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.search-bar input {
  flex: 1;
  height: 80rpx;
  background: #fff;
  border-radius: 40rpx;
  padding: 0 30rpx;
  font-size: 28rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
}

.search-btn {
  height: 80rpx;
  padding: 0 40rpx;
  background: #ffffff;
  color: #fff;
  border-radius: 40rpx;
  font-size: 28rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
}

.filter-bar {
  display: flex;
  flex-wrap: wrap;
  margin-bottom: 30rpx;
}

.filter-item {
  padding: 12rpx 24rpx;
  background: #fff;
  border-radius: 30rpx;
  margin-right: 20rpx;
  margin-bottom: 16rpx;
  font-size: 28rpx;
  color: #666;
}

.filter-item.active {
  background: #1890ff;
  color: #fff;
}

.user-list {
  background: #fff;
  border-radius: 16rpx;
  padding: 20rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
}

.empty-tip {
  text-align: center;
  padding: 40rpx;
  color: #999;
  font-size: 28rpx;
}

.user-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx;
  border-bottom: 1rpx solid #eee;
}

.user-item:last-child {
  border-bottom: none;
}

.user-info {
  flex: 1;
}

.user-name {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 8rpx;
}

.user-dpname{
	font-size: 20rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 8rpx;
	float: right;
}

.user-id{
	font-size: 20rpx;
	color: #333;
	margin-bottom: 8rpx;
	float: initial;
}

.user-role {
  display: inline-block;
  padding: 4rpx 16rpx;
  border-radius: 20rpx;
  font-size: 24rpx;
  margin-right: 16rpx;
}

.role-student {
  background: #e6f7ff;
  color: #1890ff;
}

.role-teacher {
  background: #f6ffed;
  color: #52c41a;
}

.role-admin {
  background: #fff7e6;
  color: #fa8c16;
}

.user-username {
  font-size: 26rpx;
  color: #666;
}

.arrow {
  color: #999;
  font-size: 32rpx;
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 999;
  visibility: hidden;
  opacity: 0;
  transition: all 0.3s ease-in-out;
}

.modal-show {
  visibility: visible;
  opacity: 1;
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
  width: 80vw;
  max-width: 600rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.15);
}

.modal-header {
  padding: 30rpx;
  border-bottom: 1rpx solid #eee;
}

.modal-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.modal-body {
  padding: 30rpx;
}

.form-item {
  margin-bottom: 20rpx;
}

.form-item .label {
  display: block;
  font-size: 28rpx;
  color: #666;
  margin-bottom: 10rpx;
}

.form-item input {
  width: 100%;
  height: 80rpx;
  background: #f5f7fa;
  border-radius: 8rpx;
  padding: 0 20rpx;
  font-size: 28rpx;
}

.picker-value {
  width: 100%;
  height: 80rpx;
  background: #f5f7fa;
  border-radius: 8rpx;
  padding: 0 20rpx;
  font-size: 28rpx;
  line-height: 80rpx;
}

.modal-footer {
  padding: 20rpx 30rpx;
  border-top: 1rpx solid #eee;
  display: flex;
  justify-content: flex-end;
  gap: 20rpx;
}

.delete-btn, .cancel-btn, .confirm-btn {
  padding: 16rpx 32rpx;
  border-radius: 8rpx;
  font-size: 28rpx;
}

.delete-btn {
  background: #fff1f0;
  color: #f5222d;
}

.cancel-btn {
  background: #f5f7fa;
  color: #666;
}

.confirm-btn {
  background: #1890ff;
  color: #fff;
}

/* 分页样式 */
.pagination {
  margin-top: 30rpx;
  padding: 20rpx;
  background: #fff;
  border-radius: 12rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-info {
  font-size: 28rpx;
  color: #666;
}

.page-info text {
  margin-right: 20rpx;
}

.page-buttons {
  display: flex;
  gap: 20rpx;
}

.page-btn {
  font-size: 28rpx;
  padding: 10rpx 30rpx;
  background: #1890ff;
  color: #fff;
  border-radius: 8rpx;
}

.page-btn:disabled {
  background: #ccc;
  color: #fff;
}
</style> 