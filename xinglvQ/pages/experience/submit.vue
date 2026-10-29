<template>
  <view class="page">
    <view class="title">写体验</view>
    <view class="target-name" v-if="targetName">为「{{ targetName }}」写一段真实体验</view>
    <view class="card">
      <textarea class="textarea" v-model="content" placeholder="分享你的真实体验，核验通过后将展示给其他同学～" maxlength="500" />
      <view class="count">{{ content.length }}/500</view>
      <button class="btn" :disabled="submitting || !content.trim()" @tap="submit">{{ submitting ? '提交中...' : '提交' }}</button>
    </view>
  </view>
</template>

<script>
import api from '@/utils/api.js'
export default {
  data() {
    return {
      targetType: '',
      targetId: null,
      targetName: '',
      content: '',
      submitting: false
    }
  },
  onLoad(op) {
    this.targetType = op.targetType || 'attraction'
    this.targetId = op.targetId ? Number(op.targetId) : null
    this.targetName = op.targetName ? decodeURIComponent(op.targetName) : ''
  },
  methods: {
    submit() {
      if (!this.content.trim()) {
        uni.showToast({ title: '请填写体验内容', icon: 'none' })
        return
      }
      if (!this.targetId) {
        uni.showToast({ title: '参数错误', icon: 'none' })
        return
      }
      this.submitting = true
      api.experiences.submit({
        targetType: this.targetType,
        targetId: this.targetId,
        content: this.content.trim()
      }).then(() => {
        uni.showToast({ title: '提交成功，核验通过后将展示' })
        setTimeout(() => uni.navigateBack(), 1200)
      }).catch(e => {
        uni.showToast({ title: e.message || '提交失败', icon: 'none' })
      }).finally(() => {
        this.submitting = false
      })
    }
  }
}
</script>

<style scoped>
.page { padding: 24rpx; min-height: 100vh; background: #f5f5f0; }
.title { font-size: 36rpx; font-weight: 700; margin-bottom: 12rpx; }
.target-name { font-size: 28rpx; color: #666; margin-bottom: 24rpx; }
.card { background: #fff; border-radius: 16rpx; padding: 24rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,.06); }
.textarea { width: 100%; min-height: 240rpx; border: 1rpx solid #e0e0e0; border-radius: 12rpx; padding: 20rpx; font-size: 28rpx; box-sizing: border-box; }
.count { font-size: 24rpx; color: #999; text-align: right; margin-top: 12rpx; }
.btn { background: #2d5a3d; color: #fff; font-size: 30rpx; padding: 24rpx; border-radius: 12rpx; margin-top: 24rpx; }
</style>
