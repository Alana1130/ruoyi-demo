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
    }
  },
  data() {
    return {
      chart: null
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
      this.setOptions()
    },
    setOptions() {
      this.chart.setOption({
        tooltip: {
          trigger: 'item',
          backgroundColor: 'rgba(255, 255, 255, 0.95)',
          borderColor: '#e4e7ed',
          borderWidth: 1,
          textStyle: {
            color: '#606266'
          },
          padding: [10, 15],
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          right: '5%',
          top: 'center',
          itemWidth: 12,
          itemHeight: 12,
          itemGap: 16,
          textStyle: {
            color: '#606266',
            fontSize: 13
          },
          formatter: function(name) {
            return name
          }
        },
        series: [
          {
            name: '消费分类',
            type: 'pie',
            radius: ['45%', '70%'],
            center: ['35%', '50%'],
            avoidLabelOverlap: true,
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: 16,
                fontWeight: 'bold'
              },
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.3)'
              }
            },
            labelLine: {
              show: false
            },
            data: [
              {
                value: 3200,
                name: '餐饮',
                itemStyle: {
                  color: {
                    type: 'linear',
                    x: 0, y: 0, x2: 1, y2: 1,
                    colorStops: [
                      { offset: 0, color: '#667eea' },
                      { offset: 1, color: '#764ba2' }
                    ]
                  }
                }
              },
              {
                value: 2400,
                name: '交通',
                itemStyle: {
                  color: {
                    type: 'linear',
                    x: 0, y: 0, x2: 1, y2: 1,
                    colorStops: [
                      { offset: 0, color: '#f093fb' },
                      { offset: 1, color: '#f5576c' }
                    ]
                  }
                }
              },
              {
                value: 1490,
                name: '住宿',
                itemStyle: {
                  color: {
                    type: 'linear',
                    x: 0, y: 0, x2: 1, y2: 1,
                    colorStops: [
                      { offset: 0, color: '#4facfe' },
                      { offset: 1, color: '#00f2fe' }
                    ]
                  }
                }
              },
              {
                value: 1000,
                name: '门票',
                itemStyle: {
                  color: {
                    type: 'linear',
                    x: 0, y: 0, x2: 1, y2: 1,
                    colorStops: [
                      { offset: 0, color: '#43e97b' },
                      { offset: 1, color: '#38f9d7' }
                    ]
                  }
                }
              },
              {
                value: 590,
                name: '其他',
                itemStyle: {
                  color: {
                    type: 'linear',
                    x: 0, y: 0, x2: 1, y2: 1,
                    colorStops: [
                      { offset: 0, color: '#feca57' },
                      { offset: 1, color: '#ff9f43' }
                    ]
                  }
                }
              }
            ],
            animationType: 'scale',
            animationEasing: 'elasticOut',
            animationDuration: 2000
          }
        ]
      })
    }
  }
}
</script>
