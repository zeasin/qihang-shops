<template>
  <div v-if="entries.length" class="quick-entry">
    <div class="quick-entry-title">
      快捷入口
    </div>
    <div class="quick-entry-list">
      <div
        v-for="entry in entries"
        :key="entry.label"
        class="quick-entry-item"
        @click="handleClick(entry)"
      >
        <div class="quick-entry-icon">
          <svg-icon :icon-class="entry.icon" />
        </div>
        <div class="quick-entry-label">{{ entry.label }}</div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'QuickEntry',
  props: {
    // [{ label, icon, to }]，to 为空表示不可访问，已由父组件过滤
    entries: {
      type: Array,
      default: () => []
    }
  },
  methods: {
    handleClick(entry) {
      if (entry.to) {
        this.$router.push(entry.to)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.quick-entry {
  padding: 16px 18px;
  margin-bottom: 16px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, .06);

  .quick-entry-title {
    margin-bottom: 14px;
    font-size: 15px;
    font-weight: 600;
    color: #303133;
  }

  .quick-entry-list {
    display: flex;
    flex-wrap: wrap;
  }

  .quick-entry-item {
    display: flex;
    align-items: center;
    padding: 8px 16px;
    margin: 0 12px 12px 0;
    border-radius: 8px;
    background: #fafbfc;
    cursor: pointer;
    transition: background .2s ease, transform .2s ease;

    &:hover {
      background: #ecf5ff;
      transform: translateY(-2px);
    }

    .quick-entry-icon {
      margin-right: 8px;
      font-size: 16px;
      color: #409EFF;
    }

    .quick-entry-label {
      font-size: 13px;
      color: #303133;
    }
  }
}
</style>
