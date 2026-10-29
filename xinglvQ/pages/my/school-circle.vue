<template>
  <view class="page page-wrap">
    <view class="picker-row card-base">
      <text class="label">选择学校</text>
      <picker :range="schools" range-key="name" @change="onSchoolChange">
        <view class="picker-value">{{ currentSchool ? currentSchool.name : '请选择学校' }}</view>
      </picker>
    </view>
    <template v-if="currentSchool">
      <view class="post-btn-wrap" v-if="canPost">
        <button class="post-btn" @tap="showPublish">发帖</button>
      </view>
      <view class="list" v-if="list.length">
        <view class="post card-base" v-for="p in list" :key="p.id">
          <view class="post-head">
            <view class="avatar" v-if="p.avatar"><image :src="p.avatar" mode="aspectFill" /></view>
            <view class="avatar placeholder" v-else>{{ (p.nickname || '用').slice(0,1) }}</view>
            <view class="meta">
              <text class="nickname">{{ p.nickname || '旅行者' }}</text>
              <text class="time">{{ formatTime(p.createTime) }}</text>
            </view>
          </view>
          <view class="content">{{ p.content }}</view>
          <view class="images" v-if="p.images">
            <image v-for="(img, i) in (p.images || '').split(',')" :key="i" v-show="img" class="thumb" :src="img" mode="aspectFill" />
          </view>
        </view>
      </view>
      <view class="empty" v-else-if="!loading">
        <text>暂无帖子，{{ canPost ? '来发一条吧' : '认证该校后可发帖' }}</text>
      </view>
    </template>
    <view class="empty-hint" v-else-if="!loading">请先选择学校</view>
    <view class="loading-wrap" v-if="loading">加载中...</view>

    <view class="publish-mask" v-if="publishVisible" @tap="publishVisible = false">
      <view class="publish-box" @tap.stop>
        <textarea class="publish-input" v-model="publishContent" placeholder="分享校园生活..." auto-height />
        <view class="publish-actions">
          <button class="btn-cancel" @tap="publishVisible = false">取消</button>
          <button class="btn-submit" @tap="submitPost">发布</button>
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
      schools: [],
      currentSchool: null,
      list: [],
      mySchool: {},
      user: {},
      loading: false,
      publishVisible: false,
      publishContent: ''
    }
  },
  computed: {
    canPost() {
      return this.mySchool.schoolVerified === 1 && this.currentSchool && this.currentSchool.id === this.mySchool.schoolId
    }
  },
  onShow() {
    this.loadSchools()
    this.loadMySchool()
  },
  onPullDownRefresh() {
    this.loadMySchool()
    if (this.currentSchool) this.loadPosts()
    else this.loadSchools()
    setTimeout(() => uni.stopPullDownRefresh(), 300)
  },
  watch: {
    currentSchool(s) {
      if (s) this.loadPosts()
      else this.list = []
    }
  },
  methods: {
    loadSchools() {
      api.schools.list().then(res => { this.schools = res || [] }).catch(() => { this.schools = [] })
    },
    loadMySchool() {
      api.schools.mySchool().then(res => { this.mySchool = res || {} }).catch(() => {})
      api.user.me().then(res => { this.user = res || {} }).catch(() => {})
    },
    onSchoolChange(e) {
      const i = parseInt(e.detail.value, 10)
      this.currentSchool = this.schools[i] || null
    },
    loadPosts() {
      if (!this.currentSchool) return
      this.loading = true
      api.schoolCircle.posts(this.currentSchool.id, 0, 50).then(res => {
        this.list = Array.isArray(res) ? res : []
      }).catch(() => { this.list = [] }).finally(() => { this.loading = false })
    },
    formatTime(t) {
      if (!t) return ''
      const d = new Date(t)
      const now = new Date()
      const diff = now - d
      if (diff < 60000) return '刚刚'
      if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
      if (d.toDateString() === now.toDateString()) return (d.getHours() + ':' + String(d.getMinutes()).padStart(2, '0'))
      return d.getMonth() + 1 + '-' + d.getDate()
    },
    showPublish() {
      this.publishContent = ''
      this.publishVisible = true
    },
    submitPost() {
      const content = (this.publishContent || '').trim()
      if (!content) {
        uni.showToast({ title: '请输入内容', icon: 'none' })
        return
      }
      if (!this.currentSchool) return
      api.schoolCircle.post({ schoolId: this.currentSchool.id, content }).then(() => {
        this.publishVisible = false
        uni.showToast({ title: '发布成功', icon: 'none' })
        this.loadPosts()
      }).catch(e => uni.showToast({ title: e.message || '发布失败', icon: 'none' }))
    }
  }
}
</script>

<style scoped>
.page { padding: 24rpx; padding-bottom: 120rpx; }
.picker-row { display: flex; align-items: center; padding: 28rpx; border-radius: 16rpx; margin-bottom: 24rpx; }
.label { font-size: 28rpx; color: #666; margin-right: 24rpx; }
.picker-value { flex: 1; font-size: 30rpx; color: #2d5a3d; }
.post-btn-wrap { margin-bottom: 24rpx; }
.post-btn { background: #2d5a3d; color: #fff; border: none; border-radius: 12rpx; padding: 20rpx 32rpx; font-size: 28rpx; }
.list { padding: 0; }
.post { padding: 28rpx; margin-bottom: 24rpx; border-radius: 16rpx; box-shadow: 0 4rpx 16rpx rgba(0,0,0,.06); }
.post-head { display: flex; align-items: center; margin-bottom: 16rpx; }
.avatar { width: 72rpx; height: 72rpx; border-radius: 36rpx; margin-right: 20rpx; overflow: hidden; background: #f0f0f0; }
.avatar image { width: 100%; height: 100%; }
.avatar.placeholder { display: flex; align-items: center; justify-content: center; font-size: 28rpx; color: #999; }
.meta { flex: 1; }
.nickname { font-size: 28rpx; font-weight: 600; color: #1a1a1a; display: block; }
.time { font-size: 24rpx; color: #999; }
.content { font-size: 28rpx; color: #333; line-height: 1.5; white-space: pre-wrap; }
.images { margin-top: 16rpx; display: flex; flex-wrap: wrap; gap: 12rpx; }
.thumb { width: 160rpx; height: 160rpx; border-radius: 8rpx; }
.empty, .empty-hint { text-align: center; padding: 80rpx 24rpx; color: #999; font-size: 28rpx; }
.loading-wrap { text-align: center; padding: 48rpx; color: #999; }
.publish-mask { position: fixed; left: 0; right: 0; top: 0; bottom: 0; background: rgba(0,0,0,.5); z-index: 100; display: flex; align-items: flex-end; }
.publish-box { width: 100%; background: #fff; border-radius: 24rpx 24rpx 0 0; padding: 32rpx; max-height: 70vh; }
.publish-input { width: 100%; min-height: 200rpx; padding: 24rpx; border: 1rpx solid #eee; border-radius: 12rpx; font-size: 28rpx; box-sizing: border-box; }
.publish-actions { display: flex; justify-content: flex-end; gap: 24rpx; margin-top: 24rpx; }
.btn-cancel { background: #f5f5f5; color: #666; border: none; padding: 20rpx 36rpx; border-radius: 12rpx; font-size: 28rpx; }
.btn-submit { background: #2d5a3d; color: #fff; border: none; padding: 20rpx 36rpx; border-radius: 12rpx; font-size: 28rpx; }
</style>
