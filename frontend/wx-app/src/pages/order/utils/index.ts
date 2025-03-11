import { navigateToStatusPage } from "@/utils/navigation";

export const gotoPayPageResult = (isSuccess?: boolean) => {
  navigateToStatusPage({
    status: !isSuccess ? "error" : "success",
    title: !isSuccess ? "支付失败" : "支付成功",
    buttons: [
      {
        text: "返回首页",
        actionType: "switchTab",
        url: "/pages/home/index",
      },
      {
        text: "查看订单",
        actionType: "switchTab",
        url: "/pages/order/index",
      },
    ],
  });
};

export const gotoPayRefundResult = (isSuccess?: boolean) => {
  navigateToStatusPage({
    status: !isSuccess ? "error" : "success",
    title: !isSuccess ? "申请退款失败" : "申请退款成功",
    buttons: [
      {
        text: "返回首页",
        actionType: "switchTab",
        url: "/pages/home/index",
      },
      {
        text: "查看订单",
        actionType: "switchTab",
        url: "/pages/order/index",
      },
    ],
  });
};
