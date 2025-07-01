<template>
  <view class="container">
    <view class="header">
      <text class="title">竞赛管理</text>
      <button class="add-btn" @click="showAddModal">添加竞赛</button>
    </view>

    <view class="filter-section">
      <view class="search-box">
        <input 
          type="text" 
          v-model="searchQuery" 
          placeholder="搜索竞赛标题" 
          @input="handleSearch"
          class="search-input"
        />
      </view>
      <view class="filter-item">
        <text class="label">竞赛状态：</text>
        <picker :range="statusOptions" :value="statusIndex" @change="handleStatusChange">
          <view class="picker-value">{{ statusOptions[statusIndex] }}</view>
        </picker>
      </view>
    </view>

    <view class="competition-list">
      <view v-if="filteredCompetitions.length === 0" class="empty-tip">
        <text>暂无竞赛记录</text>
      </view>
      <view v-else class="competition-item" v-for="(competition, index) in filteredCompetitions" :key="index">
        <view class="competition-info">
          <text class="competition-title">{{ competition.title }}</text>
          <text class="competition-status" :class="getStatusClass(competition.status)">
            {{ getStatusText(competition.status) }}
          </text>
		  <view>
			<view>
			  <text class="competition-time">报名开始时间：{{ formatDate(competition.signupStartTime) }}</text>
			</view>
			
			<view>
			  <text class="competition-time">报名截止时间：{{ formatDate(competition.signupEndTime) }}</text>
			</view>
			<view>
			  <text class="competition-time">竞赛开始时间：{{ formatDate(competition.competitionStartTime) }}</text>
		    </view>
			<view>
			  <text class="competition-time">竞赛结束时间：{{ formatDate(competition.competitionEndTime) }}</text>
			</view>
		  </view>
		</view>
        <view class="action-buttons">
          <button class="action-btn edit" @click="showEditModal(competition)">编辑</button>
          <button class="action-btn delete" @click="handleDelete(competition)">删除</button>
        </view>
      </view>
    </view>

    <!-- 分页组件 -->
    <view class="pagination" v-if="totalPages > 1">
      <view class="pagination-info">
        共 {{ total }} 条记录，当前第 {{ currentPage }}/{{ totalPages }} 页
      </view>
      <view class="pagination-buttons">
        <button 
          class="page-btn" 
          :disabled="currentPage === 1"
          @click="handlePageChange(currentPage - 1)"
        ><text class="btn-text"><</text></button>
        
        <view class="page-numbers">
          <button 
            v-for="page in displayPages" 
            :key="page"
            class="page-number-btn"
            :class="{ active: currentPage === page }"
            @click="handlePageChange(page)"
          >{{ page }}</button>
        </view>

        <button 
          class="page-btn" 
          :disabled="currentPage === totalPages"
          @click="handlePageChange(currentPage + 1)"
        ><text class="btn-text">></text></button>
      </view>
    </view>

    <!-- 添加/编辑竞赛弹窗 -->
    <view v-if="currentCompetition" class="modal" :class="{ 'modal-show': showModal }">
      <view class="modal-mask" @click="closeModal"></view>
      <view class="modal-content">
        <view class="modal-header">
          <text class="modal-title">{{ isEditing ? '编辑竞赛' : '添加竞赛' }}</text>
        </view>
        <view class="modal-body">
          <view class="form-item">
            <text class="label">竞赛标题：</text>
            <input type="text" v-model="currentCompetition.title" placeholder="请输入竞赛标题" />
          </view>
          <view class="form-item">
            <text class="label">竞赛描述：</text>
            <textarea v-model="currentCompetition.description" placeholder="请输入竞赛描述"  maxlength="5000"/>
          </view>
          <view class="form-item">
            <text class="label">主办单位：</text>
            <picker :range="hostUnits" range-key="name" :value="hostUnitIndex" @change="handleHostUnitChange">
              <view class="picker-value">{{ hostUnits[hostUnitIndex] ? hostUnits[hostUnitIndex].name : '请选择主办单位' }}</view>
            </picker>
          </view>
          <view class="form-item">
            <text class="label">责任院系：</text>
            <picker :range="departments" range-key="name" :value="departmentIndex" @change="handleDepartmentChange">
              <view class="picker-value">{{ departments[departmentIndex] ? departments[departmentIndex].name : '请选择责任院系' }}</view>
            </picker>
          </view>
          <view class="form-item">
            <text class="label">竞赛等级：</text>
            <picker :range="levels" range-key="levelName" :value="levelIndex" @change="handleLevelChange">
              <view class="picker-value">{{ levels[levelIndex] ? levels[levelIndex].levelName : '请选择竞赛等级' }}</view>
            </picker>
          </view>
          <view class="form-item">
            <text class="label">报名时间：</text>
            <view class="date-range">
              <picker mode="date" :value="signupStartDate" @change="handleSignupStartDateChange">
                <view class="picker-value">{{ signupStartDate || '开始日期' }}</view>
              </picker>
              <text>至</text>
              <picker mode="date" :value="signupEndDate" @change="handleSignupEndDateChange">
                <view class="picker-value">{{ signupEndDate || '结束日期' }}</view>
              </picker>
            </view>
          </view>
          <view class="form-item">
            <text class="label">竞赛时间：</text>
            <view class="date-range">
              <picker mode="date" :value="competitionStartDate" @change="handleCompetitionStartDateChange">
                <view class="picker-value">{{ competitionStartDate || '开始日期' }}</view>
              </picker>
              <text>至</text>
              <picker mode="date" :value="competitionEndDate" @change="handleCompetitionEndDateChange">
                <view class="picker-value">{{ competitionEndDate || '结束日期' }}</view>
              </picker>
            </view>
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
export default {
  data() {
    return {
      competitions: [],
      allCompetitions: [], // 存储所有竞赛数据
      searchQuery: '',
      statusOptions: ['全部', '待开始','报名中', '进行中', '已结束'],
      statusIndex: 0,
      isEditing: false,
      currentCompetition: {
        title: '',
        description: '',
        hostUnitId: null,
        responsibleDepartmentId: null,
        levelId: null,
        signupStartTime: '',
        signupEndTime: '',
        competitionStartTime: '',
        competitionEndTime: ''
      },
      hostUnits: [],
      departments: [],
      levels: [],
      hostUnitIndex: 0,
      departmentIndex: 0,
      levelIndex: 0,
      signupStartDate: '',
      signupEndDate: '',
      competitionStartDate: '',
      competitionEndDate: '',
      // 分页相关数据
      currentPage: 1,
      pageSize: 2,
      total: 0,
      totalPages: 0,
      showModal: false
    }
  },
  computed: {
    filteredCompetitions() {
      if (!this.allCompetitions) return []
      
      let filtered = [...this.allCompetitions]
      
      // 按状态筛选
      if (this.statusIndex !== 0) {
        const statusMap = {
          1: 'pending',
		  2: 'available',
          3: 'active',
          4: 'ended'
        }
        filtered = filtered.filter(competition => 
          competition.status === statusMap[this.statusIndex]
        )
      }
      
      // 按标题搜索
      if (this.searchQuery) {
        const keyword = this.searchQuery.toLowerCase()
        filtered = filtered.filter(competition => 
          competition.title.toLowerCase().includes(keyword)
        )
      }

      // 更新总数和总页数
      this.total = filtered.length
      this.totalPages = Math.ceil(this.total / this.pageSize)
      
      // 计算当前页的数据
      const start = (this.currentPage - 1) * this.pageSize
      const end = start + this.pageSize
      return filtered.slice(start, end)
    },
    displayPages() {
      const pages = []
      const maxVisiblePages = 5
      
      if (this.totalPages <= maxVisiblePages) {
        // 如果总页数小于等于最大显示页数，显示所有页码
        for (let i = 1; i <= this.totalPages; i++) {
          pages.push(i)
        }
      } else {
        // 如果总页数大于最大显示页数，显示部分页码
        let start = Math.max(1, this.currentPage - Math.floor(maxVisiblePages / 2))
        let end = start + maxVisiblePages - 1
        
        if (end > this.totalPages) {
          end = this.totalPages
          start = Math.max(1, end - maxVisiblePages + 1)
        }
        
        for (let i = start; i <= end; i++) {
          pages.push(i)
        }
      }
      
      return pages
    }
  },
  onLoad() {
    this.loadCompetitions()
    this.loadHostUnits()
    this.loadDepartments()
    this.loadLevels()
  },
  methods: {
    async loadCompetitions() {
      try {
        const token = uni.getStorageSync('token')
        const [error, res] = await uni.request({
          url: 'http://localhost:8080/api/competitions/findAll',
          method: 'GET',
          header: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        })

        if (error) {
          throw new Error(error.errMsg)
        }

        if (res.data.code === 200) {
          this.allCompetitions = res.data.data.list
          this.total = this.allCompetitions.length
          this.totalPages = Math.ceil(this.total / this.pageSize)
        } else {
          throw new Error(res.data.message)
        }
      } catch (error) {
        console.error('Error loading competitions:', error)
        uni.showToast({
          title: error.message || '加载竞赛列表失败',
          icon: 'none'
        })
      }
    },

    async loadHostUnits() {
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
          this.hostUnits = res.data.data
        }
      } catch (error) {
        console.error('Error loading host units:', error)
      }
    },

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
        }
      } catch (error) {
        console.error('Error loading departments:', error)
      }
    },

    async loadLevels() {
      try {
        const token = uni.getStorageSync('token')
        const [error, res] = await uni.request({
          url: 'http://localhost:8080/api/competition-levels/findAll',
          method: 'GET',
          header: {
            'Authorization': `Bearer ${token}`
          }
        })

        if (error) {
          throw new Error(error.errMsg)
        }

        if (res.data.code === 200) {
          this.levels = res.data.data
          console.log('竞赛等级数据：', this.levels)
        }
      } catch (error) {
        console.error('Error loading levels:', error)
      }
    },

    handleStatusChange(e) {
      this.statusIndex = e.detail.value
      this.currentPage = 1 // 切换状态时重置到第一页
    },

    handleSearch() {
      this.currentPage = 1 // 搜索时重置到第一页
    },

    handlePageChange(page) {
      this.currentPage = page
    },

    showAddModal() {
      this.isEditing = false
      this.resetForm()
      this.showModal = true
    },

    showEditModal(competition) {
      this.isEditing = true
      this.currentCompetition = { ...competition }
      this.hostUnitIndex = this.hostUnits.findIndex(unit => unit.hostUnitId === competition.hostUnitId)
      this.departmentIndex = this.departments.findIndex(dept => dept.departmentId === competition.responsibleDepartmentId)
      this.levelIndex = this.levels.findIndex(level => level.levelId === competition.levelId)
      this.signupStartDate = this.formatDateForPicker(competition.signupStartTime)
      this.signupEndDate = this.formatDateForPicker(competition.signupEndTime)
      this.competitionStartDate = this.formatDateForPicker(competition.competitionStartTime)
      this.competitionEndDate = this.formatDateForPicker(competition.competitionEndTime)
      this.showModal = true
    },

    closeModal() {
      this.showModal = false
    },

    resetForm() {
      this.currentCompetition = {
        title: '',
        description: '',
        hostUnitId: null,
        responsibleDepartmentId: null,
        levelId: null,
        signupStartTime: '',
        signupEndTime: '',
        competitionStartTime: '',
        competitionEndTime: ''
      }
      this.hostUnitIndex = 0
      this.departmentIndex = 0
      this.levelIndex = 0
      this.signupStartDate = ''
      this.signupEndDate = ''
      this.competitionStartDate = ''
      this.competitionEndDate = ''
    },

    handleHostUnitChange(e) {
      this.hostUnitIndex = e.detail.value
      if (this.hostUnits[this.hostUnitIndex]) {
        this.currentCompetition.hostUnitId = this.hostUnits[this.hostUnitIndex].hostUnitId
        console.log('选择的主办单位ID：', this.currentCompetition.hostUnitId)
      }
    },

    handleDepartmentChange(e) {
      this.departmentIndex = e.detail.value
      if (this.departments[this.departmentIndex]) {
        this.currentCompetition.responsibleDepartmentId = this.departments[this.departmentIndex].departmentId
        console.log('选择的责任院系ID：', this.currentCompetition.responsibleDepartmentId)
      }
    },

    handleLevelChange(e) {
      this.levelIndex = e.detail.value
      if (this.levels[this.levelIndex]) {
        this.currentCompetition.levelId = this.levels[this.levelIndex].levelId
        console.log('选择的竞赛等级ID：', this.currentCompetition.levelId)
      }
    },

    handleSignupStartDateChange(e) {
      this.signupStartDate = e.detail.value
      this.currentCompetition.signupStartTime = this.formatDateTime(this.signupStartDate, '00:00:00')
    },

    handleSignupEndDateChange(e) {
      this.signupEndDate = e.detail.value
      this.currentCompetition.signupEndTime = this.formatDateTime(this.signupEndDate, '23:59:59')
    },

    handleCompetitionStartDateChange(e) {
      this.competitionStartDate = e.detail.value
      this.currentCompetition.competitionStartTime = this.formatDateTime(this.competitionStartDate, '00:00:00')
    },

    handleCompetitionEndDateChange(e) {
      this.competitionEndDate = e.detail.value
      this.currentCompetition.competitionEndTime = this.formatDateTime(this.competitionEndDate, '23:59:59')
    },

    formatDateTime(dateStr, timeStr) {
      if (!dateStr) return ''
      return `${dateStr}T${timeStr}`
    },

    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
    },

    formatDateForPicker(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
    },

    async handleSubmit() {
      try {
        // 验证必填字段
        if (!this.currentCompetition.title) {
          uni.showToast({
            title: '请输入竞赛标题',
            icon: 'none'
          })
          return
        }
        if (!this.currentCompetition.hostUnitId) {
          uni.showToast({
            title: '请选择主办单位',
            icon: 'none'
          })
          return
        }
        if (!this.currentCompetition.responsibleDepartmentId) {
          uni.showToast({
            title: '请选择责任院系',
            icon: 'none'
          })
          return
        }
        if (!this.currentCompetition.levelId) {
          uni.showToast({
            title: '请选择竞赛等级',
            icon: 'none'
          })
          return
        }
        if (!this.currentCompetition.signupStartTime || !this.currentCompetition.signupEndTime) {
          uni.showToast({
            title: '请选择报名时间',
            icon: 'none'
          })
          return
        }
        if (!this.currentCompetition.competitionStartTime || !this.currentCompetition.competitionEndTime) {
          uni.showToast({
            title: '请选择竞赛时间',
            icon: 'none'
          })
          return
        }

        const token = uni.getStorageSync('token')
        if (!token) {
          uni.showToast({
            title: '请先登录',
            icon: 'none'
          })
          return
        }

        console.log('提交的竞赛数据：', this.currentCompetition)

        const url = this.isEditing
          ? `http://localhost:8080/api/competitions/${this.currentCompetition.competitionId}`
          : 'http://localhost:8080/api/competitions'
        
        const [error, res] = await uni.request({
          url: url,
          method: this.isEditing ? 'PUT' : 'POST',
          header: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          },
          data: this.currentCompetition
        })

        if (error) {
          throw new Error(error.errMsg)
        }

        if (res.data.code === 200) {
          uni.showToast({
            title: this.isEditing ? '更新成功' : '添加成功',
            icon: 'success'
          })
          this.closeModal()
          this.loadCompetitions()
        } else {
          throw new Error(res.data.message)
        }
      } catch (error) {
        console.error('Error submitting competition:', error)
        uni.showToast({
          title: error.message || (this.isEditing ? '更新失败' : '添加失败'),
          icon: 'none'
        })
      }
    },

    async handleDelete(competition) {
      try {
        const [confirmError, confirmRes] = await uni.showModal({
          title: '确认删除',
          content: '确定要删除这个竞赛吗？',
          confirmText: '确定',
          cancelText: '取消'
        })

        if (confirmError || !confirmRes.confirm) {
          return
        }

        const token = uni.getStorageSync('token')
        const [error, res] = await uni.request({
          url: `http://localhost:8080/api/competitions/${competition.competitionId}`,
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
          this.loadCompetitions()
        } else {
          throw new Error(res.data.message)
        }
      } catch (error) {
        console.error('Error deleting competition:', error)
        uni.showToast({
          title: error.message || '删除失败',
          icon: 'none'
        })
      }
    },

    getStatusText(status) {
      const statusMap = {
        'pending': '待开始',
		'available': '报名中',
        'active': '进行中',
        'ended': '已结束'
      }
      return statusMap[status] || status
    },

    getStatusClass(status) {
      const classMap = {
        'pending': 'status-pending',
		'available': 'status-available',
        'active': 'status-active',
        'ended': 'status-ended'
      }
      return classMap[status] || ''
    },

    async handleUploadScore(file) {
      try {
        // 检查文件大小（10MB = 10 * 1024 * 1024 bytes）
        if (file.size > 10 * 1024 * 1024) {
          uni.showToast({
            title: '文件大小不能超过10MB',
            icon: 'none'
          })
          return
        }

        const token = uni.getStorageSync('token')
        const [error, res] = await uni.uploadFile({
          url: 'http://localhost:8080/api/upload/score',
          filePath: file.path,
          name: 'file',
          header: {
            'Authorization': `Bearer ${token}`
          }
        })

        if (error) {
          throw new Error(error.errMsg)
        }

        if (res.data.code === 200) {
          uni.showToast({
            title: '上传成功',
            icon: 'success'
          })
        } else {
          throw new Error(res.data.message)
        }
      } catch (error) {
        console.error('Error uploading score:', error)
        uni.showToast({
          title: error.message || '上传失败',
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
  background-color: #f5f5f5;
  min-height: 100vh;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.title {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.add-btn {
  background-color: #007AFF;
  color: #fff;
  padding: 8px 16px;
  border-radius: 4px;
  font-size: 14px;
}

.filter-section {
  background-color: #fff;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.search-box {
  margin-bottom: 10px;
}

.search-input {
  width: 100%;
  background: #f5f7fa;
  border-radius: 8px;
  padding: 10px;
  font-size: 14px;
}

.filter-item {
  display: flex;
  align-items: center;
}

.label {
  font-size: 14px;
  color: #666;
  margin-right: 10px;
}

.picker-value {
  font-size: 14px;
  color: #333;
  padding: 4px 8px;
  background-color: #f5f5f5;
  border-radius: 4px;
}

.competition-list {
  background-color: #fff;
  border-radius: 8px;
  padding: 15px;
}

.empty-tip {
  text-align: center;
  padding: 20px;
  color: #999;
}

.competition-item {
  border-bottom: 1px solid #eee;
  padding: 15px 0;
}

.competition-item:last-child {
  border-bottom: none;
}

.competition-info {
  margin-bottom: 10px;
}

.competition-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.competition-status {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  margin-bottom: 5px;
}

.status-pending {
  background-color: #FFE58F;
  color: #D48806;
}

.status-available {
  background-color: #0055ff;
  color: #00ffff;
}

.status-active {
  background-color: #B7EB8F;
  color: #389E0D;
}

.status-ended {
  background-color: #D9D9D9;
  color: #595959;
}

.competition-time {
  font-size: 14px;
  color: #666;
  margin-bottom: 5px;
}

.action-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.action-btn {
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 14px;
}

.edit {
  background-color: #E6F7FF;
  color: #1890FF;
}

.delete {
  background-color: #FFF1F0;
  color: #F5222D;
}

/* 弹窗样式 */
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
  max-height: 70vh;
  overflow-y: auto;
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

.form-item input,
.form-item textarea {
  width: 100%;
  background: #f5f7fa;
  border-radius: 8rpx;
  padding: 20rpx;
  font-size: 28rpx;
}

.form-item textarea {
  height: 200rpx;
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

.date-range {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.date-range .picker-value {
  flex: 1;
}

.modal-footer {
  padding: 20rpx 30rpx;
  border-top: 1rpx solid #eee;
  display: flex;
  justify-content: flex-end;
  gap: 20rpx;
}

.cancel-btn, .confirm-btn {
  padding: 16rpx 32rpx;
  border-radius: 8rpx;
  font-size: 28rpx;
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
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 15px;
  background-color: #fff;
  border-radius: 8px;
  margin-top: 20px;
}

.pagination-info {
  font-size: 14px;
  color: #666;
  text-align: center;
}

.pagination-buttons {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
}

.page-btn {
  min-width: 60px;
  height: 32px;
  padding: 0 12px;
  border-radius: 4px;
  font-size: 14px;
  background-color: #f5f7fa;
  color: #666;
  display: flex;
  align-items: center;
  justify-content: center;
}

.page-btn:disabled {
  background-color: #f5f7fa;
  color: #999;
  cursor: not-allowed;
}

.page-numbers {
  display: flex;
  gap: 4px;
}

.page-number-btn {
  min-width: 32px;
  height: 32px;
  padding: 0 8px;
  border-radius: 4px;
  font-size: 14px;
  background-color: #f5f7fa;
  color: #666;
  display: flex;
  align-items: center;
  justify-content: center;
}

.page-number-btn.active {
  background-color: #1890ff;
  color: #fff;
}

.btn-text {
  font-size: 14px;
}
</style>