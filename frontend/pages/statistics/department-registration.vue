<template>
  <view class="container">
    <view class="chart-container">
      <view class="chart-title">各院系报名人数统计</view>
      <view class="total-count">总报名人数：{{ totalCount }}人</view>
      <view class="chart" id="departmentChart"></view>
    </view>
    <view class="chart-container">
      <view class="chart-title">各院系报名率统计</view>
      <view class="chart" id="departmentRateChart"></view>
    </view>
    <view class="chart-container">
      <view class="chart-title">各年级报名情况统计</view>
      <view class="chart" id="gradeChart"></view>
    </view>
  </view>
</template>

<script>
import * as echarts from '@/static/libs/echarts/echarts.min.js'

export default {
  data() {
    return {
      chart: null,
      rateChart: null,
      gradeChart: null,
      departmentData: [],
      gradeData: [],
      totalCount: 0
    }
  },
  onLoad() {
    this.loadDepartmentRegistrationData()
    this.loadGradeRegistrationData()
  },
  methods: {
    async loadDepartmentRegistrationData() {
      try {
        const token = uni.getStorageSync('token')
        const [error, res] = await uni.request({
          url: 'http://localhost:8080/api/statistics/department-registration',
          method: 'GET',
          header: {
            'Authorization': `Bearer ${token}`
          }
        })

        if (error) {
          throw new Error(error.errMsg)
        }

        if (res.data.code === 200) {
          // 转换数据结构
          this.departmentData = res.data.data.map(item => ({
            name: item.院系名称,
            value: item.总报名次数,
            totalStudents: item.总学生数,
            registeredStudents: item.已报名学生数,
            rate: item.报名率
          }))
          // 计算总报名人数
          this.totalCount = this.departmentData.reduce((sum, item) => sum + item.value, 0)
          this.initChart()
          this.initRateChart()
        } else {
          throw new Error(res.data.message)
        }
      } catch (error) {
        console.error('获取院系报名数据失败:', error)
        uni.showToast({
          title: '获取数据失败',
          icon: 'none'
        })
      }
    },

    async loadGradeRegistrationData() {
      try {
        const token = uni.getStorageSync('token')
        const [error, res] = await uni.request({
          url: 'http://localhost:8080/api/statistics/grade-registration',
          method: 'GET',
          header: {
            'Authorization': `Bearer ${token}`
          }
        })

        if (error) {
          throw new Error(error.errMsg)
        }

        if (res.data.code === 200) {
          this.gradeData = res.data.data.map(item => ({
            grade: item.年级,
            totalStudents: item.总人数,
            registeredStudents: item.报名人数,
            totalRegistrations: item.报名总次数,
            rate: item.报名率
          }))
          this.initGradeChart()
        } else {
          throw new Error(res.data.message)
        }
      } catch (error) {
        console.error('获取年级报名数据失败:', error)
        uni.showToast({
          title: '获取数据失败',
          icon: 'none'
        })
      }
    },

    initChart() {
      // 初始化饼图
      this.chart = echarts.init(document.getElementById('departmentChart'))
      
      // 配置项
      const option = {
        tooltip: {
          trigger: 'item',
          formatter: function(params) {
            const data = params.data
            return `${data.name}报名人数：${data.value}人报名学生数：${data.registeredStudents}人总学生数：${data.totalStudents}人`
          }
        },
        legend: {
          orient: 'horizontal',
          bottom: '15%',
          left: 'center',
          itemWidth: 10,
          itemHeight: 10,
          itemGap: 20,
          textStyle: {
            fontSize: 12,
            color: '#666'
          },
          data: this.departmentData.map(item => item.name)
        },
        series: [
          {
            name: '报名人数',
            type: 'pie',
            radius: ['30%', '70%'],
            center: ['50%', '35%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: true,
              position: 'outside',
              formatter: function(params) {
                const data = params.data
                return `${data.name}\n${data.value}人`
              },
              fontSize: 14,
              fontWeight: 'bold'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: 16,
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: true,
              length: 10,
              length2: 10,
              smooth: true
            },
            data: this.departmentData
          }
        ]
      }

      // 使用配置项显示图表
      this.chart.setOption(option)
    },

    initRateChart() {
      // 初始化横向柱状图
      this.rateChart = echarts.init(document.getElementById('departmentRateChart'))
      
      // 配置项
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          },
          formatter: function(params) {
            const data = params[0].data
            return `${params[0].name}报名数：${data.registeredStudents}人，总数：${data.totalStudents}人`
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'value',
          axisLabel: {
            formatter: '{value}%'
          }
        },
        yAxis: {
          type: 'category',
          data: this.departmentData.map(item => item.name),
          axisLabel: {
            interval: 0,
            rotate: 30
          }
        },
        series: [
          {
            name: '报名率',
            type: 'bar',
            data: this.departmentData.map(item => ({
              value: item.rate,
              registeredStudents: item.registeredStudents,
              totalStudents: item.totalStudents
            })),
            label: {
              show: true,
              position: 'right',
              formatter: '{c}%'
            },
            itemStyle: {
              color: function(params) {
                const value = parseFloat(params.value)
                if (value >= 80) return '#91cc75'
                if (value >= 60) return '#fac858'
                if (value >= 40) return '#ee6666'
                return '#73c0de'
              }
            }
          }
        ]
      }

      // 使用配置项显示图表
      this.rateChart.setOption(option)
    },

    initGradeChart() {
      // 初始化折线图
      this.gradeChart = echarts.init(document.getElementById('gradeChart'))
      
      // 配置项
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross',
            label: {
              backgroundColor: '#6a7985'
            }
          },
          formatter: function(params) {
            const data = params[0].data
            return `${params[0].name}级<br/>
                    报名率：${data.rate}%<br/>
                    报名人数：${data.registeredStudents}人<br/>
                    总人数：${data.totalStudents}人<br/>
                    报名总次数：${data.totalRegistrations}次`
          }
        },
        legend: {
          data: ['报名率'],
          bottom: '0'
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '10%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: this.gradeData.map(item => item.grade + '级'),
          axisLabel: {
            interval: 0
          }
        },
        yAxis: {
          type: 'value',
          axisLabel: {
            formatter: '{value}%'
          }
        },
        series: [
          {
            name: '报名率',
            type: 'line',
            smooth: true,
            symbol: 'circle',
            symbolSize: 8,
            lineStyle: {
              width: 3
            },
            itemStyle: {
              color: '#5470c6'
            },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                {
                  offset: 0,
                  color: 'rgba(84,112,198,0.5)'
                },
                {
                  offset: 1,
                  color: 'rgba(84,112,198,0.1)'
                }
              ])
            },
            data: this.gradeData.map(item => ({
              value: item.rate,
              registeredStudents: item.registeredStudents,
              totalStudents: item.totalStudents,
              totalRegistrations: item.totalRegistrations,
              rate: item.rate
            }))
          }
        ]
      }

      // 使用配置项显示图表
      this.gradeChart.setOption(option)
    }
  },
  onUnload() {
    // 页面卸载时销毁图表实例
    if (this.chart) {
      this.chart.dispose()
    }
    if (this.rateChart) {
      this.rateChart.dispose()
    }
    if (this.gradeChart) {
      this.gradeChart.dispose()
    }
  }
}
</script>

<style>
.container {
  padding: 20px;
  background-color: #F8F8F8;
  min-height: 100vh;
}

.chart-container {
  background-color: #FFFFFF;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.chart-title {
  font-size: 18px;
  font-weight: bold;
  color: #333333;
  margin-bottom: 0px;
  text-align: center;
}

.total-count {
  font-size: 16px;
  color: #666666;
  text-align: center;
  margin-bottom: 0px;
}

.chart {
  width: 100%;
  height: 480px;
}

</style> 