import Vue from 'vue'
import VueRouter from 'vue-router'
import layContent from '@/layout/layContent.vue'
import MainUserVue from '@/layout/mainUser.vue'
import personalCenter from '@/views/pc/personalCenter/personalCenter.vue'
import home from '@/views/pc/home'
import topUp from '@/views/pc/personalCenter/topUp'
Vue.use(VueRouter)

const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => err)
}

export const routes = [
  {
    path: '/',
    name: '客户端',
    type: 'user',
    component: MainUserVue,
    children: [
      /*{
        path: '/',
        name: 'home',
        component: home,
      },*/
      {
        path: '/',
        name: 'ai聊天',
        component: layContent,
        children: [
          {
            name: '/',
            meta: {
              icon: 'el-icon-s-home',
              title: '首页'
            },
            path: '/',
            component: () => import('../../views/pc/homePage')
          },
          {
            name: 'chat3.5',
            meta: {
              icon: 'el-icon-s-home',
              title: 'ai聊天'
            },
            path: '/chat3.5',
            component: () => import('../../views/pc/aiChat/aiChat')
          },
          {
            name: 'chat4',
            meta: {
              icon: 'el-icon-s-home',
              title: 'ai聊天'
            },
            path: '/chat4',
            component: () => import('../../views/pc/aiChat4/aiChat4')
          },
          {
            name: 'aiPainting',
            meta: {
              icon: 'el-icon-s-home',
              title: 'ai绘画'
            },
            path: '/aiPainting',
            component: () => import('../../views/pc/aiPainting/aiPainting')
          },
          {
            name: 'paintingStyle',
            meta: {
              icon: 'el-icon-s-home',
              title: '风格长廊'
            },
            path: '/paintingStyle',
            component: () => import('../../views/pc/paintingSquare/paintingStyle')
          },
          {
            name: 'personalDynamics',
            meta: {
              icon: 'el-icon-s-home',
              title: '个人动态'
            },
            path: '/personalDynamics',
            component: () => import('../../views/pc/paintingSquare/personalDynamics')
          },
          {
            name: 'instructions',
            meta: {
              icon: 'el-icon-s-home',
              title: '操作指南'
            },
            path: '/instructions',
            component: () => import('../../views/pc/instructions/instructions')
          },
          {
            name: 'contactUs',
            meta: {
              icon: 'el-icon-s-home',
              title: '联系我们'
            },
            path: '/contactUs',
            component: () => import('../../views/pc/contactUs/contactUs')
          },
          {
            name: 'test',
            meta: {
              icon: 'el-icon-s-home',
              title: '测试版本页面'
            },
            path: '/test',
            component: () => import('../../views/pc/test')
          },
        ]
      },
      {
        path: '/personalCenter',
        name: '个人中心',
        component: personalCenter,
      },
      {
        path: '/topUp',
        name: '充值',
        component: topUp,
      },
    ]
  }
]

const router = new VueRouter({
  // mode: 'hash',
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})
// 全局路由构造函数，判断是否登录和要跳转到页面
/*router.beforeEach((to, from, next) => {
  console.log(to.path)
  if (to.path !== '/login') {
    let token = window.localStorage.getItem('token')
    if (!token) {
      next('/login')
    } else {
      // const userInfo = JSON.parse(window.localStorage.getItem('userInfo'))

      // if (userInfo.mobile !== 'admin') {
      //   next('/user/index')
      // } else {
      //   next()
      // }
      document.title = to.meta.title
      next()
    }
  } else if (to.path == '/login') {
    next()
  }
})*/
export default router
