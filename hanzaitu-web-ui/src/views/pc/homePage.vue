<template>
    <div class="container">
        <div class="top">
            <div class="top-left homeBanner">
                <el-carousel height="328px">
                    <el-carousel-item v-for="(item,index) in homeBanner" :key="index">
                        <img :src="item.imgUrl" alt="" style="width: 100%">
                    </el-carousel-item>
                </el-carousel>
            </div>
            <div class="top-message">
                <div class="title">公告通知</div>
                <div class="home-message-box">
                    <div class="home-message-item" v-for="(item,index) in homeMessage" :key="index">
                        <div class="item-left">{{item.content}}</div>
                        <div class="item-btn" @click="openDetails">详情</div>
                    </div>
                </div>
            </div>
        </div>
        <div class="center" ref="contentUs">
            <div class="contact-us-item" :class="currentCodeActive == index?'currentActive':''"
                 v-for="(item,index) in contactUsData" :key="index"
            :style="{width:contentUsWidth + 'px'}" @click="openErCode(item,index)">
                <img :src="item.icon" alt="">
                <div class="text">{{item.label}}</div>
                <i></i>
            </div>

            <div class="contact-us-item" :class="currentCodeActive == 4?'currentActive':''"
                 :style="{width:contentUsWidth + 'px'}" @click="clickDropDown(4)">
                <el-popover placement="bottom" popper-class="xz-pop" :width="contentUsWidth" trigger="click">
                    <div class="xz-box">
                        <div class="xz-item" >
                            <div class="xz-item-left">
                                <i class="win-icon"></i>
                                <span>Windows版</span>
                            </div>
                            <div class="xz-item-right">下载</div>
                        </div>
                        <div class="xz-item" >
                            <div class="xz-item-left">
                                <i class="mac-icon"></i>
                                <span>Mac版</span>
                            </div>
                            <div class="xz-item-right">下载</div>
                        </div>
                    </div>
                    <div slot="reference" class="contact-us-pop-item">
                        <img src="https://image.hanzaitu.com/static/other/icon_clientdownload_42.png" alt="">
                        <span>客户端下载</span>
                        <div class="xz-icon"></div>
                    </div>
                </el-popover>
            </div>
            <div class="contact-us-item" :class="currentCodeActive == 5?'currentActive':''"
                 :style="{width:contentUsWidth + 'px'}" @click="clickDropDown(5)">
                <el-popover placement="bottom" popper-class="xz-pop" :width="contentUsWidth" trigger="click">
                    <div class="xz-box">
                        <div class="xz-item" >
                            <div class="xz-item-left">
                                <i class="Android-icon"></i>
                                <span>Android</span>
                            </div>
                            <div class="xz-item-right">下载</div>
                        </div>
                        <div class="xz-item" >
                            <div class="xz-item-left">
                                <i class="iOS-icon"></i>
                                <span>iOS</span>
                            </div>
                            <div class="xz-item-right">下载</div>
                        </div>
                    </div>
                    <div slot="reference" class="contact-us-pop-item">
                        <img src="https://image.hanzaitu.com/static/other/icon_Appdownload_42.png" alt="">
                        <span>APP下载</span>
                        <div class="xz-icon"></div>
                    </div>
                </el-popover>
            </div>
        </div>
        <div class="bottom">
            <div class="bottom-title">
                <div class="title-left"><span>AIGC</span> 商学院</div>
                <div class="title-right">更多 ></div>
            </div>
            <div class="bottom-cont">
                <div class="bottom-cont-item" v-for="item in 7">
                    <div class="item-img-box">
                        <img src="https://image.hanzaitu.com/static/other/Teacherpicture02.png" alt="">
                        <div class="shade-box">
                            <img src="https://image.hanzaitu.com/static/other/Frame36.png" alt="">
                        </div>
                    </div>
                    <div class="item-cont">
                        <div class="item-tit">开篇丨迎接时代转折，让Chat为效率助力</div>
                        <p class="item-text">《零基础开始Chat应用入门课》让新技术助力效率，告别陈旧内卷赛道，迎接互联网转折点，资深AIGC名师深入浅出讲解Chat应用，循序渐进，掌握熟练。</p>
                        <div class="item-bottoom">
                            <div>主讲老师：王丹洛</div>
                            <div>时长 17:49</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="bottom">
            <div class="bottom-title">
                <div class="title-left">为您推荐</div>
                <!--<div class="title-right">更多 ></div>-->
            </div>
            <div class="bottom-cont">
                <div class="bottom-cont-item" v-for="item in 4">
                    <div class="item-img-box">
                        <img src="https://image.hanzaitu.com/static/other/Teacherpicture02.png" alt="">
                        <div class="shade-box">
                            <img src="https://image.hanzaitu.com/static/other/Frame36.png" alt="">
                        </div>
                    </div>
                    <div class="item-cont">
                        <div class="item-tit">AIGC是什么？</div>
                        <p class="item-text">风口还是泡沫？普通人如何把握机会？</p>
                        <!--<div class="item-bottoom">
                            <div>主讲老师：王丹洛</div>
                            <div>时长 17:49</div>
                        </div>-->
                    </div>
                </div>
            </div>
        </div>

        <el-dialog :visible.sync="erCodeVisible" custom-class="erCodeDialog" width="195px" top="20%" :show-close="false">
            <div><img :src="baseUrl+currentErCode.value" alt=""></div>
        </el-dialog>
    </div>
</template>

<script>
    import wxIcon from '@/assets/img/other/icon_weixin_42.png'
    import qqIcon from '@/assets/img/other/icon_QQ_42.png'
    import qqqIcon from '@/assets/img/other/icon_QQqun _42.png'
    import gzhIcon from '@/assets/img/other/icon_gongzhonghao_42.png'
    export default {
        name: "homePage",
        data(){
            return {
                homeBanner:[],
                homeMessage:[],
                contactUsData:[],
                contentUsWidth:0,
                erCodeVisible:false,
                currentErCode:{},
                currentCodeActive:null,
            }
        },
        mounted() {
            let token = window.localStorage.getItem("token") || null;
            if(token != null){


            }
            this.getBannerList();
            this.getHomeMessageList();
            this.getContactUs();
        },
        methods:{
            getBannerList(){
                this.$https('homeBanner', {}).then(res => {
                    if (res.code == 200) {
                        this.homeBanner = res.data;
                    } else {
                        //this.$message.error('请求失败！'+res.msg)
                    }
                })
            },
            getHomeMessageList(){
                this.$https('homeMessage', {}).then(res => {
                    if (res.code == 200) {
                        this.homeMessage = res.data;
                    } else {
                        //this.$message.error('请求失败sss！'+res.msg)
                    }
                })
            },
            getContactUs(){
                this.$GET('GETCONTACTUS', {},0).then(res => {
                    if (res.code == 200) {
                        let arr = []
                        for(let key  in res.data){
                            if(key == 'wxImg' && res.data[key] != null && res.data[key] != ""){
                                arr.push({label:res.data.wxImgMessage,value:res.data.wxImg,icon:wxIcon});
                            }
                            if(key == 'joinImg' && res.data[key] != null && res.data[key] != ""){
                                arr.push({label:res.data.joinImgMessage,value:res.data.joinImg,icon:qqIcon});
                            }
                            if(key == 'dy1Img' && res.data[key] != null && res.data[key] != ""){
                                arr.push({label:res.data.dy1ImgMessage,value:res.data.dy1Img,icon:qqqIcon});
                            }
                            if(key == 'dy2Img' && res.data[key] != null && res.data[key] != ""){
                                arr.push({label:res.data.dy2ImgMessage,value:res.data.dy2Img,icon:gzhIcon});
                            }
                        }

                        this.contentUsWidth = parseFloat((parseInt(this.$refs.contentUs.clientWidth)/(parseInt(arr.length)+2)).toFixed(2));
                        this.contactUsData = arr;
                    } else {
                        this.$message.error('请求失败！'+res.msg)
                    }
                })
            },
            openErCode(obj,i){
                this.erCodeVisible = true;
                this.currentCodeActive = i;
                this.currentErCode = obj;
            },
            clickDropDown(i){
                this.currentCodeActive = i;
            },
            openDetails(){

            }
        }
    }
</script>

<style lang="scss" scoped>
.container{
    padding: 2.4rem;
    box-sizing: border-box;
    .top{
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        .top-left{
            width: calc(100% - 55rem);
            height: 32.8rem;
        }
        .top-message{
            width: 53.6rem;
            height: 32.8rem;
            background: #131313;
            border-radius: 0.8rem;
            padding: 2.4rem 3rem;
            box-sizing: border-box;
            .title{
                text-align: center;
                font-size: 2rem;
                background: linear-gradient(90deg, #D950A1 0%, #6C7AFB 100%);
                -webkit-background-clip: text;
                -webkit-text-fill-color: transparent;
            }
            .home-message-box{
                margin-top: 1.6rem;
                overflow-y: auto;
                height: calc(100% - 4rem);
                .home-message-item{
                    height: 6rem;
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    padding: 0 1.6rem;
                    border-bottom: 1px solid #272E3B;
                    .item-left{
                        width: calc(100% - 5rem);
                        font-size: 1.4rem;
                        box-sizing: border-box;
                        overflow:hidden;
                        white-space: nowrap;
                        text-overflow: ellipsis;
                        -o-text-overflow:ellipsis;
                    }
                    .item-btn{
                        width: 3rem;
                        font-size: 1.4rem;
                        background: linear-gradient(90deg, #D950A1 0%, #6C7AFB 100%);
                        -webkit-background-clip: text;
                        -webkit-text-fill-color: transparent;
                        cursor: pointer;
                    }
                }
            }
        }
    }
    .center{
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-top: 1.6rem;
        .contact-us-item:hover{
            background-color: #141924 !important;
            .text{
                color: #fff !important;
            }
        }
        .contact-us-item{
            height: 7.4rem;
            background: #0C0F16;
            display: flex;
            justify-content: center;
            align-items: center;
            position: relative;
            cursor: pointer;
            &>span{
                width: 100%;
                height: 100%;
                color: #A7B2C1;
                font-size: 1.8rem;
            }
            .contact-us-pop-item{
                width: 100%;
                height: 100%;
                display: flex;
                justify-content: center;
                align-items: center;
            }
            img{
                width: 4.2rem;
                height: 4.2rem;
                margin-right: 1.6rem;
            }
            .text{
                color: #A7B2C1;
                font-size: 1.8rem;
            }
            i{
                width: 1.8rem;
                height: 1.8rem;
                background: url("https://image.hanzaitu.com/static/other/icon_QRcode_Hover_32.png") no-repeat;
                background-size: 100%;
                position: absolute;
                right: 1rem;
                bottom: 1rem;
            }
            .xz-icon{
                width: 1.4rem;
                height: 1.4rem;
                background: url("https://image.hanzaitu.com/static/other/fullarrow_up__Hover_14.png") no-repeat;
                background-size: 100%;
                position: absolute;
                right: 1rem;
                bottom: 3.2rem;
            }
        }
    }
    .bottom{
        margin-top: 3.6rem;
        .bottom-title{
            display: flex;
            justify-content: space-between;
            align-items: center;
            .title-left{
                font-size: 24px;
                font-weight: bold;
                span{
                    background: linear-gradient(90deg, #D950A1 0%, #6C7AFB 100%);
                    -webkit-background-clip: text;
                    -webkit-text-fill-color: transparent;
                }
            }
            .title-right{
                width: 82px;
                height: 30px;
                text-align: center;
                line-height: 30px;

                display: flex;
                flex-direction: column;
                justify-content: center;
                align-items: center;
                border-radius: 0.4rem;
                box-sizing: border-box;
                border: 1px solid transparent;
                background-image: linear-gradient(#11182F, #11182F), linear-gradient(to top left, rgb(124, 108, 251), rgb(233, 109, 194));
                background-origin: border-box;
                background-clip: content-box, border-box;
                cursor: pointer;

            }
        }
        .bottom-cont{
            width: 100%;
            margin-top: 1.6rem;
            display: flex;
            flex-wrap: wrap;
            justify-content: flex-start;
            align-items: flex-start;
            .bottom-cont-item{
                width: calc(25% - 1.6rem);
                height: 37rem;
                background: #131313;
                cursor: pointer;
                margin-right: 1.6rem;
                margin-bottom: 1.6rem;
                .item-img-box{
                    height: 19rem;
                    width: 100%;
                    position: relative;
                    img{
                        width: 100%;
                        height: 100%;
                    }
                    .shade-box{
                        position: absolute;
                        width: 100%;
                        height: 100%;
                        top: 0;
                        left: 0;
                        display: flex;
                        justify-content: center;
                        align-items: center;
                        background-color: rgba(0,0,0,0.4);
                        img{
                            width: 4.2rem;
                            height: 4.2rem;
                        }
                    }
                }
                .item-cont{
                    padding: 1.6rem 2.4rem;
                    .item-tit{
                        font-size: 1.6rem;
                        height: 2.6rem;
                        line-height: 2.6rem;
                        overflow:hidden;
                        white-space: nowrap;
                        text-overflow: ellipsis;
                        -o-text-overflow:ellipsis;
                    }
                    .item-text{
                        font-size: 1.4rem;
                        height: 8rem;
                        color: #DBDBDB;
                        line-height: 2.6rem;
                        margin-top: 0.5rem;
                        overflow:hidden;
                        -webkit-line-clamp: 3;
                        display: -webkit-box;
                        -webkit-box-orient: vertical;
                        text-overflow: ellipsis;
                    }
                    .item-bottoom{
                        display: flex;
                        justify-content: space-between;
                        align-items: center;
                        font-size: 1.4rem;
                        color: #A7B2C1;
                        margin-top: 1.6rem;
                    }
                }
            }
        }
    }
}
.xz-box{
    height: 15rem;
    background: #141414;
    border-radius: 0.8rem;
    border: 2px solid #424242;
    box-sizing: border-box;
    color: #fff;
    .xz-item{
        display: flex;
        justify-content: space-between;
        align-items: center;
        height: 7.4rem;
        padding:0 1.6rem;
        .xz-item-left{
            display: flex;
            justify-content: flex-start;
            align-items: center;
            .win-icon{
                width: 4.2rem;
                height: 4.2rem;
                background: url("https://image.hanzaitu.com/static/other/icon_window_42.png") no-repeat;
                background-size: 100%;
                margin-right: 1.6rem;
            }
            .mac-icon{
                width: 4.2rem;
                height: 4.2rem;
                background: url("https://image.hanzaitu.com/static/other/icon_mac_42.png") no-repeat;
                background-size: 100%;
                margin-right: 1.6rem;
            }
            .Android-icon{
                width: 4.2rem;
                height: 4.2rem;
                background: url("https://image.hanzaitu.com/static/other/icon_Android_42.png") no-repeat;
                background-size: 100%;
                margin-right: 1.6rem;
            }
            .iOS-icon{
                width: 4.2rem;
                height: 4.2rem;
                background: url("https://image.hanzaitu.com/static/other/icon_appstore_42.png") no-repeat;
                background-size: 100%;
                margin-right: 1.6rem;
            }
            span{
                font-size: 1.6rem;
                color: #FFFFFF;
            }
        }
        .xz-item-right{
            background: linear-gradient(90deg, #D950A1 0%, #6C7AFB 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            cursor: pointer;
        }
    }
}
.currentActive{
    background-color: #141924 !important;
    .text{
        color: #fff !important;
    }

}
</style>