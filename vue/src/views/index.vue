<template>
  <div class="workbench">
    <workbench-header
      :nickname="name"
      :range="range"
      :loading="anyLoading"
      @change-range="handleChangeRange"
      @refresh="refreshAll"
    />

    <!-- 销售统计：销售额/订单数/客单价 -->
    <el-row :gutter="16">
      <el-col v-for="card in kpiCards" :key="card.label" :xs="24" :sm="8" :lg="8">
        <stat-card
          :label="card.label"
          :value="card.value"
          :prefix="card.prefix"
          :decimals="card.decimals"
          :growth="card.growth"
          :icon="card.icon"
          :color="card.color"
          :loading="kpiLoading"
        />
      </el-col>
    </el-row>

    <todo-panel v-if="todoItems.length" :items="todoItems" :loading="todoLoading" />

    <quick-entry :entries="quickEntries" />
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { todayDaily, salesDaily } from '@/api/report/report'
import WorkbenchHeader from './dashboard/WorkbenchHeader'
import TodoPanel from './dashboard/TodoPanel'
import QuickEntry from './dashboard/QuickEntry'
import StatCard from './dashboard/StatCard'
import { TODO_CONFIG, QUICK_ENTRY_CONFIG, collectRoutePaths, pickReachablePath } from './dashboard/identity'

export default {
  name: 'Index',
  components: {
    WorkbenchHeader,
    TodoPanel,
    QuickEntry,
    StatCard
  },
  data() {
    return {
      range: 'today',
      todoLoading: false,
      kpiLoading: false,
      counts: {},
      daily: []
    }
  },
  computed: {
    ...mapGetters(['name', 'permission_routes', 'sidebarRouters']),
    anyLoading() {
      return this.todoLoading || this.kpiLoading
    },
    // 平台超管 admin 无到期概念，不显示到期徽标、不弹续费提醒
    // 当前账号实际可访问的全部路由路径
    reachablePaths() {
      const paths = collectRoutePaths(this.permission_routes)
      collectRoutePaths(this.sidebarRouters, '', paths)
      return paths
    },
    todoItems() {
      const items = []
      TODO_CONFIG.forEach(def => {
        const to = pickReachablePath(def.paths, this.reachablePaths)
        if (!to) {
          return
        }
        const value = this.counts[def.key]
        items.push({
          key: def.key,
          label: def.label,
          icon: def.icon,
          color: def.color,
          to: to,
          value: value === undefined || value === null ? 0 : value
        })
      })
      return items
    },
    quickEntries() {
      const entries = []
      QUICK_ENTRY_CONFIG.forEach(def => {
        const to = pickReachablePath(def.paths, this.reachablePaths)
        if (!to) {
          return
        }
        entries.push({ label: def.label, icon: def.icon, to: to })
      })
      return entries
    },
    kpiCards() {
      const cur = this.sumRange(this.daily, this.rangeDays, 0)
      const prev = this.sumRange(this.daily, this.rangeDays, this.rangeDays)
      return [
        {
          label: '销售额',
          value: cur.amount,
          prefix: '¥',
          decimals: 2,
          growth: this.computeGrowth(cur.amount, prev.amount),
          icon: 'money',
          color: '#1890ff'
        },
        {
          label: '订单数',
          value: cur.count,
          prefix: '',
          decimals: 0,
          growth: this.computeGrowth(cur.count, prev.count),
          icon: 'list',
          color: '#E6A23C'
        },
        {
          label: '客单价',
          value: cur.count > 0 ? cur.amount / cur.count : 0,
          prefix: '¥',
          decimals: 2,
          growth: this.computeGrowth(cur.count > 0 ? cur.amount / cur.count : null, prev.count > 0 ? prev.amount / prev.count : null),
          icon: 'chart',
          color: '#67C23A'
        }
      ]
    },
    rangeDays() {
      return this.range === '7d' ? 7 : this.range === '30d' ? 30 : 1
    }
  },
  mounted() {
    this.refreshAll()
  },
  methods: {
    handleChangeRange(val) {
      this.range = val
      this.refreshAll()
    },
    refreshAll() {
      this.loadTodayDaily()
      this.loadSalesDaily()
    },
    loadTodayDaily() {
      this.todoLoading = true
      todayDaily().then(resp => {
        this.counts = resp.data || {}
      }).catch(() => {
        this.counts = {}
      }).finally(() => {
        this.todoLoading = false
      })
    },
    loadSalesDaily() {
      this.kpiLoading = true
      salesDaily().then(resp => {
        this.daily = resp.data || []
      }).catch(() => {
        this.daily = []
      }).finally(() => {
        this.kpiLoading = false
      })
    },
    /**
     * 汇总最近 days 天的销售额/订单数；offset 用于取上一等长周期
     * daily 按日期升序，最后一条为最近有订单的日期
     */
    sumRange(daily, days, offset) {
      const list = daily || []
      const end = list.length - offset
      const start = Math.max(0, end - days)
      let amount = 0
      let count = 0
      for (let i = start; i < end; i++) {
        amount += Number(list[i].amount) || 0
        count += Number(list[i].count) || 0
      }
      return { amount: Math.round(amount * 100) / 100, count: count }
    },
    computeGrowth(current, prev) {
      const cur = Number(current)
      const pre = Number(prev)
      if (!pre || isNaN(cur) || isNaN(pre)) {
        return null
      }
      return ((cur - pre) / pre) * 100
    }
  }
}
</script>

<style lang="scss" scoped>
.workbench {
  position: relative;
  padding: 16px;
  background-color: rgb(240, 242, 245);
  min-height: calc(100vh - 84px);
}

@media (max-width: 1024px) {
  .workbench {
    padding: 8px;
  }
}
</style>
