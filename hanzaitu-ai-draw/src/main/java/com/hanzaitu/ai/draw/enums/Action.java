package com.hanzaitu.ai.draw.enums;


public enum Action {
	/**
	 * 生成图片.
	 */
	IMAGINE,


	/**
	 * 选中放大.
	 */
	UPSCALE,


	/**
	 * 选中其中的一张图，生成四张相似的.
	 */
	VARIATION,


	/**
	 * 重新生成.
	 */
	RESET,


	/**
	 * 图转prompt.
	 */
	DESCRIBE,

	/**
	 * 提示词优化
	 */
	SHORTEN,

	/**
	 * 风格化更强烈
	 */
	HIGH_VARIATION,

	/**
	 * 风格化更加贴近原生
	 */
	LOW_VARIATION,

	/**
	 * 镜头拉近2.0数值
	 */
	HIGH_ZOOM,

	/**
	 * 镜头拉近1.5数值
	 */
	LOW_ZOOM

}
