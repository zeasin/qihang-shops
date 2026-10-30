import request from '@/utils/request'

// 解析订单 Excel：表头 + 行数 + 样例
export function parseOrderExcel(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/orderImport/parse',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data', repeatSubmit: false }
  })
}

// 预览导入（前 50 行 + 重复/未匹配统计）
export function previewOrderImport(file, shopId, mapping) {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('shopId', shopId)
  formData.append('mapping', JSON.stringify(mapping))
  return request({
    url: '/orderImport/preview',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data', repeatSubmit: false }
  })
}

// 提交入库
export function commitOrderImport(file, shopId, mapping) {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('shopId', shopId)
  formData.append('mapping', JSON.stringify(mapping))
  return request({
    url: '/orderImport/commit',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data', repeatSubmit: false }
  })
}

// 手动建单
export function createManualOrder(data) {
  return request({
    url: '/orderImport/create',
    method: 'post',
    data: data
  })
}

// ==================== 映射模板 ====================

// 模板列表
export function listImportTemplate(platform) {
  return request({
    url: '/orderImport/template/list',
    method: 'get',
    params: { platform }
  })
}

// 保存/更新模板
export function saveImportTemplate(data) {
  return request({
    url: '/orderImport/template/save',
    method: 'post',
    data: data
  })
}

// 删除模板
export function delImportTemplate(ids) {
  return request({
    url: '/orderImport/template/del/' + ids,
    method: 'delete'
  })
}
