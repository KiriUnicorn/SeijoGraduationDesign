<template>
    <view class="container">
        <view class="register-form">
            <view class="title">用户注册</view>
            <view class="form-item">
                <input class="input" type="text" v-model="form.studentId" placeholder="请输入学号/工号" />
            </view>
            <view class="form-item">
                <input class="input" type="text" v-model="form.username" placeholder="请输入用户名" />
            </view>
            <view class="form-item">
                <input class="input" type="password" v-model="form.password" placeholder="请输入密码" />
            </view>
            <view class="form-item">
                <input class="input" type="password" v-model="form.confirmPassword" placeholder="请确认密码" />
            </view>
            <view class="form-item">
                <input class="input" type="text" v-model="form.email" placeholder="请输入电子邮箱" />
            </view>
            <view class="form-item">
                <input class="input" type="text" v-model="form.phoneNumber" placeholder="请输入手机号" />
            </view>
            <view class="form-item">
                <input class="input" type="text" v-model="form.realName" placeholder="请输入真实姓名（选填）" />
            </view>
            <button class="btn" @click="handleRegister">注册</button>
            <view class="login-link" @click="goToLogin">已有账号？立即登录</view>
        </view>
    </view>
</template>

<script>
export default {
    data() {
        return {
            form: {
                studentId: '',
                username: '',
                password: '',
                confirmPassword: '',
                email: '',
                phoneNumber: '',
                realName: ''
            }
        }
    },
    methods: {
        validateForm() {
            if (!this.form.studentId) {
                uni.showToast({
                    title: '请输入学号/工号',
                    icon: 'none'
                })
                return false
            }
            if (!this.form.username) {
                uni.showToast({
                    title: '请输入用户名',
                    icon: 'none'
                })
                return false
            }
            if (!this.form.password) {
                uni.showToast({
                    title: '请输入密码',
                    icon: 'none'
                })
                return false
            }
            if (this.form.password !== this.form.confirmPassword) {
                uni.showToast({
                    title: '两次输入的密码不一致',
                    icon: 'none'
                })
                return false
            }
            if (!this.form.email) {
                uni.showToast({
                    title: '请输入电子邮箱',
                    icon: 'none'
                })
                return false
            }
            if (!this.validateEmail(this.form.email)) {
                uni.showToast({
                    title: '请输入正确的电子邮箱',
                    icon: 'none'
                })
                return false
            }
            if (!this.form.phoneNumber) {
                uni.showToast({
                    title: '请输入手机号',
                    icon: 'none'
                })
                return false
            }
            if (!this.validatePhone(this.form.phoneNumber)) {
                uni.showToast({
                    title: '请输入正确的手机号',
                    icon: 'none'
                })
                return false
            }
            return true
        },
        validateEmail(email) {
            const reg = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
            return reg.test(email)
        },
        validatePhone(phone) {
            const reg = /^1[3-9]\d{9}$/
            return reg.test(phone)
        },
        async handleRegister() {
            if (!this.validateForm()) {
                return
            }
            
            try {
                uni.showLoading({
                    title: '注册中...'
                })
                
                // TODO: 调用注册API
                const response = await this.$http.post('/api/user/register', {
                    studentId: this.form.studentId,
                    username: this.form.username,
                    password: this.form.password,
                    email: this.form.email,
                    phoneNumber: this.form.phoneNumber,
                    realName: this.form.realName
                })
                
                uni.hideLoading()
                
                if (response.data.code === 200) {
                    uni.showToast({
                        title: '注册成功',
                        icon: 'success',
                        duration: 1500
                    })
                    setTimeout(() => {
                        uni.navigateTo({
                            url: '/pages/user/login',
                            success: () => {
                                console.log('跳转到登录页面成功')
                            },
                            fail: (err) => {
                                console.error('跳转失败:', err)
                                uni.showToast({
                                    title: '跳转失败，请重试',
                                    icon: 'none'
                                })
                            }
                        })
                    }, 1500)
                } else {
                    uni.showToast({
                        title: response.data.message || '注册失败',
                        icon: 'none'
                    })
                }
            } catch (error) {
                uni.hideLoading()
                uni.showToast({
                    title: '注册失败，请重试',
                    icon: 'none'
                })
            }
        },
        goToLogin() {
            uni.navigateTo({
                url: '/pages/user/login'
            })
        }
    }
}
</script>

<style>
.container {
    padding: 30px 20px;
}

.register-form {
    background-color: #FFFFFF;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.title {
    font-size: 24px;
    font-weight: bold;
    text-align: center;
    margin-bottom: 30px;
    color: #333333;
}

.form-item {
    margin-bottom: 15px;
}

.input {
    width: 100%;
    height: 44px;
    padding: 0 15px;
    border: 1px solid #DCDFE6;
    border-radius: 4px;
    font-size: 16px;
    color: #333333;
    box-sizing: border-box;
}

.input:focus {
    border-color: #007AFF;
}

.btn {
    width: 100%;
    height: 44px;
    background-color: #007AFF;
    color: #FFFFFF;
    font-size: 16px;
    border-radius: 4px;
    margin-top: 20px;
}

.btn:active {
    background-color: #0062CC;
}

.login-link {
    text-align: center;
    margin-top: 15px;
    color: #007AFF;
    font-size: 14px;
}

.login-link:active {
    opacity: 0.8;
}
</style> 