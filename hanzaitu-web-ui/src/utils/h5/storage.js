import constant from './constant'

// 存储变量名
let storageKey = 'storage_data'

// 存储节点变量名
let storageNodeKeys = [constant.currentUser, constant.avatar, constant.name, constant.roles, constant.permissions]

// 存储的数据
let storageData = window.localStorage.getItem(storageKey) || {}

const storage = {
  set: function(key, value) {
    if (storageNodeKeys.indexOf(key) != -1) {
      let tmp = window.localStorage.getItem(storageKey)
      tmp = tmp ? tmp : {}
      tmp[key] = value
      window.localStorage.setItem(storageKey, tmp)
    }
  },
  get: function(key) {
    return storageData[key] || ""
  },
  remove: function(key) {
    delete storageData[key]
    window.localStorage.setItem(storageKey, storageData)
  },
  clean: function() {
    window.localStorage.removeItem(storageKey)
  }
}

export default storage
