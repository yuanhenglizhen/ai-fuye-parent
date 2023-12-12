<template>
	<div class="history-panel" :class="visible ? 'visible' : 'hide'">
		<!-- 遮罩 -->
		<div class="mask" @click="closePanel"></div>
		<!-- 内容区 -->
		<div class="content-panel">
			<div class="header">
				<div class="text">绘画记录</div>
				<!-- <div @click="closePanel" class="icon-36 icon-setting bg-img"></div> -->
			</div>

			<div class="content scrollRef" ref="scrollRef" @touchstart="touchStart($event)" @touchmove="handleScrollDraw($event)">
				<empty v-if="!history.length" placeholder="绘画记录空空如也~"></empty>
				<div v-else class="history-list">
					<el-image v-for="item in history" :key="item.id" @click="chooseHistory(item)"
							  class="picture" style="" fit="scale-down" :src="item.imgUrl" >
						<div slot="error" class="picture" >
							<img style="width: 100%" src="@/static/images/draw/jiazai_shibai@2x.png" alt="">
						</div>
					</el-image>
				</div>
			</div>

			<!-- <div class="bottom">
        <div class="add-chat">
          <div class="icon-36 icon-add bg-img"></div>
          <div class="text font-linear-radient">新增聊天</div>
        </div>
      </div> -->
		</div>
	</div>
</template>

<script>
	import Empty from "../../empty";
	import {
		getTaskList, submitTransformTask
	} from "@/api/h5";

	export default {
		components: {
			Empty,
		},
		async created() {
			this.contentBoxHeight = 500;

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
				contentBoxHeight: 0,
				history: [],
				selectedId: '',
				totalPages:0,
				drawListParams:{
					"pageNo": 1,
					"pageSize": 10,
					"allowPaging": true
				},
				startY: 0,
			};
		},
		watch: {
			visible(val) {
				if (val) {
					this.getDrawList();
				}else{
					this.history = []
				}
			},
		},
		methods: {
			closePanel() {
				this.$emit("visibleChange", false);
			},
			chooseHistory(item) {
				this.selectedId = item.id;
				this.$emit("chooseHistory", this.selectedId);
				this.$emit("visibleChange", false);
			},

			async getDrawList(){
				const {data} = await getTaskList(this.drawListParams);
				data.list.map(item => {
					if (item.status === 'SUCCESS') {
						if (item.picture) {
							item.imgUrl = item.picture;
						} else {
							item.imgUrl = `/static/images/draw/jiazai_shibai@2x.png`
						}
					} else {
						item.imgUrl = `/static/images/draw/huihuajilu_shibai@2x.png`
					}
					this.history.push(item);
				})
			},

			touchStart (e) {
				this.startY = e.targetTouches[0].pageY
			},
			handleScrollDraw(e){
				if (e.targetTouches[0].pageY < this.startY) { // 上拉
					this.judgeScrollBarToTheEnd()
				}
				/*let el=e.target;
				if(Math.ceil(el.scrollTop+el.clientHeight) >= el.scrollHeight){
					if(this.drawListParams.pageNo <= this.totalPages){
						this.drawListParams.pageNo += 1;
						this.getDrawList();
					}
				}*/
			},
			// 判断滚动条是否到底
			judgeScrollBarToTheEnd () {
				let innerHeight = document.querySelector('.scrollRef').clientHeight
				let itemHeight = document.querySelector('.history-list').clientHeight
				let scrollTopss = document.querySelector('.scrollRef').scrollTop

				// 滚动条到底部的条件
				if (scrollTopss + innerHeight == itemHeight) {
					this.drawListParams.pageNo += 1;
					this.getDrawList();
				}
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
				height: calc(100% - 4.5rem);
				overflow-y: auto;
				.history-list {
					display: flex;
					flex-direction: column;
					align-items: center;
					justify-content: center;
					padding-bottom: 2rem;
					.picture {
						margin-top: 2rem;
						width: 18.8rem;

						&.active {
							&.IMAGINE {
								border: 0.05rem solid #47c4f7;
							}

							&.DESCRIBE {
								border: 0.05rem solid #f87def;
							}
						}
					}
				}
			}

			.bottom {
				position: absolute;
				width: 100%;
				height: 6.6rem;
				bottom: 0;

				.add-chat {
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