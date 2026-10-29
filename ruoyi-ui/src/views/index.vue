<template>
  <div class="dashboard-container">
    <div class="dashboard-header">
      <div class="header-content">
        <h1 class="dashboard-title">星旅管理系统</h1>
        <p class="dashboard-subtitle">欢迎使用星旅旅行团队AA记账管理平台</p>
      </div>
      <div class="header-time">
        <span class="time-label">当前时间</span>
        <span class="time-value">{{ currentTime }}</span>
      </div>
    </div>

    <el-row :gutter="20" class="stats-row">
      <el-col :xs="12" :sm="12" :lg="6">
        <div class="stat-card stat-card-1">
          <div class="stat-icon">
            <svg-icon icon-class="peoples" />
          </div>
          <div class="stat-info">
            <span class="stat-label">旅行队伍</span>
            <span class="stat-value">{{ statsData.teams }}</span>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :lg="6">
        <div class="stat-card stat-card-2">
          <div class="stat-icon">
            <svg-icon icon-class="user" />
          </div>
          <div class="stat-info">
            <span class="stat-label">参与成员</span>
            <span class="stat-value">{{ statsData.members }}</span>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :lg="6">
        <div class="stat-card stat-card-3">
          <div class="stat-icon">
            <svg-icon icon-class="money" />
          </div>
          <div class="stat-info">
            <span class="stat-label">总消费金额</span>
            <span class="stat-value">¥{{ statsData.totalAmount }}</span>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :lg="6">
        <div class="stat-card stat-card-4">
          <div class="stat-icon">
            <svg-icon icon-class="log" />
          </div>
          <div class="stat-info">
            <span class="stat-label">进行中旅行</span>
            <span class="stat-value">{{ statsData.activeTravels }}</span>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :lg="16">
        <div class="chart-card">
          <div class="chart-header">
            <h3 class="chart-title">消费趋势</h3>
            <div class="chart-actions">
              <el-radio-group v-model="expenseType" size="small" @change="handleExpenseTypeChange">
                <el-radio-button label="week">本周</el-radio-button>
                <el-radio-button label="month">本月</el-radio-button>
                <el-radio-button label="year">本年</el-radio-button>
              </el-radio-group>
            </div>
          </div>
          <div class="chart-body">
            <line-chart :chart-data="lineChartData" height="320px" />
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :lg="8">
        <div class="chart-card">
          <div class="chart-header">
            <h3 class="chart-title">消费分类</h3>
          </div>
          <div class="chart-body">
            <pie-chart height="320px" />
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="bottom-row">
      <el-col :xs="24" :lg="12">
        <div class="list-card">
          <div class="list-header">
            <h3 class="list-title">最新旅行队伍</h3>
            <el-button type="primary" size="mini" icon="el-icon-plus" @click="goToAaManage">管理AA</el-button>
          </div>
          <div class="list-body">
            <el-table :data="recentTeams" style="width: 100%" :show-header="false">
              <el-table-column prop="teamName" label="队伍名称">
                <template slot-scope="scope">
                  <div class="team-item">
                    <span class="team-name">{{ scope.row.teamName }}</span>
                    <el-tag :type="scope.row.status === 'active' ? 'success' : 'info'" size="small">
                      {{ scope.row.status === 'active' ? '进行中' : '已结束' }}
                    </el-tag>
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="memberCount" label="成员" width="80" align="center">
                <template slot-scope="scope">
                  <span class="member-count"><i class="el-icon-user"></i> {{ scope.row.memberCount }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="totalAmount" label="金额" width="120" align="right">
                <template slot-scope="scope">
                  <span class="amount">¥{{ scope.row.totalAmount }}</span>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :lg="12">
        <div class="list-card">
          <div class="list-header">
            <h3 class="list-title">快捷操作</h3>
          </div>
          <div class="list-body">
            <div class="quick-actions">
              <div class="action-item" @click="goToAaManage">
                <div class="action-icon action-icon-1">
                  <svg-icon icon-class="money" />
                </div>
                <span class="action-text">AA记账</span>
              </div>
              <div class="action-item" @click="goToUserManage">
                <div class="action-icon action-icon-2">
                  <svg-icon icon-class="peoples" />
                </div>
                <span class="action-text">用户管理</span>
              </div>
              <div class="action-item" @click="goToTeamManage">
                <div class="action-icon action-icon-3">
                  <svg-icon icon-class="tree" />
                </div>
                <span class="action-text">队伍管理</span>
              </div>
              <div class="action-item" @click="goToBillManage">
                <div class="action-icon action-icon-4">
                  <svg-icon icon-class="log" />
                </div>
                <span class="action-text">账单管理</span>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import LineChart from './dashboard/LineChart'
import PieChart from './dashboard/PieChart'

export default {
  name: 'Dashboard',
  components: {
    LineChart,
    PieChart
  },
  data() {
    return {
      currentTime: '',
      expenseType: 'week',
      statsData: {
        teams: 12,
        members: 48,
        totalAmount: '28,960.00',
        activeTravels: 5
      },
      lineChartData: {
        expectedData: [820, 932, 901, 1234, 1290, 1330, 1520],
        actualData: [850, 945, 920, 1290, 1330, 1380, 1550]
      },
      recentTeams: [
        { teamName: '西藏自驾游', status: 'active', memberCount: 6, totalAmount: '5,680.00' },
        { teamName: '云南七日游', status: 'active', memberCount: 4, totalAmount: '8,250.00' },
        { teamName: '张家界徒步', status: 'ended', memberCount: 8, totalAmount: '3,420.00' },
        { teamName: '厦门鼓浪屿', status: 'ended', memberCount: 5, totalAmount: '4,890.00' }
      ]
    }
  },
  created() {
    this.updateTime()
    setInterval(() => {
      this.updateTime()
    }, 1000)
  },
  methods: {
    updateTime() {
      const now = new Date()
      this.currentTime = now.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      })
    },
    handleExpenseTypeChange(val) {
      if (val === 'week') {
        this.lineChartData = {
          expectedData: [820, 932, 901, 1234, 1290, 1330, 1520],
          actualData: [850, 945, 920, 1290, 1330, 1380, 1550]
        }
      } else if (val === 'month') {
        this.lineChartData = {
          expectedData: [3200, 4100, 3800, 5200, 4800, 5900, 6200],
          actualData: [3350, 4250, 3950, 5350, 5100, 6050, 6400]
        }
      } else {
        this.lineChartData = {
          expectedData: [28000, 35000, 42000, 55000, 48000, 62000, 75000],
          actualData: [29500, 36500, 43500, 57000, 51000, 64000, 78000]
        }
      }
    },
    goToAaManage() {
      this.$router.push('/travel/aa')
    },
    goToUserManage() {
      this.$router.push('/system/user')
    },
    goToTeamManage() {
      this.$router.push('/travel/aa')
    },
    goToBillManage() {
      this.$router.push('/travel/aa')
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard-container {
  padding: 24px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ed 100%);
  min-height: calc(100vh - 84px);
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding: 24px 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.25);
  animation: fadeInDown 0.6s ease-out;

  .header-content {
    .dashboard-title {
      font-size: 28px;
      font-weight: 600;
      color: #fff;
      margin: 0 0 8px 0;
    }

    .dashboard-subtitle {
      font-size: 14px;
      color: rgba(255, 255, 255, 0.85);
      margin: 0;
    }
  }

  .header-time {
    text-align: right;

    .time-label {
      display: block;
      font-size: 12px;
      color: rgba(255, 255, 255, 0.7);
      margin-bottom: 4px;
    }

    .time-value {
      font-size: 16px;
      font-weight: 500;
      color: #fff;
    }
  }
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 24px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
  animation: fadeInUp 0.6s ease-out;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  }

  .stat-icon {
    width: 60px;
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 12px;
    margin-right: 20px;

    .svg-icon {
      font-size: 28px;
      color: #fff;
    }
  }

  .stat-info {
    display: flex;
    flex-direction: column;

    .stat-label {
      font-size: 13px;
      color: #909399;
      margin-bottom: 6px;
    }

    .stat-value {
      font-size: 24px;
      font-weight: 600;
      color: #303133;
    }
  }
}

.stat-card-1 {
  .stat-icon {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  }
}

.stat-card-2 {
  .stat-icon {
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  }
}

.stat-card-3 {
  .stat-icon {
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  }
}

.stat-card-4 {
  .stat-icon {
    background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  }
}

.chart-row {
  margin-bottom: 20px;
}

.chart-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  animation: fadeInUp 0.6s ease-out;
  overflow: hidden;

  .chart-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px 24px;
    border-bottom: 1px solid #f0f0f0;

    .chart-title {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      margin: 0;
    }
  }

  .chart-body {
    padding: 20px 24px;
  }
}

.bottom-row {
  margin-bottom: 20px;
}

.list-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  animation: fadeInUp 0.6s ease-out;
  overflow: hidden;

  .list-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px 24px;
    border-bottom: 1px solid #f0f0f0;

    .list-title {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      margin: 0;
    }
  }

  .list-body {
    padding: 16px 24px;
  }
}

.team-item {
  display: flex;
  align-items: center;
  gap: 12px;

  .team-name {
    font-weight: 500;
    color: #303133;
  }
}

.member-count {
  color: #909399;
  font-size: 14px;

  i {
    margin-right: 4px;
  }
}

.amount {
  font-weight: 600;
  color: #f56c6c;
  font-size: 14px;
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 24px 16px;
  background: #f8f9fa;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    background: #f0f2f5;
    transform: translateY(-4px);

    .action-icon-1 {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    }

    .action-icon-2 {
      background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
    }

    .action-icon-3 {
      background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    }

    .action-icon-4 {
      background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
    }
  }

  .action-icon {
    width: 56px;
    height: 56px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 14px;
    margin-bottom: 12px;
    transition: all 0.3s ease;

    .svg-icon {
      font-size: 26px;
      color: #fff;
    }
  }

  .action-icon-1 {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  }

  .action-icon-2 {
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  }

  .action-icon-3 {
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  }

  .action-icon-4 {
    background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  }

  .action-text {
    font-size: 14px;
    color: #606266;
    font-weight: 500;
  }
}

@keyframes fadeInDown {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 768px) {
  .dashboard-header {
    flex-direction: column;
    text-align: center;
    padding: 20px;

    .header-time {
      margin-top: 16px;
      text-align: center;
    }
  }

  .quick-actions {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
