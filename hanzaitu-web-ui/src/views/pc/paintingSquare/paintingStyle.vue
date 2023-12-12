<template>
    <div class="container">
        <div class="title-tag-box">
            <div class="title-tag-cont" :class="unfoldFlag?'':'unfoldCSS'" >
                <div class="title-tag-item" :class="typeTagActive == i?'currentTypeTag':''"
                     v-for="(item,i) in typeTagList" :key="item.id" @click="selectTypeTag(item,i)">{{item.name}}</div>
            </div>
            <div class="unfold-box" @click="unfoldBtn">
                <div class="unfold-cont" v-if="!unfoldFlag">
                    <span>展开</span>
                    <i></i>
                </div>
                <div class="pack-up" v-else>
                    <span>收起</span>
                    <i></i>
                </div>
            </div>
        </div>
        <div class="top-tag-box">
            <div class="tag-item" @click="selectTagBtn('HOT')" :class="topTagNum == 'HOT'?'tag-act':''">
                <i class="rm-a" v-if="topTagNum == 'HOT'"></i>
                <i class="rm" v-else></i>
                <span>热门</span>
            </div>
            <div class="tag-item" @click="selectTagBtn('NEWEST')" :class="topTagNum == 'NEWEST'?'tag-act':''">
                <i class="zx-a" v-if="topTagNum == 'NEWEST'"></i>
                <i class="zx" v-else></i>
                <span>最新</span>
            </div>
        </div>
        <el-divider style="background-color: #282828"></el-divider>
        <div class="img-cont-box">
            <imgLazyLoading :tagLable="tagLabelId" :sortType="topTagNum" urlStr="drawSquareList"></imgLazyLoading>
        </div>
    </div>
</template>

<script>
    import imgLazyLoading from '@/components/imgLazyLoading/index'
    export default {
        name: "paintingStyle",
        components:{
            imgLazyLoading
        },
        data(){
            return {
                typeTagList:[],
                typeTagActive:0,
                tagLabelId:0,
                unfoldFlag:false,
                topTagNum:"HOT",
            }
        },
        mounted() {
            this.getTagList();
        },
        methods:{
            getTagList(){
                this.typeTagList = [];
                this.$https('drawSquareLabel', {}).then(res => {
                    if (res.code == 200) {
                        let obj = {
                            id:0,
                            name:"全部",
                            sort:0,
                        }
                        this.typeTagList = res.data.data;
                        this.typeTagList.unshift(obj);

                    } else {
                        this.$message.error(res.msg);
                    }
                })
            },
            selectTypeTag(obj,i){
                this.typeTagActive = i;
                this.tagLabelId = obj.id;
            },
            unfoldBtn(){
                if(this.unfoldFlag == false){
                    this.unfoldFlag = true;
                }else{
                    this.unfoldFlag = false;
                }
            },
            selectTagBtn(str){
                this.topTagNum = str;
            },
        }
    }
</script>

<style lang="scss" scoped>
.container{
    padding: 2.4rem;
    height: 100%;
    .title-tag-box{
        position: relative;
        .title-tag-cont{
            display: flex;
            flex-wrap: wrap;
            justify-content: flex-start;
            align-items: center;
            box-sizing: border-box;
            overflow: hidden;
            .title-tag-item{
                background: #232327;
                border-radius: 2.4rem;
                border: 1px solid #3B3B3B;
                padding: 0.8rem 2.4rem;
                margin-right: 2.4rem;
                margin-bottom: 2rem;
                font-size: 1.6rem;
                cursor: pointer;
            }
            .currentTypeTag{
                background: linear-gradient(90deg, #D950A1 0%, #6C7AFB 100%);
            }
        }
        .unfoldCSS{
            height: 5.7rem;
        }
        .unfold-box{
            position: absolute;
            bottom: 0;
            right: 0;
            color: #A7B2C1;
            cursor: pointer;
            .unfold-cont{
                display: flex;
                justify-content: center;
                align-items: center;
                i{
                    width: 1.4rem;
                    height: 1.4rem;
                    background: url("https://image.hanzaitu.com/static/other/fullarrow_up_Default_14.png") no-repeat;
                    background-size: 100%;
                }
            }
            .pack-up{
                display: flex;
                justify-content: center;
                align-items: center;
                i{
                    width: 1.4rem;
                    height: 1.4rem;
                    background: url("https://image.hanzaitu.com/static/other/fullarrow_down_Default_14.png") no-repeat;
                    background-size: 100%;
                }
            }

        }
        .unfold-box:hover{
            color: #fff;
            .unfold-cont{
                i{
                    background: url("https://image.hanzaitu.com/static/other/fullarrow_up__Hover_14.png") no-repeat;
                    background-size: 100%;
                }
            }
            .pack-up{
                i{
                    background: url("https://image.hanzaitu.com/static/other/fullarrow_down_Hover_14.png") no-repeat;
                    background-size: 100%;
                }
            }

        }
    }
    .top-tag-box{
        display: flex;
        justify-content: flex-start;
        align-items: center;
        margin-top: 0.8rem;
        .tag-item{
            display: flex;
            justify-content: center;
            align-items: center;
            width: 9.6rem;
            height: 3.8rem;
            background: #0C0F16;
            cursor: pointer;
            i{
                width: 1.6rem;
                height: 1.6rem;
                margin-right: 0.4rem;
            }
            .rm{
                background: url("https://image.hanzaitu.com/static/other/icon_hot_Default_14.png") no-repeat;
                background-size: 100%;
            }
            .rm-a{
                background: url("https://image.hanzaitu.com/static/other/icon_hot_Hover_14.png") no-repeat;
                background-size: 100%;
            }
            .zx{
                background: url("https://image.hanzaitu.com/static/other/icon_new_Default_14.png") no-repeat;
                background-size: 100%;
            }
            .zx-a{
                background: url("https://image.hanzaitu.com/static/other/icon_new__Hover_14.png") no-repeat;
                background-size: 100%;
            }
        }
        .tag-item:first-child{
            border-radius: 0.8rem 0 0 0.8rem;
        }
        .tag-item:last-child{
            border-radius: 0 0.8rem 0.8rem 0;
        }
        .tag-act{
            background: linear-gradient(90deg, #D950A1 0%, #6C7AFB 100%);
        }
    }
    .img-cont-box{
        height: calc(100% - 15rem);
    }
}
</style>