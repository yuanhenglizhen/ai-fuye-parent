<template>
    <div class="ai-chat-box">
        <div class="ai-chat-left">
            <div class="left-top">
                <div class="left-top-text">聊天缓存</div>
                <!--<div class="left-top-icon"></div>-->
            </div>
            <div class="left-center">
                <div class="btnx" :class="{active:isActive === index}" v-for="(item,index) in chatList" :key="index"
                     @click="openWindow(item,index)">
                    <div class="btnx-text">{{item.winTitle}}</div>
                    <div class="edit">
                        <i class="el-icon-delete" @click.prevent.stop="delAiChat(item,index)"></i>
                    </div>
                </div>
            </div>
            <div class="left-bottom">
                <div class="btn-css" @click="addAiChat">
                    <i></i><span>新增聊天</span>
                </div>
            </div>
        </div>
        <div class="ai-chat-center">
            <chatFrame4 :winId="winId" :chatData="chatData" :textStr="textData"
                       @inputStr="getTitleStr" @refreshData="refreshChat" @isInput="isInputFlag"></chatFrame4>
        </div>
        <div class="ai-chat-right">
            <!--<div class="chat-right-top">
                <div>AI聊天提示词库</div>
                <div class="search-box">
                    <input type="text" placeholder="请输入搜索关键字" class="search-input">
                    <div class="search-icon">
                        <i class="el-icon-search iconfont"></i>
                    </div>
                </div>
            </div>-->
            <div class="chat-right-cont">
                <div class="chat-right-cy">
                    <div class="cy-tit">我的提示词</div>
                    <div class="cy-cont">
                        <div class="cy-cont-item" v-for="(item,index) in callWordList.promptList" :key="index+'a'" v-if="index<8"
                             @click="selectCallWord(item,index)">
                            <div class="item-top">
                                <div class="item-tit">{{item.content}}</div>
                                <div class="item-close" @click="delCallWord(item,index)">
                                    <i class="el-icon-close"></i>
                                </div>
                            </div>
                            <div class="item-text">{{item.content}}</div>
                        </div>
                        <div class="cy-item-add" @click="addCallWords(callWordList)">
                            <i></i>
                            <div>显示更多收藏</div>
                        </div>
                    </div>
                </div>
                <div class="chat-right-cy" v-for="(cwItem,index) in callWordData" :key="index+'s'">
                    <div class="cy-tit">{{cwItem.categoryName}}提示词</div>
                    <div class="cy-cont">
                        <div class="cy-cont-item" v-for="(item,index) in cwItem.promptList" :key="index+'b'" v-if="index<8"
                             @click="selectCallWord(item,index)">
                            <div class="item-top">
                                <div class="item-tit">{{item.title}}</div>
                                <!--<div class="item-close"><i class="el-icon-close"></i></div>-->
                            </div>
                            <div class="item-text">{{item.content}}</div>
                        </div>
                        <div class="cy-item-add" @click="addCallWords(cwItem)">
                            <i></i>
                            <div>显示更多</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <el-dialog custom-class="dialog-box" :visible.sync="dialogVisible" width="65.3%" center :show-close="false">
            <div class="call-word-box">
                <div class="call-word-search">
                    <el-autocomplete
                            class="inline-input search-input"
                            v-model="searchData"
                            :fetch-suggestions="querySearch"
                            placeholder="请输入搜索关键字"
                            @select="handleSelect"
                            @change="clearSearch"
                            clearable
                    ></el-autocomplete>
                    <div class="search-icon">
                        <i class="el-icon-search iconfont"></i>
                    </div>
                </div>
                <div class="call-word-cont">
                    <div class="call-word-item" v-for="(item,index) in callWordDialog.promptList" :key="index"
                         @click="selectCallWord(item,index)">
                        <div class="call-word-item-tit">
                            <div class="call-word-item-tit-text">{{item.title}}</div>
                            <div class="right-tag">{{callWordDialog.categoryName}}</div>
                        </div>
                        <div class="call-word-item-cont" :title="item.content">{{item.content}}</div>
                    </div>
                </div>
            </div>
            <span slot="title" class="dialog-title">AI聊天提示词库</span>
        </el-dialog>
    </div>
</template>

<script>
    import chatFrame4 from "@/views/pc/aiChat4/chatFrame4";
    import {randomString,deepClone} from "@/utils/tools";
    export default {
        name: "aiChat",
        components:{chatFrame4},
        data(){
            return{
                isActive: 0,
                chatList:[],
                callWordList:{},
                dialogVisible:false,
                callWordArr:[],
                chatData:[],
                winId:"",
                callWordData:[],
                searchData:"",
                callWordDialog:[],
                textData:"",
                isInputDisabled:false,//AI是否回答完毕
            }
        },
        created() {
            let token = window.localStorage.getItem("token") || null;
            if(token != null){
                this.getDataList();
                this.getCallWords();
                this.getCallWordsCollect();
            }
        },
        methods:{
            //获取收藏列表
            getCallWordsCollect(){
                this.$https('COLLECTLIST', {}).then(res => {
                    if (res.code == 200) {
                        res.data.map((item)=>{
                            item.title = item.content;
                        });
                        let obj = {
                            categoryName:"",
                            promptList:res.data,
                        }
                        this.callWordList = obj;
                    } else {
                        this.$message.error('请求失败！'+res.msg)
                    }
                })
            },
            //获取提示词
            getCallWords(){
                this.$https('PROMPTCHAT', {}).then(res => {
                    if (res.code == 200) {
                        this.callWordData = res.data;

                    } else {
                        this.$message.error('请求失败！'+res.msg)
                    }
                })
            },
            //聊天列表
            openWindow(item, index) {
                if(this.isInputDisabled == false){
                    this.isActive = index;
                    this.winId = item.winId;
                    this.$https('CHATDETAIL', {
                        winId:item.winId
                    }).then(res => {
                        if (res.code == 200) {
                            if(res.data.length > 0){
                                this.chatData = res.data;
                            }else{
                                this.chatData = [];
                            }

                        } else {
                            this.$message.error('请求失败！'+res.msg)
                        }
                    })
                }else{
                    this.$message.error('请等待AI回答完毕！')
                }

            },
            //查看更多
            addCallWords(obj){
                this.dialogVisible = true;
                this.callWordArr = obj;
                this.callWordDialog = deepClone(obj);
            },
            //选择提示词
            selectCallWord(item,index){
                this.textData = item.content;
                this.dialogVisible = false;
            },
            //删除收藏提示词
            delCallWord(obj,i){
                if(this.isInputDisabled == false){
                    this.$https('ADDCOLLECT', {
                        content:obj.content,
                        operate:"del",
                        msgId:obj.msgId
                    }).then(res => {
                        if (res.code == 200) {
                            this.refreshChat(this.chatList[this.isActive].winId);
                        } else {
                            this.$message.error('请求失败！'+res.msg)
                        }
                    })
                }else{
                    this.$message.error('请等待AI回答完毕！')
                }

            },
            //添加新聊天
            addAiChat(){
               let winId = randomString(16);
               this.chatList.push({winTitle:"新聊天",winId:winId});
            },
            //删除聊天
            delAiChat(item, index) {
                if(this.chatList.length > 1){
                    this.$https('DELETECHAT', {
                        "model":"GPT_4",
                        "operate": "choose",//choose单个删除，clean全部清空
                        "winId": item.winId
                    }).then(res => {
                        if (res.code == 200) {
                            this.chatList.splice(index, 1);
                            if(this.isActive == index){
                                if(index != 0){
                                    let i = index - 1;
                                    this.openWindow(this.chatList[i],i);
                                }else{
                                    let i = index;
                                    this.openWindow(this.chatList[i],i);
                                }
                            }else{
                                this.openWindow(this.chatList[index],index);
                            }
                        } else {
                            this.$message.error('删除失败！')
                        }
                    })
                }
            },
            getTitleStr(e){
                this.chatList[this.isActive].winTitle = e;
            },
            getDataList(){
                this.$https('CHATTINGRECORDSLIST4', {}).then(res => {
                    if (res.code == 200) {
                        this.chatList = res.data;
                        if(this.chatList.length==0){
                            let obj = {
                                winTitle:"新聊天",
                                winId:randomString(16),
                            };
                            this.chatList.push(obj);
                        }

                        if(this.chatList.length > 0){
                            this.openWindow(this.chatList[0],0);
                        }
                    } else {
                        this.$message.error('请求失败！'+res.msg)
                    }
                })
            },
            refreshChat(e){
                this.$https('CHATDETAIL', {
                    winId:e
                }).then(res => {
                    if (res.code == 200) {
                        this.chatData = res.data;
                    } else {
                        this.$message.error('请求失败！'+res.msg)
                    }
                })
                this.getCallWordsCollect();
            },
            querySearch(queryString, cb) {
                this.callWordArr.promptList.map((item)=>{
                    item.value = item.title;
                })
                let restaurants = this.callWordArr.promptList;
                let results = queryString ? restaurants.filter(this.createFilter(queryString)) : restaurants;
                // 调用 callback 返回建议列表的数据
                cb(results);
            },
            createFilter(queryString) {
                return (restaurant) => {
                    return (restaurant.value.toLowerCase().indexOf(queryString.toLowerCase()) != -1);
                };
            },
            //选择搜索框
            handleSelect(item) {
                this.callWordDialog.promptList = [item];
            },
            //清空搜索框
            clearSearch(){
                this.callWordDialog = deepClone(this.callWordArr);
            },
            isInputFlag(e){
                this.isInputDisabled = e;
            }
        }
    }
</script>

<style src="../../../assets/css/aiChat.scss" lang="scss" scoped></style>