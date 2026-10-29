<template>
  <view class="page page-wrap">
    <view class="hero">
      <view class="title">学生优惠</view>
      <view class="desc">专属大学生的折扣信息，省钱出行</view>
    </view>
    
    <view class="section">
      <view class="section-title">热门优惠</view>
      <view class="discount-list" v-if="!loading">
        <view class="discount-card" v-for="(item, index) in hotDiscounts" :key="index">
          <view class="discount-icon">{{ item.icon }}</view>
          <view class="discount-info">
            <view class="discount-name">{{ item.name }}</view>
            <view class="discount-desc">{{ item.desc }}</view>
            <view class="discount-tips">{{ item.tips }}</view>
          </view>
          <view class="discount-tag">{{ item.tag }}</view>
        </view>
      </view>
      <view class="loading" v-else>
        <text>加载中...</text>
      </view>
    </view>
    
    <view class="section">
      <view class="section-title">分类优惠</view>
      <view class="category-list">
        <view class="category-card" @tap="goCategory('attractions')">
          <view class="category-icon">🎫</view>
          <view class="category-name">景点门票</view>
        </view>
        <view class="category-card" @tap="goCategory('transportation')">
          <view class="category-icon">🚗</view>
          <view class="category-name">交通出行</view>
        </view>
        <view class="category-card" @tap="goCategory('accommodation')">
          <view class="category-icon">🏨</view>
          <view class="category-name">住宿</view>
        </view>
        <view class="category-card" @tap="goCategory('food')">
          <view class="category-icon">🍜</view>
          <view class="category-name">餐饮</view>
        </view>
      </view>
    </view>
    
    <view class="section">
      <view class="section-title">优惠日历</view>
      <view class="calendar" v-if="!loading">
        <view class="calendar-item" v-for="(item, index) in calendar" :key="index">
          <view class="calendar-date">{{ item.date }}</view>
          <view class="calendar-event">{{ item.event }}</view>
        </view>
      </view>
      <view class="loading" v-else>
        <text>加载中...</text>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      hotDiscounts: [],
      calendar: [],
      loading: true
    }
  },
  onLoad() {
    this.loadData()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        // 获取热门优惠
        const hotRes = await uni.request({
          url: 'http://localhost:8080/travel/miniapp/discounts/hot',
          method: 'GET'
        })
        if (hotRes.statusCode === 200 && hotRes.data.code === 200) {
          this.hotDiscounts = hotRes.data.data
        }
        
        // 获取优惠日历
        const calendarRes = await uni.request({
          url: 'http://localhost:8080/travel/miniapp/discounts/calendar',
          method: 'GET'
        })
        if (calendarRes.statusCode === 200 && calendarRes.data.code === 200) {
          this.calendar = calendarRes.data.data
        }
      } catch (error) {
        console.error('加载数据失败:', error)
        uni.showToast({
          title: '加载失败，请重试',
          icon: 'none'
        })
      } finally {
        this.loading = false
      }
    },
    async goCategory(category) {
      try {
        const res = await uni.request({
          url: `http://localhost:8080/travel/miniapp/discounts/category?category=${category}`,
          method: 'GET'
        })
        if (res.statusCode === 200 && res.data.code === 200) {
          const discounts = res.data.data
          if (discounts.length > 0) {
            uni.showModal({
              title: '分类优惠',
              content: discounts.map(item => `${item.name}: ${item.desc}`).join('\n'),
              showCancel: false
            })
          } else {
            uni.showToast({
              title: '暂无该分类优惠',
              icon: 'none'
            })
          }
        }
      } catch (error) {
        console.error('获取分类优惠失败:', error)
        uni.showToast({
          title: '获取失败，请重试',
          icon: 'none'
        })
      }
    }
  }
}
</script>

<style scoped>
.page { padding: 24rpx; padding-bottom: 60rpx; background: #f5f5f0; min-height: 100vh; }
.hero { margin-bottom: 32rpx; }
.title { font-size: 40rpx; font-weight: 700; color: #1a1a1a; margin-bottom: 8rpx; }
.desc { font-size: 26rpx; color: #666; }

.section { margin-bottom: 32rpx; }
.section-title { font-size: 32rpx; font-weight: 600; color: #1a1a1a; margin-bottom: 16rpx; }

.discount-list { display: flex; flex-direction: column; gap: 16rpx; }
.discount-card { display: flex; align-items: center; background: #fff; border-radius: 16rpx; padding: 24rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,.04); }
.discount-icon { font-size: 48rpx; margin-right: 16rpx; }
.discount-info { flex: 1; }
.discount-name { font-size: 28rpx; font-weight: 600; color: #1a1a1a; margin-bottom: 4rpx; }
.discount-desc { font-size: 24rpx; color: #666; margin-bottom: 4rpx; }
.discount-tips { font-size: 20rpx; color: #999; }
.discount-tag { background: #ff6b6b; color: #fff; font-size: 20rpx; padding: 4rpx 12rpx; border-radius: 12rpx; }

.category-list { display: grid; grid-template-columns: 1fr 1fr; gap: 16rpx; }
.category-card { display: flex; flex-direction: column; align-items: center; background: #fff; border-radius: 16rpx; padding: 32rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,.04); }
.category-icon { font-size: 48rpx; margin-bottom: 12rpx; }
.category-name { font-size: 24rpx; color: #333; }

.calendar { display: flex; flex-direction: column; gap: 12rpx; }
.calendar-item { display: flex; justify-content: space-between; align-items: center; background: #fff; border-radius: 12rpx; padding: 20rpx; box-shadow: 0 2rpx 8rpx rgba(0,0,0,.04); }
.calendar-date { font-size: 24rpx; font-weight: 600; color: #333; }
.calendar-event { font-size: 22rpx; color: #666; }
</style>