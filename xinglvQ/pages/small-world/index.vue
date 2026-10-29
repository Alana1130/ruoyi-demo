<template>
  <view class="page">
    <view class="hero">
      <view class="title">我的小世界</view>
      <view class="desc">解锁地区得宠物与美食经营权，用星星交易、饲养宠物</view>
    </view>
    <view class="overview">
      <view class="stat" @tap="tab = 'pets'">
        <text class="num">{{ overview.petCount || 0 }}</text>
        <text class="label">宠物</text>
      </view>
      <view class="stat" @tap="tab = 'licenses'">
        <text class="num">{{ overview.licenseCount || 0 }}</text>
        <text class="label">经营权</text>
      </view>
      <view class="stat" @tap="goRewards">
        <text class="num">{{ overview.stars || 0 }}</text>
        <text class="label">星星</text>
      </view>
    </view>
    <view class="tabs">
      <view class="tab" :class="{ active: tab === 'pets' }" @tap="tab = 'pets'">我的宠物</view>
      <view class="tab" :class="{ active: tab === 'licenses' }" @tap="tab = 'licenses'">美食经营权</view>
    </view>
    <view class="list" v-if="tab === 'pets'">
      <view class="card pet-card" v-for="(p, i) in pets" :key="p.id">
        <view class="pet-head">
          <text class="icon">{{ p.icon || '🐾' }}</text>
          <view class="info">
            <text class="name">{{ p.nickname || p.name }}</text>
            <text class="meta">{{ p.destinationName }} · Lv.{{ p.level || 1 }} · {{ p.evolutionStageName || '幼年' }}</text>
            <view class="bar">
              <text class="bar-label">经验</text>
              <view class="bar-bg"><view class="bar-fill exp" :style="petBarStyles[i] ? petBarStyles[i].exp : defaultBarStyle"></view></view>
              <text class="bar-num">{{ expText(p, i) }}</text>
            </view>
            <view class="bar">
              <text class="bar-label">饱食</text>
              <view class="bar-bg"><view class="bar-fill hunger" :style="petBarStyles[i] ? petBarStyles[i].hunger : defaultBarStyle"></view></view>
              <text class="bar-num">{{ p.hunger || 0 }}%</text>
            </view>
            <view class="bar">
              <text class="bar-label">心情</text>
              <view class="bar-bg"><view class="bar-fill mood" :style="petBarStyles[i] ? petBarStyles[i].mood : defaultBarStyle"></view></view>
              <text class="bar-num">{{ p.mood || 0 }}%</text>
            </view>
          </view>
        </view>
        <view class="actions">
          <text class="btn-s" @tap="feedPet(p)">喂食</text>
          <text class="btn-s" @tap="playPet(p)">互动</text>
          <text class="btn-s" @tap="setNickname(p)">改昵称</text>
          <text class="btn-s" @tap="goListPet(p)">上架</text>
        </view>
      </view>
      <view class="empty-mini" v-if="pets.length === 0">
        <text>暂无宠物，去点亮目的地解锁吧</text>
        <button v-if="(overview.petCount || 0) === 0 && (overview.licenseCount || 0) === 0" class="btn seed-btn" @tap="seedDemo">领取演示数据</button>
      </view>
    </view>
    <view class="list" v-if="tab === 'licenses'">
      <view class="card license-card" v-for="l in licenses" :key="l.id">
        <text class="icon">{{ l.icon || '🍜' }}</text>
        <text class="name">{{ l.name }}</text>
        <text class="meta">{{ l.destinationName }}</text>
        <text class="btn-s" @tap="goListLicense(l)">上架交易</text>
      </view>
      <view class="empty-mini" v-if="licenses.length === 0">暂无经营权，去点亮目的地解锁吧</view>
    </view>
    <view class="bottom-actions">
      <button class="btn primary" @tap="goTrade">星星交易市场</button>
    </view>
    <view class="mask" v-if="showNickname" @tap="showNickname = false">
      <view class="modal" @tap.stop>
        <view class="modal-title">改昵称</view>
        <input class="inp" v-model="nicknameVal" placeholder="输入昵称" />
        <view class="modal-actions">
          <text @tap="showNickname = false">取消</text>
          <text class="primary" @tap="submitNickname">确定</text>
        </view>
      </view>
    </view>
    <view class="mask" v-if="showListModal" @tap="showListModal = false">
      <view class="modal" @tap.stop>
        <view class="modal-title">{{ listItemType === 'pet' ? '上架宠物' : '上架经营权' }}</view>
        <view class="suggested" v-if="listItemType === 'pet' && listSuggestedStars">建议售价 {{ listSuggestedStars }} 星星（等级与进化越高越高）</view>
        <input class="inp" type="number" v-model="listStars" placeholder="星星数量" />
        <view class="modal-actions">
          <text @tap="showListModal = false">取消</text>
          <text class="primary" @tap="submitList">确定</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import api from '@/utils/api.js'
export default {
  data() {
    return {
      defaultBarStyle: { width: '0%' },
      overview: {},
      pets: [],
      licenses: [],
      tab: 'pets',
      showNickname: false,
      nicknameVal: '',
      currentPetId: null,
      showListModal: false,
      listItemType: '',
      listItemId: null,
      listStars: '',
      listSuggestedStars: 0
    }
  },
  onLoad() {
    this.load()
  },
  onPullDownRefresh() {
    this.load().then(() => uni.stopPullDownRefresh())
  },
  computed: {
    petBarStyles() {
      return (this.pets || []).map(p => {
        const hunger = Math.min(100, Math.max(0, p.hunger != null ? p.hunger : 0))
        const mood = Math.min(100, Math.max(0, p.mood != null ? p.mood : 0))
        const level = p.level != null ? p.level : 1
        const exp = p.exp != null ? p.exp : 0
        const cur = p.expThresholdCurrent != null ? p.expThresholdCurrent : 0
        const next = p.expThresholdNext != null ? p.expThresholdNext : 10
        let expPct = 100
        if (level < 10 && next > cur) expPct = Math.min(100, Math.max(0, ((exp - cur) / (next - cur)) * 100))
        return { hunger: { width: hunger + '%' }, mood: { width: mood + '%' }, exp: { width: expPct + '%' } }
      })
    }
  },
  methods: {
    expText(p, i) {
      const level = p.level != null ? p.level : 1
      if (level >= 10) return '已满级'
      const exp = p.exp != null ? p.exp : 0
      const next = p.expThresholdNext != null ? p.expThresholdNext : 10
      return exp + '/' + next
    },
    load() {
      const p1 = api.smallWorld.overview().then(res => { this.overview = res || {} }).catch(() => {})
      const p2 = api.smallWorld.pets().then(res => { this.pets = res || [] }).catch(() => {})
      const p3 = api.smallWorld.licenses().then(res => { this.licenses = res || [] }).catch(() => {})
      return Promise.all([p1, p2, p3])
    },
    seedDemo() {
      api.smallWorld.seedDemo().then(res => {
        const data = res && res.data !== undefined ? res.data : res
        const a = (data && data.petCountAdded != null) ? data.petCountAdded : 0
        const b = (data && data.licenseCountAdded != null) ? data.licenseCountAdded : 0
        if (a > 0 || b > 0) {
          this.load().then(() => {
            uni.showToast({ title: `已领取${a}只宠物、${b}个经营权` })
          })
        } else {
          uni.showToast({ title: '暂无模板数据，请先执行小世界SQL（xl_small_world.sql 等）', icon: 'none', duration: 3000 })
        }
      }).catch(e => uni.showToast({ title: e.message || '领取失败', icon: 'none' }))
    },
    feedPet(p) {
      api.smallWorld.feedPet(p.id).then(res => {
        if (res.error) { uni.showToast({ title: res.error, icon: 'none' }); return }
        uni.showToast({ title: '喂食成功' })
        this.load()
      }).catch(e => uni.showToast({ title: e.message || '失败', icon: 'none' }))
    },
    playPet(p) {
      api.smallWorld.playPet(p.id).then(res => {
        if (res.error) { uni.showToast({ title: res.error, icon: 'none' }); return }
        uni.showToast({ title: '互动成功' })
        this.load()
      }).catch(e => uni.showToast({ title: e.message || '失败', icon: 'none' }))
    },
    setNickname(p) {
      this.currentPetId = p.id
      this.nicknameVal = p.nickname || p.name || ''
      this.showNickname = true
    },
    submitNickname() {
      if (!this.currentPetId) return
      api.smallWorld.updatePetNickname(this.currentPetId, this.nicknameVal).then(() => {
        uni.showToast({ title: '已修改' })
        this.showNickname = false
        this.load()
      }).catch(() => {})
    },
    goListPet(p) {
      this.listItemType = 'pet'
      this.listItemId = p.id
      this.listSuggestedStars = p.suggestedStars != null ? p.suggestedStars : 0
      this.listStars = String(this.listSuggestedStars || '')
      this.showListModal = true
    },
    goListLicense(l) {
      this.listItemType = 'food_license'
      this.listItemId = l.id
      this.listSuggestedStars = 0
      this.listStars = ''
      this.showListModal = true
    },
    submitList() {
      const stars = parseInt(this.listStars, 10)
      if (!stars || stars <= 0) { uni.showToast({ title: '请输入星星数量', icon: 'none' }); return }
      api.smallWorld.createListing(this.listItemType, this.listItemId, stars).then(res => {
        if (res.error) { uni.showToast({ title: res.error, icon: 'none' }); return }
        uni.showToast({ title: '已上架' })
        this.showListModal = false
        this.load()
      }).catch(e => uni.showToast({ title: e.message || '失败', icon: 'none' }))
    },
    goTrade() {
      uni.navigateTo({ url: '/pages/small-world/trade' })
    },
    goRewards() {
      uni.switchTab({ url: '/pages/rewards/index' })
    }
  }
}
</script>

<style scoped>
.page { padding: 24rpx; padding-bottom: 140rpx; background: linear-gradient(180deg, #e8f5e9 0%, #f2f4f3 30%); min-height: 100vh; }
.hero { margin-bottom: 28rpx; }
.title { font-size: 40rpx; font-weight: 700; color: #1a1a1a; }
.desc { font-size: 26rpx; color: #666; margin-top: 8rpx; }
.overview { display: flex; background: #fff; border-radius: 20rpx; padding: 28rpx; box-shadow: 0 2rpx 16rpx rgba(0,0,0,.06); margin-bottom: 24rpx; }
.stat { flex: 1; text-align: center; }
.stat .num { font-size: 40rpx; font-weight: 700; color: #2d5a3d; display: block; }
.stat .label { font-size: 24rpx; color: #999; }
.tabs { display: flex; gap: 16rpx; margin-bottom: 20rpx; }
.tab { padding: 14rpx 28rpx; background: #fff; border-radius: 24rpx; font-size: 28rpx; color: #666; }
.tab.active { background: #2d5a3d; color: #fff; }
.list { display: flex; flex-direction: column; gap: 20rpx; }
.card { background: #fff; border-radius: 20rpx; padding: 24rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,.06); }
.pet-head { display: flex; align-items: flex-start; }
.pet-head .icon { font-size: 72rpx; margin-right: 24rpx; }
.pet-head .info { flex: 1; min-width: 0; }
.pet-head .name { font-size: 30rpx; font-weight: 600; display: block; }
.pet-head .meta { font-size: 24rpx; color: #999; }
.bar { display: flex; align-items: center; margin-top: 8rpx; font-size: 22rpx; color: #666; }
.bar-label { width: 48rpx; }
.bar-bg { flex: 1; height: 12rpx; background: #eee; border-radius: 6rpx; overflow: hidden; margin: 0 12rpx; }
.bar-fill { height: 100%; border-radius: 6rpx; }
.bar-fill.hunger { background: #ff9800; }
.bar-fill.mood { background: #e91e63; }
.bar-fill.exp { background: #2196f3; }
.suggested { font-size: 24rpx; color: #666; margin-bottom: 16rpx; }
.bar-num { width: 48rpx; text-align: right; }
.actions { margin-top: 16rpx; padding-top: 16rpx; border-top: 1rpx solid #f0f0f0; display: flex; flex-wrap: wrap; gap: 16rpx; }
.btn-s { color: #2d5a3d; font-size: 26rpx; padding: 8rpx 20rpx; border: 1rpx solid #2d5a3d; border-radius: 20rpx; }
.license-card { display: flex; flex-direction: column; align-items: center; text-align: center; }
.license-card .icon { font-size: 56rpx; margin-bottom: 12rpx; }
.license-card .name { font-size: 30rpx; font-weight: 600; }
.license-card .meta { font-size: 24rpx; color: #999; margin-bottom: 16rpx; }
.empty-mini { text-align: center; padding: 40rpx; color: #999; font-size: 26rpx; }
.seed-btn { margin-top: 24rpx; background: #2d5a3d; color: #fff; font-size: 28rpx; }
.bottom-actions { position: fixed; left: 0; right: 0; bottom: 0; padding: 24rpx; background: #fff; border-top: 1rpx solid #eee; }
.btn.primary { background: #2d5a3d; color: #fff; height: 88rpx; line-height: 88rpx; border-radius: 16rpx; font-size: 32rpx; }
.mask { position: fixed; left: 0; right: 0; top: 0; bottom: 0; background: rgba(0,0,0,.4); z-index: 100; display: flex; align-items: center; justify-content: center; }
.modal { width: 560rpx; background: #fff; border-radius: 20rpx; padding: 32rpx; }
.modal-title { font-size: 32rpx; font-weight: 600; margin-bottom: 24rpx; }
.inp { width: 100%; padding: 24rpx; border: 1rpx solid #eee; border-radius: 12rpx; box-sizing: border-box; margin-bottom: 24rpx; }
.modal-actions { display: flex; justify-content: flex-end; gap: 32rpx; font-size: 30rpx; }
.modal-actions .primary { color: #2d5a3d; font-weight: 600; }
</style>
