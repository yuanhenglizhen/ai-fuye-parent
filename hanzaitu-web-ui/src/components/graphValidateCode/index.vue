<template>
    <div>
        <div class="silder-box">
            <div class="silder-range" :class="rangeStatus?'success':''">
                <i class="hk-btn" @mousedown="rangeMove" v-if="!rangeStatus"></i>
                <span class="silder-text-box">
                    <i class="text-icon" v-if="rangeStatus"></i>
                    {{rangeStatus?successText:startText}}
                </span>
            </div>
            <div class="refresh-box">
                <el-button type="text" @click="onRefresh" style="width: 100%;height:100%;color: #fff">刷新</el-button>
            </div>
        </div>

        <div class="code-box" v-show="dialogVisible">
            <div class="mark"></div>
            <div class="code-cont">
                <slideverify :l="42"  ref="dialogopen"
                             :r="10"
                             :w="360"
                             :h="197"
                             :block_y="block_y"
                             :imgurl="imgurl"
                             :miniimgurl="miniimgurl"
                             @success="onSuccess"
                ></slideverify>
            </div>
        </div>
    </div>
</template>

<script>
    import slideverify from '@/components/graphValidateCode/slide-verify.vue'
    export default {
        name: "index",
        components:{slideverify},
        props: {
            //成功文字
            successText: {
                type: String,
                default: "验证成功"
            },
            //开始的文字
            startText: {
                type: String,
                default: "请拖动滑块还原图片"
            }
        },
        data(){
            return {
                rangeStatus:'',
                dialogVisible:false,
                imgurl:"",
                miniimgurl:"",
                block_y:0,
            }
        },
        mounted() {
            this.imgurl = ''
            this.miniimgurl = ''
            this.getImageVerifyCode();
        },
        methods:{
            rangeMove(e){
                this.openCode();
                this.$refs.dialogopen.sliderDown(e);
                let ele = e.target;
                let startX = e.clientX;
                let eleWidth = ele.offsetWidth;
                let parentWidth =  ele.parentElement.offsetWidth;
                let MaxX = parentWidth - eleWidth;
                if(this.rangeStatus){//不运行
                    return false;
                }
                document.onmousemove = (e) => {
                    let endX = e.clientX;
                    this.disX = endX - startX;
                    if(this.disX<=0){
                        this.disX = 0;
                    }
                    if(this.disX>=MaxX-eleWidth){//减去滑块的宽度,体验效果更好
                        this.disX = MaxX;
                    }
                    ele.style.transition = '.1s all';
                    ele.style.transform = 'translateX('+this.disX+'px)';
                    e.preventDefault();
                }
                document.onmouseup = ()=> {
                    this.closeCode();
                    ele.style.transition = '.5s all';
                    ele.style.transform = 'translateX(0)';
                    this.$emit("failed", this.rangeStatus);
                    document.onmousemove = null;
                    document.onmouseup = null;
                }
            },

            openCode(){
                this.dialogVisible = true;
            },
            closeCode(){
                this.dialogVisible = false;
            },
            getImageVerifyCode: function() {
                var that = this
                this.$https('ImageVerifyCode', {}).then(res => {
                    if (!res || res.code != '200') {
                        /*this.$message({
                            showClose: true,
                            message: '获取图形验证码失败，请重试',
                            type: 'error'
                        })*/
                    } else {
                        var imgobj = JSON.parse(res.data)
                        that.block_y = imgobj.yHeight;
                        that.imgurl =  'data:image/png;base64,' + imgobj.bigImage;
                        that.miniimgurl = 'data:image/png;base64,' + imgobj.smallImage;
                        that.chenckMoveid = imgobj.chenckMoveid;
                        if (that.$refs.dialogopen) {
                            that.$refs.dialogopen.reset(imgobj.yHeight)
                        }
                    }
                })
            },
            onRefresh() {
                this.rangeStatus = false;
                this.imgurl = '';
                this.miniimgurl = '';
                this.getImageVerifyCode();
                this.$emit("refresh",this.chenckMoveid);
            },
            onSuccess(e){
                this.$https('verifyImageVerifyCode', {
                    "chenckMoveid": this.chenckMoveid,
                    "removing": e
                }).then(res => {
                    if (res.code == '200') {
                        this.rangeStatus = true;
                        let objStr = {
                            "chenckMoveid": this.chenckMoveid,
                            "removing": e
                        }
                        let str = JSON.stringify(objStr);
                        this.$emit("success",str);
                    }else{
                        this.$message({
                            showClose: true,
                            message: '验证失败！',
                            type: 'error'
                        })
                        this.onRefresh();
                        this.$emit("success",null);
                    }
                })
            },
        }
    }
</script>

<style lang="scss" scoped>
    .silder-box{
        display: flex;
        justify-content: space-between;
        align-items: center;
        width: 100%;
        .silder-range{
            width: calc(100% - 14rem);
            border-radius: 0.6rem;
            .silder-text-box{
                display: flex;
                justify-content: center;
                align-items: center;
                .text-icon{
                    width: 2.4rem;
                    height: 2.4rem;
                    background: url("https://image.hanzaitu.com/static/home/icon_success_24.png") no-repeat;
                    background-size: 100%;
                    margin-right: 0.8rem;
                }
            }

        }
        .refresh-box{
            width: 12.8rem;
            height: 4rem;
            background: #131313;
            border-radius: 0.6rem;
            display: flex;
            justify-content: center;
            align-items: center;
        }
    }
    .code-box{
        position: fixed;
        width: 100%;
        height: 100%;
        left: 0;
        top: 0;
        z-index: 9;
        .mark{
            width: 100%;
            height: 100%;
            background: rgba(0,0,0,0.5);
        }
        .code-cont{
            position: absolute;
            top: 32%;
            left: 52%;
            width: 38rem;
            height: 38rem;
            background-color: #fff;
            padding: 1rem;
            z-index: 99;
            border-radius: 1rem;
        }
    }
    .silder-range{
        background-color: #131313;
        position: relative;
        transition: 1s all;
        user-select: none;
        color: #BDBDBD;
        display: flex;
        justify-content: center;
        align-items: center;
        height: 4rem; /*no*/
    }
    .silder-range .hk-btn{
        position: absolute;
        left: 0;
        width: 5rem;/*no*/
        height: 5rem;
        color: #919191;
        background: url("https://image.hanzaitu.com/static/home/icon_slide_76.png") no-repeat;
        background-size: 100%;
        cursor: pointer;
    }
    .silder-range.success{
        background-color: #BD5BB9;
        color: #fff;
    }
    .silder-range.success .hk-btn{
        color: #fff;
    }
</style>