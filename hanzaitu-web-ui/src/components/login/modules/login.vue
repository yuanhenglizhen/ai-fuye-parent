<template>
    <div class="login-container">
        <el-form ref="loginForm" :model="loginForm" :rules="loginRules" label-width="80px" size="medium" label-position="top">
            <div>
                <el-tabs v-model="activeName" @tab-click="handleTabsClick">
                    <el-tab-pane label="账号登录" name="0" v-if="isEmile == 0">
                        <div v-if="tabCurrent == '0'">
                            <el-form-item prop="username" >
                                <el-input v-model="loginForm.username" autocomplete="off" placeholder="请输入邮箱或手机号码"></el-input>
                            </el-form-item>
                            <el-form-item prop="password" >
                                <el-input v-model="loginForm.password" type="password" autocomplete="off"
                                          placeholder="请输入密码" :show-password="true"></el-input>
                            </el-form-item>
                            <el-form-item >
                                <graphValidateCode @success="getVerify" @refresh="onRefresh"></graphValidateCode>
                            </el-form-item>
                        </div>
                    </el-tab-pane>
                    <el-tab-pane label="手机登录" name="1" v-if="isPhone == 0">
                        <div v-if="tabCurrent == '1'">
                            <el-form-item prop="phoneNumber">
                                <el-input placeholder="请输入手机号码" v-model="loginForm.phoneNumber" autocomplete="off"></el-input>
                            </el-form-item>
                            <el-form-item >
                                <graphValidateCode @success="getVerify" @refresh="onRefresh"></graphValidateCode>
                            </el-form-item>
                            <el-form-item prop="verificationCode" >
                                <div class="code-box">
                                    <div class="code-box-left">
                                        <el-input placeholder="请输入短信验证码" v-model="loginForm.verificationCode"></el-input>
                                    </div>
                                    <div class="input-btns">
                                        <el-button type="text" @click="getVerifyCode(loginForm.phoneNumber)"
                                                   :disabled="!loginForm.phoneNumber || disableGetVerifyCode || loginDisabled">
                                            {{ disableGetVerifyCode ? `${countDown}s` : '获取验证码' }}
                                        </el-button>
                                    </div>
                                </div>
                            </el-form-item>
                        </div>
                    </el-tab-pane>
                </el-tabs>
            </div>
        </el-form>
        <div>
            <div class="login-btn" style="padding-top: 1rem">
                <div class="tag-text">点击「登录」表示已阅读并同意 <span>《用户授权与隐私保护协议》</span></div>
                <el-button @click="loginSubmit" :disabled="loginDisabled">登录</el-button>
            </div>
            <div class="bottom-box">
                <div class="zczh-text" @click="cutGo('register')">注册账号</div>
                <div class="wjmm-text" @click="cutGo('newPassword')">找回密码?</div>
            </div>
        </div>

    </div>

</template>

<script>
    import graphValidateCode from '@/components/graphValidateCode/index'
    export default {
        name: "login",
        components:{graphValidateCode},
        props:{
            isEmile:{
                type:Number,
                default:0
            },
            isPhone:{
                type:Number,
                default:0
            },
        },
        data() {
            return {
                //短信
                disableGetVerifyCode: false,
                countDown: 60,
                activeName:"0",
                loginForm: {
                    username: '',
                    password: '',
                    phoneNumber: null,
                    verificationCode: '',
                    chenckMoveid: null,
                    removing: null,
                },
                loginRules:{
                    username: [
                        { required: true, message: '请输入用户名', trigger: 'blur' },
                    ],
                    password: [
                        { required: true, message: '请输入密码', trigger: 'blur' },
                    ],
                    phoneNumber: [
                        { required: true, message: '请输入电话号码', trigger: 'blur' },
                    ],
                    verificationCode: [
                        { required: true, message: '请输入验证码', trigger: 'blur' },
                    ],
                },
                loginDisabled:true,
                tabCurrent:'0',
            }
        },
        mounted() {

        },
        methods: {
            loginSubmit(){
                this.$refs.loginForm.validate(valid => {
                    if (valid) {
                        let url = "";
                        if(this.tabCurrent == 0){
                            url = 'LOGIN';
                        }else{
                            url = 'loginPhone';
                        }
                        this.$https(url, this.loginForm).then(res => {
                            if (res.code != 200) {
                                this.$message.error(res.msg);
                            } else {
                                window.localStorage.setItem('userInfo', JSON.stringify(res.data))
                                window.localStorage.setItem('token', res.data.accessToken)
                                this.$message.success('登录成功！')
                                this.$store.commit('OPEN_LOGIN', false);
                                this.$router.go(0);
                            }
                        })
                    }
                })
            },
            cutGo(str){
                this.$emit("cutGo",str);
            },

            getVerifyCode(phone) {
                // 处理获取验证码逻辑
                this.disableGetVerifyCode = true
                const interval = setInterval(() => {
                    this.countDown--
                    if (this.countDown <= 0) {
                        clearInterval(interval)
                        this.countDown = 60
                        this.disableGetVerifyCode = false
                    }
                }, 1000)
                this.$https('SENDREGISTERCODE', {
                    param:phone,
                    chenckMoveid:this.loginForm.chenckMoveid,
                    removing:this.loginForm.removing,
                }).then(res => {
                    if (res.code != 200) {
                        this.$message.error(res.msg);
                    } else {
                        this.$message.success('发送成功！')
                    }
                })
            },
            getVerify(e){
                if(e != null){
                    let obj = JSON.parse(e);
                    this.loginForm.chenckMoveid = obj.chenckMoveid;
                    this.loginForm.removing = obj.removing;
                    this.loginDisabled = false;
                }else{
                    this.loginDisabled = true;
                }
            },
            onRefresh(e){
                this.loginDisabled = true;
            },
            handleTabsClick(e) {
                this.tabCurrent = e.index;
            },
        }
    }
</script>

<style lang="scss" scoped>
    .form-box{
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
    .bottom-box{
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-top: 0.8rem;
        .zczh-text{
            color: #FFFFFF;
            cursor: pointer;
            font-size: 1.6rem;
        }
        .wjmm-text{
            color:#7079F9;
            cursor: pointer;
            font-size: 1.6rem;
        }
    }

    .code-box{
        display: flex;
        justify-content: space-between;
        align-items: center;
        box-sizing: border-box;
        .code-box-left{
            flex: 1;
        }
        .input-btns{
            width: 16rem;
            height: 4rem;
            display: flex;
            justify-content: center;
            align-items: center;
            background: #131313;
            margin-left: 1.2rem;
            button{
                width: 100%;
                height: 100%;
                background: linear-gradient(90deg, #D950A1 0%, #6C7AFB 100%);
                border-radius: 6px;
            }
        }
    }
    .tag-text{
        margin-bottom: 0.6rem;
        color: #fff;
        span{
            color: #7079F9;
            cursor: pointer;
        }
    }

</style>