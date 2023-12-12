import { HTTP_URI,baseUrl } from '@/api/api'
import storage from "@/utils/h5/storage";
import constant from "@/utils/h5/constant";
import { login, logout, getUserInfo } from "@/api/h5";
import { getToken, setToken, removeToken } from "@/utils/h5/auth";

const user = {
  namespaced: true,
  state: {
    token: getToken(),
    currentUser: storage.get(constant.currentUser) || {},
    // name: storage.get(constant.name),
    // avatar: storage.get(constant.avatar),
    // roles: storage.get(constant.roles),
    // permissions: storage.get(constant.permissions)
  },

  mutations: {
    SET_TOKEN: (state, token) => {
      state.token = token;
    },
    SET_CURRENTUSER: (state, payload) => {
      state.currentUser = payload;
      //storage.set(constant.currentUser, payload);
    },
    // SET_NAME: (state, name) => {
    //   state.name = name
    //   storage.set(constant.name, name)
    // },
    // SET_AVATAR: (state, avatar) => {
    //   state.avatar = avatar
    //   storage.set(constant.avatar, avatar)
    // },
    // SET_ROLES: (state, roles) => {
    //   state.roles = roles
    //   storage.set(constant.roles, roles)
    // },
    // SET_PERMISSIONS: (state, permissions) => {
    //   state.permissions = permissions
    //   storage.set(constant.permissions, permissions)
    // }
	logOutSync(state) {
		state.token = "";
		state.currentUser = {};
		storage.set(constant.currentUser, {});
		removeToken();
		storage.clean();
	}
  },

  actions: {
    // 登录
    Login({ commit }, userInfo) {
      const username = userInfo.username;
      const password = userInfo.password;
      const chenckMoveid = userInfo.chenckMoveid;
      const removing = userInfo.removing;
      const code = userInfo.code;
      const uuid = userInfo.uuid;
      return new Promise((resolve, reject) => {
        login(username, password,chenckMoveid,removing)
          .then(({ data }) => {
            setToken(data.accessToken);
            commit("SET_TOKEN", data.accessToken);
            resolve();
          })
          .catch((error) => {
            reject(error);
          });
      });
    },

    // 获取用户信息
    GetUserInfo({ commit, state }) {
      return new Promise((resolve, reject) => {
        getUserInfo()
          .then((res) => {
            const user = res.data;
            commit("SET_CURRENTUSER", user);
            resolve(res);
          })
          .catch((error) => {
            reject(error);
          });
      });
    },

    // 退出系统
    LogOut({ commit, state }) {
      return new Promise((resolve, reject) => {
        logout(state.token)
          .then(() => {
            commit("SET_TOKEN", "");
            commit("SET_CURRENTUSER", {});
            removeToken();
            storage.clean();
            resolve();
          })
          .catch((error) => {
			commit("SET_TOKEN", "");
			commit("SET_CURRENTUSER", {});
			removeToken();
			storage.clean();
			reject(error);
          });
      });
    },
  },
};

export default user;
