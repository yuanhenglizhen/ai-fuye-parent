<template>
    <div class="chat-frame-box">
        <div class="select-model-box">
            <el-select v-model="modelType" placeholder="请选择" size="medium" popper-class="model-down-box">
                <el-option v-for="(item,index) in modelTypeList" :key="index" :label="item.model" :value="item.model"></el-option>
            </el-select>
        </div>
        <div class="input-show-box" ref="articleWrapper" v-if="chatList.length > 0">
            <div class="quiz-item" v-for="(item,index) in chatList" :key="index">
                <div class="quiz-item-cont">
                    <div class="avatar-me" v-if="item.role == 'user'"></div>
                    <div class="avatar-ai" v-else></div>
                    <div class="text-cont">
                        <pre>{{item.content}}</pre>
                    </div>
                </div>
                <div class="quiz-item-icon">
                    <el-tooltip class="item" effect="dark" content="收藏" placement="top" v-if="item.role == 'user'">
                        <div :class="[item.collectId != null ? 'activeClass' : 'icon1', 'icon-btn']" @click="collectCallWork(item,index)"></div>
                    </el-tooltip>
                    <el-tooltip class="item" effect="dark" content="刷新" placement="top" v-if="item.role == 'user'">
                        <div class="icon-btn icon3" @click="sendAgain(item.content)"></div>
                    </el-tooltip>
                    <el-tooltip class="item" effect="dark" content="复制" placement="top">
                        <div class="icon-btn icon4" v-clipboard:copy="item.content"  v-clipboard:success="onCopy"></div>
                    </el-tooltip>
                </div>
            </div>
        </div>
        <div class="aiChat-input-tag" v-else>
            <div class="img-box"><img src="../../../assets/img/aiChat/Group3733@2x.png" alt=""></div>
            <div class="text">探索文字世界的魅力 提高文字工作效率</div>
            <div class="subtext">你好，请在下方输入框尝试向我提问，精彩世界静待开启</div>
        </div>
        <div class="input-box" ref="inputBox">
            <div class="input-box-cont">
                <div class="cont-left">
                    <el-input class="cont-textarea" type="textarea" :disabled="inputDisabled"
                        placeholder="请输入消息" v-model="textData" :autosize="{ minRows: 1}" resize="none"
                              @keydown.enter.native="chatSubmit" ></el-input>
                </div>
                <button ref="chatSubmit" slot="append" class="cont-right" :disabled="inputDisabled" @click="chatSubmit"></button>
            </div>
        </div>
    </div>
</template>

<script>
    import {randomString} from "@/utils/tools";
    import {EventSourcePolyfill} from 'event-source-polyfill';
    export default {
        name: "chatFrame",
        components:{},
        props:{
            chatData:{
                type:Array,
                default:()=>{
                    return []
                }
            },
            winId:{
                type:String,
                default:null,
            },
            textStr:{
                type:String,
                default:null,
            }
        },
        data(){
          return{
              textData:"",
              chatList:[],
              inputDisabled:false,
              currentAiData:{},
              modelTypeList:[],
              modelType:"gpt-3.5-turbo",
          }
        },
        watch:{
            chatData: {
                handler(val) {
                    if (val && val.length > 0) {
                        this.chatList = val;
                        this.handleScroll();
                    }else{
                        this.chatList = []
                    }

                },
                deep: true
            },
            textStr(val){
               this.textData = val;
            }
        },
        mounted() {
            this.getModelTypeList();
        },
        methods:{
            getModelTypeList(){
                this.$https('modelTypeGpt4', {}).then(res => {
                    if (res.code == 200) {
                        this.modelTypeList = res.data;
                    } else {
                        this.$message.error('请求失败！'+res.msg)
                    }
                })
            },
            handleScroll() {
                this.$nextTick(() => {
                    let scrollElem = this.$refs.articleWrapper;
                    scrollElem.scrollTop = scrollElem.scrollHeight - scrollElem.clientHeight;
                });
            },
            // 复制成功
            onCopy(e){
                this.$message({
                    message:'复制成功！',
                    type:'success'
                })
            },
            sendAgain(str){
                this.textData = str;
                if(this.winId != null){
                    let msgId = randomString(14);
                    if(this.textData != "" && this.textData != null){
                        let obj = {
                            content:this.textData,
                            role:"user",
                            winId:this.winId,
                            msgId:msgId,
                        }
                        this.chatList.push(obj)
                        if(this.chatList.length == 1){
                            this.$emit("inputStr",this.textData);
                        }
                        this.sseChat(this.textData);
                    }else{
                        this.$message.error("请输入消息！");
                    }
                }
            },
            chatSubmit(e){
                if (e.keyCode == 13) {
                    if (!e.metaKey) {
                        e.preventDefault();
                    } else {
                        this.textData = this.textData + '\n';
                    }
                }
                if(this.winId != null){
                    let msgId = randomString(14);
                    if(this.textData != "" && this.textData != null){
                        let obj = {
                            content:this.textData,
                            role:"user",
                            winId:this.winId,
                            msgId:msgId,
                        }
                        this.chatList.push(obj)
                        if(this.chatList.length == 1){
                            this.$emit("inputStr",this.textData);
                        }
                        this.sseChat(this.textData);
                    }else{
                        this.$message.error("请输入消息！");
                    }
                }
            },
            collectCallWork(obj,i){
                if(this.inputDisabled == false){
                    this.$https('ADDCOLLECT', {
                        content:obj.content,
                        operate:"add",
                        msgId:obj.msgId
                    }).then(res => {
                        if (res.code == 200) {
                            this.$emit('refreshData',this.winId);
                        } else {
                            this.$message.error('请求失败！'+res.msg)
                        }
                    })
                }else{
                    this.$message.error('请等待AI回答完毕！')
                }

            },
            sseChat(prompt) {
                let msgId = randomString(14);
                //双不等号，可以避免为空值的判断
                let _that = this;
                if (!!window.EventSource) {
                    let pro = encodeURIComponent(prompt);
                    const token = window.localStorage.getItem('token')
                    var source = new EventSourcePolyfill(`http://localhost:6679/chat/gpt4/sse?winId=${_that.winId}&prompt=${pro}&msgId=${msgId}&model=${_that.modelType}`,{
                        headers: {
                            'Access-Token-Hzt': token,
                        }
                    });//请求路径
                    let numss = _that.chatList.length;
                    //收到服务器消息
                    _that.inputDisabled = true;
                    _that.$emit("isInput",_that.inputDisabled);
                    let obj = {
                        content:"",
                        role:"assistant",
                        winId:_that.winId,
                        msgId:msgId,
                    }
                    _that.chatList.push(obj);
                    //建立连接
                    source.onopen = function (evt) {
                        console.log("server connect successed");
                    }
                    source.onmessage = function (e) {
                        _that.inputDisabled = true;
                        _that.$emit("isInput",_that.inputDisabled);
                        _that.textData = "";
                        let resData = JSON.parse(e.data);
                        _that.chatList[numss].content += resData.content;
                    };

                    //发送错误
                    source.onerror = function (evt) {
                        console.log(evt)
                        _that.inputDisabled = false;
                        _that.$emit("isInput",_that.inputDisabled);
                        _that.textData = "";
                        source.close()
                    }
                } else {
                    console.log("你的浏览器不支持sse ")
                }
            },

        }
    }
</script>

<style src="../../../assets/css/aiChat.scss" lang="scss" scoped></style>