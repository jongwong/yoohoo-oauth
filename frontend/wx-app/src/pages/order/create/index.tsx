import React, { useState } from "react";
import Layout from "@/component/Layout";
import request from "@/utils/request";
import { useRouter } from "@tarojs/taro";
import { Text, View } from "@tarojs/components";
import styles from "./index.module.less";
import { Button, Form, FormItem, Icon, Tag } from "@antmjs/vantui";
import { formatSortTime } from "@/utils/date";
import { first } from "lodash-es";
import Image from "@/component/Image";
import { generateFileUrl } from "@/utils/file";
import classNames from "classnames";
import useRequest from "@/hooks/useRequest";
import CouponsPicker from "@/pages/order/create/components/CouponsPicker";
import { useUpdate } from "ahooks";

const OrderCreate: React.FC = () => {
  const router = useRouter();

  const [currentConsignee, setCurrentConsignee] = useState();
  const form = Form.useForm();
  const forceUpdate = useUpdate();
  // 请求产品列表数据
  const { loading: productLoading, data: productData } = useRequest(
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

  const { loading: consigneeLoading } = useRequest(
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
  const { loading: couponsLoading, data: couponsList = [] } = useRequest(
    async () => {
      return request.get(`/client/user/coupons`, {
        params: {},
      });
    },
    {
      refreshDeps: [router.params?.area_id],
      ready: !!router.params?.area_id,
    }
  );
  // 请求产品列表数据
  const { loading: deliveryFeeLoading, data: deliveryFee } = useRequest(
    async () => {
      return request.get(`/client/delivery/fee`);
    },
    {
      refreshDeps: [router.params?.area_id],
      ready: !!router.params?.area_id,
    }
  );

  // 请求产品列表数据
  const { loading: areaLoading, data: areaData } = useRequest(
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
  const getFormatValue = () => {
    const currentCoupons = form.getFieldValue("currentCoupons");
    const find = couponsList.find((item) => item.id === currentCoupons);
    const couponsFee = find?.discount_amount || 0;
    const _products = [
      {
        id: productData?.product_id,
        group_product_id: productData?.group_id,
        name: productData?.product_name,
        code: productData?.product_code,
        price: productData?.price,
        num: 1,
      },
    ];
    const amountProduct = _products.reduce((prev, next) => {
      return prev + next.price * next.num;
    }, 0);
    const val = {
      amount_delivery: deliveryFee || 0,
      amount_discount: couponsFee || 0,
      amount_product: amountProduct || 0,
      products: _products,
      coupons_id: currentCoupons,
      consignee_mobile: currentConsignee?.mobile,
      consignee_name: currentConsignee?.name,
      delivery_point_id: areaData?.id,
      delivery_point_name: areaData?.name,
      delivery_point_address: areaData?.address,
      amount_total: 0,
    };
    val.amount_total =
      val.amount_product + val.amount_delivery - val.amount_discount;
    return val;
  };

  const submitHandle = () => {
    const val = getFormatValue();
    request.post(`/client/order/submit`, val);
  };

  return (
    <Form initialValues={{ code: 3 }} form={form}>
      <Layout
        loading={
          productLoading ||
          areaLoading ||
          consigneeLoading ||
          couponsLoading ||
          deliveryFeeLoading
        }
        footer={
          <View className={styles.footer}>
            <View className={styles.footerPrice}>
              <Text className={"text-12"}>￥</Text>
              {getFormatValue()?.amount_total}
            </View>
            <View>
              <Button
                type="primary"
                style="margin-left: 120px"
                onClick={() => submitHandle()}
              >
                立即支付
              </Button>
            </View>
          </View>
        }
      >
        <View className={"mb-16"}>{renderAlert()}</View>

        <View className={styles.areaCard}>
          <View>
            <View className={styles.areaCardLocationTitle}>
              {areaData?.name}
            </View>
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
                大约{formatSortTime(productData?.time_delivery_start)}送达
              </Text>
              <Tag round type="warning" className={"ml-4"}>
                提前预约，指定时间送达
              </Tag>
            </View>
            <View className={styles.areaCardLocationDesc}>
              最迟{formatSortTime(productData?.time_delivery_end)}送达
            </View>
            <View className={styles.areaCardLocationDesc}>
              最迟预约时间 {formatSortTime(productData?.time_group_end)}
            </View>
          </View>
        </View>
        <View className={classNames("mt-16", styles.card)}>
          <View className={styles.productCard}>
            <Image
              src={generateFileUrl(productData?.thumbnail_image_url, true)}
              fadeIn
              fallback
              className={styles.productImage}
              mode="aspectFill"
            />
            <View className={styles.productInfo}>
              <View className={styles.productTitleRow}>
                <View className={styles.productTitle}>
                  {productData?.product_name}
                </View>

                <Text className={styles.productPrice}>
                  <Text className={"text-12"}>￥</Text>
                  {productData?.price}
                </Text>
              </View>
              <View>
                <Text className={styles.productNum}> x 1</Text>
              </View>
            </View>
          </View>

          <FormItem
            label={"配送费"}
            name={"_deliveryFee"}
            controllFlexEnd
            trigger={"none"}
          >
            <Text className={"text-12"}>￥</Text>
            {deliveryFee}
          </FormItem>
          <FormItem
            label={
              <View className={styles.couponsLabel}>
                <Icon
                  className={styles.couponsLabelIcon}
                  classPrefix="iconfont yh"
                  name="coupons"
                />
                优惠券
              </View>
            }
            name={"currentCoupons"}
            controllFlexEnd
            trigger={"none"}
          >
            <CouponsPicker
              couponsList={couponsList}
              value={form.getFieldValue("currentCoupons")}
              onConfirm={(e) => {
                form.setFieldsValue("currentCoupons", e);
                forceUpdate();
              }}
            />
          </FormItem>

          <FormItem
            label={
              <Text
                style={{
                  fontSize: "16px",
                }}
              >
                小计
              </Text>
            }
            name={"_total"}
            controllFlexEnd
            trigger={"none"}
          >
            <Text className={"text-12"}>￥</Text>
            {getFormatValue()?.amount_total}
          </FormItem>
        </View>
      </Layout>
    </Form>
  );
};

export default OrderCreate;
