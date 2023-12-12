<template>
	<div class="thesaurus-panel" :class="visible ? 'visible' : 'hide'">
		<!-- 遮罩 -->
		<div class="mask" @click="closePanel"></div>
		<!-- 内容区 -->
		<div class="content-panel">
			<div class="header">
				<div class="text">AI绘画</div>
			</div>

			<div class="tabs-wrapper">
				<div class="tabs">
					<div class="tab font-28" :class="tabIndex == 0 ? 'bg-linear-radient' : 'black'"
						@click="tabIndex = 0">垫图</div>
					<div class="tab font-28" :class="tabIndex == 1 ? 'bg-linear-radient' : 'black'"
						@click="tabIndex = 1">以图生文</div>
				</div>
			</div>

			<div :style="{ height: contentBoxHeight }" :scroll-y="true" class="content">
				<div v-if="tabIndex == 0" class="upload">
					<div class="tips" v-if="uploadStatus == 0">
						<div class="font-28">AI垫图 — 请上传参考图</div>
						<div class="font-24">支特JPG.PNG.1OM以内</div>
					</div>
					<div class="upload-img-box" v-else>
						<img :src="picture.imgUrl" alt="" v-if="picture.imgUrl != null">
					</div>

					<el-upload
							:action="uploadImgUrl"
							:auto-upload="true"
							:show-file-list="false"
							:multiple="false"
							:limit="1"
							:headers="headers"
							:before-upload="handleBeforeUpload"
							:on-success="handleUploadSuccess">
							<div class="btn bg-linear-radient font-28">
								<div class="icon-36 icon-upload bg-img"></div>
								上传
							</div>
					</el-upload>
				</div>
				<div v-if="tabIndex == 1" class="upload">
					<div class="tips" v-if="uploadStatus == 0">
						<div class="font-28">以图生文 — 请上传参考图</div>
						<div class="font-24">支特JPG.PNG.1OM以内</div>
					</div>
					<div class="upload-img-box" v-else>
						<img :src="picture.imgUrl" alt="" v-if="picture.imgUrl != null">
					</div>

					<div v-if="uploadStatus == 2" @click="sendImage" class="btn bg-linear-radient font-28">
						<div class="icon-36 icon-send bg-img"></div>
						发送
					</div>

					<el-upload
							v-else
							:action="uploadImgUrl"
							:auto-upload="true"
							:show-file-list="false"
							:multiple="false"
							:limit="1"
							:headers="headers"
							:before-upload="handleBeforeUpload"
							:on-success="handleUploadSuccess">
							<div class="btn bg-linear-radient font-28">
								<div class="icon-36 icon-upload bg-img"></div>
								上传
							</div>
					</el-upload>
				</div>

				<div class="title font-28 font-wt-500">输出尺寸</div>
				<div class="size-group">
					<div class="size" :class="sizeIndex == 0 ? 'active' : ''" @click="chooseAr(0, '1:1')">
						<div class="rect r11"></div>
						<div class="text font-28">1:1</div>
					</div>
					<div class="size" :class="sizeIndex == 1 ? 'active' : ''" @click="chooseAr(1, '3:4')">
						<div class="rect r34"></div>
						<div class="text font-28">3:4</div>
					</div>
					<div class="size" :class="sizeIndex == 2 ? 'active' : ''" @click="chooseAr(2, '4:3')">
						<div class="rect r43"></div>
						<div class="text font-28">4:3</div>
					</div>
					<div class="size" :class="sizeIndex == 3 ? 'active' : ''" @click="chooseAr(3, '16:9')">
						<div class="rect r169"></div>
						<div class="text font-28">16:9</div>
					</div>
					<div class="size" :class="sizeIndex == 4 ? 'active' : ''" @click="chooseAr(4, '9:16')">
						<div class="rect r916"></div>
						<div class="text font-28">9:16</div>
					</div>
					<div class="size" :class="sizeIndex == 5 ? 'active' : ''" @click="sizeIndex = 5">
						<div class="rect customize font-28">自定义尺寸</div>
						<div class="text customize font-24">
							<input type="number" v-model="custom.width" auto-blur class="number-imput font-28"
								maxlength="2" onke />
							:
							<input type="number" v-model="custom.height" auto-blur class="number-imput font-28"
								maxlength="2" />
						</div>
					</div>
				</div>
				<div class="title font-28 font-wt-500">提示词推荐</div>
				<div class="word-group">
					<div class="word font-28" :class="prompt.index == index ? 'active' : ''"
						@click="prompt.index = index" v-for="(prop, index) in prompt.items" :key="prop.unique">
						{{ prop.category }}
					</div>
				</div>

				<div v-show="prompt.index == index" v-for="(prop, index) in prompt.items" :key="prop.unique">
					<div v-for="classify in prop.classifyList" :key="classify.unique">
						<switch-group @sendArgs="sendArgs" :classify="classify"></switch-group>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
	import storage from "@/utils/h5/storage";
	import Empty from "../../empty";
	import SwitchGroup from "./switch-group";
	import {getDrawPromptList} from "@/api/h5";
	import {randomString} from "@/utils/h5/common";
	import gptUploader from '@/components/h5/gpt/uploader/index.vue';
	import { HTTP_URI,baseUrl } from '@/api/api'
	import { getToken } from "@/utils/h5/auth";
	export default {
		components: {
			Empty,
			SwitchGroup,
			gptUploader,
		},
		created() {
			this.contentBoxHeight = 500;

			function handle(data) {
				for (let category of data) {
					category.unique = randomString(32);
					for (let classify of category.classifyList) {
						classify.unique = randomString(32);
						classify.isOpen = false;
						for (let content of classify.categoryContent) {
							content.isActived = false;
							content.unique = randomString(32);
						}
					}
				}
			}

			let prompts = storage.get('_draw_prompts');
			if (prompts) {
				handle(data)
				this.prompt.items = data;
			}

			let token = getToken();
			if(token != null){
				getDrawPromptList().then(({data}) => {
					storage.set('_draw_prompts', data);
					if (!prompts) {
						handle(data)
						this.prompt.items = data;
					}
				})
			}

		},
		props: {
			visible: Boolean,
		},
		model: {
			prop: "visible",
			event: "visibleChange",
		},
		data() {
			return {
				sizeIndex: 0,
				contentBoxHeight: 0,
				tabIndex: 0,
				prompt: {
					index: 0,
					items: [],
				},
				picture: {
					imgUrl: "",
				},
				uploadStatus: 0,
				ar: '1:1',
				custom: {
					width: '',
					height: ''
				},
				uploadImgUrl: `${baseUrl}/draw/trigger/upload-img`, // 上传的图片服务器地址
				headers: {
					'Access-Token-Hzt':getToken() || window.localStorage.getItem("token"),
				},
				imgTextUrl:null,

			};
		},
		methods: {
			chooseAr(index, ar) {
				this.sizeIndex = index;
				this.ar = ar;
			},

			sendArgs(content) {
				this.$emit("choosePropmptArgs", content.key);
			},
			/*uploadSuccess(res) {
				const json = JSON.parse(res);
				this.uploadStatus = 2;
				this.picture.imgUrl = json.result;
			},
			uploadCancel() {
				this.picture = {
					imgUrl: ''
				};
				this.uploadStatus = 0;
			},*/

			sendImage() {
				this.$emit("sendTask", this.picture.imgUrl);
				this.closePanel();
			},
			argsChange() {
				this.$emit('argsChange', {
					ar: this.ar,
					img: this.picture.imgUrl
				})
			},
			closePanel() {
				if (this.sizeIndex == 5) {
					this.ar = this.custom.width + ':' + this.custom.height
				}

				this.argsChange();
				this.$emit("visibleChange", false);
			},
			handleBeforeUpload(e){
				console.log(e);
			},
			//上传成功回调
			handleUploadSuccess(e){
				if(e.code == 200){
					this.uploadStatus = 2;
					this.picture.imgUrl = e.result;

				}
			},
		},
	};
</script>

<style lang="scss" scoped>
	@import "./index.scss";
</style>