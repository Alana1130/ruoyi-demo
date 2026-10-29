<template>
  <view class="page page-wrap">
    <view class="hero">
      <view class="title">编辑资料</view>
      <view class="subtitle">修改昵称与头像，保存后将在「我的」页展示</view>
    </view>
    <view class="card card-base">
      <view class="form-item">
        <text class="label">昵称</text>
        <input class="input" v-model="form.nickname" placeholder="请输入昵称" />
      </view>
      <view class="form-item">
        <text class="label">头像链接</text>
        <input class="input" v-model="form.avatar" placeholder="选填，图片地址" />
      </view>
      <view class="avatar-preview" v-if="form.avatar">
        <image :src="form.avatar" mode="aspectFill" class="preview-img" @error="onImgError" />
      </view>
      <button class="btn" :disabled="saving" @tap="save">{{ saving ? '保存中...' : '保存' }}</button>
    </view>
    <view class="tip">保存后返回「我的」即可看到更新</view>
  </view>
</template>

<script>
import api from '@/utils/api.js'
export default {
  data() {
    return {
      form: { nickname: '', avatar: '' },
      saving: false
    }
  },
  onLoad() {
    api.user.me().then(res => {
      if (res) {
        this.form.nickname = res.nickname || ''
        this.form.avatar = res.avatar || ''
      }
    })
  },
  methods: {
    onImgError() {
      this.form.avatar = ''
    },
    save() {
      this.saving = true
      api.user.updateProfile({
        nickname: this.form.nickname || undefined,
        avatar: this.form.avatar || undefined
      }).then(() => {
        uni.showToast({ title: '保存成功' })
        setTimeout(() => uni.navigateBack(), 800)
      }).catch(e => {
        uni.showToast({ title: e.message || '保存失败', icon: 'none' })
      }).finally(() => {
        this.saving = false
      })
    }
  }
}
</script>

<style scoped>
.page { padding: 0; }
.hero { padding: 24rpx 24rpx 16rpx; }
.title { font-size: 40rpx; font-weight: 700; color: #1a1a1a; margin-bottom: 8rpx; }
.subtitle { font-size: 26rpx; color: #666; }
.card { margin: 0 24rpx; padding: 28rpx; }
.form-item { margin-bottom: 24rpx; }
.label { display: block; font-size: 28rpx; color: #666; margin-bottom: 12rpx; }
.input { border: 1rpx solid #e8e8e8; padding: 24rpx; border-radius: 16rpx; font-size: 30rpx; background: #fff; width: 100%; box-sizing: border-box; }
.avatar-preview { margin: 24rpx 0; text-align: center; }
.preview-img { width: 120rpx; height: 120rpx; border-radius: 60rpx; background: #f0f0f0; }
.btn { background: linear-gradient(135deg, #4a7c59 0%, #2d5a3d 100%); color: #fff; font-size: 30rpx; font-weight: 600; padding: 24rpx; border-radius: 16rpx; margin-top: 16rpx; border: none; width: 100%; }
.tip { font-size: 24rpx; color: #999; text-align: center; margin-top: 32rpx; padding-bottom: 40rpx; }
</style>
