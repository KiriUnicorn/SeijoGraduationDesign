<template>
  <view class="container">
    <view class="competition-header">
      <image :src="competition.coverImage || '/static/images/competition-default.jpg'" mode="aspectFill" class="competition-cover"></image>
      <view class="competition-title">{{competition.name}}</view>
      <view class="competition-status" :class="competitionStatus">{{statusText}}</view>
    </view>

    <view class="competition-info">
		<view class="info-section">
		  <view class="section-title">竞赛名称</view>
		  <view class="section-content">{{competition.title}}</view>

		</view>
      <view class="info-section">
        <view class="section-title">介绍</view>
        <view class="section-content">{{competition.description}}</view>
      </view>

      <view class="info-section">
        <view class="section-title">相关信息</view>
        <view class="basic-info">
          <view class="info-item">
            <text class="info-label">主办单位：</text>
            <text class="info-value">{{competition.hostUnitName || '暂无'}}</text>
          </view>
          <view class="info-item">
            <text class="info-label">竞赛等级：</text>
            <text class="info-value">{{competition.levelName || '暂无'}}</text>
          </view>
          <view class="info-item">
            <text class="info-label">责任院系：</text>
            <text class="info-value">{{competition.departmentName || '暂无'}}</text>
          </view>
        </view>
      </view>

      <view class="info-section">
        <view class="section-title">时间安排</view>
        <view class="time-list">
			<view class="time-item">
			  <text class="time-label">发布时间：</text>
			  <text class="time-value">{{formatDate(competition.createdAt,'yyyy/MM/dd HH:mm:ss')}}</text>
			</view>
          <view class="time-item">
            <text class="time-label">报名开始：</text>
            <text class="time-value">{{formatDate(competition.signupStartTime,'yyyy/MM/dd hh:mm:ss')}}</text>
          </view>
          <view class="time-item">
            <text class="time-label">报名截止：</text>
            <text class="time-value">{{formatDate(competition.signupEndTime,'yyyy/MM/dd hh:mm:ss')}}</text>
          </view>
          <view class="time-item">
            <text class="time-label">竞赛开始：</text>
            <text class="time-value">{{formatDate(competition.competitionStartTime)}}</text>
          </view>
          <view class="time-item">
            <text class="time-label">竞赛结束：</text>
            <text class="time-value">{{formatDate(competition.competitionEndTime)}}</text>
          </view>
		  <view class="time-item">
		    <text class="time-label">目前状态：</text>
		    <text class="time-value">{{registerButtonText}}</text>
		  </view>
        </view>
      </view>

      <view class="info-section" v-if="requirementsList.length > 0">
        <view class="section-title">参赛要求</view>
        <view class="requirement-list">
          <view class="requirement-item" v-for="(item, index) in requirementsList" :key="index">
            {{index + 1}}. {{item}}
          </view>
        </view>
      </view>

      <view class="info-section" v-if="awardsList.length > 0">
        <view class="section-title">奖项设置</view>
        <view class="award-list">
          <view class="award-item" v-for="(item, index) in awardsList" :key="index">
            <text class="award-name">{{item.name}}</text>
            <text class="award-count">{{item.count}}名</text>
          </view>
        </view>
      </view>
    </view>

    <view class="action-bar">
      <view class="participant-count">
        <text>已报名：</text>
        <text class="count">{{competition.currentParticipants || 0}}/{{competition.maxParticipants || '无限制'}}</text>
      </view>
      <button class="register-btn" :class="{ disabled: !canRegister }" @click="handleRegister">
        {{registerButtonText}}
      </button>
    </view>
    
    <!-- 加载中 -->
    <view v-if="isLoading" class="loading-container">
      <view class="loading-spinner"></view>
      <text class="loading-text">加载中...</text>
    </view>
    
    <!-- 加载错误 -->
    <view v-if="loadError" class="error-container">
      <text class="error-text">{{errorMsg}}</text>
      <button class="retry-btn" @click="loadCompetitionDetail">重试</button>
    </view>
  </view>
</template>

<script>
import request from '@/utils/request'

export default {
  data() {
    return {
      competitionId: null,
      competition: {},
      isLoading: false,
      loadError: false,
      errorMsg: '加载失败，请重试'
    }
  },
  computed: {
    competitionStatus() {
      const status = this.competition.status || '';
      return status.toLowerCase();
    },
    statusText() {
      const statusMap = {
        'REGISTERING': '报名中',
        'ONGOING': '进行中',
        'UPCOMING': '即将开始',
        'ENDED': '已结束'
      };
      return statusMap[this.competition.status] || '未知状态';
    },
    canRegister() {
		const now = new Date();
		const signup = this.competition.signupStartTime
		if(this.competition.status === 'active' || this.competition.status === 'available'){
			return true;
		} else if(this.competition.status === 'pending' || this.competition.status === 'ending'){
			return false;
		}

    },
    registerButtonText() {
		const now = new Date();
		const signup = this.competition.signupStartTime
      if (this.competition.status === 'ended') {
        return '已结束';
	  } else if (this.competition.status === 'available'){
		return '报名中';
      } else if (this.competition.status === 'pending') {
        return '即将开始';
	  } else if (this.competition.status === 'active') {
		  return '活动中';
      } else if (this.competition.currentParticipants >= this.competition.maxParticipants) {
        return '名额已满';
      } else {
        return '立即报名';
      }
    },
    requirementsList() {
      if (!this.competition.requirements) return [];
      try {
        return this.competition.requirements.split('\n').filter(item => item.trim() !== '');
      } catch (e) {
        return [this.competition.requirements];
      }
    },
    awardsList() {
      if (!this.competition.awards) return [];
      try {
        const lines = this.competition.awards.split('\n').filter(item => item.trim() !== '');
        return lines.map(line => {
          const parts = line.split(':');
          return {
            name: parts[0] || '',
            count: parts[1] || '若干'
          };
        });
      } catch (e) {
        return [];
      }
    }
  },
  onLoad(options) {
    this.competitionId = options.id;
    this.loadCompetitionDetail();
  },
  methods: {
    async loadCompetitionDetail() {
      if (!this.competitionId) {
        this.loadError = true;
        this.errorMsg = '竞赛ID不能为空';
        return;
      }
      
      this.isLoading = true;
      this.loadError = false;
      
      try {
        uni.showLoading({
          title: '加载中...'
        });
        
        const response = await this.fetchCompetitionDetail();
        console.log('竞赛详情数据:', response);
        
        if (response) {
          this.competition = response;
          console.log('竞赛详情加载成功:', this.competition);
        } else {
          throw new Error('获取竞赛详情失败');
        }
      } catch (error) {
        console.error('获取竞赛详情失败:', error);
        this.loadError = true;
        this.errorMsg = '获取竞赛详情失败，请重试';
        uni.showToast({
          title: this.errorMsg,
          icon: 'none'
        });
      } finally {
        this.isLoading = false;
        uni.hideLoading();
      }
    },
    
    async fetchCompetitionDetail() {
      try {
        const response = await request.get(`/competitions/${this.competitionId}`);
        return response;
      } catch (error) {
        console.error('API请求失败:', error);
        throw error;
      }
    },
    
formatDate(dateString) {
  if (!dateString) return '未设置';
  const date = new Date(dateString);
  if (isNaN(date.getTime())) return '日期格式错误'; // 容错
  
  // 提取并格式化所有字段
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  const seconds = String(date.getSeconds()).padStart(2, '0');
  
  return `${year}年${month}月${day}日 ${hours}:${minutes}:${seconds}`;
},
    
    handleRegister() {
		console.log('开始报名程序')
      if (!this.canRegister) {
        uni.showToast({
          title: '当前状态不可报名',
          icon: 'none'
        })
        return
      }
      console.log('发送报名请求')
      uni.navigateTo({
        url: `/pages/competition/register?competitionId=${this.competition.competitionId}`
      })
    },
    
    async registerCompetition() {
      try {
        uni.showLoading({
          title: '报名中...'
        });
        
        const response = await request.post(`/api/registrations/competition/${this.competitionId}`);
        
        uni.hideLoading();
        
        if (response) {
          uni.showToast({
            title: '报名成功',
            icon: 'success'
          });
          
          // 重新加载竞赛详情，更新参与人数
          setTimeout(() => {
            this.loadCompetitionDetail();
          }, 1500);
        }
      } catch (error) {
        uni.hideLoading();
        
        uni.showToast({
          title: error.message || '报名失败，请重试',
          icon: 'none'
        });
      }
    }
  }
}
</script>

<style>
.container {
  padding-bottom: 120rpx;
}

.competition-header {
  position: relative;
  height: 400rpx;
}

.competition-cover {
  width: 100%;
  height: 100%;
}

.competition-title {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 30rpx;
  color: #fff;
  font-size: 36rpx;
  font-weight: bold;
  background: linear-gradient(to top, rgba(0,0,0,0.7), transparent);
}

.competition-status {
  position: absolute;
  top: 30rpx;
  right: 30rpx;
  padding: 8rpx 20rpx;
  border-radius: 30rpx;
  font-size: 24rpx;
  color: #fff;
}

.competition-status.ongoing, .competition-status.registering {
  background: rgba(24, 144, 255, 0.8);
}

.competition-status.upcoming {
  background: rgba(82, 196, 26, 0.8);
}

.competition-status.ended {
  background: rgba(0, 0, 0, 0.5);
}

.competition-info {
  padding: 30rpx;
}

.info-section {
  margin-bottom: 40rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  margin-bottom: 20rpx;
  padding-left: 20rpx;
  border-left: 8rpx solid #3cc51f;
}

.section-content {
  font-size: 28rpx;
  color: #666;
  line-height: 1.6;
}

.time-list {
  background: #f8f8f8;
  padding: 20rpx;
  border-radius: 12rpx;
}

.time-item {
  display: flex;
  margin-bottom: 10rpx;
}

.time-label {
  width: 160rpx;
  font-size: 28rpx;
  color: #999;
}

.time-value {
  flex: 1;
  font-size: 28rpx;
  color: #333;
}

.requirement-list {
  background: #f8f8f8;
  padding: 20rpx;
  border-radius: 12rpx;
}

.requirement-item {
  font-size: 28rpx;
  color: #666;
  margin-bottom: 10rpx;
  line-height: 1.6;
}

.award-list {
  display: flex;
  flex-wrap: wrap;
  gap: 20rpx;
}

.award-item {
  flex: 1;
  min-width: 200rpx;
  background: #f8f8f8;
  padding: 20rpx;
  border-radius: 12rpx;
  text-align: center;
}

.award-name {
  font-size: 28rpx;
  color: #333;
  margin-bottom: 10rpx;
  display: block;
}

.award-count {
  font-size: 24rpx;
  color: #999;
}

.action-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  padding: 20rpx 30rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 -2rpx 10rpx rgba(0,0,0,0.1);
}

.participant-count {
  font-size: 28rpx;
  color: #666;
}

.count {
  color: #3cc51f;
  font-weight: bold;
}

.register-btn {
  width: 240rpx;
  height: 80rpx;
  line-height: 80rpx;
  text-align: center;
  background: #3cc51f;
  color: #fff;
  border-radius: 40rpx;
  font-size: 32rpx;
}

.register-btn.disabled {
  background: #ccc;
}

.loading-container, .error-container {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background-color: rgba(255, 255, 255, 0.9);
  z-index: 999;
}

.loading-spinner {
  width: 60rpx;
  height: 60rpx;
  border: 6rpx solid #f3f3f3;
  border-top: 6rpx solid #3cc51f;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 20rpx;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-text, .error-text {
  font-size: 30rpx;
  color: #666;
  margin-bottom: 20rpx;
}

.retry-btn {
  background-color: #3cc51f;
  color: white;
  padding: 10rpx 30rpx;
  border-radius: 10rpx;
  font-size: 28rpx;
}

.basic-info {
  background: #f8f8f8;
  padding: 20rpx;
  border-radius: 12rpx;
}

.info-item {
  display: flex;
  margin-bottom: 10rpx;
}

.info-label {
  width: 160rpx;
  font-size: 28rpx;
  color: #999;
}

.info-value {
  flex: 1;
  font-size: 28rpx;
  color: #333;
}
</style> 