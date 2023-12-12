<template>
	<gpt-page>
		<div class="chat-container">
			<gpt-navigation-bar>
				<template> AI聊天 </template>
			</gpt-navigation-bar>

			<div class="info">
				<div class="tabs" :scroll-x="true">
					<div @click="chooseVersion(0) " class="rebot" :class="rebotIndex === 0 ? 'active' : ''">Chat3.5
					</div>
					<div @click="chooseVersion(1) " class="rebot" :class="rebotIndex === 1 ? 'active' : ''">Chat4.0
					</div>
				</div>

				<div class="operate">
					<div @click="showHistory" class="icon-48 icon-notepad bg-img"></div>
					<div @click="showTool" class="icon-48 icon-book bg-img"></div>
				</div>
			</div>
			<div></div>

			<div v-if="isGpt4">
				<aichat-not-open></aichat-not-open>
			</div>
			<div v-else>
				<div style="height: 4.4rem; width: 100%;"></div>
				<aichat-first-use v-if="!messages.items.length"></aichat-first-use>
				<div v-else class="message-box">
					<div id="scroll-view-content" ref="scrollViewContent" class="message-list">
						<div v-for="msg in messages.items" :key="msg.key" class="msg-block" :class="msg.role">
							<div class="msg-content">
								<div v-if="msg.role == 'user'" class="icon-48 icon-carrot bg-img"></div>
								<div v-else class="icon-48 icon-gpt bg-img"></div>
								<!-- <rich-text class="msg-text" :nodes="msg.content"> </rich-text> -->
								<pre class="msg-text">{{msg.content}}</pre>
								<!--<u-parse class="msg-text" :content="msg.content"></u-parse>-->
							</div>
							<div class="msg-operate">
								<div class="msg-operate-l" v-if="msg.role == 'user'">
									<!-- <div @click="addCollect(msg.content, msg.msgId, msg.collectId)" class="icon-36 bg-img"
										:class="msg.collectId ? 'icon-star2' : 'icon-star'"></div> -->
									<div @click="edit(msg.content)" class="icon-36 icon-edit bg-img"></div>
									<div @click="refresh(msg.content)" class="icon-36 icon-refresh bg-img"></div>
								</div>
								<div @click="copy(msg.content)" class="icon-36 icon-copy bg-img"></div>
							</div>
						</div>
					</div>
					<div :id="scrollIntoViewId" style="height: 10px;"></div>
				</div>
			</div>


			<div class="bottom">
				<gpt-input :disabled="disabledInput" placeholder="请输入消息" :value="userInputText"
					@send="sendMessage"></gpt-input>
			</div>

			<aichat-chat-history v-model="historyVisible" @openWindow="handleOpenWindow" :isGpt4="isGpt4"
				@newWindow="handleNewWindow"></aichat-chat-history>
			<aichat-thesaurus v-model="toolVisible" @choosePrompt="handleChoosePrompt"></aichat-thesaurus>

			<gpt-event-source :gtpModel="eventGptModel" :eventId="eventSourceId" :isGpt4="isGpt4"
				:prompt="eventSourceContent" :ran="eventRan" @message="emitMessage"
				@error="emitError" @close="emitClose"></gpt-event-source>

			<aichat-gpt-model v-show="!isGpt4" :disabled="!!messages.items.length" @gptModelChange="handleGptModelChange"></aichat-gpt-model>
		</div>
	</gpt-page>
</template>

<script>
	import {
		mapState
	} from "vuex";
	import {
		sendMsg,
		addCollect,
		getContent,
		getGptModel
	} from "@/api/h5/chat";
	import uParse from '@/components/h5/u-parse/u-parse.vue'
	import {
		randomString
	} from "@/utils/h5/common";
	import gptPage from '@/components/h5/gpt/page/index.vue';
	import gptNavigationBar from '@/components/h5/gpt/navigation-bar/index.vue';
	import aichatNotOpen from '@/components/h5/aichat/not-open/index.vue';
	import aichatFirstUse from '@/components/h5/aichat/first-use/index.vue';
	import gptInput from '@/components/h5/gpt/input/index.vue';
	import aichatChatHistory from '@/components/h5/aichat/chat-history/index.vue';
	import aichatThesaurus from '@/components/h5/aichat/thesaurus/index.vue';
	import aichatGptModel from '@/components/h5/aichat/gpt-model/index.vue';
	import gptEventSource from '@/components/h5/gpt/event-source/index.vue';

	// ai 思考
	function thinking(msg) {
		let isAbort = false;

		function _thinking() {
			setTimeout(() => {
				if (isAbort) {
					return;
				}
				if (msg.content.length == 0) {
					msg.content = ".";
				} else if (msg.content.length == 1) {
					msg.content = "..";
				} else if (msg.content.length == 2) {
					msg.content = "...";
				} else {
					msg.content = ".";
				}
				_thinking();
			}, 200);
		}

		return {
			start() {
				msg.content = ".";
				_thinking();
			},
			stop() {
				if (!isAbort) {
					console.log("isAbort");
					msg.content = "";
					isAbort = true;
				}
			},
		};
	}

	export default {
		components: {
			uParse,
			gptPage,
			gptNavigationBar,
			aichatNotOpen,
			aichatFirstUse,
			gptInput,
			aichatChatHistory,
			aichatThesaurus,
			gptEventSource,
			aichatGptModel,
		},
		data() {
			return {
				historyVisible: false,
				toolVisible: false,
				userInputText: "",
				disabledInput: false,
				rebotIndex: 0,
				messages: {
					total: 0,
					items: [],
				},
				scrollViewContentHeight: -1,
				scrollIntoViewId: '',
				scrollIntoView: '',
				scrollTop: 19999999,
				winId: randomString(14),
				eventSourceId: '',
				eventSourceContent: '',
				eventRan: '',
				eventGptModel: '',
				gptModel: '',
				currentMsgId: "",
				thk: "",
				cacheText: []
			};
		},
		computed: {
			...mapState("user", ["token"]),
			isGpt4() {
				return this.rebotIndex == 1;
			},
		},
		created() {

			this.$eventBus.$on("logout-success", () => {
				this.messages = {
					total: 0,
					items: [],
				};
			});

			this.$eventBus.$on("apply-prompt", (data) => {
				this.userInputText = data
			});
		},
		mounted() {},
		methods: {
			handleGptModelChange(model) {
				this.gptModel = model;
			},
			chooseVersion(index) {
				this.rebotIndex = index;
				// this.handleNewWindow(randomString(14));
			},
			async handleOpenWindow(winId) {
				this.winId = winId;
				const {
					data
				} = await getContent(winId);
				this.messages = {
					total: data.length,
					items: data
				};
				this.disabledInput = false;
				this.$forceUpdate();
				this.scrollToBottom();
			},
			handleNewWindow(winId) {
				this.winId = winId;
				this.messages = {
					total: 0,
					items: [],
				};
				this.disabledInput = false;
			},
			// 编辑
			edit(data) {
				this.userInputText = data;
			},
			refresh(data) {
				this.sendMessage(data);
			},
			// 复制文本
			copy(data) {
				uni.setClipboardData({
					showToast: false,
					data,
					success: () => {
						this.$ui.showToast("内容已复制到剪切板");
					},
				});
			},
			// 添加收藏
			async addCollect(content, msgId, collectId) {
				await addCollect({
					content,
					msgId,
					operate: !collectId ? "add" : "del",
				});
				this.$ui.showToast(!collectId ? "收藏成功" : "取消收藏成功");
				const msg = this.messages.items.find((a) => a.msgId === msgId);
				msg.collectId = !collectId ? randomString(32) : "";
			},
			handleChoosePrompt(data) {
				//this.sendMessage(data);
				this.userInputText = data;
			},
			// 显示工具栏
			showTool() {
				if (this.isGpt4) {
					// TODO
					this.$ui.showToast('暂不开放')
					return;
				}
				if (this.token) {
					this.toolVisible = true;
				} else {
					this.$eventBus.$emit("need-login");
				}
			},
			// 显示历史记录
			showHistory() {
				if (this.isGpt4) {
					// TODO
					this.$ui.showToast('暂不开放')
					return;
				}
				if (this.token) {
					this.historyVisible = true;
				} else {
					this.$eventBus.$emit("need-login");
				}
			},
			// 发送消息
			async sendMessage(val) {
				if (this.isGpt4) {
					// TODO
					this.$ui.showToast('暂不开放')
					return;
				}
				if (this.disabledInput) {
					this.$ui.showToast('请等待AI回复完毕')
					return;
				}
				this.disabledInput = true;
				this.userInputText = "";

				this.messages.items.push({
					msgId: randomString(14),
					type: "text",
					role: "user",
					content: val,
					collectId: "",
				});


				const msgId = randomString(14);
				this.currentMsgId = msgId;
				this.messages.items.push({
					msgId,
					type: "text",
					role: "assistant",
					content: "",
				});
				this.cacheText = [];

				const msg = this.messages.items.find((a) => a.msgId == this.currentMsgId);
				this.thk = thinking(msg);
				this.thk.start();
				// renderjs变量	
				// 不能直接将winId、gptModel赋值到组件，否则winId在其他方法中变化也会导致renderjs触发
				this.eventSourceId = this.winId;
				this.eventSourceContent = val;
				// 为了每次发送数据都触发renderjs
				this.eventRan = randomString(32);
				this.eventGptModel = this.gptModel;

				this.$forceUpdate();
				await this.scrollToBottom();
				// 开始更新
				this.$nextTick(() => {
					this.updateUI();
				})
			},
			emitError({
				e
			}) {
				this.thk.stop();
				this.cacheText.push('e7CH33XiGTSnFZYBH2sY58Pf7J4sFQnr1686968863097');
			},
			emitMessage({
							content
						}) {
				this.thk.stop();
				this.cacheText.push(content);
			},
			emitClose() {
				this.thk.stop();
				this.cacheText.push('e7CH33XiGTSnFZYBH2sY58Pf7J4sFQnr1686968863097');
			},
			async updateUI() {
				if (this.cacheText.length) {
					const msg = this.messages.items.find((a) => a.msgId == this.currentMsgId);
					let content = this.cacheText.shift();
					if (content === 'e7CH33XiGTSnFZYBH2sY58Pf7J4sFQnr1686968863097') {
						this.disabledInput = false;
						this.scrollToBottom();
						return;
					}
					msg.content += content;

					await this.scrollToBottom();
				}


				// 控制输出速度
				setTimeout(() => {
					this.updateUI();
				}, 50);
			},
			handleVisibleChange(val) {
				this.registerDialogVisible = val;
			},
			async scrollToBottom() {
				let id = `scroll${randomString(14)}`;
				this.scrollIntoViewId = id;
				return new Promise((resolve, reject) => {
					this.$nextTick(() => {
						let res = this.$refs.scrollViewContent.getBoundingClientRect();
						if (this.scrollViewContentHeight != res.height) {
							this.scrollViewContentHeight = res.height;
							this.scrollIntoView = id;
							//this.scrollTop++;
						}
						resolve();
					});
				})

			}
		},
	};
</script>

<style lang="scss" scoped>
	@import './chat.scss';
</style>