import request from '@/utils/request'

// SKU 成本档案列表（关键字/未填成本筛选）
export function listCostSku(query) {
  return request({
    url: '/goodsCost/skuList',
    method: 'get',
    params: query
  })
}

// 行内更新成本价/售价
export function updateSkuCost(data) {
  return request({
    url: '/goodsCost/skuCost',
    method: 'put',
    data: data
  })
}

// Excel 导入成本
export function importGoodsCost(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/goodsCost/import',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data', repeatSubmit: false }
  })
}
