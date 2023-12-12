<template>
	<div class="contact-us">
		<gpt-navigation-bar>
			<template>联系我们</template>
		</gpt-navigation-bar>
		<div class="content">
			<el-image fit="cover" :src="contactUs.mainImg" class="bg" style="width: 100%; height:16rem"></el-image>
			<img mode="widthFix" :src="logo" class="logo" />
			<div class="copywriting">{{contactUs.mainImgMessage}}</div>

			<div class="code-group">
				<div class="code-item">
					<img mode="widthFix" :src="contactUs.wxImg" class="code" />
					<div class="text font-24">{{contactUs.wxImgMessage}}</div>
				</div>
				<div class="code-item">
					<img mode="widthFix" :src="contactUs.joinImg" class="code" />
					<div class="text font-24">{{contactUs.joinImgMessage}}</div>
				</div>
				<div class="code-item">
					<img mode="widthFix" :src="contactUs.dy1Img" class="code" />
					<div class="text font-24">{{contactUs.dy1ImgMessage}}</div>
				</div>
				<div class="code-item">
					<img mode="widthFix" :src="contactUs.dy2Img" class="code" />
					<div class="text font-24">{{contactUs.dy2ImgMessage}}</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
	import gptNavigationBar from '@/components/h5/gpt/navigation-bar/index.vue';
	import {
		getContactUs,
		config
	} from "@/api/h5";
	export default {
		components: {gptNavigationBar},
		data() {
			return {
				contactUs: {
					"mainImg": "5",
					"mainImgMessage": null,
					"wxImg": "4",
					"wxImgMessage": null,
					"joinImg": "1",
					"joinImgMessage": null,
					"dy1Img": "4",
					"dy1ImgMessage": null,
					"dy2Img": "5",
					"dy2ImgMessage": null
				},
				logo: '',
			}
		},
		created() {
			getContactUs().then(res => {
				this.contactUs = res.data;
			})
			config().then(res => {
				this.logo = res.data.homeConfig.logo;
			})
		},
	};
</script>

<style lang="scss" scoped>
	.contact-us {
		min-height: 100%;
		background: var(--primary-color);
		color: var(--white-1);
	}

	.content {
		position: relative;
	}

	.bg {
		height: 16rem;
	}

	.logo {
		position: absolute;
		top: 4.6rem;
		left: 2.4rem;
		width: 13.5rem;
	}

	.copywriting {
		position: absolute;
		top: 8.2rem;
		left: 2.4rem;
		height: 2.9rem;
		font-size: 2rem;
		font-weight: bold;
		color: var(--white-1);
	}

	.code-group {
		flex: 1;
		display: flex;
		flex-wrap: wrap;
		justify-content: space-between;
		padding: 4.5rem 3rem 0;

		.code-item {
			display: flex;
			flex-direction: column;
			align-items: center;
			margin: 0 0 3.5rem;
		}

		.code {
			width: 13rem;
			height: 13rem;
			border-radius: 0.5rem;
		}

		.text {
			margin-top: 1.3rem;
			height: 1.7rem;
		}
	}
</style>