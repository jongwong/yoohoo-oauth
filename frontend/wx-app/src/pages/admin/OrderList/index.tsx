import React, { useCallback, useRef } from "react";

import {
  Button,
  Dialog,
  Divider,
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
import { EOrderStatus, EOrderStatusMap } from "@/constant/order";
import { divide } from "@/utils/number";
import ProxyWrapped from "@/component/ProxyWrapped";
import ScrollPage from "@/hooks/usePageRequest";
import Tag from "@/component/Tag";

const DialogInstance = Dialog.createOnlyDialog();
const Index: React.FC<{
  status?: number;
}> = ({ status = EOrderStatus.RefundInProgress }) => {
  const form = Form.useForm();

  const actionRef = useRef<any>();
  const refundReview = useCallback(
    (orderItem, type: "pass" | "refuse" | "direct") => {
      let title = "是否申请退款" + type === "pass" ? "通过?" : "拒绝?";

      if (type === "direct") {
        title = "是否主动退款给用户?";
      }
      DialogInstance.confirm({
        renderTitle: (
          <View
            style={{
              textAlign: "left",
              paddingLeft: "22px",
            }}
            className={"text-lg"}
          >
            {title}
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
          return new Promise(async (resolve) => {
            if (action === "cancel") {
              resolve(true);
              return true;
            }
            return form.validateFields((err, val) => {
              if (err?.length) {
                resolve(false);
                return;
              }

              let _url =
                type === "pass"
                  ? `/client/order/refund/approve`
                  : `/client/order/refund/reject`;

              if (type === "direct") {
                _url = `/client/order/refund/direct`;
              }

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
    },
    []
  );

  const renderActions = (orderItem) => {
    if (
      orderItem?.status < EOrderStatus.Completed &&
      orderItem?.status !== EOrderStatus.RefundInProgress
    ) {
      return (
        <Space direction={"vertical"}>
          <Button
            type={"primary"}
            size={"small"}
            onClick={() => {
              refundReview(orderItem, "direct");
            }}
          >
            主动退款
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
              refundReview(orderItem, "refuse");
            }}
          >
            拒绝退款
          </Button>
          <Button
            type={"primary"}
            size={"small"}
            onClick={() => {
              refundReview(orderItem, "pass");
            }}
          >
            同意退款
          </Button>
        </Space>
      );
    }
  };

  return (
    <ScrollPage
      actionRef={actionRef}
      request={(params) => {
        return request.get("/client/admin/order/with_refund", {
          params: {
            ...params,
            status,
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
                          <View className={"flex justify-between"}>
                            <View className={"flex items-center"}>
                              <Image
                                height={30}
                                width={30}
                                round
                                src={orderItem?.user_avatar}
                              />
                              <View className={"ml-8"}>
                                {orderItem.user_nickname}
                              </View>
                            </View>

                            <View className={styles.orderItemStatus}>
                              <Tag
                                status={
                                  EOrderStatusMap.get(orderItem.status)?.status
                                }
                              >
                                {EOrderStatusMap.getText(orderItem.status)}
                              </Tag>
                            </View>
                          </View>
                        </View>
                        <View className={styles.orderItemTime}>
                          {dayjs(orderItem.created_at).format(
                            "YYYY/MM/DD HH:mm "
                          )}
                          {getFormatWeekdays(orderItem.created_at)}
                        </View>
                        <View className={styles.orderItemTime}>
                          {orderItem?.delivery_point_name}

                          <Text className={"ml-16"}>
                            {orderItem?.user_mobile}
                          </Text>
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
                        <Divider />
                        {orderItem?.status === EOrderStatus.RefundInProgress ? (
                          <View className={"text-red"}>
                            {"退款备注: " +
                              (orderItem?.refund_info?.apply_reason || "")}
                          </View>
                        ) : null}
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
