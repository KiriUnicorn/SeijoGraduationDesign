<template>
  <view class="container">
    <view class="header">
      <text class="title">成绩管理</text>
    </view>

    <!-- 未录入成绩的队伍列表 -->
    <view class="section">
      <text class="section-title">待录入成绩的队伍</text>
      <view class="team-list">
        <view v-if="teamsWithoutScore.length === 0" class="empty-tip">
          <text>暂无待录入成绩的队伍</text>
        </view>
        <view v-else class="team-item" v-for="team in teamsWithoutScore" :key="team.teamId">
          <view class="team-info">
            <text class="team-name">{{ team.name }}</text>
            <text class="competition-name">竞赛：{{ team.competitionName }}</text>
            <text class="team-detail">队长：{{ team.captainName }}</text>
            <text class="team-detail">院系：{{ team.departmentName }}</text>
            <text class="team-detail">成员数：{{ team.memberCount }}/{{ team.maxMemberCount }}</text>
          </view>
          <button class="action-btn" @click="showScoreInputModal(team)">录入成绩</button>
        </view>
      </view>
    </view>

    <!-- 已录入成绩的列表 -->
    <view class="section">
      <text class="section-title">已录入成绩</text>
      <view class="score-list">
        <view v-if="scores.length === 0" class="empty-tip">
          <text>暂无成绩记录</text>
        </view>
        <view v-else class="score-item" v-for="score in scores" :key="score.scoreId" @click="goToScoreDetail(score)">
          <view class="score-info">
            <text class="team-name">{{ score.team_name }}</text>
            <text class="competition-name">竞赛：{{ score.competitionName }}</text>
			<text class="award-level">获奖队伍：{{ score.teamName }}</text>
            <text class="award-level">获奖等级：{{ score.awardLevel }}</text>
            <text class="created-at">录入时间：{{ formatDate(score.createdAt) }}</text>
          </view>
          <button class="action-btn edit" @click.stop="showScoreEditModal(score)">修改</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      teamsWithoutScore: [],
      scores: [],
      awardLevels: ['特等奖','一等奖', '二等奖', '三等奖', '优胜奖'],
      selectedAwardLevel: '',
      certificatePath: '',
      currentTeam: null,
      isEditing: false,
      currentScore: null
    }
  },
  onShow() {
    this.loadTeamsWithoutScore()
    this.loadScores()
  },
  methods: {
    async loadTeamsWithoutScore() {
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
          url: 'http://localhost:8080/api/scores/teacher/teams',
          method: 'GET',
          header: {
            'Authorization': `${token}`
          }
        })

        if (error) {
          throw new Error(error.errMsg)
        }

        console.log('API Response:', res.data)
        
        if (res.data.code === 200) {
          this.teamsWithoutScore = res.data.data
          console.log('Teams without score:', this.teamsWithoutScore)
        } else {
          throw new Error(res.data.message)
        }
      } catch (error) {
        console.error('Error loading teams:', error)
        uni.showToast({
          title: error.message || '加载队伍列表失败',
          icon: 'none'
        })
      }
    },

    async loadScores() {
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
          url: 'http://localhost:8080/api/scores/teacher',
          method: 'GET',
          header: {
            'Authorization': `${token}`
          }
        })

        if (error) {
          throw new Error(error.errMsg)
        }

        console.log('Scores API Response:', res.data)

        if (res.data.code === 200) {
          this.scores = res.data.data
          console.log('Scores data:', this.scores)
        } else {
          throw new Error(res.data.message)
        }
      } catch (error) {
        console.error('Error loading scores:', error)
        uni.showToast({
          title: error.message || '加载成绩列表失败',
          icon: 'none'
        })
      }
    },

    showScoreInputModal(team) {
      this.isEditing = false
      this.currentTeam = team
      this.selectedAwardLevel = ''
      this.certificatePath = ''
      this.showScoreModal()
    },

    showScoreEditModal(score) {
      this.isEditing = true
      this.currentScore = score
      this.selectedAwardLevel = score.award_level
      this.certificatePath = score.certificatePath
      this.showScoreModal()
    },

    showScoreModal() {
      uni.showActionSheet({
        itemList: this.awardLevels,
        success: (res) => {
          this.selectedAwardLevel = this.awardLevels[res.tapIndex]
          this.chooseCertificate()
        }
      })
    },

    async chooseCertificate() {
      try {
        const [error, res] = await uni.chooseImage({
          count: 1,
          sizeType: ['original', 'compressed'],
          sourceType: ['album', 'camera']
        })

        if (error) {
          throw new Error(error.errMsg)
        }

        // 生成文件名：时间戳_队伍ID.jpg
        const timestamp = new Date().getTime()
        const teamId = this.isEditing ? this.currentScore.teamId : this.currentTeam.teamId
        const fileName = `${timestamp}_${teamId}.jpg`

        // 上传图片到服务器
        const token = uni.getStorageSync('token')
        console.log('Uploading file:', {
          url: 'http://localhost:8080/api/upload/score',
          filePath: res.tempFilePaths[0],
          fileName: fileName
        })

        const [uploadError, uploadRes] = await uni.uploadFile({
          url: 'http://localhost:8080/api/upload/score',
          filePath: res.tempFilePaths[0],
          name: 'file',
          header: {
            'Authorization': `Bearer ${token}`
          },
          formData: {
            fileName: fileName
          }
        })

        if (uploadError) {
          console.error('Upload error:', uploadError)
          throw new Error(uploadError.errMsg)
        }

        console.log('Upload response:', uploadRes)
        const uploadResult = JSON.parse(uploadRes.data)
        console.log('Parsed upload result:', uploadResult)

        if (uploadResult.code !== 200) {
          throw new Error(uploadResult.message || '上传失败')
        }

        // 更新证书路径
        this.certificatePath = uploadResult.data
        console.log('Certificate path updated:', this.certificatePath)
        
        // 选择完证书后自动提交
        this.handleScoreSubmit()
      } catch (error) {
        console.error('Upload certificate error:', error)
        uni.showToast({
          title: error.message || '上传证书失败',
          icon: 'none',
          duration: 3000
        })
      }
    },

    async handleScoreSubmit() {
      try {
        if (!this.selectedAwardLevel) {
          throw new Error('请选择获奖等级')
        }

        const token = uni.getStorageSync('token')
        const data = {
          teamId: this.isEditing ? this.currentScore.teamId : this.currentTeam.teamId,
          awardLevel: this.selectedAwardLevel,
          certificatePath: this.certificatePath
        }

        const [error, res] = await uni.request({
          url: this.isEditing 
            ? `http://localhost:8080/api/scores/${this.currentScore.scoreId}`
            : 'http://localhost:8080/api/scores',
          method: this.isEditing ? 'PUT' : 'POST',
          header: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          },
          data
        })

        if (error) {
          throw new Error(error.errMsg)
        }

        if (res.data.code === 200) {
          uni.showToast({
            title: this.isEditing ? '修改成功' : '录入成功',
            icon: 'success'
          })
          this.loadTeamsWithoutScore()
          this.loadScores()
        } else {
          throw new Error(res.data.message)
        }
      } catch (error) {
        uni.showToast({
          title: error.message || '操作失败',
          icon: 'none'
        })
      }
    },

    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
    },

    goToScoreDetail(score) {
      uni.navigateTo({
        url: `/pages/teacher/score-detail?scoreId=${score.scoreId}`
      })
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

.section {
  background-color: #fff;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 20px;
}

.section-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-bottom: 15px;
}

.empty-tip {
  text-align: center;
  padding: 20px 0;
  color: #999;
}

.team-item, .score-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.team-info, .score-info {
  flex: 1;
}

.team-name, .competition-name {
  display: block;
  margin-bottom: 5px;
}

.team-name {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
}

.competition-name {
  font-size: 14px;
  color: #666;
  margin-bottom: 4px;
}

.award-level, .created-at {
  display: block;
  font-size: 14px;
  color: #666;
  margin-top: 5px;
}

.team-detail {
  display: block;
  font-size: 14px;
  color: #666;
  margin-top: 4px;
}

.action-btn {
  padding: 6px 16px;
  border-radius: 4px;
  font-size: 14px;
  background-color: #1890ff;
  color: #fff;
}

.action-btn.edit {
  background-color: #52c41a;
}
</style> 