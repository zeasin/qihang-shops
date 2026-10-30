<template>
  <div class="app-container">
    <el-form :inline="true" size="small" @submit.native.prevent>
      <el-form-item label="月份">
        <el-date-picker v-model="month" type="month" value-format="yyyy-MM" :clearable="false" style="width: 130px" @change="loadAll" />
      </el-form-item>
      <el-form-item label="店铺">
        <el-select v-model="shopId" placeholder="全部店铺" clearable filterable style="width: 160px" @change="loadAll">
          <el-option v-for="s in shopList" :key="s.id" :label="s.name" :value="s.id" />
        </el-select>
      </el-form-item>
    </el-form>

    <completeness-card :month="month" />

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport">导出 Excel</el-button>
      </el-col>
    </el-row>

    <el-alert type="info" :closable="false" show-icon style="margin-bottom: 10px"
      title="推广费为分摊后数字：链接级直归该链接，店铺级按各链接实收占比分摊" />

    <el-table v-loading="loading" :data="rows" show-summary :summary-method="getSummary">
      <el-table-column label="链接（商品）" align="left" prop="goodsName" min-width="180" show-overflow-tooltip />
      <el-table-column label="销量" align="center" prop="salesQty" width="80" />
      <el-table-column label="实收" align="center" prop="payAmount" width="100" />
      <el-table-column label="商品成本" align="center" prop="goodsCost" width="100" />
      <el-table-column label="快递" align="center" prop="expressFee" width="90" />
      <el-table-column label="平台扣费" align="center" prop="platformFee" width="100" />
      <el-table-column label="售后成本" align="center" prop="afterSaleCost" width="95" />
      <el-table-column label="分摊推广费" align="center" prop="promotionFee" width="105">
        <template slot-scope="scope">
          <span style="color: #e6a23c">{{ scope.row.promotionFee }}</span>
        </template>
      </el-table-column>
      <el-table-column label="链接贡献利润" align="center" width="115">
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
import { linkProfit, exportProfit } from '@/api/profit'
import { listShop } from '@/api/shop/shop'
import CompletenessCard from './components/CompletenessCard'

export default {
  name: 'ProfitLink',
  components: { CompletenessCard },
  data() {
    const now = new Date()
    return {
      month: `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`,
      shopId: null,
      shopList: [],
      rows: [],
      loading: false
    }
  },
  created() {
    this.loadAll()
    listShop().then(res => { this.shopList = res.rows || res.data || [] }).catch(() => {})
  },
  methods: {
    loadAll() {
      this.loading = true
      linkProfit({ month: this.month, shopId: this.shopId }).then(res => {
        this.rows = res.data || []
      }).finally(() => { this.loading = false })
    },
    profitStyle(v) {
      return { color: (v || 0) < 0 ? '#f56c6c' : '#67c23a', fontWeight: 600 }
    },
    getSummary({ columns, data }) {
      const sums = []
      const sumFields = ['payAmount', 'goodsCost', 'expressFee', 'platformFee', 'afterSaleCost', 'promotionFee', 'profit']
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
      exportProfit({ level: 'link', month: this.month, shopId: this.shopId })
    }
  }
}
</script>
