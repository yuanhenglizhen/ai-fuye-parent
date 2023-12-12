<template>
    <div class="cont-box">
        <div class="cont-left">
            <div class="cont-left-box">
                <div class="left">
                    <img :src="baseUrl+footerText.logo" alt="后台需配置">
                    <div class="left-subhead">
                        <div class="subhead-jf">
                            <span>{{infoData.points}}</span>
                            <i></i>
                        </div>
                        <div class="line"></div>
                        <div class="subhead-btn"><div class="btn" @click="goTopUp">充值</div></div>
                    </div>
                </div>
            </div>
            <div class="cont-center-box">
                <el-menu @select="selectMenu" :default-active="menuIndex" class="el-menu-vertical-demo"
                         background-color="#000" text-color="#A7B2C1" active-text-color="#A7B2C1">
                    <div v-for="(item,index) in menuData" :key="index">
                        <el-submenu :index="item.id" v-if="item.children && item.children.length>0">
                            <div slot="title">
                                <i class="icons" :class="item.icon" alt=""></i>
                                <span>{{item.title}}</span>
                            </div>
                            <el-menu-item :index="subItem.id" v-for="(subItem,subIndex) in item.children" :key="subIndex+'a'">
                                <div @click="goRoute(subItem)">
                                    <i class="icons" :class="subItem.icon" alt=""></i>
                                    <span>{{subItem.title}}</span>
                                </div>
                            </el-menu-item>
                        </el-submenu>
                        <el-menu-item :index="item.id" v-else>
                            <div @click="goRoute(item)">
                                <i class="icons" :class="item.icon" alt=""></i>
                                <span slot="title">{{item.title}}</span>
                            </div>
                        </el-menu-item>
                    </div>
                </el-menu>
            </div>
            <div class="cont-bottom-box">
                <div class="head-box" v-if="isInfo">
                    <el-popover placement="top-end" width="96" trigger="hover">
                        <div class="popover-box">
                            <div class="popover-item" @click="goPersonalCenter">个人中心</div>
                            <div class="popover-item" @click="logout">退出登录</div>
                        </div>
                        <div slot="reference" class="avatar-box">
                            <!--<img src="../assets/img/aiChat/icon_gerenzhongxin32@2x.png" alt="">-->
                            <div class="home-avatar-icon" :class="'homeavatar'+infoData.avatar"></div>
                        </div>
                    </el-popover>
                    <span class="head-name">{{infoData.userName}}</span>
                </div>
                <div class="message-content" v-if="isInfo">
                    <el-popover placement="top-end" width="345" trigger="hover" :disabled="messageList.length<1">
                        <div class="message-box">
                            <div class="message-item" v-for="(item,i) in messageList" :key="i+'sa'">
                                <div class="message-item-top">
                                    <div>{{item.title}}</div>
                                    <div class="message-item-detail" @click="goMessageInfo(item)">查看详情 >></div>
                                </div>
                                <p class="message-item-cont">{{item.content}}</p>
                            </div>
                        </div>
                        <div slot="reference" class="message-icon"></div>
                    </el-popover>
                </div>
                <div class="login-btn" @click="openLogins" v-else>登录/注册</div>
            </div>
        </div>
        <div class="cont-right">
            <router-view></router-view>
        </div>

        <LayOut ref="layout"></LayOut>
        <login :show="show"></login>

        <el-dialog :visible.sync="messageVisible" width="570px"
                   :close-on-click-modal="false" @close="handleClose" custom-class="message-dialog">
            <div slot="title" class="dialog-title">
                <span>{{messageObj.title}}</span>
            </div>
            <div class="form-box">
                <p>{{messageObj.content}}</p>
            </div>
        </el-dialog>
    </div>
</template>

<script>
    import login from '@/components/login/index'
    import LayOut from './layOut.vue'
    export default {
        name: "layContent",
        components: { login,LayOut },
        data(){
            return{
                menuData:[
                    {
                        title:"首页",
                        path:"/",
                        icon:"icon0 icon0_a",
                        id:"/homePage",
                    },
                    {
                        title:"AI 聊天",
                        path:"",
                        icon:"icon1 icon1_a",
                        children:[
                            {title:"Chat3.5",path:"/chat3.5",icon:"icon1_1 icon1_1_a",id:"/chat3.5"},
                            //{title:"Chat4.0",path:"/chat4",id:"/chat4"},
                            {title:"Chat4.0",path:"/test",icon:"icon1_1 icon1_1_a",id:"/chat4"},
                        ],
                        id:"1",
                    },
                    {
                        title:"AI 绘画",
                        path:"",
                        icon:"icon2 icon2_a",
                        children:[
                            {title:"Midjourney",path:"/aiPainting",icon:"icon2_1 icon2_1_a",id:"/aiPainting"},
                        ],
                        id:"2",
                    },
                    {
                        title:"绘画广场",
                        path:"",
                        icon:"icon3 icon3_a",
                        children:[
                            {title:"风格长廊",path:"/paintingStyle",icon:"icon3_1 icon3_1_a",id:"/paintingStyle"},
                            {title:"个人动态",path:"/personalDynamics",icon:"icon3_2 icon3_2_a",id:"/personalDynamics"},
                        ],
                        id:"3",
                    },
                    /*{
                        title:"操作指南",
                        path:"/instructions",
                        icon:icon3,
                        id:"/instructions",
                    },
                    {
                        title:"联系我们",
                        path:"/contactUs",
                        icon:icon4,
                        id:"/contactUs",
                    }*/
                ],
                menuIndex:"/",
                footerText:{},
                show: false,
                isInfo:false,
                infoData:{
                    userName:""
                },
                messageVisible:false,
                messageList:[],
                messageObj:{},
            }
        },
        watch:{
            "$store.state.loginShow":{
                handler:function(newVal,oldVal){
                    this.show = newVal;
                },
                immediate: true
            },
        },
        mounted() {
            this.menuIndex = this.$route.path;

            let token = window.localStorage.getItem("token") || null;
            if(token != null){
                this.isInfo = true;
                this.isMessage = true;
                this.getMessageList();
                this.getMessageDialog();
            }
            this.getInfo();
            this.getDataList();
        },
        methods:{
            getInfo(){
                this.$GET('GETUSERINFO', {},0).then(res => {
                    if (res.code == 200) {
                        window.localStorage.setItem('infoData', JSON.stringify(res.data));
                        this.infoData = res.data;
                    } else {
                        this.$store.commit('OPEN_LOGIN', true);
                        window.localStorage.clear();
                    }
                })
            },

            getDataList(){
                this.$https('configInfo', {}).then(res => {
                    if (res.code == 200) {
                        this.footerText = res.data.homeConfig;
                    } else {
                        this.$message.error('请求失败！'+res.msg)
                    }
                })
            },
            selectMenu(e){
                this.menuIndex = e;
            },
            goRoute(obj){
                if(obj.path != "" && obj.path != null){
                    this.$router.push(obj.path);
                }
            },

            goPersonalCenter(){
                this.$router.push('/personalCenter');
            },
            openLogins(){
                this.show = true;
                this.$store.commit('OPEN_LOGIN', true);
            },

            logout(){
                this.$https('SIGNOUT', {}).then(res => {
                    if (res.code == 200) {
                        window.localStorage.clear();
                        this.isInfo = false;
                        this.$router.go(0);
                    } else {
                        this.$message.error('请求失败！'+res.msg)
                    }
                })
            },
            goMessageInfo(obj){
                this.messageVisible = true;
                this.messageObj = obj;
            },
            handleClose(){
                this.messageVisible = false;
            },
            getMessageList(){
                this.$https('notifyUser', {}).then(res => {
                    if (res.code == 200) {
                        this.messageList = res.data;
                    } else {
                        this.$message.error('请求失败！'+res.msg)
                    }
                })
            },
            getMessageDialog(){
                this.$https('notifySys', {}).then(res => {
                    if (res.code == 200) {
                        if(res.data.content != null){
                            let stime = (new Date(res.data.createTime)).getTime()/1000;
                            let times = window.localStorage.getItem("stime") || null;
                            if(times == null || stime != times){
                                this.messageVisible = true;
                                this.messageObj = res.data;
                                window.localStorage.setItem("stime",stime);
                            }
                        }
                    } else {
                        this.$message.error('请求失败！'+res.msg)
                    }
                })
            },
            goTopUp(){
                this.$router.push('/topUp');
            },
        }
    }
</script>

<style lang="scss" scoped>
    .cont-box{
        width: 100%;
        height: 100%;
        display: flex;
        justify-content: flex-start;
        align-items: flex-start;
        .cont-left{
            width: 22rem;
            height: 100%;
            background: #000;
            overflow-y: auto;
            border-right: 1px solid #282828;
            .cont-left-box{
                height: 13rem;
                padding: 0 0.8rem;
                box-sizing: border-box;
                .left{
                    display: flex;
                    flex-direction: column;
                    justify-content: center;
                    align-items: center;
                    height: 100%;
                    width: 100%;
                    border-bottom: 1px solid #282828;
                    img{
                        width: 12rem;
                        height: 3.2rem;
                    }
                    .left-subhead{
                        width: 20rem;
                        height: 5rem;
                        background: #0B0F17;
                        border-radius: 7rem;
                        display: flex;
                        justify-content: space-between;
                        align-items: center;
                        margin-top: 1.5rem;
                        .subhead-jf{
                            width: 50%;
                            height: 100%;
                            display: flex;
                            justify-content: center;
                            align-items: center;
                            i{
                                width: 1.8rem;
                                height: 1.8rem;
                                background: url("../assets/img/other/icon_Goldcoin_Selected_32.png") no-repeat;
                                background-size: 100%;
                                margin-left: 0.6rem;
                            }
                            span{
                                font-size: 1.4rem;
                            }
                        }
                        .line{
                            width: 0.1rem;
                            height: 1.4rem;
                            background: #282828;
                        }
                        .subhead-btn{
                            width: 50%;
                            height: 100%;
                            display: flex;
                            justify-content:center;
                            align-items: center;
                            .btn{
                                width: 6.8rem;
                                height: 2.6rem;
                                text-align: center;
                                line-height: 2.6rem;
                                background: linear-gradient(90deg, #D950A1 0%, #6C7AFB 100%);
                                border-radius: 0.4rem;
                                color: #fff;
                                cursor: pointer;
                            }
                        }
                    }
                }
            }
            .cont-center-box{
                height: calc(100% - 20.4rem);
                padding: 1.6rem 0.8rem 1rem 0.8rem;
                box-sizing: border-box;
            }
            .cont-bottom-box{
                display: flex;
                justify-content: center;
                align-items: center;
                height: 7.4rem;
                width: 100%;
                padding: 0 1.4rem;
                box-sizing: border-box;
                .head-box{
                    display: flex;
                    justify-content: flex-start;
                    align-items: center;
                    width: calc(100% - 2rem);
                    .head-name{
                        margin-left: 0.8rem;
                        font-size: 1.4rem;
                    }
                }
                .message-content{
                    flex: 1;
                }
                .message-icon{
                    width: 3.2rem;
                    height: 3.2rem;
                    background: url("../assets/img/aiChat/icon_notice32@2x.png") no-repeat;
                    background-size: 100%;
                    cursor: pointer;
                }
                .avatar-box{
                    width: 4.2rem;
                    height: 4.2rem;
                    border-radius: 50%;
                    cursor: pointer;
                    img{
                        width: 100%;
                        height: 100%;
                    }
                }
                .login-btn{
                    text-align: center;
                    line-height: 3.8rem;
                    cursor: pointer;
                    width: 15.4rem;
                    height: 3.8rem;
                    background: linear-gradient(90deg, #D950A1 0%, #6C7AFB 100%);
                    border-radius: 16.8rem;
                }
            }
            .icons{
                display: inline-block;
                width: 1.8rem;
                height: 1.8rem;
                margin-right: 1rem;
            }
        }
        .cont-right{
            width: calc(100% - 22rem);
            height: 100%;
            overflow-y: auto;
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
    .message-dialog {
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