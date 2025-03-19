import React, { useCallback, useRef } from "react";

import {
  Button,
  Dialog,
  Empty,
  Form,
  FormItem,
  Image,
  Space,
} from "@antmjs/vantui";
import { Text, Textarea, View } from "@tarojs/components";
import styles from "./index.module.less";
import dayjs from "dayjs";
import { getFormatWeekdays } from "@/utils/date";
import request from "@/utils/request";
import { generateFileUrl, getNoDataUrl } from "@/utils/file";
import Taro from "@tarojs/taro";
import { EOrderStatus, StatusMap } from "@/pages/order/constants";
import { divide } from "@/utils/number";
import ProxyWrapped from "@/component/ProxyWrapped";
import ScrollPage from "@/hooks/usePageRequest";

const DialogInstance = Dialog.createOnlyDialog();
const Index: React.FC = () => {
  const form = Form.useForm();

  const actionRef = useRef<any>();
  const refundReview = useCallback((orderItem, isPass: boolean) => {
    DialogInstance.confirm({
      renderTitle: (
        <View
          style={{
            textAlign: "left",
            paddingLeft: "22px",
          }}
          className={"text-lg"}
        >
          {"是否申请退款" + isPass ? "通过?" : "拒绝?"}
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
              label={"备注"}
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
                      placeholder="请输入备注"
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

            const _url = isPass
              ? `/client/order/refund/approve`
              : `/client/order/refund/reject`;

            request
              .post(_url, {
                reason: val?.reason,
                order_id: orderItem?.id,
              })

              .then((res) => {
                if (res?.success) {
                  resolve(true);
                  actionRef.current?.reload();
                  return;
                }
                resolve(false);
              })
              .catch(() => {
                resolve(false);
              });
          });
        });
      },
    });
  }, []);

  const renderActions = (orderItem) => {
    if (orderItem?.status < EOrderStatus.Completed) {
      return (
        <Space direction={"vertical"}>
          <Button
            type={"primary"}
            size={"small"}
            onClick={() => {
              refundReview(orderItem);
            }}
          >
            退款
          </Button>
        </Space>
      );
    }
    if (orderItem?.status === EOrderStatus.RefundInProgress) {
      return (
        <Space direction={"horizontal"}>
          <Button
            type={"danger"}
            size={"small"}
            onClick={() => {
              refundReview(orderItem, false);
            }}
          >
            退款拒绝
          </Button>
          <Button
            type={"primary"}
            size={"small"}
            onClick={() => {
              refundReview(orderItem, true);
            }}
          >
            退款通过
          </Button>
        </Space>
      );
    }
  };

  return (
    <ScrollPage
      actionRef={actionRef}
      request={(params) => {
        return request.get("/client/admin/order", {
          params: {
            ...params,
            status: 70,
          },
        });
      }}
    >
      {(orderDataList) => {
        return (
          <>
            <View>
              <Space direction={"vertical"} block gapVertical={16}>
                {orderDataList?.length ? (
                  orderDataList.map((orderItem) => {
                    return (
                      <View className={styles.orderItem}>
                        <View className={styles.orderItemHeader}>
                          <View className={styles.orderItemStatus}>
                            {StatusMap[orderItem.status]}
                          </View>

                          <View className={styles.orderItemTime}>
                            {dayjs(orderItem.created_at).format(
                              "YYYY/MM/DD HH:mm "
                            )}
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
                              <Text>
                                ￥{divide(orderItem?.amount_total, 100)}
                              </Text>
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
          </>
        );
      }}
    </ScrollPage>
  );
};

export default Index;
