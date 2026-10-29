<template>
  <view class="page">
    <!-- 用户信息卡片 -->
    <view class="user-card">
      <view class="user-glow" />
      <view class="avatar-wrap">
        <image v-if="user.avatar" class="avatar" :src="user.avatar" mode="aspectFill" />
        <view v-else class="avatar placeholder">{{ (user.nickname || '我').slice(0, 1) }}</view>
      </view>
      <view class="info">
        <view class="nickname">{{ user.nickname || '旅行者' }}</view>
        <view class="school-tag" v-if="user.schoolName">{{ user.schoolName }} · 已认证</view>
        <view class="stars-wrap">
          <text class="stars-label">星星</text>
          <text class="stars-num">{{ user.stars || 0 }}</text>
        </view>
      </view>
    </view>

    <!-- 数据统计条 -->
    <view class="stats-row" v-if="!loading">
      <view class="stats-item">
        <text class="stats-num">{{ user.stars || 0 }}</text>
        <text class="stats-label">星星</text>
      </view>
      <view class="stats-divider" />
      <view class="stats-item" @tap="goCheckin">
        <text class="stats-num">{{ checkinCount }}</text>
        <text class="stats-label">已点亮</text>
      </view>
      <view class="stats-divider" />
      <view class="stats-item" @tap="goExchanges">
        <text class="stats-num">{{ exchangeCount }}</text>
        <text class="stats-label">已兑换</text>
      </view>
    </view>

    <!-- 我的足迹 -->
    <view class="footprint card-base" v-if="!loading">
      <view class="footprint-label">MY JOURNEY</view>
      <view class="footprint-title">我的假期足迹</view>
      <view class="footprint-desc">{{ footprintText }}</view>
      <view class="footprint-actions">
        <view class="fp-btn solid" @tap="goBudget">规划下次旅行</view>
        <view class="fp-btn" @tap="goOpportunityList">看看实践机会</view>
      </view>
    </view>

    <!-- 功能入口 -->
    <view class="menu">
      <view class="menu-item" @tap="goCheckin">
        <text class="menu-icon gold">✨</text>
        <text class="menu-text">我的点亮</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @tap="goExchanges">
        <text class="menu-icon gold">🎁</text>
        <text class="menu-text">我的兑换</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @tap="goRewards">
        <text class="menu-icon gold">⭐</text>
        <text class="menu-text">去兑换好物</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @tap="goSmallWorld">
        <text class="menu-icon moss">🌍</text>
        <text class="menu-text">小世界</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @tap="goCountdown">
        <text class="menu-icon sky">⏱️</text>
        <text class="menu-text">出行倒计时</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @tap="goPlan">
        <text class="menu-icon plum">📅</text>
        <text class="menu-text">学期·假期计划</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @tap="goIdle">
        <text class="menu-icon clay">📦</text>
        <text class="menu-text">二手闲置</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @tap="goSchoolCircle">
        <text class="menu-icon sky">🏫</text>
        <text class="menu-text">学校圈</text>
        <text class="menu-arrow">›</text>
      </view>
    </view>

    <view class="menu sec">
      <view class="menu-item" @tap="goSchoolVerify">
        <text class="menu-icon moss">🎓</text>
        <text class="menu-text">学校认证</text>
        <text class="menu-tip" v-if="user.schoolVerified">已认证</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @tap="goAccount">
        <text class="menu-icon moss">🔐</text>
        <text class="menu-text">账号管理</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @tap="goNotifications">
        <text class="menu-icon clay">📬</text>
        <text class="menu-text">消息</text>
        <view class="badge" v-if="notificationUnread > 0">{{ notificationUnread > 99 ? '99+' : notificationUnread }}</view>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @tap="goOpportunityList">
        <text class="menu-icon sky">📋</text>
        <text class="menu-text">实践机会</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @tap="goAddressList">
        <text class="menu-icon plum">📍</text>
        <text class="menu-text">收货地址</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @tap="goProfile">
        <text class="menu-icon moss">✏️</text>
        <text class="menu-text">编辑资料</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @tap="goAbout">
        <text class="menu-icon moss">ℹ️</text>
        <text class="menu-text">关于我们</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item logout" @tap="logout">
        <text class="menu-icon">🚪</text>
        <text class="menu-text">退出登录</text>
        <text class="menu-arrow">›</text>
      </view>
    </view>

    <view class="loading" v-if="loading">加载中...</view>
  </view>
</template>

<script>
import api from '@/utils/api.js'
import request from '@/utils/request.js'
export default {
  data() {
    return { user: { nickname: '旅行者', stars: 0 }, loading: true, checkinCount: 0, exchangeCount: 0, notificationUnread: 0 }
  },
  computed: {
    footprintText() {
      const c = this.checkinCount || 0
      const s = this.user.stars || 0
      const e = this.exchangeCount || 0
      if (c === 0 && e === 0) return '去探索目的地、点亮拿星星，兑换好物丰富假期吧'
      const parts = []
      if (c > 0) parts.push('已走过 ' + c + ' 个目的地')
      if (s > 0) parts.push('获得 ' + s + ' 星')
      if (e > 0) parts.push('兑换 ' + e + ' 次好物')
      return parts.join('，') + '。继续探索，让假期更精彩～'
    }
  },
  onShow() {
    this.loadMe()
    this.loadCounts()
  },
  methods: {
    loadCounts() {
      api.checkin.my().then(res => { this.checkinCount = (res && res.length) ? res.length : 0 }).catch(() => { this.checkinCount = 0 })
      api.rewards.myRecords().then(res => {
        const arr = Array.isArray(res) ? res : (res && res.list ? res.list : [])
        this.exchangeCount = (arr && arr.length) ? arr.length : 0
      }).catch(() => { this.exchangeCount = 0 })
      api.notifications.unreadCount().then(res => { this.notificationUnread = (res && res.count != null) ? res.count : 0 }).catch(() => { this.notificationUnread = 0 })
    },
    loadMe() {
      this.loading = true
      api.user.me().then(res => {
        this.user = res || { nickname: '旅行者', stars: 0 }
      }).catch(() => {
        this.user = { nickname: '旅行者', stars: 0 }
      }).finally(() => {
        this.loading = false
      })
    },
    goCheckin() {
      uni.navigateTo({ url: '/pages/checkin/index' })
    },
    goExchanges() {
      uni.navigateTo({ url: '/pages/my/exchanges' })
    },
    goRewards() {
      uni.switchTab({ url: '/pages/rewards/index' })
    },
    goSmallWorld() {
      uni.navigateTo({ url: '/pages/small-world/index' })
    },
    goCountdown() {
      uni.navigateTo({ url: '/pages/countdown/index' })
    },
    goPlan() {
      uni.navigateTo({ url: '/pages/plan/index' })
    },
    goIdle() {
      uni.navigateTo({ url: '/pages/idle/index' })
    },
    goNotifications() {
      uni.navigateTo({ url: '/pages/my/notifications' })
    },
    goOpportunityList() {
      uni.navigateTo({ url: '/pages/opportunity/list' })
    },
    goAddressList() {
      uni.navigateTo({ url: '/pages/my/address-list' })
    },
    goProfile() {
      uni.navigateTo({ url: '/pages/my/profile' })
    },
    goAbout() {
      uni.showModal({
        title: '关于我们',
        content: '大学生旅游 · 规划假期、出行服务、点亮得星、兑换好物、兼职志愿、讨论约伴，丰富大学假期。使用即表示同意用户协议与隐私政策，我们保护您的个人信息安全。',
        confirmText: '知道了',
        showCancel: true,
        cancelText: '用户协议',
        success: (res) => {
          if (res.cancel) uni.showToast({ title: '协议页可后续在设置中查看', icon: 'none' })
        }
      })
    },
    goAccount() {
      uni.navigateTo({ url: '/pages/my/account' })
    },
    goSchoolVerify() {
      uni.navigateTo({ url: '/pages/my/school-verify' })
    },
    goSchoolCircle() {
      uni.navigateTo({ url: '/pages/my/school-circle' })
    },
    logout() {
      uni.showModal({
        title: '退出登录',
        content: '退出后需重新登录或使用游客身份',
        success: (res) => {
          if (res.confirm) {
            api.auth.logout().catch(() => {})
            request.clearToken()
            uni.showToast({ title: '已退出', icon: 'none' })
            setTimeout(() => {
              uni.navigateTo({ url: '/pages/auth/index' })
            }, 300)
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.page { padding: 20rpx 24rpx 140rpx; background: var(--xlv-paper); min-height: 100vh; }

/* 用户卡 */
.user-card { position: relative; overflow: hidden; display: flex; align-items: center; padding: 40rpx 32rpx; background: linear-gradient(152deg, #2F5A44 0%, #1F3D2F 64%, #172F24 100%); border-radius: var(--xlv-radius-lg); box-shadow: 0 18rpx 44rpx rgba(22,33,28,.18); }
.user-glow { position: absolute; right: -120rpx; top: -160rpx; width: 380rpx; height: 380rpx; border-radius: 50%; background: radial-gradient(circle, rgba(201,162,39,.4) 0%, rgba(201,162,39,0) 70%); }
.avatar-wrap { margin-right: 26rpx; flex-shrink: 0; }
.avatar { width: 124rpx; height: 124rpx; border-radius: 50%; background: rgba(255,255,255,.28); border: 2rpx solid rgba(255,255,255,.5); }
.avatar.placeholder { display: flex; align-items: center; justify-content: center; color: #fff; font-size: 50rpx; font-weight: 700; }
.info { flex: 1; min-width: 0; }
.nickname { font-size: 38rpx; font-weight: 700; color: #fff; margin-bottom: 10rpx; letter-spacing: .5rpx; }
.school-tag { display: inline-block; font-size: 21rpx; color: #E8D49A; background: rgba(201,162,39,.18); border: 1rpx solid rgba(201,162,39,.35); padding: 4rpx 14rpx; border-radius: 999rpx; margin-bottom: 10rpx; }
.stars-wrap { display: flex; align-items: baseline; }
.stars-label { font-size: 23rpx; color: rgba(255,255,255,.62); margin-right: 10rpx; }
.stars-num { font-size: 42rpx; font-weight: 700; color: #EBD494; letter-spacing: -.5rpx; }

/* 统计 */
.stats-row { display: flex; align-items: center; margin-top: 22rpx; padding: 28rpx 20rpx; background: var(--xlv-card); border-radius: var(--xlv-radius-lg); border: 1rpx solid rgba(31,61,47,.06); box-shadow: var(--xlv-shadow); }
.stats-item { flex: 1; text-align: center; }
.stats-num { display: block; font-size: 38rpx; font-weight: 700; color: var(--xlv-forest); letter-spacing: -.5rpx; }
.stats-label { display: block; margin-top: 6rpx; font-size: 22rpx; color: var(--xlv-muted); }
.stats-divider { width: 1rpx; height: 44rpx; background: var(--xlv-line); }

/* 足迹 */
.footprint { margin-top: 22rpx; padding: 30rpx; box-shadow: var(--xlv-shadow); }
.footprint-label { font-size: 20rpx; letter-spacing: 3rpx; color: var(--xlv-gold); margin-bottom: 8rpx; }
.footprint-title { font-size: 31rpx; font-weight: 700; color: var(--xlv-ink); margin-bottom: 12rpx; }
.footprint-desc { font-size: 25rpx; color: var(--xlv-ink-2); line-height: 1.6; margin-bottom: 24rpx; }
.footprint-actions { display: flex; gap: 18rpx; }
.fp-btn { flex: 1; text-align: center; padding: 20rpx; border-radius: var(--xlv-radius-md); font-size: 25rpx; color: var(--xlv-forest-2); background: rgba(45,90,61,.08); }
.fp-btn.solid { background: linear-gradient(135deg, #3A6A4B, #23503A); color: #fff; font-weight: 600; }

/* 菜单 */
.menu { margin-top: 22rpx; background: var(--xlv-card); border-radius: var(--xlv-radius-lg); border: 1rpx solid rgba(31,61,47,.06); box-shadow: var(--xlv-shadow); overflow: hidden; }
.menu.sec { margin-top: 22rpx; }
.menu-item { position: relative; display: flex; align-items: center; padding: 24rpx 26rpx; border-bottom: 1rpx solid rgba(31,61,47,.055); }
.menu-item:last-child { border-bottom: none; }
.menu-icon { width: 64rpx; height: 64rpx; border-radius: 18rpx; background: rgba(45,90,61,.07); display: flex; align-items: center; justify-content: center; font-size: 30rpx; margin-right: 20rpx; flex-shrink: 0; }
.menu-icon.gold { background: var(--xlv-gold-soft); }
.menu-icon.moss { background: rgba(110,143,125,.18); }
.menu-icon.sky { background: rgba(74,111,165,.10); }
.menu-icon.plum { background: rgba(122,96,148,.12); }
.menu-icon.clay { background: rgba(191,74,60,.10); }
.menu-text { flex: 1; font-size: 29rpx; color: var(--xlv-ink); }
.menu-arrow { font-size: 32rpx; color: #C6C0B2; }
.menu-tip { font-size: 22rpx; color: var(--xlv-forest-2); margin-right: 10rpx; }
.menu-item.logout .menu-text { color: var(--xlv-muted); }
.badge { position: absolute; left: 68rpx; top: 50%; transform: translateY(-50%); min-width: 32rpx; height: 32rpx; line-height: 32rpx; padding: 0 8rpx; background: var(--xlv-danger); color: #fff; font-size: 19rpx; border-radius: 999rpx; text-align: center; }
.loading { text-align: center; padding: 40rpx; color: var(--xlv-muted); font-size: 26rpx; }
</style>
