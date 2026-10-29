<template>
  <view class="page">
    <view class="toolbar">
      <input class="search-inp" v-model="keyword" placeholder="搜标题、内容" @confirm="load" />
      <view class="btn-wrap">
        <button class="btn primary" @tap="goPublish">发布约伴</button>
      </view>
    </view>
    <view class="filters">
      <picker mode="date" :value="dateFrom" @change="onDateFromChange">
        <view class="filter-item">出发 {{ dateFrom || '不限' }}</view>
      </picker>
      <picker mode="date" :value="dateTo" @change="onDateToChange">
        <view class="filter-item">至 {{ dateTo || '不限' }}</view>
      </picker>
      <view class="filter-item" @tap="showDestPicker = true">{{ destinationName || '目的地不限' }}</view>
    </view>
    <view class="list" v-if="list.length">
      <view class="card" v-for="item in list" :key="item.id" @tap="goDetail(item.id)">
        <view class="card-title">{{ item.title }}</view>
        <view class="card-meta" v-if="item.destinationName">{{ item.destinationName }}</view>
        <view class="card-meta" v-if="item.travelDateStart || item.budgetRange">
          <text v-if="item.travelDateStart">{{ formatDate(item.travelDateStart) }}</text>
          <text v-if="item.budgetRange"> · {{ item.budgetRange }}</text>
        </view>
        <view class="card-content" v-if="item.content">{{ item.content }}</view>
        <view class="card-footer">
          <text class="author">{{ item.nickname || '旅行者' }}</text>
          <text class="time">{{ formatTime(item.createTime) }}</text>
        </view>
      </view>
    </view>
    <view class="empty" v-else-if="!loading">
      <text>暂无约伴，来发一条吧</text>
      <button class="btn primary" @tap="goPublish">发布约伴</button>
    </view>
    <view class="loading-wrap" v-else>加载中...</view>

    <view class="dest-picker-mask" v-if="showDestPicker" @tap="showDestPicker = false">
      <view class="dest-picker" @tap.stop>
        <view class="picker-title">选择目的地</view>
        <scroll-view scroll-y class="dest-list">
          <view class="dest-opt" @tap="selectDest(null, '不限')">不限</view>
          <view class="dest-opt" v-for="d in destinations" :key="d.id" @tap="selectDest(d.id, d.name)">{{ d.name }}</view>
        </scroll-view>
      </view>
    </view>
  </view>
</template>

<script>
import api from '@/utils/api.js'
export default {
  data() {
    return {
      list: [],
      loading: false,
      keyword: '',
      dateFrom: '',
      dateTo: '',
      destinationId: null,
      destinationName: '',
      destinations: [],
      showDestPicker: false
    }
  },
  onLoad() {
    this.load()
    api.destinations.list().then(res => { this.destinations = res || [] }).catch(() => {})
  },
  onPullDownRefresh() {
    this.load().then(() => uni.stopPullDownRefresh())
  },
  methods: {
    load() {
      this.loading = true
      const params = {}
      if (this.keyword.trim()) params.keyword = this.keyword.trim()
      if (this.dateFrom) params.dateFrom = this.dateFrom
      if (this.dateTo) params.dateTo = this.dateTo
      if (this.destinationId) params.destinationId = this.destinationId
      return api.companions.list(params).then(res => {
        this.list = res || []
      }).catch(() => {
        this.list = []
      }).finally(() => {
        this.loading = false
      })
    },
    onDateFromChange(e) {
      this.dateFrom = e.detail.value
      this.load()
    },
    onDateToChange(e) {
      this.dateTo = e.detail.value
      this.load()
    },
    selectDest(id, name) {
      this.destinationId = id
      this.destinationName = name
      this.showDestPicker = false
      this.load()
    },
    formatDate(d) {
      if (!d) return ''
      const s = typeof d === 'string' ? d : (d.toISOString ? d.toISOString().slice(0, 10) : '')
      return s.slice(0, 10)
    },
    formatTime(t) {
      if (!t) return ''
      const d = typeof t === 'string' ? new Date(t) : t
      const now = new Date()
      const diff = now - d
      if (diff < 60000) return '刚刚'
      if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
      if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
      return d.getMonth() + 1 + '-' + d.getDate()
    },
    goDetail(id) {
      uni.navigateTo({ url: '/pages/companion/detail?id=' + id })
    },
    goPublish() {
      uni.navigateTo({ url: '/pages/companion/publish' })
    }
  }
}
</script>

<style scoped>
.page { padding: 24rpx; padding-bottom: 48rpx; background: #f2f4f3; min-height: 100vh; }
.toolbar { display: flex; align-items: center; gap: 20rpx; margin-bottom: 20rpx; }
.search-inp { flex: 1; height: 72rpx; padding: 0 24rpx; background: #fff; border-radius: 12rpx; font-size: 28rpx; }
.btn-wrap { flex-shrink: 0; }
.btn { padding: 0 28rpx; height: 72rpx; line-height: 72rpx; font-size: 28rpx; border-radius: 12rpx; }
.btn.primary { background: #2d5a3d; color: #fff; }
.filters { display: flex; flex-wrap: wrap; gap: 16rpx; margin-bottom: 24rpx; }
.filter-item { padding: 12rpx 24rpx; background: #fff; border-radius: 12rpx; font-size: 26rpx; color: #666; }
.list { display: flex; flex-direction: column; gap: 20rpx; }
.card { background: #fff; border-radius: 20rpx; padding: 28rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,.06); }
.card-title { font-size: 32rpx; font-weight: 600; color: #1a1a1a; margin-bottom: 12rpx; }
.card-meta { font-size: 24rpx; color: #666; margin-bottom: 8rpx; }
.card-content { font-size: 28rpx; color: #333; margin-bottom: 16rpx; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.card-footer { display: flex; justify-content: space-between; font-size: 24rpx; color: #999; }
.author { color: #2d5a3d; }
.empty { text-align: center; padding: 80rpx 40rpx; color: #999; font-size: 28rpx; }
.empty .btn { margin-top: 24rpx; }
.loading-wrap { text-align: center; padding: 60rpx; color: #999; }
.dest-picker-mask { position: fixed; left: 0; right: 0; top: 0; bottom: 0; background: rgba(0,0,0,.4); z-index: 100; }
.dest-picker { position: absolute; left: 24rpx; right: 24rpx; bottom: 0; max-height: 60vh; background: #fff; border-radius: 20rpx 20rpx 0 0; overflow: hidden; }
.picker-title { padding: 28rpx; font-size: 32rpx; font-weight: 600; text-align: center; border-bottom: 1rpx solid #eee; }
.dest-list { max-height: 50vh; }
.dest-opt { padding: 28rpx; font-size: 30rpx; border-bottom: 1rpx solid #f5f5f5; }
</style>
