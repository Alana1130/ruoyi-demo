<template>
  <view class="page page-wrap">
    <view class="hero">
      <view class="title">预算规划</view>
      <view class="subtitle">输入预算与天数，智能推荐目的地与线路</view>
    </view>
    <view class="card form card-base">
      <view class="row">
        <text class="label">预算上限（元）</text>
        <input class="input" type="number" v-model.number="maxBudget" placeholder="600" />
      </view>
      <view class="row">
        <text class="label">出行天数</text>
        <picker :value="daysIdx" :range="[1,2,3,4,5]" @change="onDaysChange">
          <view class="picker">{{ days }} 天</view>
        </picker>
      </view>
      <view class="row">
        <text class="label">指定目的地</text>
        <picker :value="destIdx" :range="destOptions" range-key="name" @change="onDestChange">
          <view class="picker">{{ destLabel }}</view>
        </picker>
      </view>
      <button class="btn" :disabled="loading" @tap="search">{{ loading ? '规划中...' : '智能规划' }}</button>
    </view>

    <view class="section-title" v-if="plans.length">推荐方案</view>
    <view class="card plan card-base" v-for="(p, i) in plans" :key="i">
      <view class="plan-head">
        <text class="plan-name">{{ p.destination.name }}</text>
        <text class="link" @tap="goDest(p.destination.id)">详情</text>
      </view>
      <view class="plan-desc">{{ p.route.description }}</view>
      <view class="plan-route" @tap="goRoute(p.route.id)">线路：{{ p.route.name }}</view>
      <view class="plan-acc" v-if="p.accommodations && p.accommodations.length">住宿参考：{{ (p.accommodations.slice(0,2).map(a => a.name + ' ¥' + a.pricePerNight + '/晚')).join('、') }}</view>
      <view class="plan-actions">
        <text class="plan-action" @tap="goRoute(p.route.id)">看线路</text>
        <text class="plan-action" @tap="goServiceHotel(p.destination.id)">订酒店</text>
        <text class="plan-action" @tap="goServiceTicket(p.destination.id)">门票</text>
        <text class="plan-action" @tap="goServiceTaxi(p.destination.id)">打车</text>
      </view>
    </view>
    <view class="empty-state" v-if="!loading && plans.length === 0 && maxBudget > 0">
      <view class="empty-icon">📋</view>
      <text>输入预算与天数后点击「智能规划」</text>
    </view>
  </view>
</template>

<script>
import api from '@/utils/api.js'
export default {
  data() {
    return {
      maxBudget: 600,
      days: 2,
      daysIdx: 1,
      destIdx: 0,
      destinations: [],
      destOptions: [{ id: '', name: '全部' }],
      destLabel: '全部',
      plans: [],
      loading: false
    }
  },
  onLoad(op) {
    api.destinations.list().then(res => {
      this.destinations = res || []
      this.destOptions = [{ id: '', name: '全部' }, ...this.destinations]
      const destId = op.destinationId || op.destId
      if (destId && this.destinations.length) {
        const idx = this.destinations.findIndex(d => d.id == destId)
        if (idx >= 0) {
          this.destIdx = idx + 1
          this.destLabel = this.destinations[idx].name
        }
      }
    })
  },
  methods: {
    onDaysChange(e) {
      this.daysIdx = e.detail.value
      this.days = [1,2,3,4,5][e.detail.value]
    },
    onDestChange(e) {
      this.destIdx = e.detail.value
      const o = this.destOptions[e.detail.value]
      this.destLabel = o ? o.name : '全部'
    },
    search() {
      this.loading = true
      const params = { maxBudget: this.maxBudget, days: this.days }
      const cur = this.destOptions[this.destIdx]
      if (cur && cur.id) params.destinationId = cur.id
      api.budget.plan(params).then(res => {
        this.plans = res || []
      }).catch(() => {
        this.plans = []
      }).finally(() => {
        this.loading = false
      })
    },
    goDest(id) {
      uni.navigateTo({ url: '/pages/destination/detail?id=' + id })
    },
    goRoute(id) {
      uni.navigateTo({ url: '/pages/route/detail?id=' + id })
    },
    goServiceHotel(destId) {
      uni.navigateTo({ url: '/pages/service/hotel?destId=' + destId })
    },
    goServiceTicket(destId) {
      uni.navigateTo({ url: '/pages/service/ticket?destId=' + destId })
    },
    goServiceTaxi(destId) {
      uni.navigateTo({ url: '/pages/service/taxi?destId=' + destId })
    }
  }
}
</script>

<style scoped>
.page { padding: 0; }
.hero { padding: 24rpx 24rpx 16rpx; }
.title { font-size: 40rpx; font-weight: 700; color: #1a1a1a; margin-bottom: 8rpx; }
.subtitle { font-size: 26rpx; color: #666; }
.card { margin: 0 24rpx 24rpx; padding: 28rpx; margin-bottom: 24rpx; }
.card.form { margin: 0 24rpx 24rpx 24rpx; }
.row { margin-bottom: 24rpx; }
.label { display: block; font-size: 26rpx; color: #666; margin-bottom: 8rpx; }
.input, .picker { border: 1rpx solid #e8e8e8; padding: 20rpx; border-radius: 16rpx; font-size: 30rpx; background: #fff; }
.btn { background: linear-gradient(135deg, #4a7c59 0%, #2d5a3d 100%); color: #fff; font-size: 30rpx; font-weight: 600; padding: 24rpx; border-radius: 16rpx; margin-top: 16rpx; border: none; }
.plan-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12rpx; }
.plan-name { font-weight: 700; font-size: 30rpx; color: #1a1a1a; }
.link { color: #2d5a3d; font-size: 26rpx; }
.plan-desc { font-size: 26rpx; color: #666; margin-bottom: 8rpx; }
.plan-route { font-size: 26rpx; color: #2d5a3d; margin-bottom: 6rpx; }
.plan-acc { font-size: 24rpx; color: #999; margin-bottom: 12rpx; }
.plan-actions { display: flex; flex-wrap: wrap; gap: 16rpx; padding-top: 12rpx; border-top: 1rpx solid #f0f0f0; }
.plan-action { font-size: 24rpx; color: #2d5a3d; padding: 8rpx 16rpx; background: rgba(45,90,61,.08); border-radius: 12rpx; }
.empty-state { padding: 60rpx 40rpx; text-align: center; color: #999; font-size: 28rpx; }
.empty-state .empty-icon { font-size: 72rpx; margin-bottom: 20rpx; display: block; opacity: .6; }
</style>
