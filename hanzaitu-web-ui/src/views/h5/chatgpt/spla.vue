<template>
	<div>
		<div class="container">
			<img mode="widthFix" class="banner" src="../../../static/images/home/banner1.png">
			<div class="home-top-font">全民AI时代</div>
			<div class="home-top-font home-top-font2">集合灵感探索可能</div>
			<div class="home-top-subhead">用AI助力效率，灵感从此即可把握</div>
			<div class="trial-btn" @click="redirect('/chat')">立即体验</div>
			<div class="text">仅需一次输入&nbsp;&nbsp;&nbsp;抓准瞬间之美</div>
			<img mode="widthFix" class="banner" src="https://image.hanzaitu.com/static/h5/home/aigongju_banner.png">
			<div class="text-2">
				<div class="title font-linear-radient">AI&nbsp;文字工具</div>
				<div class="content">文字效率工具，定制角色问答，你的智能助手，海量内置提示词库，快速生成所需各种类型文案，优化AI绘画提示词，你想知道的都在这里</div>
			</div>
			<img mode="widthFix" class="banner" src="https://image.hanzaitu.com/static/h5/home/ailinggan_banner.png">
			<div class="text-2">
				<div class="title font-linear-radient">AI&nbsp;灵感绘画</div>
				<div class="content">3D卡通IP形象，打造自己的专属人物头像，享受随心所欲的创作乐趣，内置多种模型，让您的作品风格百变亮眼，不是大师胜似大师</div>
			</div>
			<img mode="widthFix" class="banner" src="https://image.hanzaitu.com/static/h5/home/weilaikeqi_banner.png">
			<div class="text-2">
				<div class="title font-linear-radient">未来可期&nbsp;不断更新</div>
				<div class="content">未来我们将不断丰富平台内容，构建一站式AI工具库，拓展AI在各个领域的可行性。让AI工具为企业赋能，为个人增效。</div>
			</div>
			<div class="text">仅需一次输入&nbsp;&nbsp;&nbsp;抓准瞬间之美</div>

			<div class="block">
				<div class="left">
					<div class="title">AI&nbsp;文字工具</div>
					<div class="subtitle">便捷聊天&nbsp;文字即现</div>
					<div @click="redirect('/chat')" class="btn">开始聊天&nbsp;>></div>
				</div>
				<img mode="widthFix" class="img" src="https://image.hanzaitu.com/static/h5/home/xiaotu_banner1.png">
			</div>

			<div class="block">
				<div class="left">
					<div class="title">AI&nbsp;灵感绘画</div>
					<div class="subtitle">快速上手&nbsp;即可出图</div>
					<div @click="redirect('/draw')" class="btn">开启创作&nbsp;>></div>
				</div>
				<img mode="widthFix" class="img" src="https://image.hanzaitu.com/static/h5/home/xiaotu_banner2.png">
			</div>

			<div class="block">
				<div class="left">
					<div class="title">商务合作</div>
					<div class="subtitle">期待您的洽谈合作</div>
					<div @click="redirect('/contact-us')" class="btn">立即沟通&nbsp;>></div>
				</div>
				<img mode="widthFix" class="img" src="https://image.hanzaitu.com/static/h5/home/xiaotu_banner3.png">
			</div>

			<div class="bottom">
				<div class="ai-font">
					<!-- <text class="font-linear-radient">AI</text>RABBIT -->
					<img class="logo" :src="logo" alt="后台需配置">
				</div>
				<div class="desc">{{desc}}</div>

				<div class="code-group">
					<div class="code">
						<div class="text">{{contactUs.wxImgMessage}}</div>
						<img mode="widthFix" class="img" :src="baseUrl+contactUs.wxImg">
					</div>
					<div class="code">
						<div class="text">{{contactUs.joinImgMessage}}</div>
						<img mode="widthFix" class="img" :src="baseUrl+contactUs.joinImg">
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
	import {
		getContactUs,
		config
	} from "@/api/h5";
	export default {
		data() {
			return {
				desc: '',
				contactUs: {
					"mainImg": "",
					"mainImgMessage": null,
					"wxImg": "",
					"wxImgMessage": null,
					"joinImg": "",
					"joinImgMessage": null,
					"dy1Img": "",
					"dy1ImgMessage": null,
					"dy2Img": "",
					"dy2ImgMessage": null
				},
				logo: null
			}
		},
		mounted() {
			getContactUs().then(res => {
				this.contactUs = res.data;
			})
			config().then(res => {
				this.desc = res.data.homeConfig.text;
				this.logo = this.baseUrl+ res.data.homeConfig.logo;
			})
		},
		methods: {
			redirect(path) {
				this.$router.push(path);
			},

		}
	}
</script>

<style lang="scss" scoped>
	.container {
		overflow: hidden;
		position: relative;
		width: 100%;
		background: #000;
		color: var(--white-1);
	}
	.home-top-font{
		text-align: center;
		font-size: 3.4rem;
		font-weight: bold;
		position: absolute;
		width: 100%;
		top: 25.5rem;
	}
	.home-top-font2{
		top: 30.5rem !important;
	}
	.home-top-subhead{
		text-align: center;
		font-size: 1.8rem;
		position: absolute;
		width: 100%;
		top: 35.5rem;
	}

	.trial-btn {
		z-index: 99;
		position: absolute;
		top: 40.5rem;
		left: 9.8rem;
		width: 17.8rem;
		height: 4.8rem;
		text-align: center;
		line-height: 4.8rem;
		border-radius: 8.1rem;
		background: linear-gradient(90deg, #836CF7 0%, #EC6DC0 100%);
		font-size: 2.4rem;
	}

	.banner {
		width: 100%;
	}

	.text {
		padding: 0.9rem 0 1.5rem;
		font-size: 2rem;
		font-weight: bold;
		line-height: 58px;
		text-align: center;
	}

	.text-2 {
		padding: 1rem 1.6rem;

		.title {
			font-size: 2.4rem;
			font-weight: bold;
			line-height: 3.5rem;
		}

		.content {
			padding: 0.2rem 0 3rem;
			line-height: 1.5;
			word-break: break-all;
		}
	}

	.block {
		display: flex;
		justify-content: space-between;
		margin: 1rem auto;
		padding: 1.2rem 2.4rem;
		width: 34.3rem;
		height: 11.3rem;
		background: #11172D;
		border-radius: 0.4rem;

		.left {
			position: relative;

			.title {
				font-size: 1.8rem;
				font-weight: 500;
				line-height: 2.6rem;
			}

			.subtitle {
				margin-top: 0.4rem;
				font-size: 1.4rem;
				line-height: 2rem;
				opacity: 0.7;
			}

			.btn {
				position: absolute;
				bottom: 0;
			}
		}



		.img {
			width: 12rem;
		}
	}

	.bottom {
		margin-top: 6.8rem;
		padding: 4rem 1.6rem;
		width: 100%;
		background: #11162A;
		box-sizing: border-box;
		.ai-font {
			margin: auto;
			width: 100%;
			height: 32px;
			text-align: center;
			font-weight: bold;
			font-size: 1.9rem;

			.logo {
				width: 18rem;
				height: 4.8rem;
			}
		}

		.desc {
			padding: 2.6rem 0 3rem;
			font-size: 1.6rem;
			font-weight: 500;
			line-height: 1.5;
		}

		.code-group {
			display: flex;
			justify-content: space-between;

			.code {
				width: 12rem;
				text-align: center;

				.text {
					font-size: 1.4rem;
					font-weight: 500;
					line-height: 2rem;
				}

				.img {
					margin-top: 1rem;
					width: 100%;
				}
			}


		}

	}
</style>