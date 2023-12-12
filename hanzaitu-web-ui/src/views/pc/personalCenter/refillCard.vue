<template>
    <el-dialog :visible.sync="dialogVisible" :title="title" :width="width"
               :close-on-click-modal="false" @close="handleClose" custom-class="personal-dialog">
        <div slot="title" class="dialog-title">
            <span>卡密充值</span>
        </div>
        <div class="form-box">
            <el-form ref="form" :model="form" :rules="rules" label-width="80px" label-position="top">
                <el-form-item label="卡密充值" prop="cipherCode">
                    <el-input v-model="form.cipherCode" placeholder="请输入你的充值卡密"></el-input>
                </el-form-item>
                <el-form-item>
                    <div class="login-btn" style="padding-top: 3.4rem">
                        <el-button @click="submit">充值</el-button>
                    </div>
                </el-form-item>
            </el-form>
        </div>
    </el-dialog>
</template>

<script>
    export default {
        name: "refillCard",
        props: {
            title: {
                type: String,
                default: '卡密充值'
            },
            width: {
                type: String,
                default: '30%'
            },
            show:{
                type:Boolean,
                default:false
            }
        },
        data() {
            return {
                dialogVisible: false,
                form: {
                    cipherCode: ''
                },
                rules: {
                    cipherCode: [
                        { required: true, message: '请输入卡密', trigger: 'blur' }
                    ]
                }
            }
        },
        watch:{
            show(val){
                this.dialogVisible = val;
            }
        },
        mounted() {

        },
        methods: {
            handleClose() {
                this.$emit('close', false);
            },
            submit() {
                this.$refs.form.validate(valid => {
                    if (valid) {
                        // TODO: 发送充值请求
                        this.$https('getCipherCode', this.form).then(res => {
                            if (res.code != 200) {
                                this.$message.error(res.msg);
                            } else {
                                this.$message.success('修改成功！');
                                this.dialogVisible = false;
                                this.$router.go(0);
                            }
                        })
                    }
                })
            }
        }
    }
</script>

<style lang="scss" scoped>
    .personal-dialog {
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
                margin-top: -2rem;
                .zczh-text{
                    color: #FFFFFF;
                    cursor: pointer;
                }
                .wjmm-text{
                    color:#5E86D6;
                    cursor: pointer;
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