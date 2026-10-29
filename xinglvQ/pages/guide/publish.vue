<template>
  <view class="page">
    <view class="card">
      <view class="label">目的地 *</view>
      <view class="picker-btn" @tap="showDestPicker = true">{{ form.destinationName || '选择目的地' }}</view>
      <view class="label">标题（选填，不填则取内容前几句）</view>
      <input class="inp" v-model="form.title" placeholder="例：杭州两日穷游攻略" />
      <view class="label">话题标签（选填，逗号分隔）</view>
      <input class="inp" v-model="form.tagsStr" placeholder="例：穷游,周末两日游" />
      <view class="label">攻略内容 *</view>
      <textarea class="textarea" v-model="form.content" placeholder="分享你的行程、花费、 tips～" maxlength="2000" />
      <view class="count">{{ form.content.length }}/2000</view>
      <button class="btn primary" :disabled="submitting || !form.content.trim() || !form.destinationId" @tap="submit">{{ submitting ? '提交中...' : '提交' }}</button>
    </view>
    <view class="dest-mask" v-if="showDestPicker" @tap="showDestPicker = false">
      <view class="dest-panel" @tap.stop>
        <scroll-view scroll-y class="dest-list">
          <view class="dest-opt" v-for="d in destinations" :key="d.id" @tap="selectDest(d)">{{ d.name }}</view>
        </scroll-view>
      </view>
    </view>
  </view>
</template>

<script>
import api from '@/utils/api.js'
export default {
  data() {
    return {
      form: {
        destinationId: null,
        destinationName: '',
        title: '',
        tagsStr: '',
        content: ''
      },
      destinations: [],
      showDestPicker: false,
      submitting: false
    }
  },
  onLoad(op) {
    api.destinations.list().then(res => { this.destinations = res || [] }).catch(() => {})
    if (op.destinationId) {
      this.form.destinationId = Number(op.destinationId)
      this.form.destinationName = op.destinationName ? decodeURIComponent(op.destinationName) : ''
    }
  },
  methods: {
    selectDest(d) {
      this.form.destinationId = d.id
      this.form.destinationName = d.name
      this.showDestPicker = false
    },
    submit() {
      if (!this.form.content.trim()) {
        uni.showToast({ title: '请填写攻略内容', icon: 'none' })
        return
      }
      if (!this.form.destinationId) {
        uni.showToast({ title: '请选择目的地', icon: 'none' })
        return
      }
      this.submitting = true
      const tags = (this.form.tagsStr || '').trim()
      api.experiences.submit({
        targetType: 'destination',
        targetId: this.form.destinationId,
        content: this.form.content.trim(),
        title: (this.form.title || '').trim() || null,
        tags: tags || null
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
.page { padding: 24rpx; background: #f2f4f3; min-height: 100vh; }
.card { background: #fff; border-radius: 20rpx; padding: 28rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,.06); }
.label { font-size: 28rpx; color: #333; margin-bottom: 12rpx; margin-top: 20rpx; }
.label:first-child { margin-top: 0; }
.inp, .picker-btn, .textarea { width: 100%; padding: 24rpx; border: 1rpx solid #e8e8e8; border-radius: 12rpx; font-size: 28rpx; box-sizing: border-box; }
.textarea { min-height: 280rpx; }
.count { font-size: 24rpx; color: #999; text-align: right; margin-top: 8rpx; }
.btn { margin-top: 40rpx; height: 88rpx; line-height: 88rpx; border-radius: 12rpx; font-size: 32rpx; }
.btn.primary { background: #2d5a3d; color: #fff; }
.dest-mask { position: fixed; left: 0; right: 0; top: 0; bottom: 0; background: rgba(0,0,0,.4); z-index: 100; }
.dest-panel { position: absolute; left: 0; right: 0; bottom: 0; max-height: 60vh; background: #fff; border-radius: 20rpx 20rpx 0 0; }
.dest-list { max-height: 50vh; }
.dest-opt { padding: 28rpx; font-size: 30rpx; border-bottom: 1rpx solid #f0f0f0; }
</style>
