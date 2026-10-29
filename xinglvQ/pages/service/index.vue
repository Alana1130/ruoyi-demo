<template>
  <view class="page">
    <view class="hero">
      <view class="hero-kicker">TRAVEL SERVICE</view>
      <view class="hero-title">出行服务</view>
      <view class="hero-desc">丰富假期，从规划到出行一条龙 · 打车、门票、酒店、兼职实习</view>
    </view>

    <view class="section-title">必备服务<text class="section-desc">6 项</text></view>
    <view class="grid">
      <view class="card" @tap="goTaxi">
        <view class="icon clay">🚗</view>
        <view class="label">打车</view>
        <view class="hint">叫车·出行</view>
      </view>
      <view class="card" @tap="goTicket">
        <view class="icon gold">🎫</view>
        <view class="label">门票</view>
        <view class="hint">景点门票</view>
      </view>
      <view class="card" @tap="goHotel">
        <view class="icon moss">🏨</view>
        <view class="label">酒店</view>
        <view class="hint">订酒店</view>
      </view>
      <view class="card" @tap="goOpportunity">
        <view class="icon sky">📋</view>
        <view class="label">兼职·实习</view>
        <view class="hint">实践机会</view>
      </view>
      <view class="card" @tap="goStudentDiscount">
        <view class="icon gold">🎓</view>
        <view class="label">学生优惠</view>
        <view class="hint">专属折扣</view>
      </view>
      <view class="card" @tap="goAABilling">
        <view class="icon plum">💰</view>
        <view class="label">AA记账</view>
        <view class="hint">旅行算账</view>
      </view>
    </view>

    <view class="section-title">更多玩法<text class="section-desc">顺手逛逛</text></view>
    <view class="extra">
      <view class="link-card" @tap="goBudget">
        <view class="link-icon gold">📊</view>
        <text class="link-text">预算规划</text>
        <text class="link-hint">按预算选线路</text>
        <text class="arrow">›</text>
      </view>
      <view class="link-card" @tap="goCompanion">
        <view class="link-icon plum">👥</view>
        <text class="link-text">约伴</text>
        <text class="link-hint">找搭子一起玩</text>
        <text class="arrow">›</text>
      </view>
      <view class="link-card" @tap="goGuide">
        <view class="link-icon sky">📝</view>
        <text class="link-text">攻略·游记</text>
        <text class="link-hint">真实旅程参考</text>
        <text class="arrow">›</text>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  methods: {
    goTaxi() {
      uni.navigateTo({ url: '/pages/service/taxi' })
    },
    goTicket() {
      uni.navigateTo({ url: '/pages/service/ticket' })
    },
    goHotel() {
      uni.navigateTo({ url: '/pages/service/hotel' })
    },
    goOpportunity() {
      uni.navigateTo({ url: '/pages/opportunity/list' })
    },
    goStudentDiscount() {
      uni.navigateTo({ url: '/pages/service/discount' })
    },
    goAABilling() {
      uni.navigateTo({ url: '/pages/service/aa-billing' })
    },
    goBudget() {
      uni.navigateTo({ url: '/pages/budget/index' })
    },
    goCompanion() {
      uni.navigateTo({ url: '/pages/companion/list' })
    },
    goGuide() {
      uni.navigateTo({ url: '/pages/guide/index' })
    }
  }
}
</script>

<style scoped>
.page { padding: 20rpx 24rpx 140rpx; background: var(--xlv-paper); min-height: 100vh; }
.hero { position: relative; overflow: hidden; border-radius: var(--xlv-radius-lg); background: linear-gradient(158deg, #2F5A44 0%, #1F3D2F 66%, #172F24 100%); padding: 44rpx 32rpx 40rpx; box-shadow: 0 18rpx 44rpx rgba(22,33,28,.18); }
.hero-kicker { font-size: 21rpx; letter-spacing: 3rpx; color: rgba(255,255,255,.5); margin-bottom: 12rpx; }
.hero-title { font-size: 46rpx; font-weight: 700; color: #fff; letter-spacing: 3rpx; line-height: 1.3; }
.hero-desc { margin-top: 12rpx; font-size: 24rpx; color: rgba(255,255,255,.68); line-height: 1.55; }
.grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20rpx; }
.card { background: var(--xlv-card); border-radius: var(--xlv-radius-lg); padding: 32rpx 28rpx; border: 1rpx solid rgba(31,61,47,.06); box-shadow: var(--xlv-shadow); }
.card:active { transform: scale(.985); }
.icon { width: 88rpx; height: 88rpx; border-radius: 24rpx; display: flex; align-items: center; justify-content: center; font-size: 40rpx; background: rgba(45,90,61,.08); margin-bottom: 18rpx; }
.icon.gold { background: var(--xlv-gold-soft); }
.icon.moss { background: rgba(110,143,125,.18); }
.icon.clay { background: rgba(191,74,60,.10); }
.icon.sky { background: rgba(74,111,165,.10); }
.icon.plum { background: rgba(122,96,148,.12); }
.label { font-size: 30rpx; font-weight: 700; color: var(--xlv-ink); }
.hint { margin-top: 8rpx; font-size: 23rpx; color: var(--xlv-muted); }
.extra { background: var(--xlv-card); border-radius: var(--xlv-radius-lg); border: 1rpx solid rgba(31,61,47,.06); box-shadow: var(--xlv-shadow); overflow: hidden; }
.link-card { display: flex; align-items: center; padding: 28rpx; border-bottom: 1rpx solid rgba(31,61,47,.06); }
.link-card:last-child { border-bottom: none; }
.link-icon { width: 64rpx; height: 64rpx; border-radius: 18rpx; display: flex; align-items: center; justify-content: center; font-size: 30rpx; margin-right: 20rpx; background: rgba(45,90,61,.08); }
.link-icon.gold { background: var(--xlv-gold-soft); }
.link-icon.plum { background: rgba(122,96,148,.12); }
.link-icon.sky { background: rgba(74,111,165,.10); }
.link-text { flex: 1; font-size: 29rpx; font-weight: 600; color: var(--xlv-ink); }
.link-hint { font-size: 22rpx; color: var(--xlv-muted); margin-right: 10rpx; }
.arrow { color: var(--xlv-muted); font-size: 32rpx; }
</style>
