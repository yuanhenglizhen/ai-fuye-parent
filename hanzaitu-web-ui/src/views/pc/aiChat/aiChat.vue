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
            <chatFrame :winId="winId" :chatData="chatData" :textStr="textData" :subModel="currentModel"
                       @inputStr="getTitleStr" @refreshData="refreshChat" @isInput="isInputFlag"></chatFrame>
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
                    <div class="cy-tit my-call-word"><span>我的提示词</span><div class="add-btn" @click="addOpenCallWord">新增</div></div>
                    <div class="cy-cont">
                        <div class="cy-cont-item" v-for="(item,index) in callWordList.promptList" :key="index+'a'" v-if="index<8"
                             @click="selectCallWord(item,index)">
                            <div class="item-top">
                                <div class="item-tit">{{item.title}}</div>
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
                            <div class="right-tag" v-if="callWordDialog.categoryName">{{callWordDialog.categoryName}}</div>
                            <div class="right-tag" style="cursor: pointer" v-else @click.stop="delCallWord(item,index)">×</div>
                        </div>
                        <div class="call-word-item-cont" :title="item.content">{{item.content}}</div>
                    </div>
                </div>
            </div>
            <span slot="title" class="dialog-title">AI聊天提示词库</span>
        </el-dialog>

        <el-dialog custom-class="add-prompt-box" :visible.sync="openAddCallWord" width="568px" center >
            <div slot="title" class="dialog-title">
                <div class="title-box">自定义提示词</div>
            </div>
            <div class="callWordBox">
                <el-form ref="customCallWordForm" :model="customCallWordForm" :rules="customCallWordRules" label-width="80px" label-position="top">
                    <el-form-item label="添加角色名称" prop="title">
                        <el-input v-model="customCallWordForm.title" autocomplete="off"></el-input>
                    </el-form-item>
                    <el-form-item label="角色提示词" prop="content">
                        <el-input type="textarea" v-model="customCallWordForm.content"></el-input>
                    </el-form-item>
                    <el-form-item>
                        <div class="call-word-btn" style="padding-top: 3.4rem">
                            <div class="cancel-btn"><el-button @click="openAddCallWord = false">取 消</el-button></div>
                            <div class="submit-btn"><el-button @click="addCallWordSubmit">保存</el-button></div>
                        </div>
                    </el-form-item>
                </el-form>
            </div>
        </el-dialog>
    </div>
</template>

<script>
    import chatFrame from "@/views/pc/aiChat/chatFrame";
    import {randomString,deepClone} from "@/utils/tools";
    export default {
        name: "aiChat",
        components:{chatFrame},
        data(){
            return{
                isActive: 0,
                chatList:[],
                callWordList:{},
                dialogVisible:false,
                callWordArr:[],
                chatData:[],
                currentModel:"gpt-3.5-turbo",
                winId:"",
                callWordData:[],
                searchData:"",
                callWordDialog:[],
                textData:"",
                isInputDisabled:false,//AI是否回答完毕
                openAddCallWord:false,
                customCallWordForm:{},
                customCallWordRules:{},

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
                this.$https('customList', {}).then(res => {
                    if (res.code == 200) {
                        let obj = {
                            categoryName:null,
                            promptList:res.data.list,
                        }
                        this.callWordList = obj;
                        this.callWordDialog = deepClone(obj);
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
                                this.currentModel = item.subModel;
                            }else{
                                this.chatData = [];
                                this.currentModel = "gpt-3.5-turbo";
                            }

                        } else {
                            this.$message.error(res.msg)
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
            /*delCallWord(obj,i){
                if(this.isInputDisabled == false){
                    this.$https('ADDCOLLECT', {
                        content:obj.content,
                        operate:"del",
                        msgId:obj.msgId
                    }).then(res => {
                        if (res.code == 200) {
                            this.refreshChat(this.chatList[this.isActive].winId);
                        } else {
                            this.$message.error(res.msg)
                        }
                    })
                }else{
                    this.$message.error('请等待AI回答完毕！')
                }

            },*/
            //添加新聊天
            addAiChat(){
               let winId = randomString(16);
               this.chatList.push({winTitle:"新聊天",winId:winId});
            },
            //删除聊天
            delAiChat(item, index) {
                if(this.chatList.length > 1){
                    this.$https('DELETECHAT', {
                        "model":"GPT_3",
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
                this.$https('CHATTINGRECORDSLIST', {}).then(res => {
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
                        this.$message.error(res.msg);
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
                        this.$message.error(res.msg)
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
            },
            addOpenCallWord(){
                this.customCallWordForm = {}
                this.openAddCallWord = true;
            },
            addCallWordSubmit(){
                this.$https('customAdd',this.customCallWordForm).then(res => {
                    if (res.code == 200) {
                        this.$message.success('添加成功！');
                        this.openAddCallWord = false;
                        this.getCallWordsCollect();
                    } else {
                        this.$message.error(res.msg);

                    }
                })
            },
            delCallWord(obj,i){
                this.$POSTPAGE('/chat/prompt/custom-del/'+obj.id,{}).then(res => {
                    if (res.code == 200) {
                        this.$message.success('删除成功！');
                        this.getCallWordsCollect();
                    } else {
                        this.$message.error(res.msg);
                    }
                })
            },
        }
    }
</script>

<style src="../../../assets/css/aiChat.scss" lang="scss" scoped></style>