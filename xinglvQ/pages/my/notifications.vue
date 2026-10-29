<template>
  <view class="page page-wrap">
    <view class="header" v-if="list.length">
      <text class="read-all" @tap="markAllRead">全部已读</text>
    </view>
    <view class="list" v-if="list.length">
      <view class="item" :class="{ unread: !n.isRead }" v-for="n in list" :key="n.id" @tap="openItem(n)">
        <view class="title">{{ n.title }}</view>
        <view class="content" v-if="n.content">{{ n.content }}</view>
        <view class="time">{{ formatTime(n.createTime) }}</view>
      </view>
    </view>
    <view class="empty-state" v-else-if="!loading">
      <view class="empty-icon">📬</view>
      <text>暂无消息</text>
      <button class="seed-btn" @tap="seedDemo">领取演示消息</button>
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
    this.load().then(() => uni.stopPullDownRefresh())
  },
  methods: {
    load() {
      this.loading = true
      return api.notifications.list().then(res => {
        this.list = Array.isArray(res) ? res : (res || [])
      }).catch(() => { this.list = [] }).finally(() => { this.loading = false })
    },
    seedDemo() {
      uni.showLoading({ title: '领取中...' })
      api.notifications.seedDemo().then(res => {
        this.list = Array.isArray(res) ? res : (res || [])
        uni.hideLoading()
        uni.showToast({ title: '已领取演示消息', icon: 'none' })
      }).catch(() => {
        uni.hideLoading()
        uni.showToast({ title: '领取失败', icon: 'none' })
      })
    },
    formatTime(t) {
      if (!t) return ''
      const d = new Date(t)
      const now = new Date()
      const diff = now - d
      if (diff < 60000) return '刚刚'
      if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
      if (d.toDateString() === now.toDateString()) return d.getHours() + ':' + String(d.getMinutes()).padStart(2, '0')
      return d.getFullYear() + '-' + (d.getMonth() + 1) + '-' + d.getDate()
    },
    openItem(n) {
      if (!n.isRead) api.notifications.markRead(n.id).then(() => { n.isRead = 1 })
      if (n.relatedType === 'chat' && n.relatedId) {
        // 讨论回复可跳转目的地/线路讨论区，需 topic 信息，这里仅标记已读
      }
    },
    markAllRead() {
      api.notifications.markAllRead().then(() => {
        this.list.forEach(n => { n.isRead = 1 })
        uni.showToast({ title: '已全部已读', icon: 'none' })
      })
    }
  }
}
</script>

<style scoped>
.page { padding: 0 24rpx 60rpx; background: #f5f5f0; min-height: 100vh; }
.header { padding: 20rpx 0; text-align: right; }
.read-all { font-size: 26rpx; color: #2d5a3d; }
.list { padding-bottom: 32rpx; }
.item { background: #fff; border-radius: 16rpx; padding: 28rpx; margin-bottom: 20rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,.06); }
.item.unread { border-left: 6rpx solid #2d5a3d; }
.title { font-size: 30rpx; font-weight: 600; color: #1a1a1a; margin-bottom: 8rpx; }
.content { font-size: 26rpx; color: #666; margin-bottom: 8rpx; }
.time { font-size: 24rpx; color: #999; }
.empty-state { padding: 80rpx 40rpx; text-align: center; color: #999; font-size: 28rpx; }
.empty-icon { font-size: 72rpx; margin-bottom: 24rpx; display: block; opacity: .6; }
.seed-btn { margin-top: 32rpx; background: #2d5a3d; color: #fff; border: none; border-radius: 12rpx; padding: 20rpx 48rpx; font-size: 28rpx; }
.loading-wrap { text-align: center; padding: 60rpx; color: #999; }
</style>
