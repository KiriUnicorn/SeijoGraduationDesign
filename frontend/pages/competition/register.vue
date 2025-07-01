<template>
  <view class="container">
    <view class="header">
      <text class="title">竞赛报名</text>
      <text class="subtitle">{{competition.name}}</text>
    </view>

    <view class="action-buttons">
      <button class="action-btn join-btn" @click="showJoinTeamPopup">
        <text class="iconfont icon-team"></text>
        <text>加入已有小队</text>
      </button>
      <button class="action-btn create-btn" @click="showCreateTeamPopup">
        <text class="iconfont icon-add"></text>
        <text>创建新队伍</text>
      </button>
    </view>

    <!-- 加入队伍弹窗 -->
    <view v-if="showJoinTeamModal" class="modal">
      <view class="modal-mask" @click="closeJoinTeamModal"></view>
      <view class="modal-content">
        <view class="popup-header">
          <text class="popup-title">选择要加入的队伍</text>
          <text class="close-btn" @click="closeJoinTeamModal">×</text>
        </view>
        
        <scroll-view scroll-y class="team-list">
          <view v-for="team in teams" :key="team.teamId" class="team-item" @click="joinTeam(team)">
            <view class="team-info">
              <view class="team-header">
                <text class="team-name">{{team.name}}</text>
                <text class="team-status">{{team.memberCount}}/{{team.maxMemberCount}}人</text>
              </view>
              <view class="team-details">
                <view class="detail-item">
                  <text class="detail-label">队长：</text>
                  <text class="detail-value">{{team.captainName || '未设置'}}</text>
                </view>
                <view class="detail-item">
                  <text class="detail-label">学院：</text>
                  <text class="detail-value">{{team.departmentName || '未设置'}}</text>
                </view>
                <view class="detail-item">
                  <text class="detail-label">指导老师：</text>
                  <text class="detail-value">{{team.teacherName || '未设置'}}</text>
                </view>
              </view>
            </view>
          </view>
        </scroll-view>
      </view>
    </view>

    <!-- 创建队伍弹窗 -->
    <view v-if="showCreateTeamModal" class="modal">
      <view class="modal-mask" @click="closeCreateTeamModal"></view>
      <view class="modal-content">
        <view class="popup-header">
          <text class="popup-title">创建新队伍</text>
          <text class="close-btn" @click="closeCreateTeamModal">×</text>
        </view>
        
        <view class="form-group">
          <view class="form-item">
            <text class="label">队伍名称</text>
            <input type="text" v-model="newTeam.name" placeholder="请输入队伍名称" />
          </view>
          
          <view class="form-item">
            <text class="label">最大成员数</text>
            <input type="number" v-model="newTeam.maxMemberCount" placeholder="请输入最大成员数" />
          </view>
          
          <view class="form-item">
            <text class="label">指导老师</text>
            <picker @change="handleTeacherChange" :value="selectedTeacherIndex" :range="teachers" range-key="name">
              <view class="picker">
                {{selectedTeacher ? selectedTeacher.name : '请选择指导老师'}}
              </view>
            </picker>
          </view>
        </view>

        <button class="submit-btn" @click="createTeam">创建队伍</button>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      competition: {},
      teams: [],
      teachers: [],
      selectedTeacherIndex: -1,
      selectedTeacher: null,
      newTeam: {
        name: '',
        maxMemberCount: '',
        teacherId: ''
      },
      showJoinTeamModal: false,
      showCreateTeamModal: false
    }
  },
  
  onLoad(options) {
    if (options.competitionId) {
      this.loadCompetitionDetail(options.competitionId)
    }
  },
  
  methods: {
    async loadCompetitionDetail(competitionId) {
      try {
        const [err, res] = await uni.request({
          url: `http://localhost:8080/api/competitions/${competitionId}`,
          method: 'GET'
        })
        
        if (err) throw err
        
        if (res.statusCode === 200 && res.data.code === 200) {
          this.competition = res.data.data
          await this.loadTeams(competitionId)
          await this.loadTeachers()
        }
      } catch (error) {
        console.error('加载竞赛详情失败:', error)
        uni.showToast({
          title: '加载竞赛详情失败',
          icon: 'none'
        })
      }
    },
    
    async loadTeams(competitionId) {
      try {
        const token = uni.getStorageSync('token')
        if (!token) {
          console.error('未登录')
          return
        }
        
        const [err, res] = await uni.request({
          url: `http://localhost:8080/api/teams/competition/${competitionId}`,
          method: 'GET',
          header: {
            'Authorization': token
          }
        })
        
        if (err) throw err
        
        console.log('队伍列表原始响应:', res.data)
        
        if (res.statusCode === 200 && res.data.code === 200) {
          this.teams = res.data.data.map(team => {
            console.log('处理前的队伍数据:', team)
            const processedTeam = {
              teamId: team.teamId || '',
              name: team.name || '',
              competitionId: team.competitionId || '',
              captainId: team.captainId || '',
              memberCount: team.memberCount || 0,
              maxMemberCount: team.maxMemberCount || 0,
              departmentId: team.departmentId || '',
              teacherId: team.teacherId || '',
              createdAt: team.createdAt || '',
              captainName: team.captainName || '未设置',
              departmentName: team.departmentName || '未设置',
              teacherName: team.teacherName || '未设置'
            }
            console.log('处理后的队伍数据:', processedTeam)
            return processedTeam
          })
          console.log('最终处理后的队伍列表:', this.teams)
        } else {
          console.error('获取队伍列表失败:', res.data.message)
        }
      } catch (error) {
        console.error('加载队伍列表失败:', error)
        uni.showToast({
          title: '加载队伍列表失败',
          icon: 'none'
        })
      }
    },
    
    async loadTeachers() {
      if (!this.competition.responsibleDepartmentId) {
        console.error('部门ID不存在')
        return
      }
      
      try {
        const token = uni.getStorageSync('token')
        if (!token) {
          console.error('未登录')
          return
        }
        
        const [err, res] = await uni.request({
          url: `http://localhost:8080/api/teachers/department/${this.competition.responsibleDepartmentId}`,
          method: 'GET',
          header: {
            'Authorization': token
          }
        })
        
        if (err) throw err
        
        console.log('教师列表响应:', res.data)
        
        if (res.statusCode === 200 && res.data.code === 200) {
          this.teachers = res.data.data
          console.log('设置教师列表:', this.teachers)
        } else {
          console.error('获取教师列表失败:', res.data.message)
        }
      } catch (error) {
        console.error('加载教师列表失败:', error)
        uni.showToast({
          title: '加载教师列表失败',
          icon: 'none'
        })
      }
    },
    
    showJoinTeamPopup() {
      this.showJoinTeamModal = true
    },
    
    closeJoinTeamModal() {
      this.showJoinTeamModal = false
    },
    
    showCreateTeamPopup() {
      this.showCreateTeamModal = true
    },
    
    closeCreateTeamModal() {
      this.showCreateTeamModal = false
    },
    
    handleTeacherChange(e) {
      this.selectedTeacherIndex = e.detail.value
      this.selectedTeacher = this.teachers[this.selectedTeacherIndex]
      this.newTeam.teacherId = this.selectedTeacher.teacherId
    },
    
async joinTeam(team) {
  uni.showModal({
    title: '确认加入',
    content: '你确认要加入此小队吗？',
    success: async (res) => {
      if (!res.confirm) return;
      
      try {
        const token = uni.getStorageSync('token');
        if (!token) {
          uni.showToast({ title: '请先登录', icon: 'none' });
          return;
        }

        if (team.memberCount >= team.maxMemberCount) {
          uni.showModal({
            title: '提示',
            content: '队伍已满！',
            showCancel: false,
            confirmText: '确定',
            confirmColor: '#3c76ff',
          });
          return;
        }

        // 添加请求日志
        console.log('发送请求数据:', {
          competitionId: this.competition.competitionId,
          teamId: team.teamId
        });

        const [err, response] = await uni.request({
          url: 'http://localhost:8080/api/registrations',
          method: 'POST',
          header: {
                  'Content-Type': 'application/json',
                  'Authorization': token
          },
          data: JSON.stringify({ // 明确序列化
            competitionId: this.competition.competitionId,
            teamId: team.teamId
          })
        });

        // 调试日志
        console.log('响应状态:', response?.statusCode);
        console.log('响应数据:', response?.data);

        if (err) {
          console.error('请求错误:', err);
          throw new Error('网络请求失败');
        }

        if (response.statusCode !== 200) {
          throw new Error(`服务器错误: ${response.statusCode}`);
        }

        if (!response.data || response.data.code !== 200) {
          throw new Error(response.data?.message || '申请处理失败');
        }

        // 成功处理
        uni.showToast({ title: '申请已提交', icon: 'success' });
        this.closeJoinTeamModal();
        setTimeout(() => uni.navigateBack(), 1500);

      } catch (error) {
        console.error('申请失败详情:', error);
        uni.showToast({
          title: error.message || '申请失败，请重试',
          icon: 'none'
        });
      }
    }
  });
},
    
    async createTeam() {
      if (!this.newTeam.name || !this.newTeam.maxMemberCount || !this.newTeam.teacherId) {
        uni.showToast({
          title: '请填写完整信息',
          icon: 'none'
        })
        return
      }
      
      // 添加确认弹窗
      uni.showModal({
        title: '确认创建',
        content: '你确认要创建此队伍吗？',
        success: async (res) => {
          if (res.confirm) {
            try {
              const token = uni.getStorageSync('token')
              if (!token) {
                uni.showToast({
                  title: '请先登录',
                  icon: 'none'
                })
                return
              }
              
              const [err, res] = await uni.request({
                url: 'http://localhost:8080/api/teams',
                method: 'POST',
                header: {
                  'Content-Type': 'application/json',
                  'Authorization': token
                },
                data: {
                  ...this.newTeam,
                  competitionId: this.competition.competitionId
                }
              })
              
              if (err) throw err
              
              if (res.statusCode === 200 && res.data.code === 200) {
                uni.showToast({
                  title: '队伍创建成功',
                  icon: 'success'
                })
                this.closeCreateTeamModal()
                setTimeout(() => {
                  uni.navigateBack()
                }, 1500)
              } else {
                uni.showToast({
                  title: res.data.message || '创建失败',
                  icon: 'none'
                })
              }
            } catch (error) {
              console.error('创建队伍失败:', error)
              uni.showToast({
                title: '创建失败，请重试',
                icon: 'none'
              })
            }
          }
        }
      })
    }
  }
}
</script>

<style lang="scss">
.container {
  padding: 30rpx;
}

.header {
  margin-bottom: 40rpx;
}

.title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
}

.subtitle {
  font-size: 28rpx;
  color: #666;
  margin-top: 10rpx;
}

.action-buttons {
  display: flex;
  justify-content: space-between;
  padding: 20rpx;
  margin-top: 30rpx;
  gap: 20rpx;
}

.action-btn {
  flex: 1;
  height: 180rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-radius: 16rpx;
  border: none;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  
  .iconfont {
    font-size: 48rpx;
    margin-bottom: 16rpx;
  }
  
  text {
    font-size: 28rpx;
    font-weight: 500;
  }
  
  &:active {
    transform: scale(0.98);
    box-shadow: 0 2rpx 6rpx rgba(0, 0, 0, 0.1);
  }
}

.join-btn {
  background: linear-gradient(135deg, #4CAF50, #45a049);
  color: #ffffff;
  
  .iconfont {
    color: rgba(255, 255, 255, 0.9);
  }
}

.create-btn {
  background: linear-gradient(135deg, #2196F3, #1e88e5);
  color: #ffffff;
  
  .iconfont {
    color: rgba(255, 255, 255, 0.9);
  }
}

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
  background: rgba(0, 0, 0, 0.5);
}

.modal-content {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  background: #fff;
  border-radius: 24rpx 24rpx 0 0;
  padding: 30rpx;
  max-height: 80vh;
}

.popup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30rpx;
}

.popup-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.close-btn {
  font-size: 40rpx;
  color: #999;
  padding: 10rpx;
}

.team-list {
  max-height: 60vh;
  padding: 0 10rpx;
}

.team-item {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
  border: 2rpx solid #f0f0f0;
}

.team-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
  padding-bottom: 16rpx;
  border-bottom: 2rpx solid #f5f5f5;
}

.team-name {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.team-status {
  font-size: 26rpx;
  color: #666;
  background: #f8f8f8;
  padding: 4rpx 16rpx;
  border-radius: 20rpx;
}

.team-details {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
}

.detail-item {
  display: flex;
  align-items: center;
}

.detail-label {
  font-size: 26rpx;
  color: #999;
  width: 140rpx;
}

.detail-value {
  font-size: 26rpx;
  color: #333;
  flex: 1;
}

.team-item:active {
  background: #f9f9f9;
}

.form-group {
  margin-bottom: 30rpx;
}

.form-item {
  margin-bottom: 20rpx;
}

.label {
  font-size: 28rpx;
  color: #333;
  margin-bottom: 10rpx;
  display: block;
}

input {
  width: 100%;
  height: 80rpx;
  background: #f8f8f8;
  border-radius: 8rpx;
  padding: 0 20rpx;
  font-size: 28rpx;
}

.picker {
  width: 100%;
  height: 80rpx;
  background: #f8f8f8;
  border-radius: 8rpx;
  padding: 0 20rpx;
  font-size: 28rpx;
  line-height: 80rpx;
}

.submit-btn {
  width: 100%;
  height: 88rpx;
  line-height: 88rpx;
  text-align: center;
  background: #3cc51f;
  color: #fff;
  border-radius: 44rpx;
  font-size: 32rpx;
}
</style> 