<template>
	<div class="preview" v-if="visible">
		<div class="mask"></div>
		<!--<image id="img" mode="widthFix" class="image" @longpress="saveImg" :style="styles" :src="src">
		</image>-->
		<el-image id="img" class="image" fit="scale-down" :style="styles" :src="src" ></el-image>
		<div class="icon-48 icon-close bg-img" @click="close"></div>
		<div class="function">
			<img @click="transform('zoom-in')" class="img" src="@/static/images/draw/search@2x.png">
			<img @click="transform('zoom-out')" class="img" src="@/static/images/draw/Zoom-in@2x.png">
			<img @click="transform('fullscreen')" class="img" src="@/static/images/draw/Full-screen-one@2x.png">
			<img @click="transform('rotate-left')" class="img" src="@/static/images/draw/xuanzhuan@2x.png">
			<img @click="transform('rotate-right')" class="img" src="@/static/images/draw/xuanzhuan2@2x.png">
		</div>
	</div>
</template>

<script>
	export default {
		props: {
			visible: Boolean,
			src: String
		},
		model: {
			event: "visibleChange",
			prop: "visible",
		},
		data() {
			return {
				scale: 1,
				rotate: 0,
				isFullscreen: false
			}
		},
		computed: {
			styles() {
				return {
					transform: `scale(${this.scale}) rotateZ(${this.rotate}deg)`
				}
			}
		},
		methods: {
			saveImageToPhotosAlbum() {

				// #ifdef APP-PLUS
				uni.saveImageToPhotosAlbum({
					filePath: this.src,
					success: () => {
						this.$ui.showToast("保存图片到相册成功！");
					},
					fail: (e) => {
						this.$ui.showToast("保存失败！");
					},
					complete: () => {
						this.$ui.hideLoading();
					},
				});
				// #endif
				// #ifdef H5
				window.open(this.src)
				// #endif

			},
			saveImg() {
				uni.showActionSheet({
					itemList: ['保存图片'],
					success: (res) => {
						if (res.tapIndex == 0) {
							this.saveImageToPhotosAlbum()
						}
					}
				})
			},
			transform(type) {
				switch (type) {
					case 'zoom-in':
						if (this.scale < 0.4) {
							return;
						}
						this.scale -= 0.2
						break;
					case 'zoom-out':
						if (this.scale > 2) {
							return;
						}
						this.scale += 0.2
						break;
					case 'fullscreen':
						this.toggleFullscreen()
						break;
					case 'rotate-left':
						this.rotate -= 90;
						break;
					case 'rotate-right':
						this.rotate += 90;
						break;

				}
			},
			toggleFullscreen() {
				if (this.isFullscreen) {
					this.scale = 1;
				} else {
					uni.createSelectorQuery().in(this).select('#img').boundingClientRect((res) => {
						const {
							screenWidth,
							screenHeight
						} = uni.getSystemInfoSync();
						const imgHeight = res.height;
						const imgWidth = res.width;
						const scaleWidth = screenWidth / imgWidth;
						const scaleHeight = screenHeight / imgHeight;
						const scale = scaleWidth > scaleHeight ? scaleHeight : scaleWidth;

						if (scale < 1) {
							this.scale = 1;
						} else {
							this.scale = scale;
						}
					}).exec()
				}

				this.isFullscreen = !this.isFullscreen;
			},
			close() {
				this.$emit("visibleChange", !this.visible);
			}
		}
	}
</script>

<style lang="scss" scoped>
	.preview {
		position: fixed;
		z-index: 999;
		overflow: hidden;
		left: 0;
		top: 0;
		display: flex;
		justify-content: center;
		align-items: center;
		width: 100%;
		height: calc(100%);

	}

	.image {
		z-index: 996;
		margin-top: -5rem;
		width: 90%;
		transform: scale(1) rotateZ(0);
		transition: all .3s;
	}

	.icon-close {
		position: absolute;
		z-index: 996;
		right: 2.5rem;
		top: 6rem;
		cursor: pointer;
	}

	.function {
		position: absolute;
		left: 6.25rem;
		bottom: 5rem;
		z-index: 996;
		display: flex;
		justify-content: center;
		align-items: center;

		width: 25rem;
		height: 3.5rem;
		border-radius: 1.5rem;
		background: grey;
		opacity: 0.5;

		.img {
			margin: 1rem;
			width: 2.5rem;
			height: 2.5rem;
			cursor: pointer;
		}
	}

	.mask {
		position: absolute;
		z-index: 995;
		width: 100%;
		height: 100%;
		opacity: 0.8;
		background: #000;
	}
</style>