import request from '@/utils/request'
import { parseStrEmpty } from "@/utils/ruoyi";

// 查询定时任务调度列表
export function listData(query) {
  return request({
    url: '/system/config/getDefaultPointsConfig',
    method: 'get',
    params: query
  })
}

//
export function addFun(data) {
  return request({
    url: '/system/config/saveDefaultPointsConfig',
    method: 'post',
    data: data
  })
}
