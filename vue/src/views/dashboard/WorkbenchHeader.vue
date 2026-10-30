<template>
  <div class="workbench-header">
    <div class="greeting">
      <div class="greeting-text">
        {{ greeting }}，{{ nickname || '您好' }}
        <span v-if="identityLabel" class="identity-badge">{{ identityLabel }}</span>
      </div>
      <div class="greeting-date">{{ today }}</div>
    </div>
    <div class="header-actions">
      <el-radio-group v-model="innerRange" size="small" @change="handleChangeRange">
        <el-radio-button label="today">今日</el-radio-button>
        <el-radio-button label="7d">近7天</el-radio-button>
        <el-radio-button label="30d">近30天</el-radio-button>
      </el-radio-group>
      <el-tooltip content="刷新数据" placement="top">
        <el-button size="small" icon="el-icon-refresh" :loading="loading" @click="handleRefresh" />
      </el-tooltip>
    </div>
  </div>
</template>

<script>
export default {
  name: 'WorkbenchHeader',
  props: {
    identity: {
      type: Number,
      default: null
    },
    nickname: {
      type: String,
      default: ''
    },
    identityLabel: {
      type: String,
      default: ''
    },
    range: {
      type: String,
      default: 'today'
    },
    loading: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      innerRange: this.range
    }
  },
  computed: {
    greeting() {
      const hour = new Date().getHours()
      if (hour < 6) return '凌晨好'
      if (hour < 12) return '早上好'
      if (hour < 14) return '中午好'
      if (hour < 18) return '下午好'
      return '晚上好'
    },
    today() {
      const now = new Date()
      const week = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'][now.getDay()]
      return `${now.getFullYear()}年${now.getMonth() + 1}月${now.getDate()}日 ${week}`
    },
  },
  watch: {
    range(val) {
      this.innerRange = val
    }
  },
  methods: {
    handleChangeRange(val) {
      this.$emit('change-range', val)
    },
    handleRefresh() {
      this.$emit('refresh')
    }
  }
}
</script>

<style lang="scss" scoped>
.workbench-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  padding: 16px 20px;
  margin-bottom: 16px;
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, .06);

  .greeting-text {
    font-size: 18px;
    font-weight: 600;
    color: #303133;
  }

  .identity-badge {
    display: inline-block;
    margin-left: 8px;
    padding: 1px 10px;
    font-size: 12px;
    font-weight: normal;
    color: #1890ff;
    background: #e6f7ff;
    border: 1px solid #91d5ff;
    border-radius: 10px;
    vertical-align: middle;
  }

  .expire-badge {
    display: inline-block;
    margin-left: 8px;
    padding: 1px 10px;
    font-size: 12px;
    font-weight: normal;
    color: #1890ff;
    background: #e6f7ff;
    border: 1px solid #91d5ff;
    border-radius: 10px;
    vertical-align: middle;
    cursor: pointer;
  }

  .expire-badge.expire-warning {
    color: #e6a23c;
    background: #fdf6ec;
    border-color: #f5dab1;
  }

  .expire-badge.expire-danger {
    color: #f56c6c;
    background: #fef0f0;
    border-color: #fbc4c4;
  }

  .greeting-date {
    margin-top: 6px;
    font-size: 13px;
    color: #909399;
  }

  .header-actions {
    display: flex;
    align-items: center;

    .el-button {
      margin-left: 12px;
    }
  }
}

@media (max-width: 768px) {
  .workbench-header {
    .header-actions {
      margin-top: 12px;
      width: 100%;
    }
  }
}
</style>
