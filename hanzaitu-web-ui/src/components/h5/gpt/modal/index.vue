<template>

	<div v-if="visible" class="modal">
		<div class="content">
			<div class="header" :style="{ width: width }">
				<div @click="handleClose('close')" class="icon-40 icon-close bg-img"></div>
				<slot name="header"></slot>
			</div>
			<div class="body" :style="{ width: width, height:height }">
				<slot name="default"></slot>
			</div>
			<div class="footer" :style="{ width: width }"></div>
		</div>
		<div class="mask" @click="handleClose('mask')"></div>
	</div>
</template>

<script>
	export default {
		props: {
			visible: Boolean,
			width: {
				type: String,
				default: "32.7rem",
			},
			height: {
				type: String,
				default: "unset",
			},
			maskClose: {
				default: true,
				type: Boolean,
			},
		},
		model: {
			event: "visibleChange",
			prop: "visible",
		},

		created() {},

		methods: {
			handleClose(type) {
				if (!this.maskClose && type == "mask") {
					return;
				}
				this.$emit("visibleChange", false);
			},
		},
	};
</script>

<style lang="scss" scoped>
	.modal {
		position: fixed;
		z-index: 999;
		overflow: hidden;
		left: 0;
		top: 0;
		width: 100%;
		height: calc(100%);

		.content {
			position: absolute;
			top: 50%;
			transform: translateY(-50%);
			z-index: 996;
			display: flex;
			flex-flow: column;
			align-items: center;
			width: 100%;
			// height: 100%;
			// height: 410px;

			>div {
				// box-sizing: border-box;
				// width: 654rem;
				background: #1a1e2d;
			}

			.header {
				position: relative;
				padding: 1.5rem;
				text-align: center;
				border-top-left-radius: 0.8rem;
				border-top-right-radius: 0.8rem;
				font-size: 1.8rem;
				font-weight: 500;
				color: #fff;

				.icon-close {
					position: absolute;
					top: 1.6rem;
					right: 1.6rem;
				}
			}

			.body {
				padding: 0.5rem 1.5rem;
				min-height: 5rem;
			}

			.footer {
				height: 3rem;
				border-bottom-left-radius: 0.8rem;
				border-bottom-right-radius: 0.8rem;
			}
		}

		.mask {
			position: absolute;
			z-index: 995;
			width: 100%;
			height: 100%;
			opacity: 0.5;
			background: #000;
		}
	}
</style>