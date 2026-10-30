<template>
  <el-card v-loading="loading" shadow="never" class="completeness-card">
    <div v-if="complete" class="complete-ok">
      <i class="el-icon-circle-check"></i>
      数据完整，本月利润数字可信
    </div>
    <template v-else>
      <div class="complete-title">
        <i class="el-icon-warning-outline"></i>
        数据完整度提示（{{ messages.length }} 项待处理，处理后利润数字才可信）
      </div>
      <div v-for="(m, i) in messages" :key="i" class="complete-item">
        <span class="msg">{{ m }}</span>
        <el-button v-if="targetOf(m)" type="text" size="mini" @click="go(targetOf(m))">去处理<i class="el-icon-arrow-right"></i></el-button>
      </div>
    </template>
  </el-card>
</template>

<script>
import { profitCompleteness } from '@/api/profit'

export default {
  name: 'CompletenessCard',
  props: {
    month: { type: String, required: true }
  },
  data() {
    return {
      loading: false,
      messages: [],
      complete: false
    }
  },
  watch: {
    month() {
      this.load()
    }
  },
  created() {
    this.load()
  },
  methods: {
    load() {
      this.loading = true
      profitCompleteness({ month: this.month }).then(res => {
        const d = res.data || {}
        this.messages = d.messages || []
        this.complete = !!d.complete
        this.$emit('loaded', d)
      }).finally(() => { this.loading = false })
    },
    // 按提示内容映射处理入口（只映射本系统已确定的租户侧路由）
    targetOf(msg) {
      if (msg.indexOf('工单') > -1) return '/workOrder/index'
      if (msg.indexOf('商品成本') > -1) return '/goodsCost/profile'
      if (msg.indexOf('推广费') > -1) return '/fee/promotion'
      if (msg.indexOf('固定费用') > -1) return '/fee/fixed'
      if (msg.indexOf('账单') > -1) return '/fee/bill'
      if (msg.indexOf('订单数据') > -1) return '/order/import'
      return null
    },
    go(path) {
      this.$router.push(path).catch(() => {})
    }
  }
}
</script>

<style scoped>
.completeness-card {
  margin-bottom: 14px;
}
.complete-ok {
  color: #67c23a;
  font-size: 14px;
  font-weight: 600;
}
.complete-title {
  color: #e6a23c;
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 8px;
}
.complete-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 4px 0 4px 22px;
  color: #606266;
  font-size: 13px;
  border-top: 1px dashed #ebeef5;
}
.complete-item .msg {
  flex: 1;
}
</style>
