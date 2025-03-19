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

export const gotoPayRefundResult = (
  status: "error" | "success" | "processing"
) => {
  const ob = {
    error: {
      title: "申请退款失败",
      status: "error",
    },
    success: {
      title: "申请退款成功",
      status: "success",
    },
    processing: {
      title: "申请退款成功",
      status: "success",
    },
  };
  const find: any = ob[status];
  navigateToStatusPage({
    status: find?.status,
    title: find?.title,
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
