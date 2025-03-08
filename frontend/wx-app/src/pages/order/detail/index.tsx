import React, { useState } from "react";
import Layout from "@/component/Layout";
import Taro, { useRouter } from "@tarojs/taro";
import { Text, View } from "@tarojs/components";
import styles from "./index.module.less";
import { Button, Cell, Field, Form } from "@antmjs/vantui";
import Image from "@/component/Image";
import { generateFileUrl } from "@/utils/file";
import classNames from "classnames";
import useRequest from "@/hooks/useRequest";
import request from "@/utils/request";
import { EOrderStatus, StatusMap } from "@/pages/order/constants";
import dayjs from "dayjs";
import { transformMoney } from "@/utils/number";

const OrderCreate: React.FC<{
  pageType?: "refund" | "create";
}> = ({ pageType = "create" }) => {
  const router = useRouter();

  const isRefund = pageType === "refund";
  const [hasValidError, setHasValidError] = useState(false);

  const [refundReason, setRefundReason] = useState("");
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
  const submitHandle = async () => {
    const res = await request.post(`/client/order/pay/submit`, {
      open_id: wx.getStorageSync("open_id"),
      order_id: orderData?.id,
    });

    if (res.success) {
      const data: Record<string, string> = res?.data?.prepay_info || {}; // 假设返回的数据在 resp.data
      wx.requestPayment({
        timeStamp: data.timestamp,
        nonceStr: data.nonce_str,
        package: data.package,
        signType: "RSA" as any,
        paySign: data.pay_sign,
        success: (res) => {
          Taro.navigateTo({
            url: `/pages/order/detail/index?id=${res.data.id}`,
          });
        },
        fail: (e) => {
          console.log(e);
        },
      });
    }
  };

  const refundHandle = async () => {
    console.log("=====refundReason=====", refundReason);
    if (!refundReason?.trim()?.length) {
      setHasValidError(true);
      return Taro.showToast({
        title: "请输入退款原因",
        icon: "none",
      });
    }
    const res = await request.post(`/client/order/refund`, {
      reason: refundReason,
      open_id: wx.getStorageSync("open_id"),
      order_id: orderData?.id,
    });

    console.log("=====res=====", res);
  };
  const renderFooter = () => {
    if (isRefund) {
      return (
        <View className={styles.footer}>
          <Button
            type="primary"
            block
            size={"small"}
            onClick={() => refundHandle()}
          >
            申请退款
          </Button>
        </View>
      );
    }
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
        {orderData?.items.map((item) => (
          <View className={styles.productCard}>
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
                <Text className={styles.productNum}> x {item?.count}</Text>
              </View>
            </View>
          </View>
        ))}
      </View>
    );
  };
  if (isRefund) {
    return (
      <Layout loading={loading} footer={renderFooter()}>
        <View className={classNames(styles.card, "mb-16")}>
          <View className={"mb-8"} style={{ fontSize: 16 }}>
            {StatusMap[orderData?.status]}
          </View>
          {renderProduct()}
        </View>
        <View className={classNames(styles.card, "mb-16")}>{renderFee()}</View>
        <View className={styles.card}>
          <Field
            type="textarea"
            value={refundReason}
            onChange={(e) => {
              const val = e?.detail;
              console.log("=====val=====", val);
              setHasValidError(!!val?.trim());
              setRefundReason(val);
            }}
            style={"border: 1px solid #eee;background: rgba(0,0,0,0.01);"}
            placeholder="请输入退款原因"
            autosize
            focus
            errorMessage={
              hasValidError && !refundReason?.length
                ? "请输入退款原因"
                : undefined
            }
            showWordLimit
          />
        </View>
      </Layout>
    );
  }

  return (
    <Layout loading={loading} footer={renderFooter()}>
      <View
        className={styles.card}
        style={{
          marginBottom: 16,
        }}
      >
        <View className={"mb-8"} style={{ fontSize: 16 }}>
          {StatusMap[orderData?.status]}
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

export default OrderCreate;
