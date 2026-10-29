<template>
  <view class="page page-wrap">
    <view class="hero">
      <view class="title">我的兑换 / 购买</view>
      <view class="desc">星星兑换与直接购买的记录，按时间倒序</view>
    </view>
    <view class="section-title" v-if="list.length">记录列表</view>
    <view class="list" v-if="list.length">
      <view class="card record-card" v-for="e in list" :key="e.id">
        <view class="row">
          <text class="item-name">{{ e.itemName || '商品' }}</text>
          <text class="type-tag" :class="e.type === 'purchase' ? 'buy' : 'star'">{{ e.type === 'purchase' ? '购买' : '星星兑换' }}</text>
        </view>
        <view class="row2">
          <text v-if="e.starsCost" class="cost">-{{ e.starsCost }} 星</text>
          <text v-if="e.amountPaid" class="cost">¥{{ e.amountPaid }}</text>
          <text class="time">{{ formatTime(e.createTime) }}</text>
        </view>
      </view>
    </view>
    <view class="empty-state" v-else-if="!loading">
      <view class="empty-icon">📋</view>
      <text>暂无兑换或购买记录</text>
      <text class="empty-hint">完成星星兑换或直接购买后，记录将显示在此</text>
      <text class="empty-tip">请下拉刷新或重新进入本页</text>
    </view>
    <view class="loading-wrap" v-else>加载中...</view>
  </view>
</template>

<script>
import api from '@/utils/api.js'
export default {
  data() {
    return { list: [], loading: true }
  },
  onShow() {
    this.load()
  },
  onPullDownRefresh() {
    this.load().then(() => {}).finally(() => {
      uni.stopPullDownRefresh()
    })
  },
  methods: {
    load() {
      this.loading = true
      return api.rewards.myRecords().then(res => {
        let arr = Array.isArray(res) ? res : (res && res.list ? res.list : [])
        if (arr && arr.length && typeof arr[0] !== 'object') arr = []
        this.list = arr || []
      }).catch(() => {
        this.list = []
      }).finally(() => {
        this.loading = false
      })
    },
    formatTime(t) {
      if (t == null || t === '') return '—'
      const d = new Date(t)
      if (isNaN(d.getTime())) return '—'
      const y = d.getFullYear()
      const m = String(d.getMonth() + 1).padStart(2, '0')
      const day = String(d.getDate()).padStart(2, '0')
      const h = String(d.getHours()).padStart(2, '0')
      const min = String(d.getMinutes()).padStart(2, '0')
      return y + '-' + m + '-' + day + ' ' + h + ':' + min
    }
  }
}
</script>

<style scoped>
.page { padding: 0; padding-bottom: 60rpx; }
.hero { padding: 28rpx 24rpx 20rpx; }
.title { font-size: 42rpx; font-weight: 700; color: #1a1a1a; margin-bottom: 10rpx; }
.desc { font-size: 28rpx; color: #666; line-height: 1.5; }
.section-title { margin: 0 24rpx 20rpx; padding-left: 16rpx; border-left: 6rpx solid #2d5a3d; font-size: 30rpx; font-weight: 700; color: #1a1a1a; }
.list { padding: 0 24rpx; }
.record-card { margin-bottom: 24rpx; padding: 28rpx; border-radius: 20rpx; box-shadow: 0 4rpx 20rpx rgba(45,90,61,.06); background: #fff; }
.row { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 14rpx; gap: 16rpx; }
.item-name { font-size: 30rpx; font-weight: 600; color: #1a1a1a; flex: 1; min-width: 0; word-break: break-all; line-height: 1.4; }
.type-tag { font-size: 24rpx; padding: 8rpx 16rpx; border-radius: 20rpx; flex-shrink: 0; }
.type-tag.star { background: linear-gradient(135deg, #f5e6c8, #e8d4a8); color: #6b5a2e; }
.type-tag.buy { background: #ffebee; color: #c62828; }
.row2 { display: flex; justify-content: space-between; align-items: center; font-size: 26rpx; color: #888; flex-wrap: wrap; gap: 8rpx; }
.cost { color: #2d5a3d; font-weight: 500; }
.time { color: #999; }
.empty-state { padding: 100rpx 48rpx; text-align: center; color: #666; font-size: 30rpx; }
.empty-state .empty-icon { font-size: 96rpx; margin-bottom: 28rpx; display: block; opacity: .5; }
.empty-hint { display: block; margin-top: 16rpx; font-size: 26rpx; color: #999; line-height: 1.5; }
.empty-tip { display: block; margin-top: 24rpx; font-size: 24rpx; color: #bbb; }
.loading-wrap { text-align: center; padding: 80rpx; color: #999; font-size: 28rpx; }
</style>
