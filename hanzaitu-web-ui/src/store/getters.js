const getters = {
  drawer: state => state.drawer == false,
  loginShow: state => state.loginShow == false,

  token: state => state.user.token,
  avatar: state => state.user.avatar,
  name: state => state.user.name,
  roles: state => state.user.roles,
  permissions: state => state.user.permissions
}
export default getters
