import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
// 可选：自定义 Babel 插件
import { createHtmlPlugin } from 'vite-plugin-html';
import svgr from 'vite-plugin-svgr';
import { readFileSync } from 'node:fs';
import path from 'path';

// 解析 tsconfig.json 中的 paths
function resolveTsconfigPaths() {
	const tsconfig = JSON.parse(readFileSync('./tsconfig.json', 'utf8'));
	const paths = tsconfig.compilerOptions.paths || {};
	const aliases = {};
	for (const [key, value] of Object.entries(paths)) {
		const alias = key.replace('/*', '');
		const resolvedPath = path.resolve(__dirname, value[0].replace('/*', ''));
		aliases[alias] = resolvedPath;
	}
	return aliases;
}

export default defineConfig({
	mode: 'development',
	server: {
		port: 3000,
		host: '0.0.0.0',
	},
	publicDir: 'public',
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
		alias: resolveTsconfigPaths(),
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
