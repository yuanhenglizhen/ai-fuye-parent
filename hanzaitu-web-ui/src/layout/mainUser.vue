<template>
  <div class="content cont-box">
    <div class="cont-bottom">
      <router-view></router-view>
    </div>
  </div>
</template>

<script>
  export default {
    components: {},
    data() {
      return {}
    },
    computed: {},
    mounted() {
    },
    methods: {}
  }
</script>

<style lang="scss" scoped>
  .content {
    background: #000;
    height: 100vh;
    width: 100vw;
    overflow-y: auto;
    display: flex;
    flex-flow: column;
    align-items: center;
    color:#fff;
    .header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      width: 100%;
      height: 6rem;
      box-sizing: border-box;
      background: #181A21;
      border-bottom: 1px solid #2B3A73;

      .rights {
        width: 50%;
        margin-right: 3rem;
        display: flex;
        justify-content: flex-end;
        align-items: center;
        .message-icon{
          width: 3.2rem;
          height: 3.2rem;
          background: url("../assets/img/aiChat/icon_notice32@2x.png") no-repeat;
          background-size: 100%;
          cursor: pointer;
        }
        .avatar-box{
          width: 3.2rem;
          height: 3.2rem;
          border-radius: 50%;
          cursor: pointer;
          img{
            width: 100%;
            height: 100%;
          }
        }
      }
    }
    .cont-bottom{
      width: 100%;
      height: 100%;
    }
    .login-btn{
      width: 6rem;
      height: 2.4rem;
      text-align: center;
      line-height: 2.4rem;
      background: linear-gradient(90deg, #836CF7 0%, #EC6DC0 100%);
      cursor: pointer;
      border-radius: 2.4rem;
    }
  }

  .seamless-warp2 {
    overflow: hidden;
    height: 3.2rem;
    width: 34rem;
    background: #2E3653;
    border-radius: 0.4rem;
    color: #94A3B8;
    font-size: 1.2rem;
    ul.item {
      height: 3.2rem;
      margin: 0;
      li {
        float: left;
        margin:0 10px;
        height: 3.2rem;
        line-height: 3.2rem;
        list-style: none;
      }
    }
  }
  .message-box{
    width: 100%;
    max-height: 43.5rem;
    overflow-y: auto;
    padding:1.8rem 1.6rem;
    box-sizing: border-box;
    .message-item{
      line-height: 2rem;
      color: #fff;
      cursor: pointer;
      margin-bottom: 2.5rem;
      .message-item-top{
        display: flex;
        justify-content: space-between;
        align-items: center;
        border-bottom: 1px solid #7886BD;
        padding-bottom: 1rem;
        margin-bottom: 1rem;
        .message-item-detail{
          color: #5E86D6;
          font-size: 1.4rem;
          cursor: pointer;
        }
      }
      .message-item-cont{
        overflow:hidden;
        text-overflow: ellipsis;
        -webkit-line-clamp: 2;
        display: -webkit-box;
        -webkit-box-orient: vertical;
      }
    }
    .message-item:last-child{
      border-bottom:none
    }
  }
  .login-dialog {
    background-color: #1A1E2D;
    max-width: 720px;
    margin: 0 auto;
    border-radius: 8px;
    .dialog-title{
      text-align: center;
      padding-top: 1.5rem;
      span{
        font-size: 2.4rem;
        font-family: Noto Sans SC-Bold, Noto Sans SC;
        font-weight: bold;
        color: #FFFFFF;
      }
    }
    .form-box{
      p{
        color: #fff;
      }
    }
  }
</style>