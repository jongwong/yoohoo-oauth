import { defineConfig, type UserConfigExport } from "@tarojs/cli";
import devConfig from "./dev";
import prodConfig from "./prod";
import vitePluginImp from "vite-plugin-imp";
import * as path from "node:path";

const isVite = process.argv.includes("--vite");

const commonConfig: UserConfigExport<"vite"> | UserConfigExport<"webpack5"> = {
  projectName: "vite-demo",
  date: "2025-1-3",
  designWidth(input) {
    if (
      input.file &&
      input.file.replace(/\+/g, "/").indexOf("@antmjs/vantui") > -1
    ) {
      return 750;
    }
    return 375;
  },
  deviceRatio: {
    640: 2.34 / 2,
    750: 1,
    375: 2,
    828: 1.81 / 2,
  },
  sourceRoot: "src",
  outputRoot: "dist",
  public: {
    css: ["//at.alicdn.com/t/font_1989858_lu8avru5dog.css"],
  },
  plugins: ["@tarojs/plugin-html"],
  defineConstants: {},
  copy: {
    patterns: [
      {
        from: "src/assets", // 源文件夹
        to: "dist/assets", // 目标文件夹
      },
    ],
    options: {},
  },
  framework: "react",
  cache: {
    enable: false, // Webpack 持久化缓存配置，建议开启。默认配置请参考：https://docs.taro.zone/docs/config-detail#cache
  },
};
const viteConfig = defineConfig<"vite">(async (merge, { command, mode }) => {
  const baseConfig: UserConfigExport<"vite"> = {
    ...(commonConfig as any),
    mini: {
      postcss: {
        pxtransform: {
          enable: true,
          config: {
            selectorBlackList: ["nut-"],
          },
        },
        cssModules: {
          enable: false, // 默认为 false，如需使用 css modules 功能，则设为 true
          config: {
            namingPattern: "module", // 转换模式，取值为 global/module
            generateScopedName: "[name]__[local]___[hash:base64:5]",
          },
        },
      },
    },
    compiler: {
      vitePlugins: [
        vitePluginImp({
          libList: [
            {
              libName: "@nutui/nutui-react-taro",
              style: (name) => {
                return `@nutui/nutui-react-taro/dist/esm/${name}/style/css`;
              },
              replaceOldImport: false,
              camel2DashComponentName: false,
            },
          ],
        }),
      ],
      type: "vite",
    },
  };
  if (process.env.NODE_ENV === "development") {
    // 本地开发构建配置（不混淆压缩）
    return merge({}, baseConfig, devConfig);
  }
  // 生产构建配置（默认开启压缩混淆等）
  return merge({}, baseConfig, prodConfig);
});

// https://taro-docs.jd.com/docs/next/config#defineconfig-辅助函数
const webpackConfig = defineConfig<"webpack5">(async (merge, {}) => {
  const baseConfig: UserConfigExport<"webpack5"> = {
    ...(commonConfig as any),
    compiler: {
      type: "webpack5",
      prebundle: {
        enable: false,
      },
    },
    optimization: {
      splitChunks: {
        cacheGroups: {
          styles: {
            name: "styles",
            test: /\.css$/,
            chunks: "all",
            enforce: true,
          },
        },
      },
    },
    mini: {
      miniCssExtractPluginOption: {
        ignoreOrder: true,
      },
      lessLoaderOption: {
        // lessOptions: {
        //   modifyVars: {
        //     hack: `true; @import "${npath.join(
        //       process.cwd(),
        //       "src/styles/index.less"
        //     )}";`,
        //   },
        // },
        // 适用于全局引入样式
        // additionalData: "@import '~/src/styles/index.less';",
      },
      esnextModules: [/@antmjs[\/]vantui/],
      postcss: {
        autoprefixer: {
          enable: true,
          config: {},
        },
        pxtransform: {
          enable: true,
          config: {},
        },
        url: {
          enable: true,
          config: {
            limit: 1024, // 设定转换尺寸上限
          },
        },
        cssModules: {
          enable: true, // 默认为 false，如需使用 css modules 功能，则设为 true
          config: {
            namingPattern: "module", // 转换模式，取值为 global/module
            generateScopedName: "[name]__[local]___[hash:base64:5]",
          },
        },
      },
      webpackChain(chain) {
        chain.resolve.modules
          .clear() // 清除默认解析路径
          .add(path.resolve(__dirname, "../node_modules")) // 项目根目录的 node_modules
          .add("node_modules"); // 全局 node_modules

        // 可选：添加 TsconfigPathsPlugin（如果用到 TypeScript 配置路径别名）
        const TsconfigPathsPlugin = require("tsconfig-paths-webpack-plugin");
        chain.resolve.plugin("tsconfig-paths").use(TsconfigPathsPlugin);
      },
    },
  };
  if (process.env.NODE_ENV === "development") {
    // 本地开发构建配置（不混淆压缩）
    return merge({}, baseConfig, devConfig);
  }
  // 生产构建配置（默认开启压缩混淆等）
  return merge({}, baseConfig, prodConfig);
});
const config = isVite ? viteConfig : webpackConfig;

export default config;
