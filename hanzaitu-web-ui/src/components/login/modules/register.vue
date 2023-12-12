<template>
    <div class="login-container">
        <el-form ref="registerForm" :model="registerForm" :rules="registerRules" label-width="80px" label-position="top">
            <div>
                <el-tabs v-model="activeName" @tab-click="handleTabsClick">
                    <el-tab-pane label="邮箱注册" name="0" v-if="isEmile == 0">
                        <div v-if="tabCurrent == '0'">
                            <el-form-item prop="email">
                                <el-input placeholder="请输入注册邮箱" v-model="registerForm.email" autocomplete="off"></el-input>
                            </el-form-item>
                            <el-form-item prop="password">
                                <el-input placeholder="请输入密码" v-model="registerForm.password" type="password"
                                          autocomplete="off" :show-password="true"></el-input>
                            </el-form-item>
                            <el-form-item >
                                <graphValidateCode @success="getVerify" @refresh="onRefresh"></graphValidateCode>
                            </el-form-item>
                            <el-form-item prop="verificationCode" >
                                <div class="code-box">
                                    <div class="code-box-left">
                                        <el-input placeholder="请输入邮箱验证码" v-model="registerForm.verificationCode"></el-input>
                                    </div>
                                    <div class="input-btns">
                                        <el-button type="text" @click="getVerifyCode(registerForm.email)"
                                                   :disabled="!registerForm.email || disableGetVerifyCode || loginDisabled">
                                            {{ disableGetVerifyCode ? `${countDown}s` : '获取验证码' }}
                                        </el-button>
                                    </div>
                                </div>
                            </el-form-item>
                            <el-form-item prop="inviteCode">
                                <el-input placeholder="邀请码，非必填项，没有可不填" v-model="registerForm.inviteCode"></el-input>
                            </el-form-item>
                        </div>
                    </el-tab-pane>
                    <el-tab-pane label="手机注册" name="1" v-if="isPhone == 0">
                        <div v-if="tabCurrent == '1'">
                            <el-form-item prop="phoneNumber">
                                <el-input placeholder="请输入手机号" v-model="registerForm.phoneNumber" autocomplete="off"></el-input>
                            </el-form-item>
                            <el-form-item prop="password">
                                <el-input placeholder="请输入密码" v-model="registerForm.password" type="password"
                                          autocomplete="off" :show-password="true"></el-input>
                            </el-form-item>
                            <el-form-item >
                                <graphValidateCode @success="getVerify" @refresh="onRefresh"></graphValidateCode>
                            </el-form-item>
                            <el-form-item prop="verificationCode" >
                                <div class="code-box">
                                    <div class="code-box-left">
                                        <el-input placeholder="请输入短信验证码" v-model="registerForm.verificationCode"></el-input>
                                    </div>
                                    <div class="input-btns">
                                        <el-button type="text" @click="getVerifyCode(registerForm.phoneNumber)"
                                                   :disabled="!registerForm.phoneNumber || disableGetVerifyCode || loginDisabled">
                                            {{ disableGetVerifyCode ? `${countDown}s` : '获取验证码' }}
                                        </el-button>
                                    </div>
                                </div>
                            </el-form-item>
                            <el-form-item prop="inviteCode">
                                <el-input placeholder="邀请码，非必填项，没有可不填" v-model="registerForm.inviteCode"></el-input>
                            </el-form-item>
                        </div>
                    </el-tab-pane>
                </el-tabs>
            </div>
        </el-form>

        <div>
            <div class="login-btn" >
                <el-button @click="registerSubmit" :disabled="loginDisabled">注册</el-button>
            </div>
            <div class="bottom-box">
                <div class="zczh-text" @click="cutGo('login')">返回登录</div>
                <div class="wjmm-text" @click="cutGo('newPassword')">找回密码?</div>
            </div>
        </div>
    </div>
</template>

<script>
    import graphValidateCode from "@/components/graphValidateCode/index";
    export default {
        name: "register",
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
        data(){
          return {
              //短信
              disableGetVerifyCode: false,
              countDown: 60,
              activeName:"0",
              registerForm:{
                  inviteCode: '',
                  phoneNumber: '',
                  verificationCode: '',
                  password: '',
                  email:'',
              },
              registerRules:{
                  phoneNumber: [
                      { required: true, message: '请输入电话号码', trigger: 'blur' },
                  ],
                  verificationCode: [
                      { required: true, message: '请输入验证码', trigger: 'blur' },
                  ],
                  password: [
                      { required: true, message: '请输入密码', trigger: 'blur' },
                  ],
                  email:[
                      { required: true, message: '请输入邮箱地址', trigger: 'blur' },
                      { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
                  ],
              },

              loginDisabled:true,
              tabCurrent:'0',
          }
        },
        mounted() {

        },
        methods: {
            registerSubmit(){
                this.$refs.registerForm.validate(valid => {
                    if (valid) {
                        let url = "";
                        if(this.tabCurrent == 0){
                            url = 'registerMail';
                        }else{
                            url = 'REGISTER';
                        }
                        this.$https(url, this.registerForm).then(res => {
                            if (res.code != 200) {
                                this.$message.error(res.msg);
                            } else {
                                this.$message.success('注册成功！')
                                this.cutGo('login');
                            }
                        })
                    }
                })
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
                let url = "";
                if(this.tabCurrent == 0){
                    url = 'sendMailRegisterCode';
                }else{
                    url = 'SENDREGISTERCODE';
                }
                this.$https(url, {
                    param:phone,
                    chenckMoveid:this.registerForm.chenckMoveid,
                    removing:this.registerForm.removing,
                }).then(res => {
                    if (res.code != 200) {
                        this.$message.error(res.msg);
                    } else {
                        this.$message.success('发送成功！')
                    }
                })
            },
            cutGo(str){
                this.$emit("cutGo",str);
            },
            getVerify(e){
                if(e != null){
                    let obj = JSON.parse(e);
                    this.registerForm.chenckMoveid = obj.chenckMoveid;
                    this.registerForm.removing = obj.removing;
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
            }

        }
    }
</script>

<style lang="scss" scoped>
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
                    color:#5E86D6;
                    cursor: pointer;
                    font-size: 1.6rem;
                }
            }
            .bottom-btn-cont{
                text-align: center;
                color: #fff;
                margin-top: 1.6rem;
                cursor: pointer;
                span{
                    color: #5E86D6;
                    cursor: pointer;
                }
            }
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