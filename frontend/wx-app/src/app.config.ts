export default defineAppConfig({
  entryPagePath: "pages/home/index",
  pages: [
    "pages/home/index",
    "pages/classify/index",
    "pages/order/index",
    "pages/profile/index",
    "pages/registration/index",
  ],
  permission: {},
  debug: true,
  window: {
    backgroundTextStyle: "light",
    navigationBarTitleText: "WeChat",
    navigationBarTextStyle: "black",
    backgroundColor: "#fff", // 设置窗口背景色
    backgroundColorContent: "#fff",
  },
  tabBar: {
    list: [
      {
        pagePath: "pages/home/index", // 正确的路径，指向 pages/home/index
        text: "首页",
        iconPath: "assets/tab-bar/home.png",
        selectedIconPath: "assets/tab-bar/home-active.png",
      },
      {
        pagePath: "pages/classify/index", // 正确的路径，指向 pages/classify/index
        text: "点餐",
        iconPath: "assets/tab-bar/classify.png",
        selectedIconPath: "assets/tab-bar/classify-active.png",
      },
      {
        pagePath: "pages/order/index", // 正确的路径，指向 pages/cart/index
        text: "订单",
        iconPath: "assets/tab-bar/order.png",
        selectedIconPath: "assets/tab-bar/order-active.png",
      },
      {
        pagePath: "pages/profile/index", // 正确的路径，指向 pages/profile/index
        text: "我的",
        iconPath: "assets/tab-bar/profile.png",
        selectedIconPath: "assets/tab-bar/profile-active.png",
      },
    ],
  },
});
