<template>
  <view class="page page-wrap">
    <view class="search-bar">
      <text class="search-icon">🔍</text>
      <input class="search-input" v-model="keyword" placeholder="搜目的地、实践机会" placeholder-class="ph" focus @confirm="doSearch" @input="onInput" />
      <text class="cancel" v-if="keyword" @tap="keyword = ''">清空</text>
    </view>
    <view class="result" v-if="searched">
      <view class="block" v-if="destinations.length">
        <view class="block-title">目的地</view>
        <view class="card" v-for="d in destinations" :key="d.id" @tap="goDest(d.id)">
          <text class="name">{{ d.name }}</text>
          <text class="meta" v-if="d.region">{{ d.region }}</text>
        </view>
      </view>
      <view class="block" v-if="opportunities.length">
        <view class="block-title">实践机会</view>
        <view class="card" v-for="o in opportunities" :key="o.id" @tap="goOpp(o.id)">
          <view class="row">
            <text class="tag" :class="o.type">{{ o.type === 'volunteer' ? '志愿' : '兼职' }}</text>
            <text class="name">{{ o.title }}</text>
          </view>
          <text class="meta" v-if="o.region">{{ o.region }}</text>
        </view>
      </view>
      <view class="empty-mini" v-if="searched && !destinations.length && !opportunities.length">无相关结果</view>
    </view>
    <view class="hint" v-else-if="!loading">输入关键词搜索目的地与实践机会</view>
    <view class="loading-wrap" v-if="loading">搜索中...</view>
  </view>
</template>

<script>
import api from '@/utils/api.js'
export default {
  data() {
    return { keyword: '', destinations: [], opportunities: [], searched: false, loading: false }
  },
  onLoad(op) {
    if (op.keyword) this.keyword = op.keyword
    if (this.keyword.trim()) this.doSearch()
  },
  methods: {
    onInput() {
      if (!this.keyword.trim()) this.searched = false
    },
    doSearch() {
      const k = this.keyword.trim()
      if (!k) return
      this.loading = true
      this.searched = true
      api.search.keyword(k).then(res => {
        this.destinations = res.destinations || []
        this.opportunities = res.opportunities || []
      }).catch(() => {
        this.destinations = []
        this.opportunities = []
      }).finally(() => { this.loading = false })
    },
    goDest(id) {
      uni.navigateTo({ url: '/pages/destination/detail?id=' + id })
    },
    goOpp(id) {
      uni.navigateTo({ url: '/pages/opportunity/detail?id=' + id })
    }
  }
}
</script>

<style scoped>
.page { padding: 24rpx; padding-bottom: 60rpx; background: var(--xlv-paper); min-height: 100vh; }
.search-bar { display: flex; align-items: center; padding: 22rpx 24rpx; background: #fff; border-radius: 999rpx; margin-bottom: 28rpx; border: 1rpx solid rgba(31,61,47,.06); box-shadow: 0 8rpx 24rpx rgba(22,33,28,.06); }
.search-icon { font-size: 30rpx; margin-right: 14rpx; opacity: .55; }
.search-input { flex: 1; font-size: 27rpx; height: 44rpx; color: var(--xlv-ink); }
.ph { color: var(--xlv-muted); }
.cancel { font-size: 24rpx; color: var(--xlv-gold); margin-left: 16rpx; font-weight: 600; }
.block-title { font-size: 28rpx; font-weight: 700; color: var(--xlv-ink); margin: 28rpx 0 18rpx; padding-left: 18rpx; border-left: 6rpx solid var(--xlv-gold); line-height: 1.3; }
.card { background: #fff; border-radius: var(--xlv-radius-lg); padding: 26rpx; margin-bottom: 18rpx; border: 1rpx solid rgba(31,61,47,.06); box-shadow: 0 6rpx 20rpx rgba(22,33,28,.04); }
.name { font-size: 29rpx; font-weight: 600; color: var(--xlv-ink); display: block; }
.meta { font-size: 23rpx; color: var(--xlv-muted); margin-top: 8rpx; display: block; }
.row { display: flex; align-items: center; gap: 12rpx; }
.tag { font-size: 21rpx; padding: 5rpx 14rpx; border-radius: 999rpx; }
.tag.part_time { background: rgba(45,90,61,.09); color: var(--xlv-forest-2); }
.tag.volunteer { background: var(--xlv-gold-soft); color: #7A6320; }
.empty-mini { text-align: center; padding: 40rpx; color: var(--xlv-muted); font-size: 26rpx; }
.hint { text-align: center; padding: 80rpx 40rpx; color: var(--xlv-muted); font-size: 26rpx; }
.loading-wrap { text-align: center; padding: 40rpx; color: var(--xlv-muted); }
</style>
