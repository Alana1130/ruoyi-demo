<template>
  <div :class="className" :style="{height:height,width:width}" />
</template>

<script>
import * as echarts from 'echarts'
import resize from './mixins/resize'

export default {
  mixins: [resize],
  props: {
    className: {
      type: String,
      default: 'chart'
    },
    width: {
      type: String,
      default: '100%'
    },
    height: {
      type: String,
      default: '320px'
    },
    autoResize: {
      type: Boolean,
      default: true
    },
    chartData: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      chart: null
    }
  },
  watch: {
    chartData: {
      deep: true,
      handler(val) {
        this.setOptions(val)
      }
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initChart()
    })
  },
  beforeDestroy() {
    if (!this.chart) {
      return
    }
    this.chart.dispose()
    this.chart = null
  },
  methods: {
    initChart() {
      this.chart = echarts.init(this.$el)
      this.setOptions(this.chartData)
    },
    setOptions({ expectedData, actualData } = {}) {
      this.chart.setOption({
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          top: '15%',
          containLabel: true
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross',
            crossStyle: {
              color: '#999'
            }
          },
          backgroundColor: 'rgba(255, 255, 255, 0.95)',
          borderColor: '#e4e7ed',
          borderWidth: 1,
          textStyle: {
            color: '#606266'
          },
          padding: [10, 15]
        },
        legend: {
          data: ['预算', '实际'],
          top: 0,
          textStyle: {
            color: '#606266'
          },
          itemWidth: 20,
          itemHeight: 10
        },
        xAxis: {
          type: 'category',
          data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
          boundaryGap: false,
          axisLine: {
            lineStyle: {
              color: '#e4e7ed'
            }
          },
          axisLabel: {
            color: '#909399'
          },
          axisTick: {
            show: false
          }
        },
        yAxis: {
          type: 'value',
          axisLine: {
            show: false
          },
          axisTick: {
            show: false
          },
          axisLabel: {
            color: '#909399'
          },
          splitLine: {
            lineStyle: {
              color: '#f0f2f5'
            }
          }
        },
        series: [
          {
            name: '预算',
            type: 'line',
            smooth: true,
            symbol: 'circle',
            symbolSize: 8,
            data: expectedData,
            itemStyle: {
              color: '#667eea',
              borderWidth: 2
            },
            lineStyle: {
              color: '#667eea',
              width: 3
            },
            areaStyle: {
              color: {
                type: 'linear',
                x: 0,
                y: 0,
                x2: 0,
                y2: 1,
                colorStops: [
                  { offset: 0, color: 'rgba(102, 126, 234, 0.25)' },
                  { offset: 1, color: 'rgba(102, 126, 234, 0.02)' }
                ]
              }
            },
            animationDuration: 2000,
            animationEasing: 'cubicInOut'
          },
          {
            name: '实际',
            type: 'line',
            smooth: true,
            symbol: 'circle',
            symbolSize: 8,
            data: actualData,
            itemStyle: {
              color: '#f093fb',
              borderWidth: 2
            },
            lineStyle: {
              color: '#f093fb',
              width: 3
            },
            areaStyle: {
              color: {
                type: 'linear',
                x: 0,
                y: 0,
                x2: 0,
                y2: 1,
                colorStops: [
                  { offset: 0, color: 'rgba(240, 147, 251, 0.25)' },
                  { offset: 1, color: 'rgba(240, 147, 251, 0.02)' }
                ]
              }
            },
            animationDuration: 2000,
            animationEasing: 'cubicInOut'
          }
        ]
      })
    }
  }
}
</script>
