<template>
  <view class="page page-wrap">
    <view class="loading-wrap" v-if="loading">加载中...</view>
    <template v-else-if="item">
      <!-- 商品图 -->
      <view class="cover-wrap">
        <image v-if="item.image" class="cover" :src="item.image" mode="aspectFill" />
        <view v-else class="cover placeholder">🎁</view>
      </view>
      <!-- 积分 + 库存（售品详情头部） -->
      <view class="card card-base price-row">
        <view class="price-stock">
          <text class="stars-label" v-if="item.starsCost"><text class="icon-star">★</text> {{ item.starsCost }} 星星</text>
          <text class="price-label" v-if="item.price">¥{{ item.price }}</text>
          <view class="stock-badge">库存 {{ item.stock === undefined || item.stock < 0 ? '不限' : (item.stock === 0 ? '0' : item.stock) }}</view>
        </view>
        <view class="limit-badge">每人限兑 1 件</view>
      </view>
      <!-- 商品名称 -->
      <view class="card card-base info">
        <view class="name">{{ item.name }}</view>
        <view class="meta">
          <text class="tag type">{{ typeLabel[item.type] || item.type }}</text>
          <text class="partner" v-if="item.partnerName"> · 合作：{{ item.partnerName }}</text>
        </view>
      </view>
      <!-- 商品介绍 -->
      <view class="section-title">商品介绍</view>
      <view class="card card-base desc-block">
        <text class="desc-text" v-if="item.description">{{ item.description }}</text>
        <text class="desc-text empty" v-else>暂无详细介绍</text>
      </view>
      <!-- 兑换限制 -->
      <view class="section-title">兑换限制</view>
      <view class="card card-base rule-block">
        <text class="rule-text">无等级限制，登录即可参与兑换。</text>
      </view>
      <!-- 兑换次数 -->
      <view class="section-title">兑换次数</view>
      <view class="card card-base rule-block">
        <text class="rule-text">不同商品每位用户有可兑换次数限制，数量有限，兑完即止。</text>
      </view>
      <!-- 发货情况 -->
      <view class="section-title">发货情况</view>
      <view class="card card-base rule-block">
        <text class="rule-text">我们将在 15 个工作日内安排发货，请留意收货信息。</text>
      </view>
      <!-- 售后说明 -->
      <view class="section-title">售后说明</view>
      <view class="card card-base rule-block">
        <text class="rule-text">活动奖品若有质量问题，服务方在兑换截止后 9 个月内承担更换、修理等义务。</text>
      </view>
      <!-- 底部操作 -->
      <view class="footer">
        <button class="btn btn-exchange" :disabled="exchanging || (item.stock === 0)" @tap="openExchangePopup">
          {{ exchanging ? '兑换中...' : item.stock === 0 ? '已兑完' : '星星兑换' }}
        </button>
        <button class="btn btn-buy" v-if="item.allowPurchase === 1 && item.price && item.stock !== 0" :disabled="exchanging || purchasing" @tap="openPurchasePopup">
          {{ purchasing ? '处理中...' : '直接购买' }}
        </button>
      </view>
      <view class="msg success" v-if="message && message.ok">{{ message.text }}</view>
      <view class="msg err" v-if="message && !message.ok">{{ message.text }}</view>

      <!-- 兑换详情 / 购买详情 弹窗 -->
      <view class="popup-mask" v-if="popupVisible" @tap="closePopup"></view>
      <view class="popup-wrap" v-if="popupVisible">
        <view class="popup">
          <view class="popup-title">{{ popupMode === 'exchange' ? '兑换详情' : '购买详情' }}</view>
          <view class="popup-row clickable" @tap="goSelectAddress">
            <text class="popup-label">收货地址</text>
            <text class="popup-value">{{ addressSummary || (popupMode === 'purchase' ? '未填写' : '选填') }}</text>
            <text class="popup-arrow">></text>
          </view>
          <view class="popup-row">
            <text class="popup-label">购买数量</text>
            <view class="qty-wrap">
              <text class="qty-btn" :class="{ disabled: quantity <= 1 }" @tap="qtyChange(-1)">-</text>
              <text class="qty-num">{{ quantity }}</text>
              <text class="qty-btn" :class="{ disabled: quantity >= maxQty }" @tap="qtyChange(1)">+</text>
            </view>
          </view>
          <view class="popup-summary">
            <view class="summary-row">
              <text>单价</text>
              <text class="highlight" v-if="popupMode === 'exchange'">{{ unitStars }} 星星</text>
              <text class="highlight" v-else>¥{{ item.price }}</text>
            </view>
            <view class="summary-row">
              <text>合计</text>
              <text class="highlight" v-if="popupMode === 'exchange'">{{ totalStars }} 星星</text>
              <text class="highlight" v-else>¥{{ totalPrice }}</text>
            </view>
            <view class="summary-row balance" v-if="popupMode === 'exchange'">
              <text>余额</text>
              <text class="highlight">{{ userStars }} 星星</text>
            </view>
          </view>
          <button class="popup-btn" :class="{ disabled: popupMode === 'exchange' && totalStars > userStars }" :disabled="(popupMode === 'exchange' && totalStars > userStars) || submitting" @tap="confirmPopup">
            {{ popupMode === 'exchange' ? (submitting ? '兑换中...' : '确认兑换') : (submitting ? '提交中...' : '确认购买') }}
          </button>
        </view>
      </view>
    </template>
    <view class="empty-state" v-else>
      <view class="empty-icon">🎁</view>
      <text>商品不存在或已下架</text>
      <view class="back" @tap="goBack">返回列表</view>
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
      exchanging: false,
      purchasing: false,
      message: null,
      typeLabel: { culture_tourism: '文旅合作', public_welfare: '公益好物', self_operated: '自营' },
      popupVisible: false,
      popupMode: 'exchange',
      quantity: 1,
      userStars: 0,
      submitting: false,
      maxQty: 99
    }
  },
  computed: {
    unitStars() {
      return this.item ? (this.item.starsCost || 0) : 0
    },
    totalStars() {
      return this.unitStars * this.quantity
    },
    totalPrice() {
      if (!this.item || !this.item.price) return '0'
      return (Number(this.item.price) * this.quantity).toFixed(2)
    },
    addressSummary() {
      const s = uni.getStorageSync('order_selected_address')
      if (!s || typeof s !== 'object') return ''
      const name = s.receiverName || ''
      const addr = (s.receiverAddress || '').trim()
      if (name && addr) return name + ' ' + (addr.length > 10 ? addr.slice(0, 10) + '…' : addr)
      if (name) return name
      if (addr) return addr.length > 12 ? addr.slice(0, 12) + '…' : addr
      return ''
    }
  },
  onLoad(op) {
    this.id = op.id ? Number(op.id) : null
    if (this.id) this.load()
    else this.loading = false
  },
  onShow() {
    this.$forceUpdate()
  },
  methods: {
    load() {
      this.loading = true
      api.rewards.get(this.id).then(res => {
        this.item = res || null
      }).catch(() => {
        this.item = null
      }).finally(() => {
        this.loading = false
      })
    },
    loadUserStars() {
      return api.user.me().then(res => {
        this.userStars = (res && res.stars != null) ? res.stars : 0
      }).catch(() => { this.userStars = 0 })
    },
    openExchangePopup() {
      if (!this.item || this.item.stock === 0 || this.exchanging) return
      this.quantity = 1
      this.popupMode = 'exchange'
      this.loadUserStars()
      this.popupVisible = true
    },
    openPurchasePopup() {
      if (!this.item || this.item.allowPurchase !== 1 || !this.item.price || this.item.stock === 0) return
      this.quantity = 1
      this.popupMode = 'purchase'
      this.popupVisible = true
    },
    closePopup() {
      this.popupVisible = false
    },
    qtyChange(delta) {
      let n = this.quantity + delta
      if (n < 1) n = 1
      if (n > this.maxQty) n = this.maxQty
      if (this.item && this.item.stock >= 0 && n > this.item.stock) n = this.item.stock
      this.quantity = n
    },
    goSelectAddress() {
      uni.navigateTo({ url: '/pages/my/address-list?from=order' })
    },
    goOrderFromPopup() {
      this.closePopup()
      uni.navigateTo({ url: '/pages/rewards/order?id=' + this.item.id })
    },
    confirmPopup() {
      if (this.popupMode === 'exchange') {
        if (this.totalStars > this.userStars) return
        this.submitting = true
        this.closePopup()
        api.rewards.exchange(this.item.id).then(res => {
          this.message = { ok: true, text: '兑换成功：' + res.item + '，当前 ' + res.userStars + ' 星' }
          this.load()
          this.loadUserStars()
        }).catch(e => {
          this.message = { ok: false, text: (e && e.message) || '兑换失败' }
        }).finally(() => {
          this.submitting = false
        })
      } else {
        this.closePopup()
        uni.navigateTo({ url: '/pages/rewards/order?id=' + this.item.id })
      }
    },
    exchange() {
      this.openExchangePopup()
    },
    goOrder() {
      this.openPurchasePopup()
    },
    goBack() {
      uni.navigateBack()
    }
  }
}
</script>

<style scoped>
.page { padding-bottom: 160rpx; }
.cover-wrap { width: 100%; height: 380rpx; background: linear-gradient(135deg, #e8f0ea 0%, #d4e5d9 100%); }
.cover { width: 100%; height: 100%; }
.cover.placeholder { display: flex; align-items: center; justify-content: center; font-size: 120rpx; }
.price-row { margin: 24rpx; display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 16rpx; }
.price-stock { display: flex; align-items: center; flex-wrap: wrap; gap: 24rpx; }
.stars-label { font-size: 34rpx; font-weight: 700; color: #2d5a3d; }
.icon-star { color: #c9a227; margin-right: 4rpx; }
.price-label { font-size: 32rpx; font-weight: 700; color: #c0392b; }
.stock-badge { font-size: 26rpx; color: #666; background: #f5f5f5; padding: 8rpx 20rpx; border-radius: 24rpx; }
.limit-badge { font-size: 24rpx; color: #e67e22; background: #fef5e7; padding: 8rpx 20rpx; border-radius: 24rpx; }
.info { margin: 0 24rpx 24rpx; }
.name { font-size: 34rpx; font-weight: 700; color: #1a1a1a; margin-bottom: 12rpx; line-height: 1.4; }
.meta { margin-bottom: 0; }
.tag { margin-right: 8rpx; }
.tag.type { background: rgba(45,90,61,.12); color: #2d5a3d; padding: 6rpx 16rpx; border-radius: 20rpx; font-size: 24rpx; }
.partner { font-size: 24rpx; color: #999; }
.desc-block, .rule-block { margin: 0 24rpx 24rpx; padding: 28rpx; }
.desc-text, .rule-text { font-size: 28rpx; color: #555; line-height: 1.6; }
.desc-text.empty { color: #999; }
.rule-text { color: #666; }
.section-title { margin: 0 24rpx 20rpx; padding-left: 16rpx; border-left: 6rpx solid #2d5a3d; font-size: 32rpx; font-weight: 700; color: #1a1a1a; }
.footer { position: fixed; bottom: 0; left: 0; right: 0; display: flex; padding: 20rpx 24rpx; padding-bottom: calc(20rpx + env(safe-area-inset-bottom)); background: #fff; box-shadow: 0 -4rpx 20rpx rgba(0,0,0,.06); }
.btn { flex: 1; margin: 0 8rpx; font-size: 30rpx; padding: 24rpx; border-radius: 16rpx; border: none; }
.btn-exchange { background: linear-gradient(135deg, #4a7c59 0%, #2d5a3d 100%); color: #fff; }
.btn-buy { background: #fff; color: #c0392b; border: 2rpx solid #c0392b; }
.msg { margin: 0 24rpx 20rpx; padding: 20rpx; border-radius: 16rpx; font-size: 26rpx; }
.msg.success { background: #e8f5e9; color: #2e7d32; }
.msg.err { background: #ffebee; color: #c62828; }
.empty-state { padding: 80rpx 40rpx; text-align: center; color: #999; font-size: 28rpx; }
.empty-state .empty-icon { font-size: 80rpx; margin-bottom: 24rpx; display: block; opacity: .6; }
.back { margin-top: 24rpx; color: #2d5a3d; font-size: 28rpx; }

/* 兑换/购买详情弹窗 */
.popup-mask { position: fixed; left: 0; right: 0; top: 0; bottom: 0; background: rgba(0,0,0,.5); z-index: 100; }
.popup-wrap { position: fixed; left: 0; right: 0; bottom: 0; z-index: 101; display: flex; justify-content: center; align-items: flex-end; }
.popup { width: 100%; max-height: 80vh; background: #fff; border-radius: 24rpx 24rpx 0 0; padding: 32rpx 24rpx; padding-bottom: calc(32rpx + env(safe-area-inset-bottom)); }
.popup-title { font-size: 34rpx; font-weight: 700; color: #1a1a1a; text-align: center; margin-bottom: 32rpx; }
.popup-row { display: flex; align-items: center; padding: 24rpx 0; border-bottom: 1rpx solid #f0f0f0; }
.popup-row.clickable { cursor: pointer; }
.popup-row .popup-value { flex: 1; min-width: 0; }
.popup-label { font-size: 28rpx; color: #333; width: 160rpx; flex-shrink: 0; }
.popup-value { font-size: 28rpx; color: #999; }
.popup-row.clickable .popup-value { color: #555; }
.popup-arrow { font-size: 28rpx; color: #2d5a3d; margin-left: 8rpx; flex-shrink: 0; }
.qty-wrap { display: flex; align-items: center; }
.qty-btn { width: 56rpx; height: 56rpx; line-height: 56rpx; text-align: center; font-size: 36rpx; color: #2d5a3d; border: 1rpx solid #2d5a3d; border-radius: 12rpx; }
.qty-btn.disabled { color: #ccc; border-color: #eee; }
.qty-num { min-width: 80rpx; text-align: center; font-size: 30rpx; font-weight: 600; }
.popup-summary { margin: 24rpx 0; padding: 24rpx; background: #f8f8f8; border-radius: 16rpx; }
.summary-row { display: flex; justify-content: space-between; font-size: 28rpx; color: #666; margin-bottom: 12rpx; }
.summary-row:last-child { margin-bottom: 0; }
.summary-row.balance { margin-top: 8rpx; padding-top: 12rpx; border-top: 1rpx solid #eee; }
.highlight { color: #e67e22; font-weight: 600; }
.popup-btn { width: 100%; height: 88rpx; line-height: 88rpx; text-align: center; font-size: 32rpx; font-weight: 600; color: #fff; background: linear-gradient(135deg, #4a7c59 0%, #2d5a3d 100%); border: none; border-radius: 16rpx; margin-top: 16rpx; }
.popup-btn.disabled { background: #ccc; color: #fff; }
</style>
