<template>
    <div class="ai-painting-box">
        <div class="painting-left">
            <div class="left-tit">
                <div class="text">绘画记录</div>
                <!--<i class="el-icon-s-operation"></i>-->
            </div>
            <div class="left-cont" ref="scrollDrawList" @scroll="handleScrollDraw" v-if="drawingRecordList.length > 0">
                <div class="img-item-list" :class="[{'imgItemActive':imgItemActive == index && item.action != 'DESCRIBE'},{'imgItemActiveRed':imgItemActive == index && item.action == 'DESCRIBE'}]"
                     @click="selectImgList(item,index)" v-for="(item,index) in drawingRecordList" :key="index">
                    <div class="el-image" style="width: 100%" v-if="item.status == 'IN_PROGRESS' || item.status == 'NOT_START' || item.status == 'SUBMITTED'">
                        <div class="image-slot" style="width: 10.8rem;height: 13.8rem;margin: 2.5rem auto;">
                            <img src="https://image.hanzaitu.com/static/aiPainting/Group3735@2x.png" alt="">
                            <div style="margin-top: 0.8rem;text-align: center">正在加载中~</div>
                        </div>
                    </div>
                    <el-image style="width: 100%;" fit="scale-down" :src="item.picture" v-else>
                        <div slot="error" class="image-slot" style="width: 10.8rem;height: 13.8rem;margin: 2.5rem auto;">
                            <img src="https://image.hanzaitu.com/static/aiPainting/Group3735@2x.png" alt="">
                            <div style="margin-top: 0.8rem;text-align: center">加载失败啦~</div>
                        </div>
                    </el-image>
                </div>
            </div>
            <div class="left-cont" v-else>
                <div class="left-cont-no">
                    <img src="https://image.hanzaitu.com/static/aiPainting/huihuaweikong@2x.png" alt="">
                </div>
            </div>
        </div>
        <div class="painting-center">
            <div class="painting-cont">
                <div class="painting-cont-top">
                    <div class="painting-input-box" v-if="firstInput">
                        <div class="content">
                            <div class="yesImg" v-if="progressFlag">
                                <el-image
                                        style="width: 60rem; height: 64rem"
                                        fit="scale-down"
                                        :src="drawingRecordObj.picture"
                                        :preview-src-list="[drawingRecordObj.picture]">
                                    <div slot="error" class="image-slot" style="width: 30rem;height: 30rem;margin:25rem auto 0 auto;text-align: center">
                                        <img src="https://image.hanzaitu.com/static/aiPainting/chucuo@2x.png" alt="">
                                        <div>哎呀出错了~请检查您的提示词和参数~</div>
                                    </div>
                                    <div slot="placeholder" class="image-slot" style="width: 30rem;height: 30rem;margin:25rem auto 0 auto;text-align: center">
                                        <img src="https://image.hanzaitu.com/static/aiPainting/jiazai@2x.png" alt="">
                                        <div>正在加载，请耐心等待片刻</div>
                                    </div>
                                </el-image>
                            </div>
                            <div class="inPainting-box" v-else>
                                <img src="https://image.hanzaitu.com/static/aiPainting/jiazai@2x.png" alt="">
                                <div class="progress-text">正在画图中{{progressDatas}}%，请耐心等待片刻</div>
                                <div class="progress-boxs">
                                    <el-progress
                                            :width="240"
                                            :percentage="progressDatas"
                                            :show-text="false"
                                            :stroke-width="16"
                                            color="#836CF7"
                                            define-back-color="#1A1E2D"
                                    ></el-progress>
                                </div>
                            </div>
                        </div>
                        <div class="cont-right-box">
                            <div class="cont-right" v-if="drawingRecordObj.action == 'IMAGINE'
                             || drawingRecordObj.action == 'VARIATION' || drawingRecordObj.action == 'HIGH_VARIATION'
                             || drawingRecordObj.action == 'LOW_VARIATION' || drawingRecordObj.action == 'LOW_ZOOM'
                             || drawingRecordObj.action == 'HIGH_ZOOM'">
                                <div class="title">图片提示词</div>
                                <pre class="cont-text">{{drawingRecordObj.promptEn}}</pre>
                                <div class="imgHandle">
                                    <div class="title">
                                        <div class="left">图片放大</div>
                                        <div class="right">对左侧的其中张图片进行放大</div>
                                    </div>
                                    <div class="imgHandle-btn-box">
                                        <div @click="selectUV('UPSCALE',1)">U1<br/>左上</div>
                                        <div @click="selectUV('UPSCALE',2)">U2<br/>右上</div>
                                        <div @click="selectUV('UPSCALE',3)">U3<br/>左下</div>
                                        <div @click="selectUV('UPSCALE',4)">U4<br/>右下</div>
                                    </div>
                                </div>
                                <div class="imgHandle">
                                    <div class="title">
                                        <div class="left">变化图片</div>
                                        <div class="right">对左侧的其中张图片进行多样化修改</div>
                                    </div>
                                    <div class="imgHandle-btn-box">
                                        <div @click="selectUV('VARIATION',1)">V1<br/>左上</div>
                                        <div @click="selectUV('VARIATION',2)">V2<br/>右上</div>
                                        <div @click="selectUV('VARIATION',3)">V3<br/>左下</div>
                                        <div @click="selectUV('VARIATION',4)">V4<br/>右下</div>
                                    </div>
                                </div>
                                <div class="more-box">
                                    <div class="more-title">更多操作</div>
                                    <div class="more-cont">
                                        <div class="more-btn" @click="redraw()">
                                            <i class="i1"></i>
                                            <span>重新生成</span>
                                        </div>
                                        <div class="more-btn" @click="delDrawingList">
                                            <i class="i2"></i>
                                            <span>删除图片</span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="cont-right-imgText" v-else>
                                <div class="title">提示词</div>
                                <div class="cont"><pre>{{drawingRecordObj.prompt}}</pre></div>
                                <div class="more-imgText-cont">
                                    <div class="prompt-tag-box" v-if="drawingRecordObj.action == 'DESCRIBE'">
                                        <div class="prompt-item" @click="selectTextImg(0)">1️⃣</div>
                                        <div class="prompt-item" @click="selectTextImg(1)">2️⃣</div>
                                        <div class="prompt-item" @click="selectTextImg(2)">3️⃣</div>
                                        <div class="prompt-item" @click="selectTextImg(3)">4️⃣</div>
                                    </div>
                                    <div class="mt20" v-if="drawingRecordObj.action == 'UPSCALE'">
                                        <div class="title">图片变化<span class="ml10" style="color: #94A3B8;font-size: 12px;">
                                            对左侧的图片进行二次变化</span></div>
                                        <div class="more-btn-box mt15">
                                            <div class="more-btn" @click="changeImage(0)">
                                                <i class="i4"></i>
                                                <span>强力变化</span>
                                            </div>
                                            <div class="more-btn" @click="changeImage(1)">
                                                <i class="i4"></i>
                                                <span>微小变化</span>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="mt20" v-if="drawingRecordObj.action == 'UPSCALE'">
                                        <div class="title">景别缩小<span class="ml10" style="color: #94A3B8;font-size: 12px;">
                                            对左侧的图片进行景别缩小</span></div>
                                        <div class="more-btn-box mt15">
                                            <div class="more-btn" @click="lessenImage(0)">
                                                <i class="i5"></i>
                                                <span>缩小2倍</span>
                                            </div>
                                            <div class="more-btn" @click="lessenImage(1)">
                                                <i class="i5"></i>
                                                <span>缩小1.5倍</span>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="mt60">
                                        <div class="title">更多操作</div>
                                        <div class="more-btn-box mt15">
                                            <div class="more-btn" @click="regenText" v-if="drawingRecordObj.action == 'DESCRIBE'">
                                                <i class="i1"></i>
                                                <span>重新生成</span>
                                            </div>
                                            <div class="more-btn" @click="downloadImg(drawingRecordObj.picture)" v-if="drawingRecordObj.action == 'UPSCALE'">
                                                <i class="i3"></i>
                                                <span>下载图片</span>
                                            </div>
                                            <div class="more-btn" @click="delDrawingList">
                                                <i class="i2"></i>
                                                <span>删除图片</span>
                                            </div>
                                        </div>
                                        <div class="more-btn-box mt15">
                                            <div class="more-btn" @click="publishImg(drawingRecordObj.id)" v-if="drawingRecordObj.action == 'UPSCALE'">
                                                <i class="i6"></i>
                                                <span>发布图片</span>
                                            </div>
                                            <div class="more-btn" @click="creationCollection(drawingRecordObj.id)" v-if="drawingRecordObj.action == 'UPSCALE'">
                                                <i class="i7"></i>
                                                <span v-if="!collectFlag">创作收藏</span>
                                                <span v-else>取消收藏</span>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                    </div>
                    <div class="painting-input-tag" v-else>
                        <div class="img-box"><img src="https://image.hanzaitu.com/static/aiPainting/morenchatu@2x.png" alt=""></div>
                        <div class="text">简单一条提示词，开启绘画新世界大门</div>
                        <div class="subtext">你好，请在下方输入框尝试向我提问，精彩世界静待开启</div>
                    </div>
                </div>
                <div class="input-box" ref="inputBox">
                    <div class="input-box-cont">
                        <div class="cont-left">
                            <el-input class="cont-textarea" type="textarea" :disabled="inputDisabled"
                                      placeholder="请输入图片的英文描述~~中文输入暂不支持哦~！" v-model="textData" :autosize="{ minRows: 1}" resize="none"
                                      @keydown.enter.native="sendTexts" ></el-input>
                        </div>
                        <button class="cont-right" :disabled="inputDisabled" @click="sendTexts"></button>
                    </div>
                </div>
            </div>
        </div>
        <div class="painting-right">
            <div class="right-title">AI绘画</div>
            <div class="upload-tab">
                <div class="upload-tab-btn" :class="{'upload-active':uploadFlag == 0}" @click="selectUpload(0)">垫图</div>
                <div class="upload-tab-btn" :class="{'upload-active':uploadFlag == 1}" @click="selectUpload(1)">以图生文</div>
            </div>
            <div class="right-upload-box">
                <div>
                    <div class="text" v-if="isImg">
                        <p v-if="uploadFlag == 0">AI垫图 — 请上传参考图</p>
                        <p v-else>AI以图生文 — 请上传参考图</p>
                        <p>支特JPG.PNG.1OM以内</p>
                    </div>
                    <div class="upload-img-box" v-else>
                        <img :src="imgTextUrl" alt="" v-if="imgTextUrl != null">
                    </div>
                </div>
                <div>
                    <div v-if="uploadBtnFlag">
                        <el-upload
                                :action="uploadImgUrl"
                                :auto-upload="true"
                                :show-file-list="false"
                                :multiple="false"
                                :limit="1"
                                :headers="headers"
                                :before-upload="handleBeforeUpload"
                                :on-success="handleUploadSuccess">
                            <div class="upload-btn"><img src="https://image.hanzaitu.com/static/aiPainting/Group26@2x.png" alt=""></div>
                        </el-upload>
                    </div>
                    <div v-else>
                        <div class="upload-btn" @click="sendImgText">
                            <img src="https://image.hanzaitu.com/static/aiPainting/yitushengwenfasong@2x.png" alt="">
                        </div>
                    </div>
                </div>
            </div>
            <div class="size-box">
                <div class="size-title">输出尺寸</div>
                <div class="size-cont">
                    <div class="size-cont-item" :class="{'sizeActive':sizeActive == index}"
                         v-for="(item,index) in sizeList" :key="index" @click="selectSize(item.text,index)">
                        <div class="item-icon i1"></div>
                        <div class="text">{{item.text}}</div>
                    </div>

                    <div class="size-cont-item" :class="{'sizeActive':sizeActive == -1}"
                         @click="selectSize('custom',-1)">
                        <div>自定义尺寸</div>
                        <div class="text customSize">
                            <input type="number" v-model="customStrat">
                            <div>:</div>
                            <input type="number" v-model="customEnd">
                        </div>
                    </div>
                </div>
            </div>
            <div class="prompt-box">
                <div class="prompt-tit">提示词推荐</div>
                <div class="prompt-tab">
                    <div class="prompt-tab-item" :class="{'itemActive':callWordActive == index}"
                         v-for="(item,index) in callWordType" :key="index+'v'"
                         @click="selectCallWordType(index,item.classifyList)">{{item.category}}</div>
                </div>
                <div class="prompt-cont">
                    <div class="prompt-item" v-for="(item,index) in callWordData" :key="index+'k'">
                        <div class="prompt-item-tit" @click="flexTags(item,index)">
                            <div><i class="el-icon-caret-bottom"></i></div>
                            <div class="ml5">{{item.categoryName}}</div>
                            <div class="tag ml10">{{item.categoryContent.length?item.categoryContent.length:null}}</div>
                        </div>
                        <div class="prompt-item-cont" v-show="item.flag == true">
                            <div class="cont-item" v-for="(items,i) in item.categoryContent" :key="i+'n'" @click="selectCallWord(items.key)">
                                <div class="item-key">{{items.key}}</div>
                                <div class="item-val" v-if="items.value">{{items.value}}</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    export default {
        name: "aiPainting",
        data(){
            return {
                token:null,
                inputDisabled:false,
                textData:"",

                color: '',
                scoketText: '',

                progressFlag:false,
                progressDatas:0,
                drawingRecordObj:{
                    imageUrl:"",
                    action:"IMAGINE"
                },

                firstInput:false,
                drawingRecordList:[],
                imgItemActive:0,
                sizeActive:0,
                sizeList:[
                    {text:"1:1"},
                    {text:"3:4"},
                    {text:"4:3"},
                    {text:"16:9"},
                    {text:"9:16"},
                ],
                sizeData:"--ar 1:1",
                customStrat:null,
                customEnd:null,
                uploadFlag:0,
                uploadBtnFlag:true,
                isImg:true,

                uploadImgUrl: this.baseUrl + "/draw/trigger/upload-img", // 上传的图片服务器地址
                headers: {
                    'Access-Token-Hzt': window.localStorage.getItem("token"),
                },
                imgTextUrl:null,
                callWordType:[],
                callWordActive:0,
                callWordData:{},

                collectFlag:false,//是否收藏
                drawListParams:{
                    "pageNo": 1,
                    "pageSize": 10,
                    "allowPaging": true
                },
                totalPages:0,
            }
        },
        mounted() {
            this.token = window.localStorage.getItem("token") || null;
            if(this.token != null){
                this.initWebSocket();
                this.getDrawingList(0);
                this.getCallWordLists();
            }
        },
        methods:{
            //获取绘画记录列表
            getDrawingList(num){
                this.$https('DRAWINGLIST',this.drawListParams,0).then(res => {
                    if (res.code == 200) {
                        this.totalPages = res.data.totalPages;
                        res.data.list.map((item)=>{
                            this.drawingRecordList.push(item);
                        })
                        if(num == 0 && this.drawingRecordList.length > 0){
                            this.selectImgList(this.drawingRecordList[0],0);
                        }
                    } else {
                        this.$message.error('请求失败！'+res.msg)
                    }
                })
            },
            handleScrollDraw(event) {
                let el=event.target;
                if(Math.ceil(el.scrollTop+el.clientHeight) >= el.scrollHeight){
                    if(this.drawListParams.pageNo <= this.totalPages){
                        this.drawListParams.pageNo += 1;
                        this.getDrawingList(1);
                    }
                }
            },
            //选择绘画记录列表
            selectImgList(obj,i){
                this.imgItemActive = i
                this.firstInput = true;
                this.$GET(this.baseUrl + '/draw/task/'+obj.id+'/fetch', {},1).then(res => {
                    if (res.code == 200) {
                        if(res.data){
                            this.drawingRecordObj = res.data;
                            this.collectFlag = res.data.collectStatus;
                            if(this.drawingRecordObj.status == "IN_PROGRESS" || this.drawingRecordObj.status == "NOT_START" || this.drawingRecordObj.status == "SUBMITTED"){
                                this.progressFlag = false;
                                this.progressDatas = res.data.progress;
                                let timeIntval = null;
                                if(timeIntval != null || this.progressDatas >= 99){
                                    clearInterval(timeIntval);
                                }
                                timeIntval= setInterval(()=>{
                                    this.progressDatas += 1;
                                    if(this.drawingRecordObj.action=="IMAGINE" && this.progressDatas >= 13){
                                        clearInterval(timeIntval);
                                    }else if(this.drawingRecordObj.action=="UPSCALE" ||
                                        this.drawingRecordObj.action=="VARIATION" ||
                                        this.drawingRecordObj.action=="LOW_ZOOM" ||
                                        this.drawingRecordObj.action=="HIGH_ZOOM" ||
                                        this.drawingRecordObj.action=="HIGH_VARIATION" ||
                                        this.drawingRecordObj.action=="LOW_VARIATION"){
                                        if(this.progressDatas >= 99){
                                            clearInterval(timeIntval);
                                        }
                                    }
                                },2000)
                            }else{
                                this.progressFlag = true;
                            }
                        }
                    } else {
                        this.$message.error('请求失败！'+res.msg)
                    }
                })
            },
            //选择尺寸
            selectSize(text,i){
                this.sizeActive = i;
                if(i != -1){
                    this.customStrat=null;
                    this.customEnd=null;
                }
                this.sizeData = '--ar '+text;
            },
            //切换垫图和以图生文tab
            selectUpload(num){
                this.uploadFlag = num;
                if(num == 1 && this.imgTextUrl != null){
                    this.uploadBtnFlag = false;
                }else{
                    this.uploadBtnFlag = true;
                }
            },
            //上传前的回调
            handleBeforeUpload(e){
                console.log(e);
            },
            //上传成功回调
            handleUploadSuccess(e){
                if(e.code == 200){
                    this.isImg = false;
                    this.imgTextUrl = e.result;
                    window.localStorage.setItem('imgTextUrl', e.result);
                    if(this.uploadFlag == 0){
                        this.uploadBtnFlag = true;
                    }else{
                        this.uploadBtnFlag = false;
                    }
                }
            },
            //提交以图生文
            sendImgText(){
                /*this.$alert('Midjourney官方接口升级！此功能修复中！', '提示', {
                    confirmButtonText: '确定',
                    callback: action => {

                    }
                });*/

                if(this.imgTextUrl != "" && this.imgTextUrl != null){
                    this.$https('IMGGENERATEDTEXT', {
                        imgUrl:this.imgTextUrl
                    }).then(res => {
                        if (res.code == 200) {
                            this.isImg = true;
                            this.uploadBtnFlag = true;
                            this.progressFlag = false;
                            this.drawListParams.pageNo = 1;
                            this.drawingRecordList = [];
                            this.getDrawingList(0);
                        } else {
                            this.$message.error('请求失败！'+res.msg)
                        }
                    })
                }
            },

            // 发送websockwt请求
            initWebSocket() {
                let websocketUrl = this.wsUrl + '/draw/k/' + window.localStorage.getItem('token');
                // WebSocket与普通的请求所用协议有所不同，ws等同于http，wss等同于https
                this.webSock = new WebSocket(websocketUrl)
                this.webSock.onopen = this.webSocketOnOpen
                this.webSock.onerror = this.webSocketOnError
                this.webSock.onmessage = this.webSocketOnMessage
                this.webSock.onclose = this.webSocketClose
            },
            webSocketOnOpen() {
                this.scoketText = '连接成功'
                this.color = 'green'
            },
            webSocketOnMessage(e) {
                let data = JSON.parse(e.data);
                //接收数据
                if(data.progress && data.progress>0){
                    this.progressDatas = data.progress;
                }
                if(this.progressDatas == 100){debugger
                    this.progressFlag = true;
                    this.drawingRecordObj = data;
                    this.drawingRecordObj.action = data.action;
                    this.drawingRecordObj.imageUrl = data.imageUrl;
                    this.imgTextUrl = null;
                    this.$forceUpdate();
                    this.drawListParams.pageNo = 1;
                    this.drawingRecordList = [];
                    this.getDrawingList(1);
                }
            },
            webSocketClose(e) {
                this.scoketText = '断开连接'
                this.color = 'red'
                this.lockReconnect = false
                this.reconnect();
            },
            webSocketOnError(e) {
                this.$message.error('报错信息', e)
            },
            webSocketSend(Data) {
                //发送数据发送
                //this.webSock.send(Data);
                this.$https('SUBMITDRAW', Data).then(res => {
                    if (res.code == 200) {
                        this.textData = "";
                        this.firstInput = true;
                        this.progressFlag = false;
                        this.progressDatas = 0;
                        this.drawListParams.pageNo = 1;
                        this.drawingRecordList = [];
                        this.getDrawingList(0);
                    } else {
                        this.$message.error('请求失败！'+res.msg);
                    }
                })
            },
            // 断开重连操作
            reconnect() {
                if (this.lockReconnect) return
                this.lockReconnect = true
                let _this = this
                //没连接上会一直重连，设置延迟避免请求过多
                setTimeout(function () {
                    _this.initWebSocket()
                    _this.lockReconnect = false
                }, 2000)
            },
            //输入框提交
            sendTexts(e) {
                if (e.keyCode == 13) {
                    if (!e.metaKey) {
                        e.preventDefault();
                    } else {
                        this.textData = this.textData + '\n';
                    }
                }
                this.textData = this.textData.replace(/^\s*|\s*$/g, "");
                if(this.textData != null && this.textData != "" && this.token != null){
                    if(this.sizeData == "--ar custom"){
                        this.sizeData = '--ar '+this.customStrat+":"+this.customEnd;
                    }
                    if(this.imgTextUrl != null){
                        this.textData = this.imgTextUrl+" "+this.textData+" "+this.sizeData;
                    }else{
                        this.textData = this.textData+" "+this.sizeData;
                    }
                    this.drawingRecordObj.promptEn = this.textData;

                    let obj = {
                        "action":"IMAGINE",
                        "prompt":this.textData
                    }
                    this.webSocketSend(obj);
                }
            },
            //选择以图生文生成的提示词
            selectTextImg(num){
                //"1️⃣", "2️⃣", "3️⃣",  "4️⃣",
                let strArr = this.drawingRecordObj.prompt.split("1️⃣")[1];
                let str1 = strArr.split("2️⃣");
                let str2 = str1[1].split("3️⃣");
                let str3 = str2[1].split("4️⃣");
                if(num == 0){
                    this.textData = str1[0].trim()
                }else if(num == 1){
                    this.textData = str2[0].trim()
                }else if(num == 2){
                    this.textData = str3[0].trim()
                }else if(num == 3){
                    this.textData = str3[1].trim()
                }
            },
            //选择UV按钮
            selectUV(action,num){
                if(this.drawingRecordObj.action == 'IMAGINE' || this.drawingRecordObj.action == 'VARIATION'
                    || this.drawingRecordObj.action == 'LOW_ZOOM' || this.drawingRecordObj.action == 'HIGH_ZOOM'
                    || this.drawingRecordObj.action == 'HIGH_VARIATION' || this.drawingRecordObj.action == 'LOW_VARIATION'){
                    this.$https('SUBMITDRAW', {
                        "action":action,
                        "taskId":this.drawingRecordObj.id,
                        "index":num
                    }).then(res => {
                        if (res.code == 200) {
                            this.firstInput = true;
                            this.progressFlag = false;
                            this.progressDatas = 0;
                            this.drawListParams.pageNo = 1;
                            this.drawingRecordList = [];
                            this.getDrawingList(0);
                        } else {
                            this.$message.error('请求失败！'+res.msg)
                        }
                    })
                }
            },
            //重新生成图生文
            regenText(){
                if(this.drawingRecordObj.imageUrl != ""){
                    this.$https('IMGGENERATEDTEXT', {
                        imgUrl:this.drawingRecordObj.imageUrl
                    }).then(res => {
                        if (res.code == 200) {
                            this.isImg = true;
                            this.uploadBtnFlag = true;
                            this.progressFlag = false;
                            this.drawListParams.pageNo = 1;
                            this.drawingRecordList = [];
                            this.getDrawingList(0);
                        } else {
                            this.$message.error('请求失败！'+res.msg)
                        }
                    })
                }
            },
            //重新生成图片
            redraw(){
                this.textData = this.drawingRecordObj.promptEn;
                this.sendTexts();
            },
            //删除绘画记录
            delDrawingList(){
                this.$https('DELDRAWLIST', {
                    "taskId":this.drawingRecordObj.id,
                }).then(res => {
                    if (res.code == 200) {
                        this.drawListParams.pageNo = 1;
                        this.drawingRecordList = [];
                        this.getDrawingList(0);
                    } else {
                        this.$message.error('请求失败！'+res.msg)
                    }
                })
            },
            //获取绘画提示词
            getCallWordLists(){
                this.$GET('PROMPCALLWORD', {},0).then(res => {
                    if (res.code == 200) {
                        this.callWordType = res.data;
                        this.selectCallWordType(0,res.data[0].classifyList);
                    } else {
                        this.$message.error('请求失败！'+res.msg)
                    }
                })
            },
            //选择绘画提示词类型tab
            selectCallWordType(i,obj){
                this.callWordActive = i;
                this.callWordData = obj;
                this.callWordData.map((item)=>{
                    item.flag = true;
                });
            },
            selectCallWord(str){
                this.textData = this.textData+" "+str
            },
            flexTags(obj,i){
                if(this.callWordData[i].flag == false){
                    this.callWordData[i].flag = true;
                }else{
                    this.callWordData[i].flag = false;
                }
                this.$forceUpdate();
            },
            //下载
            downloadImg(imgPath){
                const image = new Image();
                // 解决跨域 Canvas 污染问题
                image.setAttribute('crossOrigin', 'anonymous');
                image.onload = function() {
                    const canvas = document.createElement('canvas');
                    canvas.width = image.width;
                    canvas.height = image.height;
                    const context = canvas.getContext('2d');
                    context.drawImage(image, 0, 0, image.width, image.height);
                    const url = canvas.toDataURL('image/png'); // 得到图片的base64编码数据
                    const a = document.createElement('a'); // 生成一个a元素
                    const event = new MouseEvent('click'); // 创建一个单击事件
                    a.download = 'img.png' || 'photo'; // 设置图片名称
                    a.href = url; // 将生成的URL设置为a.href属性
                    a.dispatchEvent(event); // 触发a的单击事件
                };
                image.src = imgPath;
            },
            //变化单图
            changeImage(num){
                let action = "";
                if(num == 0){
                    action = "HIGH_VARIATION";
                }else{
                    action = "LOW_VARIATION";
                }
                this.$https('SUBMITDRAW', {
                    "action":action,
                    "taskId":this.drawingRecordObj.id,
                }).then(res => {
                    if (res.code == 200) {
                        this.firstInput = true;
                        this.progressFlag = false;
                        this.progressDatas = 0;
                        this.drawListParams.pageNo = 1;
                        this.drawingRecordList = [];
                        this.getDrawingList(0);
                    } else {
                        this.$message.error('请求失败！'+res.msg)
                    }
                })
            },
            //缩小图片
            lessenImage(num){
                let action = "";
                if(num == 0){
                    action = "HIGH_ZOOM";
                }else{
                    action = "LOW_ZOOM";
                }
                this.$https('SUBMITDRAW', {
                    "action":action,
                    "taskId":this.drawingRecordObj.id,
                }).then(res => {
                    if (res.code == 200) {
                        this.firstInput = true;
                        this.progressFlag = false;
                        this.progressDatas = 0;
                        this.drawListParams.pageNo = 1;
                        this.drawingRecordList = [];
                        this.getDrawingList(0);
                    } else {
                        this.$message.error('请求失败！'+res.msg)
                    }
                })
            },
            //发布图片
            publishImg(id){
                this.$https('drawTriggerRelease', {
                    "taskId":id,
                }).then(res => {
                    if (res.code == 200) {
                        this.$message.success('发布成功！')
                    } else {
                        this.$message.error('请求失败！'+res.msg)
                    }
                })
            },
            creationCollection(id){
                this.$https('drawTriggerCollect', {
                    "taskId":id,
                }).then(res => {
                    if (res.code == 200) {
                        this.$message.success('操作成功！')
                        this.collectFlag = res.collectStatus;
                    } else {
                        this.$message.error('请求失败！'+res.msg)
                    }
                })
            }
        }
    }
</script>

<style src="../../../assets/css/aiPainting.scss" lang="scss" scoped></style>