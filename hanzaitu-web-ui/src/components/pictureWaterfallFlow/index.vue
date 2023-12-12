<template>
    <div class="image-waterfall" ref="scrollable" @scroll="handleScroll">
        <div class="image-column" v-for="(column, index) in columns" :key="index">
            <div class="image-item" v-for="(image,i) in column" :key="image.id">
                <el-image
                        style="border-radius: 1rem"
                        fit="scale-down"
                        :src="image.imgUrl"
                        @load="handleImageLoad"
                        :preview-src-list="[image.imgUrl]">
                    <div slot="placeholder" class="image-slot" :style="{width: image.width+'px',height:image.height+'px'}">
                        加载中<span class="dot">...</span>
                    </div>
                </el-image>
                <div class="image-icons">
                    <span class="text">{{image.wieNum}}</span>
                    <span class="icon" v-if="isCZSC" :class="image.squareStatus?'icon-like':''" @click="handleLike(image.taskId,index,i)"></span>
                </div>
                <div class="icon-favorite" v-if="!isCZSC" @click="handleFavorite(image.taskId)"></div>
                <!--<div class="marks"></div>-->
                <div class="prompt-box">
                    <div class="prompt-text">{{image.prompt}}</div>
                    <div class="prompt-bottom">
                        <div class="prompt-name">{{image.userName}}</div>
                        <div class="prompt-bnt" v-clipboard:copy="image.prompt"  v-clipboard:success="onCopy">复制提示词</div>
                    </div>
                </div>
            </div>
        </div>
        <button class="scroll-to-top" v-if="showScrollToTop" @click="scrollToTop"></button>
    </div>
</template>

<script>
    export default {
        props:{
            columnNum:{
                type:Number,
                default:4
            },
            urlStr:{
                type:String,
                default:''
            },
            params:{
                type:Object,
                default:()=>{
                    return {
                        allowPaging:true,
                        pageSize:10,
                        pageNo:1,
                    }
                }
            },
            isCZSC:{
                type:Boolean,
                default:true
            }
        },
        data() {
            return {
                images: [], // 图片数组
                columns: [], // 图片列数组
                columnCount: 4, // 图片列数
                contWidth: null, // 默认图片宽度
                showScrollToTop: false, // 是否显示回到顶部按钮
                paramsList:{
                    allowPaging:true,
                    pageSize:10,
                    pageNo:1,
                },
                srcStr:"drawSquareCollectSquare",
            };
        },
        computed: {
            paramsObj() {
                return Object.assign({}, this.params);
            }
        },
        watch:{
            columnNum:{
                handler(newName, oldName){
                    this.columnCount = newName;
                },
                immediate: true
            },
            urlStr:{
                handler(newName, oldName){
                    this.srcStr = newName;
                },
                immediate: true
            },
            paramsObj: {
                handler(val, oldVal) {
                    this.paramsList = val;
                },
                deep: true,
                immediate: true
            }
        },
        mounted() {
            this.loadImages(); // 初始加载图片
            this.contWidth = this.$refs.scrollable.clientWidth;
        },
        destroyed() {

        },
        methods: {
            loadImages() {
                this.$https(this.srcStr, this.paramsList).then(res => {
                    if (res.code == 200) {
                        this.images.push(...res.data.list);
                        this.resetColumns(); // 重新计算图片列
                    } else {
                        this.$message.error('请求失败！'+res.msg)
                    }
                })
            },
            resetColumns() {
                this.columns = Array.from({ length: this.columnCount }, () => []);
                this.images.forEach((image, index) => {
                    const columnIndex = index % this.columnCount;
                    let numW = (parseInt(this.contWidth)/parseInt(this.columnCount)).toFixed(2) || 0;
                    image.width = numW - 25;
                    let numH = (image.imgHeight/image.imgWidth).toFixed(2) || 0;
                    image.height = (numH * image.width).toFixed(2);
                    this.columns[columnIndex].push(image);
                });
            },
            handleImageLoad() {
                // 图片加载完成后重新计算图片列高度
                this.$nextTick(() => {
                    this.resetColumns();
                });
            },
            handleScroll(event) {
                let el=event.target;
                if(Math.ceil(el.scrollTop+el.clientHeight) >= el.scrollHeight){
                    this.paramsList.pageNo += 1;
                    this.loadImages();
                }
                this.showScrollToTop = el.scrollTop > el.clientHeight; // 根据滚动位置显示/隐藏回到顶部按钮
            },
            handleLike(imageId,index,i) {
                // 处理点赞逻辑
                this.$https('drawSquareSquare', {
                    "taskId":imageId,
                }).then(res => {
                    if (res.code == 200) {
                        this.$message.success('操作成功！');
                        this.columns[index][i].squareStatus = res.data.praiseStatus;
                        if(res.data.praiseStatus){
                            this.columns[index][i].wieNum += 1;
                        }else{
                            this.columns[index][i].wieNum -= 1;
                        }
                        this.paramsList.pageNo = 1;
                        this.images = [];
                        this.loadImages();
                    } else {
                        this.$message.error('请求失败！'+res.msg)
                    }
                })
            },
            handleFavorite(imageId) {
                // 取消创作收藏
                this.$https('drawTriggerCollect', {
                    "taskId":imageId,
                }).then(res => {
                    if (res.code == 200) {
                        this.$message.success('操作成功！')
                        this.paramsList.pageNo = 1;
                        this.images = [];
                        this.loadImages();
                    } else {
                        this.$message.error('请求失败！'+res.msg)
                    }
                })
            },
            scrollToTop() {
                // 回到顶部
                this.$refs.scrollable.scrollTo({ top: 0, behavior: 'smooth' });
            },
            // 复制成功
            onCopy(e){
                this.$message({
                    message:'复制成功！',
                    type:'success'
                })
            },
        },
    };
</script>

<style lang="scss" scoped>
    .image-waterfall {
        display: flex;
        flex-wrap: wrap;
        justify-content: space-between;
        height: 100%;
        overflow-y: auto;
    }

    .image-column {
        flex: 1;
        margin-right: 20px;
    }

    .image-item {
        margin-bottom: 20px;
        position: relative;
        .image-slot{
            background: #2b2f3a;
            border-radius: 1rem;
            display: flex;
            justify-content: center;
            align-items: center;
        }
    }

    .image-item img {
        display: block;
        width: 100%;
        height: auto;
    }
    .image-item:hover{
        .image-icons {
            display: flex;
        }
        .prompt-box{
            display: block;
        }
        .icon-favorite {
            display: block;
        }
        .marks{
            display: block;
        }
    }
    .marks{
        display: none;
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-color: rgba(0,0,0,0.5);
        z-index: 1;
    }
    .image-icons {
        position: absolute;
        z-index: 9;
        top: 10px;
        right: 10px;
        display: none;
        justify-content: center;
        align-items: center;
        padding: 0rem 1.6rem;
        background: rgba(255,255,255,0.1);
        border-radius: 2rem;
        cursor: pointer;
        .text{
            margin-right: 0.8rem;
            font-size: 2.2rem;
        }
    }

    .icon {
        display: inline-block;
        width: 32px;
        height: 32px;
        background-image: url("https://image.hanzaitu.com/static/other/ALL-icon.png");
        background-position: 0px -204px;
        cursor: pointer;
    }

    .icon-like {
        background-image: url("https://image.hanzaitu.com/static/other/ALL-icon.png");
        background-position: -48px -204px;
    }

    .icon-favorite {
        position: absolute;
        z-index: 9;
        top: 10px;
        right: 10px;
        display: none;
        width: 40px;
        height: 40px;
        background-image: url("https://image.hanzaitu.com/static/other/ALL-icon.png");
        background-position: -264px -204px;
        cursor: pointer;
    }

    .scroll-to-top {
        position: fixed;
        bottom: 3rem;
        right: 3rem;
        width: 6.4rem;
        height: 6.4rem;
        cursor: pointer;
        background-image: url("https://image.hanzaitu.com/static/other/ALL-icon.png");
        background-position: -184px -204px;
    }
    .prompt-box{
        position: absolute;
        z-index: 9;
        width: calc(100% - 4.8rem);
        height: 15rem;
        left: 2.4rem;
        bottom: 1.6rem;
        background: #0B0F17;
        border-radius: 0.8rem;
        display: none;
        .prompt-text{
            height: 10rem;
            line-height: 2.2rem;
            font-size: 1.4rem;
            width: 100%;
            padding: 1.6rem 2.4rem;
            -webkit-line-clamp: 3;
            display: -webkit-box;
            -webkit-box-orient: vertical;
            overflow: hidden;
            text-overflow: ellipsis;
        }
        .prompt-bottom{
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-top: 0.8rem;
            padding: 0 2.4rem;
            .prompt-bnt{
                width: 10.8rem;
                height: 3rem;
                text-align: center;
                line-height: 3rem;
                display: flex;
                flex-direction: column;
                justify-content: center;
                align-items: center;
                border-radius: 0.6rem;
                box-sizing: border-box;
                border: 1px solid transparent;
                background-image: linear-gradient(#11182F, #11182F), linear-gradient(to bottom right, rgb(217, 80, 161), rgb(108, 122, 251));
                background-origin: border-box;
                background-clip: content-box, border-box;
                cursor: pointer;
            }
        }
    }
</style>