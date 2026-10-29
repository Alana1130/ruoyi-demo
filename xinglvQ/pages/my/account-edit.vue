<template>
  <view class="page page-wrap">
    <!-- 未绑定手机：绑定后即可用手机号+密码登录 -->
    <view class="card card-base" v-if="!hasPhone">
      <view class="title">绑定手机号并设置密码</view>
      <view class="desc">您当前是游客身份，绑定后可在其他设备用「手机号+密码」登录，数据保留在本账号</view>
      <input class="input" v-model="bindPhone" placeholder="手机号" type="number" maxlength="11" />
      <input class="input" v-model="bindPassword" placeholder="设置密码" password />
      <input class="input" v-model="bindConfirm" placeholder="再次输入密码" password />
      <button class="btn" @tap="submitBind">确认绑定</button>
    </view>
    <!-- 已绑定：修改密码 -->
    <view class="card card-base" v-else>
      <view class="title">修改密码</view>
      <view class="desc">已绑定手机号 {{ userPhoneMask }} 的账号可在此修改密码</view>
      <input class="input" v-model="oldPassword" placeholder="原密码" password />
      <input class="input" v-model="newPassword" placeholder="新密码" password />
      <input class="input" v-model="confirmPassword" placeholder="再次输入新密码" password />
      <button class="btn" @tap="submitPassword">确认修改</button>
    </view>
    <view class="link-row">
      <text class="link" @tap="goProfile">编辑资料（昵称、头像）</text>
    </view>
  </view>
</template>

<script>
import api from '@/utils/api.js'
export default {
  data() {
    return {
      hasPhone: false,
      userPhoneMask: '',
      bindPhone: '',
      bindPassword: '',
      bindConfirm: '',
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    }
  },
  onShow() {
    api.user.me().then(res => {
      const phone = res && res.phone
      this.hasPhone = !!(phone && phone.length >= 4)
      this.userPhoneMask = phone || ''
    }).catch(() => { this.hasPhone = false })
  },
  methods: {
    submitBind() {
      const phone = (this.bindPhone || '').trim()
      const pwd = (this.bindPassword || '').trim()
      const confirm = (this.bindConfirm || '').trim()
      if (!phone || !pwd) {
        uni.showToast({ title: '请填写手机号和密码', icon: 'none' })
        return
      }
      if (phone.length < 11) {
        uni.showToast({ title: '请输入正确手机号', icon: 'none' })
        return
      }
      if (pwd.length < 6) {
        uni.showToast({ title: '密码至少 6 位', icon: 'none' })
        return
      }
      if (pwd !== confirm) {
        uni.showToast({ title: '两次输入的密码不一致', icon: 'none' })
        return
      }
      uni.showLoading({ title: '绑定中...' })
      api.user.bindPhone({ phone, password: pwd }).then(() => {
        uni.hideLoading()
        uni.showToast({ title: '绑定成功，可用手机号+密码登录', icon: 'none' })
        this.bindPhone = ''
        this.bindPassword = ''
        this.bindConfirm = ''
        this.hasPhone = true
        this.userPhoneMask = phone.substring(0, 3) + '****' + phone.substring(7)
      }).catch(e => {
        uni.hideLoading()
        uni.showToast({ title: e.message || '绑定失败', icon: 'none' })
      })
    },
    submitPassword() {
      const oldP = (this.oldPassword || '').trim()
      const newP = (this.newPassword || '').trim()
      const confirm = (this.confirmPassword || '').trim()
      if (!oldP) {
        uni.showToast({ title: '请填写原密码', icon: 'none' })
        return
      }
      if (!newP) {
        uni.showToast({ title: '请填写新密码', icon: 'none' })
        return
      }
      if (!confirm) {
        uni.showToast({ title: '请再次输入新密码', icon: 'none' })
        return
      }
      if (newP.length < 6) {
        uni.showToast({ title: '新密码至少 6 位', icon: 'none' })
        return
      }
      if (newP !== confirm) {
        uni.showToast({ title: '两次输入的新密码不一致', icon: 'none' })
        return
      }
      uni.showLoading({ title: '提交中...' })
      api.user.changePassword({ oldPassword: oldP, newPassword: newP }).then(() => {
        uni.hideLoading()
        uni.showToast({ title: '密码已修改', icon: 'none' })
        this.oldPassword = ''
        this.newPassword = ''
        this.confirmPassword = ''
      }).catch(e => {
        uni.hideLoading()
        uni.showToast({ title: e.message || '修改失败', icon: 'none' })
      })
    },
    goProfile() {
      uni.navigateTo({ url: '/pages/my/profile' })
    }
  }
}
</script>

<style scoped>
.page { padding: 24rpx; min-height: 100vh; background: #f5f5f0; }
.card { padding: 32rpx; border-radius: 20rpx; box-shadow: 0 4rpx 20rpx rgba(0,0,0,.06); background: #fff; }
.title { font-size: 32rpx; font-weight: 600; color: #1a1a1a; margin-bottom: 8rpx; }
.desc { font-size: 24rpx; color: #999; margin-bottom: 24rpx; }
.input { width: 100%; padding: 28rpx; border: 1rpx solid #eee; border-radius: 12rpx; margin-bottom: 20rpx; font-size: 28rpx; box-sizing: border-box; }
.btn { background: linear-gradient(135deg, #4a7c59, #2d5a3d); color: #fff; border: none; border-radius: 12rpx; padding: 28rpx; font-size: 30rpx; margin-top: 12rpx; }
.link-row { margin-top: 32rpx; text-align: center; }
.link { font-size: 28rpx; color: #2d5a3d; }
</style>
