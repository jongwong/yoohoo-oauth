import React, { useState } from "react";
import Layout from "@/component/Layout";
import Taro, { useRouter } from "@tarojs/taro";
import { Text, View } from "@tarojs/components";
import styles from "./index.module.less";
import { Button, Cell, Form } from "@antmjs/vantui";
import Image from "@/component/Image";
import { generateFileUrl } from "@/utils/file";
import classNames from "classnames";
import useRequest from "@/hooks/useRequest";
import request from "@/utils/request";
import { EOrderStatus, EOrderStatusMap } from "@/constant/order";
import dayjs from "dayjs";
import { transformMoney } from "@/utils/number";
import { gotoPayPageResult } from "@/pages/order/utils";
import { formatMiddleTime } from "@/utils/date";
import Tag from "@/component/Tag";

const OrderDetail: React.FC<{}> = () => {
  const router = useRouter();

  const [saveLoading, setSaveLoading] = useState(false);
  const form = Form.useForm();
  // 请求产品列表数据
  const { loading, data: orderData } = useRequest(
    async () => {
      return request.get(`/client/order/${router.params?.id}`, {
        params: {},
      });
    },
    {
      refreshDeps: [router.params?.id],
      ready: !!router.params?.id,
      onSuccess: (res) => {
        form.setFields(res?.data || {});
        return res?.data;
      },
    }
  );

  const { loading: groupLoading, data: groupData } = useRequest(
    async () => {
      return request.get(`/client/group/${orderData?.ref_id}`, {
        params: {},
      });
    },
    {
      refreshDeps: [orderData],
      ready: orderData?.ref_type === 1 && !!orderData?.ref_id,
    }
  );
  const isOverTime = groupData?.time_end < dayjs().valueOf();
  const submitHandle = async () => {
    setSaveLoading(true);
    const res = await request
      .post(`/client/order/pay/submit`, {
        open_id: wx.getStorageSync("open_id"),
        order_id: orderData?.id,
      })
      .finally(() => {
        setSaveLoading(false);
      });

    if (res?.success) {
      const data: Record<string, string> = res?.prepay_info || {}; // 假设返回的数据在 resp.data
      wx.requestPayment({
        timeStamp: data.timestamp,
        nonceStr: data.nonce_str,
        package: data.package,
        signType: "RSA" as any,
        paySign: data.pay_sign,
        success: () => {
          gotoPayPageResult(true);
        },
        fail: () => {
          gotoPayPageResult(false);
        },
      });
    }
  };

  const renderFooter = () => {
    if (orderData?.status === EOrderStatus.PendingPayment) {
      return (
        <View className={styles.footer}>
          <>
            <View className={styles.footerPrice}>
              <Text className={"text-12"}>￥</Text>
              {transformMoney(orderData?.amount_total || 0)}
            </View>
            <Button
              type="primary"
              block
              size={"small"}
              disabled={isOverTime}
              style="margin-left: 120px"
              onClick={() => submitHandle()}
            >
              立即支付
            </Button>
          </>
        </View>
      );
    }
  };

  const renderFee = () => {
    return (
      <View className={classNames("mt-16", styles.card)} style={{ padding: 0 }}>
        <Cell
          title="配送费"
          value={
            orderData?.amount_delivery
              ? `￥${transformMoney(orderData?.amount_delivery || 0)}`
              : "免配送费"
          }
        ></Cell>
        <Cell
          title="优惠券"
          value={
            orderData?.amount_discount
              ? `-￥${transformMoney(orderData?.amount_discount || 0)}`
              : "无"
          }
        ></Cell>

        <Cell
          title="实付"
          value={`￥${transformMoney(orderData?.amount_total || 0)}`}
        ></Cell>
      </View>
    );
  };

  const renderProduct = () => {
    return (
      <View className={classNames("mt-16", styles.card)}>
        {orderData?.items?.map((item, idx) => (
          <View
            className={classNames(styles.productCard, idx ? "mt-8" : undefined)}
            key={item?.id}
          >
            <Image
              src={generateFileUrl(item?.product_image_url, true)}
              fadeIn
              fallback
              className={styles.productImage}
              mode="aspectFill"
            />
            <View className={styles.productInfo}>
              <View className={styles.productTitleRow}>
                <View className={styles.productTitle}>
                  {item?.product_name}
                </View>

                <Text className={styles.productPrice}>
                  <Text className={"text-12"}>￥</Text>
                  {transformMoney(item?.amount)}
                </Text>
              </View>
              <View>
                <Text className={"text-sm text-grey-dark"}>
                  {item?.sku_name}
                </Text>
              </View>
              <View>
                <Text className={styles.productNum}> x {item?.count}</Text>
              </View>
            </View>
          </View>
        ))}
      </View>
    );
  };

  return (
    <Layout
      loading={loading || saveLoading || groupLoading}
      footer={renderFooter()}
    >
      <View
        className={classNames(styles.card, "flex justify-between items-center")}
        style={{
          marginBottom: 16,
        }}
      >
        <View className={"mb-8 flex items-end"} style={{ fontSize: 16 }}>
          {EOrderStatusMap.has(orderData?.status) ? (
            <Tag
              status={EOrderStatusMap.get(orderData?.status)?.status}
              size={"large"}
            >
              {EOrderStatusMap.getText(orderData?.status)}
            </Tag>
          ) : null}
        </View>
        <View>
          {orderData?.ref_type === 1 && orderData?.ref_id ? (
            <Button
              plain
              onClick={() => {
                Taro.navigateTo({
                  url: `/pages/group/detail/index?id=${orderData?.ref_id}`,
                });
              }}
            >
              {"查看拼团 >"}
            </Button>
          ) : null}
        </View>
      </View>

      <View className={styles.areaCard}>
        <View>
          <View className={styles.areaCardLocationTitle}>
            {orderData?.delivery_point_name}
          </View>
          <View className={styles.areaCardLocationDesc}>
            {orderData?.delivery_point_address}
          </View>
        </View>
        <View className={"mt-16"}>
          <View className={styles.areaCardLocationTitle}>收货人</View>
          <View className={styles.areaCardLocationDesc}>
            {orderData
              ? orderData?.consignee_name + " " + orderData?.consignee_mobile
              : "--"}
          </View>
        </View>

        <View className={"mt-16"}>
          <View className={styles.areaCardLocationTitle}>预计到达时间</View>
          <View className={styles.areaCardLocationDesc}>
            {groupData ? formatMiddleTime(groupData.time_delivery_start) : "--"}
          </View>
        </View>
      </View>

      {renderProduct()}

      {renderFee()}
      <View className={classNames("mt-16", styles.card)} style={{ padding: 0 }}>
        <Cell
          title="创建时间"
          value={dayjs(orderData?.created_at).format("YYYY-MM-DD HH:mm")}
        ></Cell>

        {orderData?.status !== EOrderStatus.PendingPayment &&
        orderData?.status !== EOrderStatus.Cancelled ? (
          <Cell
            title="支付时间"
            value={
              orderData?.payment_at
                ? dayjs(orderData?.payment_at).format("YYYY-MM-DD HH:mm")
                : "--"
            }
          ></Cell>
        ) : null}
      </View>
    </Layout>
  );
};

export default OrderDetail;
