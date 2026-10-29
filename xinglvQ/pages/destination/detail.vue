<template>
  <view class="page page-wrap">
    <view class="loading-wrap" v-if="loading">加载中...</view>
    <template v-else-if="data">
      <view class="card head card-base">
        <view class="head-kicker">DESTINATION</view>
        <view class="name">{{ data.name }}</view>
        <view class="desc">{{ data.description }}</view>
        <view class="want-gone">
          <text class="stat">有 {{ data.wantCount || 0 }} 人想去 · {{ data.goneCount || 0 }} 人去过</text>
          <button class="btn btn-want" :class="{ on: data.userWanted }" @tap="toggleWant">{{ data.userWanted ? '已想去' : '想去' }}</button>
        </view>
        <view class="act">
          <text class="stars-badge">点亮可得 {{ data.starReward || 1 }} 星</text>
          <button class="btn" :disabled="lighting || lightResult.already" @tap="light">{{ lightResult.already ? '已点亮' : lighting ? '点亮中...' : '点亮此地' }}</button>
        </view>
        <view class="msg success" v-if="lightResult.success">
          +{{ lightResult.starsEarned }} 星，当前共 {{ lightResult.userStars }} 星
          <text class="inline-link" @tap.stop="goOpportunity"> · 看看当地实践机会</text>
        </view>
        <view class="msg err" v-if="lightResult.error">{{ lightResult.error }}</view>
        <text class="inline-link" style="margin-top:12rpx;display:block" @tap.stop="goWriteGuide">写攻略 · 分享你的游记</text>
      </view>

      <view class="section-title">地图一览</view>
      <view class="map-wrap card-base">
        <map
          class="poi-map"
          :latitude="mapCenter.lat"
          :longitude="mapCenter.lng"
          :scale="mapScale"
          :markers="mapMarkers"
          show-location
          @markertap="onMarkerTap"
        />
        <view class="map-tip" v-if="mapMarkers.length === 0">为景点、美食、住宿配置经纬度后，可在此查看分布</view>
      </view>

      <view class="section-title">推荐线路</view>
      <view class="budget-hint" @tap="goBudget">按预算规划此地线路 →</view>
      <view class="card card-base" v-for="r in routes" :key="r.id" @tap="goRoute(r.id)">
        <view class="rname">{{ r.name }}</view>
        <view class="rmeta">{{ r.days }} 天 · 预算约 ¥{{ r.totalBudgetMin }}–{{ r.totalBudgetMax }}</view>
      </view>
      <view class="empty-mini" v-if="routes.length === 0">暂无线路</view>

      <view class="section-title">打车 / 出行</view>
      <view class="card card-base" v-for="t in taxis" :key="t.id">
        <view class="rname">{{ t.name }}</view>
        <view class="rmeta" v-if="t.appType">{{ t.appType }}</view>
        <view class="rdesc" v-if="t.description">{{ t.description }}</view>
        <view class="rdesc" v-if="t.priceApprox">参考：{{ t.priceApprox }}</view>
        <view class="link-wrap" v-if="t.link">
          <text class="link" @tap="openLink(t.link)">打开叫车</text>
        </view>
      </view>
      <view class="empty-mini" v-if="taxis.length === 0">暂无打车信息</view>

      <view class="section-title">门票</view>
      <view class="card card-base" v-for="t in tickets" :key="t.id">
        <view class="rname">{{ t.name }}</view>
        <view class="rmeta">¥{{ t.price }} {{ t.originalPrice ? '门市价¥' + t.originalPrice : '' }}</view>
        <view class="rdesc" v-if="t.description">{{ t.description }}</view>
        <view class="link-wrap" v-if="t.buyLink">
          <text class="link" @tap="openLink(t.buyLink)">购买门票</text>
        </view>
      </view>
      <view class="empty-mini" v-if="tickets.length === 0">暂无门票信息</view>

      <view class="section-title">景点</view>
      <view class="card card-base" v-for="a in (data.attractions || [])" :key="a.id">
        <view class="rname">{{ a.name }}</view>
        <view class="rmeta">门票 {{ a.ticketPrice ? '¥' + a.ticketPrice : '免费' }} {{ a.aiVerified ? '· 已核验' : '' }}</view>
        <view class="rdesc" v-if="a.description">{{ a.description }}</view>
        <view class="exp-wrap">
          <text class="exp-link" @tap="goWriteExp('attraction', a.id, a.name)">写体验</text>
          <view class="exp-list" v-if="(expMap['attraction_'+a.id] || []).length">
            <view class="exp-item" v-for="e in expMap['attraction_'+a.id]" :key="e.id">
              <text class="exp-content">{{ e.content }}</text>
              <text class="exp-time">{{ formatTime(e.createTime) }}</text>
            </view>
          </view>
        </view>
      </view>

      <view class="section-title">美食</view>
      <view class="card card-base" v-for="f in (data.foods || [])" :key="f.id">
        <view class="rname">{{ f.name }}</view>
        <view class="rmeta">{{ f.priceRange }} {{ f.aiVerified ? '· 已核验' : '' }}</view>
        <view class="exp-wrap">
          <text class="exp-link" @tap="goWriteExp('food', f.id, f.name)">写体验</text>
          <view class="exp-list" v-if="(expMap['food_'+f.id] || []).length">
            <view class="exp-item" v-for="e in expMap['food_'+f.id]" :key="e.id">
              <text class="exp-content">{{ e.content }}</text>
              <text class="exp-time">{{ formatTime(e.createTime) }}</text>
            </view>
          </view>
        </view>
      </view>

      <view class="section-title">住宿参考</view>
      <view class="card card-base" v-for="a in (data.accommodations || [])" :key="a.id">
        <view class="rname">{{ a.name }}</view>
        <view class="rmeta">约 ¥{{ a.pricePerNight }}/晚</view>
        <view class="link-wrap"><text class="link" @tap="bookHotel">订酒店</text></view>
      </view>

      <view class="section-title">当地实践机会</view>
      <view class="card card-base" v-for="o in (data.opportunities || [])" :key="o.id" @tap="goOpportunity(o.id)">
        <view class="opp-tag" :class="o.type">{{ o.type === 'volunteer' ? '志愿' : (o.type === 'part_time' ? '兼职' : '') }}</view>
        <view class="rname">{{ o.title }}</view>
        <view class="rmeta" v-if="o.region">{{ o.region }}</view>
      </view>
      <view class="empty-mini" v-if="!(data.opportunities || []).length">暂无当地兼职/志愿信息</view>

      <view class="section-title">讨论</view>
      <view class="card card-base chat-card">
        <view class="chat-list" v-if="chatList.length">
          <view class="chat-item" v-for="m in chatList" :key="m.id">
            <view class="chat-user">{{ m.nickname || '旅行者' }}</view>
            <view class="chat-content">{{ m.content }}</view>
            <view class="chat-time">{{ formatTime(m.createTime) }}</view>
            <text class="chat-reply" @tap="replyTo(m)">回复</text>
          </view>
        </view>
        <view class="chat-empty" v-else-if="!chatLoading">暂无留言，来发一条吧</view>
        <view class="chat-reply-hint" v-if="replyToMsg">回复 {{ replyToMsg.nickname || '旅行者' }}<text class="cancel-reply" @tap="replyToMsg = null">取消</text></view>
        <view class="chat-input-wrap">
          <input class="chat-input" v-model="chatInput" :placeholder="replyToMsg ? '回复 ' + (replyToMsg.nickname || '') + '…' : '约伴、问攻略、聊体验…'" @confirm="sendChat" />
          <button class="chat-btn" @tap="sendChat">发送</button>
        </view>
      </view>
    </template>
    <view class="empty-state" v-else>
      <view class="empty-icon">🗺️</view>
      <text>{{ emptyText }}</text>
      <text class="empty-hint" v-if="emptyHint">{{ emptyHint }}</text>
      <view class="empty-actions">
        <button class="empty-btn" @tap="goIndex">去首页看看</button>
        <button class="empty-btn secondary" @tap="goSearch">搜索目的地</button>
      </view>
    </view>
  </view>
</template>

<script>
import api from '@/utils/api.js'
export default {
  data() {
    return {
      id: null,
      data: null,
      routes: [],
      taxis: [],
      tickets: [],
      expMap: {},
      loading: true,
      lighting: false,
      lightResult: {},
      chatList: [],
      chatInput: '',
      chatLoading: false,
      replyToMsg: null,
      loadError: null
    }
  },
  computed: {
    emptyText() {
      if (!this.id && this.id !== 0) return '请选择目的地'
      return this.loadError || '目的地不存在'
    },
    emptyHint() {
      if (!this.id && this.id !== 0) return '从首页或搜索进入目的地后可查看详情、点亮拿星星'
      return ''
    },
    mapMarkers() {
      const list = []
      const att = (this.data && this.data.attractions) || []
      const foods = (this.data && this.data.foods) || []
      const acc = (this.data && this.data.accommodations) || []
      let id = 0
      att.forEach(a => {
        if (a.latitude != null && a.longitude != null) {
          list.push({ id: ++id, latitude: +a.latitude, longitude: +a.longitude, title: a.name || '景点', type: 'attraction', itemId: a.id })
        }
      })
      foods.forEach(f => {
        if (f.latitude != null && f.longitude != null) {
          list.push({ id: ++id, latitude: +f.latitude, longitude: +f.longitude, title: f.name || '美食', type: 'food', itemId: f.id })
        }
      })
      acc.forEach(a => {
        if (a.latitude != null && a.longitude != null) {
          list.push({ id: ++id, latitude: +a.latitude, longitude: +a.longitude, title: a.name || '住宿', type: 'accommodation', itemId: a.id })
        }
      })
      return list
    },
    mapCenter() {
      const m = this.mapMarkers
      if (m.length === 0) return { lat: 39.9, lng: 116.4 }
      const lat = m.reduce((s, x) => s + x.latitude, 0) / m.length
      const lng = m.reduce((s, x) => s + x.longitude, 0) / m.length
      return { lat, lng }
    },
    mapScale() {
      const m = this.mapMarkers
      if (m.length <= 1) return 14
      const lats = m.map(x => x.latitude)
      const lngs = m.map(x => x.longitude)
      const span = Math.max(Math.max(...lats) - Math.min(...lats), Math.max(...lngs) - Math.min(...lngs))
      if (span > 0.1) return 12
      if (span > 0.05) return 13
      return 14
    }
  },
  onLoad(op) {
    const raw = op.id
    this.id = raw != null && raw !== '' ? Number(raw) : null
    if (isNaN(this.id)) this.id = null
    this.loadError = null
    if (this.id != null) {
      this.load()
      this.loadTaxiTickets()
    } else {
      this.loading = false
    }
  },
  onShow() {
    if (this.data && (this.data.attractions || this.data.foods)) this.loadExperiences()
  },
  methods: {
    load() {
      if (!this.id) return
      this.loading = true
      Promise.all([
        api.destinations.get(this.id),
        api.routes.byDestination(this.id)
      ]).then(([detail, routes]) => {
        this.data = detail
        this.routes = routes || []
        if (detail && detail.userGone) this.lightResult = { already: true }
        this.loadExperiences()
        this.loadChat()
      }).catch((e) => {
        this.data = null
        this.loadError = (e && e.message) ? e.message : '加载失败'
      }).finally(() => {
        this.loading = false
      })
    },
    loadChat() {
      if (!this.id) return
      this.chatLoading = true
      api.chat.list('destination', this.id).then(res => {
        this.chatList = res || []
      }).catch(() => { this.chatList = [] }).finally(() => { this.chatLoading = false })
    },
    replyTo(m) { this.replyToMsg = { id: m.id, nickname: m.nickname } },
    sendChat() {
      const content = (this.chatInput || '').trim()
      if (!content || !this.id) return
      const body = { topicType: 'destination', topicId: this.id, content }
      if (this.replyToMsg) body.parentId = this.replyToMsg.id
      api.chat.post(body).then(() => {
        this.chatInput = ''
        this.replyToMsg = null
        this.loadChat()
      }).catch(() => uni.showToast({ title: '发送失败', icon: 'none' }))
    },
    bookHotel() {
      const keyword = encodeURIComponent((this.data && this.data.name) || '')
      const url = 'https://hotels.ctrip.com/hotels/list?keyword=' + keyword
      if (url.indexOf('http') === 0) {
        // #ifdef H5
        window.open(url)
        // #endif
        // #ifdef MP-WEIXIN
        uni.setClipboardData({ data: url })
        uni.showToast({ title: '链接已复制，可到浏览器打开订酒店', icon: 'none' })
        // #endif
      }
    },
    loadExperiences() {
      const att = this.data && this.data.attractions ? this.data.attractions : []
      const foods = this.data && this.data.foods ? this.data.foods : []
      att.forEach(a => {
        api.experiences.list('attraction', a.id).then(res => {
          this.$set(this.expMap, 'attraction_' + a.id, res || [])
        }).catch(() => { this.$set(this.expMap, 'attraction_' + a.id, []) })
      })
      foods.forEach(f => {
        api.experiences.list('food', f.id).then(res => {
          this.$set(this.expMap, 'food_' + f.id, res || [])
        }).catch(() => { this.$set(this.expMap, 'food_' + f.id, []) })
      })
    },
    goWriteExp(targetType, targetId, targetName) {
      uni.navigateTo({ url: '/pages/experience/submit?targetType=' + targetType + '&targetId=' + targetId + '&targetName=' + encodeURIComponent(targetName || '') })
    },
    formatTime(t) {
      if (!t) return ''
      const d = new Date(t)
      return d.getFullYear() + '-' + String(d.getMonth() + 1).padStart(2, '0') + '-' + String(d.getDate()).padStart(2, '0')
    },
    loadTaxiTickets() {
      if (!this.id) return
      api.taxi.list(this.id).then(res => { this.taxis = res || [] }).catch(() => { this.taxis = [] })
      api.tickets.list(this.id).then(res => { this.tickets = res || [] }).catch(() => { this.tickets = [] })
    },
    openLink(url) {
      if (!url) return
      if (url.indexOf('http') === 0) {
        // #ifdef H5
        window.open(url)
        // #endif
        // #ifdef MP-WEIXIN
        uni.setClipboardData({ data: url })
        uni.showToast({ title: '链接已复制', icon: 'none' })
        // #endif
      }
    },
    toggleWant() {
      api.interact.wantGoToggle(Number(this.id)).then(res => {
        this.data.userWanted = res.wanted
        this.data.wantCount = res.wantCount != null ? res.wantCount : this.data.wantCount
      }).catch(() => {})
    },
    light() {
      this.lighting = true
      this.lightResult = {}
      api.checkin.light(Number(this.id)).then(res => {
        this.lightResult = res
        if (res && res.starsEarned != null) {
          this.data.userGone = true
          this.data.goneCount = (this.data.goneCount || 0) + 1
        }
      }).catch(e => {
        this.lightResult = { error: e.message || '失败' }
      }).finally(() => {
        this.lighting = false
      })
    },
    goRoute(id) {
      uni.navigateTo({ url: '/pages/route/detail?id=' + id })
    },
    goOpportunity(id) {
      if (id) uni.navigateTo({ url: '/pages/opportunity/detail?id=' + id })
      else uni.navigateTo({ url: '/pages/opportunity/list?destinationId=' + this.id })
    },
    goBudget() {
      uni.navigateTo({ url: '/pages/budget/index?destinationId=' + this.id })
    },
    goWriteGuide() {
      const name = (this.data && this.data.name) ? encodeURIComponent(this.data.name) : ''
      uni.navigateTo({ url: '/pages/guide/publish?destinationId=' + this.id + (name ? '&destinationName=' + name : '') })
    },
    onMarkerTap(e) {
      const mid = e.detail && e.detail.markerId
      const m = (this.mapMarkers || []).find(x => x.id === mid)
      if (!m || !m.title) return
      uni.showToast({ title: m.title, icon: 'none' })
    },
    goIndex() {
      uni.switchTab({ url: '/pages/index/index' })
    },
    goSearch() {
      uni.navigateTo({ url: '/pages/search/index' })
    }
  }
}
</script>

<style scoped>
.page { padding: 20rpx 24rpx 80rpx; background: var(--xlv-paper); min-height: 100vh; }
.loading-wrap { text-align: center; padding: 60rpx; color: var(--xlv-muted); }
.card { margin-bottom: 22rpx; padding: 28rpx; }

/* 详情头部 */
.head { position: relative; overflow: hidden; background: linear-gradient(152deg, #2F5A44 0%, #1F3D2F 64%, #172F24 100%); border: none; box-shadow: 0 18rpx 44rpx rgba(22,33,28,.18); padding: 34rpx 30rpx; }
.head::after { content: ''; position: absolute; right: -130rpx; top: -150rpx; width: 380rpx; height: 380rpx; border-radius: 50%; background: radial-gradient(circle, rgba(201,162,39,.38) 0%, rgba(201,162,39,0) 68%); }
.head-kicker { font-size: 20rpx; letter-spacing: 3rpx; color: rgba(255,255,255,.48); margin-bottom: 10rpx; }
.head .name { font-size: 42rpx; font-weight: 700; color: #fff; letter-spacing: 1rpx; margin-bottom: 12rpx; line-height: 1.3; }
.head .desc { font-size: 26rpx; color: rgba(255,255,255,.7); line-height: 1.6; margin-bottom: 22rpx; }
.want-gone { display: flex; align-items: center; flex-wrap: wrap; gap: 16rpx; margin-bottom: 18rpx; }
.want-gone .stat { font-size: 23rpx; color: rgba(255,255,255,.6); }
.btn { border: none; font-size: 26rpx; padding: 14rpx 30rpx; border-radius: 999rpx; }
.btn::after { border: none; }
.btn-want { background: rgba(255,255,255,.14); color: #fff; border: 1rpx solid rgba(255,255,255,.4); }
.btn-want.on { background: rgba(201,162,39,.9); border-color: rgba(201,162,39,.9); color: #3A2E07; font-weight: 600; }
.act { display: flex; align-items: center; flex-wrap: wrap; gap: 16rpx; }
.stars-badge { background: linear-gradient(135deg, #F7ECD0, #E9D5A4); color: #7A6320; padding: 10rpx 22rpx; border-radius: 999rpx; font-size: 23rpx; font-weight: 700; }
.act .btn { background: linear-gradient(135deg, #E4C466, #C9A227); color: #3A2E07; font-weight: 700; }
.msg { font-size: 24rpx; margin-top: 16rpx; padding: 14rpx 20rpx; border-radius: 14rpx; background: rgba(255,255,255,.08); }
.msg.success { color: #F0DFAE; }
.msg.err { color: #F3B9B1; }
.inline-link { color: #EBD494; font-size: 24rpx; margin-left: 4rpx; }
.head .inline-link { display: inline-block; margin-top: 18rpx; }

/* 内容 */
.budget-hint { padding: 0 0 16rpx; font-size: 25rpx; color: var(--xlv-forest-2); }
.rname { font-weight: 600; font-size: 29rpx; color: var(--xlv-ink); line-height: 1.45; }
.rmeta { font-size: 23rpx; color: var(--xlv-muted); margin-top: 8rpx; }
.rdesc { font-size: 24rpx; color: var(--xlv-ink-2); margin-top: 8rpx; line-height: 1.55; }
.link-wrap { margin-top: 14rpx; }
.link { color: var(--xlv-forest-2); font-size: 25rpx; font-weight: 600; }
.exp-wrap { margin-top: 18rpx; padding-top: 16rpx; border-top: 1rpx solid rgba(31,61,47,.07); }
.exp-link { color: var(--xlv-gold); font-size: 25rpx; font-weight: 600; }
.exp-list { margin-top: 12rpx; }
.exp-item { background: #FAF8F1; padding: 16rpx 18rpx; border-radius: 14rpx; margin-bottom: 10rpx; }
.exp-content { font-size: 25rpx; color: var(--xlv-ink-2); display: block; line-height: 1.55; }
.exp-time { font-size: 21rpx; color: var(--xlv-muted); margin-top: 8rpx; display: block; }
.empty-mini { font-size: 25rpx; color: var(--xlv-muted); padding: 16rpx 8rpx; }
.opp-tag { display: inline-block; padding: 6rpx 16rpx; border-radius: 999rpx; font-size: 21rpx; margin-bottom: 10rpx; }
.opp-tag.part_time { background: rgba(45,90,61,.09); color: var(--xlv-forest-2); }
.opp-tag.volunteer { background: var(--xlv-gold-soft); color: #7A6320; }
.map-wrap { padding: 0; overflow: hidden; border-radius: var(--xlv-radius-lg); }
.poi-map { width: 100%; height: 360rpx; display: block; }
.map-tip { padding: 24rpx; font-size: 23rpx; color: var(--xlv-muted); text-align: center; }

/* 讨论 */
.chat-list { max-height: 420rpx; overflow-y: auto; margin-bottom: 24rpx; }
.chat-item { padding: 18rpx 0; border-bottom: 1rpx solid rgba(31,61,47,.06); }
.chat-user { font-size: 23rpx; color: var(--xlv-forest-2); font-weight: 600; margin-bottom: 8rpx; }
.chat-content { font-size: 27rpx; color: var(--xlv-ink); line-height: 1.55; }
.chat-time { font-size: 21rpx; color: var(--xlv-muted); margin-top: 8rpx; }
.chat-reply { font-size: 23rpx; color: var(--xlv-gold); margin-top: 10rpx; display: inline-block; }
.chat-reply-hint { font-size: 23rpx; color: var(--xlv-muted); padding: 12rpx 0; }
.cancel-reply { color: var(--xlv-gold); margin-left: 16rpx; }
.chat-empty { font-size: 25rpx; color: var(--xlv-muted); padding: 24rpx 0; text-align: center; }
.chat-input-wrap { display: flex; align-items: center; gap: 16rpx; margin-top: 16rpx; }
.chat-input { flex: 1; padding: 20rpx 22rpx; border: 1rpx solid var(--xlv-line); background: #FAF8F1; border-radius: 16rpx; font-size: 26rpx; }
.chat-btn { padding: 20rpx 30rpx; background: linear-gradient(135deg, #3A6A4B, #23503A); color: #fff; font-size: 26rpx; border-radius: 16rpx; border: none; font-weight: 600; }
.chat-btn::after { border: none; }

.empty-actions { margin-top: 32rpx; display: flex; flex-wrap: wrap; justify-content: center; gap: 24rpx; }
.empty-btn { padding: 18rpx 36rpx; font-size: 26rpx; border-radius: 999rpx; border: none; background: linear-gradient(135deg, #3A6A4B, #23503A); color: #fff; }
.empty-btn::after { border: none; }
.empty-btn.secondary { background: transparent; color: var(--xlv-forest-2); border: 1rpx solid rgba(31,61,47,.25); }
</style>
