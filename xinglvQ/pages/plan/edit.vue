<template>
  <view class="page">
    <view class="form">
      <view class="row"><text class="label">计划名称</text><input class="inp" v-model="form.name" placeholder="如：2025寒假、五一假期" /></view>
      <view class="row"><text class="label">开始日期</text><input class="inp" type="date" v-model="form.startDate" /></view>
      <view class="row"><text class="label">结束日期</text><input class="inp" type="date" v-model="form.endDate" /></view>
      <view class="row"><text class="label">想去的目的地（多选）</text></view>
      <view class="tags">
        <view class="tag" v-for="d in destinations" :key="d.id" :class="{ on: selectedIds.indexOf(d.id) >= 0 }" @tap="toggleDest(d.id)">{{ d.name }}</view>
      </view>
      <view class="row"><text class="label">备注</text><input class="inp" v-model="form.memo" placeholder="可选" /></view>
      <button class="btn primary" @tap="submit">保存</button>
    </view>
  </view>
</template>

<script>
import api from '@/utils/api.js'
export default {
  data() {
    return { id: null, form: { name: '', startDate: '', endDate: '', memo: '' }, destinations: [], selectedIds: [] }
  },
  onLoad(op) {
    api.destinations.list().then(res => { this.destinations = res || [] }).catch(() => {})
    if (op.id) {
      this.id = op.id
      api.plans.get(op.id).then(res => {
        if (res) {
          this.form.name = res.name || ''
          this.form.startDate = this.toDateStr(res.startDate)
          this.form.endDate = this.toDateStr(res.endDate)
          this.form.memo = res.memo || ''
          this.selectedIds = (res.destinationIds || []).map(x => Number(x))
        }
      })
    }
  },
  methods: {
    toDateStr(d) {
      if (!d) return ''
      if (typeof d === 'string') return d.substring(0, 10)
      if (typeof d === 'number') return new Date(d).toISOString().slice(0, 10)
      if (d.year != null) return d.year + '-' + String(d.month || d.monthValue).padStart(2, '0') + '-' + String(d.dayOfMonth || d.date).padStart(2, '0')
      return ''
    },
    toggleDest(id) {
      const i = this.selectedIds.indexOf(id)
      if (i >= 0) this.selectedIds.splice(i, 1)
      else this.selectedIds.push(id)
    },
    submit() {
      if (!this.form.name.trim()) { uni.showToast({ title: '请输入计划名称', icon: 'none' }); return }
      if (!this.form.startDate || !this.form.endDate) { uni.showToast({ title: '请选择开始和结束日期', icon: 'none' }); return }
      const payload = { name: this.form.name, startDate: this.form.startDate, endDate: this.form.endDate, memo: this.form.memo, destinationIds: this.selectedIds }
      if (this.id) {
        api.plans.update(this.id, payload).then(() => { uni.showToast({ title: '已更新' }); uni.navigateBack() }).catch(e => uni.showToast({ title: e.message || '失败', icon: 'none' }))
      } else {
        api.plans.add(payload).then(() => { uni.showToast({ title: '已添加' }); uni.navigateBack() }).catch(e => uni.showToast({ title: e.message || '失败', icon: 'none' }))
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
.inp { border: 1rpx solid #eee; border-radius: 12rpx; padding: 24rpx; font-size: 28rpx; }
.tags { display: flex; flex-wrap: wrap; gap: 16rpx; margin-bottom: 24rpx; }
.tag { padding: 12rpx 24rpx; background: #f5f5f5; border-radius: 24rpx; font-size: 26rpx; color: #666; }
.tag.on { background: #2d5a3d; color: #fff; }
.btn.primary { margin-top: 32rpx; background: #2d5a3d; color: #fff; height: 88rpx; line-height: 88rpx; border-radius: 16rpx; }
</style>
