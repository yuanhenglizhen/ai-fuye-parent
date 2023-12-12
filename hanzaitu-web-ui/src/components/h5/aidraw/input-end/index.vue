<template>
	<div class="input-end-container">
		<div class="text">{{ action === 'DESCRIBE' ? '以图生文提示词' : '图片提示词' }}</div>
		
		<div v-if="action !== 'DESCRIBE'">
			<pre class="english" >{{displayPrompt}}</pre>
		</div>
		<div v-else>
			<pre class="prompt-text" >{{displayPrompt}}</pre>
			<div class="prompts">
				<div @click="choosePrompt(item.text)" v-for="(item,index) in prompts" :key="item.key" class="prompt-item">
					{{emoji[index]}}
				</div>
			</div>
		</div>

		<div v-if="action !== 'DESCRIBE' && action !== 'UPSCALE'">
			<div class="text margin-top_40">
				图片放大
				<text class="lbl">对左侧的其中张图片进行放大</text>
			</div>
			<div class="operate">
				<div class="op-item" @click="tranformImage({'action':'UPSCALE','index':'1'})">
					<div>U1</div>
					<div>左上</div>
				</div>
				<div class="op-item" @click="tranformImage({'action':'UPSCALE','index':'2'})">
					<div>U2</div>
					<div>右上</div>
				</div>
				<div class="op-item" @click="tranformImage({'action':'UPSCALE','index':'3'})">
					<div>U3</div>
					<div>左下</div>
				</div>
				<div class="op-item" @click="tranformImage({'action':'UPSCALE','index':'4'})">
					<div>U4</div>
					<div>右下</div>
				</div>
			</div>
			<div class="text margin-top_40">
				变化图片
				<text class="lbl">对左侧的其中张图片进行多样化修改</text>
			</div>
			<div class="operate">
				<div class="op-item" @click="tranformImage({'action':'VARIATION','index':'1'})">
					<div>V1</div>
					<div>左上</div>
				</div>
				<div class="op-item" @click="tranformImage({'action':'VARIATION','index':'2'})">
					<div>V2</div>
					<div>右上</div>
				</div>
				<div class="op-item" @click="tranformImage({'action':'VARIATION','index':'3'})">
					<div>V3</div>
					<div>左下</div>
				</div>
				<div class="op-item" @click="tranformImage({'action':'VARIATION','index':'4'})">
					<div>V4</div>
					<div>右下</div>
				</div>
			</div>
			<div class="text margin-top_40"> 更多操作 </div>
		</div>

		<div v-if="action == 'UPSCALE'">
			<div class="text margin-top_40">
				图片变化
				<span class="lbl">对左侧的图片进行二次变化</span>
			</div>
			<div class="btn-group">
				<div class="btn redo">
					<div class="icon-36 icon-redo-color bg-img"></div>
					<div class="btn-text font-linear-radient" @click="tranformImage({'action':'HIGH_VARIATION','index':null})">强力变化</div>
				</div>
				<div class="btn redo">
					<div class="icon-36 icon-redo-color bg-img"></div>
					<div class="btn-text font-linear-radient" @click="tranformImage({'action':'LOW_VARIATION','index':null})">微小变化</div>
				</div>
			</div>
			<div class="text margin-top_40">
				景别缩小
				<span class="lbl">对左侧的图片进行景别缩小</span>
			</div>
			<div class="btn-group">
				<div class="btn redo">
					<div class="icon-36 icon-redo-color bg-img"></div>
					<div class="btn-text font-linear-radient" @click="tranformImage({'action':'HIGH_ZOOM','index':null})">缩小2倍</div>
				</div>
				<div class="btn redo">
					<div class="icon-36 icon-redo-color bg-img"></div>
					<div class="btn-text font-linear-radient" @click="tranformImage({'action':'LOW_ZOOM','index':null})">缩小1.5倍</div>
				</div>
			</div>
			<div class="text margin-top_40"> 更多操作 </div>
		</div>

		<div v-if="action !== 'DESCRIBE'" class="btn-group">
			<div v-if="action !== 'IMAGINE'" class="btn redo">
				<div class="icon-36 icon-download bg-img"></div>
				<div class="btn-text font-linear-radient" @click="downloadImage">下载图片</div>
			</div>
			<div v-else class="btn redo">
				<div class="icon-36 icon-redo-color bg-img"></div>
				<div class="btn-text font-linear-radient" @click="redoImage">重新生成</div>
			</div>
			<div class="btn delete">
				<div class="icon-36 icon-delete bg-img"></div>
				<div class="btn-text font-linear-radient" @click="deleteImage">删除图片</div>
			</div>
		</div>
		<div v-else class="btn-group flex-1">
			<div class="btn redo">
				<div class="icon-36 icon-redo-color bg-img"></div>
				<div class="btn-text font-linear-radient" @click="redoDescribe">重新生成</div>
			</div>
		</div>
	</div>
</template>

<script>
	export default {
		props: {
			action: String,
			prompt: String,
		},
		data() {
			return {
				displayPrompt: '',
				prompts: [],
				emoji: ['1️⃣', '2️⃣', '3️⃣', '4️⃣']
			}
		},
		created() {
			if (this.action == 'DESCRIBE') {
				this.prompts = this.prompt.split('\n').filter(item => !!item).map((item, index) => {
					return {
						key: index,
						text: item.replace(this.emoji[index], '').replace(' ', '')
					}
				});
			}

			this.displayPrompt = this.prompt;
		},
		methods: {
			choosePrompt(text) {
				this.$emit("choosePrompt", text);
			},
			redoImage() {
				this.$emit("redoImage");
			},
			redoDescribe() {
				this.$emit("redoDescribe");
			},
			deleteImage() {
				this.$emit("deleteImage");
			},
			tranformImage(op) {
				this.$emit("tranformImage", op);
			},
			downloadImage() {
				this.$emit("downloadImage");
			},
		},
	};
</script>

<style lang="scss" scoped>
	.input-end-container {
		margin: 2rem 0;
		padding: 1.6rem 1.6rem 0;
		width: 34.3rem;
		// height: 1282rem;
		background: #11141e;
		border-radius: 0 0 0.4rem 0.4rem;
		border: 0.1rem solid #4e5c8d;

		.text {
			line-height: 2rem;
			font-size: 1.4rem;
			font-weight: 500;
			color: #fff;

			>.lbl {
				padding-left: 0.8rem;
				font-size: 1.2rem;
				color: #94a3b8;
			}
		}

		.english {
			margin-top: 1.6rem;
			padding: 1.2rem 2.4rem;
			width: 31.1rem;
			// height: 268rem;
			border-radius: 0.4rem;
			border: 0.1rem solid #94a3b8;
			line-height: 1.4;
			font-size: 1.2rem;
			color: #fff;
		}

		.prompt-text {
			display: flex;
			align-items: center;
			margin-top: 1.6rem;
			padding: 1.2rem 0;
			width: 31.1rem;
			// height: 268rem;
			border-radius: 0.4rem;
			line-height: 1.4;
			font-size: 1.2rem;
			color: #fff;
		}

		.prompts {
			display: flex;
			flex: 1;
			align-items: center;
			justify-content: space-between;

			.prompt-item {
				display: flex;
				align-items: center;
				justify-content: center;
				width: 5rem;
				height: 3.6rem;
				background: #1A1E2D;
				border-radius: 0.2rem;
				border: 0.1rem solid #4E5C8D;
			}
		}

		.operate {
			display: flex;
			justify-content: space-between;
			flex-direction: row;
			margin-top: 1.6rem;
			width: 100%;

			.op-item {
				display: flex;
				justify-content: center;
				align-items: center;
				flex-direction: column;
				width: 6rem;
				height: 5.6rem;
				background: #1a1e2d;
				border-radius: 0.2rem;
				border: 0.1rem solid #4e5c8d;
				color: #fff;
			}
		}

		.btn-group {
			display: flex;
			justify-content: space-between;
			// padding: 32rem 0 278rem;
			padding: 1.6rem 0;

			&.flex-1 {
				justify-content: center;
			}

			>.btn {
				position: relative;
				display: flex;
				justify-content: center;
				align-items: center;
				width: 13.6rem;
				height: 3.6rem;
				background: linear-gradient(90deg, #836CF7 0%, #EC6DC0 100%);
				border-radius: 0.4rem;

				&:before {
					content: "";
					position: absolute;
					top: 0.1rem;
					left: 0.1rem;
					display: block;
					width: 13.4rem;
					height: 3.4rem;
					background: #11182f;
					border-radius:0.4rem;
				}

				.btn-text {
					z-index: 1;
					padding: 0 0.8rem;
					font-size: 1.2rem;
					font-weight: bold;
					line-height: 2.2rem;
					
				}
			}

			.redo {}

			.delete {}
		}
	}
	.margin-top_40{
		margin-top: 2rem;
	}
	.icon-36 {
		z-index: 1;
	}
</style>