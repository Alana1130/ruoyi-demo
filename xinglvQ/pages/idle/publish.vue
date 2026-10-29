<template>
  <view class="page">
    <view class="form">
      <view class="row"><text class="label">标题</text><input class="inp" v-model="form.title" placeholder="如：高数教材、登山包" /></view>
      <view class="row"><text class="label">分类</text><picker mode="selector" :range="categories" range-key="label" @change="onCategoryChange"><view class="picker">{{ form.categoryLabel }}</view></picker></view>
      <view class="row"><text class="label">描述</text><textarea class="inp area" v-model="form.description" placeholder="可选" /></view>
      <view class="row"><text class="label">售价（星星）</text><input class="inp" type="number" v-model="form.priceStars" placeholder="整数" /></view>
      <view class="row"><text class="label">图片链接（多张逗号分隔，可选）</text><input class="inp" v-model="form.images" placeholder="https://..." /></view>
      <button class="btn primary" @tap="submit">{{ id ? '保存' : '发布' }}</button>
    </view>
  </view>
</template>

<script>
import api from '@/utils/api.js'
const CATEGORIES = [{ value: 'textbook', label: '教材' }, { value: 'travel_gear', label: '出行装备' }, { value: 'other', label: '其他' }]
export default {
  data() {
    return {
      id: null,
      form: { title: '', category: 'other', categoryLabel: '其他', description: '', priceStars: '', images: '' },
      categories: CATEGORIES
    }
  },
  onLoad(op) {
    if (op.id) {
      this.id = op.id
      api.idle.my().then(myList => {
        const res = (myList || []).find(x => x.id == op.id)
        if (!res) return
        if (res) {
          this.form.title = res.title || ''
          this.form.category = res.category || 'other'
          this.form.categoryLabel = (CATEGORIES.find(c => c.value === this.form.category) || {}).label || '其他'
          this.form.description = res.description || ''
          this.form.priceStars = String(res.priceStars != null ? res.priceStars : '')
          this.form.images = res.images || ''
        }
      }).catch(() => {})
    } else {
      this.form.categoryLabel = '其他'
    }
  },
  methods: {
    onCategoryChange(e) {
      const c = CATEGORIES[e.detail.value]
      if (c) { this.form.category = c.value; this.form.categoryLabel = c.label }
    },
    submit() {
      if (!this.form.title.trim()) { uni.showToast({ title: '请输入标题', icon: 'none' }); return }
      const stars = parseInt(this.form.priceStars, 10)
      if (isNaN(stars) || stars < 0) { uni.showToast({ title: '请输入有效星星数', icon: 'none' }); return }
      const payload = { title: this.form.title, category: this.form.category, description: this.form.description, priceStars: stars, images: this.form.images }
      if (this.id) {
        api.idle.update(this.id, payload).then(() => { uni.showToast({ title: '已更新' }); uni.navigateBack() }).catch(e => uni.showToast({ title: e.message || '失败', icon: 'none' }))
      } else {
        api.idle.publish(payload).then(() => { uni.showToast({ title: '已发布' }); uni.navigateBack() }).catch(e => uni.showToast({ title: e.message || '失败', icon: 'none' }))
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
.area { min-height: 120rpx; }
.picker { color: #1a1a1a; }
.btn.primary { margin-top: 32rpx; background: #2d5a3d; color: #fff; height: 88rpx; line-height: 88rpx; border-radius: 16rpx; }
</style>
