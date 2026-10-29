<template>
  <view class="page">
    <view class="nav-row">
      <text class="nav-edit" @tap="goEdit">编辑</text>
    </view>
    <view class="account-list card-base">
      <view class="account-item current" v-if="user.id">
        <view class="avatar-wrap">
          <image v-if="user.avatar" class="avatar" :src="user.avatar" mode="aspectFill" />
          <view v-else class="avatar placeholder">{{ (user.nickname || '我').slice(0, 1) }}</view>
        </view>
        <view class="info">
          <view class="name">{{ user.nickname || '旅行者' }}</view>
          <view class="sub">{{ user.phone || '未绑定手机号' }}</view>
        </view>
        <view class="current-tag">当前</view>
      </view>
      <view class="account-item bind" v-if="user.id && !user.phone" @tap="goEdit">
        <view class="avatar-wrap bind-icon">📱</view>
        <view class="info">
          <view class="name">绑定手机号和密码</view>
          <view class="sub">游客账号绑定后，可用手机号+密码在其他设备登录，数据保留</view>
        </view>
        <text class="arrow">></text>
      </view>
      <view class="account-item add" @tap="goAddAccount">
        <view class="avatar-wrap add-icon">+</view>
        <view class="info">
          <view class="name">添加或注册账号</view>
          <view class="sub">使用手机号登录或注册新账号</view>
        </view>
        <text class="arrow">></text>
      </view>
    </view>

    <view class="logout-wrap">
      <view class="logout-btn" @tap="logout">
        <text class="logout-icon">⎋</text>
        <text class="logout-text">退出当前账号</text>
      </view>
    </view>

    <view class="loading-wrap" v-if="loading">加载中...</view>
  </view>
</template>

<script>
import api from '@/utils/api.js'
import request from '@/utils/request.js'
export default {
  data() {
    return { user: {}, loading: true }
  },
  onShow() {
    this.load()
  },
  onLoad() {
    uni.setNavigationBarTitle({ title: '账号管理' })
  },
  methods: {
    load() {
      this.loading = true
      api.user.me().then(res => {
        this.user = res || {}
      }).catch(() => {
        this.user = {}
      }).finally(() => { this.loading = false })
    },
    goAddAccount() {
      uni.navigateTo({ url: '/pages/auth/index' })
    },
    goEdit() {
      uni.navigateTo({ url: '/pages/my/account-edit' })
    },
    logout() {
      uni.showModal({
        title: '退出当前账号',
        content: '退出后需重新登录',
        success: (res) => {
          if (res.confirm) {
            api.auth.logout().catch(() => {})
            request.clearToken()
            uni.showToast({ title: '已退出', icon: 'none' })
            setTimeout(() => uni.navigateTo({ url: '/pages/auth/index' }), 300)
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.page { min-height: 100vh; background: #f5f5f0; padding: 24rpx; padding-bottom: 160rpx; }
.nav-row { display: flex; justify-content: flex-end; padding: 16rpx 0 8rpx; }
.nav-edit { font-size: 30rpx; color: #2d5a3d; padding: 12rpx 24rpx; }
.account-list { border-radius: 20rpx; overflow: hidden; box-shadow: 0 4rpx 20rpx rgba(0,0,0,.06); background: #fff; }
.account-item { display: flex; align-items: center; padding: 32rpx 24rpx; border-bottom: 1rpx solid #f0f0f0; }
.account-item:last-child { border-bottom: none; }
.avatar-wrap { width: 96rpx; height: 96rpx; margin-right: 24rpx; flex-shrink: 0; }
.avatar { width: 96rpx; height: 96rpx; border-radius: 48rpx; background: #f0f0f0; }
.avatar.placeholder { width: 96rpx; height: 96rpx; border-radius: 48rpx; background: linear-gradient(135deg, #4a7c59, #2d5a3d); color: #fff; font-size: 36rpx; font-weight: 600; display: flex; align-items: center; justify-content: center; }
.avatar-wrap.add-icon { border-radius: 48rpx; border: 2rpx dashed #ccc; color: #999; font-size: 48rpx; display: flex; align-items: center; justify-content: center; }
.info { flex: 1; min-width: 0; }
.name { font-size: 30rpx; font-weight: 600; color: #1a1a1a; margin-bottom: 6rpx; }
.sub { font-size: 24rpx; color: #999; }
.current-tag { font-size: 22rpx; color: #2d5a3d; background: rgba(45,90,61,.1); padding: 6rpx 16rpx; border-radius: 20rpx; margin-left: 12rpx; }
.account-item.add .name { color: #2d5a3d; }
.account-item.bind .name { color: #2d5a3d; }
.avatar-wrap.bind-icon { border-radius: 48rpx; background: rgba(45,90,61,.12); font-size: 40rpx; display: flex; align-items: center; justify-content: center; }
.arrow { font-size: 26rpx; color: #999; }
.logout-wrap { margin-top: 48rpx; padding: 0 24rpx; }
.logout-btn { display: flex; align-items: center; justify-content: center; padding: 28rpx; background: #fff; border-radius: 16rpx; box-shadow: 0 4rpx 16rpx rgba(0,0,0,.06); }
.logout-icon { font-size: 32rpx; margin-right: 12rpx; color: #666; }
.logout-text { font-size: 28rpx; color: #666; }
.loading-wrap { text-align: center; padding: 48rpx; color: #999; }
</style>
