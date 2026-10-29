<template>
  <view class="page page-wrap">
    <view class="header">
      <view class="back-btn" @tap="goBack">
        <text class="back-icon">←</text>
      </view>
      <view class="header-title">队伍详情</view>
    </view>
    
    <!-- 加载状态 -->
    <view v-if="loading" class="loading-container">
      <view class="loading-spinner"></view>
      <text class="loading-text">加载中...</text>
    </view>
    
    <!-- 错误状态 -->
    <view v-else-if="error" class="error-container">
      <text class="error-text">{{ error }}</text>
      <button type="primary" @tap="loadData" class="retry-btn">重新加载</button>
    </view>
    
    <!-- 正常内容 -->
    <view v-else>
      <view class="team-info" v-if="team">
        <view class="team-name">{{ team.name }}</view>
        <view class="travel-name">{{ team.travelName }}</view>
        <view class="team-status" :class="team.status === 1 ? 'active' : team.status === 2 ? 'ended' : ''">
          {{ team.status === 0 ? '创建中' : team.status === 1 ? '旅行中' : '已结束' }}
        </view>
        <view class="join-code">
          <text class="label">加入码:</text>
          <text class="code">{{ team.joinCode }}</text>
          <button type="default" @tap="copyJoinCode" class="copy-btn">复制</button>
        </view>
      </view>
      
      <view class="section">
        <view class="section-title">
          <text>队伍成员</text>
          <text class="count">({{ members.length }}人)</text>
        </view>
        <view class="member-list">
          <view class="member-item" v-for="member in members" :key="member.id">
            <view class="member-avatar" v-if="member.avatar">
              <image :src="member.avatar" mode="aspectFill" />
            </view>
            <view class="member-avatar default" v-else>
              <text class="avatar-text">{{ getAvatarText(member.nickname) }}</text>
            </view>
            <view class="member-info">
              <view class="member-name">{{ member.nickname }}</view>
              <view class="member-role" v-if="member.role === 1">队长</view>
            </view>
          </view>
        </view>
      </view>
      
      <view class="section">
        <view class="section-title">
          <text>队伍账单</text>
          <button type="primary" @tap="createBill" class="create-btn">创建账单</button>
        </view>
        <view class="bill-list" v-if="bills.length > 0">
          <view class="bill-item" v-for="bill in bills" :key="bill.id" @tap="goToBillDetail(bill.id)">
            <view class="bill-info">
              <view class="bill-name">{{ bill.name }}</view>
              <view class="bill-amount">总金额: ¥{{ bill.totalAmount }}</view>
            </view>
            <view class="bill-status" :class="bill.status === 1 ? 'settled' : ''">
              {{ bill.status === 0 ? '未结算' : '已结算' }}
            </view>
          </view>
        </view>
        <view v-else class="empty">
          <text class="empty-text">暂无账单</text>
          <text class="empty-desc">创建一个账单开始记账</text>
        </view>
      </view>
      
      <view class="section">
        <view class="section-title">
          <text>最近消费</text>
        </view>
        <view class="expense-list" v-if="expenses.length > 0">
          <view class="expense-item" v-for="expense in expenses" :key="expense.id">
            <view class="expense-info">
              <view class="expense-name">{{ expense.expenseName }}</view>
              <view class="expense-category">{{ getCategoryName(expense.category) }}</view>
              <view class="expense-time">{{ formatTime(expense.createTime) }}</view>
            </view>
            <view class="expense-amount">¥{{ expense.amount }}</view>
          </view>
        </view>
        <view v-else class="empty">
          <text class="empty-text">暂无消费记录</text>
          <text class="empty-desc">添加消费记录开始记账</text>
        </view>
      </view>
      
      <view class="action-bar">
        <button type="default" @tap="updateTeamStatus" class="status-btn">
          {{ team && team.status === 1 ? '结束旅行' : '开始旅行' }}
        </button>
        <button type="default" @tap="leaveTeam" class="leave-btn">离开队伍</button>
      </view>
    </view>
  </view>
</template>

<script>
import api from '../../utils/api.js'

export default {
  data() {
    return {
      teamId: '',
      team: null,
      members: [],
      bills: [],
      expenses: [],
      loading: true,
      error: '',
      createBillForm: {
        billName: ''
      }
    }
  },
  onLoad(options) {
    this.teamId = options.id
    this.loadData()
  },
  methods: {
    goBack() {
      uni.navigateBack()
    },
    async loadData() {
      this.loading = true
      try {
        await Promise.all([
          this.loadTeamDetail(),
          this.loadTeamMembers(),
          this.loadTeamBills(),
          this.loadTeamExpenses()
        ])
        this.error = ''
      } catch (error) {
        console.error('加载数据失败:', error)
        this.error = '加载数据失败，请重试'
      } finally {
        this.loading = false
      }
    },
    async loadTeamDetail() {
      try {
        const res = await api.aa.getTeamDetail(this.teamId)
        this.team = res
      } catch (error) {
        console.error('加载队伍详情失败:', error)
      }
    },
    async loadTeamMembers() {
      try {
        const res = await api.aa.getTeamMembers(this.teamId)
        this.members = res || []
      } catch (error) {
        console.error('加载队伍成员失败:', error)
      }
    },
    async loadTeamBills() {
      try {
        const res = await api.aa.getTeamBills(this.teamId)
        this.bills = res || []
      } catch (error) {
        console.error('加载队伍账单失败:', error)
      }
    },
    async loadTeamExpenses() {
      try {
        const res = await api.aa.getTeamExpenses(this.teamId)
        this.expenses = res ? res.slice(0, 5) : [] // 只显示最近5条
      } catch (error) {
        console.error('加载队伍消费记录失败:', error)
      }
    },
    copyJoinCode() {
      if (this.team && this.team.joinCode) {
        uni.setClipboardData({
          data: this.team.joinCode,
          success() {
            uni.showToast({ title: '复制成功', icon: 'success' })
          }
        })
      }
    },
    createBill() {
      uni.showModal({
        title: '创建账单',
        content: '请输入账单名称',
        editable: true,
        placeholderText: '输入账单名称',
        success: async (res) => {
          if (res.confirm && res.content) {
            try {
              await api.aa.createBill(this.teamId, res.content)
              uni.showToast({ title: '创建账单成功', icon: 'success' })
              this.loadTeamBills()
            } catch (error) {
              console.error('创建账单失败:', error)
              uni.showToast({ title: '创建账单失败，请重试', icon: 'none' })
            }
          }
        }
      })
    },
    async submitCreateBill() {
      // 该方法已替换为使用uni.showModal
    },
    goToBillDetail(billId) {
      uni.navigateTo({
        url: `/pages/service/aa-bill-detail?id=${billId}`
      })
    },
    async updateTeamStatus() {
      if (!this.team) return
      
      const newStatus = this.team.status === 1 ? 2 : 1
      const statusText = newStatus === 1 ? '开始旅行' : '结束旅行'
      
      uni.showModal({
        title: '确认操作',
        content: `确定要${statusText}吗？`,
        success: async (res) => {
          if (res.confirm) {
            try {
              await api.aa.updateTeamStatus(this.teamId, newStatus)
              uni.showToast({ title: `${statusText}成功`, icon: 'success' })
              this.loadTeamDetail()
            } catch (error) {
              console.error('更新队伍状态失败:', error)
              uni.showToast({ title: '操作失败，请重试', icon: 'none' })
            }
          }
        }
      })
    },
    async leaveTeam() {
      uni.showModal({
        title: '确认操作',
        content: '确定要离开队伍吗？',
        success: async (res) => {
          if (res.confirm) {
            try {
              await api.aa.leaveTeam(this.teamId)
              uni.showToast({ title: '离开队伍成功', icon: 'success' })
              setTimeout(() => {
                uni.navigateBack()
              }, 1000)
            } catch (error) {
              console.error('离开队伍失败:', error)
              uni.showToast({ title: '操作失败，请重试', icon: 'none' })
            }
          }
        }
      })
    },
    getAvatarText(nickname) {
      if (!nickname) return 'U'
      return nickname.charAt(0).toUpperCase()
    },
    getCategoryName(category) {
      const categoryMap = {
        'food': '餐饮',
        'transport': '交通',
        'accommodation': '住宿',
        'shopping': '购物',
        'other': '其他'
      }
      return categoryMap[category] || category
    },
    formatTime(time) {
      if (!time) return ''
      const date = new Date(time)
      return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
    }
  }
}
</script>

<style scoped>
/* 全局样式 */
.page { padding: 0; background: linear-gradient(135deg, #f5f5f0 0%, #f0f0ea 100%); min-height: 100vh; }

/* 头部 */
.header { display: flex; align-items: center; height: 96rpx; background: #fff; padding: 0 24rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,.06); position: sticky; top: 0; z-index: 10; }
.back-btn { width: 64rpx; height: 64rpx; display: flex; align-items: center; justify-content: center; border-radius: 50%; transition: all 0.3s ease; }
.back-btn:active { background: rgba(0,0,0,0.05); }
.back-icon { font-size: 36rpx; color: #333; font-weight: 600; }
.header-title { flex: 1; font-size: 34rpx; font-weight: 600; color: #333; text-align: center; margin-right: 64rpx; }

/* 队伍信息卡片 */
.team-info { padding: 36rpx 24rpx; background: linear-gradient(135deg, #fff 0%, #fafafa 100%); margin: 20rpx 24rpx; border-radius: 20rpx; box-shadow: 0 4rpx 16rpx rgba(0,0,0,.06); }
.team-name { font-size: 40rpx; font-weight: 700; color: #333; margin-bottom: 12rpx; }
.travel-name { font-size: 28rpx; color: #666; margin-bottom: 20rpx; }
.team-status { display: inline-block; padding: 8rpx 24rpx; border-radius: 20rpx; font-size: 24rpx; font-weight: 500; margin-bottom: 20rpx; }
.team-status.active { background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%); color: #1890ff; box-shadow: 0 2rpx 8rpx rgba(24, 144, 255, 0.1); }
.team-status.ended { background: linear-gradient(135deg, #f6ffed 0%, #d9f7be 100%); color: #52c41a; box-shadow: 0 2rpx 8rpx rgba(82, 196, 26, 0.1); }
.join-code { display: flex; align-items: center; background: #f5f5f5; padding: 16rpx 20rpx; border-radius: 12rpx; }
.join-code .label { font-size: 26rpx; color: #666; margin-right: 16rpx; }
.join-code .code { font-size: 26rpx; font-weight: 600; color: #333; flex: 1; font-family: 'Courier New', monospace; }
.copy-btn { height: 60rpx; font-size: 22rpx; line-height: 60rpx; padding: 0 20rpx; background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%); color: #fff; border-radius: 10rpx; box-shadow: 0 2rpx 8rpx rgba(255, 107, 107, 0.2); transition: all 0.3s ease; }
.copy-btn:active { transform: translateY(2rpx); box-shadow: 0 1rpx 4rpx rgba(255, 107, 107, 0.2); }

/*  sections */
.section { background: #fff; margin: 0 24rpx 20rpx; border-radius: 20rpx; box-shadow: 0 4rpx 16rpx rgba(0,0,0,.06); padding: 28rpx; }
.section-title { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24rpx; }
.section-title text { font-size: 30rpx; font-weight: 600; color: #333; }
.section-title .count { font-size: 24rpx; font-weight: normal; color: #666; margin-left: 8rpx; }
.create-btn { height: 68rpx; font-size: 24rpx; line-height: 68rpx; padding: 0 24rpx; background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%); color: #fff; border-radius: 12rpx; box-shadow: 0 2rpx 8rpx rgba(255, 107, 107, 0.2); transition: all 0.3s ease; }
.create-btn:active { transform: translateY(2rpx); box-shadow: 0 1rpx 4rpx rgba(255, 107, 107, 0.2); }

/* 成员列表 */
.member-list { display: grid; grid-template-columns: repeat(4, 1fr); gap: 24rpx; }
.member-item { display: flex; flex-direction: column; align-items: center; transition: all 0.3s ease; }
.member-item:active { transform: scale(0.95); }
.member-avatar { width: 112rpx; height: 112rpx; border-radius: 50%; overflow: hidden; margin-bottom: 16rpx; box-shadow: 0 4rpx 12rpx rgba(0,0,0,.1); }
.member-avatar image { width: 100%; height: 100%; }
.member-avatar.default { background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%); display: flex; align-items: center; justify-content: center; }
.avatar-text { font-size: 44rpx; font-weight: 600; color: #fff; }
.member-info { display: flex; flex-direction: column; align-items: center; }
.member-name { font-size: 26rpx; color: #333; margin-bottom: 4rpx; text-align: center; font-weight: 500; }
.member-role { font-size: 22rpx; color: #ff6b6b; background: linear-gradient(135deg, #fff5f5 0%, #ffe6e6 100%); padding: 2rpx 12rpx; border-radius: 8rpx; }

/* 账单和消费列表 */
.bill-list, .expense-list { border-radius: 16rpx; overflow: hidden; }
.bill-item, .expense-item { padding: 24rpx; border-bottom: 1rpx solid #f5f5f5; display: flex; justify-content: space-between; align-items: center; transition: all 0.2s ease; }
.bill-item:last-child, .expense-item:last-child { border-bottom: none; }
.bill-item:active, .expense-item:active { background: rgba(0,0,0,0.02); }

.bill-info { flex: 1; margin-right: 20rpx; }
.bill-name { font-size: 28rpx; font-weight: 600; color: #333; margin-bottom: 12rpx; }
.bill-amount { font-size: 24rpx; color: #666; font-weight: 500; }
.bill-status { padding: 6rpx 20rpx; border-radius: 16rpx; font-size: 22rpx; font-weight: 500; }
.bill-status.settled { background: linear-gradient(135deg, #f6ffed 0%, #d9f7be 100%); color: #52c41a; box-shadow: 0 2rpx 8rpx rgba(82, 196, 26, 0.1); }

.expense-info { flex: 1; margin-right: 20rpx; }
.expense-name { font-size: 28rpx; font-weight: 600; color: #333; margin-bottom: 8rpx; }
.expense-category { font-size: 22rpx; color: #666; margin-bottom: 8rpx; background: #f5f5f5; padding: 4rpx 12rpx; border-radius: 8rpx; display: inline-block; }
.expense-time { font-size: 20rpx; color: #999; }
.expense-amount { font-size: 32rpx; font-weight: 600; color: #ff6b6b; background: linear-gradient(135deg, #fff5f5 0%, #ffe6e6 100%); padding: 8rpx 16rpx; border-radius: 12rpx; box-shadow: 0 2rpx 8rpx rgba(255, 107, 107, 0.1); }

/* 空状态 */
.empty { padding: 60rpx 0; text-align: center; }
.empty-text { font-size: 26rpx; color: #666; margin-bottom: 12rpx; display: block; font-weight: 500; }
.empty-desc { font-size: 22rpx; color: #999; display: block; }

/* 操作栏 */
.action-bar { padding: 24rpx; display: flex; gap: 20rpx; margin-bottom: 40rpx; }
.status-btn, .leave-btn { flex: 1; height: 88rpx; font-size: 28rpx; line-height: 88rpx; padding: 0; border-radius: 16rpx; border: none; transition: all 0.3s ease; }
.status-btn { background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%); color: #fff; box-shadow: 0 4rpx 16rpx rgba(24, 144, 255, 0.3); }
.status-btn:active { transform: translateY(2rpx); box-shadow: 0 2rpx 8rpx rgba(24, 144, 255, 0.3); }
.leave-btn { background: #f5f5f5; color: #666; box-shadow: 0 2rpx 8rpx rgba(0,0,0,.06); border: 1rpx solid #e5e5e5; }
.leave-btn:active { transform: translateY(2rpx); box-shadow: 0 1rpx 4rpx rgba(0,0,0,.06); }

/* 弹窗样式 */
.popup-content { background: linear-gradient(135deg, #fff 0%, #fafafa 100%); border-radius: 24rpx; padding: 40rpx; width: 85%; max-width: 500rpx; box-shadow: 0 8rpx 32rpx rgba(0,0,0,.15); margin: 0 auto; }
.popup-title { font-size: 36rpx; font-weight: 700; color: #333; text-align: center; margin-bottom: 40rpx; }
.popup-form { margin-bottom: 40rpx; }
.form-item { margin-bottom: 32rpx; text-align: center; }
.label { display: block; font-size: 28rpx; color: #333; margin-bottom: 12rpx; font-weight: 500; text-align: left; }
.input { width: 100%; height: 92rpx; border: 2rpx solid #f0f0f0; border-radius: 12rpx; padding: 0 20rpx; font-size: 28rpx; transition: all 0.3s ease; background: linear-gradient(135deg, #fff 0%, #fafafa 100%); text-align: center; }
.input:focus { border-color: #ff6b6b; box-shadow: 0 0 0 4rpx rgba(255, 107, 107, 0.1); }
.popup-actions { display: flex; gap: 20rpx; justify-content: center; }
.cancel-btn, .confirm-btn { flex: 1; height: 92rpx; font-size: 28rpx; line-height: 92rpx; padding: 0; border-radius: 16rpx; border: none; transition: all 0.3s ease; text-align: center; }
.confirm-btn { background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%); color: #fff; box-shadow: 0 4rpx 16rpx rgba(255, 107, 107, 0.3); }
.confirm-btn:active { transform: translateY(2rpx); box-shadow: 0 2rpx 8rpx rgba(255, 107, 107, 0.3); }
.cancel-btn { background: #f5f5f5; color: #666; box-shadow: 0 2rpx 8rpx rgba(0,0,0,.06); }
.cancel-btn:active { transform: translateY(2rpx); box-shadow: 0 1rpx 4rpx rgba(0,0,0,.06); }

/* 动画效果 */
@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20rpx); }
  to { opacity: 1; transform: translateY(0); }
}

.team-info, .section { animation: fadeInUp 0.3s ease-out; }
.member-item, .bill-item, .expense-item { animation: fadeInUp 0.3s ease-out; }

/* 加载状态 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  padding: 40rpx;
}

.loading-spinner {
  width: 80rpx;
  height: 80rpx;
  border: 8rpx solid #f3f3f3;
  border-top: 8rpx solid #ff6b6b;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 24rpx;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-text {
  font-size: 28rpx;
  color: #666;
  font-weight: 500;
}

/* 错误状态 */
.error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  padding: 40rpx;
  text-align: center;
}

.error-text {
  font-size: 28rpx;
  color: #ff6b6b;
  margin-bottom: 32rpx;
  line-height: 1.5;
}

.retry-btn {
  height: 88rpx;
  font-size: 28rpx;
  line-height: 88rpx;
  padding: 0 48rpx;
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%);
  color: #fff;
  border-radius: 16rpx;
  box-shadow: 0 4rpx 16rpx rgba(255, 107, 107, 0.3);
  transition: all 0.3s ease;
}

.retry-btn:active {
  transform: translateY(2rpx);
  box-shadow: 0 2rpx 8rpx rgba(255, 107, 107, 0.3);
}

/* 响应式调整 */
@media (max-width: 375px) {
  .team-name { font-size: 36rpx; }
  .travel-name { font-size: 26rpx; }
  .section-title text { font-size: 28rpx; }
  .member-avatar { width: 100rpx; height: 100rpx; }
  .avatar-text { font-size: 40rpx; }
  .loading-spinner { width: 60rpx; height: 60rpx; border-width: 6rpx; }
  .loading-text, .error-text { font-size: 26rpx; }
  .retry-btn { height: 76rpx; font-size: 26rpx; line-height: 76rpx; }
}
</style>
