/**
 * 首页工作台配置（小店管家）
 * 待办卡片 key 对应 /report/todayDaily 返回字段
 * paths 跳转候选路由，按顺序取第一个当前账号实际可访问的；全部不可达时该卡片不显示
 */

export const TODO_CONFIG = [
  { key: 'orderCount', label: '今日订单', icon: 'list', color: '#E6A23C', paths: ['/order/list'] },
  { key: 'shopCount', label: '店铺数', icon: 'shop', color: '#67C23A', paths: ['/shop/list', '/shop/index'] }
]

/**
 * 快捷入口配置（同样走候选路由 + 可达性过滤）
 * 利润工作流入口在前：速记工单 → 费用 → 利润
 */
export const QUICK_ENTRY_CONFIG = [
  { label: '订单导入', icon: 'download', paths: ['/order/import'] },
  { label: '商品成本', icon: 'excel', paths: ['/goodsCost/profile'] },
  { label: '速记工单', icon: 'message', paths: ['/workOrder/index', '/workOrder'] },
  { label: '推广费', icon: 'money', paths: ['/fee/promotion'] },
  { label: '固定费用', icon: 'money', paths: ['/fee/fixed'] },
  { label: '对账单', icon: 'money', paths: ['/fee/bill'] },
  { label: '店铺利润', icon: 'chart', paths: ['/profit/shop'] },
  { label: '订单利润', icon: 'chart', paths: ['/profit/order'] },
  { label: '手动建单', icon: 'edit', paths: ['/order/create'] },
  { label: '订单库', icon: 'list', paths: ['/order/list'] },
  { label: '店铺管理', icon: 'shop', paths: ['/shop/list', '/shop/index'] }
]

/**
 * 规范化路由路径：折叠重复斜杠、去掉末尾斜杠
 */
function normalizePath(path) {
  if (!path) {
    return ''
  }
  let result = path.replace(/\/{2,}/g, '/')
  if (result.length > 1 && result.charAt(result.length - 1) === '/') {
    result = result.slice(0, -1)
  }
  return result
}

/**
 * 按 vue-router 的规则把父子路由拼成完整路径
 */
function resolveRoutePath(parentPath, path) {
  if (!path) {
    return parentPath || ''
  }
  if (path.charAt(0) === '/') {
    return normalizePath(path)
  }
  if (!parentPath) {
    return normalizePath('/' + path)
  }
  return normalizePath(parentPath + '/' + path)
}

/**
 * 从动态路由树中收集全部可访问的完整路径（如 /order/shop_order）
 */
export function collectRoutePaths(routes, parentPath = '', result = []) {
  if (!routes || !routes.length) {
    return result
  }
  routes.forEach(route => {
    const fullPath = resolveRoutePath(parentPath, route.path)
    if (fullPath) {
      result.push(fullPath)
    }
    if (route.children && route.children.length) {
      collectRoutePaths(route.children, fullPath, result)
    }
  })
  return result
}

/**
 * 按候选顺序返回第一个可访问的路由，全部不可达返回空字符串
 */
export function pickReachablePath(paths, reachablePaths) {
  const candidates = paths || []
  for (let i = 0; i < candidates.length; i++) {
    if (reachablePaths.indexOf(normalizePath(candidates[i])) > -1) {
      return candidates[i]
    }
  }
  return ''
}
