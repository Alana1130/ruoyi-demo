<script>
	import api from '@/utils/api.js'
	import request from '@/utils/request.js'
	export default {
		onLaunch: function() {
			if (!request.getToken()) {
				api.auth.login().catch(() => {})
			}
		},
		onShow: function() {
			console.log('App Show')
		},
		onHide: function() {
			console.log('App Hide')
		}
	}
</script>

<style>
	@import "@/common/style.css";
	/* 全局设计令牌：山野纸感 · 墨绿 + 星金 */
	page {
		--xlv-ink: #16211C;
		--xlv-ink-2: #46534C;
		--xlv-muted: #8A948F;
		--xlv-line: #E8E1D1;
		--xlv-paper: #F5F2EA;
		--xlv-card: #FFFFFF;
		--xlv-forest: #1F3D2F;
		--xlv-forest-2: #2D5A3D;
		--xlv-moss: #6E8F7D;
		--xlv-gold: #C9A227;
		--xlv-gold-soft: #F4E9CB;
		--xlv-danger: #BF4A3C;
		--xlv-shadow: 0 10rpx 30rpx rgba(22, 33, 28, 0.06);
		--xlv-radius-lg: 28rpx;
		--xlv-radius-md: 20rpx;
		--xlv-radius-sm: 12rpx;
		background: var(--xlv-paper);
		color: var(--xlv-ink);
		font-family: -apple-system, BlinkMacSystemFont, "PingFang SC", "Helvetica Neue", "Microsoft YaHei", sans-serif;
		-webkit-font-smoothing: antialiased;
	}
	view, text, scroll-view, swiper, swiper-item {
		box-sizing: border-box;
	}
</style>
