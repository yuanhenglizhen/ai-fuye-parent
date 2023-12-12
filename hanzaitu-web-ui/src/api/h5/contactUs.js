import request from "@/utils/h5/request";
import { getToken } from "@/utils/h5/auth";


const baseUrl = "/contactUs";
const isToken = true;



// 查询窗口列表
export function getContactUs() {
  return request({
    url: `${baseUrl}/getContactUs`,
    headers: {
      isToken,
    },
    method: "get",
  });
}
