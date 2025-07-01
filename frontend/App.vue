<script>
export default {
    onLaunch: function() {
        console.log('App Launch')
        // 检查登录状态
        this.checkLoginStatus()
    },
    onShow: function() {
        console.log('App Show')
    },
    onHide: function() {
        console.log('App Hide')
    },
    methods: {
        checkLoginStatus() {
            const token = uni.getStorageSync('token')
            const userInfo = uni.getStorageSync('userInfo')
            const role = uni.getStorageSync('role')
            
            console.log('App检查登录状态:', { token, userInfo, role })

            // 如果没有token或userInfo或role，跳转到登录页
            if (!token || !userInfo || !role) {
                console.log('App: 登录状态无效，跳转到登录页')
                this.clearLoginInfo()
                uni.reLaunch({
                    url: '/pages/user/login'
                })
                return
            }

            // 验证token是否过期
            try {
                const tokenParts = token.split('.')
                if (tokenParts.length === 3) {
                    const base64Url = tokenParts[1]
                    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
                    const payload = JSON.parse(decodeURIComponent(atob(base64).split('').map(function(c) {
                        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)
                    }).join('')))
                    
                    const expirationTime = payload.exp * 1000 // 转换为毫秒
                    if (Date.now() >= expirationTime) {
                        console.log('App: Token已过期')
                        this.clearLoginInfo()
                        uni.reLaunch({
                            url: '/pages/user/login'
                        })
                        return
                    }
                    
                    // 验证token中的用户信息与存储的用户信息是否一致
                    if (payload.userId !== userInfo.userId || payload.role !== role) {
                        console.log('App: Token中的用户信息与存储的用户信息不一致')
                        this.clearLoginInfo()
                        uni.reLaunch({
                            url: '/pages/user/login'
                        })
                        return
                    }
                } else {
                    throw new Error('无效的token格式')
                }
            } catch (e) {
                console.error('App: Token验证失败:', e)
                this.clearLoginInfo()
                uni.reLaunch({
                    url: '/pages/user/login'
                })
            }
        },
        
        clearLoginInfo() {
            uni.removeStorageSync('token')
            uni.removeStorageSync('userInfo')
            uni.removeStorageSync('role')
        }
    }
}
</script>

<style>
/* 全局样式 */
page {
    background-color: #F8F8F8;
    font-size: 16px;
    font-family: -apple-system, BlinkMacSystemFont, 'Helvetica Neue', Helvetica,
        Segoe UI, Arial, Roboto, 'PingFang SC', 'miui', 'Hiragino Sans GB', 'Microsoft Yahei',
        sans-serif;
}

.container {
    padding: 20px;
}

.btn {
    margin: 10px 0;
    padding: 10px 20px;
    border-radius: 4px;
    font-size: 16px;
    color: #FFFFFF;
    background-color: #007AFF;
    border: none;
}

.btn:active {
    background-color: #0062CC;
}

.btn-outline {
    background-color: transparent;
    color: #007AFF;
    border: 1px solid #007AFF;
}

.btn-outline:active {
    background-color: rgba(0, 122, 255, 0.1);
}

.input {
    width: 100%;
    height: 40px;
    padding: 0 10px;
    margin: 10px 0;
    border: 1px solid #DDDDDD;
    border-radius: 4px;
    box-sizing: border-box;
}

.card {
    background-color: #FFFFFF;
    border-radius: 8px;
    padding: 15px;
    margin-bottom: 15px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.title {
    font-size: 18px;
    font-weight: bold;
    margin-bottom: 10px;
}

.text-primary {
    color: #007AFF;
}

.text-success {
    color: #4CD964;
}

.text-warning {
    color: #FF9500;
}

.text-danger {
    color: #FF3B30;
}

.text-muted {
    color: #8E8E93;
}

.flex-row {
    display: flex;
    flex-direction: row;
}

.flex-column {
    display: flex;
    flex-direction: column;
}

.justify-between {
    justify-content: space-between;
}

.align-center {
    align-items: center;
}

.text-center {
    text-align: center;
}
</style> 