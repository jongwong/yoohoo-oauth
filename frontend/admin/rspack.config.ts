import { defineConfig } from '@rspack/cli';
import { ProvidePlugin, rspack } from '@rspack/core';
import * as path from 'path';
import { readFileSync } from 'fs';
// @ts-ignore
import ReactRefreshPlugin from '@rspack/plugin-react-refresh';

const isDev = process.env.NODE_ENV === 'development';
// Target browsers, see: https://github.com/browserslist/browserslist
const targets = ['chrome >= 87', 'edge >= 88', 'firefox >= 78', 'safari >= 14'];

// 解析 tsconfig.json 中的 paths
function resolveTsconfigPaths() {
	const tsconfig = JSON.parse(readFileSync('./tsconfig.json', 'utf8'));
	const paths = tsconfig.compilerOptions.paths || {};
	const aliases: Record<string, any> = {};
	for (const [key, value] of Object.entries(paths)) {
		const _val = value as any;
		const alias = key.replace('/*', '');
		if (_val && _val[0]) {
			const resolvedPath = path.resolve(__dirname, _val[0].replace('/*', ''));
			aliases[alias] = resolvedPath;
		}
	}
	return aliases;
}

export default defineConfig({
	context: __dirname,
	entry: {
		main: './src/index.tsx',
	},
	stats: {
		warnings: false, // 禁止显示警告
	},
	experiments: {
		css: true,
	},
	output: {
		globalObject: 'self',
		filename: '[name].bundle.js',
		path: path.resolve(__dirname, 'dist'),
	},
	externals: {
		LowCodeDragItem: 'LowCodeDragItem',
		LowCodeItemContainer: 'LowCodeItemContainer',
	},
	resolve: {
		extensions: ['.js', '.tsx', '.ts', '.json', '.css', '.less'],
		alias: resolveTsconfigPaths(),
	},
	module: {
		parser: {
			'css/auto': {
				namedExports: false,
				auto: true,
				localIdentName: '[name]__[local]___[hash:base64:5]',
			},
		},
		rules: [
			{
				test: /\.ttf$/,
				use: ['file-loader'],
			},
			{
				test: /\.svg$/,
				use: [
					{
						loader: '@svgr/webpack', // 使用 svgr-loader
						options: {
							svgo: true, // 启用 svgo 优化
						},
					},
				],
			},
			{
				test: /\.txt$/,
				use: [
					{
						loader: 'raw-loader',
					},
				],
			},
			{
				test: /\.(jsx?|tsx?)$/,
				use: [
					{
						loader: 'builtin:swc-loader',
						options: {
							jsc: {
								parser: {
									syntax: 'typescript',
									tsx: true,
								},
								transform: {
									react: {
										runtime: 'automatic',
										development: isDev,
										refresh: isDev,
									},
								},
							},
							env: { targets },
						},
					},
				],
			},
			{
				test: /\.module\.(less|css)$/,
				type: 'css/auto', // 👈
				use: ['less-loader'],
			},
			{
				test: /\.less$/,
				type: 'css/auto', // 👈
				use: ['less-loader'],
			},
		],
	},
	plugins: [
		new rspack.HtmlRspackPlugin({
			template: './public/index.html',
		}),

		new ProvidePlugin({
			process: [require.resolve('process/browser')],
			Buffer: ['buffer', 'Buffer'],
		}),
		new ReactRefreshPlugin(),
	].filter(Boolean),
	optimization: {
		minimizer: [
			new rspack.SwcJsMinimizerRspackPlugin(),
			new rspack.LightningCssMinimizerRspackPlugin({
				minimizerOptions: { targets },
			}),
		],
	},
	devServer: {
		port: 3000,
		hot: true,
		liveReload: true,
		proxy: [
			{
				context: ['/preview'], // 代理路径
				target: 'http://localhost:3001',
				changeOrigin: true, // 修改请求头中的 Origin
			},
			{
				context: ['/api'], // 代理路径
				target: 'http://localhost:3001',
				changeOrigin: true, // 修改请求头中的 Origin
				pathRewrite: { '^/api': '' }, // 重写路径，将 `/api` 替换为空字符串
			},
		],
		historyApiFallback: true,
	},
});
