import request, { download } from '@/utils/request'

// 订单利润（一行一单）
export function orderProfit(query) {
  return request({
    url: '/profit/order',
    method: 'get',
    params: query
  })
}

// 链接利润（一行一款，推广费分摊后）
export function linkProfit(query) {
  return request({
    url: '/profit/link',
    method: 'get',
    params: query
  })
}

// 店铺利润（一行一店，净利润）
export function shopProfit(query) {
  return request({
    url: '/profit/shop',
    method: 'get',
    params: query
  })
}

// 数据完整度检查
export function profitCompleteness(query) {
  return request({
    url: '/profit/completeness',
    method: 'get',
    params: query
  })
}

// 导出利润报表 Excel（level: order|link|shop）
export function exportProfit(query) {
  return download('/profit/export', query, `利润报表-${query.level}-${query.month || ''}.xlsx`)
}
