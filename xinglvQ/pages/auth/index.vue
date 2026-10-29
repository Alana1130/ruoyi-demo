<template>
  <view class="page">
    <view class="tabs">
      <view class="tab" :class="{ on: tab === 'login' }" @tap="tab = 'login'">登录</view>
      <view class="tab" :class="{ on: tab === 'register' }" @tap="tab = 'register'">注册</view>
    </view>
    <view class="form" v-if="tab === 'login'">
      <input class="input" v-model="loginPhone" placeholder="手机号" type="number" maxlength="11" />
      <input class="input" v-model="loginPassword" placeholder="密码" password />
      <button class="btn" @tap="doLogin">登录</button>
    </view>
    <view class="form" v-else>
      <input class="input" v-model="regPhone" placeholder="手机号" type="number" maxlength="11" />
      <input class="input" v-model="regPassword" placeholder="设置密码" password />
      <input class="input" v-model="regNickname" placeholder="昵称（选填）" />
      <button class="btn" @tap="doRegister">注册</button>
    </view>
    <view class="tip">注册即表示同意用户协议与隐私政策</view>
    <view class="tip hint">未注册用户直接打开小程序即可使用；若要在其他设备用手机号登录，请先注册，或在使用中的账号里进入「账号管理」→「编辑」→ 绑定手机号并设置密码。</view>
  </view>
</template>

<script>
import api from '@/utils/api.js'
export default {
  data() {
    return {
      tab: 'login',
      loginPhone: '',
      loginPassword: '',
      regPhone: '',
      regPassword: '',
      regNickname: ''
    }
  },
  methods: {
    doLogin() {
      const phone = (this.loginPhone || '').trim()
      const password = this.loginPassword || ''
      if (!phone || !password) {
        uni.showToast({ title: '请填写手机号和密码', icon: 'none' })
        return
      }
      console.log('登录请求参数:', { phone, password })
      uni.showLoading({ title: '登录中...' })
      api.auth.login({ phone, password }).then(res => {
        console.log('登录成功响应:', res)
        uni.hideLoading()
        uni.showToast({ title: '登录成功', icon: 'none' })
        setTimeout(() => uni.navigateBack(), 500)
      }).catch(e => {
        console.error('登录失败:', e)
        uni.hideLoading()
        uni.showToast({ title: e.message || '登录失败', icon: 'none' })
      })
    },
    doRegister() {
      const phone = (this.regPhone || '').trim()
      const password = this.regPassword || ''
      if (!phone || !password) {
        uni.showToast({ title: '请填写手机号和密码', icon: 'none' })
        return
      }
      if (phone.length < 11) {
        uni.showToast({ title: '请输入正确手机号', icon: 'none' })
        return
      }
      uni.showLoading({ title: '注册中...' })
      api.auth.register({ phone, password, nickname: (this.regNickname || '').trim() }).then(() => {
        uni.hideLoading()
        uni.showToast({ title: '注册成功', icon: 'none' })
        setTimeout(() => uni.navigateBack(), 500)
      }).catch(e => {
        uni.hideLoading()
        uni.showToast({ title: e.message || '注册失败', icon: 'none' })
      })
    }
  }
}
</script>

<style scoped>
.page { padding: 48rpx 32rpx; min-height: 100vh; background: #f5f5f0; }
.tabs { display: flex; margin-bottom: 48rpx; }
.tab { flex: 1; text-align: center; font-size: 32rpx; color: #666; padding: 20rpx; }
.tab.on { color: #2d5a3d; font-weight: 600; border-bottom: 4rpx solid #2d5a3d; }
.form { background: #fff; border-radius: 20rpx; padding: 40rpx; box-shadow: 0 4rpx 20rpx rgba(0,0,0,.06); }
.input { width: 100%; padding: 28rpx; border: 1rpx solid #eee; border-radius: 12rpx; margin-bottom: 24rpx; font-size: 28rpx; box-sizing: border-box; }
.btn { background: linear-gradient(135deg, #4a7c59, #2d5a3d); color: #fff; border: none; border-radius: 12rpx; padding: 28rpx; font-size: 30rpx; margin-top: 16rpx; }
.tip { text-align: center; color: #999; font-size: 24rpx; margin-top: 40rpx; }
.tip.hint { margin-top: 16rpx; font-size: 22rpx; color: #aaa; line-height: 1.5; padding: 0 16rpx; }
</style>
