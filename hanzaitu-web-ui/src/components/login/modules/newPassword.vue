<template>
    <div class="login-container">
        <el-form ref="newPasswordform" :model="newPasswordform" :rules="newPasswordRules" label-width="80px" label-position="top">
            <div>
                <el-tabs v-model="activeName" @tab-click="handleTabsClick">
                    <el-tab-pane label="邮箱找回" name="0" v-if="isEmile == 0">
                        <div v-if="tabCurrent == '0'">
                            <el-form-item prop="email">
                                <el-input placeholder="请输入邮箱" v-model="newPasswordform.email" autocomplete="off"></el-input>
                            </el-form-item>
                            <el-form-item prop="password">
                                <el-input placeholder="请输入新密码" v-model="newPasswordform.password" type="password"
                                          autocomplete="off" :show-password="true"></el-input>
                            </el-form-item>
                            <el-form-item prop="checkPass">
                                <el-input placeholder="请确认新密码" v-model="newPasswordform.checkPass" type="password"
                                          autocomplete="off" :show-password="true"></el-input>
                            </el-form-item>
                            <el-form-item >
                                <graphValidateCode @success="getVerify" @refresh="onRefresh"></graphValidateCode>
                            </el-form-item>
                            <el-form-item prop="verificationCode" >
                                <div class="code-box">
                                    <div class="code-box-left">
                                        <el-input placeholder="请输入邮箱验证码" v-model="newPasswordform.verificationCode"></el-input>
                                    </div>
                                    <div class="input-btns">
                                        <el-button type="text" @click="getVerifyCode(newPasswordform.email)"
                                                   :disabled="!newPasswordform.email || disableGetVerifyCode">
                                            {{ disableGetVerifyCode ? `${countDown}s` : '获取验证码' }}
                                        </el-button>
                                    </div>
                                </div>
                            </el-form-item>
                        </div>
                    </el-tab-pane>
                    <el-tab-pane label="手机找回" name="1" v-if="isPhone == 0">
                        <div v-if="tabCurrent == '1'">
                            <el-form-item prop="phoneNumber">
                                <el-input placeholder="请输入手机号" v-model="newPasswordform.phoneNumber" autocomplete="off"></el-input>
                            </el-form-item>
                            <el-form-item prop="password">
                                <el-input placeholder="请输入新密码" v-model="newPasswordform.password" type="password"
                                          autocomplete="off" :show-password="true"></el-input>
                            </el-form-item>
                            <el-form-item prop="checkPass">
                                <el-input placeholder="请确认新密码" v-model="newPasswordform.checkPass" type="password"
                                          autocomplete="off" :show-password="true"></el-input>
                            </el-form-item>
                            <el-form-item >
                                <graphValidateCode @success="getVerify" @refresh="onRefresh"></graphValidateCode>
                            </el-form-item>
                            <el-form-item prop="verificationCode" >
                                <div class="code-box">
                                    <div class="code-box-left">
                                        <el-input placeholder="请输入验证码" v-model="newPasswordform.verificationCode"></el-input>
                                    </div>
                                    <div class="input-btns">
                                        <el-button type="text" @click="getVerifyCode(newPasswordform.phoneNumber)"
                                                   :disabled="!newPasswordform.phoneNumber || disableGetVerifyCode">
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
                <el-button @click="resetPasswordsSubmit" :disabled="loginDisabled">重置密码</el-button>
            </div>
            <div class="bottom-btn-cont" @click="cutGo('login')">返回登录</div>
        </div>
    </div>

</template>

<script>
    import graphValidateCode from "@/components/graphValidateCode/index";

    export default {
        name: "newPassword",
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
            var validatePass = (rule, value, callback) => {
                if (value === '') {
                    callback(new Error('请输入密码'));
                } else {
                    if (this.newPasswordform.checkPass !== '') {
                        this.$refs.newPasswordform.validateField('checkPass');
                    }
                    callback();
                }
            };
            var validatePass2 = (rule, value, callback) => {
                if (value === '') {
                    callback(new Error('请再次输入密码'));
                } else if (value !== this.newPasswordform.password) {
                    callback(new Error('两次输入密码不一致!'));
                } else {
                    callback();
                }
            };
            return {
                //短信
                disableGetVerifyCode: false,
                countDown: 60,
                activeName:"0",
                newPasswordform:{
                    email: '',
                    phoneNumber: '',
                    verificationCode: '',
                    password: '',
                    checkPass: '',
                },
                newPasswordRules:{
                    email:[
                        { required: true, message: '请输入邮箱地址', trigger: 'blur' },
                        { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
                    ],
                    phoneNumber: [
                        { required: true, message: '请输入电话号码', trigger: 'blur' },
                    ],
                    verificationCode: [
                        { required: true, message: '请输入验证码', trigger: 'blur' },
                    ],
                    password: [
                        { validator: validatePass, trigger: 'blur' }
                    ],
                    checkPass: [
                        { validator: validatePass2, trigger: 'blur' }
                    ],
                },

                loginDisabled:true,
                tabCurrent:'0',
            }
        },
        mounted() {

        },
        methods: {
            resetPasswordsSubmit() {
                this.$refs.newPasswordform.validate(valid => {
                    if (valid) {
                        let url = "";
                        if(this.tabCurrent == 0){
                            url = 'resetPasswordEmail';
                        }else{
                            url = 'RESETPASSWORD';
                        }
                        this.$https(url, this.newPasswordform).then(res => {
                            if (res.code != 200) {
                                this.$message.error(res.msg);
                            } else {
                                this.$message.success('修改成功！')
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
                    chenckMoveid:this.newPasswordform.chenckMoveid,
                    removing:this.newPasswordform.removing,
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
                    this.newPasswordform.chenckMoveid = obj.chenckMoveid;
                    this.newPasswordform.removing = obj.removing;
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
    .bottom-btn-cont{
        text-align: left;
        margin-top: 0.8rem;
        cursor: pointer;
        font-size: 1.6rem;
        color: #5E86D6;
        span{
            color: #5E86D6;
            cursor: pointer;

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