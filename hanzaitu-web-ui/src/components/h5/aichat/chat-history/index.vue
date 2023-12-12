<template>
	<div class="history-panel" :class="visible ? 'visible' : 'hide'">
		<!-- 遮罩 -->
		<div class="mask" @click="closePanel"></div>
		<!-- 内容区 -->
		<div class="content-panel">
			<div class="header">
				<div class="text">聊天记录</div>
				<div @click="clearWindow" class="icon-36 icon-setting bg-img"></div>
			</div>

			<div :style="{ height: contentBoxHeight }" :scroll-y="true" class="content">
				<!-- <empty v-if="!windows.length" placeholder="聊天记录空空如也~"></empty> -->
				<div class="history-list">
					<div class="history" :class="winId === item.winId ? 'active' : ''" v-for="item in windows"
						:key="item.winId" @click="openWindow(item.winId)">
						<div class="text">{{ item.winTitle }}</div>
					</div>
				</div>
			</div>

			<div class="bottom">
				<div class="add-chat" @click="newWindow">
					<div class="icon-36 icon-add bg-img"></div>
					<div class="text font-linear-radient">新增聊天</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
	import Empty from "../../empty";
	import {
		getTitleList,
		delWindow,
		delAllWindow
	} from "@/api/h5";
	import {
		randomString
	} from "@/utils/h5/common";

	export default {
		components: {
			Empty,
		},
		created() {
			this.contentBoxHeight = 500;
		},
		props: {
			visible: Boolean,
			isGpt4: Boolean
		},
		model: {
			prop: "visible",
			event: "visibleChange",
		},
		data() {
			return {
				contentBoxHeight: 0,
				winId: "",
				windows: [],
			};
		},
		watch: {
			async visible(val) {
				if (val) {
					const {
						data
					} = await getTitleList(this.isGpt4);
					this.windows = data;
				}
			},
		},
		methods: {
			clearWindow() {
				this.$ui.usingLoading(async () => {
					await delAllWindow(this.isGpt4);
					this.windows = []
					this.winId = randomString(14);
					this.$emit("newWindow", this.winId);
				})
			},
			closePanel() {
				this.$emit("visibleChange", false);
			},
			openWindow(val) {
				this.winId = val;
				this.$emit("openWindow", this.winId);
				this.closePanel();
			},
			newWindow() {
				this.winId = randomString(14);
				this.$emit("newWindow", this.winId);
				this.closePanel();
			},
		},
	};
</script>

<style lang="scss" scoped>
	.history-panel {
		position: fixed;
		z-index: 2;
		left: 0;
		top: 4.5rem;
		height: calc(100% - 4.5rem);

		>div {
			width: 100%;
			height: 100%;
		}

		&.visible {
			width: 100%;

			.mask {
				display: block;
			}

			.content-panel {
				width: 29rem;
			}
		}

		&.hide {
			width: 0;

			.mask {
				display: none;
			}

			.content-panel {
				width: 0;
			}
		}

		.mask {
			position: absolute;
			left: 0;
			top: 0;
			background: var(--black-1);
			opacity: 0.7;
		}

		.content-panel {
			overflow: hidden;
			position: absolute;
			left: 0;
			top: 0;
			height: 100%;
			background: var(--primary-color);
			color: var(--white-1);
			transition: width 0.2s ease-out;

			&.visible {
				width: 29rem;
			}

			&.hide {
				width: 0;
			}

			.header {
				display: flex;
				justify-content: space-between;
				align-items: center;
				padding: 0.9rem 1.6rem;
				width: 100%;
				height: 4.4rem;
				background: #181a21;

				.text {
					min-width: 12rem;
					line-height: 2.6rem;
				}

			}

			.content {
				.history-list {
					display: flex;
					flex-direction: column;
					align-items: center;
					justify-content: center;
					padding: 1.1rem 0;

					.history {
						position: relative;
						margin: 0.5rem;
						width: 25.8rem;
						height: 3rem;
						line-height: 3rem;
						font-size: 1.2rem;
						border-radius: 0.2rem;
						background: #fff;

						.text {
							position: absolute;
							top: 0.1rem;
							left: 0.1rem;
							padding: 0 0.8rem;
							width: 25.6rem;
							height: 2.8rem;
							background: #11141e;
							overflow: hidden;
							text-overflow: ellipsis;
							white-space: nowrap;
						}

						&.active {
							background: linear-gradient(90deg, #836CF7 0%, #EC6DC0 100%);
						}
					}
				}
			}

			.bottom {
				position: absolute;
				width: 100%;
				height: 6.6rem;
				bottom: 0;
				background: var(--primary-color);

				.add-chat {
					box-sizing: border-box;
					position: relative;
					display: flex;
					justify-content: center;
					align-items: center;
					margin: auto;
					width: 19.6rem;
					height: 4.2rem;
					background: linear-gradient(90deg, #836CF7 0%, #EC6DC0 100%);
					border-radius: 0.4rem;

					&:before {
						content: "";
						position: absolute;
						top: 0.1rem;
						left: 0.1rem;
						display: block;
						width: 19.4rem;
						height: 4rem;
						background: #11182f;
						border-radius: 0.4rem;
					}

					.icon-add {
						z-index: 1;
					}

					.text {
						z-index: 1;
						padding: 0 0.8rem;
						font-size: 1.2rem;
						font-weight: bold;
						line-height: 2.2rem;
					}
				}
			}
		}
	}
</style>