<template>
  <div class="app-container">
    <!-- 公共筛选：月份 + 店铺 + 导出 -->
    <el-form :inline="true" size="small" @submit.native.prevent>
      <el-form-item label="月份">
        <el-date-picker v-model="month" type="month" value-format="yyyy-MM" :clearable="false" style="width: 130px" @change="loadAll" />
      </el-form-item>
      <el-form-item label="店铺">
        <el-select v-model="shopId" placeholder="全部店铺" clearable filterable style="width: 160px" @change="loadAll">
          <el-option v-for="s in shopList" :key="s.id" :label="s.name" :value="s.id" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-radio-group v-model="sortBy" size="mini" @change="sortRows">
          <el-radio-button label="default">默认</el-radio-button>
          <el-radio-button label="loss">亏损优先</el-radio-button>
        </el-radio-group>
      </el-form-item>
    </el-form>

    <completeness-card :month="month" />

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport">导出 Excel</el-button>
      </el-col>
    </el-row>

    <el-alert type="info" :closable="false" show-icon style="margin-bottom: 10px"
      title="口径：平台退款已在收入中扣除，工单只记额外损失；平台扣费优先取对账单，无账单时用订单自带扣费参考" />

    <el-table v-loading="loading" :data="rows" show-summary :summary-method="getSummary">
      <el-table-column label="订单号" align="center" prop="orderNum" min-width="150" show-overflow-tooltip />
      <el-table-column label="店铺" align="center" prop="shopName" width="120" />
      <el-table-column label="实收" align="center" prop="payAmount" width="100" />
      <el-table-column label="商品成本" align="center" prop="goodsCost" width="100" />
      <el-table-column label="快递" align="center" prop="expressFee" width="90" />
      <el-table-column label="平台扣费" align="center" width="130">
        <template slot-scope="scope">
          <span>{{ scope.row.platformFee }}</span>
          <el-tag v-if="scope.row.platformFeeSource === 'bill'" type="success" size="mini" style="margin-left: 4px">账单</el-tag>
          <el-tag v-else type="info" size="mini" style="margin-left: 4px">参考</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="售后成本" align="center" prop="afterSaleCost" width="95" />
      <el-table-column label="订单利润" align="center" width="110">
        <template slot-scope="scope">
          <span :style="profitStyle(scope.row.profit)" class="profit-cell">{{ scope.row.profit }}</span>
        </template>
      </el-table-column>
      <el-table-column label="利润率" align="center" width="90">
        <template slot-scope="scope">
          <span :style="profitStyle(scope.row.profit)">{{ scope.row.profitRate }}%</span>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { orderProfit, exportProfit } from '@/api/profit'
import { listShop } from '@/api/shop/shop'
import CompletenessCard from './components/CompletenessCard'

export default {
  name: 'ProfitOrder',
  components: { CompletenessCard },
  data() {
    const now = new Date()
    return {
      month: `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`,
      shopId: null,
      shopList: [],
      rows: [],
      loading: false,
      sortBy: 'default'
    }
  },
  created() {
    this.loadAll()
    listShop().then(res => { this.shopList = res.rows || res.data || [] }).catch(() => {})
  },
  methods: {
    loadAll() {
      this.loading = true
      orderProfit({ month: this.month, shopId: this.shopId }).then(res => {
        this.rows = res.data || []
        this.sortRows()
      }).finally(() => { this.loading = false })
    },
    sortRows() {
      if (this.sortBy === 'loss') {
        this.rows = [...this.rows].sort((a, b) => (a.profitRate || 0) - (b.profitRate || 0))
      } else {
        this.loadAll()
      }
    },
    profitStyle(v) {
      return { color: (v || 0) < 0 ? '#f56c6c' : '#67c23a', fontWeight: 600 }
    },
    getSummary({ columns, data }) {
      const sums = []
      const sumFields = ['payAmount', 'goodsCost', 'expressFee', 'platformFee', 'afterSaleCost', 'profit']
      columns.forEach((col, i) => {
        if (i === 0) {
          sums[i] = '合计'
          return
        }
        if (sumFields.indexOf(col.property) > -1) {
          const total = data.reduce((acc, row) => acc + (Number(row[col.property]) || 0), 0)
          sums[i] = total.toFixed(2)
        } else {
          sums[i] = ''
        }
      })
      return sums
    },
    handleExport() {
      exportProfit({ level: 'order', month: this.month, shopId: this.shopId })
    }
  }
}
</script>
