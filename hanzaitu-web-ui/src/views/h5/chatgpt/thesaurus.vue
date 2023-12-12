<template>
	<div class="thesaurus-container">
		<gpt-navigation-bar>
			<template> AI聊天提示词库 </template>
		</gpt-navigation-bar>

		<div class="word-list" v-for="prompt in prompts.items" :key="prompt.categoryId">
			<div class="word" v-for="item in prompt.promptList" :key="item.prompId" @click="showDetail(item)">
				<div class="label">{{ prompt.categoryName }}</div>
				<div class="word-title">{{ item.title }}</div>
				<div class="word-content">
					{{ item.content }}
				</div>
			</div>
		</div>

		<div class="word-detail-modal" v-if="detailVisible">
			<div class="detail-mask"></div>
			<div class="word-detail">
				<div class="detail-header">{{prompts.current.title}}</div>
				<div class="detail-content">
					<div class="scroll">{{prompts.current.content}}</div>
				</div>
				<div class="detail-btn-group">
					<div class="btn left" @click="detailVisible = false">取消</div>
					<div class="btn right" @click="detailVisible = false">
						<div class="text" @click="apply">应用</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
	import gptNavigationBar from '@/components/h5/gpt/navigation-bar/index.vue';
	import {
		getPromptList
	} from "@/api/h5";
	export default {
		components:{
			gptNavigationBar,
		},
		data() {
			return {
				detailVisible: false,
				prompts: {
					current: 0,
					items: [],
				},
			};
		},
		created() {

			this.$ui.usingLoading(async () => {
				const {
					data
				} = await getPromptList();
				this.prompts.items = data;
			})

		},
		methods: {
			showDetail(item) {
				this.detailVisible = true;
				this.prompts.current = item;
			},
			apply() {
				this.$router.go(-1);
				this.$eventBus.$emit("apply-prompt", this.prompts.current.content);
			}
		},
	};
</script>

<style lang="scss" scoped>
	.thesaurus-container {
		min-height: 100%;
		background: var(--primary-color);
		color: var(--white-1);

		.word-list {
			display: flex;
			flex-direction: column;
			align-items: center;
			padding: 2.5rem 0;

			.word {
				overflow: hidden;
				position: relative;
				margin: 0.5rem 0;
				padding: 0.8rem;
				width: 34.3rem;
				height: 7.1rem;
				border-radius: 0.4rem;
				border: 0.1rem solid #94a3b8;

				.label {
					position: absolute;
					top: 0;
					right: 0;
					width: 3.4rem;
					height: 1.8rem;
					line-height: 1.8rem;
					background: #23293e;
					font-size: 1rem;
					text-align: center;
					border-radius: 0 0 0 0.4rem;
				}

				.word-title {
					margin: 0.4rem 0;
					width: fit-content;
					max-width: 29rem;
					font-size: 14rem;

					overflow: hidden;
					text-overflow: ellipsis;
					white-space: nowrap;
				}

				.word-content {
					height: 6.6rem;
					font-size: 12px;
					color: rgba(255, 255, 255, 0.5);
					line-height: 14px;
				}
			}
		}

		.word-detail-modal {
			position: fixed;
			z-index: 99999999;
			top: 0;
			left: 0;
			width: 100%;
			height: 100%;

			.detail-mask {
				position: absolute;
				left: 0;
				top: 0;
				width: 100%;
				height: 100%;
				background: var(--black-1);
				opacity: 0.7;
			}

			.word-detail {
				position: absolute;
				left: 1.6rem;
				top: 50%;
				transform: translateY(-50%);
				display: flex;
				flex-direction: column;
				align-items: center;
				padding: 1.6rem 1.6rem 2.4rem;
				width: 34.3rem;
				height: 28.9rem;
				background: #11141e;

				.detail-header {
					margin: 0.4rem 0;
					width: fit-content;
					max-width: 31.1rem;
				}

				.detail-content {
					display: flex;
					justify-content: center;
					align-items: center;
					margin: 2.4rem 0;
					width: 31.1rem;
					height: 14.2rem;
					border-radius: 0.8rem;
					border: 0.1rem solid var(--white-1);
					font-size: 1.2rem;
					line-height: 1.5;
					color: rgba(255, 255, 255, 0.5);

					.scroll {
						overflow: hidden auto;
						width: 26.3rem;
						height: 10.8rem;
					}
				}

				.detail-btn-group {
					display: flex;
					justify-content: space-between;
					width: 31.1rem;

					.btn {
						position: relative;
						width: 14.6rem;
						height: 3.7rem;
						line-height: 3.7rem;
						background: #1a1e2d;
						border-radius: 0.4rem;
						font-size: 1.4rem;
						text-align: center;

						&.left {
							border:0.1rem solid #4e5c8d;
						}

						&.right {
							background: linear-gradient(90deg, #836CF7 0%, #EC6DC0 100%);

							.text {
								position: absolute;
								top: 0.1rem;
								left: 0.1rem;
								display: block;
								width: 14.4rem;
								height: 3.5rem;
								background: #11182f;
								border-radius: 0.4rem;
							}
						}
					}
				}
			}
		}
	}
</style>