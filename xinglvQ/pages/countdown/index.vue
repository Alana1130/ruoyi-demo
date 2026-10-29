<template>
  <view class="page">
    <view class="list" v-if="list.length">
      <view class="card" v-for="c in list" :key="c.id">
        <view class="head">
          <text class="title">{{ c.title }}</text>
          <text class="days" :class="{ overdue: (c.daysLeft || 0) < 0 }">{{ daysText(c) }}</text>
        </view>
        <view class="meta" v-if="c.destinationName">{{ c.destinationName }}</view>
        <view class="meta" v-if="c.tripDate">出行日 {{ formatDate(c.tripDate) }}</view>
        <view class="memo" v-if="c.memo">{{ c.memo }}</view>
        <view class="actions">
          <text class="btn-s" @tap="edit(c)">编辑</text>
          <text class="btn-s del" @tap="del(c)">删除</text>
        </view>
      </view>
    </view>
    <view class="empty" v-else-if="!loading">暂无出行倒计时，添加一个吧</view>
    <view class="loading" v-else>加载中...</view>
    <view class="fab" @tap="add">+ 添加倒计时</view>
  </view>
</template>

<script>
import api from '@/utils/api.js'
export default {
  data() { return { list: [], loading: false } },
  onLoad() { this.load() },
  onPullDownRefresh() { this.load().then(() => uni.stopPullDownRefresh()) },
  methods: {
    load() {
      this.loading = true
      api.countdowns.list().then(res => { this.list = res || [] }).catch(() => { this.list = [] }).finally(() => { this.loading = false })
    },
    daysText(c) {
      const d = c.daysLeft
      if (d == null) return ''
      if (d < 0) return '已过 ' + (-d) + ' 天'
      if (d === 0) return '今天出发'
      return '还有 ' + d + ' 天'
    },
    formatDate(d) {
      if (!d) return ''
      const s = typeof d === 'string' ? d : (d.year + '-' + String(d.month || d.monthValue).padStart(2, '0') + '-' + String(d.dayOfMonth || d.date).padStart(2, '0'))
      return s.substring(0, 10)
    },
    add() { uni.navigateTo({ url: '/pages/countdown/edit' }) },
    edit(c) { uni.navigateTo({ url: '/pages/countdown/edit?id=' + c.id }) },
    del(c) {
      uni.showModal({ title: '确认删除', content: '删除「' + c.title + '」？', success: (r) => {
        if (!r.confirm) return
        api.countdowns.delete(c.id).then(() => { uni.showToast({ title: '已删除' }); this.load() }).catch(e => uni.showToast({ title: e.message || '失败', icon: 'none' }))
      }})
    }
  }
}
</script>

<style scoped>
.page { padding: 24rpx; padding-bottom: 120rpx; background: #f2f4f3; min-height: 100vh; }
.card { background: #fff; border-radius: 20rpx; padding: 28rpx; margin-bottom: 20rpx; box-shadow: 0 4rpx 20rpx rgba(0,0,0,.06); }
.head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12rpx; }
.title { font-size: 32rpx; font-weight: 600; color: #1a1a1a; }
.days { font-size: 28rpx; font-weight: 600; color: #2d5a3d; }
.days.overdue { color: #999; }
.meta, .memo { font-size: 26rpx; color: #666; margin-top: 8rpx; }
.actions { margin-top: 20rpx; padding-top: 16rpx; border-top: 1rpx solid #eee; display: flex; gap: 24rpx; }
.btn-s { color: #2d5a3d; font-size: 26rpx; }
.btn-s.del { color: #e65100; }
.fab { position: fixed; right: 24rpx; bottom: 80rpx; padding: 24rpx 32rpx; background: #2d5a3d; color: #fff; border-radius: 48rpx; font-size: 28rpx; box-shadow: 0 8rpx 24rpx rgba(45,90,61,.3); }
.empty, .loading { text-align: center; padding: 80rpx; color: #999; }
</style>
