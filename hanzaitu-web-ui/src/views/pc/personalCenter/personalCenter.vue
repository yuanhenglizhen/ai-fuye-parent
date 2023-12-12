<template>
    <div class="personal-box">
        <div class="title">
            <div>个人信息</div>
            <div class="black-btn" @click="goBlack">返回上级 >></div>
        </div>
        <div class="personal-info">
            <div class="avatar-box">
                <el-popover placement="bottom" width="580" trigger="click">
                    <div class="avatar-cont">
                        <div class="avatar-item" @click="selectAvatar(item,index)" v-for="(item,index) in avatarList" :key="index">
                            <div class="avatar-icon" :class="'avatar'+index"></div>
                        </div>
                    </div>
                    <div slot="reference" class="avatar-btn-box">
                        <div class="avatar-icon" :class="'avatar'+avatarUrl"></div>
                    </div>
                </el-popover>
            </div>

            <div class="info-box">
                <div class="info-box-left">
                    <div class="info-box-item">
                        <span class="item-label">账号：</span>
                        <span class="text">{{infoData.userName}}</span>
                    </div>
                    <div class="info-box-item mt30 mb30">
                        <span class="item-label">手机号码：</span>
                        <span class="text">{{infoData.phoneNumber}}</span>
                        <span class="item-btn" @click="openEditPhone">修改</span>
                    </div>
                    <div class="info-box-item">
                        <span class="item-label">登录密码：</span>
                        <span class="text">已设置</span>
                        <span class="item-btn" @click="updatePassword">修改</span>
                    </div>
                </div>
                <div class="info-box-right ml60">
                    <div class="info-box-item">
                        <span class="item-label">昵称：</span>
                        <span class="text">{{infoData.nickName}}</span>
                        <span class="item-btn" @click="openEditUserName">修改</span>
                    </div>

                    <div class="info-box-item mt30 mb30">
                        <span class="item-label">我的邀请码：</span>
                        <span class="text">{{infoData.inviteCode}}</span>
                        <!--<span class="item-label">邀请链接：</span>{{infoData.inviteUrl}}
                        <span class="item-btn" v-clipboard:copy="infoData.inviteUrl" v-clipboard:success="onCopy">复制链接</span>-->
                    </div>
                    <div class="info-box-item">
                        <span class="item-label">邀请人数：</span>
                        <span class="text">{{infoData.inviteCount}}人</span>
                    </div>
                </div>
                <div class="info-box-right">
                    <!--<div class="info-box-item">
                        <span>我的邀请码：{{infoData.inviteCode}}</span>
                        <span></span>
                    </div>-->
                    <div class="info-box-item">
                        <span class="item-label">我的积分：</span>
                        <span class="text">{{infoData.points}}</span>
                        <span class="item-btn" @click="goTopUp">充值</span>
                        <span class="item-btn" @click="openRefillCard">卡密兑换</span>
                    </div>
                </div>
            </div>

        </div>
        <div class="detail-box">
            <div class="detail-tab-box">
                <div class="detail-tab">
                    <div class="tab-btns" :class="{ 'tab-btns-active': tabActive == 0}" @click="clickTab(0)">积分消费明细清单</div>
                    <div class="tab-btns" :class="{ 'tab-btns-active': tabActive == 1}" @click="clickTab(1)">邀请记录明细</div>
                </div>
                <div class="detail-right" v-if="tabActive == 0">
                    <div class="text">按时间筛选</div>
                    <div class="time-box">
                        <el-date-picker
                                v-model="walletRecordParams.dateRange"
                                style="width:100%"
                                size="small"
                                type="daterange"
                                popper-class="detail-time"
                                range-separator="-"
                                start-placeholder="开始日期"
                                end-placeholder="结束日期"
                                value-format="yyyy-MM-dd"
                                @change="walletRecordMessageTime"
                        ></el-date-picker>
                    </div>
                    <div class="btn btn-bule" @click="resetSubmit(0)">
                        <i class="el-icon-search" style="margin-right: 0.5rem"></i>
                        重置
                    </div>
                    <div class="btn" @click="selectSubmit(0)">
                        <i class="el-icon-search" style="margin-right: 0.5rem"></i>
                        查询
                    </div>
                </div>
                <div class="detail-right" v-if="tabActive == 1">
                    <div class="text">按时间筛选</div>
                    <div class="time-box">
                        <el-date-picker
                                v-model="inviteParams.dateRange"
                                style="width:100%"
                                size="small"
                                type="daterange"
                                popper-class="detail-time"
                                range-separator="-"
                                start-placeholder="开始日期"
                                end-placeholder="结束日期"
                                value-format="yyyy-MM-dd"
                                @change="inviteMessageTime"
                        ></el-date-picker>
                    </div>
                    <div class="btn btn-bule" @click="resetSubmit(0)">
                        <i class="el-icon-search" style="margin-right: 0.5rem"></i>
                        重置
                    </div>
                    <div class="btn" @click="selectSubmit(1)">
                        <i class="el-icon-search" style="margin-right: 0.5rem"></i>
                        查询
                    </div>
                </div>
            </div>
            <div class="detail-table" v-if="tabActive == 0">
                <div class="detail-table-cont">
                    <el-table v-loading="loading" :data="walletRecordData" :max-height="380">
                        <el-table-column label="消费内容" prop="recordTypeName"/>
                        <el-table-column label="消费时间" prop="createTime" />
                        <el-table-column label="积分变动" prop="points" />
                    </el-table>
                </div>
                <div class="table-cont-pages">
                    <el-pagination
                            v-show="totalWalletRecord>0"
                            background
                            layout="prev, pager, next"
                            :total="totalWalletRecord"
                            :currentPage.sync="walletRecordParams.pageNo"
                            :pageSize.sync="walletRecordParams.pageSize"
                            @current-change="getListUserWalletRecord"
                    ></el-pagination>
                </div>
            </div>
            <div class="detail-table" v-if="tabActive == 1">
                <div class="detail-table-cont">
                    <el-table v-loading="loading" :data="listInvite" :max-height="380">
                        <el-table-column label="用户名" prop="userName"/>
                        <el-table-column label="注册时间" prop="createTime" />
                        <el-table-column label="获得积分" prop="points" />
                    </el-table>
                </div>
                <div class="table-cont-pages">
                    <el-pagination
                            v-show="totalInvite>0"
                            background
                            layout="prev, pager, next"
                            :total="totalInvite"
                            :currentPage.sync="inviteParams.pageNo"
                            :pageSize.sync="inviteParams.pageSize"
                            @current-change="listInviteUser"
                    ></el-pagination>
                </div>
            </div>
        </div>
        <refillCard :show="openRefillFlag" @close="closeRefil"></refillCard>

        <el-dialog :visible.sync="editPWDVisible" :close-on-click-modal="false" @close="editPWDVisible = false" custom-class="personal-dialog" width="56.8rem">
            <div slot="title" class="dialog-title">
                <div class="title-box">更改密码</div>
                <div class="subHead-box">更改密码后无法使用旧密码进行登录</div>
            </div>
            <div>
                <el-form ref="newPasswordform" :model="newPasswordform" :rules="newPasswordRules" label-width="80px" label-position="top">
                    <el-form-item label="新密码" prop="password">
                        <el-input v-model="newPasswordform.password" type="password" autocomplete="off"></el-input>
                    </el-form-item>
                    <el-form-item label="验证码" prop="verificationCode">
                        <el-input v-model="newPasswordform.verificationCode">
                            <div class="input-btns" slot="append">
                                <el-button type="text" @click="getVerifyCode(newPasswordform.phoneNumber)"
                                           :disabled="!newPasswordform.phoneNumber || disableGetVerifyCode">
                                    {{ disableGetVerifyCode ? `${countDown}s` : '获取验证码' }}
                                </el-button>
                            </div>
                        </el-input>
                    </el-form-item>
                    <el-form-item>
                        <div class="login-btn" style="padding-top: 3.4rem">
                            <el-button @click="resetPasswordsSubmit">完成</el-button>
                        </div>
                    </el-form-item>
                </el-form>
            </div>
        </el-dialog>
        <el-dialog
                :visible.sync="editUserNameVisible"
                :close-on-click-modal="false"
                @close="editUserNameVisible = false"
                custom-class="personal-dialog" width="56.8rem">
            <div slot="title" class="dialog-title">
                <div class="title-box">修改昵称</div>
            </div>
            <div>
                <el-form ref="userNameForm" :model="userNameForm" :rules="userNameRules" label-width="80px" label-position="top">
                    <el-form-item label="昵称" prop="nickName">
                        <el-input v-model="userNameForm.nickName" autocomplete="off"></el-input>
                    </el-form-item>
                    <el-form-item>
                        <div class="login-btn" style="padding-top: 3.4rem">
                            <el-button @click="editUserNameSubmit">提交</el-button>
                        </div>
                    </el-form-item>
                </el-form>
            </div>
        </el-dialog>
        <el-dialog
                :visible.sync="editPhoneVisible"
                :close-on-click-modal="false"
                @close="editPhoneVisible = false"
                custom-class="personal-dialog" width="56.8rem">
            <div slot="title" class="dialog-title">
                <div class="title-box">修改电话号码</div>
            </div>
            <div>
                <el-form ref="userNameForm" :model="phoneForm" :rules="phoneRules" label-width="80px" label-position="top">
                    <el-form-item label="电话号码" prop="phoneNumber">
                        <el-input v-model="phoneForm.phoneNumber" autocomplete="off"></el-input>
                    </el-form-item>
                    <el-form-item label="验证码" prop="verificationCode">
                        <el-input v-model="phoneForm.verificationCode">
                            <div class="input-btns" slot="append">
                                <el-button type="text" @click="getVerifyCode(phoneForm.phoneNumber)"
                                           :disabled="!phoneForm.phoneNumber || disableGetVerifyCode">
                                    {{ disableGetVerifyCode ? `${countDown}s` : '获取验证码' }}
                                </el-button>
                            </div>
                        </el-input>
                    </el-form-item>
                    <el-form-item>
                        <div class="login-btn" style="padding-top: 3.4rem">
                            <el-button @click="editPhoneSubmit">提交</el-button>
                        </div>
                    </el-form-item>
                </el-form>
            </div>
        </el-dialog>
    </div>
</template>

<script>
    import refillCard from "@/views/pc/personalCenter/refillCard"
    export default {
        name: "personalCenter",
        components:{refillCard},
        data(){
            return {
                tabActive:0,
                avatarList:20,
                avatarUrl:0,
                infoData:{
                    username:"",
                    phoneNumber:"",
                },
                openRefillFlag:false,

                countDown: 60,
                editPWDVisible:false,
                disableGetVerifyCode: false,
                newPasswordform:{
                    phoneNumber: '',
                    verificationCode: '',
                    password: '',
                },
                newPasswordRules:{
                    phoneNumber: [
                        { required: true, message: '请输入电话号码', trigger: 'blur' },
                    ],
                    verificationCode: [
                        { required: true, message: '请输入验证码', trigger: 'blur' },
                    ],
                    password: [
                        { required: true, message: '请输入密码', trigger: 'blur' },
                    ],
                },
                editUserNameVisible:false,
                userNameForm:{
                    nickName:""
                },
                userNameRules:{
                    nickName: [
                        { required: true, message: '请输入昵称', trigger: 'blur' },
                    ],
                },
                editPhoneVisible:false,
                phoneForm:{
                    phoneNumber:"",
                    verificationCode:'',
                },
                phoneRules:{
                    phoneNumber: [
                        { required: true, message: '请输入电话号码', trigger: 'blur' },
                    ],
                    verificationCode: [
                        { required: true, message: '请输入验证码', trigger: 'blur' },
                    ],
                },
                listInvite:[],
                totalInvite:0,
                inviteParams: {
                    pageNo: 1,
                    pageSize: 10,
                    startDate:"",
                    endDate:"",
                },
                walletRecordData:[],
                totalWalletRecord:0,
                walletRecordParams: {
                    pageNo: 1,
                    pageSize: 10,
                    startDate:"",
                    endDate:"",
                },
                dateRange:"",
                loading: true,
            }
        },
        mounted() {
            let token = window.localStorage.getItem("token") || null;
            if(token != null){
                this.getInfo();
                this.listInviteUser();
                this.getListUserWalletRecord();
            }

        },
        methods:{
            getInfo(){
                this.$GET('GETUSERINFO', {},0).then(res => {
                    if (res.code == 200) {
                        window.localStorage.setItem('infoData', JSON.stringify(res.data));
                        this.infoData = res.data;
                        this.avatarUrl = parseInt(res.data.avatar);
                        this.newPasswordform.phoneNumber = this.infoData.phoneNumber;
                    } else {
                        this.$store.commit('OPEN_LOGIN', true);
                        window.localStorage.clear();
                    }
                })
            },
            //获取邀请信息
            listInviteUser(){
                this.loading = true;
                this.$POSTPAGE('/customer/listInviteUser',this.inviteParams).then(res => {
                    if (res.code == 200) {
                        this.listInvite = res.data.list;
                        this.totalInvite = res.data.total;
                        this.loading = false;
                    } else {
                        this.$message.error(res.msg);
                    }
                })
            },
            //获取用户钱包明细记录
            getListUserWalletRecord(){
                this.loading = true;
                this.$POSTPAGE('/financePay/listUserWalletRecord', this.walletRecordParams).then(res => {
                    if (res.code == 200) {
                        this.walletRecordData = res.data.list;
                        this.totalWalletRecord = res.data.total;
                        this.loading = false;
                    } else {
                        this.$message.error(res.msg);
                    }
                })
            },
            clickTab(num){
              this.tabActive = num;
            },
            goBlack(){
                this.$router.push('/');
            },
            selectAvatar(imgUrl,i){
                this.$https('editAvatar',{
                    avatar:parseInt(i)
                }).then(res => {
                    if (res.code == 200) {
                        this.avatarUrl = parseInt(i);
                        this.getInfo();
                    } else {
                        this.$message.error(res.msg);

                    }
                })
            },
            openRefillCard(){
                this.openRefillFlag = true;
            },
            closeRefil(e){
                this.openRefillFlag = e;
            },
            goTopUp(){
                this.$router.push('/topUp');
            },
            updatePassword(){
                this.editPWDVisible = true;
            },
            resetPasswordsSubmit(){
                this.newPasswordform.phoneNumber = this.infoData.phoneNumber;
                this.$refs.newPasswordform.validate(valid => {
                    if (valid) {
                        this.$https('RESETPASSWORD', this.newPasswordform).then(res => {
                            if (res.code != 200) {
                                this.$message.error(res.msg);
                            } else {
                                this.$message.success('修改成功！');
                                this.editPWDVisible = false;
                                this.getInfo();
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
                this.$https('sendResetRegisterCode', {
                    param:phone
                }).then(res => {
                    if (res.code != 200) {
                        this.$message.error(res.msg);
                    } else {
                        this.$message.success('发送成功！')
                    }
                })
            },
            openEditUserName(){
                this.editUserNameVisible = true;
            },
            editUserNameSubmit(){
                this.$https('EDITUSERNAME',this.userNameForm).then(res => {
                    if (res.code == 200) {
                        this.$message.success('修改成功！');
                        this.editUserNameVisible = false;
                        this.getInfo();
                    } else {
                        this.$message.error(res.msg);

                    }
                })
            },
            openEditPhone(){
                this.editPhoneVisible = true;
            },
            editPhoneSubmit(){
                this.$https('EDITPHONENUMBER',this.phoneForm).then(res => {
                    if (res.code == 200) {
                        this.$message.success('修改成功！');
                        this.editPhoneVisible = false;
                        this.getInfo();
                    } else {
                        this.$message.error(res.msg);

                    }
                })
            },
            // 复制成功
            onCopy(e){
                this.$message({
                    message:'复制成功！',
                    type:'success'
                })
            },
            walletRecordMessageTime(e){
                this.walletRecordParams.startTime = e[0];
                this.walletRecordParams.endTime = e[1];
            },
            inviteMessageTime(e){
                this.inviteParams.startTime = e[0];
                this.inviteParams.endTime = e[1];
            },
            selectSubmit(num){
                if(num == 0){
                    this.getListUserWalletRecord();
                }else if(num == 1){
                    this.listInviteUser();
                }
            },
            resetSubmit(num){
                if(num == 0){
                    this.walletRecordParams.startTime = "";
                    this.walletRecordParams.endTime = "";
                    this.getListUserWalletRecord();
                }else if(num == 1){
                    this.inviteParams.startTime = "";
                    this.inviteParams.endTime = "";
                    this.listInviteUser();
                }
            }

        }
    }
</script>

<style lang="scss" scoped>
.personal-box{
    padding: 6rem 22rem 0;
    box-sizing: border-box;
    height: 100%;
    .title{
        font-size: 18px;
        font-family: Noto Sans SC-Medium, Noto Sans SC;
        font-weight: 500;
        color: #FFFFFF;
        display: flex;
        justify-content: space-between;
        align-items: center;
        .black-btn{
            font-size: 18px;
            font-family: Noto Sans SC-Medium, Noto Sans SC;
            font-weight: 500;
            color: #5E86D6;
            cursor: pointer;
        }
    }
    .personal-info{
        width: 100%;
        height: 20.6rem;
        background: #11141E;
        border-radius: 0.4rem;
        padding: 0 10rem;
        box-sizing: border-box;
        display: flex;
        justify-content: flex-start;
        align-items: center;
        margin-top: 1.6rem;
        .avatar-btn-box{
            width: 9.6rem;
            height: 9.6rem;
            cursor: pointer;
            img{
                width: 100%;
                height: 100%;
            }
        }

        .info-box{
            width: calc(100% - 15rem);
            display: flex;
            justify-content: center;
            align-items: flex-start;
            .info-box-left{
                width: 32%;
                padding-left: 7rem;
            }
            .info-box-right{
                width: 32%;
                margin-left: 2rem;
            }
            .info-box-item{
                .item-label{
                    display: inline-block;
                    width: 9rem;
                    text-align: left;
                    padding-right: 0.5rem;
                    color: #94A3B8;
                    font-size: 1.4rem;
                }
                .text{
                    font-size: 1.4rem;
                }
                .item-btn{
                    display: inline-block;
                    font-size: 1.4rem;
                    color: #5E86D6;
                    margin-left: 1.2rem;
                    cursor: pointer;
                }
            }
        }
        .qrCode{
            width: 26rem;
            display: flex;
            justify-content: flex-start;
            align-items: flex-start;
            .qrCode-left{
                text-align: center;
                margin-right: 1.2rem;
                .download-text{
                    font-size: 1.4rem;
                    color: #5E86D6;
                    margin-top: 0.6rem;
                    cursor: pointer;
                }
            }
            .qrCode-right{
                background: url("https://image.hanzaitu.com/static/home/kefuzhushou@2x.png");
                background-size: 100%;
                width: 12.6rem;
                height: 12.6rem;
            }
        }
    }
    .detail-box{
        width: 100%;
        padding:2.4rem 2.4rem 0;
        box-sizing: border-box;
        height: calc(100% - 28rem);
        background: #11141E;
        margin-top: 1.4rem;
        .detail-tab-box{
            display: flex;
            justify-content: space-between;
            align-items: center;
            .detail-tab{
                display: flex;
                justify-content: flex-start;
                align-items: center;
                height: 4.8rem;
                .tab-btns{
                    width: 14.5rem;
                    height: 3.2rem;
                    text-align: center;
                    line-height: 3.2rem;
                    font-size: 1.4rem;
                    color: #FFFFFF;
                    cursor: pointer;
                }
                .tab-btns-active{
                    background: linear-gradient(90deg, #836CF7 0%, #EC6DC0 100%);
                    border-radius: 3.2rem;
                }
            }
            .detail-right{
                display: flex;
                justify-content: flex-start;
                align-items: center;
                .text{
                    margin-right: 1rem;
                    font-size: 1.4rem;
                }
                .time-box{
                    width: 24.3rem;
                    height: 100%;
                }
                .btn{
                    width: 8.2rem;
                    height: 3.2rem;
                    background: linear-gradient(90deg, #836CF7 0%, #EC6DC0 100%);
                    border-radius: 0.2rem;
                    color: #fff;
                    cursor: pointer;
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    margin-left: 2rem;
                    font-size: 1.4rem;
                }
                .btn-bule{
                    background: #5E86D6;
                }
            }
        }

        .detail-table{
            margin-top: 2rem;
            height: calc(100% - 8rem);
            overflow-y: auto;
        }
    }
}
.dialog-title{
    .title-box{
        font-size: 2.4rem;
        font-weight: bold;
        color: #FFFFFF;
        text-align: center;
    }
    .subHead-box{
        font-size: 12px;
        color: #FFFFFF;
        opacity: 0.5;
        text-align: center;
        margin-top: 0.5rem;
    }
}
</style>