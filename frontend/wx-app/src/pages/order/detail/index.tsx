import React from "react";
import Layout from "@/component/Layout";
import { useRouter } from "@tarojs/taro";
import { Text, View } from "@tarojs/components";
import styles from "./index.module.less";
import { Cell, Form } from "@antmjs/vantui";
import Image from "@/component/Image";
import { generateFileUrl } from "@/utils/file";
import classNames from "classnames";
import useRequest from "@/hooks/useRequest";
import request from "@/utils/request";
import { formatSortTime } from "@/utils/date";

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
  return (
    <Layout loading={loading}>
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
        <Cell
          title="支付时间"
          value={
            orderData?.payment_at ? formatSortTime(orderData?.payment_at) : "--"
          }
        ></Cell>
      </View>
    </Layout>
  );
};

export default OrderCreate;
