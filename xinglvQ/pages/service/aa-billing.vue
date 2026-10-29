<template>
  <view class="page page-wrap">
    <view class="hero">
      <view class="title">AA记账</view>
      <view class="desc">旅行算账好帮手，轻松分账单</view>
    </view>
    
    <view class="tab-bar">
      <view class="tab-item" :class="{ active: activeTab === 'teams' }" @tap="switchTab('teams')">
        我的队伍
      </view>
      <view class="tab-item" :class="{ active: activeTab === 'bills' }" @tap="switchTab('bills')">
        我的账单
      </view>
      <view class="tab-item" :class="{ active: activeTab === 'expenses' }" @tap="switchTab('expenses')">
        消费记录
      </view>
    </view>
    
    <!-- 队伍管理 -->
    <view v-if="activeTab === 'teams'" class="tab-content">
      <view class="action-bar">
        <button type="primary" @tap="createTeam" class="create-btn">创建队伍</button>
        <button type="default" @tap="joinTeam" class="join-btn">加入队伍</button>
      </view>
      
      <view class="team-list" v-if="teams.length > 0">
        <view class="team-item" v-for="team in teams" :key="team.id" @tap="goToTeamDetail(team.id)">
          <view class="team-info">
            <view class="team-name">{{ team.name }}</view>
            <view class="travel-name">{{ team.travelName }}</view>
            <view class="team-status" :class="team.status === 1 ? 'active' : team.status === 2 ? 'ended' : ''">
              {{ team.status === 0 ? '创建中' : team.status === 1 ? '旅行中' : '已结束' }}
            </view>
          </view>
          <view class="team-actions">
            <text class="join-code">加入码: {{ team.joinCode }}</text>
          </view>
        </view>
      </view>
      
      <view v-else class="empty">
        <image src="/static/empty.png" class="empty-img" />
        <text class="empty-text">暂无队伍</text>
        <text class="empty-desc">创建或加入一个队伍开始AA记账</text>
      </view>
    </view>
    
    <!-- 账单管理 -->
    <view v-if="activeTab === 'bills'" class="tab-content">
      <view class="action-bar">
        <button type="primary" @tap="createBill" class="create-btn">创建账单</button>
      </view>
      
      <view class="bill-list" v-if="bills.length > 0">
        <view class="bill-item" v-for="bill in bills" :key="bill.id" @tap="goToBillDetail(bill.id)">
          <view class="bill-info">
            <view class="bill-name">{{ bill.name }}</view>
            <view class="bill-amount">总金额: ¥{{ bill.totalAmount }}</view>
            <view class="bill-status" :class="bill.status === 1 ? 'settled' : ''">
              {{ bill.status === 0 ? '未结算' : '已结算' }}
            </view>
          </view>
          <view class="bill-details">
            <text class="per-person">人均: ¥{{ bill.perPersonAmount }}</text>
            <text class="member-count">{{ bill.memberCount }}人</text>
          </view>
        </view>
      </view>
      
      <view v-else class="empty">
        <image src="/static/empty.png" class="empty-img" />
        <text class="empty-text">暂无账单</text>
        <text class="empty-desc">创建一个账单开始记账</text>
      </view>
    </view>
    
    <!-- 消费记录 -->
    <view v-if="activeTab === 'expenses'" class="tab-content">
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
        <image src="/static/empty.png" class="empty-img" />
        <text class="empty-text">暂无消费记录</text>
        <text class="empty-desc">添加消费记录开始记账</text>
      </view>
    </view>
    

  </view>
</template>

<script>
import api from '../../utils/api.js'

export default {
  data() {
    return {
      activeTab: 'teams',
      teams: [],
      bills: [],
      expenses: [],
      createTeamForm: {
        teamName: '',
        travelName: ''
      },
      joinTeamForm: {
        joinCode: ''
      },
      createBillForm: {
        teamIndex: 0,
        billName: ''
      }
    }
  },
  onLoad() {
    this.loadData()
  },
  methods: {
    switchTab(tab) {
      this.activeTab = tab
      if (tab === 'teams') {
        this.loadTeams()
      } else if (tab === 'bills') {
        this.loadBills()
      } else if (tab === 'expenses') {
        this.loadExpenses()
      }
    },
    async loadData() {
      await this.loadTeams()
    },
    async loadTeams() {
      try {
        const res = await api.aa.myTeams()
        this.teams = res || []
      } catch (error) {
        console.error('加载队伍失败:', error)
      }
    },
    async loadBills() {
      try {
        // 加载所有队伍的账单
        let allBills = []
        for (const team of this.teams) {
          const res = await api.aa.getTeamBills(team.id)
          if (res && res.length > 0) {
            allBills = allBills.concat(res)
          }
        }
        this.bills = allBills
      } catch (error) {
        console.error('加载账单失败:', error)
      }
    },
    async loadExpenses() {
      try {
        // 加载所有队伍的消费记录
        let allExpenses = []
        for (const team of this.teams) {
          const res = await api.aa.getTeamExpenses(team.id)
          if (res && res.length > 0) {
            allExpenses = allExpenses.concat(res)
          }
        }
        // 按时间倒序排序
        this.expenses = allExpenses.sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
      } catch (error) {
        console.error('加载消费记录失败:', error)
      }
    },
    createTeam() {
      uni.showModal({
        title: '创建队伍',
        content: '请输入队伍名称和旅行名称',
        editable: true,
        placeholderText: '队伍名称',
        success: (res) => {
          if (res.confirm && res.content) {
            this.createTeamForm.teamName = res.content
            uni.showModal({
              title: '创建队伍',
              content: '请输入旅行名称',
              editable: true,
              placeholderText: '旅行名称',
              success: async (res2) => {
                if (res2.confirm && res2.content) {
                  this.createTeamForm.travelName = res2.content
                  try {
                    await api.aa.createTeam(this.createTeamForm.teamName, this.createTeamForm.travelName)
                    uni.showToast({ title: '创建队伍成功', icon: 'success' })
                    this.loadTeams()
                  } catch (error) {
                    console.error('创建队伍失败:', error)
                    uni.showToast({ title: '创建队伍失败，请重试', icon: 'none' })
                  }
                }
              }
            })
          }
        }
      })
    },
    async submitCreateTeam() {
      // 该方法已替换为使用uni.showModal
    },
    joinTeam() {
      uni.showModal({
        title: '加入队伍',
        content: '请输入队伍加入码',
        editable: true,
        placeholderText: '输入加入码',
        success: async (res) => {
          if (res.confirm && res.content) {
            try {
              await api.aa.joinTeam(res.content)
              uni.showToast({ title: '加入队伍成功', icon: 'success' })
              this.loadTeams()
            } catch (error) {
              console.error('加入队伍失败:', error)
              uni.showToast({ title: '加入队伍失败，请重试', icon: 'none' })
            }
          }
        }
      })
    },
    async submitJoinTeam() {
      // 该方法已替换为使用uni.showModal
    },
    createBill() {
      if (this.teams.length === 0) {
        uni.showToast({ title: '请先创建或加入队伍', icon: 'none' })
        return
      }
      
      // 显示队伍选择
      const teamNames = this.teams.map(team => team.name)
      uni.showActionSheet({
        itemList: teamNames,
        success: (res) => {
          const selectedTeam = this.teams[res.tapIndex]
          if (selectedTeam) {
            uni.showModal({
              title: '创建账单',
              content: '请输入账单名称',
              editable: true,
              placeholderText: '输入账单名称',
              success: async (res2) => {
                if (res2.confirm && res2.content) {
                  try {
                    await api.aa.createBill(selectedTeam.id, res2.content)
                    uni.showToast({ title: '创建账单成功', icon: 'success' })
                    this.loadBills()
                  } catch (error) {
                    console.error('创建账单失败:', error)
                    uni.showToast({ title: '创建账单失败，请重试', icon: 'none' })
                  }
                }
              }
            })
          }
        }
      })
    },
    async submitCreateBill() {
      // 该方法已替换为使用uni.showModal
    },
    goToTeamDetail(teamId) {
      uni.navigateTo({
        url: `aa-team-detail?id=${teamId}`
      })
    },
    goToBillDetail(billId) {
      uni.navigateTo({
        url: `aa-bill-detail?id=${billId}`
      })
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

/* 顶部英雄区 */
.hero { padding: 40rpx 24rpx; background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%); color: #fff; border-radius: 0 0 32rpx 32rpx; box-shadow: 0 4rpx 20rpx rgba(255, 107, 107, 0.2); }
.title { font-size: 48rpx; font-weight: 700; margin-bottom: 12rpx; text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.1); }
.desc { font-size: 28rpx; opacity: 0.9; text-shadow: 0 1rpx 2rpx rgba(0, 0, 0, 0.1); }

/* 标签栏 */
.tab-bar { display: flex; background: #fff; margin: 20rpx 24rpx; border-radius: 20rpx; box-shadow: 0 2rpx 12rpx rgba(0,0,0,.06); overflow: hidden; }
.tab-item { flex: 1; height: 84rpx; line-height: 84rpx; text-align: center; font-size: 28rpx; color: #666; position: relative; transition: all 0.3s ease; }
.tab-item.active { color: #ff6b6b; font-weight: 600; background: rgba(255, 107, 107, 0.05); }
.tab-item.active::after { content: ''; position: absolute; bottom: 0; left: 50%; transform: translateX(-50%); width: 60rpx; height: 4rpx; background: linear-gradient(90deg, #ff6b6b 0%, #ee5a6f 100%); border-radius: 2rpx; }

/* 内容区域 */
.tab-content { padding: 0 24rpx 40rpx; }

/* 操作栏 */
.action-bar { display: flex; gap: 16rpx; margin-bottom: 24rpx; }
.create-btn, .join-btn { flex: 1; height: 88rpx; font-size: 28rpx; line-height: 88rpx; padding: 0; border-radius: 20rpx; border: none; transition: all 0.3s ease; }
.create-btn { background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%); color: #fff; box-shadow: 0 4rpx 16rpx rgba(255, 107, 107, 0.3); }
.create-btn:active { transform: translateY(2rpx); box-shadow: 0 2rpx 8rpx rgba(255, 107, 107, 0.3); }
.join-btn { background: #fff; color: #333; box-shadow: 0 2rpx 12rpx rgba(0,0,0,.06); border: 1rpx solid #e5e5e5; }
.join-btn:active { transform: translateY(2rpx); box-shadow: 0 1rpx 6rpx rgba(0,0,0,.06); }

/* 列表样式 */
.team-list, .bill-list, .expense-list { background: #fff; border-radius: 20rpx; overflow: hidden; box-shadow: 0 4rpx 16rpx rgba(0,0,0,.06); margin-bottom: 20rpx; }
.team-item, .bill-item, .expense-item { padding: 28rpx; border-bottom: 1rpx solid #f5f5f5; transition: all 0.2s ease; }
.team-item:last-child, .bill-item:last-child, .expense-item:last-child { border-bottom: none; }
.team-item:active, .bill-item:active, .expense-item:active { background: rgba(0,0,0,0.02); }

/* 队伍项 */
.team-info { margin-bottom: 20rpx; }
.team-name { font-size: 34rpx; font-weight: 600; color: #333; margin-bottom: 12rpx; }
.travel-name { font-size: 26rpx; color: #666; margin-bottom: 16rpx; }
.team-status { display: inline-block; padding: 6rpx 20rpx; border-radius: 16rpx; font-size: 22rpx; font-weight: 500; }
.team-status.active { background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%); color: #1890ff; box-shadow: 0 2rpx 8rpx rgba(24, 144, 255, 0.1); }
.team-status.ended { background: linear-gradient(135deg, #f6ffed 0%, #d9f7be 100%); color: #52c41a; box-shadow: 0 2rpx 8rpx rgba(82, 196, 26, 0.1); }

.team-actions { display: flex; justify-content: space-between; align-items: center; }
.join-code { font-size: 24rpx; color: #999; background: #f5f5f5; padding: 8rpx 16rpx; border-radius: 12rpx; }

/* 账单项 */
.bill-info { margin-bottom: 20rpx; }
.bill-name { font-size: 34rpx; font-weight: 600; color: #333; margin-bottom: 12rpx; }
.bill-amount { font-size: 28rpx; color: #ff6b6b; font-weight: 600; margin-bottom: 16rpx; }
.bill-status { display: inline-block; padding: 6rpx 20rpx; border-radius: 16rpx; font-size: 22rpx; font-weight: 500; }
.bill-status.settled { background: linear-gradient(135deg, #f6ffed 0%, #d9f7be 100%); color: #52c41a; box-shadow: 0 2rpx 8rpx rgba(82, 196, 26, 0.1); }

.bill-details { display: flex; justify-content: space-between; align-items: center; }
.per-person { font-size: 24rpx; color: #666; font-weight: 500; }
.member-count { font-size: 24rpx; color: #999; background: #f5f5f5; padding: 6rpx 12rpx; border-radius: 10rpx; }

/* 消费项 */
.expense-item { display: flex; justify-content: space-between; align-items: flex-start; }
.expense-info { flex: 1; margin-right: 20rpx; }
.expense-name { font-size: 30rpx; font-weight: 600; color: #333; margin-bottom: 12rpx; }
.expense-category { font-size: 24rpx; color: #666; margin-bottom: 8rpx; background: #f5f5f5; padding: 4rpx 12rpx; border-radius: 8rpx; display: inline-block; }
.expense-time { font-size: 22rpx; color: #999; }
.expense-amount { font-size: 34rpx; font-weight: 600; color: #ff6b6b; background: linear-gradient(135deg, #fff5f5 0%, #ffe6e6 100%); padding: 8rpx 16rpx; border-radius: 12rpx; box-shadow: 0 2rpx 8rpx rgba(255, 107, 107, 0.1); }

/* 空状态 */
.empty { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 160rpx 0; background: #fff; border-radius: 20rpx; box-shadow: 0 4rpx 16rpx rgba(0,0,0,.06); margin: 20rpx 0; }
.empty-img { width: 240rpx; height: 240rpx; margin-bottom: 32rpx; opacity: 0.6; }
.empty-text { font-size: 32rpx; color: #666; margin-bottom: 12rpx; font-weight: 500; }
.empty-desc { font-size: 26rpx; color: #999; text-align: center; padding: 0 40rpx; }

/* 弹窗样式 */
.popup-content { background: linear-gradient(135deg, #fff 0%, #fafafa 100%); border-radius: 24rpx; padding: 40rpx; width: 85%; max-width: 500rpx; box-shadow: 0 8rpx 32rpx rgba(0,0,0,.15); margin: 0 auto; }
.popup-title { font-size: 36rpx; font-weight: 700; color: #333; text-align: center; margin-bottom: 40rpx; }
.popup-form { margin-bottom: 40rpx; }
.form-item { margin-bottom: 32rpx; text-align: center; }
.label { display: block; font-size: 28rpx; color: #333; margin-bottom: 12rpx; font-weight: 500; text-align: left; }
.input { width: 100%; height: 92rpx; border: 2rpx solid #f0f0f0; border-radius: 12rpx; padding: 0 20rpx; font-size: 28rpx; transition: all 0.3s ease; background: linear-gradient(135deg, #fff 0%, #fafafa 100%); text-align: center; }
.input:focus { border-color: #ff6b6b; box-shadow: 0 0 0 4rpx rgba(255, 107, 107, 0.1); }
.picker { width: 100%; height: 92rpx; border: 2rpx solid #f0f0f0; border-radius: 12rpx; padding: 0 20rpx; display: flex; align-items: center; justify-content: center; transition: all 0.3s ease; background: linear-gradient(135deg, #fff 0%, #fafafa 100%); }
.picker:active { border-color: #ff6b6b; box-shadow: 0 0 0 4rpx rgba(255, 107, 107, 0.1); }
.picker-text { font-size: 28rpx; color: #333; text-align: center; }
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

.team-item, .bill-item, .expense-item { animation: fadeInUp 0.3s ease-out; }

/* 响应式调整 */
@media (max-width: 375px) {
  .title { font-size: 42rpx; }
  .desc { font-size: 24rpx; }
  .tab-item { font-size: 26rpx; }
  .team-name, .bill-name { font-size: 30rpx; }
  .expense-name { font-size: 28rpx; }
}
</style>
