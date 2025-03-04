import React from "react";
import Layout from "@/component/Layout";
import { useRouter } from "@tarojs/taro";
import { Text, View } from "@tarojs/components";
import styles from "./index.module.less";
import { Button, Cell, Form } from "@antmjs/vantui";
import Image from "@/component/Image";
import { generateFileUrl } from "@/utils/file";
import classNames from "classnames";
import useRequest from "@/hooks/useRequest";
import request from "@/utils/request";
import { formatSortTime } from "@/utils/date";
import { EOrderStatus } from "@/pages/order/constants";

const OrderCreate: React.FC = () => {
  const router = useRouter();

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
      console.log("=====data=====", data);
      wx.requestPayment({
        timeStamp: data.timestamp,
        nonceStr: data.nonce_str,
        package: data.package,
        signType: "RSA" as any,
        paySign: data.pay_sign,
        success: (res) => {
          console.log(res);
        },
        fail: (e) => {
          console.log(e);
        },
      });
    }
  };

  return (
    <Layout
      loading={loading}
      footer={
        orderData?.status === EOrderStatus.PendingPayment ? (
          <View className={styles.footer}>
            <View className={styles.footerPrice}>
              <Text className={"text-12"}>￥</Text>
              {orderData?.amount_total || 0}
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
          </View>
        ) : undefined
      }
    >
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
                  {item?.amount}
                </Text>
              </View>
              <View>
                <Text className={styles.productNum}> x {item?.count}</Text>
              </View>
            </View>
          </View>
        ))}
      </View>
      <View className={classNames("mt-16", styles.card)} style={{ padding: 0 }}>
        {orderData?.amount_delivery ? (
          <Cell
            title="配送费"
            value={`￥${orderData?.amount_delivery || 0}`}
          ></Cell>
        ) : null}
        <Cell
          title="优惠券"
          value={`-￥${orderData?.amount_discount || 0}`}
        ></Cell>

        <Cell title="实付" value={`￥${orderData?.amount_total || 0}`}></Cell>
      </View>

      <View className={classNames("mt-16", styles.card)} style={{ padding: 0 }}>
        <Cell
          title="创建时间"
          value={formatSortTime(orderData?.created_at)}
        ></Cell>

        {orderData?.status !== EOrderStatus.PendingPayment &&
        orderData?.status !== EOrderStatus.Cancelled ? (
          <Cell
            title="支付时间"
            value={
              orderData?.payment_at
                ? formatSortTime(orderData?.payment_at)
                : "--"
            }
          ></Cell>
        ) : null}
      </View>
    </Layout>
  );
};

export default OrderCreate;
