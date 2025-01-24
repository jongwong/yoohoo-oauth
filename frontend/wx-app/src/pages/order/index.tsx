import React from "react";

import Layout from "../../component/Layout";
import { Button, Image, Space, Tab, Tabs } from "@antmjs/vantui";
import { View } from "@tarojs/components";
import styles from "./index.module.less";
import dayjs from "dayjs";
import { getFormatWeekdays } from "../../utils/date";

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
  const orderDataList = [
    {
      id: "202501060001",
      items: [
        {
          itemName: "苹果",
          quantity: 3,
          price: 5,
          image: "https://via.placeholder.com/100x100.png?text=苹果",
        },
        {
          itemName: "香蕉",
          quantity: 2,
          price: 3,
          image: "https://via.placeholder.com/100x100.png?text=香蕉",
        },
      ],
      totalPrice: 21,
      orderAt: 1704508800000, // 时间戳
      status: EOrderStatus.Completed, // 已完成
    },
    {
      id: "202501060002",
      items: [
        {
          itemName: "橙子",
          quantity: 1,
          price: 4,
          image: "https://via.placeholder.com/100x100.png?text=橙子",
        },
        {
          itemName: "西瓜",
          quantity: 1,
          price: 10,
          image: "https://via.placeholder.com/100x100.png?text=西瓜",
        },
      ],
      totalPrice: 14,
      orderAt: 1704508800000,
      status: EOrderStatus.InDelivery, // 已完成
    },
    {
      id: "202501060003",
      items: [
        {
          itemName: "芒果",
          quantity: 5,
          price: 6,
          image: "https://via.placeholder.com/100x100.png?text=芒果",
        },
        {
          itemName: "柚子",
          quantity: 2,
          price: 8,
          image: "https://via.placeholder.com/100x100.png?text=柚子",
        },
      ],
      totalPrice: 46,
      orderAt: 1704508800000,
      status: EOrderStatus.PendingPayment, // 已完成
    },
  ];

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
    >
      <View>
        <Tabs className={styles.tabs}>
          <Tab key={-1} title={"全部订单"}></Tab>
          <Tab key={1} title={"待支付"}></Tab>
          <Tab key={2} title={"待收货"}></Tab>
          <Tab key={3} title={"退款/售后"}></Tab>
        </Tabs>

        <Space direction={"vertical"} block gapVertical={16}>
          {orderDataList.map((it) => {
            return (
              <View className={styles.orderItem}>
                <View className={styles.orderItemHeader}>
                  <View className={styles.orderItemStatus}>
                    {statusMap[it.status]}
                  </View>
                  <View className={styles.orderItemTime}>
                    {dayjs(it.orderAt).format("YYYY/MM/DD HH:mm ")}
                    {getFormatWeekdays(it.orderAt)}
                  </View>
                </View>
                {/* 商品部分 */}
                <View className={styles.orderItemProduct}>
                  {it.items.map((item, index) => (
                    <View key={index}>
                      <Image src={item.image}></Image> {/* 商品图片 */}
                    </View>
                  ))}
                </View>

                {/* 底部部分 */}
                <View className={styles.orderItemFooter}>
                  {renderActions(it)}
                </View>
              </View>
            );
          })}
        </Space>
      </View>
    </Layout>
  );
};

export default Profile;
