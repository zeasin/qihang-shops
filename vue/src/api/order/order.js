import request from '@/utils/request'

// 查询订单列表
export function listOrder(query) {
  return request({
    url: '/order/list',
    method: 'get',
    params: query
  })
}

// 查询订单详细
export function getOrder(id) {
  return request({
    url: '/order/' + id,
    method: 'get'
  })
}

// 订单发货（填写发货成本，记录成本快照）
export function shipOrder(data) {
  return request({
    url: '/order/ship',
    method: 'post',
    data: data
  })
}
