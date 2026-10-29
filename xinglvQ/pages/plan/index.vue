<template>
  <view class="page">
    <view class="list" v-if="list.length">
      <view class="card" v-for="p in list" :key="p.id">
        <view class="title">{{ p.name }}</view>
        <view class="date">{{ formatDate(p.startDate) }} ～ {{ formatDate(p.endDate) }}</view>
        <view class="dests" v-if="p.destinationNames && p.destinationNames.length">想去：{{ p.destinationNames.join('、') }}</view>
        <view class="memo" v-if="p.memo">{{ p.memo }}</view>
        <view class="actions">
          <text class="btn-s" @tap="edit(p)">编辑</text>
          <text class="btn-s del" @tap="del(p)">删除</text>
        </view>
      </view>
    </view>
    <view class="empty" v-else-if="!loading">暂无学期/假期计划，添加一个吧</view>
    <view class="loading" v-else>加载中...</view>
    <view class="fab" @tap="add">+ 添加计划</view>
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
      api.plans.list().then(res => { this.list = res || [] }).catch(() => { this.list = [] }).finally(() => { this.loading = false })
    },
    formatDate(d) {
      if (!d) return ''
      if (typeof d === 'string') return d.substring(0, 10)
      if (typeof d === 'number') return new Date(d).toISOString().slice(0, 10)
      if (d.year != null) return d.year + '-' + String(d.month || d.monthValue).padStart(2, '0') + '-' + String(d.dayOfMonth || d.date).padStart(2, '0')
      return ''
    },
    add() { uni.navigateTo({ url: '/pages/plan/edit' }) },
    edit(p) { uni.navigateTo({ url: '/pages/plan/edit?id=' + p.id }) },
    del(p) {
      uni.showModal({ title: '确认删除', content: '删除「' + p.name + '」？', success: (r) => {
        if (!r.confirm) return
        api.plans.delete(p.id).then(() => { uni.showToast({ title: '已删除' }); this.load() }).catch(e => uni.showToast({ title: e.message || '失败', icon: 'none' }))
      }})
    }
  }
}
</script>

<style scoped>
.page { padding: 24rpx; padding-bottom: 120rpx; background: #f2f4f3; min-height: 100vh; }
.card { background: #fff; border-radius: 20rpx; padding: 28rpx; margin-bottom: 20rpx; box-shadow: 0 4rpx 20rpx rgba(0,0,0,.06); }
.title { font-size: 32rpx; font-weight: 600; color: #1a1a1a; margin-bottom: 12rpx; }
.date { font-size: 26rpx; color: #2d5a3d; margin-bottom: 8rpx; }
.dests, .memo { font-size: 26rpx; color: #666; margin-top: 8rpx; }
.actions { margin-top: 20rpx; padding-top: 16rpx; border-top: 1rpx solid #eee; display: flex; gap: 24rpx; }
.btn-s { color: #2d5a3d; font-size: 26rpx; }
.btn-s.del { color: #e65100; }
.fab { position: fixed; right: 24rpx; bottom: 80rpx; padding: 24rpx 32rpx; background: #2d5a3d; color: #fff; border-radius: 48rpx; font-size: 28rpx; box-shadow: 0 8rpx 24rpx rgba(45,90,61,.3); }
.empty, .loading { text-align: center; padding: 80rpx; color: #999; }
</style>
