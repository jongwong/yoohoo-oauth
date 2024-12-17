import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'path';
// 可选：自定义 Babel 插件
import { createHtmlPlugin } from 'vite-plugin-html';
import svgr from 'vite-plugin-svgr';

export default defineConfig({
	mode: 'development',
	server: {
		port: 3000,
		host: '0.0.0.0',
	},
	plugins: [
		svgr({
			// svgr options: https://react-svgr.com/docs/options/
			svgrOptions: {
				plugins: ['@svgr/plugin-svgo', '@svgr/plugin-jsx'],
				svgoConfig: {
					floatPrecision: 2,
				},
			},

			// esbuild options, to transform jsx to js
			esbuildOptions: {
				// ...
			},

			// A minimatch pattern, or array of patterns, which specifies the files in the build the plugin should include.
			include: '**/*.svg',

			//  A minimatch pattern, or array of patterns, which specifies the files in the build the plugin should ignore. By default no files are ignored.
			exclude: '',
		}),
		react(), // 代替 ReactRefreshWebpackPlugin

		createHtmlPlugin({
			minify: true,
			entry: '/src/index.tsx',
			template: `public/vite.html`,
			filename: 'index.html',
		}),
	],
	resolve: {
		extensions: ['.ts', '.tsx', '.js', '.jsx', '.json'],
		alias: {
			'@': path.resolve(__dirname, './src'),
			'@containers': path.resolve(__dirname, './src/containers'),
			'@public': path.resolve(__dirname, './public'),
		},
	},
	css: {
		preprocessorOptions: {
			less: {
				javascriptEnabled: true, // 支持 JS
			},
			postcss: {
				plugins: [],
			},
		},
	},
	build: {
		sourcemap: true, // Useful to diagnose issues in production
		minify: false,
	},
	optimizeDeps: {
		include: ['react', 'react-dom', 'react-router-dom', 'react-dnd', 'react-dnd-html5-backend'],
	},
	define: {
		// 如果需要使用 process.env 变量，可以在这里添加
		'process.env': JSON.stringify(process.env),
	},
});
