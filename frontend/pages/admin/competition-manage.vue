<template>
  <view class="container">
    <view class="header">
      <text class="title">竞赛管理</text>
      <view class="search-bar">
        <input type="text" v-model="searchKeyword" placeholder="搜索竞赛名称" @input="onSearch" />
        <text class="search-icon">🔍</text>
      </view>
    </view>

    <view class="filter-bar">
      <view class="filter-item" :class="{ active: currentFilter === 'all' }" @click="setFilter('all')">全部</view>
      <view class="filter-item" :class="{ active: currentFilter === 'REGISTERING' }" @click="setFilter('REGISTERING')">报名中</view>
      <view class="filter-item" :class="{ active: currentFilter === 'ONGOING' }" @click="setFilter('ONGOING')">进行中</view>
      <view class="filter-item" :class="{ active: currentFilter === 'UPCOMING' }" @click="setFilter('UPCOMING')">即将开始</view>
      <view class="filter-item" :class="{ active: currentFilter === 'ENDED' }" @click="setFilter('ENDED')">已结束</view>
    </view>
    
    <view class="action-btns">
      <button class="add-btn" @click="handleAddCompetition">添加竞赛</button>
    </view>

    <view class="competition-list">
      <view class="empty-tip" v-if="filteredCompetitions.length === 0 && !isLoading">
        <text>暂无符合条件的竞赛</text>
      </view>
      
      <view class="competition-item" v-for="(item, index) in filteredCompetitions" :key="index">
        <view class="competition-header">
          <image :src="getImageUrl(item.coverImage)" mode="aspectFill" class="competition-image"></image>
          <view class="competition-info">
            <text class="competition-name">{{item.name}}</text>
            <text class="competition-desc">{{item.description}}</text>
            <view class="competition-status" :class="getStatusClass(item.status)">{{getStatusText(item.status)}}</view>
          </view>
        </view>
        
        <view class="competition-body">
          <view class="info-row">
            <view class="info-item">
              <text class="info-label">报名开始：</text>
              <text class="info-value">{{formatDate(item.registrationStart)}}</text>
            </view>
            <view class="info-item">
              <text class="info-label">报名截止：</text>
              <text class="info-value">{{formatDate(item.registrationDeadline)}}</text>
            </view>
          </view>
          <view class="info-row">
            <view class="info-item">
              <text class="info-label">开始时间：</text>
              <text class="info-value">{{formatDate(item.startTime)}}</text>
            </view>
            <view class="info-item">
              <text class="info-label">结束时间：</text>
              <text class="info-value">{{formatDate(item.endTime)}}</text>
            </view>
          </view>
          <view class="info-row">
            <view class="info-item">
              <text class="info-label">参与人数：</text>
              <text class="info-value">{{item.currentParticipants}}/{{item.maxParticipants}}</text>
            </view>
          </view>
        </view>
        
        <view class="competition-footer">
          <button class="action-btn view-btn" @click="handleViewCompetition(item)">查看</button>
          <button class="action-btn edit-btn" @click="handleEditCompetition(item)">编辑</button>
          <button class="action-btn delete-btn" @click="handleDeleteCompetition(item)">删除</button>
        </view>
      </view>
    </view>
    
    <view class="load-more" v-if="hasMore && !isLoading" @click="loadMore">
      <text>加载更多</text>
    </view>
    
    <view class="loading-state" v-if="isLoading">
      <text>加载中...</text>
    </view>

    <!-- 添加/编辑竞赛弹窗 -->
    <view class="modal" v-if="showModal">
      <view class="modal-mask" @click="closeModal"></view>
      <view class="modal-content">
        <view class="modal-header">
          <text class="modal-title">{{ isEdit ? '编辑竞赛' : '添加竞赛' }}</text>
        </view>
        <view class="modal-body">
          <view class="form-item">
            <text class="label">竞赛名称</text>
            <input type="text" v-model="form.title" placeholder="请输入竞赛名称" />
          </view>
          <view class="form-item">
            <text class="label">竞赛类型</text>
            <input type="text" v-model="form.type" placeholder="请输入竞赛类型" />
          </view>
          <view class="form-item">
            <text class="label">主办单位</text>
            <input type="text" v-model="form.hostUnit" placeholder="请输入主办单位" />
          </view>
          <view class="form-item">
            <text class="label">竞赛级别</text>
            <input type="text" v-model="form.level" placeholder="请输入竞赛级别" />
          </view>
          <view class="form-item">
            <text class="label">开始时间</text>
            <input type="text" v-model="form.startTime" placeholder="请输入开始时间" />
          </view>
          <view class="form-item">
            <text class="label">结束时间</text>
            <input type="text" v-model="form.endTime" placeholder="请输入结束时间" />
          </view>
          <view class="form-item">
            <text class="label">竞赛描述</text>
            <textarea v-model="form.description" placeholder="请输入竞赛描述" class="content-textarea" />
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
import request from '@/utils/request'

export default {
  data() {
    return {
      competitions: [],
      searchKeyword: '',
      currentFilter: 'all',
      page: 1,
      size: 10,
      hasMore: true,
      isLoading: false,
      loadError: false,
      isEdit: false,
      showModal: false,
      form: {
        competitionId: null,
        title: '',
        type: '',
        hostUnit: '',
        level: '',
        startTime: '',
        endTime: '',
        description: ''
      }
    }
  },
  computed: {
    ...mapGetters(['isLoggedIn', 'userRole']),
    
    filteredCompetitions() {
      let result = this.competitions;
      
      // 关键词搜索
      if (this.searchKeyword && this.searchKeyword.trim() !== '') {
        const keyword = this.searchKeyword.trim().toLowerCase();
        result = result.filter(item => 
          (item.name && item.name.toLowerCase().includes(keyword)) || 
          (item.description && item.description.toLowerCase().includes(keyword))
        );
      }
      
      // 状态筛选
      if (this.currentFilter !== 'all') {
        result = result.filter(item => item.status === this.currentFilter);
      }
      
      return result;
    }
  },
  onLoad() {
    this.checkAdminPermission();
    this.loadCompetitions();
  },
  onPullDownRefresh() {
    this.refreshList();
  },
  methods: {
    // 检查管理员权限
    checkAdminPermission() {
      if (!this.isLoggedIn) {
        uni.showToast({
          title: '请先登录',
          icon: 'none'
        });
        setTimeout(() => {
          uni.navigateTo({
            url: '/pages/user/login'
          });
        }, 1500);
        return;
      }
      
      if (this.userRole !== 'ADMIN') {
        uni.showToast({
          title: '您没有管理员权限',
          icon: 'none'
        });
        setTimeout(() => {
          uni.navigateBack();
        }, 1500);
      }
    },
    
    // 加载竞赛列表
    async loadCompetitions() {
      if (this.isLoading) return;
      
      this.isLoading = true;
      
      try {
        uni.showLoading({
          title: '加载中...'
        });
        
        // 构建请求参数
        let params = {
          page: this.page - 1,
          size: this.size
        };
        
        console.log('请求参数:', params);
        
        // TODO: 修改为真实API
        // const response = await request.get('/api/admin/competitions', { params });
        
        // 模拟数据
        const mockResponse = {
          code: 200,
          message: '操作成功',
          data: this.getMockData()
        };
        
        const response = mockResponse;
        console.log('竞赛列表响应:', response);
        
        let competitionsData = [];
        if (response && response.code === 200) {
          competitionsData = response.data || [];
        } else if (Array.isArray(response)) {
          competitionsData = response;
        } else {
          throw new Error('响应数据格式错误');
        }
        
        // 更新数据
        if (this.page === 1) {
          this.competitions = competitionsData;
        } else {
          this.competitions = [...this.competitions, ...competitionsData];
        }
        
        this.hasMore = competitionsData.length >= this.size;
      } catch (error) {
        console.error('获取竞赛列表失败:', error);
        uni.showToast({
          title: '获取竞赛列表失败',
          icon: 'none'
        });
      } finally {
        this.isLoading = false;
        uni.hideLoading();
        uni.stopPullDownRefresh();
      }
    },
    
    // 刷新列表
    refreshList() {
      this.page = 1;
      this.competitions = [];
      this.hasMore = true;
      this.loadCompetitions();
    },
    
    // 设置过滤条件
    setFilter(filter) {
      if (this.currentFilter === filter) return;
      
      this.currentFilter = filter;
      console.log('设置过滤条件:', filter);
    },
    
    // 搜索
    onSearch() {
      console.log('搜索关键词:', this.searchKeyword);
    },
    
    // 加载更多
    loadMore() {
      if (this.hasMore && !this.isLoading) {
        this.page++;
        this.loadCompetitions();
      }
    },
    
    // 添加竞赛
    handleAddCompetition() {
      this.isEdit = false
      this.form = {
        competitionId: null,
        title: '',
        type: '',
        hostUnit: '',
        level: '',
        startTime: '',
        endTime: '',
        description: ''
      }
      this.showModal = true
    },
    
    // 查看竞赛
    handleViewCompetition(item) {
      uni.navigateTo({
        url: `/pages/competition/detail?id=${item.id}`
      });
    },
    
    // 编辑竞赛
    handleEditCompetition(item) {
      this.isEdit = true
      this.form = { ...item }
      this.showModal = true
    },
    
    // 删除竞赛
    async handleDeleteCompetition(item) {
      const [confirmRes] = await uni.showModal({
        title: '确认删除',
        content: `确定要删除"${item.name}"竞赛吗？此操作不可撤销！`,
        confirmText: '删除',
        confirmColor: '#ff4d4f'
      });
      
      if (confirmRes.confirm) {
        try {
          uni.showLoading({
            title: '删除中...'
          });
          
          // TODO: 替换为真实的删除API
          // await request.delete(`/api/admin/competitions/${item.id}`);
          
          // 模拟删除
          setTimeout(() => {
            // 从列表中移除该竞赛
            this.competitions = this.competitions.filter(comp => comp.id !== item.id);
            
            uni.showToast({
              title: '删除成功',
              icon: 'success'
            });
            
            uni.hideLoading();
          }, 1000);
        } catch (error) {
          console.error('删除竞赛失败:', error);
          uni.showToast({
            title: '删除失败',
            icon: 'none'
          });
          uni.hideLoading();
        }
      }
    },
    
    // 图片路径处理
    getImageUrl(path) {
      if (!path) {
        return '/static/images/competition-default.jpg';
      }
      
      // 如果是相对路径且不以斜杠开头，添加斜杠
      if (!path.startsWith('/') && !path.startsWith('http')) {
        path = '/' + path;
      }
      
      return path;
    },
    
    // 获取状态文本
    getStatusText(status) {
      const statusMap = {
        'REGISTERING': '报名中',
        'ONGOING': '进行中',
        'UPCOMING': '即将开始',
        'ENDED': '已结束'
      };
      return statusMap[status] || '未知状态';
    },
    
    // 获取状态样式类
    getStatusClass(status) {
      const classMap = {
        'REGISTERING': 'registering',
        'ONGOING': 'ongoing',
        'UPCOMING': 'upcoming',
        'ENDED': 'ended'
      };
      return classMap[status] || '';
    },
    
    // 日期格式化
    formatDate(dateString) {
      if (!dateString) return '未设置';
      
      try {
        const date = new Date(dateString);
        if (isNaN(date.getTime())) return '日期错误';
        
        return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
      } catch (e) {
        console.error('日期格式化错误:', e);
        return '日期错误';
      }
    },
    
    // 获取模拟数据
    getMockData() {
      return [
        {
          id: 1,
          name: '2024年全国大学生创新创业大赛',
          description: '激发创新思维，培养创业能力',
          coverImage: '/static/images/competition/1.jpg',
          registrationStart: '2024-04-01',
          registrationDeadline: '2024-05-01',
          startTime: '2024-05-15',
          endTime: '2024-06-15',
          currentParticipants: 150,
          maxParticipants: 200,
          status: 'REGISTERING'
        },
        {
          id: 2,
          name: '互联网+创新创业大赛',
          description: '推动互联网与各行业深度融合',
          coverImage: '/static/images/competition/2.jpg',
          registrationStart: '2024-03-15',
          registrationDeadline: '2024-04-15',
          startTime: '2024-05-01',
          endTime: '2024-06-01',
          currentParticipants: 80,
          maxParticipants: 100,
          status: 'ONGOING'
        },
        {
          id: 3,
          name: '人工智能应用大赛',
          description: '探索AI技术在各领域的创新应用',
          coverImage: '/static/images/competition/3.jpg',
          registrationStart: '2024-05-01',
          registrationDeadline: '2024-06-01',
          startTime: '2024-06-15',
          endTime: '2024-07-15',
          currentParticipants: 50,
          maxParticipants: 150,
          status: 'UPCOMING'
        },
        {
          id: 4,
          name: '2023年程序设计大赛',
          description: '年度程序设计挑战赛，考验编程能力',
          coverImage: '/static/images/competition/4.jpg',
          registrationStart: '2023-09-01',
          registrationDeadline: '2023-10-01',
          startTime: '2023-10-15',
          endTime: '2023-11-15',
          currentParticipants: 200,
          maxParticipants: 200,
          status: 'ENDED'
        }
      ];
    },

    // 关闭弹窗
    closeModal() {
      this.showModal = false
    },

    // 处理表单提交
    handleSubmit() {
      // 在这里处理表单提交逻辑
      console.log('表单数据:', this.form);
      this.closeModal();
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
}

.search-bar input {
  width: 100%;
  height: 80rpx;
  background: #fff;
  border-radius: 40rpx;
  padding: 0 80rpx 0 30rpx;
  font-size: 28rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
}

.search-icon {
  position: absolute;
  right: 30rpx;
  top: 20rpx;
  font-size: 32rpx;
  color: #999;
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

.action-btns {
  margin-bottom: 30rpx;
}

.add-btn {
  background: #1890ff;
  color: #fff;
  font-size: 28rpx;
  height: 80rpx;
  line-height: 80rpx;
  border-radius: 40rpx;
}

.competition-list {
  margin-bottom: 30rpx;
}

.competition-item {
  background: #fff;
  border-radius: 16rpx;
  margin-bottom: 30rpx;
  overflow: hidden;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
}

.competition-header {
  display: flex;
  padding: 20rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.competition-image {
  width: 160rpx;
  height: 160rpx;
  border-radius: 8rpx;
  margin-right: 20rpx;
}

.competition-info {
  flex: 1;
  position: relative;
}

.competition-name {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 10rpx;
}

.competition-desc {
  font-size: 26rpx;
  color: #666;
  margin-bottom: 10rpx;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}

.competition-status {
  position: absolute;
  top: 0;
  right: 0;
  padding: 6rpx 16rpx;
  font-size: 24rpx;
  border-radius: 20rpx;
}

.competition-status.registering {
  background: #e6f7ff;
  color: #1890ff;
}

.competition-status.ongoing {
  background: #f0f5ff;
  color: #597ef7;
}

.competition-status.upcoming {
  background: #f6ffed;
  color: #52c41a;
}

.competition-status.ended {
  background: #f5f5f5;
  color: #999;
}

.competition-body {
  padding: 20rpx;
}

.info-row {
  display: flex;
  margin-bottom: 16rpx;
}

.info-item {
  flex: 1;
  display: flex;
}

.info-label {
  color: #999;
  font-size: 26rpx;
  min-width: 140rpx;
}

.info-value {
  color: #333;
  font-size: 26rpx;
}

.competition-footer {
  display: flex;
  padding: 20rpx;
  border-top: 1rpx solid #f0f0f0;
}

.action-btn {
  flex: 1;
  height: 70rpx;
  line-height: 70rpx;
  margin: 0 10rpx;
  font-size: 28rpx;
  border-radius: 35rpx;
}

.view-btn {
  background: #e6f7ff;
  color: #1890ff;
  border: 1rpx solid #91d5ff;
}

.edit-btn {
  background: #f0f5ff;
  color: #597ef7;
  border: 1rpx solid #adc6ff;
}

.delete-btn {
  background: #fff1f0;
  color: #ff4d4f;
  border: 1rpx solid #ffa39e;
}

.empty-tip {
  text-align: center;
  padding: 100rpx 0;
  color: #999;
}

.load-more {
  text-align: center;
  padding: 30rpx 0;
  color: #999;
  font-size: 28rpx;
}

.loading-state {
  text-align: center;
  padding: 30rpx 0;
  color: #999;
}

/* 弹窗样式 */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 999;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
}

.modal-content {
  position: relative;
  background: #fff;
  border-radius: 16rpx;
  width: 600rpx;
  padding: 30rpx;
  max-height: 90vh;
  overflow-y: auto;
  z-index: 1000;
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
  height: 200rpx;
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