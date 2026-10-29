<template>
  <view class="page" v-if="item">
    <view class="img-wrap" v-if="firstImg"><image :src="firstImg" mode="aspectFill" class="thumb" /></view>
    <view class="img-wrap placeholder" v-else><text class="icon">📦</text></view>
    <view class="section">
      <view class="title">{{ item.title }}</view>
      <view class="stars">⭐ {{ item.priceStars }} 星星</view>
      <view class="meta">分类 {{ categoryText }} · 卖家 {{ item.sellerNickname }}</view>
      <view class="desc" v-if="item.description">{{ item.description }}</view>
      <view class="act-row">
        <text class="like-btn" :class="{ on: likeInfo.liked }" @tap="toggleLike">❤ {{ likeInfo.likeCount || 0 }}</text>
      </view>
    </view>
    <view class="comment-section">
      <view class="comment-title">评论 ({{ comments.length }})</view>
      <view class="comment-item" v-for="c in comments" :key="c.id">
        <text class="c-nick">{{ c.nickname }}</text>
        <text class="c-content">{{ c.content }}</text>
        <text class="c-time">{{ formatTime(c.createTime) }}</text>
      </view>
      <view class="comment-input">
        <input class="inp" v-model="commentVal" placeholder="说两句..." @confirm="submitComment" />
        <text class="send" @tap="submitComment">发送</text>
      </view>
    </view>
    <view class="bottom-actions" v-if="item.status === '0' && !isMine">
      <button class="btn primary" @tap="buy">用星星购买</button>
    </view>
    <view class="bottom-actions" v-else-if="isMine && item.status === '0'">
      <button class="btn outline" @tap="edit">编辑</button>
      <button class="btn del" @tap="del">下架</button>
    </view>
  </view>
  <view class="page" v-else-if="!loading">内容不存在</view>
  <view class="page" v-else>加载中...</view>
</template>

<script>
import api from '@/utils/api.js'
export default {
  data() { return { item: null, loading: false, myId: null, likeInfo: { likeCount: 0, liked: false }, comments: [], commentVal: '' } },
  computed: {
    firstImg() {
      const s = (this.item && this.item.images) || ''
      const u = s.split(',')[0].trim()
      return u || ''
    },
    categoryText() {
      const m = { textbook: '教材', travel_gear: '出行装备', other: '其他' }
      return m[this.item && this.item.category] || '其他'
    },
    isMine() {
      return this.myId != null && this.item && this.item.sellerId == this.myId
    }
  },
  onLoad(op) {
    api.user.me().then(res => { this.myId = res && res.id }).catch(() => {})
    if (op.id) this.load(op.id)
  },
  methods: {
    formatTime(t) {
      if (!t) return ''
      const d = typeof t === 'string' ? new Date(t) : t
      return (d.getMonth() + 1) + '-' + d.getDate() + ' ' + (d.getHours() < 10 ? '0' : '') + d.getHours() + ':' + (d.getMinutes() < 10 ? '0' : '') + d.getMinutes()
    },
    load(id) {
      this.loading = true
      api.idle.get(id).then(res => { this.item = res }).catch(() => { this.item = null }).finally(() => { this.loading = false })
      api.interact.likeInfo('idle', id).then(res => { this.likeInfo = res || {} }).catch(() => {})
      api.interact.commentList('idle', id).then(res => { this.comments = res || [] }).catch(() => { this.comments = [] })
    },
    toggleLike() {
      api.interact.likeToggle('idle', this.item.id).then(res => { this.likeInfo = { likeCount: res.likeCount, liked: res.liked } }).catch(() => {})
    },
    submitComment() {
      const c = (this.commentVal || '').trim()
      if (!c) return
      api.interact.commentAdd('idle', this.item.id, c).then(() => { this.commentVal = ''; this.loadComments() }).catch(e => uni.showToast({ title: e.message || '失败', icon: 'none' }))
    },
    loadComments() {
      api.interact.commentList('idle', this.item.id).then(res => { this.comments = res || [] }).catch(() => {})
    },
    buy() {
      uni.showModal({ title: '确认购买', content: '花费 ' + this.item.priceStars + ' 星星购买「' + this.item.title + '」？', success: (r) => {
        if (!r.confirm) return
        api.idle.buy(this.item.id).then(() => { uni.showToast({ title: '购买成功' }); this.load(this.item.id) }).catch(e => uni.showToast({ title: e.message || '失败', icon: 'none' }))
      }})
    },
    edit() { uni.navigateTo({ url: '/pages/idle/publish?id=' + this.item.id }) },
    del() {
      uni.showModal({ title: '确认下架', content: '下架后将不再在市场中展示', success: (r) => {
        if (!r.confirm) return
        api.idle.delete(this.item.id).then(() => { uni.showToast({ title: '已下架' }); uni.navigateBack() }).catch(e => uni.showToast({ title: e.message || '失败', icon: 'none' }))
      }})
    }
  }
}
</script>

<style scoped>
.page { padding: 24rpx; padding-bottom: 140rpx; background: #f2f4f3; min-height: 100vh; }
.img-wrap { height: 360rpx; background: #f0f0f0; border-radius: 16rpx; overflow: hidden; margin-bottom: 24rpx; display: flex; align-items: center; justify-content: center; }
.img-wrap.placeholder .icon { font-size: 80rpx; }
.thumb { width: 100%; height: 100%; }
.section { background: #fff; border-radius: 20rpx; padding: 28rpx; box-shadow: 0 4rpx 20rpx rgba(0,0,0,.06); }
.title { font-size: 34rpx; font-weight: 600; margin-bottom: 16rpx; }
.stars { font-size: 32rpx; color: #2d5a3d; font-weight: 600; margin-bottom: 12rpx; }
.meta { font-size: 26rpx; color: #999; margin-bottom: 20rpx; }
.desc { font-size: 28rpx; color: #666; line-height: 1.5; }
.bottom-actions { position: fixed; left: 0; right: 0; bottom: 0; padding: 24rpx; background: #fff; border-top: 1rpx solid #eee; display: flex; gap: 24rpx; }
.btn { flex: 1; height: 88rpx; line-height: 88rpx; border-radius: 16rpx; font-size: 30rpx; }
.btn.primary { background: #2d5a3d; color: #fff; }
.btn.outline { background: #fff; color: #2d5a3d; border: 1rpx solid #2d5a3d; }
.btn.del { background: #fff; color: #e65100; border: 1rpx solid #e65100; }
.act-row { margin-top: 16rpx; padding-top: 16rpx; border-top: 1rpx solid #eee; }
.like-btn { font-size: 28rpx; color: #999; }
.like-btn.on { color: #e91e63; }
.comment-section { margin-top: 24rpx; padding: 28rpx; background: #fff; border-radius: 20rpx; }
.comment-title { font-size: 28rpx; font-weight: 600; margin-bottom: 16rpx; }
.comment-item { padding: 16rpx 0; border-bottom: 1rpx solid #f0f0f0; }
.c-nick { font-size: 24rpx; color: #2d5a3d; margin-right: 12rpx; }
.c-content { font-size: 28rpx; color: #333; }
.c-time { font-size: 22rpx; color: #999; display: block; margin-top: 8rpx; }
.comment-input { display: flex; align-items: center; margin-top: 20rpx; gap: 16rpx; }
.comment-input .inp { flex: 1; border: 1rpx solid #eee; border-radius: 12rpx; padding: 20rpx; font-size: 28rpx; }
.comment-input .send { color: #2d5a3d; font-size: 28rpx; }
</style>
