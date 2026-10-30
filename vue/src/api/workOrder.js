import request from '@/utils/request'

// 工单列表（待处理置顶）
export function listWorkOrder(query) {
  return request({
    url: '/workOrder/list',
    method: 'get',
    params: query
  })
}

// 速记工单
export function quickWorkOrder(data) {
  return request({
    url: '/workOrder/quick',
    method: 'post',
    data: data
  })
}

// 完整新建工单
export function addWorkOrder(data) {
  return request({
    url: '/workOrder/add',
    method: 'post',
    data: data
  })
}

// 修改工单
export function updateWorkOrder(data) {
  return request({
    url: '/workOrder/edit',
    method: 'put',
    data: data
  })
}

// 删除工单
export function delWorkOrder(ids) {
  return request({
    url: '/workOrder/del/' + ids,
    method: 'delete'
  })
}

// 批量结算
export function settleWorkOrder(ids) {
  return request({
    url: '/workOrder/settle/' + ids,
    method: 'put'
  })
}

// 撤销结算
export function revokeWorkOrder(id) {
  return request({
    url: '/workOrder/revoke/' + id,
    method: 'put'
  })
}
