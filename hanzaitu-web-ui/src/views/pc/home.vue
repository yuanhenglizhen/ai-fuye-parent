<template>
    <div class="container">
        <div class="top-box" :style="{backgroundImage:'url('+staticUrl+'/home/banner@2x.png)'}">
            <div class="top-title">全民AI时代 ，集合灵感探索可能</div>
            <div class="top-subhead">用Ai助理效率，灵感从此即刻把握</div>
            <div class="btn-css" @click="goRoute">立即体验</div>
        </div>

        <div class="center-box" :style="{backgroundImage:'url('+staticUrl+'/home/Background3@2x.png)'}">
            <div class="center-title">仅需一次输入  抓准瞬间之美</div>
            <div class="center-item">
                <div class="item-box">
                    <div class="item-left img-box1" :style="{backgroundImage:'url('+staticUrl+'/home/AIlingganchatu2@2x.png)'}"></div>
                    <div class="item-right">
                        <div class="item-right-top">AI 文字工具</div>
                        <div class="item-right-bottom">文字效率工具，定制角色问答，你的智能助手，海量内置提示词库，快速生成所需各种类型文案，优化AI绘画提示词，你想知道的都在这里</div>
                    </div>
                </div>
                <div class="item-box">
                    <div class="item-right">
                        <div class="item-right-top">AI 灵感绘画</div>
                        <div class="item-right-bottom">3D卡通IP形象，打造自己的专属人物头像，享受随心所欲的创作乐趣，内置多种模型，让您的作品风格百变亮眼，不是大师胜似大师</div>
                    </div>
                    <div class="item-left img-box2" :style="{backgroundImage:'url('+staticUrl+'/home/AIlingganchatu@2x.png)'}"></div>
                </div>
                <div class="item-box">
                    <div class="item-left img-box3" :style="{backgroundImage:'url('+staticUrl+'/home/weilaichatu@2x.png)'}"></div>
                    <div class="item-right">
                        <div class="item-right-top">未来可期 不断更新</div>
                        <div class="item-right-bottom">未来我们将不断丰富平台内容，构建一站式AI工具库，拓展AI在各个领域的可行性。让AI工具为企业赋能，为个人增效</div>
                    </div>
                </div>
            </div>
            <div class="center-title">未来已来，AI全民时代助您前行</div>
            <div class="bottom-item">
                <div class="item-boxs">
                    <div class="item-boxs-img img1" :style="{backgroundImage:'url('+staticUrl+'/home/aiwenzixiaotu@2x.png)'}"></div>
                    <div class="item-boxs-right">
                        <div class="item-boxs-top">
                            <div class="top-text">AI 文字工具</div>
                            <div class="top-text2">便捷聊天 文字即现</div>
                        </div>
                        <div class="tem-boxs-bottom">开始聊天 >></div>
                    </div>
                </div>
                <div class="item-boxs">
                    <div class="item-boxs-img img2" :style="{backgroundImage:'url('+staticUrl+'/home/ailingganxiaotu@2x.png)'}"></div>
                    <div class="item-boxs-right">
                        <div class="item-boxs-top">
                            <div class="top-text">AI 灵感绘画</div>
                            <div class="top-text2">快速上手 即可出图</div>
                        </div>
                        <div class="tem-boxs-bottom">开启创作 >></div>
                    </div>
                </div>
                <div class="item-boxs">
                    <div class="item-boxs-img img3" :style="{backgroundImage:'url('+staticUrl+'/home/shangwuhezuoxiaotu@2x.png)'}"></div>
                    <div class="item-boxs-right">
                        <div class="item-boxs-top">
                            <div class="top-text">商务合作</div>
                            <div class="top-text2">期待您的洽谈合作</div>
                        </div>
                        <div class="tem-boxs-bottom">立即沟通 >></div>
                    </div>
                </div>
            </div>
        </div>

        <div class="footer-box">
            <div class="footer-cont">
                <div class="footer-left">
                    <div class="footer-img"><img :src="baseUrl+footerText.logo" alt="后台需配置"></div>
                    <div class="footer-text">{{footerText.text}}</div>
                </div>
                <div class="footer-right">
                    <div class="footer-code" style="margin-right: 10rem">
                        <div class="footer-code-tit">{{contactUsData.wxImgMessage}}</div>
                        <div class="footer-code-img">
                            <img :src="baseUrl+contactUsData.wxImg" alt="后台需配置">
                        </div>
                    </div>
                    <div class="footer-code">
                        <div class="footer-code-tit">{{contactUsData.joinImgMessage}}</div>
                        <div class="footer-code-img">
                            <img :src="baseUrl+contactUsData.joinImg" alt="后台需配置">
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    export default {
        name: "home",
        data(){
            return {
                footerText:{},
                contactUsData:[],
            }
        },
        mounted() {
            this.getDataList();
            this.getContactUs();
        },
        methods:{
            goRoute(){
                this.$router.push('/')
            },
            getDataList(){
                this.$https('configInfo', {}).then(res => {
                    if (res.code == 200) {
                        console.log(res);
                        this.footerText = res.data.homeConfig;
                    } else {
                        this.$message.error('请求失败！'+res.msg)
                    }
                })
            },
            getContactUs(){
                this.$GET('GETCONTACTUS', {},0).then(res => {
                    if (res.code == 200) {
                        this.contactUsData = res.data;
                    } else {
                        this.$message.error('请求失败！'+res.msg)
                    }
                })
            }
        }
    }
</script>

<style lang="scss" scoped>
    .container{
        width: 100%;
        background-color: #080B16;
        color: #fff;
        position: relative;
        .top-box{
            width: 100%;
            height: 108rem;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            background-repeat: no-repeat;
            background-size: 100%;
            z-index: 9;
            .top-title{
                font-size: 9rem;
                font-weight: bold;
                color: #FFFFFF;
                margin-top: 20rem;
            }
            .top-subhead{
                font-size: 5.2rem;
                font-weight: 500;
                color: #FFFFFF;
                margin-top: 2rem;
                opacity: 0.8;
            }
            .btn-css{
                width: 38rem;
                height: 10.3rem;
                text-align: center;
                line-height: 10.3rem;
                background: linear-gradient(90deg, #836CF7 0%, #EC6DC0 100%);
                border-radius: 8.1rem;
                font-size: 4.9rem;
                font-weight: 500;
                color: #FFFFFF;
                margin-top: 8rem;
                cursor: pointer;
                z-index: 9;
            }
        }
        .center-box{
            padding: 12rem 0;
            position: relative;
            width: 100%;
            color: #fff;
            background-repeat: no-repeat;
            background-size: 100%;
            z-index: 4;
            box-sizing: border-box;
            .center-title{
                font-size: 7.6rem;
                font-weight: bold;
                color: #FFFFFF;
                text-align: center;
                width: 100%;
            }
            .center-item{
                margin: 0 auto;
                width: 140rem;
                margin-top: 16rem;
                .item-box{
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    margin-bottom: 11rem;
                    .item-left{
                        width: 60rem;
                        height: 33.8rem;
                        z-index: 9;
                    }
                    .img-box1{
                        background-repeat: no-repeat;
                        background-size: 100%;
                        margin-right: 8rem;
                    }
                    .img-box2{
                        background-repeat: no-repeat;
                        background-size: 100%;
                        margin-left: 8rem;
                    }
                    .img-box3{
                        background-repeat: no-repeat;
                        background-size: 100%;
                        margin-right: 8rem;
                    }
                    .item-right{
                        flex: 1;
                        .item-right-top{
                            font-size: 5rem;
                            font-weight: bold;
                            line-height: 5.9rem;
                            background: linear-gradient(90deg, #836CF7 0%, #EC6DC0 100%);
                            -webkit-background-clip: text;
                            -webkit-text-fill-color: transparent;
                        }
                        .item-right-bottom{
                            font-size: 2.2rem;
                            font-weight: 500;
                            color: #FFFFFF;
                            margin-top: 3rem;
                        }
                    }
                }
            }
            .bottom-item{
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin: 0 auto;
                width: 140rem;
                margin-top: 8rem;
                .item-boxs{
                    width: 45.6rem;
                    height: 21.2rem;
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    cursor: pointer;
                    .item-boxs-img{
                        margin:0 2.4rem;
                        width: 21.2rem;
                        height: 17.6rem;
                    }
                    .img1{
                        background-repeat: no-repeat;
                        background-size: 100%;
                    }
                    .img2{
                        background-repeat: no-repeat;
                        background-size: 100%;
                    }
                    .img3{
                        background-repeat: no-repeat;
                        background-size: 100%;
                    }
                    .item-boxs-right{
                        display: flex;
                        flex-direction: column;
                        justify-content: space-between;
                        align-items:flex-start;
                        flex: 1;
                        height: calc(100% - 3.6rem);
                        .item-boxs-top{
                            .top-text{
                                font-size: 2.8rem;
                                font-weight: 600;
                                color: #FFFFFF;
                            }
                            .top-text2{
                                font-size: 1.8rem;
                                font-weight: 400;
                                color: #FFFFFF;
                                margin-top: 1rem;
                            }
                        }
                        .tem-boxs-bottom{
                            font-size: 2rem;
                            font-weight: 500;
                            color: #FFFFFF;
                        }
                    }
                }
                .item-boxs:hover{
                    background: #11172D;
                    transform: scale(1.05, 1.05);
                    .tem-boxs-bottom{
                        font-size: 20px;
                        font-weight: 500;
                        line-height: 23px;
                        background: linear-gradient(90deg, #836CF7 0%, #EC6DC0 100%);
                        -webkit-background-clip: text;
                        -webkit-text-fill-color: transparent;
                        border-radius: 0.4rem;
                    }
                }
            }
        }
        .footer-box{
            width: 100%;
            height: 26rem;
            background: #11162B;
            padding: 0 24rem;
            box-sizing: border-box;
            .footer-cont{
                height: 100%;
                padding: 0 24.8rem;
                box-sizing: border-box;
                display: flex;
                justify-content: space-between;
                align-items: center;
                .footer-left{
                    .footer-img{
                        width: 21.2rem;
                        height: 5.4rem;
                        img{
                            width: 100%;
                            height: 100%;
                        }
                    }
                    .footer-text{
                        width: 28.5rem;
                        font-size: 1.6rem;
                        font-weight: 500;
                        color: #FFFFFF;
                        margin-top: 1.2rem;
                    }
                }
                .footer-right{
                    display: flex;
                    justify-content: flex-start;
                    align-items: center;
                    height: 100%;
                    .footer-code{
                        width: 14rem;
                        .footer-code-tit{
                            font-size: 14px;
                            font-weight: 500;
                            color: #FFFFFF;
                        }
                        .footer-code-img{
                            width: 14rem;
                            height: 14rem;
                            margin-top: 2rem;
                            img{
                                width: 100%;
                                height: 100%;
                            }
                        }
                    }
                }
            }
        }
    }
</style>