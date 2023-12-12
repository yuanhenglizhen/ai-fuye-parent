import Vue from 'vue'
import App from './App.vue'
import routerpc from './router/pc'
import routerh5 from './router/h5'
let router = routerpc;
// vue原型挂载 - 是否PC端
if (/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|OperaMini/i.test(navigator.userAgent)) {
  router = routerh5;
} else {
  router = routerpc;
}
import store from './store'
import hljs from 'highlight.js'
import SlideVerify from 'vue-monoplasty-slide-verify'

import '@/assets/css/style.scss'
import '@/assets/css/icon.scss'
import '@/assets/css/font.scss'
import ElementUI, {Loading} from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import '@/assets/css/elementui.scss'

import VueClipboard  from 'vue-clipboard2'
import Vant from 'vant';
import 'vant/lib/index.css';

import { REQUEST } from '@/api/http'
import { wsUrl,baseUrl,staticUrl } from '@/api/api'

import dragVerify from 'vue-drag-verify2'

Vue.prototype.$eventBus = new Vue()
Vue.config.productionTip = false
Vue.prototype.wsUrl = wsUrl
Vue.prototype.baseUrl = baseUrl
Vue.prototype.staticUrl = staticUrl
Vue.prototype.$https = REQUEST.POST
Vue.prototype.$POSTPAGE = REQUEST.POSTPAGE
Vue.prototype.$GET = REQUEST.GET
Vue.prototype.$PUT = REQUEST.PUT
Vue.use(SlideVerify)
Vue.use(ElementUI)
Vue.use(hljs)
Vue.use(VueClipboard)
Vue.use(Vant)
Vue.use(dragVerify)

let loading
// ui
Vue.prototype.$ui = {
  showLoading() {
    loading = Loading.service({
      customClass: 'loading_icon',
      fullscreen: true,
      lock: true,
      text: '',
      spinner: '', // 自定义图标
      background: 'rgba(0, 0, 0, 0.1)'
    })
  },
  hideLoading() {
    //uni.hideLoading();
    loading.close()
  },
  showModal(title) {
    return new Promise((resolve, reject) => {
      /*uni.showModal({
        title: "提示",
        content: title,
        showCancel: false,
        confirmText: "确定",
        success(res) {
          resolve(res);
        },
        fail(err) {
          reject(err);
        },
      });*/
      Vue.$confirm('是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then((res) => {
        resolve(res);
      }).catch((err) => {
        reject(err);
      });

    });
  },
  // https://uniapp.dcloud.io/api/ui/prompt?id=showtoast
  showToast(title) {
    return new Promise((resolve, reject) => {
      resolve();
    });
  },
  usingLoading(action) {
    return new Promise((resolve, reject) => {
      this.showLoading();

      if (typeof action === "function") {
        try {
          const result = action.call(this);
          if (result instanceof Promise) {
            result
                .then(() => {
                  this.hideLoading();
                  resolve();
                })
                .catch((e) => {
                  this.hideLoading();
                  //reject(e);
                });
          } else {
            this.hideLoading();
            resolve();
          }
        } catch (e) {
          this.hideLoading();
          reject();
        }
      } else {
        this.hideLoading();
        resolve();
      }
    });
  },
};


new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
