<template>
  <div class="aa-management">
    <div class="page-header">
      <h2 class="page-title">AA记账管理</h2>
      <p class="page-subtitle">管理所有旅行团队的AA记账数据</p>
    </div>

    <el-tabs v-model="activeTab" class="aa-tabs" @tab-click="handleTabClick">
      <el-tab-pane label="队伍管理" name="teams">
        <div class="tab-content">
          <el-row :gutter="20" class="stats-row">
            <el-col :xs="12" :sm="6">
              <div class="stat-card stat-card-1">
                <div class="stat-value">{{ stats.teams }}</div>
                <div class="stat-label">总队伍数</div>
              </div>
            </el-col>
            <el-col :xs="12" :sm="6">
              <div class="stat-card stat-card-2">
                <div class="stat-value">{{ stats.members }}</div>
                <div class="stat-label">总成员数</div>
              </div>
            </el-col>
            <el-col :xs="12" :sm="6">
              <div class="stat-card stat-card-3">
                <div class="stat-value">¥{{ stats.totalAmount }}</div>
                <div class="stat-label">总消费金额</div>
              </div>
            </el-col>
            <el-col :xs="12" :sm="6">
              <div class="stat-card stat-card-4">
                <div class="stat-value">{{ stats.bills }}</div>
                <div class="stat-label">总账单数</div>
              </div>
            </el-col>
          </el-row>

          <el-card class="table-card">
            <div slot="header" class="card-header">
              <span>队伍列表</span>
              <el-button type="primary" size="small" icon="el-icon-refresh" @click="loadTeams">刷新</el-button>
            </div>
            <el-table :data="teams" v-loading="loading" stripe>
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="name" label="队伍名称" />
              <el-table-column prop="travelName" label="旅行名称" />
              <el-table-column prop="joinCode" label="加入码" width="120">
                <template slot-scope="scope">
                  <el-tag type="info">{{ scope.row.joinCode }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="status" label="状态" width="100">
                <template slot-scope="scope">
                  <el-tag :type="scope.row.status === 2 ? 'success' : scope.row.status === 1 ? 'warning' : 'info'">
                    {{ statusMap[scope.row.status] }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="creatorId" label="创建者ID" width="100" />
              <el-table-column label="操作" width="200" fixed="right">
                <template slot-scope="scope">
                  <el-button size="mini" type="text" @click="viewTeamDetail(scope.row)">详情</el-button>
                  <el-button size="mini" type="text" @click="viewTeamBills(scope.row)">账单</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </div>
      </el-tab-pane>

      <el-tab-pane label="账单管理" name="bills">
        <div class="tab-content">
          <el-card class="table-card">
            <div slot="header" class="card-header">
              <span>账单列表</span>
              <el-button type="primary" size="small" icon="el-icon-refresh" @click="loadBills">刷新</el-button>
            </div>
            <el-table :data="bills" v-loading="loading" stripe>
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="name" label="账单名称" />
              <el-table-column prop="teamId" label="队伍ID" width="100" />
              <el-table-column prop="totalAmount" label="总金额" width="120">
                <template slot-scope="scope">
                  <span class="amount">¥{{ scope.row.totalAmount }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="memberCount" label="成员数" width="80" />
              <el-table-column prop="perPersonAmount" label="人均" width="100">
                <template slot-scope="scope">
                  <span class="amount">¥{{ scope.row.perPersonAmount }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="status" label="状态" width="100">
                <template slot-scope="scope">
                  <el-tag :type="scope.row.status === 1 ? 'success' : 'warning'">
                    {{ scope.row.status === 1 ? '已结算' : '未结算' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="280" fixed="right">
                <template slot-scope="scope">
                  <el-button size="mini" type="text" @click="viewBillDetail(scope.row)">详情</el-button>
                  <el-button size="mini" type="text" @click="calculateBill(scope.row)">计算</el-button>
                  <el-button size="mini" type="text" @click="allocateBill(scope.row)">分配</el-button>
                  <el-button size="mini" type="text" @click="exportBill(scope.row)">导出</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </div>
      </el-tab-pane>

      <el-tab-pane label="消费记录" name="expenses">
        <div class="tab-content">
          <el-card class="table-card">
            <div slot="header" class="card-header">
              <span>消费记录</span>
              <el-button type="primary" size="small" icon="el-icon-refresh" @click="loadExpenses">刷新</el-button>
            </div>
            <el-table :data="expenses" v-loading="loading" stripe>
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="expenseName" label="消费名称" />
              <el-table-column prop="amount" label="金额" width="120">
                <template slot-scope="scope">
                  <span class="amount">¥{{ scope.row.amount }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="category" label="分类" width="100">
                <template slot-scope="scope">
                  <el-tag :type="categoryTypeMap[scope.row.category]">
                    {{ categoryMap[scope.row.category] }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="description" label="描述" show-overflow-tooltip />
              <el-table-column prop="paymentMethod" label="支付方式" width="100" />
              <el-table-column prop="billId" label="账单ID" width="100" />
              <el-table-column prop="teamId" label="队伍ID" width="100" />
            </el-table>
          </el-card>
        </div>
      </el-tab-pane>
    </el-tabs>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="800px" :close-on-click-modal="false">
      <div v-if="dialogType === 'teamDetail'" class="detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="队伍ID">{{ currentItem.id }}</el-descriptions-item>
          <el-descriptions-item label="队伍名称">{{ currentItem.name }}</el-descriptions-item>
          <el-descriptions-item label="旅行名称">{{ currentItem.travelName }}</el-descriptions-item>
          <el-descriptions-item label="加入码">
            <el-tag type="info">{{ currentItem.joinCode }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="currentItem.status === 2 ? 'success' : currentItem.status === 1 ? 'warning' : 'info'">
              {{ statusMap[currentItem.status] }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建者ID">{{ currentItem.creatorId }}</el-descriptions-item>
        </el-descriptions>

        <h4 class="section-title">成员列表</h4>
        <el-table :data="teamMembers" size="small">
          <el-table-column prop="userId" label="用户ID" width="100" />
          <el-table-column prop="nickname" label="昵称" />
          <el-table-column prop="role" label="角色" width="100">
            <template slot-scope="scope">
              <el-tag :type="scope.row.role === 1 ? 'danger' : 'primary'">
                {{ scope.row.role === 1 ? '队长' : '成员' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template slot-scope="scope">
              <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
                {{ scope.row.status === 1 ? '已加入' : '申请中' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div v-if="dialogType === 'billDetail'" class="detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="账单ID">{{ currentItem.id }}</el-descriptions-item>
          <el-descriptions-item label="账单名称">{{ currentItem.name }}</el-descriptions-item>
          <el-descriptions-item label="队伍ID">{{ currentItem.teamId }}</el-descriptions-item>
          <el-descriptions-item label="总金额">
            <span class="amount">¥{{ currentItem.totalAmount }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="成员数">{{ currentItem.memberCount }}</el-descriptions-item>
          <el-descriptions-item label="人均金额">
            <span class="amount">¥{{ currentItem.perPersonAmount }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="currentItem.status === 1 ? 'success' : 'warning'">
              {{ currentItem.status === 1 ? '已结算' : '未结算' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建者ID">{{ currentItem.creatorId }}</el-descriptions-item>
        </el-descriptions>

        <h4 class="section-title">消费明细</h4>
        <el-table :data="billExpenses" size="small">
          <el-table-column prop="expenseName" label="消费名称" />
          <el-table-column prop="amount" label="金额" width="100">
            <template slot-scope="scope">
              <span class="amount">¥{{ scope.row.amount }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="category" label="分类" width="100">
            <template slot-scope="scope">
              <el-tag :type="categoryTypeMap[scope.row.category]" size="small">
                {{ categoryMap[scope.row.category] }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="paymentMethod" label="支付方式" width="100" />
        </el-table>
      </div>

      <div v-if="dialogType === 'billCalculate'" class="detail-content">
        <el-alert v-if="calculationResult" type="success" :closable="false" show-icon>
          <h4>账单计算结果</h4>
          <p>总金额：<strong>¥{{ calculationResult.bill.totalAmount }}</strong></p>
          <p>人均金额：<strong>¥{{ calculationResult.perPerson }}</strong></p>
        </el-alert>

        <h4 class="section-title">支付情况</h4>
        <el-table :data="calcPayments" size="small">
          <el-table-column prop="userId" label="用户ID" width="100" />
          <el-table-column prop="paid" label="已支付" width="120">
            <template slot-scope="scope">
              <span class="amount">¥{{ scope.row.paid }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="balance" label="差额" width="120">
            <template slot-scope="scope">
              <span :class="scope.row.balance >= 0 ? 'positive' : 'negative'">
                {{ scope.row.balance >= 0 ? '+' : '' }}{{ scope.row.balance }}
              </span>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div v-if="dialogType === 'billAllocate'" class="detail-content">
        <el-alert v-if="allocationResult" type="success" :closable="false" show-icon>
          <h4>账单分配结果</h4>
          <p>人均应付：<strong>¥{{ allocationResult.perPerson }}</strong></p>
          <p>差异金额（由队长承担）：<strong>¥{{ allocationResult.difference }}</strong></p>
        </el-alert>

        <h4 class="section-title">分配明细</h4>
        <el-table :data="allocations" size="small">
          <el-table-column prop="userId" label="用户ID" width="100" />
          <el-table-column prop="amount" label="应付金额" width="120">
            <template slot-scope="scope">
              <span class="amount">¥{{ scope.row.amount }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="paidAmount" label="已付" width="120">
            <template slot-scope="scope">
              <span class="amount">¥{{ scope.row.paidAmount }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template slot-scope="scope">
              <el-tag :type="scope.row.status === 1 ? 'success' : 'warning'" size="small">
                {{ scope.row.status === 1 ? '已支付' : '未支付' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listTeams, getTeam, listBills, getBill, listExpenses, calculateBill, allocateBill, exportBill } from '@/api/travel/aa'

export default {
  name: 'AaManagement',
  data() {
    return {
      activeTab: 'teams',
      loading: false,
      stats: {
        teams: 0,
        members: 0,
        totalAmount: '0.00',
        bills: 0
      },
      teams: [],
      bills: [],
      expenses: [],
      teamMembers: [],
      billExpenses: [],
      calculationResult: null,
      calcPayments: [],
      allocationResult: null,
      allocations: [],
      dialogVisible: false,
      dialogType: '',
      dialogTitle: '',
      currentItem: {},
      statusMap: {
        0: '创建中',
        1: '旅行中',
        2: '已结束'
      },
      categoryMap: {
        food: '餐饮',
        transport: '交通',
        accommodation: '住宿',
        shopping: '购物',
        other: '其他'
      },
      categoryTypeMap: {
        food: 'danger',
        transport: 'warning',
        accommodation: 'success',
        shopping: 'primary',
        other: 'info'
      }
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    loadData() {
      this.loadTeams()
      this.loadBills()
      this.loadExpenses()
    },
    handleTabClick(tab) {
      // Tab切换时刷新数据
    },
    async loadTeams() {
      this.loading = true
      try {
        const res = await listTeams()
        this.teams = res.data || []
        this.stats.teams = this.teams.length
        let members = 0
        this.teams.forEach(team => {
          members += team.memberCount || 0
        })
        this.stats.members = members
      } catch (e) {
        console.error('加载队伍失败:', e)
      } finally {
        this.loading = false
      }
    },
    async loadBills() {
      this.loading = true
      try {
        const res = await listBills()
        this.bills = res.data || []
        this.stats.bills = this.bills.length
        let total = 0
        this.bills.forEach(bill => {
          total += parseFloat(bill.totalAmount || 0)
        })
        this.stats.totalAmount = total.toFixed(2)
      } catch (e) {
        console.error('加载账单失败:', e)
      } finally {
        this.loading = false
      }
    },
    async loadExpenses() {
      this.loading = true
      try {
        const res = await listExpenses()
        this.expenses = res.data || []
      } catch (e) {
        console.error('加载消费记录失败:', e)
      } finally {
        this.loading = false
      }
    },
    async viewTeamDetail(row) {
      this.currentItem = row
      this.dialogType = 'teamDetail'
      this.dialogTitle = '队伍详情'
      try {
        const res = await getTeam(row.id)
        if (res.data && res.data.members) {
          this.teamMembers = res.data.members
        }
      } catch (e) {
        console.error('加载队伍详情失败:', e)
      }
      this.dialogVisible = true
    },
    async viewTeamBills(row) {
      this.activeTab = 'bills'
      this.currentItem = row
      await this.loadBills()
    },
    async viewBillDetail(row) {
      this.currentItem = row
      this.dialogType = 'billDetail'
      this.dialogTitle = '账单详情'
      try {
        const res = await getBill(row.id)
        if (res.data && res.data.expenses) {
          this.billExpenses = res.data.expenses
        }
      } catch (e) {
        console.error('加载账单详情失败:', e)
      }
      this.dialogVisible = true
    },
    async calculateBill(row) {
      this.currentItem = row
      this.dialogType = 'billCalculate'
      this.dialogTitle = '计算账单'
      this.loading = true
      try {
        const res = await calculateBill(row.id)
        if (res.data) {
          this.calculationResult = res.data
          const paymentMap = res.data.paymentMap || {}
          const balanceMap = res.data.balanceMap || {}
          const members = res.data.members || []
          this.calcPayments = members.map(m => ({
            userId: m.userId,
            paid: paymentMap[m.userId] || 0,
            balance: balanceMap[m.userId] || 0
          }))
        }
      } catch (e) {
        console.error('计算账单失败:', e)
        this.$message.error('计算账单失败')
      } finally {
        this.loading = false
      }
      this.dialogVisible = true
    },
    async allocateBill(row) {
      this.currentItem = row
      this.dialogType = 'billAllocate'
      this.dialogTitle = '分配账单'
      this.loading = true
      try {
        const res = await allocateBill(row.id)
        if (res.data) {
          this.allocationResult = res.data
          this.allocations = res.data.allocations || []
          this.$message.success('账单分配成功')
        }
      } catch (e) {
        console.error('分配账单失败:', e)
        this.$message.error('分配账单失败')
      } finally {
        this.loading = false
      }
      this.dialogVisible = true
    },
    exportBill(row) {
      this.$message.info('导出功能开发中')
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.aa-management {
  padding: 20px;

  .page-header {
    margin-bottom: 20px;

    .page-title {
      font-size: 24px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 8px;
    }

    .page-subtitle {
      font-size: 14px;
      color: #909399;
    }
  }

  .aa-tabs {
    background: #fff;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  }

  .stats-row {
    margin-bottom: 20px;
  }

  .stat-card {
    padding: 20px;
    border-radius: 8px;
    color: #fff;
    text-align: center;

    &.stat-card-1 { background: linear-gradient(135deg, #667eea, #764ba2); }
    &.stat-card-2 { background: linear-gradient(135deg, #f093fb, #f5576c); }
    &.stat-card-3 { background: linear-gradient(135deg, #4facfe, #00f2fe); }
    &.stat-card-4 { background: linear-gradient(135deg, #43e97b, #38f9d7); }

    .stat-value {
      font-size: 28px;
      font-weight: 600;
      margin-bottom: 8px;
    }

    .stat-label {
      font-size: 14px;
      opacity: 0.9;
    }
  }

  .table-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-weight: 600;
    }
  }

  .detail-content {
    .section-title {
      margin: 20px 0 10px;
      font-size: 16px;
      color: #303133;
    }

    .amount {
      color: #f56c6c;
      font-weight: 600;
    }

    .positive {
      color: #67c23a;
    }

    .negative {
      color: #f56c6c;
    }
  }
}
</style>