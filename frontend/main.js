import Vue from 'vue'
import App from './App'
import store from './store'
import request from './utils/request'

Vue.config.productionTip = false

// 挂载axios到Vue原型上
Vue.prototype.$http = request

App.mpType = 'app'

const app = new Vue({
  store,
  ...App
})
app.$mount() 