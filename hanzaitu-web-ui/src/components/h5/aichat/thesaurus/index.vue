<template>
	<div class="thesaurus-panel" :class="visible ? 'visible' : 'hide'">
		<!-- 遮罩 -->
		<div class="mask" @click="closePanel"></div>
		<!-- 内容区 -->
		<div class="content-panel">
			<div class="header">
				<div class="text">AI聊天提示词库</div>
				<div @click="openSearch" class="icon-search bg-img"></div>
			</div>

			<div class="tabs">
				<div v-for="item in prompts.items" :key="item.categoryId" @click="prompts.current = item.categoryId"
					class="tab" :class="prompts.current === item.categoryId ? 'active' : ''">{{ item.categoryName }}
				</div>
			</div>

			<div :style="{ height: contentBoxHeight }" :scroll-y="true" class="content">
				<empty v-if="!prompts.items.length" placeholder="空空如也~"></empty>
				<div v-if="prompts.current == prompt.categoryId" v-for="prompt in prompts.items"
					:key="prompt.categoryId" class="thesaurus-list">
					<div class="thesaurus" v-for="item in prompt.promptList" :key="item.prompId"
						@click="choosePrompt(item.content)">
						<div class="title">{{ item.title }}</div>
						<div class="content-item">
							{{ item.content }}
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
	import storage from "@/utils/h5/storage";
	import Empty from "../../empty";
	import {
		getPromptList
	} from "@/api/h5";

	export default {
		components: {
			Empty,
		},
		created() {
			this.contentBoxHeight = 500;
		},
		props: {
			visible: Boolean,
		},
		model: {
			prop: "visible",
			event: "visibleChange",
		},
		watch: {
			visible(val) {
				if (val) {
					let prompts = storage.get('_chat_prompts');
					if (prompts) {
						this.prompts.items = data;
						this.prompts.current = data[0].categoryId;
					}
					// 更新下一次打开时的内容
					getPromptList().then(({
						data
					}) => {
						storage.set('_chat_prompts', data);
						if (!prompts) {
							this.prompts.items = data;
							this.prompts.current = data[0].categoryId;
						}
					});
				}
			},
		},
		data() {
			return {
				contentBoxHeight: 0,
				prompts: {
					current: 0,
					items: [],
				},
			};
		},
		methods: {
			choosePrompt(val) {
				this.$emit("choosePrompt", val);
				this.closePanel();
			},
			closePanel() {
				this.$emit("visibleChange", false);
			},
			openSearch() {
				this.closePanel()
				this.$router.push("/pages/chatgpt/thesaurus");
			},
		},
	};
</script>

<style lang="scss" scoped>
	.thesaurus-panel {
		position: fixed;
		right: 0;
		z-index: 2;
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
			top: 0;
			right: 0;
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

				.icon-search {
					width: 1.8rem;
					height: 1.8rem;
				}
			}

			.tabs {
				display: flex;
				width: 100%;
				height: 4.8rem;
				background: #11141e;

				.tab {
					position: relative;
					width: 50%;
					height: 100%;
					line-height: 4.8rem;
					text-align: center;
					font-size: 1.4rem;
					font-weight: 500;

					&.active {
						color: #5e86d6;

						&:before {
							content: "";
							position: absolute;
							left: 50%;
							bottom: 0;
							transform: translateX(-50%);
							width: 1.6rem;
							height: 0.3rem;
							background: #5e86d6;
							border-radius: 0.15rem;
						}
					}
				}
			}

			.content {
				// .placeholder-content {
				// 	height: 100%;
				// }

				// .placeholder-content,
				overflow-y: auto;
				height: calc(100% - 9.2rem);
				.thesaurus-list {
					display: flex;
					flex-direction: row;
					flex-wrap: wrap;
					align-items: center;
					justify-content: space-between;
					width: 29rem;
					padding: 1.1rem;

					.thesaurus {
						overflow: hidden;
						margin: 0.5rem;
						padding: 0.4rem;
						width: 12.1rem;
						height: 7.1rem;
						border-radius: 0.4rem;
						border: 0.1rem solid #94a3b8;

						.title {
							width: 8.4rem;
							height: 2.2rem;
							line-height: 2.2rem;
							font-size: 1.4rem;
							color: #ffffff;

							overflow: hidden;
							text-overflow: ellipsis;
							white-space: nowrap;
						}

						.content-item {
							font-size: 1.2rem;
							color: rgba(255, 255, 255, 0.5);
							line-height: 14px;
						}
					}
				}
			}
		}
	}
</style>