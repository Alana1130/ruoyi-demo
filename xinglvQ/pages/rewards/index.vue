<template>
  <view class="page">
    <view class="hero">
      <view class="hero-kicker">REWARDS · 星旅好物</view>
      <view class="hero-title">星星兑换 / 直接购买</view>
      <view class="hero-desc">文旅、公益好物可星星兑换或直接购买；自营仅支持星星兑换</view>
    </view>

    <view class="tabs">
      <view class="tab" :class="{ active: filter === '' }" @tap="filter = ''">全部</view>
      <view class="tab" :class="{ active: filter === 'culture_tourism' }" @tap="filter = 'culture_tourism'">文旅合作</view>
      <view class="tab" :class="{ active: filter === 'public_welfare' }" @tap="filter = 'public_welfare'">公益好物</view>
      <view class="tab" :class="{ active: filter === 'self_operated' }" @tap="filter = 'self_operated'">自营</view>
    </view>

    <view class="section-title" v-if="items.length">商品列表<text class="section-desc">{{ items.length }} 件在售</text></view>
    <view class="list" v-if="items.length">
      <view class="card card-base" v-for="item in items" :key="item.id" @tap="goDetail(item.id)">
        <view class="head">
          <view class="thumb">{{ item.type === 'public_welfare' ? '🎗️' : (item.type === 'self_operated' ? '🎁' : '🏞️') }}</view>
          <view class="head-main">
            <text class="name">{{ item.name }}</text>
            <view class="prices">
              <text class="stars">{{ item.starsCost }} 星</text>
              <text class="price" v-if="item.price">¥{{ item.price }}</text>
            </view>
          </view>
        </view>
        <view class="desc" v-if="item.description">{{ item.description }}</view>
        <view class="meta-line">
          <text class="partner" v-if="item.partnerName">合作：{{ item.partnerName }}</text>
          <text class="type">{{ typeLabel[item.type] || item.type }}</text>
        </view>
        <view class="btns">
          <button class="btn btn-star" @tap.stop="goDetail(item.id)">星星兑换</button>
          <button class="btn btn-buy" v-if="item.allowPurchase === 1 && item.price" @tap.stop="goDetail(item.id)">直接购买</button>
        </view>
        <view class="link-detail">查看售品详情 ›</view>
      </view>
    </view>
    <view class="empty-state" v-else-if="!loading">
      <view class="empty-icon">🎁</view>
      <text>暂无兑换商品</text>
      <text class="empty-hint">切换分类试试</text>
    </view>
    <view class="loading-wrap" v-else>加载中...</view>
  </view>
</template>

<script>
import api from '@/utils/api.js'
export default {
  data() {
    return {
      filter: '',
      items: [],
      loading: true,
      typeLabel: { culture_tourism: '文旅合作', public_welfare: '公益好物', self_operated: '自营' }
    }
  },
  watch: {
    filter() {
      this.load()
    }
  },
  onShow() {
    this.load()
  },
  methods: {
    load() {
      this.loading = true
      api.rewards.items(this.filter || undefined).then(res => {
        this.items = res || []
      }).catch(() => {
        this.items = []
      }).finally(() => {
        this.loading = false
      })
    },
    goDetail(id) {
      uni.navigateTo({ url: '/pages/rewards/detail?id=' + id })
    }
  }
}
</script>

<style scoped>
.page { padding: 20rpx 24rpx 140rpx; background: var(--xlv-paper); min-height: 100vh; }
.hero { position: relative; overflow: hidden; border-radius: var(--xlv-radius-lg); background: linear-gradient(158deg, #2F5A44 0%, #1F3D2F 66%, #172F24 100%); padding: 42rpx 32rpx 38rpx; box-shadow: 0 18rpx 44rpx rgba(22,33,28,.18); }
.hero::after { content: ''; position: absolute; right: -130rpx; top: -150rpx; width: 400rpx; height: 400rpx; border-radius: 50%; background: radial-gradient(circle, rgba(201,162,39,.4) 0%, rgba(201,162,39,0) 68%); }
.hero-kicker { font-size: 21rpx; letter-spacing: 3rpx; color: rgba(255,255,255,.5); margin-bottom: 12rpx; }
.hero-title { font-size: 42rpx; font-weight: 700; color: #fff; letter-spacing: 1rpx; line-height: 1.35; }
.hero-desc { margin-top: 12rpx; font-size: 24rpx; color: rgba(255,255,255,.66); line-height: 1.55; }

.tabs { display: flex; flex-wrap: wrap; gap: 14rpx; margin: 26rpx 0 4rpx; }
.tab { padding: 16rpx 28rpx; border-radius: 999rpx; font-size: 25rpx; background: rgba(31,61,47,.05); color: var(--xlv-ink-2); }
.tab.active { background: var(--xlv-forest); color: #fff; font-weight: 600; box-shadow: 0 8rpx 20rpx rgba(31,61,47,.18); }

.list { display: flex; flex-direction: column; }
.card { margin-bottom: 22rpx; padding: 26rpx 28rpx; }
.head { display: flex; align-items: flex-start; margin-bottom: 16rpx; }
.thumb { width: 84rpx; height: 84rpx; border-radius: 22rpx; background: linear-gradient(135deg, #F6EDD2, #EADCB4); display: flex; align-items: center; justify-content: center; font-size: 38rpx; margin-right: 20rpx; flex-shrink: 0; }
.head-main { flex: 1; min-width: 0; display: flex; align-items: flex-start; justify-content: space-between; }
.name { flex: 1; font-weight: 700; font-size: 30rpx; color: var(--xlv-ink); line-height: 1.4; padding-right: 16rpx; }
.prices { display: flex; flex-direction: column; align-items: flex-end; gap: 8rpx; flex-shrink: 0; }
.stars { background: linear-gradient(135deg, #F7ECD0, #E9D5A4); color: #7A6320; padding: 6rpx 16rpx; border-radius: 999rpx; font-size: 23rpx; font-weight: 700; }
.price { font-size: 27rpx; font-weight: 700; color: var(--xlv-danger); }
.desc { font-size: 25rpx; color: var(--xlv-ink-2); line-height: 1.55; margin-bottom: 12rpx; }
.meta-line { display: flex; align-items: center; flex-wrap: wrap; gap: 14rpx; margin-bottom: 18rpx; }
.partner { font-size: 22rpx; color: var(--xlv-muted); }
.type { font-size: 21rpx; color: var(--xlv-forest-2); background: rgba(45,90,61,.08); padding: 4rpx 14rpx; border-radius: 999rpx; }
.btns { display: flex; flex-wrap: wrap; gap: 16rpx; }
.btn { font-size: 26rpx; padding: 16rpx 24rpx; border-radius: var(--xlv-radius-md); flex: 1; min-width: 170rpx; line-height: 1.4; }
.btn::after { border: none; }
.btn-star { background: linear-gradient(135deg, #3A6A4B, #23503A); color: #fff; font-weight: 600; }
.btn-buy { background: #fff; color: var(--xlv-danger); border: 1rpx solid rgba(191,74,60,.4); }
.link-detail { margin-top: 14rpx; font-size: 22rpx; color: var(--xlv-gold); }
</style>
