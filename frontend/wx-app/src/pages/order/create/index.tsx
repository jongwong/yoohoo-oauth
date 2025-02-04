import React, { useMemo, useState } from "react";
import Layout from "@/component/Layout";
import { useRequest } from "ahooks";
import request from "@/utils/request";
import { useRouter } from "@tarojs/taro";
import { Text, View } from "@tarojs/components";
import styles from "./index.module.less";
import { Tag } from "@antmjs/vantui";
import { formatSortTime } from "@/utils/date";
import { first } from "lodash-es";

const OrderCreate: React.FC = () => {
  const router = useRouter();

  const [currentConsignee, setCurrentConsignee] = useState();
  // 请求产品列表数据
  const { loading: productLoading, data: productDataRes } = useRequest(
    async () => {
      return request.get(`/client/group/product/${router.params?.product_id}`, {
        params: {},
      });
    },
    {
      refreshDeps: [router.params?.product_id],
      ready: !!router.params?.product_id,
      onSuccess: (res) => {
        return res?.data;
      },
    }
  );

  const { loading: consigneeLoading, data: consigneeDataRes } = useRequest(
    async () => {
      return request.get(`/client/consignee`, {
        params: {},
      });
    },
    {
      onSuccess: (res) => {
        setCurrentConsignee(first(res?.data || []));
      },
    }
  );

  // 请求产品列表数据
  const { loading: areaLoading, data: areaDataRes } = useRequest(
    async () => {
      return request.get(`/client/store/area/${router.params?.area_id}`, {
        params: {},
      });
    },
    {
      refreshDeps: [router.params?.area_id],
      ready: !!router.params?.area_id,
      onSuccess: (res) => {
        return res?.data;
      },
    }
  );

  const productData = useMemo(() => productDataRes?.data, [productDataRes]);
  const areaData = useMemo(() => areaDataRes?.data, [areaDataRes]);
  const consigneeData = useMemo(
    () => consigneeDataRes?.data,
    [consigneeDataRes]
  );

  console.log("=====productData=====", productData);
  console.log("=====areaData=====", areaData);

  const renderAlert = () => {
    if (productData?.group_required_count! > 1) {
      return (
        <View>
          <Tag round plain type="warning">
            {productData?.group_required_count}人成团
          </Tag>
          <Text className={"text-red text-12"}>
            需要{productData?.group_required_count}
            人以后才可成团，成团失败后支付金额会自动原路返回
          </Text>
        </View>
      );
    }
    return (
      <View>
        <Tag round type="danger" color="#ffe1e1" textColor="red">
          100%拼成
        </Tag>
        <Text className={"text-red text-12"}>
          附近多人正在拼团，支付成功自动拼成不用等
        </Text>
      </View>
    );
  };

  return (
    <Layout loading={productLoading || areaLoading || consigneeLoading}>
      <View className={"mb-8"}>{renderAlert()}</View>

      <View className={styles.areaCard}>
        <View>
          <View className={styles.areaCardLocationTitle}>{areaData?.name}</View>
          <View className={styles.areaCardLocationDesc}>
            {areaData?.address}
          </View>
        </View>
        <View className={"mt-16"}>
          <View className={styles.areaCardLocationTitle}>收货人</View>
          <View className={styles.areaCardLocationDesc}>
            {currentConsignee
              ? currentConsignee?.name + " " + currentConsignee?.mobile
              : "请选择收货人"}
          </View>
        </View>
        <View className={"mt-16"}>
          <View className={styles.areaCardLocationTitle}>
            <Text>
              大约{formatSortTime(productData?.delivery_time_start)}送达
            </Text>
            <Tag round type="warning" className={"ml-4"}>
              提前预约，指定时间送达
            </Tag>
          </View>
          <View className={styles.areaCardLocationDesc}>
            最迟预约时间 {formatSortTime(productData?.time_group_end)}
          </View>
        </View>
      </View>
    </Layout>
  );
};

export default OrderCreate;
