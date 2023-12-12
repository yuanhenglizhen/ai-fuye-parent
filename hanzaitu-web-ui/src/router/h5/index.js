import Vue from 'vue'
import VueRouter from 'vue-router'
Vue.use(VueRouter)

const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
    return originalPush.call(this, location).catch(err => err)
}

export const routes = [
    /*{
        path: '/',
        name: 'spla',
        component: () => import('../../views/h5/chatgpt/spla')
    },*/
    {
        path: '/',
        name: 'draw',
        component: () => import('../../views/h5/chatgpt/draw')
    },
    {
        path: '/chat',
        name: 'chat',
        component: () => import('../../views/h5/chatgpt/chat')
    },
    {
        path: '/contact-us',
        name: 'contact-us',
        component: () => import('../../views/h5/chatgpt/contact-us')
    },
    {
        path: '/instruction',
        name: 'instruction',
        component: () => import('../../views/h5/chatgpt/instruction')
    },
    {
        path: '/thesaurus',
        name: 'thesaurus',
        component: () => import('../../views/h5/chatgpt/thesaurus')
    },
    {
        path: '/mine',
        name: 'mine',
        component: () => import('../../views/h5/mine/index')
    },
    {
        path: '/recharge',
        name: 'recharge',
        component: () => import('../../views/h5/mine/recharge')
    },
    {
        path: '/message',
        name: 'message',
        component: () => import('../../views/h5/mine/message')
    },
]

const router = new VueRouter({
    // mode: 'hash',
    mode: 'history',
    base: process.env.BASE_URL,
    routes
})

export default router
