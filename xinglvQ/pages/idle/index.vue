<template>
  <view class="page">
    <view class="tabs">
      <view class="tab" :class="{ active: category === '' }" @tap="category = ''; load()">全部</view>
      <view class="tab" :class="{ active: category === 'textbook' }" @tap="category = 'textbook'; load()">教材</view>
      <view class="tab" :class="{ active: category === 'travel_gear' }" @tap="category = 'travel_gear'; load()">出行装备</view>
      <view class="tab" :class="{ active: category === 'other' }" @tap="category = 'other'; load()">其他</view>
    </view>
    <view class="list" v-if="list.length">
      <view class="card" v-for="item in list" :key="item.id" @tap="goDetail(item.id)">
        <view class="img-wrap" v-if="firstImg(item)"><image :src="firstImg(item)" mode="aspectFill" class="thumb" /></view>
        <view class="img-wrap placeholder" v-else><text class="icon">📦</text></view>
        <view class="info">
          <text class="title">{{ item.title }}</text>
          <text class="stars">⭐ {{ item.priceStars }} 星星</text>
        </view>
      </view>
    </view>
    <view class="empty" v-else-if="!loading">暂无闲置，去发布一个吧</view>
    <view class="loading" v-else>加载中...</view>
    <view class="bottom-actions">
      <button class="btn outline" @tap="goMy">我的闲置</button>
      <button class="btn primary" @tap="goPublish">发布闲置</button>
    </view>
  </view>
</template>

<script>
import api from '@/utils/api.js'
export default {
  data() { return { list: [], category: '', loading: false } },
  onLoad() { this.load() },
  onPullDownRefresh() { this.load().then(() => uni.stopPullDownRefresh()) },
  methods: {
    load() {
      this.loading = true
      const params = { limit: 30 }
      if (this.category) params.category = this.category
      api.idle.list(params).then(res => { this.list = res || [] }).catch(() => { this.list = [] }).finally(() => { this.loading = false })
    },
    firstImg(item) {
      const s = item.images || ''
      if (!s) return ''
      const u = s.split(',')[0].trim()
      return u || ''
    },
    goDetail(id) { uni.navigateTo({ url: '/pages/idle/detail?id=' + id }) },
    goMy() { uni.navigateTo({ url: '/pages/idle/my' }) },
    goPublish() { uni.navigateTo({ url: '/pages/idle/publish' }) }
  }
}
</script>

<style scoped>
.page { padding: 24rpx; padding-bottom: 140rpx; background: #f2f4f3; min-height: 100vh; }
.tabs { display: flex; gap: 16rpx; margin-bottom: 24rpx; flex-wrap: wrap; }
.tab { padding: 14rpx 28rpx; background: #fff; border-radius: 24rpx; font-size: 28rpx; color: #666; }
.tab.active { background: #2d5a3d; color: #fff; }
.list { display: flex; flex-wrap: wrap; gap: 20rpx; }
.card { width: calc(50% - 10rpx); background: #fff; border-radius: 16rpx; overflow: hidden; box-shadow: 0 4rpx 16rpx rgba(0,0,0,.06); }
.img-wrap { height: 200rpx; background: #f0f0f0; display: flex; align-items: center; justify-content: center; }
.img-wrap.placeholder .icon { font-size: 64rpx; }
.thumb { width: 100%; height: 100%; }
.info { padding: 20rpx; }
.title { font-size: 28rpx; font-weight: 600; display: block; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.stars { font-size: 24rpx; color: #2d5a3d; margin-top: 8rpx; display: block; }
.bottom-actions { position: fixed; left: 0; right: 0; bottom: 0; padding: 24rpx; background: #fff; border-top: 1rpx solid #eee; display: flex; gap: 24rpx; }
.btn { flex: 1; height: 88rpx; line-height: 88rpx; border-radius: 16rpx; font-size: 30rpx; }
.btn.outline { background: #fff; color: #2d5a3d; border: 1rpx solid #2d5a3d; }
.btn.primary { background: #2d5a3d; color: #fff; }
.empty, .loading { text-align: center; padding: 80rpx; color: #999; }
</style>
