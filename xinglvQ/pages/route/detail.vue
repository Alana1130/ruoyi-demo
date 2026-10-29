<template>
  <view class="page">
    <view class="loading" v-if="loading">加载中...</view>
    <template v-else-if="data">
      <view class="card head">
        <view class="name">{{ data.name }}</view>
        <view class="desc">{{ data.description }}</view>
        <view class="meta">{{ data.days }} 天 · 预算约 ¥{{ data.totalBudgetMin }}–{{ data.totalBudgetMax }}</view>
        <view class="link" @tap="goDest(data.destinationId)">查看目的地</view>
      </view>

      <view class="sec-title">线路地图</view>
      <view class="map-card card">
        <view class="day-tabs">
          <view
            v-for="d in dayList"
            :key="d"
            class="day-tab"
            :class="{ active: currentDay === d }"
            @tap="currentDay = d"
          >第 {{ d }} 天</view>
        </view>
        <map
          class="route-map"
          :latitude="routeMapCenter.lat"
          :longitude="routeMapCenter.lng"
          :scale="routeMapScale"
          :markers="routeMapMarkers"
          :polyline="routeMapPolyline"
          show-location
          @markertap="onRouteMarkerTap"
        />
        <view class="map-tip" v-if="routeMapMarkers.length === 0">当日行程暂无坐标，为景点/美食/住宿配置经纬度后可在此查看</view>
      </view>

      <view class="sec-title" v-for="(items, dayNo) in daysDetail" :key="dayNo">第 {{ dayNo }} 天</view>
      <view class="card" v-for="(items, dayNo) in daysDetail" :key="dayNo">
        <view class="day-item" v-for="(node, idx) in items" :key="idx">
          <view class="node-name">{{ node.detailName || ('ID' + node.itemId) }}</view>
          <view class="node-extra" v-if="node.detailExtra">{{ node.detailExtra }}</view>
          <view class="transport" v-if="node.transportApprox">交通：{{ node.transportApprox }}</view>
        </view>
      </view>

      <view class="sec-title">讨论</view>
      <view class="card chat-card">
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
    <view class="empty" v-else>线路不存在</view>
  </view>
</template>

<script>
import api from '@/utils/api.js'
export default {
  data() {
    return { id: null, data: null, daysDetail: {}, loading: true, currentDay: 1, chatList: [], chatInput: '', chatLoading: false, replyToMsg: null }
  },
  computed: {
    dayList() {
      const days = (this.data && this.data.days) || 0
      const list = []
      for (let i = 1; i <= days; i++) list.push(i)
      return list
    },
    currentDayNodes() {
      const d = this.daysDetail[this.currentDay]
      return Array.isArray(d) ? d : []
    },
    routeMapMarkers() {
      const list = []
      this.currentDayNodes.forEach((node, idx) => {
        if (node.latitude != null && node.longitude != null) {
          list.push({
            id: idx + 1,
            latitude: +node.latitude,
            longitude: +node.longitude,
            title: node.detailName || '点位' + (idx + 1),
            itemType: node.itemType,
            itemId: node.itemId
          })
        }
      })
      return list
    },
    routeMapPolyline() {
      const points = this.currentDayNodes
        .filter(n => n.latitude != null && n.longitude != null)
        .map(n => ({ latitude: +n.latitude, longitude: +n.longitude }))
      if (points.length < 2) return []
      return [{ points, color: '#2d5a3d', width: 4 }]
    },
    routeMapCenter() {
      const m = this.routeMapMarkers
      if (m.length === 0) return { lat: 39.9, lng: 116.4 }
      const lat = m.reduce((s, x) => s + x.latitude, 0) / m.length
      const lng = m.reduce((s, x) => s + x.longitude, 0) / m.length
      return { lat, lng }
    },
    routeMapScale() {
      const m = this.routeMapMarkers
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
    this.id = op.id
    this.load()
  },
  methods: {
    load() {
      if (!this.id) return
      api.routes.get(this.id).then(res => {
        this.data = res
        this.daysDetail = res.daysDetail || {}
        this.loadChat()
      }).catch(() => {
        this.data = null
      }).finally(() => {
        this.loading = false
      })
    },
    loadChat() {
      if (!this.id) return
      this.chatLoading = true
      api.chat.list('route', this.id).then(res => { this.chatList = res || [] }).catch(() => { this.chatList = [] }).finally(() => { this.chatLoading = false })
    },
    replyTo(m) { this.replyToMsg = { id: m.id, nickname: m.nickname } },
    sendChat() {
      const content = (this.chatInput || '').trim()
      if (!content || !this.id) return
      const body = { topicType: 'route', topicId: this.id, content }
      if (this.replyToMsg) body.parentId = this.replyToMsg.id
      api.chat.post(body).then(() => {
        this.chatInput = ''
        this.replyToMsg = null
        this.loadChat()
      }).catch(() => uni.showToast({ title: '发送失败', icon: 'none' }))
    },
    formatTime(t) {
      if (!t) return ''
      const d = new Date(t)
      return d.getFullYear() + '-' + String(d.getMonth() + 1).padStart(2, '0') + '-' + String(d.getDate()).padStart(2, '0') + ' ' + String(d.getHours()).padStart(2, '0') + ':' + String(d.getMinutes()).padStart(2, '0')
    },
    goDest(id) {
      uni.navigateTo({ url: '/pages/destination/detail?id=' + id })
    },
    onRouteMarkerTap(e) {
      const mid = e.detail && e.detail.markerId
      const m = (this.routeMapMarkers || []).find(x => x.id === mid)
      if (m && m.title) uni.showToast({ title: m.title, icon: 'none' })
    }
  }
}
</script>

<style scoped>
.page { padding: 24rpx; padding-bottom: 60rpx; background: #f5f5f0; min-height: 100vh; }
.loading, .empty { text-align: center; padding: 60rpx; color: #999; }
.card { background: #fff; border-radius: 16rpx; padding: 24rpx; margin-bottom: 20rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,.06); }
.head .name { font-size: 36rpx; font-weight: 700; margin-bottom: 12rpx; }
.head .desc { font-size: 28rpx; color: #666; margin-bottom: 8rpx; }
.head .meta { font-size: 26rpx; color: #666; margin-bottom: 12rpx; }
.link { color: #2d5a3d; font-size: 28rpx; }
.sec-title { font-size: 30rpx; font-weight: 600; margin: 24rpx 0 12rpx; }
.day-item { padding-bottom: 16rpx; border-bottom: 1rpx solid #eee; margin-bottom: 16rpx; }
.day-item:last-child { border-bottom: none; margin-bottom: 0; }
.node-name { font-weight: 600; font-size: 28rpx; }
.node-extra { font-size: 24rpx; color: #666; margin-top: 4rpx; }
.transport { font-size: 24rpx; color: #999; margin-top: 6rpx; }
.day-tabs { display: flex; flex-wrap: wrap; gap: 16rpx; margin-bottom: 20rpx; }
.day-tab { padding: 12rpx 24rpx; border-radius: 24rpx; font-size: 26rpx; color: #666; background: #f0f0eb; }
.day-tab.active { background: #2d5a3d; color: #fff; }
.map-card { padding: 24rpx; }
.route-map { width: 100%; height: 380rpx; border-radius: 12rpx; }
.map-tip { padding: 20rpx 0 0; font-size: 24rpx; color: #999; text-align: center; }
.chat-card { padding: 24rpx; }
.chat-list { max-height: 400rpx; overflow-y: auto; margin-bottom: 24rpx; }
.chat-item { padding: 16rpx 0; border-bottom: 1rpx solid #eee; }
.chat-user { font-size: 24rpx; color: #2d5a3d; margin-bottom: 6rpx; }
.chat-content { font-size: 28rpx; color: #333; }
.chat-time { font-size: 22rpx; color: #999; margin-top: 6rpx; }
.chat-reply { font-size: 24rpx; color: #2d5a3d; margin-top: 8rpx; display: inline-block; }
.chat-reply-hint { font-size: 24rpx; color: #666; padding: 12rpx 0; }
.cancel-reply { color: #2d5a3d; margin-left: 16rpx; }
.chat-empty { font-size: 26rpx; color: #999; padding: 24rpx 0; text-align: center; }
.chat-input-wrap { display: flex; align-items: center; gap: 16rpx; margin-top: 16rpx; }
.chat-input { flex: 1; padding: 20rpx; border: 1rpx solid #eee; border-radius: 12rpx; font-size: 28rpx; }
.chat-btn { padding: 20rpx 28rpx; background: #2d5a3d; color: #fff; font-size: 28rpx; border-radius: 12rpx; border: none; }
</style>
