import Vue from 'vue'
import Vuex from 'vuex'
import user from '@/store/modules/user'
import getters from './getters'
Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    drawer: false,
    loginShow: false,
  },
  modules: {
    user
  },
  getters,
  mutations: {
    SET_OPEN: (state, data) => {
      state.drawer = data
    },
    OPEN_LOGIN: (state, data) => {
      state.loginShow = data
    },
  },
  actions: {},
  /*plugins: [
    createPersistedState({
      storage: window.sessionStorage
    })
  ]*/
})
