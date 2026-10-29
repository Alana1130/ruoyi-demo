<template>
  <view class="page page-wrap">
    <view class="hero">
      <view class="title">{{ id ? '编辑地址' : '新增收货地址' }}</view>
      <view class="desc">{{ id ? '修改收货人、电话或详细地址' : '填写后可在下单时快速选择' }}</view>
    </view>
    <view class="card card-base form">
      <view class="form-item">
        <text class="label">收货人</text>
        <input class="input" v-model="form.receiverName" placeholder="请输入收货人姓名" placeholder-class="placeholder" />
      </view>
      <view class="form-item">
        <text class="label">联系电话</text>
        <input class="input" type="number" v-model="form.receiverPhone" placeholder="请输入手机号" placeholder-class="placeholder" />
      </view>
      <view class="form-item">
        <text class="label">详细地址</text>
        <textarea class="textarea" v-model="form.receiverAddress" placeholder="省市区、街道、门牌号等" placeholder-class="placeholder" />
      </view>
      <view class="form-item row default-row">
        <text class="label">设为默认地址</text>
        <switch :checked="form.isDefault === 1" @change="onDefaultChange" color="#2d5a3d" />
      </view>
    </view>
    <view class="footer-btn">
      <button class="btn-save" :disabled="submitting" @tap="save">{{ submitting ? '保存中...' : '保存' }}</button>
    </view>
    <view class="del-wrap" v-if="id">
      <view class="btn-del" @tap="del">删除此地址</view>
    </view>
  </view>
</template>

<script>
import api from '@/utils/api.js'
export default {
  data() {
    return {
      id: null,
      form: { receiverName: '', receiverPhone: '', receiverAddress: '', isDefault: 0 },
      submitting: false
    }
  },
  onLoad(op) {
    this.id = op.id ? Number(op.id) : null
    if (this.id) this.load()
  },
  methods: {
    load() {
      api.addresses.list().then(res => {
        const list = Array.isArray(res) ? res : []
        const a = list.find(x => x.id === this.id)
        if (a) {
          this.form.receiverName = a.receiverName || ''
          this.form.receiverPhone = a.receiverPhone || ''
          this.form.receiverAddress = a.receiverAddress || ''
          this.form.isDefault = a.isDefault != null ? a.isDefault : 0
        }
      })
    },
    onDefaultChange(e) {
      this.form.isDefault = e.detail.value ? 1 : 0
    },
    save() {
      const name = (this.form.receiverName || '').trim()
      const phone = (this.form.receiverPhone || '').trim()
      const addr = (this.form.receiverAddress || '').trim()
      if (!name) { uni.showToast({ title: '请填写收货人', icon: 'none' }); return }
      if (!phone) { uni.showToast({ title: '请填写联系电话', icon: 'none' }); return }
      if (!addr) { uni.showToast({ title: '请填写收货地址', icon: 'none' }); return }
      this.submitting = true
      if (this.id) {
        api.addresses.update(this.id, {
          receiverName: name,
          receiverPhone: phone,
          receiverAddress: addr,
          isDefault: this.form.isDefault
        }).then(() => {
          uni.showToast({ title: '保存成功' })
          setTimeout(() => uni.navigateBack(), 800)
        }).catch(e => {
          uni.showToast({ title: (e && e.message) || '保存失败', icon: 'none' })
        }).finally(() => { this.submitting = false })
      } else {
        api.addresses.add({
          receiverName: name,
          receiverPhone: phone,
          receiverAddress: addr,
          isDefault: this.form.isDefault
        }).then(() => {
          uni.showToast({ title: '添加成功' })
          setTimeout(() => uni.navigateBack(), 800)
        }).catch(e => {
          uni.showToast({ title: (e && e.message) || '添加失败', icon: 'none' })
        }).finally(() => { this.submitting = false })
      }
    },
    del() {
      uni.showModal({
        title: '确认删除',
        content: '确定删除该收货地址吗？',
        success: (res) => {
          if (res.confirm) {
            api.addresses.delete(this.id).then(() => {
              uni.showToast({ title: '已删除' })
              setTimeout(() => uni.navigateBack(), 800)
            }).catch(e => {
              uni.showToast({ title: (e && e.message) || '删除失败', icon: 'none' })
            })
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.page { padding-bottom: 220rpx; }
.hero { padding: 28rpx 24rpx 24rpx; }
.title { font-size: 42rpx; font-weight: 700; color: #1a1a1a; margin-bottom: 14rpx; letter-spacing: 0.5rpx; }
.desc { font-size: 28rpx; color: #666; line-height: 1.55; }
.form { margin: 0 24rpx 24rpx; padding: 32rpx; border-radius: 20rpx; box-shadow: 0 4rpx 20rpx rgba(45,90,61,.06); background: #fff; }
.form-item { margin-bottom: 32rpx; }
.form-item:last-of-type { margin-bottom: 0; }
.form-item.row { display: flex; justify-content: space-between; align-items: center; padding-top: 8rpx; }
.form-item.row.default-row { border-top: 1rpx solid #eee; margin-top: 28rpx; padding-top: 28rpx; }
.label { display: block; font-size: 30rpx; color: #333; margin-bottom: 16rpx; font-weight: 600; }
.form-item.row .label { margin-bottom: 0; font-weight: 500; }
.input { 
  display: block;
  height: 88rpx; 
  line-height: 88rpx; 
  padding: 0 28rpx; 
  border: 2rpx solid #e8e8e8; 
  border-radius: 16rpx; 
  font-size: 32rpx; 
  color: #1a1a1a; 
  background: #fafafa; 
  width: 100%; 
  box-sizing: border-box;
}
.textarea { 
  display: block;
  min-height: 200rpx; 
  padding: 24rpx 28rpx; 
  border: 2rpx solid #e8e8e8; 
  border-radius: 16rpx; 
  font-size: 32rpx; 
  line-height: 1.5; 
  color: #1a1a1a; 
  background: #fafafa; 
  width: 100%; 
  box-sizing: border-box;
}
.placeholder { color: #aaa; font-size: 32rpx; }
.footer-btn { position: fixed; bottom: 0; left: 0; right: 0; padding: 24rpx; padding-bottom: calc(24rpx + env(safe-area-inset-bottom)); background: #fff; box-shadow: 0 -8rpx 24rpx rgba(0,0,0,.06); }
.btn-save { width: 100%; height: 96rpx; line-height: 96rpx; background: linear-gradient(135deg, #4a7c59 0%, #2d5a3d 100%); color: #fff; font-size: 32rpx; font-weight: 600; border: none; border-radius: 20rpx; }
.del-wrap { padding: 36rpx 24rpx; text-align: center; }
.btn-del { font-size: 28rpx; color: #999; padding: 16rpx; }
</style>
