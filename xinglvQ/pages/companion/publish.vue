<template>
  <view class="page">
    <view class="card">
      <view class="label">标题 *</view>
      <input class="inp" v-model="form.title" placeholder="例：五一杭州两日寻搭子" />
      <view class="label">目的地（选填）</view>
      <view class="picker-btn" @tap="showDestPicker = true">{{ form.destinationName || '选择目的地' }}</view>
      <view class="label">出发日期（选填）</view>
      <picker mode="date" :value="form.travelDateStart" @change="onStartChange">
        <view class="picker-btn">{{ form.travelDateStart || '选择日期' }}</view>
      </picker>
      <view class="label">预算区间（选填）</view>
      <input class="inp" v-model="form.budgetRange" placeholder="例：500-1000" />
      <view class="label">内容说明</view>
      <textarea class="textarea" v-model="form.content" placeholder="简单说说行程想法、人数等" maxlength="500" />
      <view class="label">联系方式说明（选填）</view>
      <input class="inp" v-model="form.contactRemark" placeholder="例：站内私信 / 微信xxx" />
      <button class="btn primary" :disabled="submitting || !form.title.trim()" @tap="submit">{{ submitting ? '发布中...' : '发布' }}</button>
    </view>
    <view class="dest-mask" v-if="showDestPicker" @tap="showDestPicker = false">
      <view class="dest-panel" @tap.stop>
        <scroll-view scroll-y class="dest-list">
          <view class="dest-opt" @tap="selectDest(null, '')">不选</view>
          <view class="dest-opt" v-for="d in destinations" :key="d.id" @tap="selectDest(d.id, d.name)">{{ d.name }}</view>
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
      id: null,
      form: {
        title: '',
        content: '',
        destinationId: null,
        destinationName: '',
        travelDateStart: '',
        travelDateEnd: '',
        budgetRange: '',
        contactRemark: ''
      },
      destinations: [],
      showDestPicker: false,
      submitting: false
    }
  },
  onLoad(op) {
    this.id = op.id ? Number(op.id) : null
    api.destinations.list().then(res => { this.destinations = res || [] }).catch(() => {})
    if (this.id) {
      api.companions.get(this.id).then(res => {
        this.form.title = res.title || ''
        this.form.content = res.content || ''
        this.form.destinationId = res.destinationId
        this.form.destinationName = res.destinationName || ''
        this.form.travelDateStart = this.toDateStr(res.travelDateStart)
        this.form.travelDateEnd = this.toDateStr(res.travelDateEnd)
        this.form.budgetRange = res.budgetRange || ''
        this.form.contactRemark = res.contactRemark || ''
      }).catch(() => {})
    }
  },
  methods: {
    toDateStr(d) {
      if (!d) return ''
      if (typeof d === 'string') return d.slice(0, 10)
      const date = new Date(d)
      return date.toISOString ? date.toISOString().slice(0, 10) : ''
    },
    onStartChange(e) {
      this.form.travelDateStart = e.detail.value
    },
    selectDest(id, name) {
      this.form.destinationId = id
      this.form.destinationName = name
      this.showDestPicker = false
    },
    submit() {
      if (!this.form.title.trim()) {
        uni.showToast({ title: '请填写标题', icon: 'none' })
        return
      }
      this.submitting = true
      const payload = {
        title: this.form.title.trim(),
        content: (this.form.content || '').trim(),
        destinationId: this.form.destinationId,
        travelDateStart: this.form.travelDateStart || null,
        travelDateEnd: this.form.travelDateEnd || null,
        budgetRange: (this.form.budgetRange || '').trim(),
        contactRemark: (this.form.contactRemark || '').trim()
      }
      const req = this.id
        ? api.companions.update(this.id, payload)
        : api.companions.create(payload)
      req.then(() => {
        uni.showToast({ title: this.id ? '已更新' : '发布成功' })
        setTimeout(() => uni.navigateBack(), 1000)
      }).catch(e => {
        uni.showToast({ title: e.message || '失败', icon: 'none' })
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
.textarea { min-height: 160rpx; }
.btn { margin-top: 40rpx; height: 88rpx; line-height: 88rpx; border-radius: 12rpx; font-size: 32rpx; }
.btn.primary { background: #2d5a3d; color: #fff; }
.dest-mask { position: fixed; left: 0; right: 0; top: 0; bottom: 0; background: rgba(0,0,0,.4); z-index: 100; }
.dest-panel { position: absolute; left: 0; right: 0; bottom: 0; max-height: 60vh; background: #fff; border-radius: 20rpx 20rpx 0 0; }
.dest-list { max-height: 50vh; }
.dest-opt { padding: 28rpx; font-size: 30rpx; border-bottom: 1rpx solid #f0f0f0; }
</style>
