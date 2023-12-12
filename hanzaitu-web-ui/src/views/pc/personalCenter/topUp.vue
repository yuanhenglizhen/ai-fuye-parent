<template>
    <div class="topUp-box">
        <div class="title">
            <div>AIRABBIT充值收银台</div>
            <div class="black-btn" @click="goBlack">返回上级 >></div>
        </div>
        <div class="topUp-info">
            <div class="topUp-title">选择充值套餐<span class="ml20">当前积分：{{integral}}</span></div>
            <div class="money-list">
                <div class="money-item" :class="{'moneyItemActive':moneyActive == i}"
                     v-for="(item,i) in sumList" :key="i" @click="selectMoney(item,i)">
                    <div class="money-item-top"><i></i><span>{{item.points}}</span></div>
                    <div class="money-item-bottom">￥{{item.amount}}</div>
                </div>
            </div>
            <div class="topUp-title">支付方式</div>
            <div class="topUp-type">
                <div class="topUp-type-item" v-if="idPayFlag.zfb == 0" :class="{'topUpActive':topUpType == 0}" @click="selectTopUpType(0)">
                    <i class="item-icon"></i>
                    <img class="item-zf-i" src="../../../assets/img/other/zhifubaoicon@2x.png" alt="">
                    <span>支付宝支付</span>
                </div>
                <div class="topUp-type-item" v-if="idPayFlag.wx == 0" :class="{'topUpActive':topUpType == 1}" @click="selectTopUpType(1)">
                    <i class="item-icon"></i>
                    <img class="item-zf-i" src="../../../assets/img/other/weixinzhifuicon@2x.png" alt="">
                    <span>微信支付</span>
                </div>
                <div class="topUp-type-item" v-if="idPayFlag.yzf == 0" :class="{'topUpActive':topUpType == 2}" @click="selectTopUpType(2)">
                    <i class="item-icon"></i>
                    <img class="item-zf-i" src="../../../assets/img/other/yuanzhifu@2x.png" alt="">
                    <span>源支付(支付宝)</span>
                </div>
                <div class="topUp-type-item" v-if="idPayFlag.yzf == 0" :class="{'topUpActive':topUpType == 3}" @click="selectTopUpType(3)">
                    <i class="item-icon"></i>
                    <img class="item-zf-i" src="../../../assets/img/other/yuanzhifu@2x.png" alt="">
                    <span>源支付(微信)</span>
                </div>
            </div>

            <div class="topUp-QRcode">
                <div @click="paySubmit" class="pay-btn-css">立即支付</div>
            </div>
        </div>

        <div class="modal-wrapper" v-show="qrCodeVisible">
            <div class="modal-overlay"></div>
            <div class="modal-content">
                <div class="dialog-title">
                    <div>{{qrCodetitle}}</div>
                    <div class="close-btn" @click="handleClose"><i class="el-icon-close"></i></div>
                </div>
                <div class="form-box">
                    <div class="qrcode" ref="qrCodeUrl" v-html=""></div>
                </div>
            </div>
        </div>
        <div ref="zfbCont"></div>

    </div>
</template>

<script>
    import QRCode from 'qrcodejs2'
    export default {
        name: "topUp",
        data(){
            return{
                sumList:[],
                moneyActive:0,
                topUpType:0,
                amountCurrent:{},
                qrCodeVisible:false,
                qrCodetitle:"",
                integral:0,
                zfbHtml:"",
                yzfFlag:false,
                timer:null,
                idPayFlag:{},
            }
        },
        mounted() {
            this.getPayConfigInfo();
            this.getInfo();
            this.getListPayConfig(0);
        },
        methods:{
            getPayConfigInfo(){
                this.$https('configInfo', {}).then(res => {
                    if (res.code == 200) {
                        this.idPayFlag = res.data.payType;
                    } else {
                        this.$message.error('请求失败！')
                    }
                })
            },
            getInfo(){
                this.$GET('GETUSERINFO', {},0).then(res => {
                    if (res.code == 200) {
                        window.localStorage.setItem('infoData', JSON.stringify(res.data));
                        this.integral = res.data.points;
                    } else {
                        this.$store.commit('OPEN_LOGIN', true);
                        window.localStorage.clear();
                    }
                })
            },
            getListPayConfig(num){
                this.$GET('listPayConfig', {},0).then(res => {
                    if (res.code == 200) {
                        this.sumList = res.data;
                        if(num == 0){
                            this.selectMoney(this.sumList[0],0)
                        }
                    } else {

                    }
                })
            },
            goBlack(){
                this.$router.push('/personalCenter');
            },
            selectMoney(obj,i){
                this.moneyActive = i;
                this.amountCurrent = obj;
            },
            selectTopUpType(num){
                this.topUpType = num;
            },
            paySubmit(){
                if(this.topUpType == 0){
                    this.$https('ALIPAYORDER', {
                        amount:this.amountCurrent.amount
                    }).then(res => {
                        if (res.code == 200) {
                            this.$refs.zfbCont.innerHTML = res.msg;
                            document.forms[0].submit();
                        } else {
                            this.$message.error('请求失败！')
                        }
                    })
                }else if(this.topUpType == 1){
                    this.$https('WXPAYORDER', {
                        amount:this.amountCurrent.amount
                    }).then(res => {
                        if (res.code == 200) {
                            this.qrCodeVisible = true;
                            this.qrCodetitle = "请使用微信扫码付款"
                            this.$refs.qrCodeUrl.innerHTML = "";
                            this.creatQrCode(res.data.msg);
                            this.timer = setInterval(()=>{
                                this.getWXResult(res.data.resultNo);
                            },4000)
                        } else {
                            this.$message.error('请求失败！')
                        }
                    })
                }else if(this.topUpType == 2){
                    this.$https('YPayAppOrder', {
                        amount:this.amountCurrent.amount,
                        payType:"alipay",
                    }).then(res => {
                        if (res.code == 200) {
                            let matchReg = />.*?(<)/;
                            let str = res.msg.match(matchReg);
                            if (str) {
                                eval(str[0].slice(1, -1))
                            }
                        } else {
                            this.$message.error('请求失败！')
                        }
                    })
                }else if(this.topUpType == 3){
                    this.$https('YPayAppOrder', {
                        amount:this.amountCurrent.amount,
                        payType:"wxpay",
                    }).then(res => {
                        if (res.code == 200) {
                            let matchReg = />.*?(<)/;
                            let str = res.msg.match(matchReg);
                            if (str) {
                                eval(str[0].slice(1, -1))
                            }
                        } else {
                            this.$message.error('请求失败！')
                        }
                    })
                }

            },
            handleClose(){
                this.qrCodeVisible = false;
                clearInterval(this.timer);
                this.getInfo();
            },
            creatQrCode(url) {
                let qrcode = new QRCode(this.$refs.qrCodeUrl, {
                    text: url, // 需要转换为二维码的内容
                    width: 350,
                    height: 350,
                    colorDark: '#000000',
                    colorLight: '#ffffff',
                    correctLevel: QRCode.CorrectLevel.H
                })
            },
            getWXResult(resultNo){
                this.$GET('getWXresultData', {
                    resultNo:resultNo
                },0).then(res => {
                    if (res.code == 200 && res.data == true) {
                        clearInterval(this.timer);
                        this.qrCodeVisible = false;
                        this.getInfo();
                    }
                })
            },
        }
    }
</script>

<style lang="scss" scoped>
    .topUp-box{
        padding: 3rem 30rem 0;
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
        .topUp-info{
            width: 100%;
            background: #11141E;
            border-radius: 0.4rem;
            padding: 2.4rem;
            box-sizing: border-box;
            margin-top: 1.6rem;
            .topUp-title{
                font-size: 18px;
                color: #FFFFFF;
                margin-bottom: 0.8rem;
            }
            .money-list{
                display: flex;
                justify-content: flex-start;
                align-items: flex-start;
                flex-wrap: wrap;
                .money-item{
                    width: 18.5rem;
                    height: 10.8rem;
                    background: #1A1E2D;
                    border-radius: 0.4rem;
                    margin: 0.8rem;
                    display: flex;
                    flex-direction: column;
                    justify-content: center;
                    align-items: center;
                    box-sizing: border-box;
                    cursor: pointer;
                    .money-item-top{
                        display: flex;
                        justify-content: center;
                        align-items: baseline;
                        i{
                            display: inline-block;
                            width: 2rem;
                            height: 2rem;
                            background: url("../../../assets/img/other/Diamond@2x.png") no-repeat;
                            background-size: 100%;
                            margin-right: 0.9rem;
                        }
                        span{
                            font-size: 2.8rem;
                            font-family: Noto Sans-Medium, Noto Sans;
                            font-weight: 500;
                            color: #FFFFFF;
                        }
                    }
                    .money-item-bottom{
                        font-size: 1.8rem;
                        margin-top: 0.8rem;
                    }
                }
                .moneyItemActive{
                    border-radius: 0.4rem;
                    border: 1px solid;
                    border-image: linear-gradient(90deg, rgba(233, 109, 194, 1), rgba(124, 108, 251, 1)) 1 1;
                    span{
                        background: linear-gradient(90deg, #836CF7 0%, #EC6DC0 100%);
                        -webkit-background-clip: text;
                        -webkit-text-fill-color: transparent;
                    }
                }
            }
            .topUp-type{
                display: flex;
                justify-content: flex-start;
                align-items: center;
                margin-top: 1.6rem;
                .topUp-type-item{
                    width: 29.5rem;
                    height: 8.6rem;
                    background: #1A1E2D;
                    border-radius: 0.4rem;
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    cursor: pointer;
                    margin-right: 2.4rem;
                    .item-icon{
                        display: inline-block;
                        width: 1.7rem;
                        height: 1.7rem;
                        border-radius: 50%;
                        background-color: #fff;
                    }
                    .item-zf-i{
                        width: 4.2rem;
                        height: 4.2rem;
                        margin: 0 1.2rem;
                    }
                    span{
                        font-size: 28px;
                        font-family: Noto Sans-Medium, Noto Sans;
                        font-weight: 500;
                        color: #FFFFFF;
                    }
                }
                .topUpActive{
                    border: 1px solid;
                    border-image: linear-gradient(90deg, rgba(233, 109, 194, 1), rgba(124, 108, 251, 1)) 1 1;
                    .item-icon{
                        background: linear-gradient(90deg, #836CF7 0%, #EC6DC0 100%);
                    }
                }
            }
            .topUp-QRcode{
                text-align: center;
                margin-top: 2rem;
                .pay-btn-css{
                    width: 47.2rem;
                    height: 6rem;
                    background: linear-gradient(90deg, #836CF7 0%, #EC6DC0 100%);
                    border-radius: 0.4rem;
                    opacity: 1;
                    border: 1px solid #1A1E2D;
                    font-size: 2rem;
                    font-weight: 500;
                    color: #FFFFFF;
                    margin: 15rem auto 6rem auto;
                    text-align: center;
                    line-height: 6rem;
                    cursor: pointer;
                }
            }
        }
    }


    .modal-wrapper {
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        z-index: 100;
        .modal-overlay {
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background-color: rgba(0, 0, 0, 0.5);
        }
        .modal-content {
            width: 45rem;
            height: 45rem;
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            background-color:#1A1E2D;
            border-radius: 5px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.5);
            .dialog-title{
                width: 100%;
                text-align: center;
                position: relative;
                height: 5rem;
                line-height: 5rem;
                .close-btn{
                    width: 5rem;
                    height: 5rem;
                    position: absolute;
                    top: 0;
                    right: 0;
                    cursor: pointer;
                    text-align: center;
                    line-height: 5rem;
                    i{
                        font-size: 2rem;
                    }
                }
            }
            .form-box{
                width: 100%;
                height: calc(100% - 5rem);
                padding: 20px;
                box-sizing: border-box;
                text-align: center;
                .qrcode{
                    width: 100%;
                    height: 100%;
                    display: flex;
                    justify-content: center;
                }
            }
        }
    }

</style>