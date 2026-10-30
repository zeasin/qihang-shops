import request from '@/utils/request'

// ==================== 推广费 ====================

// 推广费列表
export function listPromotionFee(query) {
  return request({
    url: '/promotionFee/list',
    method: 'get',
    params: query
  })
}

// 新增推广费
export function addPromotionFee(data) {
  return request({
    url: '/promotionFee/add',
    method: 'post',
    data: data
  })
}

// 修改推广费
export function updatePromotionFee(data) {
  return request({
    url: '/promotionFee/edit',
    method: 'put',
    data: data
  })
}

// 删除推广费
export function delPromotionFee(ids) {
  return request({
    url: '/promotionFee/del/' + ids,
    method: 'delete'
  })
}

// ==================== 固定费用 ====================

// 固定费用列表
export function listFixedFee(query) {
  return request({
    url: '/fixedFee/list',
    method: 'get',
    params: query
  })
}

// 新增固定费用
export function addFixedFee(data) {
  return request({
    url: '/fixedFee/add',
    method: 'post',
    data: data
  })
}

// 修改固定费用
export function updateFixedFee(data) {
  return request({
    url: '/fixedFee/edit',
    method: 'put',
    data: data
  })
}

// 删除固定费用
export function delFixedFee(ids) {
  return request({
    url: '/fixedFee/del/' + ids,
    method: 'delete'
  })
}

// ==================== 对账单 ====================

// 对账单明细列表
export function listPlatformBill(query) {
  return request({
    url: '/platformBill/list',
    method: 'get',
    params: query
  })
}

// 手动补录账单明细
export function addPlatformBill(data) {
  return request({
    url: '/platformBill/add',
    method: 'post',
    data: data
  })
}

// 自动匹配订单
export function matchPlatformBill() {
  return request({
    url: '/platformBill/match',
    method: 'put'
  })
}

// 删除账单明细
export function delPlatformBill(ids) {
  return request({
    url: '/platformBill/del/' + ids,
    method: 'delete'
  })
}

// 按批次删除
export function delPlatformBillBatch(batchNo) {
  return request({
    url: '/platformBill/batch/' + batchNo,
    method: 'delete'
  })
}
