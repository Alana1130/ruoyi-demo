<template>
  <view class="page page-wrap">
    <view class="loading-wrap" v-if="loading">加载中...</view>
    <template v-else-if="data">
      <view class="card head card-base">
        <view class="tag" :class="data.type">{{ typeLabel(data.type) }}</view>
        <view class="title">{{ data.title }}</view>
        <view class="meta" v-if="data.region">{{ data.region }}</view>
        <view class="go-dest" v-if="data.destinationId" @tap="goDest(data.destinationId)">查看该目的地 →</view>
      </view>
      <view class="card card-base" v-if="data.description">
        <view class="sec-title">说明</view>
        <view class="body">{{ data.description }}</view>
      </view>
      <view class="card card-base" v-if="data.contactInfo">
        <view class="sec-title">报名 / 联系方式</view>
        <view class="body contact">{{ data.contactInfo }}</view>
      </view>
    </template>
    <view class="empty-state" v-else-if="!loading">
      <text>内容不存在或已下架</text>
    </view>
  </view>
</template>

<script>
import api from '@/utils/api.js'
export default {
  data() {
    return { id: null, data: null, loading: true }
  },
  onLoad(op) {
    this.id = op.id
    this.load()
  },
  methods: {
    typeLabel(t) {
      return t === 'volunteer' ? '志愿' : (t === 'part_time' ? '兼职' : '')
    },
    load() {
      if (!this.id) return
      this.loading = true
      api.opportunities.get(this.id).then(res => {
        this.data = res
      }).catch(() => {
        this.data = null
      }).finally(() => {
        this.loading = false
      })
    },
    goDest(id) {
      uni.navigateTo({ url: '/pages/destination/detail?id=' + id })
    }
  }
}
</script>

<style scoped>
.page { padding: 0 24rpx 60rpx; }
.loading-wrap { text-align: center; padding: 60rpx; color: #999; }
.card { padding: 28rpx; margin-bottom: 24rpx; }
.head { position: relative; }
.tag { display: inline-block; padding: 8rpx 20rpx; border-radius: 8rpx; font-size: 24rpx; margin-bottom: 16rpx; }
.tag.part_time { background: #e8f4ea; color: #2d5a3d; }
.tag.volunteer { background: #f5e6c8; color: #6b5a2e; }
.title { font-size: 36rpx; font-weight: 700; color: #1a1a1a; }
.meta { font-size: 26rpx; color: #666; margin-top: 12rpx; }
.go-dest { margin-top: 20rpx; color: #2d5a3d; font-size: 28rpx; }
.sec-title { font-size: 28rpx; font-weight: 600; color: #333; margin-bottom: 16rpx; }
.body { font-size: 28rpx; color: #666; line-height: 1.6; white-space: pre-wrap; word-break: break-all; }
.body.contact { color: #2d5a3d; }
.empty-state { padding: 80rpx 40rpx; text-align: center; color: #999; }
</style>
