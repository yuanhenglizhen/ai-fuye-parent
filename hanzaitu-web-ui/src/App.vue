<template>
  <div id="app">
    <router-view />
  </div>
</template>
<script>
export default {
  created() {
    //this.getInfo();
  },
  mounted() {
    // window.devicePixelRatio就是当前浏览器和系统缩放过后的比例，我只需要反向缩放body即可。
    console.log(window.devicePixelRatio);
    //document.body.style.zoom = 1 / window.devicePixelRatio;
  },
  watch: {
    $route: function (to, from) {
      if (to.name.indexOf('_p') > 0 && !this.$pc) {
        this.$router.replace(to.name.split('_p')[0])
        //电脑转手机端
        //域名为index_p去掉_p变成手机端index路由
        if (to.name=="index_p") {
          let str = to.name.split("_p")[0];
          let str2 = '/'+str;
          this.$router.push(str2);
        }
      } else if (to.name.indexOf('_p') < 0 && this.$pc) {
        //手机端转电脑
        //在路由尾巴添加_p变成电脑端
        this.$router.replace(to.name + '_p')
      }
    }
  },
  methods: {
    getInfo(){
      this.$GET('GETUSERINFO', {},0).then(res => {
        if (res.code == 200) {
          window.localStorage.setItem('infoData', JSON.stringify(res.data));
        } else {
          this.$store.commit('OPEN_LOGIN', true);
          window.localStorage.clear();
        }
      })
    },
  }
}
</script>
<style lang="scss">
body {
  margin: 0;
  padding: 0;
  height: 100%;
  width: 100%;
  #app{
    height: 100%;
    width: 100%;
  }
}
@mixin linearGradient {
  background: linear-gradient(90deg, #836CF7 0%, #EC6DC0 100%);
}
@mixin ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
:root {
  --primary-color: #080B16;
  --black-1: #000;
  --white-1: #fff;
  --linear-gradient: linear-gradient(90deg, #836CF7 0%, #EC6DC0 100%);
}
@mixin linearGradient {
  background: linear-gradient(90deg, #836CF7 0%, #EC6DC0 100%);
}
.bg-linear-radient {
  @include linearGradient();
}
.font-linear-radient {
  @include linearGradient();
  background-clip: text;
  color: transparent;
}
@mixin ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
