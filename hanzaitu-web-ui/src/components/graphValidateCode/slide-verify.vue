<template>
    <div>
        <div class="tit-box">请拖动左侧滑块还原图片</div>
        <div class="slide-verify" id="slideVerify" :style="widthlable" onselectstart="return false;">
            <canvas :width="w" :height="h" ref="canvas" ></canvas>
            <canvas :width="w" :height="h" ref="block"  class="slide-verify-block"></canvas>
            <!--<div @click="refresh" class="slide-verify-refresh-icon"></div>-->
            <div class="slide-verify-slider" :style="widthlable" :class="{'container-active': containerActive, 'container-success': containerSuccess, 'container-fail': containerFail}">
                <div class="slide-verify-slider-mask" :style="{width: sliderMaskWidth}">
                    <!-- slider -->
                    <div @mousedown="sliderDown"
                         @touchstart="touchStartEvent"
                         @touchmove="touchMoveEvent"
                         @touchend="touchEndEvent"
                         class="slide-verify-slider-mask-item"
                         :style="{left: sliderLeft}">
                        <div class="slide-verify-slider-mask-item-icon"></div>
                    </div>
                </div>
                <span class="slide-verify-slider-text">{{sliderText}}</span>
            </div>
        </div>
    </div>

</template>
<script>
    const PI = Math.PI;

    function sum(x, y) {
        return x + y
    }

    function square(x) {
        return x * x
    }
    export default {
        name: 'SlideVerify',
        props: {
            // block length
            l: {
                type: Number,
                default: 42,
            },
            // block radius
            r: {
                type: Number,
                default: 10,
            },
            // canvas width
            w: {
                type: Number,
                default: 310,
            },
            // canvas height
            h: {
                type: Number,
                default: 155,
            },
            block_x: {
                type: Number,
                default: 155,
            },
            block_y: {
                type: Number,
                default: 20,
            },
            sliderText: {
                type: String,
                default: '',
            },
            imgurl: {
                type: String,
                default: '',
            },
            miniimgurl: {
                type: String,
                default: '',
            },

        },
        data() {
            return {
                containerActive: false, // container active class
                containerSuccess: false, // container success class
                containerFail: false, // container fail class
                canvasCtx: null,
                blockCtx: null,
                block: null,
                canvasStr: null,
                //block_x: undefined, // container random position
                // block_y: undefined,
                L: this.l + this.r * 2 + 3, // block real lenght
                img: undefined,
                originX: undefined,
                originY: undefined,
                isMouseDown: false,
                trail: [],
                widthlable:'',
                sliderLeft: 0, // block right offset
                sliderMaskWidth: 0, // mask width
            }
        },
        mounted() {
            //随机生成数this.block_x =
            this.init()
        },
        methods: {
            init() {
                this.initDom()
                this.bindEvents()
                this.widthlable ='width:'+this.w+'px;'
            },
            initDom() {
                this.block = this.$refs.block;
                this.canvasStr = this.$refs.canvas;

                this.canvasCtx = this.canvasStr.getContext('2d')
                this.blockCtx = this.block.getContext('2d')
            },
            initImg(h) {
                var that = this
                const img = document.createElement('img');
                img.onload = onload;
                img.onerror = () => {
                    img.src = that.imgurl
                }
                img.src = that.imgurl
                img.onload = function(){
                    that.canvasCtx.drawImage(img, 0, 0, that.w, that.h)
                }

                this.img=img
                const img1 = document.createElement('img');
                var  blockCtx = that.blockCtx
                var block_y = h||that.block_y
                img1.onerror = () => {
                    img1.src = that.miniimgurl
                }
                img1.src = that.miniimgurl
                img1.onload = function(){
                    blockCtx.drawImage(img1, 0, block_y, 51, 51)
                }
            },
            refresh() {
                this.$emit('refresh')
            },
            sliderDown(event) {
                this.originX = event.clientX;
                this.originY = event.clientY;
                this.isMouseDown = true;
            },
            touchStartEvent(e) {
                this.originX = e.changedTouches[0].pageX;
                this.originY = e.changedTouches[0].pageY;
                this.isMouseDown = true;
            },
            bindEvents() {
                document.addEventListener('mousemove', (e) => {
                    if (!this.isMouseDown) return false;
                    const moveX = e.clientX - this.originX;
                    const moveY = e.clientY - this.originY;
                    if (moveX < 0 || moveX + 38 >= this.w) return false;
                    this.sliderLeft = moveX + 'px';
                    let blockLeft = (this.w - 40 - 20) / (this.w - 40) * moveX;
                    this.block.style.left = blockLeft + 'px';

                    this.containerActive = true; // add active
                    this.sliderMaskWidth = moveX + 'px';
                    this.trail.push(moveY);
                });
                document.addEventListener('mouseup', (e) => {
                    if (!this.isMouseDown) return false
                    this.isMouseDown = false
                    if (e.clientX === this.originX) return false;
                    this.containerActive = false; // remove active
                    this.verify();
                    this.sliderLeft = 0;
                    this.block.style.left = 0;
                    this.sliderMaskWidth = 0;
                })
            },
            touchMoveEvent(e) {
                if (!this.isMouseDown) return false;
                const moveX = e.changedTouches[0].pageX - this.originX;
                const moveY = e.changedTouches[0].pageY - this.originY;
                if (moveX < 0 || moveX + 38 >= this.w) return false;
                this.sliderLeft = moveX + 'px';
                let blockLeft = (this.w - 40 - 20) / (this.w - 40) * moveX;
                this.block.style.left = blockLeft + 'px';

                this.containerActive = true;
                this.sliderMaskWidth = moveX + 'px';
                this.trail.push(moveY);
            },
            touchEndEvent(e) {
                if (!this.isMouseDown) return false
                this.isMouseDown = false
                if (e.changedTouches[0].pageX === this.originX) return false;
                this.containerActive = false;
                this.verify()
            },
            verify() {
                const arr = this.trail // drag y move distance
                const average = arr.reduce(sum) / arr.length // average
                const deviations = arr.map(x => x - average) // deviation array
                const stddev = Math.sqrt(deviations.map(square).reduce(sum) / arr.length) // standard deviation
                const left = parseInt(this.block.style.left)
                this.$emit('success',left)

            },
            reset(h) {
                this.containerActive = false;
                this.containerSuccess = false;
                this.containerFail = false;
                this.sliderLeft = 0;
                this.block.style.left = 0;
                this.sliderMaskWidth = 0;
                this.canvasCtx.clearRect(0, 0, this.w, this.h)
                this.blockCtx.clearRect(0, 0, this.w,this.h)
                this.initImg(h)

            }
        }
    }
</script>
<style scoped>
    .tit-box{
        font-size: 2rem;
        margin-bottom: 1rem;
    }
    .slide-verify {
        position: relative;
        width: 310px;
    }

    .slide-verify-block {
        position: absolute;
        left: 0;
        top: 0
    }

    /*.slide-verify-refresh-icon {
        position: absolute;
        right: 0;
        top: 0;
        width: 34px;
        height: 34px;
        cursor: pointer;
        background: url("../../assets/img/home/icon_slide_76.png") 0 -437px;
        background-size: 34px 471px
    }*/

    .slide-verify-slider {
        position: relative;
        text-align: center;
        width: 310px;
        height: 5rem;
        line-height: 5rem;
        margin-top: 1rem;
        background: #EDEDED;
        color: #45494c;
        border-radius: 8px;
    }

    .slide-verify-slider-mask {
        position: absolute;
        left: 0;
        top: 0;
        height: 6rem;
    }

    .slide-verify-slider-mask-item {
        position: absolute;
        top: 0;
        left: 0;
        border-radius: 1rem;
        width: 5rem;
        height: 5rem;
        background: #fff;
        box-shadow: 4px 4px 8px 1px rgba(0,0,0,0.25);
        cursor: pointer;
        transition: background .2s linear
    }

    .slide-verify-slider-mask-item-icon {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: url("https://image.hanzaitu.com/static/home/icon_slide_76.png") no-repeat;
        background-size: 100%;
        border-radius: 1rem;
    }
    .container-active .slide-verify-slider-mask-item {
        height:5rem;
        top: 0;
    }

    .container-active .slide-verify-slider-mask {
        height: 5rem;
        border-width: 1px;
    }

    .container-success .slide-verify-slider-mask-item {
        height: 5rem;
        top: -1px;
        border: 1px solid #52CCBA;
        background-color: #52CCBA !important;
    }

    .container-success .slide-verify-slider-mask {
        height: 5rem;
        border: 1px solid #52CCBA;
        background-color: #D2F4EF;
    }

    .container-success .slide-verify-slider-mask-item-icon {
        background-position: 0 0 !important;
    }

    .container-fail .slide-verify-slider-mask-item {
        height: 5rem;
        top: -1px;
        border: 1px solid #f57a7a;
        background-color: #f57a7a !important;
    }

    .container-fail .slide-verify-slider-mask {
        height: 5rem;
        border: 1px solid #f57a7a;
        background-color: #fce1e1;
    }

    .container-fail .slide-verify-slider-mask-item-icon {
        top: 14px;
        background-position: 0 -82px !important;
    }

    .container-active .slide-verify-slider-text,
    .container-success .slide-verify-slider-text,
    .container-fail .slide-verify-slider-text {
        display: none;
    }
</style>