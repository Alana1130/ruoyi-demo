<template>
  <view class="page">
    <view class="loading-wrap" v-if="loading">加载中...</view>
    <template v-else-if="data">
      <view class="card head">
        <view class="title">{{ data.title }}</view>
        <view class="meta" v-if="data.destinationName">目的地：{{ data.destinationName }}</view>
        <view class="meta" v-if="data.travelDateStart || data.budgetRange">
          <text v-if="data.travelDateStart">出发 {{ formatDate(data.travelDateStart) }}</text>
          <text v-if="data.travelDateEnd"> ~ {{ formatDate(data.travelDateEnd) }}</text>
          <text v-if="data.budgetRange"> · 预算 {{ data.budgetRange }}</text>
        </view>
        <view class="content" v-if="data.content">{{ data.content }}</view>
        <view class="contact" v-if="data.contactRemark">联系方式：{{ data.contactRemark }}</view>
        <view class="footer">
          <text class="author">{{ data.nickname || '旅行者' }}</text>
          <text class="time">{{ formatTime(data.createTime) }}</text>
        </view>
        <view class="actions" v-if="isMine">
          <text class="link" @tap="goEdit">编辑</text>
          <text class="link danger" @tap="onDelete">删除</text>
        </view>
        <view class="act-row">
          <text class="like-btn" :class="{ on: likeInfo.liked }" @tap="toggleLike">❤ {{ likeInfo.likeCount || 0 }}</text>
        </view>
      </view>
      <view class="comment-section card">
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
      <view class="tip">想一起出发？可通过上方联系方式与发布者沟通，或到目的地讨论区留言约伴</view>
      <view class="go-dest" v-if="data.destinationId" @tap="goDestination(data.destinationId)">
        查看「{{ data.destinationName }}」详情
      </view>
    </template>
    <view class="empty" v-else>帖子不存在或已删除</view>
  </view>
</template>

<script>
import api from '@/utils/api.js'
export default {
  data() {
    return {
      id: null,
      data: null,
      loading: true,
      myUserId: null,
      likeInfo: { likeCount: 0, liked: false },
      comments: [],
      commentVal: ''
    }
  },
  computed: {
    isMine() {
      return this.myUserId != null && this.data && this.data.userId === this.myUserId
    }
  },
  onLoad(op) {
    this.id = op.id ? Number(op.id) : null
    if (!this.id) {
      this.loading = false
      return
    }
    api.user.me().then(res => {
      this.myUserId = res && res.id
    }).catch(() => {})
    this.load()
    this.loadLike()
    this.loadComments()
  },
  methods: {
    load() {
      api.companions.get(this.id).then(res => {
        this.data = res
      }).catch(() => {
        this.data = null
      }).finally(() => {
        this.loading = false
      })
    },
    loadLike() {
      api.interact.likeInfo('companion', this.id).then(res => { this.likeInfo = res || {} }).catch(() => {})
    },
    loadComments() {
      api.interact.commentList('companion', this.id).then(res => { this.comments = res || [] }).catch(() => { this.comments = [] })
    },
    toggleLike() {
      api.interact.likeToggle('companion', this.id).then(res => { this.likeInfo = { likeCount: res.likeCount, liked: res.liked } }).catch(() => {})
    },
    submitComment() {
      const c = (this.commentVal || '').trim()
      if (!c) return
      api.interact.commentAdd('companion', this.id, c).then(() => { this.commentVal = ''; this.loadComments() }).catch(e => uni.showToast({ title: e.message || '失败', icon: 'none' }))
    },
    formatDate(d) {
      if (!d) return ''
      const s = typeof d === 'string' ? d : (d.toISOString ? d.toISOString().slice(0, 10) : '')
      return s.slice(0, 10)
    },
    formatTime(t) {
      if (!t) return ''
      const d = typeof t === 'string' ? new Date(t) : t
      return (d.getMonth() + 1) + '-' + d.getDate() + ' ' + (d.getHours() < 10 ? '0' : '') + d.getHours() + ':' + (d.getMinutes() < 10 ? '0' : '') + d.getMinutes()
    },
    goEdit() {
      uni.navigateTo({ url: '/pages/companion/publish?id=' + this.id })
    },
    onDelete() {
      uni.showModal({
        title: '确认删除',
        content: '删除后不可恢复',
        success: (res) => {
          if (res.confirm) {
            api.companions.delete(this.id).then(() => {
              uni.showToast({ title: '已删除' })
              setTimeout(() => uni.navigateBack(), 800)
            }).catch(e => uni.showToast({ title: e.message || '删除失败', icon: 'none' }))
          }
        }
      })
    },
    goDestination(id) {
      uni.navigateTo({ url: '/pages/destination/detail?id=' + id })
    }
  }
}
</script>

<style scoped>
.page { padding: 24rpx; background: #f2f4f3; min-height: 100vh; }
.card { background: #fff; border-radius: 20rpx; padding: 28rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,.06); }
.card.head .title { font-size: 36rpx; font-weight: 700; color: #1a1a1a; margin-bottom: 16rpx; }
.card .meta { font-size: 26rpx; color: #666; margin-bottom: 12rpx; }
.card .content { font-size: 30rpx; color: #333; line-height: 1.6; margin-bottom: 16rpx; white-space: pre-wrap; }
.card .contact { font-size: 26rpx; color: #2d5a3d; margin-bottom: 16rpx; }
.card .footer { font-size: 24rpx; color: #999; }
.card .author { color: #2d5a3d; margin-right: 16rpx; }
.card .actions { margin-top: 24rpx; padding-top: 24rpx; border-top: 1rpx solid #eee; }
.card .link { margin-right: 24rpx; color: #2d5a3d; font-size: 28rpx; }
.card .link.danger { color: #c00; }
.act-row { margin-top: 16rpx; padding-top: 16rpx; border-top: 1rpx solid #eee; }
.like-btn { font-size: 28rpx; color: #999; }
.like-btn.on { color: #e91e63; }
.comment-section { margin-top: 24rpx; padding: 28rpx; }
.comment-title { font-size: 28rpx; font-weight: 600; margin-bottom: 16rpx; }
.comment-item { padding: 16rpx 0; border-bottom: 1rpx solid #f0f0f0; }
.c-nick { font-size: 24rpx; color: #2d5a3d; margin-right: 12rpx; }
.c-content { font-size: 28rpx; color: #333; }
.c-time { font-size: 22rpx; color: #999; display: block; margin-top: 8rpx; }
.comment-input { display: flex; align-items: center; margin-top: 20rpx; gap: 16rpx; }
.comment-input .inp { flex: 1; border: 1rpx solid #eee; border-radius: 12rpx; padding: 20rpx; font-size: 28rpx; }
.comment-input .send { color: #2d5a3d; font-size: 28rpx; }
.tip { margin-top: 24rpx; padding: 24rpx; background: #f8f9f8; border-radius: 12rpx; font-size: 26rpx; color: #666; }
.go-dest { margin-top: 24rpx; padding: 28rpx; background: #2d5a3d; color: #fff; text-align: center; border-radius: 16rpx; font-size: 30rpx; }
.loading-wrap, .empty { text-align: center; padding: 80rpx; color: #999; }
</style>
