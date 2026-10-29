<template>
  <view class="page">
    <view class="form">
      <view class="row"><text class="label">标题</text><input class="inp" v-model="form.title" placeholder="如：西安三日游" /></view>
      <view class="row"><text class="label">出行日期</text><input class="inp" type="date" v-model="form.tripDate" /></view>
      <view class="row"><text class="label">关联目的地</text><picker mode="selector" :range="destNames" range-key="name" @change="onDestChange"><view class="picker">{{ form.destinationName || '可选' }}</view></picker></view>
      <view class="row"><text class="label">提前提醒(天)</text><input class="inp" type="number" v-model="form.remindDays" placeholder="0不提醒" /></view>
      <view class="row"><text class="label">备注</text><input class="inp" v-model="form.memo" placeholder="可选" /></view>
      <button class="btn primary" @tap="submit">保存</button>
    </view>
  </view>
</template>

<script>
import api from '@/utils/api.js'
export default {
  data() {
    return {
      id: null,
      form: { title: '', tripDate: '', destinationId: null, destinationName: '', remindDays: 1, memo: '' },
      destinations: []
    }
  },
  computed: {
    destNames() {
      return [{ id: null, name: '不关联' }].concat((this.destinations || []).map(d => ({ id: d.id, name: d.name })))
    }
  },
  onLoad(op) {
    api.destinations.list().then(res => { this.destinations = res || [] }).catch(() => {})
    if (op.id) {
      this.id = op.id
      api.countdowns.list().then(res => {
        const c = (res || []).find(x => x.id == op.id)
        if (c) {
          this.form.title = c.title
          let d = c.tripDate
          if (typeof d === 'string') this.form.tripDate = d.substring(0, 10)
          else if (d && typeof d === 'number') this.form.tripDate = new Date(d).toISOString().slice(0, 10)
          else this.form.tripDate = ''
          this.form.destinationId = c.destinationId
          this.form.destinationName = c.destinationName || ''
          this.form.remindDays = c.remindDays != null ? c.remindDays : 1
          this.form.memo = c.memo || ''
        }
      })
    }
  },
  methods: {
    onDestChange(e) {
      const i = e.detail.value
      const arr = this.destNames
      if (arr[i]) {
        this.form.destinationId = arr[i].id
        this.form.destinationName = arr[i].name
      }
    },
    submit() {
      if (!this.form.title.trim()) { uni.showToast({ title: '请输入标题', icon: 'none' }); return }
      if (!this.form.tripDate) { uni.showToast({ title: '请选择出行日期', icon: 'none' }); return }
      const payload = { title: this.form.title, tripDate: this.form.tripDate, destinationId: this.form.destinationId || null, remindDays: parseInt(this.form.remindDays, 10) || 0, memo: this.form.memo }
      if (this.id) {
        api.countdowns.update(this.id, payload).then(() => { uni.showToast({ title: '已更新' }); uni.navigateBack() }).catch(e => uni.showToast({ title: e.message || '失败', icon: 'none' }))
      } else {
        api.countdowns.add(payload).then(() => { uni.showToast({ title: '已添加' }); uni.navigateBack() }).catch(e => uni.showToast({ title: e.message || '失败', icon: 'none' }))
      }
    }
  }
}
</script>

<style scoped>
.page { padding: 24rpx; background: #f2f4f3; min-height: 100vh; }
.form { background: #fff; border-radius: 20rpx; padding: 28rpx; box-shadow: 0 4rpx 20rpx rgba(0,0,0,.06); }
.row { margin-bottom: 24rpx; }
.label { display: block; font-size: 28rpx; color: #666; margin-bottom: 8rpx; }
.inp, .picker { border: 1rpx solid #eee; border-radius: 12rpx; padding: 24rpx; font-size: 28rpx; }
.picker { color: #1a1a1a; }
.btn.primary { margin-top: 32rpx; background: #2d5a3d; color: #fff; height: 88rpx; line-height: 88rpx; border-radius: 16rpx; }
</style>
