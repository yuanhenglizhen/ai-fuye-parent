import request from '@/utils/request'

// 查询列表
export function listFun(query) {
  return request({
    url: '/system/config/getKD100Config',
    method: 'get',
    params: query
  })
}

// 新增
export function addFun(data) {
  return request({
    url: '/system/config/saveKD100Config',
    method: 'post',
    data: data
  })
}
