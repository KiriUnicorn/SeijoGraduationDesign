<template>
  <view class="container">
    <view class="header">
      <text class="title">成绩详情</text>
    </view>

    <view class="content">
      <view class="info-section">
        <view class="info-item">
          <text class="label">竞赛名称：</text>
          <text class="value">{{ score.competitionName }}</text>
        </view>
        <view class="info-item">
          <text class="label">小队名称：</text>
          <text class="value">{{ score.teamName }}</text>
        </view>
        <view class="info-item">
          <text class="label">获奖等级：</text>
          <text class="value">{{ score.awardLevel }}</text>
        </view>
        <view class="info-item">
          <text class="label">小队队长：</text>
          <text class="value">{{ score.captainName }}</text>
        </view>
        <view class="info-item">
          <text class="label">小队成员：</text>
          <view class="members-list">
            <text v-for="(member, index) in score.members" :key="index" class="member-item">
              {{ member.studentName }}
            </text>
          </view>
        </view>
      </view>

      <view class="certificate-section">
        <text class="section-title">获奖证书</text>
        <image 
          v-if="score.certificatePath" 
          :src="getImageUrl(score.certificatePath)" 
          mode="aspectFit" 
          class="certificate-image"
          @click="previewImage"
        />
        <text v-else class="no-certificate">暂无证书</text>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      score: {
        competitionName: '',
        team_name: '',
        awardLevel: '',
        captainName: '',
        members: [],
        certificatePath: ''
      }
    }
  },
  onLoad(options) {
    if (options.scoreId) {
      this.loadScoreDetail(options.scoreId)
    }
  },
  methods: {
    async loadScoreDetail(scoreId) {
      try {
        const token = uni.getStorageSync('token')
        if (!token) {
          uni.showToast({
            title: '请先登录',
            icon: 'none'
          })
          return
        }

        const [error, res] = await uni.request({
          url: `http://localhost:8080/api/scores/${scoreId}`,
          method: 'GET',
          header: {
            'Authorization': `Bearer ${token}`
          }
        })

        if (error) {
          throw new Error(error.errMsg)
        }

        if (res.data.code === 200) {
          this.score = res.data.data
        } else {
          throw new Error(res.data.message)
        }
      } catch (error) {
        console.error('Error loading score detail:', error)
        uni.showToast({
          title: error.message || '加载成绩详情失败',
          icon: 'none'
        })
      }
    },

    getImageUrl(path) {
		console.log('路径：：：：：：：：')
		console.log(path)
      if (!path) return ''
      return `${path}`
    },

    previewImage() {
      if (this.score.certificatePath) {
        uni.previewImage({
          urls: [this.getImageUrl(this.score.certificatePath)],
          current: this.getImageUrl(this.score.certificatePath)
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
}

.content {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
}

.info-section {
  margin-bottom: 30px;
}

.info-item {
  margin-bottom: 15px;
  display: flex;
  align-items: flex-start;
}

.label {
  font-size: 16px;
  color: #666;
  width: 100px;
  flex-shrink: 0;
}

.value {
  font-size: 16px;
  color: #333;
  flex: 1;
}

.members-list {
  flex: 1;
}

.member-item {
  display: block;
  font-size: 16px;
  color: #333;
  margin-bottom: 5px;
}

.certificate-section {
  margin-top: 20px;
}

.section-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-bottom: 15px;
  display: block;
}

.certificate-image {
  width: 100%;
  height: 400px;
  border-radius: 8px;
}

.no-certificate {
  color: #999;
  font-size: 16px;
  text-align: center;
  padding: 20px 0;
}
</style> 
 
 