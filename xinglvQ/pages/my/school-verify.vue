<template>
  <view class="page page-wrap">
    <view class="card card-base" v-if="mySchool.schoolVerified">
      <view class="done-tip">已认证学校</view>
      <view class="school-name">{{ mySchool.schoolName || '未知' }}</view>
    </view>
    <view class="card card-base" v-else>
      <view class="title">选择你的学校</view>
      <view class="desc">认证后可在该学校圈内发帖，未认证仅可浏览</view>
      <view class="list">
        <view class="item" v-for="s in schools" :key="s.id" @tap="selectSchool(s)">
          <text class="name">{{ s.name }}</text>
          <text class="region" v-if="s.region">{{ s.region }}</text>
        </view>
      </view>
      <view class="empty" v-if="!loading && schools.length === 0">暂无学校数据</view>
    </view>
    <view class="loading-wrap" v-if="loading">加载中...</view>
  </view>
</template>

<script>
import api from '@/utils/api.js'
export default {
  data() {
    return { schools: [], mySchool: {}, loading: true }
  },
  onShow() {
    this.load()
  },
  methods: {
    load() {
      this.loading = true
      Promise.all([api.schools.list(), api.schools.mySchool()])
        .then(([list, my]) => {
          this.schools = list || []
          this.mySchool = my || {}
        })
        .catch(() => {})
        .finally(() => { this.loading = false })
    },
    selectSchool(s) {
      if (this.mySchool.schoolVerified) return
      uni.showModal({
        title: '确认认证',
        content: '确认为「' + s.name + '」学生？认证后可在该校学校圈发帖。',
        success: (res) => {
          if (!res.confirm) return
          uni.showLoading({ title: '提交中...' })
          api.schools.verify(s.id).then(() => {
            uni.hideLoading()
            uni.showToast({ title: '认证成功', icon: 'none' })
            this.load()
          }).catch(e => {
            uni.hideLoading()
            uni.showToast({ title: e.message || '失败', icon: 'none' })
          })
        }
      })
    }
  }
}
</script>

<style scoped>
.page { padding: 24rpx; }
.card { padding: 32rpx; border-radius: 20rpx; box-shadow: 0 4rpx 20rpx rgba(0,0,0,.06); }
.done-tip { font-size: 26rpx; color: #2d5a3d; margin-bottom: 8rpx; }
.school-name { font-size: 34rpx; font-weight: 600; color: #1a1a1a; }
.title { font-size: 32rpx; font-weight: 600; color: #1a1a1a; margin-bottom: 12rpx; }
.desc { font-size: 26rpx; color: #666; margin-bottom: 24rpx; }
.list { max-height: 70vh; overflow-y: auto; }
.item { padding: 24rpx 0; border-bottom: 1rpx solid #f0f0f0; display: flex; justify-content: space-between; align-items: center; }
.item:last-child { border-bottom: none; }
.name { font-size: 30rpx; color: #1a1a1a; }
.region { font-size: 24rpx; color: #999; }
.empty { text-align: center; padding: 48rpx; color: #999; }
.loading-wrap { text-align: center; padding: 48rpx; color: #999; }
</style>
