export enum EOrderStatus {
  PendingPayment = 10, // 待支付
  PendingDelivery = 20, // 待配送
  Preparing = 30, // 备餐中
  InDelivery = 40, // 配送中
  Completed = 50, // 已完成
  Cancelled = 60, // 已取消
  RefundInProgress = 70, // 退款中
  Refunded = 80, // 已退款
  RefundFailed = 90, // 退款失败
}

export const StatusMap = {
  10: "待支付", // 订单状态 10 - 待支付
  20: "待配送", // 订单状态 20 - 待配送
  30: "备餐中", // 订单状态 30 - 备餐中
  40: "配送中", // 订单状态 40 - 配送中
  50: "已完成", // 订单状态 50 - 已完成
  60: "已取消", // 订单状态 60 - 已取消
  70: "退款中", // 订单状态 70 - 退款中
  80: "已退款", // 订单状态 80 - 已退款
  90: "退款失败", // 订单状态 90 - 退款失败
};
