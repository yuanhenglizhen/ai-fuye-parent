import request from '@/utils/request'

// 查询列表
export function listFun(data) {
  return request({
    url: '/chat/cxpired/info',
    method: 'post',
    data: data
  })
}
// 新增
export function addFun(data) {
  return request({
    url: '/chat/cxpired/save',
    method: 'post',
    data: data
  })
}

// 查询首页配置
export function listFunHome(query) {
  return request({
    url: '/system/config/getHomeConfig',
    method: 'get',
    params: query
  })
}
// 新增首页配置
export function addFunHome(data) {
  return request({
    url: '/system/config/saveHomeConfig',
    method: 'post',
    data: data
  })
}

// 查询首页配置
export function listFunQNY(data) {
  return request({
    url: '/qny/config/info',
    method: 'post',
    data: data
  })
}
// 新增首页配置
export function addFunQNY(data) {
  return request({
    url: '/qny/config/update',
    method: 'post',
    data: data
  })
}


