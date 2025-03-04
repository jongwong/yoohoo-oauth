import React from "react";

import Layout from "../../component/Layout";
import { Button, Empty, Image, Space, Tab, Tabs, Toast } from "@antmjs/vantui";
import { View } from "@tarojs/components";
import styles from "./index.module.less";
import dayjs from "dayjs";
import { getFormatWeekdays } from "@/utils/date";
import useRequest from "@/hooks/useRequest";
import request from "@/utils/request";
import { generateFileUrl, getNoDataUrl } from "@/utils/file";
import { useGetState } from "ahooks";
import Taro from "@tarojs/taro";
import { EOrderStatus, StatusMap } from "@/pages/order/constants";

const Profile: React.FC = () => {
  const [currentStatus, setCurrentStatus, getCurrentStatus] = useGetState<
    number | undefined
  >(-1);

  const {
    data: orderDataList,
    loading,
    run: fetchOrderData,
  } = useRequest(() => {
    const val = getCurrentStatus();
    return request.get("/client/order/user", {
      params: {
        status: val === -1 ? undefined : val,
      },
    });
  });

  const { loading: cancelLoading, run: runCancelOrder } = useRequest(
    (id) => {
      return request.post(`/client/order/${id}/cancel`, {});
    },
    {
      onSuccess: (e) => {
        if (e.success) {
          Toast.success("取消成功");
          fetchOrderData();
        }
      },
      manual: true,
    }
  );

  const renderActions = (orderItem) => {
    if (orderItem?.status === EOrderStatus.PendingPayment) {
      return (
        <Space direction={"horizontal"}>
          <Button
            size={"small"}
            onClick={() => {
              runCancelOrder(orderItem.id);
            }}
          >
            取消订单
          </Button>
          <Button
            type={"primary"}
            size={"small"}
            onClick={(e) => {
              Taro.navigateTo({
                url: `/pages/order/detail/index?id=${orderItem?.id}`,
              });
              e.stopPropagation();
            }}
          >
            去支付
          </Button>
        </Space>
      );
    }
    if (orderItem?.status === EOrderStatus.PendingDelivery) {
      return (
        <Space direction={"horizontal"}>
          <Button type={"primary"} size={"small"}>
            确认收货
          </Button>
        </Space>
      );
    }
  };

  const tabList = [
    {
      value: -1,
      title: "全部订单",
    },
    {
      value: 10,
      title: "待支付",
    },
    {
      value: 20,
      title: "待收货",
    },
    {
      value: 30,
      title: "退款/售后",
    },
    {
      value: 60,
      title: "已完成",
    },
  ];
  return (
    <Layout
      style={{
        backgroundColor: "#f6f6f6",
      }}
      edge={"none"}
      loading={loading || cancelLoading}
    >
      <View>
        <Tabs
          className={styles.tabs}
          active={currentStatus}
          onChange={(e) => {
            const find = tabList.find((_it, idx) => idx === e.detail.index);

            setCurrentStatus(find?.value);
            setTimeout(() => {
              fetchOrderData();
            }, 100);
          }}
        >
          {tabList.map((it) => (
            <Tab key={it.value} title={it.title}></Tab>
          ))}
        </Tabs>

        <Space direction={"vertical"} block gapVertical={16}>
          {orderDataList?.length ? (
            orderDataList.map((orderItem) => {
              return (
                <View
                  className={styles.orderItem}
                  onClick={() => {
                    Taro.navigateTo({
                      url: `/pages/order/detail/index?id=${orderItem?.id}`,
                    });
                  }}
                >
                  <View className={styles.orderItemHeader}>
                    <View className={styles.orderItemStatus}>
                      {StatusMap[orderItem.status]}
                    </View>
                    <View className={styles.orderItemTime}>
                      {dayjs(orderItem.created_at).format("YYYY/MM/DD HH:mm ")}
                      {getFormatWeekdays(orderItem.created_at)}
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
          ) : (
            <View
              style={{
                marginTop: "15vh",
                height: "100vw",
                bottom: 0,
              }}
            >
              <Empty
                description={"暂无订单"}
                style={{ width: "100vw", flex: "0 0 auto" }}
                image={getNoDataUrl()}
              />
            </View>
          )}
        </Space>
      </View>
    </Layout>
  );
};

export default Profile;
