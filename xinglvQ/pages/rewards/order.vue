<template>
  <view class="page page-wrap">
    <view class="loading-wrap" v-if="loading && !item">加载中...</view>
    <template v-else-if="item">
      <view class="section-title">确认订单</view>
      <!-- 商品信息 -->
      <view class="card card-base product">
        <view class="row">
          <view class="label">商品</view>
          <view class="value name">{{ item.name }}</view>
        </view>
        <view class="row">
          <view class="label">金额</view>
          <view class="value price">¥{{ item.price }}</view>
        </view>
      </view>
      <!-- 购物信息（收货人） -->
      <view class="section-title">收货信息</view>
      <view class="card card-base form">
        <view class="form-item select-addr" @tap="goSelectAddress">
          <text class="label">收货地址</text>
          <text class="addr-value">{{ addressSummary || '请选择地址' }}</text>
          <text class="arrow">></text>
        </view>
        <view class="form-item">
          <text class="label">收货人</text>
          <input class="input" v-model="form.receiverName" placeholder="请输入收货人姓名" />
        </view>
        <view class="form-item">
          <text class="label">联系电话</text>
          <input class="input" type="number" v-model="form.receiverPhone" placeholder="请输入手机号" />
        </view>
        <view class="form-item">
          <text class="label">详细地址</text>
          <textarea class="textarea" v-model="form.receiverAddress" placeholder="街道、门牌号等" />
        </view>
      </view>
      <view class="tip">请填写真实收货信息，便于发货与联系</view>
      <!-- 提交 -->
      <view class="footer">
        <view class="total">合计：<text class="amount">¥{{ item.price }}</text></view>
        <button class="btn" :disabled="submitting" @tap="submit">{{ submitting ? '提交中...' : '提交订单' }}</button>
      </view>
      <view class="msg err" v-if="errText">{{ errText }}</view>
    </template>
    <view class="empty-state" v-else>
      <view class="empty-icon">📦</view>
      <text>商品信息丢失</text>
      <view class="back" @tap="goBack">返回</view>
    </view>
  </view>
</template>

<script>
import api from '@/utils/api.js'
export default {
  data() {
    return {
      id: null,
      item: null,
      loading: true,
      submitting: false,
      errText: '',
      form: { receiverName: '', receiverPhone: '', receiverAddress: '' }
    }
  },
  onLoad(op) {
    this.id = op.id ? Number(op.id) : null
    if (this.id) this.load()
    else this.loading = false
  },
  onShow() {
    const selected = uni.getStorageSync('order_selected_address')
    if (selected && typeof selected === 'object') {
      this.form.receiverName = selected.receiverName || ''
      this.form.receiverPhone = selected.receiverPhone || ''
      this.form.receiverAddress = selected.receiverAddress || ''
      uni.removeStorageSync('order_selected_address')
    }
  },
  computed: {
    addressSummary() {
      const a = this.form.receiverAddress
      const n = this.form.receiverName
      const p = this.form.receiverPhone
      if (!a && !n && !p) return ''
      return (n ? n + ' ' : '') + (p ? p + ' ' : '') + (a ? a : '')
    }
  },
  methods: {
    goSelectAddress() {
      uni.navigateTo({ url: '/pages/my/address-list?from=order' })
    },
    load() {
      this.loading = true
      api.rewards.get(this.id).then(res => {
        this.item = res || null
        if (!this.item || this.item.allowPurchase !== 1 || !this.item.price) {
          this.item = null
        }
      }).catch(() => {
        this.item = null
      }).finally(() => {
        this.loading = false
      })
    },
    submit() {
      this.errText = ''
      const name = (this.form.receiverName || '').trim()
      const phone = (this.form.receiverPhone || '').trim()
      const address = (this.form.receiverAddress || '').trim()
      if (!name) {
        this.errText = '请填写收货人姓名'
        return
      }
      if (!phone) {
        this.errText = '请填写联系电话'
        return
      }
      if (!address) {
        this.errText = '请填写收货地址'
        return
      }
      this.submitting = true
      api.rewards.purchase(this.item.id, {
        receiverName: name,
        receiverPhone: phone,
        receiverAddress: address
      }).then(res => {
        uni.showToast({ title: '订单提交成功' })
        setTimeout(() => {
          uni.navigateBack({ delta: 2 })
        }, 800)
      }).catch(e => {
        this.errText = (e && e.message) || '提交失败，请重试'
      }).finally(() => {
        this.submitting = false
      })
    },
    goBack() {
      uni.navigateBack()
    }
  }
}
</script>

<style scoped>
.page { padding-bottom: 200rpx; }
.product .row { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 16rpx; }
.product .row:last-child { margin-bottom: 0; }
.product .label { font-size: 26rpx; color: #666; }
.product .value { font-size: 28rpx; color: #1a1a1a; }
.product .name { font-weight: 600; }
.product .price { font-weight: 700; color: #c0392b; font-size: 32rpx; }
.form { margin: 0 24rpx 24rpx; padding: 28rpx; }
.form-item { margin-bottom: 24rpx; }
.form-item .label { display: block; font-size: 28rpx; color: #666; margin-bottom: 12rpx; }
.form-item.select-addr { display: flex; align-items: center; flex-wrap: wrap; }
.form-item.select-addr .label { margin-bottom: 0; margin-right: 16rpx; }
.addr-value { flex: 1; font-size: 28rpx; color: #999; min-height: 44rpx; }
.form-item.select-addr .addr-value { color: #1a1a1a; }
.arrow { font-size: 28rpx; color: #999; margin-left: 8rpx; }
.input, .textarea { border: 1rpx solid #e8e8e8; padding: 24rpx; border-radius: 16rpx; font-size: 30rpx; background: #fff; width: 100%; box-sizing: border-box; }
.textarea { min-height: 160rpx; }
.tip { padding: 0 24rpx 24rpx; font-size: 24rpx; color: #999; }
.footer { position: fixed; bottom: 0; left: 0; right: 0; display: flex; align-items: center; padding: 20rpx 24rpx; padding-bottom: calc(20rpx + env(safe-area-inset-bottom)); background: #fff; box-shadow: 0 -4rpx 20rpx rgba(0,0,0,.06); }
.total { font-size: 28rpx; color: #333; margin-right: 24rpx; }
.amount { font-weight: 700; color: #c0392b; font-size: 34rpx; }
.btn { flex: 1; background: linear-gradient(135deg, #4a7c59 0%, #2d5a3d 100%); color: #fff; font-size: 30rpx; font-weight: 600; padding: 24rpx; border-radius: 16rpx; border: none; }
.msg.err { margin: 0 24rpx 20rpx; padding: 20rpx; background: #ffebee; color: #c62828; border-radius: 16rpx; font-size: 26rpx; }
.empty-state { padding: 80rpx 40rpx; text-align: center; color: #999; font-size: 28rpx; }
.empty-state .empty-icon { font-size: 80rpx; margin-bottom: 24rpx; display: block; opacity: .6; }
.back { margin-top: 24rpx; color: #2d5a3d; font-size: 28rpx; }
</style>
