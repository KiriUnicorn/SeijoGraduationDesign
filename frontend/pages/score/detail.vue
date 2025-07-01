<template>
  <view class="container">
    <view class="header">
      <text class="title">成绩详情</text>
    </view>

    <view class="detail-card" v-if="score">
      <view class="card-section">
        <text class="section-title">竞赛信息</text>
        <view class="info-item">
          <text class="label">竞赛名称：</text>
          <text class="value">{{ score.competitionName }}</text>
        </view>
        <view class="info-item">
          <text class="label">获奖等级：</text>
          <text class="value award" :class="getAwardClass(score.awardLevel)">{{ score.awardLevel }}</text>
        </view>
        <view class="info-item">
          <text class="label">获奖时间：</text>
          <text class="value">{{ formatDate(score.createdAt) }}</text>
        </view>
      </view>

      <view class="card-section">
        <text class="section-title">队伍信息</text>
        <view class="info-item">
          <text class="label">队伍名称：</text>
          <text class="value">{{ score.teamName }}</text>
        </view>
        <view class="info-item">
          <text class="label">队长：</text>
          <text class="value">{{ score.captainName }}</text>
        </view>
      </view>

      <view class="card-section">
        <text class="section-title">团队成员</text>
        <view class="member-list">
          <view class="member-item" v-for="member in score.members" :key="member.id">
            <text class="member-name">{{ member.studentName }}</text>
            <text class="member-id">{{ member.studentId }}</text>
          </view>
        </view>
      </view>

      <view class="detail-item" v-if="score.certificatePath">
        <text class="label">获奖证书</text>
        <view class="certificate-container">
          <image 
            :src="getCertificateUrl(score.certificatePath)" 
            mode="aspectFit" 
            class="certificate-image"
            @click="previewCertificate(score.certificatePath)"
          ></image>
        </view>
      </view>
    </view>

    <view v-else class="loading">
      <text>加载中...</text>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      score: null
    }
  },

  onLoad(options) {
    if (options.id) {
      this.loadScoreDetail(options.id)
    }
  },

  methods: {
    async loadScoreDetail(scoreId) {
      try {
        const [error, res] = await uni.request({
          url: `http://localhost:8080/api/scores/${scoreId}`,
          method: 'GET'
        })

        if (error) {
          throw new Error(error.errMsg)
        }

        if (res.data.code === 200) {
          this.score = res.data.data
          console.log('证书图片路径:', this.score.certificatePath)
          console.log('完整图片URL:', this.getCertificateUrl(this.score.certificatePath))
        } else {
          throw new Error(res.data.message)
        }
      } catch (error) {
        console.error('加载成绩详情失败:', error)
        uni.showToast({
          title: error.message || '加载失败',
          icon: 'none'
        })
      }
    },

    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
    },

    getAwardClass(level) {
      const map = {
        '一等奖': 'first-prize',
        '二等奖': 'second-prize',
        '三等奖': 'third-prize',
        '优秀奖': 'excellent-prize'
      }
      return map[level] || ''
    },

    getCertificateUrl(path) {
      if (!path) return ''
      return `http://localhost:8081${path}`
    },

    previewCertificate(path) {
      if (!path) return
      uni.previewImage({
        urls: [this.getCertificateUrl(path)],
        current: this.getCertificateUrl(path)
      })
    }
  }
}
</script>

<style>
.container {
  padding: 30rpx;
}

.header {
  margin-bottom: 30rpx;
}

.title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
}

.detail-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
}

.card-section {
  margin-bottom: 40rpx;
}

.card-section:last-child {
  margin-bottom: 0;
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
  display: block;
}

.info-item {
  display: flex;
  margin-bottom: 16rpx;
  font-size: 28rpx;
}

.label {
  color: #999;
  width: 160rpx;
}

.value {
  color: #333;
  flex: 1;
}

.value.award {
  font-weight: bold;
}

.first-prize {
  color: #f5222d;
}

.second-prize {
  color: #fa8c16;
}

.third-prize {
  color: #faad14;
}

.excellent-prize {
  color: #52c41a;
}

.member-list {
  background: #f8f9fa;
  border-radius: 12rpx;
  padding: 20rpx;
}

.member-item {
  display: flex;
  justify-content: space-between;
  padding: 16rpx 0;
  border-bottom: 1rpx solid #eee;
}

.member-item:last-child {
  border-bottom: none;
}

.member-name {
  font-size: 28rpx;
  color: #333;
}

.member-id {
  font-size: 28rpx;
  color: #666;
}

.loading {
  text-align: center;
  padding: 60rpx 0;
  color: #999;
  font-size: 28rpx;
}

.detail-item {
  margin-bottom: 30rpx;
}

.certificate-container {
  margin-top: 20rpx;
}

.certificate-image {
  width: 100%;
  height: 400rpx;
  border-radius: 8rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.1);
}
</style> 