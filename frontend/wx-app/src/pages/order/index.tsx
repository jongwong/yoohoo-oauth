import React, { useCallback, useState } from "react";

import Layout from "../../component/Layout";
import {
  Button,
  Dialog,
  Empty,
  Form,
  FormItem,
  Image,
  Space,
  Tab,
  Tabs,
  Toast,
} from "@antmjs/vantui";
import { Text, Textarea, View } from "@tarojs/components";
import styles from "./index.module.less";
import dayjs from "dayjs";
import { getFormatWeekdays } from "@/utils/date";
import useRequest from "@/hooks/useRequest";
import request from "@/utils/request";
import { generateFileUrl, getNoDataUrl } from "@/utils/file";
import { useGetState } from "ahooks";
import Taro from "@tarojs/taro";
import { EOrderStatus, EOrderStatusMap } from "@/constant/order";
import { divide } from "@/utils/number";
import ProxyWrapped from "@/component/ProxyWrapped";
import { gotoPayRefundResult } from "@/pages/order/utils";

const DialogInstance = Dialog.createOnlyDialog();
const Profile: React.FC = () => {
  const [currentStatus, setCurrentStatus, getCurrentStatus] = useGetState<
    number | undefined
  >(-1);
  const [saveLoading, setSaveLoading] = useState(false);
  const form = Form.useForm();
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

  const refundConfirm = useCallback((orderItem) => {
    DialogInstance.confirm({
      zIndex: 0,
      renderTitle: (
        <View
          style={{
            textAlign: "left",
            paddingLeft: "22px",
          }}
          className={"text-lg"}
        >
          是否申请退款?
        </View>
      ),
      message: (
        <View
          style={{
            textAlign: "left",
          }}
        >
          <Form form={form} initialValues={{ reason: "" }}>
            <FormItem
              name={"reason"}
              label={"退款原因"}
              required
              requiredIcon={null}
              layout={"vertical"}
              trigger={"onInput"}
              valueFormat={(e) => e.target.value}
            >
              <ProxyWrapped>
                {(cfg) => (
                  <View
                    style={{
                      border: "1px solid #ebedf0",
                      backgroundColor: "#f6f6f6",
                      padding: "8px",
                    }}
                    className={"w-1-1"}
                  >
                    <Textarea
                      placeholder="请输入退款原因"
                      className={"w-1-1"}
                      showCount
                      {...cfg}
                      style={{
                        height: "50px",
                      }}
                    />
                  </View>
                )}
              </ProxyWrapped>
            </FormItem>
          </Form>
        </View>
      ),
      loading: false,
      beforeClose: async (action) => {
        return new Promise(async (resolve, reject) => {
          if (action === "cancel") {
            resolve(true);
            return true;
          }
          return form.validateFields((err, val) => {
            if (err?.length) {
              resolve(false);
              return;
            }

            Toast.loading({
              message: "退款中",
              duration: 3000,
            });
            request
              .post(`/client/order/refund`, {
                reason: val?.reason,
                open_id: wx.getStorageSync("open_id"),
                order_id: orderItem?.id,
              })

              .then((res) => {
                if (res?.success) {
                  resolve(true);
                  gotoPayRefundResult("processing");
                  Toast.clear();
                  return;
                }
                Toast.fail({
                  message: res?.message || "退款失败",
                  duration: 2000,
                });
                resolve(false);
              })
              .catch(() => {
                resolve(false);
                Toast.clear();
              });
          });
        });
      },
    });
  }, []);

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
            hairline
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
        <Space direction={"vertical"}>
          <Button
            type={"primary"}
            size={"small"}
            loading={false}
            onClick={() => {
              refundConfirm(orderItem);
            }}
          >
            退款
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
                <View className={styles.orderItem}>
                  <View className={styles.orderItemHeader}>
                    <View className={styles.orderItemStatus}>
                      {EOrderStatusMap.getText(orderItem.status)}
                    </View>

                    <View className={styles.orderItemTime}>
                      {dayjs(orderItem.created_at).format("YYYY/MM/DD HH:mm ")}
                      {getFormatWeekdays(orderItem.created_at)}
                    </View>
                  </View>
                  {/* 商品部分 */}
                  <View
                    className={styles.orderItemProduct}
                    onClick={() => {
                      Taro.navigateTo({
                        url: `/pages/order/detail/index?id=${orderItem?.id}`,
                      });
                    }}
                  >
                    {orderItem.items.map((item, index) => (
                      <View key={index}>
                        <Image
                          src={generateFileUrl(item.product_image_url)}
                        ></Image>
                        {/* 商品图片 */}
                      </View>
                    ))}
                  </View>
                  <View className={"mb-8"}>
                    <Space
                      className={"w-1-1"}
                      direction={"vertical"}
                      align={"end"}
                    >
                      <View style={{ fontSize: 18 }}>
                        <Text style={{ fontSize: 14, color: "#666666" }}>
                          实付款 ￥
                        </Text>
                        <Text>￥{divide(orderItem?.amount_total, 100)}</Text>
                      </View>
                    </Space>
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
      <DialogInstance />
    </Layout>
  );
};

export default Profile;
