import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

// 查询支付宝pc配置
export function listFunPcZFB(query) {
  return request({
    url: '/system/config/getAliPayConfig',
    method: 'get',
    params: query
  })
}

// 修改支付宝pc配置
export function addFunZFBPC(data) {
  return request({
    url: '/system/config/saveAliPayConfig',
    method: 'post',
    data: data
  })
}

// 查询微信配置
export function listFunWX(query) {
  return request({
    url: '/system/config/getWxPayConfig',
    method: 'get',
    params: query
  })
}

// 修改微信配置
export function addFunWX(data) {
  return request({
    url: '/system/config/saveWxPayConfig',
    method: 'post',
    data: data
  })
}



// 查询源支付配置
export function listFunY(query) {
  return request({
    url: '/system/config/getYPayConfig',
    method: 'get',
    params: query
  })
}

// 修改源支付配置
export function addFunY(data) {
  return request({
    url: '/system/config/saveYPayConfig',
    method: 'post',
    data: data
  })
}

// 查询支付积分配置
export function listFunPoints(query) {
  return request({
    url: '/system/config/getPayToPointsConfig',
    method: 'get',
    params: query
  })
}

// 修改支付积分配置
export function addFunPoints(data) {
  return request({
    url: '/system/config/savePayToPointsConfig',
    method: 'post',
    data: data
  })
}

// 获取积分消耗类型
export function listIntegralUseType(query) {
  return request({
    url: '/system/config/listExpendType',
    method: 'get',
    params: query
  })
}
// 查询积分使用配置
export function listIntegralUse(query) {
  return request({
    url: '/system/config/getExpendConfig',
    method: 'get',
    params: query
  })
}
// 修改支付积分配置
export function addIntegralUse(data) {
  return request({
    url: '/system/config/saveExpendConfig',
    method: 'post',
    data: data
  })
}

// 修改支付积分配置
export function getPayType(query) {
  return request({
    url: '/system/config/getPayType',
    method: 'get',
    params: query
  })
}
// 修改支付积分配置
export function savePayType(data) {
  return request({
    url: '/system/config/savePayType',
    method: 'post',
    data: data
  })
}
