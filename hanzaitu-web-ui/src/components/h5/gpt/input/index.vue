<template>
	<div class="input-wrapper" :style="{ height: height }">
		<div class="scroll-view">
			<!--<textarea v-model="userInputText" auto-height class="textarea" maxlength="200"
				placeholder-class="textarea-placeholder" :placeholder="placeholder" :cursor-spacing="30"
				:disabled="disabled" />-->

			<el-input class="cont-textarea" type="textarea" :disabled="disabled" placeholder-class="textarea-placeholder"
					  :placeholder="placeholder" v-model="userInputText" :autosize="{ minRows: 1}" resize="none"
					  ></el-input>
		</div>
		<div @click="sendMessage" class="send-icon bg-img"></div>
	</div>
</template>

<script>
	import {
		mapState
	} from "vuex";
	export default {
		props: {
			placeholder: String,
			text: String,
			value: String,
			disabled: Boolean
		},
		// model: {
		// 	prop: 'active',
		// 	event: 'changeActive'
		// },
		data() {
			return {
				height: "86rem",
				userInputText: "",
			};
		},
		watch: {
			value(vl) {
				this.userInputText = vl;
			},
		},
		computed: {
			...mapState("user", ["token"]),
		},
		created() {
			//this.userInputText = text;
		},
		mounted() {
			// 无固定高度时，切换屏幕时的闪烁
			// 设置固定高度，初始华完成设置为auto
			setTimeout(() => {
				this.height = "auto";
			}, 200);
		},
		methods: {
			sendMessage() {
				if (this.disabled) {
					return
				}
				if (this.token) {
					if (this.userInputText) {
						this.$emit("send", this.userInputText);
						this.userInputText = "";
					}
				} else {
					this.$eventBus.$emit("need-login");
				}
			},
		},
	};
</script>

<style lang="scss" scoped>
	.input-wrapper {
		position: absolute;
		z-index: 2;
		left: 1.6rem;
		bottom: 2.4rem;
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 0.6rem 2rem;
		width: 34.3rem;
		min-height: 4.3rem;
		// height: 86rem;
		background: #1a1e2d;
		border: 0.1rem solid #4e5c8d;
		border-radius: 0.4rem;
		font-size: 1.4rem;
		font-weight: 500;
		box-sizing: border-box;
		.scroll-view {
			overflow-x: hidden;
			overflow-y: scroll;
			width: 31.5rem;
			max-height: 10rem;
		}

		.textarea {
			width: 100%;
			min-height: 1.6rem;
			line-height: 1.5;
			text-align: left;
			resize: none;
		}

		.send-icon {
			width: 2.4rem;
			height: 2.4rem;
			background-image: url(data:image/png;base64,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);
		}
	}

	.textarea-placeholder {
		font-size: 1.35rem;
		line-height: 1.5;
		color: var(--white-1);
	}
</style>