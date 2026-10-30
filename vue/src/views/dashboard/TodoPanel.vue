<template>
  <div class="todo-panel">
    <div class="todo-panel-title">
      待办事项
    </div>
    <el-row :gutter="16">
      <el-col v-for="item in items" :key="item.key" :xs="12" :sm="8" :lg="6">
        <div class="todo-item" :class="{ 'is-clickable': !!item.to }" @click="handleClick(item)">
          <div class="todo-item-icon" :style="{ color: item.color, background: item.color + '14' }">
            <svg-icon :icon-class="item.icon" />
          </div>
          <div class="todo-item-content">
            <div class="todo-item-label">{{ item.label }}</div>
            <div class="todo-item-value">
              <count-to
                v-if="!loading"
                :start-val="0"
                :end-val="Number(item.value) || 0"
                :duration="1000"
              />
              <span v-else>--</span>
            </div>
          </div>
          <i v-if="item.to" class="el-icon-arrow-right todo-item-arrow" />
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import CountTo from 'vue-count-to'

export default {
  name: 'TodoPanel',
  components: {
    CountTo
  },
  props: {
    // [{ key, label, value, icon, color, to }]
    items: {
      type: Array,
      default: () => []
    },
    loading: {
      type: Boolean,
      default: false
    }
  },
  methods: {
    handleClick(item) {
      if (item.to) {
        this.$router.push(item.to)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.todo-panel {
  padding: 16px 18px 4px;
  margin-bottom: 16px;
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, .06);

  .todo-panel-title {
    margin-bottom: 14px;
    font-size: 15px;
    font-weight: 600;
    color: #303133;
  }

  .todo-item {
    display: flex;
    align-items: center;
    position: relative;
    padding: 12px 14px;
    margin-bottom: 12px;
    border: 1px solid #ebeef5;
    border-radius: 8px;
    background: #fff;
    transition: border-color .2s ease, box-shadow .2s ease, transform .2s ease;

    &.is-clickable {
      cursor: pointer;

      &:hover {
        border-color: #91d5ff;
        box-shadow: 0 2px 8px rgba(24, 144, 255, .1);
        transform: translateY(-2px);
      }
    }

    .todo-item-icon {
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;
      width: 38px;
      height: 38px;
      border-radius: 8px;
      font-size: 18px;
    }

    .todo-item-content {
      margin-left: 12px;
      overflow: hidden;
    }

    .todo-item-label {
      font-size: 13px;
      color: #909399;
      white-space: nowrap;
    }

    .todo-item-value {
      margin-top: 2px;
      font-size: 22px;
      font-weight: 700;
      line-height: 1.2;
      color: #303133;
    }

    .todo-item-arrow {
      position: absolute;
      right: 12px;
      color: #c0c4cc;
    }
  }
}
</style>
