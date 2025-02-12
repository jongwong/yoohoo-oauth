import React from "react";

import Layout from "../../component/Layout";
import { Button, Image, Space, Tab, Tabs } from "@antmjs/vantui";
import { View } from "@tarojs/components";
import styles from "./index.module.less";
import dayjs from "dayjs";
import { getFormatWeekdays } from "../../utils/date";
import useRequest from "@/hooks/useRequest";
import request from "@/utils/request";
import { generateFileUrl } from "@/utils/file";

enum EOrderStatus {
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

const statusMap = {
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
const Profile: React.FC = () => {
  const { data: orderDataList, loading } = useRequest(() => {
    return request.get("/client/order/user");
  });
  const renderActions = (orderItem) => {
    if (orderItem?.status === EOrderStatus.PendingPayment) {
      return (
        <Space direction={"horizontal"}>
          <Button>取消订单</Button>
          <Button type={"primary"}>去支付</Button>
        </Space>
      );
    }
    if (orderItem?.status === EOrderStatus.PendingDelivery) {
      return (
        <Space direction={"horizontal"}>
          <Button type={"primary"}>确认收货</Button>
        </Space>
      );
    }

    return (
      <Space direction={"horizontal"}>
        <Button type={"primary"}>查看</Button>
      </Space>
    );
  };

  return (
    <Layout
      style={{
        backgroundColor: "#f6f6f6",
      }}
      edge={"none"}
      loading={loading}
    >
      <View>
        <Tabs className={styles.tabs}>
          <Tab key={-1} title={"全部订单"}></Tab>
          <Tab key={1} title={"待支付"}></Tab>
          <Tab key={2} title={"待收货"}></Tab>
          <Tab key={3} title={"退款/售后"}></Tab>
        </Tabs>

        <Space direction={"vertical"} block gapVertical={16}>
          {orderDataList?.length
            ? orderDataList.map((orderItem) => {
                return (
                  <View className={styles.orderItem}>
                    <View className={styles.orderItemHeader}>
                      <View className={styles.orderItemStatus}>
                        {statusMap[orderItem.status]}
                      </View>
                      <View className={styles.orderItemTime}>
                        {dayjs(orderItem.orderAt).format("YYYY/MM/DD HH:mm ")}
                        {getFormatWeekdays(orderItem.orderAt)}
                      </View>
                    </View>
                    {/* 商品部分 */}
                    <View className={styles.orderItemProduct}>
                      {orderItem.items.map((item, index) => (
                        <View key={index}>
                          <Image
                            src={generateFileUrl(item.product_image_url)}
                          ></Image>{" "}
                          {/* 商品图片 */}
                        </View>
                      ))}
                    </View>

                    {/* 底部部分 */}
                    <View className={styles.orderItemFooter}>
                      {renderActions(orderItem)}
                    </View>
                  </View>
                );
              })
            : null}
        </Space>
      </View>
    </Layout>
  );
};

export default Profile;
