<template>
	<gpt-page>
		<div class="chat-container">
			<gpt-navigation-bar>
				<template> AI绘画 </template>
			</gpt-navigation-bar>

			<div class="info">
				<div class="tabs" :scroll-x="true">
					<div class="rebot" :class="item.key == rebots.current ? 'active-black' : ''"
						v-for="item in rebots.items" :key="item.key">{{ item.name }}</div>
				</div>

				<div class="operate">
					<div @click="showHistory" class="icon-48 icon-notepad bg-img"></div>
					<div @click="showTool" class="icon-48 icon-book bg-img"></div>
				</div>
			</div>
			<aidraw-first-use v-if="!messages.items.length"></aidraw-first-use>
			<div v-else class="message-box">
				<div id="scroll-view-content" ref="scrollViewContent" class="message-list">
					<div class="sss" v-for="msg in messages.items" :key="msg.msgId">
						<aidraw-drawing :taskId="msg.taskId" :content="msg.content" :type="msg.type"
							@redoImage="handleRedoImage" @redoDescribe="handleRedoDescribe"
							@deleteImage="handleDeleteImage" @tranformImage="handleTranformImage"
							@choosePrompt="handleChoosePrompt"></aidraw-drawing>
					</div>
				</div>
				<div :id="scrollIntoViewId" style="height: 10px;"></div>
			</div>
			<div class="bottom">
				<gpt-input :disabled="disabledInput" placeholder="请输入图片的英文描述" :value="userInputText"
					@send="sendMessage"></gpt-input>
			</div>
			<aidraw-draw-history v-model="historyVisible" @chooseHistory="handleChooseHistory"></aidraw-draw-history>
			<aidraw-tool v-model="toolVisible" @sendTask="handleSendTask" @argsChange="handleArgsChange"
				@choosePropmptArgs="handleChooseArgs"></aidraw-tool>
		</div>
	</gpt-page>
</template>

<script>
	import {
		mapState
	} from "vuex";
	import {
		drawDescribe,
		submitPromptTask,
		submitTransformTask,
		deleteTask,
	} from "@/api/h5";
	import {
		randomString
	} from "@/utils/h5/common";
	import gptPage from '@/components/h5/gpt/page/index.vue';
	import gptNavigationBar from '@/components/h5/gpt/navigation-bar/index.vue';
	import gptInput from '@/components/h5/gpt/input/index.vue';
	import aidrawFirstUse from '@/components/h5/aidraw/first-use/index.vue';
	import aidrawDrawHistory from '@/components/h5/aidraw/draw-history/index.vue';
	import aidrawTool from '@/components/h5/aidraw/tool/index.vue';
	import aidrawDrawing from '@/components/h5/aidraw/drawing/index.vue';

	export default {
		components: {
			gptPage,
			gptNavigationBar,
			gptInput,
			aidrawFirstUse,
			aidrawDrawHistory,
			aidrawTool,
			aidrawDrawing
		},
		data() {
			return {
				historyVisible: false,
				toolVisible: false,
				userInputText: "",
				disabledInput: false,
				messageBoxHeight: 0,
				rebots: {
					current: 1,
					items: [{
						key: 1,
						name: "Midjourney",
					}, ],
				},
				messages: {
					total: 0,
					items: [],
				},
				scrollViewContentHeight: -1,
				scrollIntoViewId: '',
				scrollIntoView: '',
				args: {
					ar: '',
					img: ''
				}
			};
		},
		computed: {
			...mapState("user", ["token"])
		},
		created() {

		},
		methods: {
			showTool() {
				if (this.token) {
					this.toolVisible = true;
				} else {
					this.$eventBus.$emit("need-login");
				}
			},
			showHistory() {
				if (this.token) {
					this.historyVisible = true;
				} else {
					this.$eventBus.$emit("need-login");
				}
			},
			handleChooseHistory(taskId) {
				this.messages.items = [{
					msgId: randomString(14),
					type: "DESCRIBE",
					role: "assistant",
					taskId: taskId,
					content: '',
				}];
				console.log(this.messages);
			},
			handleChoosePrompt(text) {
				this.userInputText = text;
			},
			handleChooseArgs(arg) {
				this.userInputText += ' ' + arg;
			},
			handleArgsChange(arg) {
				this.args = arg;
			},
			// 图生文
			handleSendTask(imgUrl) {
				// if (this.disabledInput) {
				// 	this.$ui.showToast('请等待AI生成完毕')
				// 	return;
				// }
				// this.disabledInput = true;

				this.$ui.usingLoading(async () => {
					try {
						const data = await drawDescribe(imgUrl);
						this.messages.items = [{
							msgId: randomString(14),
							type: "DESCRIBE",
							role: "assistant",
							taskId: data.result,
							content: imgUrl,
						}];
						this.scrollToBottom();
					} catch (e) {
						//this.disabledInput = false;
					}
				});
			},
			// 文生图
			sendMessage(val) {
				// if (this.disabledInput) {
				// 	this.$ui.showToast('请等待AI生成完毕')
				// 	return;
				// }
				// this.disabledInput = true;

				this.$ui.usingLoading(async () => {
					let content = val;
					if (this.args.img) {
						content = this.args.img + ' ' + content;
					}
					if (this.args.ar) {
						content = content + ' --ar ' + this.args.ar;
					}

					try {
						const data = await submitPromptTask(content);
						this.messages.items = [{
							msgId: randomString(14),
							type: "drawing",
							role: "assistant",
							taskId: data.result,
							content
						}]
						this.scrollToBottom();
					} catch (e) {
						//this.disabledInput = false;
					}
				});

			},
			// 图片变换
			async handleTranformImage(val) {
				console.log(val);
				const data = await submitTransformTask(val.taskId, val.op);
				this.messages.items.push({
					msgId: randomString(14),
					type: "drawing",
					role: "assistant",
					taskId: data.result,
				});
				this.scrollToBottom();
			},
			// 重新图生文
			handleRedoDescribe({
				taskId,
				content
			}) {
				this.handleDeleteImage(taskId);
				this.handleSendTask(content);
			},
			// 重新生成图片
			handleRedoImage({
				taskId,
				content
			}) {
				this.handleDeleteImage(taskId);
				this.sendMessage(content);
			},
			// 删除图片
			async handleDeleteImage(taskId) {
				await deleteTask(taskId);
				const index = this.messages.items.findIndex((a) => a.taskId === taskId);
				this.messages.items.splice(index, 1);
			},
			handleVisibleChange(val) {
				this.registerDialogVisible = val;
			},
			scrollToBottom() {
				let id = `scroll${randomString(14)}`;
				this.scrollIntoViewId = id;
				this.$nextTick(() => {
					let res = this.$refs.scrollViewContent.getBoundingClientRect();
					if (this.scrollViewContentHeight != res.height) {
						this.scrollViewContentHeight = res.height;
						this.scrollIntoView = id;
					}
				});

			}
		},
	};
</script>

<style lang="scss" scoped>
	@import './chat.scss';
</style>