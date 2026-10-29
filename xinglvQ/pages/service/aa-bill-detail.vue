<template>
  <view class="page page-wrap">
    <view class="header">
      <view class="back-btn" @tap="goBack">
        <text class="back-icon">←</text>
      </view>
      <view class="header-title">账单详情</view>
    </view>
    
    <view class="bill-info" v-if="bill">
      <view class="bill-name">{{ bill.name }}</view>
      <view class="bill-amount">
        <text class="label">总金额:</text>
        <text class="amount">¥{{ bill.totalAmount }}</text>
      </view>
      <view class="bill-details">
        <view class="detail-item">
          <text class="label">参与人数:</text>
          <text class="value">{{ bill.memberCount }}人</text>
        </view>
        <view class="detail-item">
          <text class="label">人均金额:</text>
          <text class="value">¥{{ bill.perPersonAmount }}</text>
        </view>
        <view class="detail-item">
          <text class="label">状态:</text>
          <text class="status" :class="bill.status === 1 ? 'settled' : ''">
            {{ bill.status === 0 ? '未结算' : '已结算' }}
          </text>
        </view>
      </view>
    </view>
    
    <view class="section">
      <view class="section-title">
        <text>消费明细</text>
        <button type="primary" @tap="addExpense" class="add-btn">添加消费</button>
      </view>
      <view class="expense-list" v-if="expenses.length > 0">
        <view class="expense-item" v-for="expense in expenses" :key="expense.id">
          <view class="expense-info">
            <view class="expense-name">{{ expense.expenseName }}</view>
            <view class="expense-meta">
              <text class="expense-category">{{ getCategoryName(expense.category) }}</text>
              <text class="expense-time">{{ formatTime(expense.createTime) }}</text>
            </view>
          </view>
          <view class="expense-amount">¥{{ expense.amount }}</view>
        </view>
      </view>
      <view v-else class="empty">
        <text class="empty-text">暂无消费明细</text>
        <text class="empty-desc">添加消费明细开始记账</text>
      </view>
    </view>
    
    <view class="section" v-if="bill && bill.status === 0">
      <view class="section-title">
        <text>账单操作</text>
      </view>
      <view class="action-list">
        <button type="primary" @tap="calculateBill" class="action-btn calculate-btn">
          计算账单
        </button>
        <button type="primary" @tap="generateAllocation" class="action-btn allocate-btn">
          生成账单分配
        </button>
      </view>
    </view>
    
    <view class="section" v-if="calculationResult">
      <view class="section-title">
        <text>计算结果</text>
      </view>
      <view class="calculation-result">
        <view class="result-item">
          <text class="label">总金额</text>
          <text class="value">¥{{ calculationResult.bill.totalAmount }}</text>
        </view>
        <view class="result-item">
          <text class="label">参与人数</text>
          <text class="value">{{ calculationResult.bill.memberCount }}人</text>
        </view>
        <view class="result-item total">
          <text class="label">人均金额</text>
          <text class="value highlight">¥{{ calculationResult.perPerson }}</text>
        </view>
        
        <view class="balance-section">
          <text class="balance-title">收支平衡</text>
          <view class="balance-item" v-for="(balance, userId) in calculationResult.balanceMap" :key="userId">
            <text class="member-name">{{ getMemberName(userId) }}</text>
            <text class="balance" :class="balance > 0 ? 'positive' : 'negative'">
              {{ balance > 0 ? '+' : '' }}{{ balance }}
            </text>
          </view>
        </view>
      </view>
    </view>
    
    <view class="section" v-if="allocationResult">
      <view class="section-title">
        <text>账单分配</text>
      </view>
      <view class="allocation-list">
        <view class="allocation-item" v-for="allocation in allocationResult.allocations" :key="allocation.id">
          <view class="allocation-info">
            <text class="member-name">{{ getMemberName(allocation.userId) }}</text>
            <text class="amount">¥{{ allocation.amount }}</text>
          </view>
          <view class="allocation-status" :class="allocation.status === 1 ? 'paid' : ''">
            {{ allocation.status === 0 ? '未支付' : '已支付' }}
          </view>
        </view>
      </view>
    </view>
    

  </view>
</template>

<script>
import api from '../../utils/api.js'

export default {
  data() {
    return {
      billId: '',
      bill: null,
      expenses: [],
      members: [],
      calculationResult: null,
      allocationResult: null,
      categories: ['餐饮', '交通', '住宿', '购物', '其他'],
      categoryMap: {
        '餐饮': 'food',
        '交通': 'transport',
        '住宿': 'accommodation',
        '购物': 'shopping',
        '其他': 'other'
      },
      addExpenseForm: {
        expenseName: '',
        amount: '',
        categoryIndex: 0,
        description: '',
        paymentMethod: ''
      }
    }
  },
  onLoad(options) {
    this.billId = options.id
    this.loadData()
  },
  methods: {
    goBack() {
      uni.navigateBack()
    },
    async loadData() {
      await Promise.all([
        this.loadBillDetail(),
        this.loadBillExpenses()
      ])
    },
    async loadBillDetail() {
      try {
        const res = await api.aa.getBillDetail(this.billId)
        this.bill = res
        // 加载队伍成员
        if (this.bill && this.bill.teamId) {
          const membersRes = await api.aa.getTeamMembers(this.bill.teamId)
          this.members = membersRes || []
        }
      } catch (error) {
        console.error('加载账单详情失败:', error)
      }
    },
    async loadBillExpenses() {
      try {
        const res = await api.aa.getBillExpenses(this.billId)
        this.expenses = res || []
      } catch (error) {
        console.error('加载账单消费明细失败:', error)
      }
    },
    addExpense() {
      uni.showModal({
        title: '添加消费',
        content: '请输入消费名称',
        editable: true,
        placeholderText: '输入消费名称',
        success: (res) => {
          if (res.confirm && res.content) {
            this.addExpenseForm.expenseName = res.content
            uni.showModal({
              title: '添加消费',
              content: '请输入金额',
              editable: true,
              placeholderText: '输入金额',
              success: (res2) => {
                if (res2.confirm && res2.content) {
                  this.addExpenseForm.amount = res2.content
                  
                  // 显示分类选择
                  uni.showActionSheet({
                    itemList: this.categories,
                    success: (res3) => {
                      this.addExpenseForm.categoryIndex = res3.tapIndex
                      
                      uni.showModal({
                        title: '添加消费',
                        content: '请输入支付方式',
                        editable: true,
                        placeholderText: '输入支付方式',
                        success: (res4) => {
                          if (res4.confirm) {
                            this.addExpenseForm.paymentMethod = res4.content || ''
                            
                            uni.showModal({
                              title: '添加消费',
                              content: '请输入描述',
                              editable: true,
                              placeholderText: '输入描述',
                              success: async (res5) => {
                                if (res5.confirm) {
                                  this.addExpenseForm.description = res5.content || ''
                                  
                                  if (!this.bill || !this.bill.teamId) {
                                    uni.showToast({ title: '账单信息错误', icon: 'none' })
                                    return
                                  }
                                  
                                  try {
                                    await api.aa.addExpense(
                                      this.billId,
                                      this.bill.teamId,
                                      this.addExpenseForm.expenseName,
                                      parseFloat(this.addExpenseForm.amount),
                                      this.categoryMap[this.categories[this.addExpenseForm.categoryIndex]],
                                      this.addExpenseForm.description,
                                      this.addExpenseForm.paymentMethod
                                    )
                                    uni.showToast({ title: '添加消费成功', icon: 'success' })
                                    this.loadData()
                                  } catch (error) {
                                    console.error('添加消费失败:', error)
                                    uni.showToast({ title: '添加消费失败，请重试', icon: 'none' })
                                  }
                                }
                              }
                            })
                          }
                        }
                      })
                    }
                  })
                }
              }
            })
          }
        }
      })
    },
    async submitAddExpense() {
      // 该方法已替换为使用uni.showModal
    },
    async calculateBill() {
      try {
        const res = await api.aa.calculateBill(this.billId)
        this.calculationResult = res
        uni.showToast({ title: '计算成功', icon: 'success' })
      } catch (error) {
        console.error('计算账单失败:', error)
        uni.showToast({ title: '计算失败，请重试', icon: 'none' })
      }
    },
    async generateAllocation() {
      try {
        const res = await api.aa.generateBillAllocation(this.billId)
        this.allocationResult = res
        this.bill = res.bill // 更新账单状态
        uni.showToast({ title: '生成分配成功', icon: 'success' })
      } catch (error) {
        console.error('生成账单分配失败:', error)
        uni.showToast({ title: '生成失败，请重试', icon: 'none' })
      }
    },
    getMemberName(userId) {
      const member = this.members.find(m => m.userId === userId)
      return member ? member.nickname : '未知用户'
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

/* 账单信息卡片 */
.bill-info { padding: 36rpx 24rpx; background: linear-gradient(135deg, #fff 0%, #fafafa 100%); margin: 20rpx 24rpx; border-radius: 20rpx; box-shadow: 0 4rpx 16rpx rgba(0,0,0,.06); }
.bill-name { font-size: 40rpx; font-weight: 700; color: #333; margin-bottom: 20rpx; }
.bill-amount { display: flex; align-items: center; margin-bottom: 24rpx; }
.bill-amount .label { font-size: 28rpx; color: #666; margin-right: 16rpx; }
.bill-amount .amount { font-size: 44rpx; font-weight: 700; color: #ff6b6b; text-shadow: 0 2rpx 4rpx rgba(255, 107, 107, 0.2); }
.bill-details { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20rpx; }
.detail-item { display: flex; flex-direction: column; align-items: center; background: #f5f5f5; padding: 20rpx 16rpx; border-radius: 12rpx; }
.detail-item .label { font-size: 22rpx; color: #999; margin-bottom: 8rpx; font-weight: 500; }
.detail-item .value { font-size: 28rpx; font-weight: 600; color: #333; }
.detail-item .status { padding: 6rpx 16rpx; border-radius: 16rpx; font-size: 22rpx; font-weight: 500; }
.detail-item .status.settled { background: linear-gradient(135deg, #f6ffed 0%, #d9f7be 100%); color: #52c41a; box-shadow: 0 2rpx 8rpx rgba(82, 196, 26, 0.1); }

/*  sections */
.section { background: #fff; margin: 0 24rpx 20rpx; border-radius: 20rpx; box-shadow: 0 4rpx 16rpx rgba(0,0,0,.06); padding: 28rpx; }
.section-title { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24rpx; }
.section-title text { font-size: 30rpx; font-weight: 600; color: #333; }
.add-btn { height: 68rpx; font-size: 24rpx; line-height: 68rpx; padding: 0 24rpx; background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%); color: #fff; border-radius: 12rpx; box-shadow: 0 2rpx 8rpx rgba(255, 107, 107, 0.2); transition: all 0.3s ease; }
.add-btn:active { transform: translateY(2rpx); box-shadow: 0 1rpx 4rpx rgba(255, 107, 107, 0.2); }

/* 消费明细 */
.expense-list { border-radius: 16rpx; overflow: hidden; }
.expense-item { padding: 24rpx; border-bottom: 1rpx solid #f5f5f5; display: flex; justify-content: space-between; align-items: center; transition: all 0.2s ease; }
.expense-item:last-child { border-bottom: none; }
.expense-item:active { background: rgba(0,0,0,0.02); }

.expense-info { flex: 1; margin-right: 20rpx; }
.expense-name { font-size: 28rpx; font-weight: 600; color: #333; margin-bottom: 12rpx; }
.expense-meta { display: flex; align-items: center; gap: 16rpx; flex-wrap: wrap; }
.expense-category { font-size: 22rpx; color: #666; background: #f5f5f5; padding: 4rpx 12rpx; border-radius: 8rpx; }
.expense-time { font-size: 20rpx; color: #999; }
.expense-amount { font-size: 32rpx; font-weight: 600; color: #ff6b6b; background: linear-gradient(135deg, #fff5f5 0%, #ffe6e6 100%); padding: 8rpx 16rpx; border-radius: 12rpx; box-shadow: 0 2rpx 8rpx rgba(255, 107, 107, 0.1); }

/* 空状态 */
.empty { padding: 60rpx 0; text-align: center; }
.empty-text { font-size: 26rpx; color: #666; margin-bottom: 12rpx; display: block; font-weight: 500; }
.empty-desc { font-size: 22rpx; color: #999; display: block; }

/* 操作栏 */
.action-list { display: flex; gap: 20rpx; }
.action-btn { flex: 1; height: 88rpx; font-size: 28rpx; line-height: 88rpx; padding: 0; border-radius: 16rpx; border: none; transition: all 0.3s ease; box-shadow: 0 4rpx 16rpx rgba(0,0,0,.1); }
.calculate-btn { background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%); color: #fff; }
.calculate-btn:active { transform: translateY(2rpx); box-shadow: 0 2rpx 8rpx rgba(24, 144, 255, 0.3); }
.allocate-btn { background: linear-gradient(135deg, #52c41a 0%, #389e0d 100%); color: #fff; }
.allocate-btn:active { transform: translateY(2rpx); box-shadow: 0 2rpx 8rpx rgba(82, 196, 26, 0.3); }

/* 计算结果 */
.calculation-result, .allocation-list { border-radius: 16rpx; overflow: hidden; background: #fafafa; padding: 24rpx; }

.result-item { display: flex; justify-content: space-between; align-items: center; padding: 20rpx 0; border-bottom: 1rpx solid #f0f0f0; }
.result-item:last-child { border-bottom: none; }
.result-item .label { font-size: 26rpx; color: #666; font-weight: 500; }
.result-item .value { font-size: 26rpx; font-weight: 600; color: #333; }
.result-item.total { background: linear-gradient(135deg, #fff5f5 0%, #ffe6e6 100%); padding: 20rpx; border-radius: 12rpx; margin-top: 12rpx; }
.result-item.total .value.highlight { color: #ff6b6b; font-size: 32rpx; font-weight: 700; }

.balance-section { margin-top: 28rpx; }
.balance-title { font-size: 26rpx; font-weight: 600; color: #333; margin-bottom: 20rpx; display: block; }
.balance-item { display: flex; justify-content: space-between; align-items: center; padding: 16rpx 0; border-bottom: 1rpx solid #f0f0f0; }
.balance-item:last-child { border-bottom: none; }
.balance-item .member-name { font-size: 24rpx; color: #333; font-weight: 500; }
.balance-item .balance { font-size: 24rpx; font-weight: 600; padding: 4rpx 12rpx; border-radius: 8rpx; }
.balance-item .balance.positive { color: #52c41a; background: linear-gradient(135deg, #f6ffed 0%, #d9f7be 100%); }
.balance-item .balance.negative { color: #ff6b6b; background: linear-gradient(135deg, #fff5f5 0%, #ffe6e6 100%); }

/* 账单分配 */
.allocation-item { display: flex; justify-content: space-between; align-items: center; padding: 24rpx 0; border-bottom: 1rpx solid #f0f0f0; }
.allocation-item:last-child { border-bottom: none; }
.allocation-info { flex: 1; }
.allocation-info .member-name { font-size: 26rpx; font-weight: 600; color: #333; margin-bottom: 8rpx; display: block; }
.allocation-info .amount { font-size: 24rpx; color: #666; font-weight: 500; }
.allocation-status { padding: 6rpx 20rpx; border-radius: 16rpx; font-size: 22rpx; font-weight: 500; }
.allocation-status.paid { background: linear-gradient(135deg, #f6ffed 0%, #d9f7be 100%); color: #52c41a; box-shadow: 0 2rpx 8rpx rgba(82, 196, 26, 0.1); }

/* 弹窗样式 */
.popup-content { background: linear-gradient(135deg, #fff 0%, #fafafa 100%); border-radius: 24rpx; padding: 40rpx; width: 90%; max-width: 550rpx; box-shadow: 0 8rpx 32rpx rgba(0,0,0,.15); margin: 0 auto; }
.popup-title { font-size: 36rpx; font-weight: 700; color: #333; text-align: center; margin-bottom: 40rpx; }
.popup-form { margin-bottom: 40rpx; }
.form-item { margin-bottom: 32rpx; text-align: center; }
.label { display: block; font-size: 28rpx; color: #333; margin-bottom: 12rpx; font-weight: 500; text-align: left; }
.input { width: 100%; height: 92rpx; border: 2rpx solid #f0f0f0; border-radius: 12rpx; padding: 0 20rpx; font-size: 28rpx; transition: all 0.3s ease; background: linear-gradient(135deg, #fff 0%, #fafafa 100%); text-align: center; }
.input:focus { border-color: #ff6b6b; box-shadow: 0 0 0 4rpx rgba(255, 107, 107, 0.1); }
.textarea { width: 100%; height: 140rpx; border: 2rpx solid #f0f0f0; border-radius: 12rpx; padding: 16rpx; font-size: 28rpx; resize: none; transition: all 0.3s ease; background: linear-gradient(135deg, #fff 0%, #fafafa 100%); text-align: center; }
.textarea:focus { border-color: #ff6b6b; box-shadow: 0 0 0 4rpx rgba(255, 107, 107, 0.1); }
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

.bill-info, .section { animation: fadeInUp 0.3s ease-out; }
.expense-item, .balance-item, .allocation-item { animation: fadeInUp 0.3s ease-out; }

/* 响应式调整 */
@media (max-width: 375px) {
  .bill-name { font-size: 36rpx; }
  .bill-amount .amount { font-size: 38rpx; }
  .section-title text { font-size: 28rpx; }
  .expense-name { font-size: 26rpx; }
  .result-item .label, .result-item .value { font-size: 24rpx; }
}
</style>
