<template>
  <view class="page">
    <!-- 主视觉：问候 + 搜索 -->
    <view class="hero">
      <view class="hero-glow" />
      <view class="hero-kicker">大学生旅游 · XINGLV</view>
      <view class="hero-title">把假期，过成远方</view>
      <view class="hero-desc">规划 · 出行 · 点亮 · 兑换，一站式假期生活</view>
      <view class="search-bar" @tap="goSearch">
        <text class="search-icon">🔍</text>
        <text class="search-placeholder">{{ searchKeyword || '搜目的地、实践机会' }}</text>
        <text class="search-btn">搜索</text>
      </view>
    </view>

    <!-- 轮播 / 活动横幅 -->
    <view class="banner-wrap" v-if="banners.length">
      <swiper class="banner-swiper" indicator-dots indicator-color="rgba(255,255,255,.4)" indicator-active-color="#E6C766" autoplay circular interval="4000" duration="400">
        <swiper-item v-for="(b, i) in banners" :key="i" @tap="onBannerTap(b)">
          <view class="banner-item" :style="bannerStyles[i]">
            <view class="banner-inner">
              <text class="banner-title">{{ b.title || '大学生旅游' }}</text>
              <text class="banner-cta">去看看 ›</text>
            </view>
          </view>
        </swiper-item>
      </swiper>
    </view>
    <view class="promo-card" v-else @tap="goRewards">
      <view class="promo-left">
        <text class="promo-title">点亮得星星，兑换好物</text>
        <text class="promo-desc">规划假期 · 出行服务 · 兼职志愿 · 丰富体验</text>
      </view>
      <text class="promo-btn">去兑换</text>
    </view>

    <!-- 地图：当前所在位置 -->
    <view class="map-card">
      <view class="map-head">
        <view class="map-head-left">
          <text class="map-title">当前定位</text>
          <text class="map-sub" v-if="locationError">{{ locationError }}</text>
          <text class="map-sub" v-else>为你发现身边的假期</text>
        </view>
        <text class="map-action" @tap="onMapTap">查看 ›</text>
      </view>
      <map
        class="home-map"
        :latitude="mapLatitude"
        :longitude="mapLongitude"
        :scale="mapScale"
        show-location
        @tap="onMapTap"
      />
      <view class="map-nearby" v-if="nearbyList.length">
        <scroll-view scroll-x class="nearby-scroll" show-scrollbar="false">
          <view class="nearby-item" v-for="(d, ni) in nearbyList" :key="d.id" @tap="goDetail(d.id)">
            <view class="nearby-cover" :style="nearbyStyles[ni]">
              <text class="nearby-cover-text" v-if="!d.coverImage">{{ d.name ? d.name.slice(0,1) : '' }}</text>
            </view>
            <text class="nearby-name">{{ d.name }}</text>
          </view>
        </scroll-view>
      </view>
    </view>

    <!-- 服务图标网格 -->
    <view class="section-title">出行服务<text class="section-desc">一站式搞定</text></view>
    <view class="service-grid">
      <view class="service-item" @tap="goServiceTaxi">
        <view class="service-icon clay">🚗</view>
        <text class="service-label">打车</text>
      </view>
      <view class="service-item" @tap="goServiceTicket">
        <view class="service-icon gold">🎫</view>
        <text class="service-label">门票</text>
      </view>
      <view class="service-item" @tap="goServiceHotel">
        <view class="service-icon moss">🏨</view>
        <text class="service-label">订酒店</text>
      </view>
      <view class="service-item" @tap="goOpportunity">
        <view class="service-icon sky">📋</view>
        <text class="service-label">兼职·志愿</text>
      </view>
      <view class="service-item" @tap="goBudget">
        <view class="service-icon gold">📊</view>
        <text class="service-label">预算规划</text>
      </view>
      <view class="service-item" @tap="goCheckin">
        <view class="service-icon plum">✨</view>
        <text class="service-label">我的点亮</text>
      </view>
      <view class="service-item" @tap="goRewards">
        <view class="service-icon gold">⭐</view>
        <text class="service-label">星星兑换</text>
      </view>
      <view class="service-item" @tap="goService">
        <view class="service-icon moss">⋮</view>
        <text class="service-label">更多</text>
      </view>
    </view>

    <!-- 统计条 -->
    <view class="stats-bar" v-if="userStars !== null">
      <view class="stat" @tap="goCheckin">
        <text class="stat-num">{{ userStars }}</text>
        <text class="stat-label">我的星星</text>
      </view>
      <view class="stat-divider" />
      <view class="stat" @tap="goCheckin">
        <text class="stat-icon">✨</text>
        <text class="stat-label">点亮</text>
      </view>
      <view class="stat-divider" />
      <view class="stat" @tap="goRewards">
        <text class="stat-icon">⭐</text>
        <text class="stat-label">兑换</text>
      </view>
    </view>

    <!-- 假期必看 · 榜单 -->
    <view class="section-title">假期必看<text class="section-desc">精选玩法</text></view>
    <view class="hot-grid">
      <view class="hot-card" @tap="goDetail(featuredDest.id)" v-if="featuredDest">
        <view class="hot-cover" :style="featuredDestStyle">
          <text class="hot-cover-text">{{ featuredDest.name ? featuredDest.name.slice(0,1) : '' }}</text>
          <text class="hot-cover-tag">热门</text>
        </view>
        <view class="hot-body">
          <text class="hot-name">热门目的地</text>
          <text class="hot-meta">共 {{ list.length }} 个</text>
        </view>
      </view>
      <view class="hot-card" @tap="goBudget">
        <view class="hot-cover budget-cover">
          <text class="hot-cover-text">📊</text>
        </view>
        <view class="hot-body">
          <text class="hot-name">按预算选线路</text>
          <text class="hot-meta">智能推荐</text>
        </view>
      </view>
      <view class="hot-card" @tap="goOpportunity">
        <view class="hot-cover opp-cover">
          <text class="hot-cover-text">📋</text>
        </view>
        <view class="hot-body">
          <text class="hot-name">实践机会</text>
          <text class="hot-meta">兼职·志愿</text>
        </view>
      </view>
      <view class="hot-card" @tap="goService">
        <view class="hot-cover service-cover">
          <text class="hot-cover-text">🚗</text>
        </view>
        <view class="hot-body">
          <text class="hot-name">出行服务</text>
          <text class="hot-meta">打车/酒店/门票</text>
        </view>
      </view>
      <view class="hot-card" @tap="goCompanion">
        <view class="hot-cover companion-cover">
          <text class="hot-cover-text">👥</text>
        </view>
        <view class="hot-body">
          <text class="hot-name">约伴</text>
          <text class="hot-meta">找搭子一起玩</text>
        </view>
      </view>
      <view class="hot-card" @tap="goGuide">
        <view class="hot-cover guide-cover">
          <text class="hot-cover-text">📝</text>
        </view>
        <view class="hot-body">
          <text class="hot-name">攻略·游记</text>
          <text class="hot-meta">话题发现</text>
        </view>
      </view>
    </view>

    <!-- 互动 / 动态 -->
    <view class="section-title">假期动态<text class="section-desc">写下你的旅程</text></view>
    <view class="dynamic-card" @tap="goDetail(list[0].id)" v-if="list.length">
      <view class="dynamic-left">
        <text class="dynamic-title">写体验 · 得星星</text>
        <text class="dynamic-desc">去目的地写真实体验，点亮拿星兑换好物</text>
      </view>
      <text class="dynamic-btn">去写</text>
    </view>
    <view class="dynamic-card" @tap="goBudget" v-else>
      <view class="dynamic-left">
        <text class="dynamic-title">规划你的假期</text>
        <text class="dynamic-desc">输入预算与天数，智能推荐线路与住宿</text>
      </view>
      <text class="dynamic-btn">去规划</text>
    </view>

    <!-- 推荐目的地 -->
    <view class="section-title">推荐目的地<text class="section-desc" v-if="list.length">共 {{ filteredList.length }} 处</text></view>
    <view class="list" v-if="filteredList.length">
      <view class="item" v-for="item in filteredList" :key="item.id" @tap="goDetail(item.id)">
        <view class="item-inner">
          <view class="avatar" v-if="!item.coverImage">{{ item.name ? item.name.slice(0, 1) : '' }}</view>
          <image v-else class="cover-img" :src="item.coverImage" mode="aspectFill" />
          <view class="info">
            <text class="name">{{ item.name }}</text>
            <text class="meta">{{ item.region }} · 点亮可得 {{ item.starReward || 1 }} 星</text>
          </view>
          <text class="arrow">›</text>
        </view>
      </view>
    </view>
    <view class="empty-state" v-else-if="loadError" @tap="load()">
      <view class="empty-icon">🔄</view>
      <text>加载失败</text>
      <text class="empty-hint">点击重试</text>
    </view>
    <view class="empty-state" v-else-if="!loading">
      <view class="empty-icon">🗺️</view>
      <text>{{ searchKeyword ? '无匹配目的地' : '暂无目的地' }}</text>
      <text class="empty-hint" v-if="searchKeyword" @tap="searchKeyword = ''">清空搜索</text>
    </view>
    <view class="loading-wrap" v-else>加载中...</view>
  </view>
</template>

<script>
import api from '@/utils/api.js'
export default {
  data() {
    return {
      list: [],
      banners: [],
      loading: true,
      userStars: null,
      searchKeyword: '',
      mapLatitude: 39.9,
      mapLongitude: 116.4,
      mapScale: 15,
      locationError: '',
      loadError: false,
      _firstShow: true
    }
  },
  computed: {
    filteredList() {
      const list = Array.isArray(this.list) ? this.list : []
      const k = (this.searchKeyword || '').trim().toLowerCase()
      if (!k) return list
      return list.filter(d => {
        const name = (d.name || '').toLowerCase()
        const region = (d.region || '').toLowerCase()
        return name.indexOf(k) >= 0 || region.indexOf(k) >= 0
      })
    },
    featuredDest() {
      const list = Array.isArray(this.list) ? this.list : []
      return list.length ? list[0] : null
    },
    bannerStyles() {
      return (this.banners || []).map(b => {
        if (b && b.image) return { backgroundImage: 'url(' + b.image + ')', backgroundSize: 'cover', backgroundPosition: 'center' }
        return { background: 'linear-gradient(135deg, #33604A 0%, #1F3D2F 100%)' }
      })
    },
    nearbyList() {
      return Array.isArray(this.list) ? this.list.slice(0, 4) : []
    },
    nearbyStyles() {
      return this.nearbyList.map(d => {
        if (d && d.coverImage) return { backgroundImage: 'url(' + d.coverImage + ')', backgroundSize: 'cover' }
        return { background: 'linear-gradient(135deg, #6E8F7D, #2D5A3D)' }
      })
    },
    featuredDestStyle() {
      const d = this.featuredDest
      if (!d) return {}
      if (d.coverImage) return { backgroundImage: 'url(' + d.coverImage + ')', backgroundSize: 'cover' }
      return { background: 'linear-gradient(135deg, #33604A, #1F3D2F)' }
    }
  },
  onLoad() {
    this.load()
    this.loadBanners()
    this.loadUserStars()
    this.getLocation()
  },
  onShow() {
    if (this._firstShow) {
      this._firstShow = false
      return
    }
    this.load()
    this.loadUserStars()
  },
  onPullDownRefresh() {
    Promise.all([this.load(), this.loadBanners(), this.loadUserStars()]).then(() => {}).finally(() => { uni.stopPullDownRefresh() })
  },
  methods: {
    goSearch() {
      const q = (this.searchKeyword || '').trim()
      uni.navigateTo({ url: '/pages/search/index' + (q ? '?keyword=' + encodeURIComponent(q) : '') })
    },
    getLocation() {
      this.locationError = ''
      uni.getLocation({
        type: 'gcj02',
        success: (res) => {
          this.mapLatitude = res.latitude
          this.mapLongitude = res.longitude
        },
        fail: (err) => {
          this.locationError = '定位未开启，显示默认位置'
        }
      })
    },
    onMapTap() {
      // 可扩展：进入全屏地图或目的地列表
      uni.showToast({ title: '当前所在位置', icon: 'none' })
    },
    loadUserStars() {
      return api.user.me().then(res => { this.userStars = (res && res.stars != null) ? res.stars : 0 }).catch(() => { this.userStars = 0 })
    },
    goBudget() { uni.navigateTo({ url: '/pages/budget/index' }) },
    goService() { uni.switchTab({ url: '/pages/service/index' }) },
    goServiceTaxi() { uni.navigateTo({ url: '/pages/service/taxi' }) },
    goServiceTicket() { uni.navigateTo({ url: '/pages/service/ticket' }) },
    goServiceHotel() { uni.navigateTo({ url: '/pages/service/hotel' }) },
    goOpportunity() { uni.navigateTo({ url: '/pages/opportunity/list' }) },
    goCheckin() { uni.navigateTo({ url: '/pages/checkin/index' }) },
    goRewards() { uni.switchTab({ url: '/pages/rewards/index' }) },
    loadBanners() {
      return api.banners.list().then(res => { this.banners = Array.isArray(res) ? res : [] }).catch(() => { this.banners = [] })
    },
    onBannerTap(b) {
      if (b.link && b.link.indexOf('/pages/') === 0) {
        if (['/pages/index','/pages/budget','/pages/checkin','/pages/rewards','/pages/service'].some(p => b.link.indexOf(p) === 0))
          uni.switchTab({ url: b.link })
        else uni.navigateTo({ url: b.link })
      }
    },
    load() {
      this.loading = true
      this.loadError = false
      return api.destinations.list().then(res => { this.list = Array.isArray(res) ? res : [] }).catch(() => { this.list = []; this.loadError = true }).finally(() => { this.loading = false })
    },
    goDetail(id) { uni.navigateTo({ url: '/pages/destination/detail?id=' + id }) },
    goCompanion() { uni.navigateTo({ url: '/pages/companion/list' }) },
    goGuide() { uni.navigateTo({ url: '/pages/guide/index' }) }
  }
}
</script>

<style scoped>
.page { padding: 20rpx 24rpx 140rpx; background: var(--xlv-paper); min-height: 100vh; }

/* Hero */
.hero { position: relative; overflow: hidden; border-radius: var(--xlv-radius-lg); background: linear-gradient(158deg, #2F5A44 0%, #1F3D2F 62%, #172F24 100%); padding: 40rpx 32rpx 32rpx; box-shadow: 0 18rpx 44rpx rgba(22,33,28,.18); }
.hero-glow { position: absolute; right: -140rpx; top: -160rpx; width: 420rpx; height: 420rpx; border-radius: 50%; background: radial-gradient(circle, rgba(201,162,39,.42) 0%, rgba(201,162,39,0) 68%); }
.hero-kicker { display: block; font-size: 22rpx; letter-spacing: 3rpx; color: rgba(255,255,255,.55); margin-bottom: 12rpx; }
.hero-title { display: block; font-size: 46rpx; font-weight: 700; color: #fff; letter-spacing: 2rpx; line-height: 1.3; }
.hero-desc { display: block; margin-top: 10rpx; font-size: 24rpx; color: rgba(255,255,255,.68); }
.search-bar { position: relative; display: flex; align-items: center; margin-top: 28rpx; padding: 22rpx 24rpx; background: rgba(255,255,255,.94); border-radius: 999rpx; }
.search-icon { font-size: 28rpx; margin-right: 14rpx; opacity: .55; }
.search-placeholder { flex: 1; font-size: 27rpx; color: #7C8781; }
.search-btn { font-size: 26rpx; font-weight: 600; color: var(--xlv-forest-2); padding-left: 20rpx; border-left: 1rpx solid rgba(31,61,47,.12); }

/* Banner */
.banner-wrap { margin-top: 24rpx; border-radius: var(--xlv-radius-lg); overflow: hidden; box-shadow: 0 10rpx 28rpx rgba(22,33,28,.10); }
.banner-swiper { height: 232rpx; }
.banner-item { height: 100%; display: flex; align-items: flex-end; padding: 32rpx; }
.banner-inner { display: flex; align-items: center; justify-content: space-between; width: 100%; }
.banner-title { color: #fff; font-size: 34rpx; font-weight: 700; letter-spacing: 1rpx; text-shadow: 0 2rpx 10rpx rgba(0,0,0,.25); }
.banner-cta { color: #2C2410; background: linear-gradient(135deg, #E4C466, #C9A227); font-size: 24rpx; font-weight: 700; padding: 12rpx 26rpx; border-radius: 999rpx; }

/* 无 banner 时的推广卡 */
.promo-card { margin-top: 24rpx; padding: 32rpx; background: linear-gradient(150deg, #2F5A44, #1F3D2F); border-radius: var(--xlv-radius-lg); display: flex; align-items: center; justify-content: space-between; box-shadow: 0 12rpx 30rpx rgba(31,61,47,.18); }
.promo-left { flex: 1; padding-right: 20rpx; }
.promo-title { display: block; color: #fff; font-size: 32rpx; font-weight: 700; margin-bottom: 10rpx; }
.promo-desc { display: block; color: rgba(255,255,255,.66); font-size: 23rpx; line-height: 1.5; }
.promo-btn { flex-shrink: 0; color: #2C2410; background: linear-gradient(135deg, #E4C466, #C9A227); padding: 16rpx 30rpx; border-radius: 999rpx; font-size: 25rpx; font-weight: 700; }

/* 地图卡 */
.map-card { margin-top: 24rpx; background: var(--xlv-card); border-radius: var(--xlv-radius-lg); border: 1rpx solid rgba(31,61,47,.06); box-shadow: var(--xlv-shadow); overflow: hidden; }
.map-head { display: flex; align-items: center; justify-content: space-between; padding: 26rpx 28rpx 20rpx; }
.map-head-left { flex: 1; min-width: 0; }
.map-title { display: block; font-size: 30rpx; font-weight: 700; color: var(--xlv-ink); }
.map-sub { display: block; margin-top: 6rpx; font-size: 23rpx; color: var(--xlv-muted); }
.map-action { font-size: 24rpx; color: var(--xlv-forest-2); font-weight: 600; }
.home-map { width: 100%; height: 300rpx; display: block; }
.map-nearby { padding: 24rpx 24rpx 20rpx; border-top: 1rpx solid rgba(31,61,47,.05); }
.nearby-scroll { white-space: nowrap; }
.nearby-item { display: inline-block; width: 136rpx; margin-right: 18rpx; text-align: center; vertical-align: top; }
.nearby-cover { width: 108rpx; height: 108rpx; margin: 0 auto 10rpx; border-radius: 22rpx; background: linear-gradient(135deg, #6E8F7D, #2D5A3D); color: #fff; font-size: 40rpx; font-weight: 700; line-height: 108rpx; text-align: center; }
.nearby-cover-text { color: #fff; }
.nearby-name { display: block; font-size: 23rpx; color: var(--xlv-ink-2); overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }

/* 服务宫格 */
.service-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20rpx 12rpx; }
.service-item { display: flex; flex-direction: column; align-items: center; padding: 18rpx 6rpx; border-radius: var(--xlv-radius-md); }
.service-icon { width: 92rpx; height: 92rpx; border-radius: 26rpx; display: flex; align-items: center; justify-content: center; font-size: 42rpx; margin-bottom: 12rpx; background: rgba(45,90,61,.08); }
.service-icon.gold { background: var(--xlv-gold-soft); }
.service-icon.moss { background: rgba(110,143,125,.18); }
.service-icon.clay { background: rgba(191,74,60,.10); }
.service-icon.sky { background: rgba(74,111,165,.10); }
.service-icon.plum { background: rgba(122,96,148,.12); }
.service-label { font-size: 23rpx; color: var(--xlv-ink-2); }

/* 统计条 */
.stats-bar { display: flex; align-items: center; justify-content: space-around; margin-top: 24rpx; padding: 28rpx 20rpx; background: var(--xlv-card); border-radius: var(--xlv-radius-lg); border: 1rpx solid rgba(31,61,47,.06); box-shadow: var(--xlv-shadow); }
.stat { display: flex; flex-direction: column; align-items: center; flex: 1; }
.stat-num { font-size: 40rpx; font-weight: 700; color: var(--xlv-forest); letter-spacing: -1rpx; }
.stat-icon { font-size: 34rpx; margin-bottom: 2rpx; }
.stat-label { margin-top: 6rpx; font-size: 22rpx; color: var(--xlv-muted); }
.stat-divider { width: 1rpx; height: 40rpx; background: var(--xlv-line); }

/* 假期必看 */
.hot-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 20rpx; }
.hot-card { background: var(--xlv-card); border-radius: var(--xlv-radius-lg); overflow: hidden; border: 1rpx solid rgba(31,61,47,.06); box-shadow: var(--xlv-shadow); }
.hot-cover { position: relative; height: 190rpx; display: flex; align-items: center; justify-content: center; }
.hot-cover-text { color: #fff; font-size: 52rpx; font-weight: 700; text-shadow: 0 4rpx 14rpx rgba(0,0,0,.22); }
.hot-cover-tag { position: absolute; left: 18rpx; top: 18rpx; padding: 4rpx 16rpx; border-radius: 999rpx; background: rgba(201,162,39,.92); color: #3A2E07; font-size: 20rpx; font-weight: 700; }
.budget-cover { background: linear-gradient(140deg, #4B6F8C, #2F4A60); }
.opp-cover { background: linear-gradient(140deg, #B58A3C, #8A6626); }
.service-cover { background: linear-gradient(140deg, #58795F, #2D5A3D); }
.companion-cover { background: linear-gradient(140deg, #7A6094, #503F66); }
.guide-cover { background: linear-gradient(140deg, #B98F52, #8E6A32); }
.hot-body { padding: 20rpx 22rpx 22rpx; }
.hot-name { display: block; font-size: 28rpx; font-weight: 600; color: var(--xlv-ink); }
.hot-meta { display: block; margin-top: 6rpx; font-size: 22rpx; color: var(--xlv-muted); }

/* 动态卡 */
.dynamic-card { padding: 30rpx 32rpx; background: linear-gradient(150deg, #2A4E3B, #1B3527); border-radius: var(--xlv-radius-lg); display: flex; align-items: center; justify-content: space-between; box-shadow: 0 14rpx 34rpx rgba(22,33,28,.16); }
.dynamic-left { flex: 1; padding-right: 20rpx; }
.dynamic-title { display: block; font-size: 32rpx; font-weight: 700; color: #fff; margin-bottom: 10rpx; }
.dynamic-desc { display: block; font-size: 23rpx; color: rgba(255,255,255,.64); line-height: 1.5; }
.dynamic-btn { flex-shrink: 0; font-size: 25rpx; font-weight: 700; color: #2C2410; background: linear-gradient(135deg, #E4C466, #C9A227); padding: 16rpx 30rpx; border-radius: 999rpx; }

/* 目的地列表 */
.list { display: flex; flex-direction: column; }
.item { margin-bottom: 20rpx; }
.item-inner { display: flex; align-items: center; padding: 24rpx; background: var(--xlv-card); border-radius: var(--xlv-radius-lg); border: 1rpx solid rgba(31,61,47,.06); box-shadow: 0 6rpx 20rpx rgba(22,33,28,.04); }
.avatar { width: 100rpx; height: 100rpx; border-radius: 22rpx; background: linear-gradient(135deg, #6E8F7D, #2D5A3D); color: #fff; font-size: 40rpx; font-weight: 700; display: flex; align-items: center; justify-content: center; margin-right: 24rpx; flex-shrink: 0; }
.cover-img { width: 100rpx; height: 100rpx; border-radius: 22rpx; margin-right: 24rpx; flex-shrink: 0; }
.info { flex: 1; min-width: 0; }
.name { display: block; font-size: 31rpx; font-weight: 600; color: var(--xlv-ink); }
.meta { display: block; margin-top: 8rpx; font-size: 23rpx; color: var(--xlv-muted); }
.arrow { color: var(--xlv-muted); font-size: 34rpx; margin-left: 12rpx; }
</style>
