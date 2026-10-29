<template>
  <view class="page">
    <view class="tabs">
      <view class="tab" :class="{ active: currentTag === '' }" @tap="currentTag = ''">全部</view>
      <view class="tab" v-for="t in topicTags" :key="t" :class="{ active: currentTag === t }" @tap="currentTag = t">{{ t }}</view>
    </view>
    <view class="list" v-if="list.length">
      <view class="card" v-for="item in list" :key="item.id" @tap="goDetail(item.id)">
        <view class="card-title">{{ item.title || (item.content ? item.content.slice(0, 30) + (item.content.length > 30 ? '…' : '') : '攻略') }}</view>
        <view class="card-meta" v-if="item.destinationName">{{ item.destinationName }}</view>
        <view class="tags" v-if="item.tags">
          <text class="tag" v-for="(tag, i) in parseTags(item.tags)" :key="i">{{ tag }}</text>
        </view>
        <view class="card-content" v-if="item.content">{{ item.content.slice(0, 80) }}{{ item.content.length > 80 ? '…' : '' }}</view>
        <view class="card-footer">
          <text class="author">{{ item.nickname || '旅行者' }}</text>
          <text class="time">{{ formatTime(item.createTime) }}</text>
        </view>
      </view>
    </view>
    <view class="empty" v-else-if="!loading">
      <text>暂无攻略，去目的地写一篇吧</text>
      <view class="link" @tap="goPublish">写攻略</view>
    </view>
    <view class="loading-wrap" v-else>加载中...</view>
    <view class="fab" @tap="goPublish">写攻略</view>
  </view>
</template>

<script>
import api from '@/utils/api.js'
const TOPIC_TAGS = ['穷游', '周末两日游', '独自旅行', '毕业旅行', '情侣游', '摄影', '美食']
export default {
  data() {
    return {
      list: [],
      loading: false,
      currentTag: '',
      topicTags: TOPIC_TAGS
    }
  },
  watch: {
    currentTag() {
      this.load()
    }
  },
  onLoad() {
    this.load()
  },
  onPullDownRefresh() {
    this.load().then(() => uni.stopPullDownRefresh())
  },
  methods: {
    load() {
      this.loading = true
      const params = {}
      if (this.currentTag) params.tag = this.currentTag
      return api.guides.list(params).then(res => {
        this.list = res || []
      }).catch(() => {
        this.list = []
      }).finally(() => {
        this.loading = false
      })
    },
    parseTags(s) {
      if (!s || typeof s !== 'string') return []
      return s.split(',').map(t => t.trim()).filter(Boolean)
    },
    formatTime(t) {
      if (!t) return ''
      const d = typeof t === 'string' ? new Date(t) : t
      const now = new Date()
      const diff = now - d
      if (diff < 86400000) return (d.getHours() < 10 ? '0' : '') + d.getHours() + ':' + (d.getMinutes() < 10 ? '0' : '') + d.getMinutes()
      return (d.getMonth() + 1) + '-' + d.getDate()
    },
    goDetail(id) {
      uni.navigateTo({ url: '/pages/guide/detail?id=' + id })
    },
    goPublish() {
      uni.navigateTo({ url: '/pages/guide/publish' })
    }
  }
}
</script>

<style scoped>
.page { padding: 24rpx; padding-bottom: 120rpx; background: #f2f4f3; min-height: 100vh; }
.tabs { display: flex; flex-wrap: wrap; gap: 16rpx; margin-bottom: 24rpx; }
.tab { padding: 14rpx 24rpx; background: #fff; border-radius: 24rpx; font-size: 26rpx; color: #666; }
.tab.active { background: #2d5a3d; color: #fff; }
.list { display: flex; flex-direction: column; gap: 20rpx; }
.card { background: #fff; border-radius: 20rpx; padding: 28rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,.06); }
.card-title { font-size: 32rpx; font-weight: 600; color: #1a1a1a; margin-bottom: 12rpx; }
.card-meta { font-size: 24rpx; color: #2d5a3d; margin-bottom: 8rpx; }
.tags { margin-bottom: 12rpx; }
.tag { display: inline-block; margin-right: 12rpx; margin-bottom: 8rpx; padding: 6rpx 16rpx; background: #e8f0ea; color: #2d5a3d; font-size: 22rpx; border-radius: 8rpx; }
.card-content { font-size: 26rpx; color: #666; margin-bottom: 16rpx; line-height: 1.5; }
.card-footer { font-size: 24rpx; color: #999; }
.author { color: #2d5a3d; margin-right: 16rpx; }
.empty { text-align: center; padding: 80rpx 40rpx; color: #999; font-size: 28rpx; }
.empty .link { color: #2d5a3d; margin-top: 16rpx; display: inline-block; }
.loading-wrap { text-align: center; padding: 60rpx; color: #999; }
.fab { position: fixed; right: 32rpx; bottom: 120rpx; width: 100rpx; height: 100rpx; background: #2d5a3d; color: #fff; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 28rpx; box-shadow: 0 4rpx 20rpx rgba(45,90,61,.4); }
</style>
