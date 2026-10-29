<template>
  <view class="page page-wrap">
    <view class="sec-title">选择目的地</view>
    <view class="list" v-if="destList.length">
      <view class="item" v-for="d in destList" :key="d.id" @tap="selectDest(d)">
        <view class="name">{{ d.name }}</view>
        <view class="meta" v-if="d.region">{{ d.region }}</view>
        <text class="arrow">></text>
      </view>
    </view>
    <view class="sec-title" v-if="selectedDest">打车 / 出行 · {{ selectedDest.name }}</view>
    <view class="cards" v-if="selectedDest && taxis.length">
      <view class="card card-base" v-for="t in taxis" :key="t.id">
        <view class="rname">{{ t.name }}</view>
        <view class="rmeta" v-if="t.appType">{{ t.appType }}</view>
        <view class="rdesc" v-if="t.description">{{ t.description }}</view>
        <view class="rdesc" v-if="t.priceApprox">参考：{{ t.priceApprox }}</view>
        <view class="link-wrap" v-if="t.link">
          <text class="link" @tap="openLink(t.link)">打开叫车</text>
        </view>
      </view>
    </view>
    <view class="empty-mini" v-else-if="selectedDest && !loadingTaxi">该地暂无打车信息</view>
    <view class="loading-wrap" v-if="loading">加载中...</view>
  </view>
</template>

<script>
import api from '@/utils/api.js'
export default {
  data() {
    return { destList: [], selectedDest: null, taxis: [], loading: true, loadingTaxi: false }
  },
  onLoad(op) {
    const destId = op.destId || op.destinationId
    api.destinations.list().then(res => {
      this.destList = res || []
      this.loading = false
      if (destId && this.destList.length) {
        const d = this.destList.find(x => x.id == destId)
        if (d) this.selectDest(d)
      }
    }).catch(() => { this.destList = []; this.loading = false })
  },
  methods: {
    selectDest(d) {
      this.selectedDest = d
      this.loadingTaxi = true
      api.taxi.list(d.id).then(res => { this.taxis = res || [] }).catch(() => { this.taxis = [] }).finally(() => { this.loadingTaxi = false })
    },
    openLink(url) {
      if (!url) return
      if (url.indexOf('http') === 0) {
        // #ifdef H5
        window.open(url)
        // #endif
        // #ifdef MP-WEIXIN
        uni.setClipboardData({ data: url })
        uni.showToast({ title: '链接已复制', icon: 'none' })
        // #endif
      }
    }
  }
}
</script>

<style scoped>
.page { padding: 24rpx; padding-bottom: 60rpx; }
.sec-title { font-size: 30rpx; font-weight: 600; margin: 24rpx 0 16rpx; color: #1a1a1a; }
.list { margin-bottom: 24rpx; }
.item { display: flex; align-items: center; padding: 28rpx; background: #fff; border-radius: 16rpx; margin-bottom: 16rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,.06); }
.name { flex: 1; font-weight: 600; font-size: 30rpx; }
.meta { font-size: 24rpx; color: #666; }
.arrow { color: #999; }
.cards .card { margin-bottom: 20rpx; padding: 28rpx; }
.rname { font-weight: 600; font-size: 30rpx; }
.rmeta { font-size: 24rpx; color: #666; margin-top: 6rpx; }
.rdesc { font-size: 26rpx; color: #666; margin-top: 8rpx; }
.link-wrap { margin-top: 12rpx; }
.link { color: #2d5a3d; font-size: 26rpx; }
.empty-mini { font-size: 26rpx; color: #999; padding: 16rpx; }
.loading-wrap { text-align: center; padding: 60rpx; color: #999; }
</style>
