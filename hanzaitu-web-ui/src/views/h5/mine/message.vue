<template>
	<div class="message-container">
		<gpt-navigation-bar :rightVisible="false">
			<template> 消息中心 </template>
		</gpt-navigation-bar>

		<div class="message-list">
			<div v-for="item in message.items" :key="item.key" class="message-item">
				<div class="message-title">
					<div class="tit">{{ item.title }}</div>
					<div class="link" @click="showDetail(item)">查看详情>></div>
				</div>
				<p class="message-content">{{item.content}}</p>
			</div>
		</div>

		<gpt-modal v-model="dialogVisible" width="34.3rem">
			<template v-slot:header>
				{{ selectedMessage.title }}
			</template>
			<template>
				<p class="message-content">{{selectedMessage.content}}</p>
			</template>
		</gpt-modal>
	</div>
</template>

<script>
	import gptNavigationBar from '@/components/h5/gpt/navigation-bar/index.vue';
	import gptModal from '@/components/h5/gpt/modal/index.vue';
	import {
		getMsgList
	} from "@/api/h5";
	export default {
		components: {
			gptNavigationBar,
			gptModal,
		},
		data() {
			return {
				dialogVisible: false,
				selectedMessage: {},
				message: {
					items: [],
				},
			};
		},
		created() {
			getMsgList().then((res) => {
				this.message.items = res.data.map(item => {
					item.content = item.content.replace('\r\n', '<br/>')
					return item;
				})
			});
		},
		methods: {
			showDetail(val) {
				this.selectedMessage = val;
				this.dialogVisible = true;
			},
		},
	};
</script>

<style lang="scss" scoped>
	.message-container {
		padding-bottom: 2rem;
		min-height: 100%;
		background: var(--primary-color);
		color: var(--white-1);
		font-weight: 500;
	}

	.message-list {
		margin: auto;
		width: 34.3rem;

		.message-item {
			overflow: hidden;
			margin-top: 2rem;
			padding: 1.6rem;
			width: 100%;
			height: 15.1rem;
			background: #2e354f;
			border-radius: 0.4rem;

			.message-title {
				display: flex;
				justify-content: space-between;
				align-items: flex-start;
				padding-bottom: 0.8rem;
				border-bottom: 0.1rem solid #7886bd;
				font-size: 1.6rem;
			}
			.tit{
				width: calc(100% - 8.25rem);
			}
			.link {
				font-size: 1.4rem;
				color: #5e86d6;
				width: 7.25rem;
			}

			.message-content {
				padding-top: 0.8rem;
				height: 8rem;
				line-height: 1.3;
				font-size: 1.4rem;

				overflow: hidden;
				text-overflow: ellipsis;
				display: -webkit-box;
				line-clamp: 4;
				-webkit-line-clamp: 4;
				-webkit-box-orient: vertical;
			}
		}
	}

	.modal-content {
		display: block;
		overflow: hidden scroll;
		margin-top: 1.5rem;
		height: 28.7rem;
		line-height: 1.5;
		color: #94a3b8;
		font-size: 1.4rem;
	}
</style>