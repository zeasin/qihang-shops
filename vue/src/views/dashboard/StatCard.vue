<template>
  <div class="stat-card" @click="handleClick">
    <div class="stat-card-body">
      <div class="stat-card-label">
        <span v-if="icon" class="stat-card-badge" :style="{ color: color, background: color + '14' }">
          <svg-icon :icon-class="icon" />
        </span>
        <span>{{ label }}</span>
      </div>
      <div class="stat-card-value">
        <span v-if="prefix" class="stat-card-prefix">{{ prefix }}</span>
        <count-to
          v-if="!loading"
          :start-val="0"
          :end-val="numberValue"
          :duration="1200"
          :decimals="decimals"
          class="stat-card-num"
        />
        <span v-else class="stat-card-num stat-card-num--loading">--</span>
      </div>
      <div v-if="growthText" class="stat-card-growth" :class="growthClass">
        {{ growthText }}
        <span class="growth-label">较上一周期</span>
      </div>
    </div>
  </div>
</template>

<script>
import CountTo from 'vue-count-to'

export default {
  name: 'StatCard',
  components: {
    CountTo
  },
  props: {
    label: {
      type: String,
      required: true
    },
    value: {
      type: [Number, String],
      default: 0
    },
    prefix: {
      type: String,
      default: ''
    },
    decimals: {
      type: Number,
      default: 0
    },
    // 环比增长率（百分比数值），为 null 时不显示
    growth: {
      type: Number,
      default: null
    },
    icon: {
      type: String,
      default: ''
    },
    color: {
      type: String,
      default: '#1890ff'
    },
    loading: {
      type: Boolean,
      default: false
    },
    to: {
      type: String,
      default: ''
    }
  },
  computed: {
    numberValue() {
      const num = Number(this.value)
      return isNaN(num) ? 0 : num
    },
    growthText() {
      if (this.growth === null || this.growth === undefined || isNaN(this.growth)) {
        return ''
      }
      const arrow = this.growth >= 0 ? '↑' : '↓'
      return `${arrow} ${Math.abs(this.growth).toFixed(1)}%`
    },
    growthClass() {
      return this.growth >= 0 ? 'is-up' : 'is-down'
    }
  },
  methods: {
    handleClick() {
      if (this.to) {
        this.$router.push(this.to)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.stat-card {
  padding: 16px 18px;
  margin-bottom: 16px;
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, .06);
  transition: transform .2s ease, box-shadow .2s ease;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, .08);
  }

  .stat-card-label {
    display: flex;
    align-items: center;
    font-size: 13px;
    color: #909399;

    .stat-card-badge {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;
      width: 24px;
      height: 24px;
      margin-right: 8px;
      border-radius: 6px;
      font-size: 13px;
    }
  }

  .stat-card-value {
    margin-top: 10px;
    font-size: 28px;
    font-weight: 700;
    line-height: 1.2;
    color: #303133;

    .stat-card-prefix {
      font-size: 18px;
      font-weight: 500;
    }

    .stat-card-num--loading {
      color: #c0c4cc;
    }
  }

  .stat-card-growth {
    margin-top: 8px;
    font-size: 12px;
    font-weight: 600;

    &.is-up {
      color: #67c23a;
    }

    &.is-down {
      color: #f56c6c;
    }

    .growth-label {
      margin-left: 6px;
      font-weight: normal;
      color: #c0c4cc;
    }
  }
}
</style>
