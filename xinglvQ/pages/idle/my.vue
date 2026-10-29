<template>
  <view class="page">
    <view class="list" v-if="list.length">
      <view class="card" v-for="item in list" :key="item.id" @tap="goDetail(item.id)">
        <view class="img-wrap" v-if="firstImg(item)"><image :src="firstImg(item)" mode="aspectFill" class="thumb" /></view>
        <view class="img-wrap placeholder" v-else><text class="icon">📦</text></view>
        <view class="info">
          <text class="title">{{ item.title }}</text>
          <text class="meta">⭐ {{ item.priceStars }} 星星 · {{ item.statusText }}</text>
        </view>
      </view>
    </view>
    <view class="empty" v-else-if="!loading">暂无发布，去发布闲置吧</view>
    <view class="loading" v-else>加载中...</view>
    <view class="fab" @tap="goPublish">+ 发布闲置</view>
  </view>
</template>

<script>
import api from '@/utils/api.js'
export default {
  data() { return { list: [], loading: false } },
  onLoad() { this.load() },
  onPullDownRefresh() { this.load().then(() => uni.stopPullDownRefresh()) },
  onShow() { this.load() },
  methods: {
    load() {
      this.loading = true
      api.idle.my().then(res => { this.list = res || [] }).catch(() => { this.list = [] }).finally(() => { this.loading = false })
    },
    firstImg(item) {
      const s = item.images || ''
      const u = s.split(',')[0].trim()
      return u || ''
    },
    goDetail(id) { uni.navigateTo({ url: '/pages/idle/detail?id=' + id }) },
    goPublish() { uni.navigateTo({ url: '/pages/idle/publish' }) }
  }
}
</script>

<style scoped>
.page { padding: 24rpx; padding-bottom: 120rpx; background: #f2f4f3; min-height: 100vh; }
.list { display: flex; flex-direction: column; gap: 20rpx; }
.card { display: flex; background: #fff; border-radius: 16rpx; overflow: hidden; box-shadow: 0 4rpx 16rpx rgba(0,0,0,.06); }
.img-wrap { width: 180rpx; height: 180rpx; flex-shrink: 0; background: #f0f0f0; display: flex; align-items: center; justify-content: center; }
.img-wrap.placeholder .icon { font-size: 56rpx; }
.thumb { width: 100%; height: 100%; }
.info { flex: 1; padding: 24rpx; display: flex; flex-direction: column; justify-content: center; }
.title { font-size: 30rpx; font-weight: 600; margin-bottom: 8rpx; }
.meta { font-size: 26rpx; color: #666; }
.fab { position: fixed; right: 24rpx; bottom: 80rpx; padding: 24rpx 32rpx; background: #2d5a3d; color: #fff; border-radius: 48rpx; font-size: 28rpx; box-shadow: 0 8rpx 24rpx rgba(45,90,61,.3); }
.empty, .loading { text-align: center; padding: 80rpx; color: #999; }
</style>
