<template>
  <view class="page page-wrap">
    <view class="hero">
      <view class="title">实践机会</view>
      <view class="desc">边旅行边实践，丰富假期 · 各地兼职、志愿者招募</view>
      <view class="filter-hint" v-if="destinationId">当前仅显示该目的地相关</view>
    </view>
    <view class="tabs">
      <view class="tab" :class="{ active: typeFilter === '' }" @tap="typeFilter = ''">全部</view>
      <view class="tab" :class="{ active: typeFilter === 'part_time' }" @tap="typeFilter = 'part_time'">兼职</view>
      <view class="tab" :class="{ active: typeFilter === 'volunteer' }" @tap="typeFilter = 'volunteer'">志愿</view>
    </view>
    <view class="list" v-if="filteredList.length">
      <view class="card card-base" v-for="o in filteredList" :key="o.id" @tap="goDetail(o.id)">
        <view class="tag" :class="o.type">{{ typeLabel(o.type) }}</view>
        <view class="rname">{{ o.title }}</view>
        <view class="rmeta" v-if="o.region">{{ o.region }}</view>
        <view class="rdesc" v-if="o.description">{{ o.description }}</view>
        <text class="arrow">></text>
      </view>
    </view>
    <view class="empty-state" v-else-if="!loading">
      <view class="empty-icon">📋</view>
      <text>暂无实践机会</text>
      <text class="empty-hint">稍后再来看看吧</text>
    </view>
    <view class="loading-wrap" v-else>加载中...</view>
  </view>
</template>

<script>
import api from '@/utils/api.js'
export default {
  data() {
    return { list: [], loading: true, typeFilter: '', destinationId: null }
  },
  computed: {
    filteredList() {
      if (!this.typeFilter) return this.list
      return this.list.filter(o => o.type === this.typeFilter)
    }
  },
  onLoad(op) {
    this.destinationId = op.destinationId || null
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
      const params = this.destinationId ? { destinationId: this.destinationId } : {}
      return api.opportunities.list(params).then(res => {
        this.list = res || []
      }).catch(() => {
        this.list = []
      }).finally(() => {
        this.loading = false
      })
    },
    typeLabel(t) {
      return t === 'volunteer' ? '志愿' : (t === 'part_time' ? '兼职' : '')
    },
    goDetail(id) {
      uni.navigateTo({ url: '/pages/opportunity/detail?id=' + id })
    }
  }
}
</script>

<style scoped>
.page { padding: 0; }
.hero { padding: 24rpx 24rpx 16rpx; }
.title { font-size: 40rpx; font-weight: 700; color: #1a1a1a; margin-bottom: 8rpx; }
.desc { font-size: 26rpx; color: #666; }
.filter-hint { font-size: 24rpx; color: #2d5a3d; margin-top: 8rpx; }
.tabs { display: flex; padding: 16rpx 24rpx; gap: 20rpx; }
.tab { padding: 12rpx 28rpx; border-radius: 24rpx; font-size: 26rpx; color: #666; background: #f0f0eb; }
.tab.active { background: #2d5a3d; color: #fff; }
.list { padding: 0 24rpx 60rpx; }
.card { padding: 28rpx; margin-bottom: 24rpx; position: relative; }
.tag { position: absolute; top: 28rpx; right: 28rpx; padding: 6rpx 16rpx; border-radius: 8rpx; font-size: 22rpx; }
.tag.part_time { background: #e8f4ea; color: #2d5a3d; }
.tag.volunteer { background: #f5e6c8; color: #6b5a2e; }
.rname { font-weight: 600; font-size: 30rpx; color: #1a1a1a; padding-right: 80rpx; }
.rmeta { font-size: 24rpx; color: #666; margin-top: 6rpx; }
.rdesc { font-size: 26rpx; color: #666; margin-top: 8rpx; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.arrow { position: absolute; right: 28rpx; bottom: 28rpx; color: #999; font-size: 28rpx; }
.empty-state { padding: 80rpx 40rpx; text-align: center; color: #999; font-size: 28rpx; }
.empty-icon { font-size: 80rpx; margin-bottom: 24rpx; display: block; opacity: .6; }
.empty-hint { display: block; margin-top: 12rpx; font-size: 24rpx; color: #bbb; }
.loading-wrap { text-align: center; padding: 60rpx; color: #999; }
</style>
