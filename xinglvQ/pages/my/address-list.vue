<template>
  <view class="page page-wrap">
    <view class="hero">
      <view class="title">收货地址</view>
      <view class="desc">选择或管理您的收货地址，下单时可直接选用</view>
    </view>
    <view class="section-title" v-if="list.length">地址列表</view>
    <view class="list" v-if="list.length">
      <view class="card addr-item" v-for="a in list" :key="a.id" @tap="onSelectAddr(a)">
        <view class="addr-main">
          <view class="addr-head">
            <text class="name">{{ a.receiverName }}</text>
            <text class="phone">{{ a.receiverPhone }}</text>
          </view>
          <view class="addr-detail">{{ a.receiverAddress }}</view>
          <view class="addr-tags">
            <text class="default-tag" v-if="a.isDefault === 1">默认</text>
          </view>
        </view>
        <view class="edit-btn" @tap.stop="goEdit(a.id)">
          <text class="edit-icon">✏️</text>
          <text class="edit-text">编辑</text>
        </view>
      </view>
    </view>
    <view class="empty-state" v-else-if="!loading">
      <view class="empty-icon">📍</view>
      <text>暂无收货地址</text>
      <text class="empty-hint">添加后可在下单时快速选择</text>
    </view>
    <view class="loading-wrap" v-else>加载中...</view>
    <view class="footer-btn">
      <button class="btn-add" @tap="goAdd">新增收货地址</button>
    </view>
  </view>
</template>

<script>
import api from '@/utils/api.js'
export default {
  data() {
    return { list: [], loading: true, fromOrder: false }
  },
  onLoad(op) {
    this.fromOrder = op.from === 'order'
  },
  onShow() {
    this.load()
  },
  methods: {
    load() {
      this.loading = true
      api.addresses.list().then(res => {
        this.list = Array.isArray(res) ? res : []
      }).catch(() => {
        this.list = []
      }).finally(() => {
        this.loading = false
      })
    },
    onSelectAddr(a) {
      if (this.fromOrder) {
        uni.setStorageSync('order_selected_address', {
          receiverName: a.receiverName,
          receiverPhone: a.receiverPhone,
          receiverAddress: a.receiverAddress
        })
        uni.navigateBack()
      } else {
        this.goEdit(a.id)
      }
    },
    goEdit(id) {
      uni.navigateTo({ url: '/pages/my/address-edit?id=' + id })
    },
    goAdd() {
      uni.navigateTo({ url: '/pages/my/address-edit' })
    }
  }
}
</script>

<style scoped>
.page { padding-bottom: 160rpx; }
.hero { padding: 28rpx 24rpx 20rpx; }
.title { font-size: 42rpx; font-weight: 700; color: #1a1a1a; margin-bottom: 12rpx; letter-spacing: 1rpx; }
.desc { font-size: 26rpx; color: #666; line-height: 1.5; }
.section-title { margin: 8rpx 24rpx 20rpx; padding-left: 16rpx; border-left: 6rpx solid #2d5a3d; font-size: 30rpx; font-weight: 700; color: #1a1a1a; }
.list { padding: 0 24rpx; }
.addr-item { display: flex; align-items: stretch; background: #fff; border-radius: 20rpx; padding: 0; margin-bottom: 24rpx; box-shadow: 0 4rpx 20rpx rgba(45,90,61,.08); overflow: hidden; }
.addr-main { flex: 1; padding: 28rpx 24rpx; min-width: 0; }
.addr-head { display: flex; align-items: center; flex-wrap: wrap; gap: 16rpx; margin-bottom: 12rpx; }
.name { font-size: 32rpx; font-weight: 700; color: #1a1a1a; }
.phone { font-size: 28rpx; color: #666; }
.addr-detail { font-size: 28rpx; color: #555; line-height: 1.55; margin-bottom: 12rpx; }
.addr-tags { min-height: 32rpx; }
.default-tag { display: inline-block; font-size: 22rpx; padding: 6rpx 16rpx; background: linear-gradient(135deg, #4a7c59, #2d5a3d); color: #fff; border-radius: 20rpx; }
.edit-btn { width: 120rpx; display: flex; flex-direction: column; align-items: center; justify-content: center; background: #f8faf8; border-left: 1rpx solid #eee; }
.edit-icon { font-size: 40rpx; margin-bottom: 6rpx; opacity: .9; }
.edit-text { font-size: 24rpx; color: #2d5a3d; font-weight: 500; }
.empty-state { padding: 100rpx 48rpx; text-align: center; color: #999; font-size: 28rpx; }
.empty-state .empty-icon { font-size: 96rpx; margin-bottom: 28rpx; display: block; opacity: .5; }
.empty-hint { display: block; margin-top: 16rpx; font-size: 26rpx; color: #bbb; }
.loading-wrap { text-align: center; padding: 80rpx; color: #999; font-size: 28rpx; }
.footer-btn { position: fixed; bottom: 0; left: 0; right: 0; padding: 24rpx; padding-bottom: calc(24rpx + env(safe-area-inset-bottom)); background: #fff; box-shadow: 0 -8rpx 24rpx rgba(0,0,0,.06); }
.btn-add { width: 100%; height: 96rpx; line-height: 96rpx; background: linear-gradient(135deg, #4a7c59 0%, #2d5a3d 100%); color: #fff; font-size: 32rpx; font-weight: 600; border: none; border-radius: 20rpx; }
</style>
