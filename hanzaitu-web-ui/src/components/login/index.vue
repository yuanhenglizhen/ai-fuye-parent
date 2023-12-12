<template>
    <el-dialog :visible.sync="dialogVisible" :show-close="false" @close="handleClose" custom-class="login-dialog"  width="110rem">
        <div class="dlzc-box">
            <div class="dlzc-banner">
                <el-image style="width: 100%;border-radius: 3.8rem 0 0 3.8rem" fit="scale-down" :src="footerText.loginExhibit" ></el-image>
            </div>
            <div class="dlzc-right">
                <div slot="title" class="dialog-title">
                    <div class="tit-box" v-if="showTitle == 'login'">
                        <span>登录</span>
                        <img :src="baseUrl+footerText.logo" alt="后台需配置">
                    </div>
                    <div class="tit-box" v-if="showTitle == 'register'">
                        <span>注册</span>
                        <img :src="baseUrl+footerText.logo" alt="后台需配置">
                    </div>
                    <div class="tit-box" v-if="showTitle == 'newPassword'">
                        <span>找回密码</span>
                        <img :src="baseUrl+footerText.logo" alt="后台需配置">
                    </div>
                </div>
                <div class="form-box">
                    <div v-if="showTitle == 'login'">
                        <login @cutGo="cutLogin" :isEmile="emailPhoneFlag.email" :isPhone="emailPhoneFlag.phone"></login>
                    </div>
                    <div v-if="showTitle == 'register'">
                        <register @cutGo="cutLogin" :isEmile="emailPhoneFlag.email" :isPhone="emailPhoneFlag.phone"></register>
                    </div>
                    <div v-if="showTitle == 'newPassword'">
                        <newPassword @cutGo="cutLogin" :isEmile="emailPhoneFlag.email" :isPhone="emailPhoneFlag.phone"></newPassword>
                    </div>
                </div>
            </div>
        </div>

    </el-dialog>
</template>

<script>
    import login from './modules/login'
    import register from './modules/register'
    import newPassword from './modules/newPassword'
    import loginImg from '@/assets/img/other/LoginRegistration_picture.png'
    export default {
        components:{
            login,
            register,
            newPassword
        },
        props:{
            show:{
                type:Boolean,
                default:false
            }
        },
        data() {
            return {
                dialogVisible: false,
                showTitle:null,
                loginImg:loginImg,
                footerText:{},
                emailPhoneFlag:{
                    email:0,
                    phone:0,
                },
            }
        },
        watch:{
            show(val){
                this.dialogVisible = val;
                if(val == true){
                    this.showTitle = "login"
                    this.getDataList();
                }
            }
        },
        mounted() {

        },
        methods: {
            handleClose() {
                this.$store.commit('OPEN_LOGIN', false);
            },
            cutLogin(e){
                this.showTitle = e;
            },
            getDataList(){
                this.$https('configInfo', {}).then(res => {
                    if (res.code == 200) {
                        this.footerText = res.data.homeConfig;
                        if(res.data.homeConfig.loginRegist){
                            this.emailPhoneFlag = res.data.homeConfig.loginRegist;
                        }
                    } else {
                        this.$message.error('请求失败！'+res.msg)
                    }
                })
            },
            cutDialog(e){

            },
        },
    }
</script>

<style lang="scss" scoped>
    .login-dialog {
        background-color: #1A1E2D;
        max-width: 720px;

        border-radius: 8px;
        .dialog-title{
            text-align: center;
            .tit-box{
                display: flex;
                justify-content: flex-start;
                align-items: center;
                span{
                    font-size: 3rem;
                    font-family: Noto Sans SC-Bold, Noto Sans SC;
                    font-weight: bold;
                    color: #FFFFFF;
                    margin-right: 1.5rem;
                }
                img{
                    width: 14rem;
                    height: 4rem;
                }
            }

        }
        .dlzc-box{
            display: flex;
            justify-content: flex-start;
            align-items: center;
            width: 100%;
            height: 72rem;
            .dlzc-banner{
                width: 55rem;
                height: 100%;
            }
            .dlzc-right{
                flex: 1;
                padding:0 6.1rem;
            }
        }
        .form-box{
            margin-top: 1rem;
            .bottom-box{
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-top: -2rem;
                .zczh-text{
                    color: #FFFFFF;
                    cursor: pointer;
                    font-size: 2rem;
                }
                .wjmm-text{
                    color:#5E86D6;
                    cursor: pointer;
                    font-size: 2rem;
                }
            }
            .input-btns{
                padding:0 1rem;
            }
            .bottom-btn-cont{
                text-align: center;
                color: #fff;
                margin-top: -2rem;
                cursor: pointer;
                span{
                    color: #5E86D6;
                    cursor: pointer;
                }
            }
        }
    }
</style>