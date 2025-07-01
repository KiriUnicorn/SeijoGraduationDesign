<template>
  <view class="container">
    <view class="form-group">
      <view class="form-item">
        <text class="label">原密码</text>
        <input type="password" v-model="oldPassword" placeholder="请输入原密码" />
      </view>
      <view class="form-item">
        <text class="label">新密码</text>
        <input type="password" v-model="newPassword" placeholder="请输入新密码" />
      </view>
      <view class="form-item">
        <text class="label">确认密码</text>
        <input type="password" v-model="confirmPassword" placeholder="请再次输入新密码" />
      </view>
    </view>

    <button class="submit-btn" @click="handleSubmit">确认修改</button>
  </view>
</template>

<script>
import request from '@/utils/request'

export default {
  data() {
    return {
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    }
  },
  methods: {
    async handleSubmit() {
      if (!this.oldPassword || !this.newPassword) {
        uni.showToast({
          title: '请填写完整信息',
          icon: 'none'
        })
        return
      }
      
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
          url: 'http://localhost:8080/api/user/change-password',
          method: 'POST',
          header: {
            'Content-Type': 'application/json',
            'Authorization': token
          },
          data: {
            oldPassword: this.oldPassword,
            newPassword: this.newPassword
          }
        })
        
        console.log('修改密码响应:', err, res)  // 添加日志
        
        if (err) {
          throw err
        }
        
        if (res.statusCode === 200 && res.data.code === 200) {  // 检查状态码和响应码
          uni.showToast({
            title: '密码修改成功',
            icon: 'success',
            duration: 2000
          })
          
          // 等待提示显示完成后再返回
          setTimeout(() => {
            uni.navigateBack({
              delta: 1,
              success: () => {
                console.log('返回上一页成功')
              },
              fail: (err) => {
                console.error('返回上一页失败:', err)
              }
            })
          }, 2000)
        } else {
          uni.showToast({
            title: res.data?.message || '修改失败',
            icon: 'none',
            duration: 2000
          })
        }
      } catch (error) {
        console.error('修改密码失败:', error)
        uni.showToast({
          title: error.message || '修改失败，请重试',
          icon: 'none',
          duration: 2000
        })
      }
    }
  }
}
</script>

<style>
.container {
  padding: 30rpx;
}

.form-group {
  background: #fff;
  border-radius: 12rpx;
  padding: 20rpx;
  margin-bottom: 40rpx;
}

.form-item {
  display: flex;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #eee;
}

.form-item:last-child {
  border-bottom: none;
}

.label {
  width: 160rpx;
  font-size: 28rpx;
  color: #333;
}

input {
  flex: 1;
  font-size: 28rpx;
  color: #333;
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
  margin-top: 60rpx;
}

.submit-btn:active {
  opacity: 0.8;
}
</style> 
	
	