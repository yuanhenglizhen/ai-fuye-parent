<template>
    <div class="container">
        <div class="title">联系我们</div>
        <div class="banner-box" :style="{background: 'url('+contactUsData.mainImg+') no-repeat 100%'}">
            <div class="banner-cont">
                <img :src="footerText.logo" alt="后台需配置">
                <div>{{contactUsData.mainImgMessage}}</div>
            </div>
        </div>
        <div class="item-box">
            <div class="item-cont">
                <div class="item-cont-img">
                    <img :src="contactUsData.wxImg" alt="">
                </div>
                <div class="item-cont-text">{{contactUsData.wxImgMessage}}</div>
            </div>
            <div class="item-cont">
                <div class="item-cont-img">
                    <img :src="contactUsData.joinImg" alt="">
                </div>
                <div class="item-cont-text">{{contactUsData.joinImgMessage}}</div>
            </div>
            <div class="item-cont">
                <div class="item-cont-img">
                    <img :src="contactUsData.dy1Img" alt="">
                </div>
                <div class="item-cont-text">{{contactUsData.dy1ImgMessage}}</div>
            </div>
            <div class="item-cont">
                <div class="item-cont-img">
                    <img :src="contactUsData.dy2Img" alt="">
                </div>
                <div class="item-cont-text">{{contactUsData.dy1ImgMessage}}</div>
            </div>
        </div>
    </div>
</template>

<script>
    export default {
        name: "contactUs",
        data(){
            return{
                contactUsData:[],
                footerText:{},
            }
        },
        mounted() {
            this.getContactUs();
            this.getDataList();
        },
        methods:{
            getContactUs(){
                this.$GET('GETCONTACTUS', {},0).then(res => {
                    if (res.code == 200) {
                        this.contactUsData = res.data;
                    } else {
                        this.$message.error('请求失败！'+res.msg)
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
        }
    }
</script>

<style lang="scss" scoped>
    .container{
        padding: 8rem 0 0 14rem;
        box-sizing: border-box;
        background-color: #080B16;
        height: 100%;
        width: 100%;
        .title{
            font-size: 24px;
            color: #FFFFFF;
        }
        .banner-box{
            margin-top: 1.6rem;
            width: 120rem;
            height: 28.6rem;
            background: url("../../../assets/img/other/lianxiwomen_banner@2x.png") no-repeat;
            background-size: 100%;
            position: relative;
            .banner-cont{
                position: absolute;
                top: 7.4rem;
                left: 8rem;
                div{
                    font-size: 38px;
                    font-family: Noto Sans SC-Bold, Noto Sans SC;
                    font-weight: bold;
                    color: #FFFFFF;
                }
            }
        }
        .item-box{
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            margin-top: 4rem;
            width: 120rem;
            .item-cont{
                .item-cont-img{
                    padding: 1rem;
                    box-sizing: border-box;
                    width: 28rem;
                    height: 28rem;
                    border-radius: 1rem;
                    border: 1px solid #4E5C8D;
                    img{
                        width: 100%;
                        height: 100%;
                    }
                }
                .item-cont-text{
                    text-align: center;
                    margin-top: 1.6rem;
                    font-size: 2.4rem;
                }
            }
        }
    }
</style>