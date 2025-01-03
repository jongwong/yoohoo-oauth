export default defineAppConfig({
  entryPagePath: "pages/home/index",
  pages: ["pages/home/index", "pages/registration/index"],
  permission: {},
  debug: true,
  window: {
    backgroundTextStyle: "light",
    navigationBarBackgroundColor: "#fff",
    navigationBarTitleText: "WeChat",
    navigationBarTextStyle: "black",
    backgroundColor: "#73ba9c", // 设置窗口背景色
    backgroundColorContent: "#73ba9c",
  },
});
