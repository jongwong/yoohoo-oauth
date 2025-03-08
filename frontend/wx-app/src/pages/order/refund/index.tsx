import React from "react";
import OrderCreate from "@/pages/order/detail";

type orderRefundProps = {};
const orderRefund: React.FC<orderRefundProps> = (props) => {
  const { ...rest } = props;
  return <OrderCreate pageType={"refund"} />;
};
export default orderRefund;
