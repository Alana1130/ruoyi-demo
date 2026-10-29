<template>
  <view class="page">
    <view class="hero">
      <view class="hero-kicker">MY FOOTPRINTS</view>
      <view class="title">我的点亮</view>
      <view class="desc">到访目的地后点亮即可获得星星，用于兑换文旅或公益好物</view>
    </view>
    <view class="section-title" v-if="list.length">点亮记录<text class="section-desc">共 {{ list.length }} 次</text></view>
    <view class="list" v-if="list.length">
      <view class="card card-base" v-for="c in list" :key="c.id">
        <view class="left">
          <view class="name">{{ c.destinationName }}</view>
          <view class="meta">{{ c.region }} · 获得 {{ c.starsEarned }} 星</view>
        </view>
        <view class="stars-badge">+{{ c.starsEarned }}</view>
        <view class="link" @tap="goDest(c.destinationId)">再看 ›</view>
      </view>
    </view>
    <view class="empty-state" v-else-if="!loading">
      <view class="empty-icon">✨</view>
      <text>暂无点亮记录</text>
      <text class="empty-hint">去目的地详情页点亮即可获得星星</text>
      <view class="empty-btn" @tap="goIndex">去探索目的地</view>
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
  methods: {
    load() {
      this.loading = true
      api.checkin.my().then(res => {
        this.list = res || []
      }).catch(() => {
        this.list = []
      }).finally(() => {
        this.loading = false
      })
    },
    goDest(id) {
      uni.navigateTo({ url: '/pages/destination/detail?id=' + id })
    },
    goIndex() {
      uni.switchTab({ url: '/pages/index/index' })
    }
  }
}
</script>

<style scoped>
.page { padding: 20rpx 24rpx 60rpx; background: var(--xlv-paper); min-height: 100vh; }
.hero { position: relative; overflow: hidden; border-radius: var(--xlv-radius-lg); background: linear-gradient(158deg, #2F5A44 0%, #1F3D2F 66%, #172F24 100%); padding: 42rpx 32rpx 38rpx; box-shadow: 0 18rpx 44rpx rgba(22,33,28,.18); }
.hero::after { content: ''; position: absolute; right: -130rpx; top: -150rpx; width: 400rpx; height: 400rpx; border-radius: 50%; background: radial-gradient(circle, rgba(201,162,39,.4) 0%, rgba(201,162,39,0) 68%); }
.hero-kicker { font-size: 21rpx; letter-spacing: 3rpx; color: rgba(255,255,255,.5); margin-bottom: 12rpx; }
.title { font-size: 44rpx; font-weight: 700; color: #fff; letter-spacing: 2rpx; margin-bottom: 12rpx; }
.desc { font-size: 24rpx; color: rgba(255,255,255,.68); line-height: 1.55; }
.card { display: flex; align-items: center; justify-content: space-between; padding: 28rpx; margin-bottom: 20rpx; }
.left { flex: 1; min-width: 0; }
.name { font-weight: 600; font-size: 29rpx; color: var(--xlv-ink); }
.meta { font-size: 23rpx; color: var(--xlv-muted); margin-top: 8rpx; }
.stars-badge { background: linear-gradient(135deg, #F7ECD0, #E9D5A4); color: #7A6320; padding: 8rpx 20rpx; border-radius: 999rpx; font-size: 23rpx; font-weight: 700; margin-right: 18rpx; }
.link { color: var(--xlv-gold); font-size: 24rpx; font-weight: 600; }
.empty-btn { display: inline-block; margin-top: 32rpx; padding: 18rpx 40rpx; border-radius: 999rpx; color: #fff; background: linear-gradient(135deg, #3A6A4B, #23503A); font-size: 26rpx; font-weight: 600; }
.loading-wrap { text-align: center; padding: 60rpx; color: var(--xlv-muted); }
</style>
