<template>
  <view class="page">
    <view class="section" v-if="myListings.length">
      <view class="section-title">我的挂单（可下架）</view>
      <view class="list my-list">
        <view class="card" v-for="item in myListings" :key="item.id">
          <view class="item-head">
            <text class="icon">{{ item.itemIcon || '🐾' }}</text>
            <view class="info">
              <text class="name">{{ item.itemName }}</text>
              <text class="meta" v-if="item.itemLevel">Lv.{{ item.itemLevel }} · {{ item.itemEvolutionStageName || '' }}</text>
              <text class="stars">⭐ {{ item.starsAmount }} 星星</text>
            </view>
          </view>
          <button class="btn cancel" @tap="cancel(item)">下架</button>
        </view>
      </view>
    </view>
    <view class="section">
      <view class="section-title">市场挂单</view>
    <view class="tabs">
      <view class="tab" :class="{ active: itemType === '' }" @tap="itemType = ''">全部</view>
      <view class="tab" :class="{ active: itemType === 'pet' }" @tap="itemType = 'pet'">宠物</view>
      <view class="tab" :class="{ active: itemType === 'food_license' }" @tap="itemType = 'food_license'">经营权</view>
    </view>
    <view class="list" v-if="list.length">
      <view class="card" v-for="item in list" :key="item.id">
        <view class="item-head">
          <text class="icon">{{ item.itemIcon || '🐾' }}</text>
          <view class="info">
            <text class="name">{{ item.itemName }}</text>
            <text class="seller">卖家 {{ item.sellerNickname }}</text>
            <text class="meta" v-if="item.itemLevel">Lv.{{ item.itemLevel }} · {{ item.itemEvolutionStageName || '' }}</text>
            <text class="stars">⭐ {{ item.starsAmount }} 星星</text>
            <text class="suggested" v-if="item.suggestedStars">参考价 {{ item.suggestedStars }} 星星</text>
          </view>
        </view>
        <button class="btn buy" @tap="buy(item)">购买</button>
      </view>
    </view>
    <view class="empty" v-else-if="!loading">暂无挂单，去小世界看看自己的宠物与经营权吧</view>
    <view class="loading-wrap" v-else>加载中...</view>
    </view>
  </view>
</template>

<script>
import api from '@/utils/api.js'
export default {
  data() {
    return {
      itemType: '',
      list: [],
      myListings: [],
      loading: false
    }
  },
  watch: {
    itemType() {
      this.load()
    }
  },
  onLoad() {
    this.loadMy()
    this.load()
  },
  onShow() {
    this.loadMy()
  },
  methods: {
    loadMy() {
      api.smallWorld.myListings().then(res => {
        this.myListings = res || []
      }).catch(() => { this.myListings = [] })
    },
    load() {
      this.loading = true
      const params = { limit: 30 }
      if (this.itemType) params.itemType = this.itemType
      api.smallWorld.tradeListings(params).then(res => {
        this.list = res || []
      }).catch(() => {
        this.list = []
      }).finally(() => {
        this.loading = false
      })
    },
    cancel(item) {
      uni.showModal({
        title: '确认下架',
        content: '确定下架「' + (item.itemName || '') + '」？下架后将回到你的小世界',
        success: (res) => {
          if (!res.confirm) return
          api.smallWorld.cancelListing(item.id).then(() => {
            uni.showToast({ title: '已下架' })
            this.loadMy()
            this.load()
          }).catch(e => uni.showToast({ title: e.message || '下架失败', icon: 'none' }))
        }
      })
    },
    buy(item) {
      uni.showModal({
        title: '确认购买',
        content: '花费 ' + item.starsAmount + ' 星星购买「' + (item.itemName || '') + '」？',
        success: (res) => {
          if (!res.confirm) return
          api.smallWorld.buyListing(item.id).then(() => {
            uni.showToast({ title: '购买成功，已收入小世界' })
            this.loadMy()
            this.load()
          }).catch(e => uni.showToast({ title: e.message || '购买失败', icon: 'none' }))
        }
      })
    }
  }
}
</script>

<style scoped>
.page { padding: 24rpx; background: #f2f4f3; min-height: 100vh; }
.tabs { display: flex; gap: 16rpx; margin-bottom: 24rpx; }
.tab { padding: 14rpx 28rpx; background: #fff; border-radius: 24rpx; font-size: 28rpx; color: #666; }
.tab.active { background: #2d5a3d; color: #fff; }
.list { display: flex; flex-direction: column; gap: 20rpx; }
.card { background: #fff; border-radius: 20rpx; padding: 24rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,.06); display: flex; align-items: center; justify-content: space-between; }
.item-head { display: flex; align-items: center; }
.item-head .icon { font-size: 56rpx; margin-right: 20rpx; }
.item-head .name { font-size: 30rpx; font-weight: 600; display: block; }
.item-head .seller { font-size: 24rpx; color: #999; }
.item-head .meta { font-size: 22rpx; color: #888; display: block; }
.item-head .stars { font-size: 26rpx; color: #2d5a3d; font-weight: 600; }
.item-head .suggested { font-size: 22rpx; color: #999; display: block; }
.section { margin-bottom: 32rpx; }
.section-title { font-size: 28rpx; font-weight: 600; color: #333; margin-bottom: 16rpx; }
.my-list .card .btn.cancel { background: #fff; color: #e65100; border: 1rpx solid #e65100; font-size: 26rpx; padding: 16rpx 28rpx; border-radius: 24rpx; }
.btn.buy { background: #2d5a3d; color: #fff; font-size: 26rpx; padding: 16rpx 28rpx; border-radius: 24rpx; }
.empty, .loading-wrap { text-align: center; padding: 80rpx; color: #999; }
</style>
