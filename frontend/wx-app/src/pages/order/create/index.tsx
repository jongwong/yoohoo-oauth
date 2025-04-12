import React, { useMemo, useState } from "react";
import Layout from "@/component/Layout";
import request from "@/utils/request";
import { useRouter } from "@tarojs/taro";
import { Text, View } from "@tarojs/components";
import styles from "./index.module.less";
import { Button, Form, FormItem, Icon, Tag } from "@antmjs/vantui";
import { formatSortTime } from "@/utils/date";
import { first, isNumber } from "lodash-es";
import Image from "@/component/Image";
import { generateFileUrl } from "@/utils/file";
import classNames from "classnames";
import useRequest from "@/hooks/useRequest";
import CouponsPicker from "@/pages/order/create/components/CouponsPicker";
import { useUpdate } from "ahooks";
import { transformMoney } from "@/utils/number";
import { gotoPayPageResult } from "@/pages/order/utils";
import dayjs from "dayjs";

const OrderCreate: React.FC = () => {
  const router = useRouter();

  const [currentConsignee, setCurrentConsignee] = useState();
  const form = Form.useForm();
  const forceUpdate = useUpdate();

  const [skuCountList, setSkuCountList] = useState<
    {
      data: any;
      product_id: string;
      sku_id?: string;
      count: number;
    }[]
  >(wx.getStorageSync("CART_SKU_COUNT_LIST"));

  const [saveLoading, setSaveLoading] = useState(false);

  const groupId = useMemo(() => {
    return first(skuCountList)?.purchase_group_id;
  }, [skuCountList]);
  // 请求产品列表数据

  const { loading: groupLoading, data: groupData } = useRequest(
    async () => {
      return request.get(`/client/group/${groupId}`, {
        params: {},
      });
    },
    {
      refreshDeps: [groupId],
      ready: groupId,
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
      return request.get(`/client/coupons/user`, {
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
      return request.get(`/client/delivery/fee`, {
        params: {
          point_id: router.params?.area_id,
        },
      });
    },
    {
      refreshDeps: [router.params?.area_id],
      ready: !!router.params?.area_id,
    }
  );

  const { loading: orderLoading, data: orderList = [] } = useRequest(
    async () => {
      return request.get(`/client/group/order`, {
        params: {
          group_id: groupId,
        },
      });
    },
    {
      refreshDeps: [groupId],
      ready: !!groupId,
    }
  );

  const orderItemCount = useMemo(() => {
    // 将order的items 拍平
    const orderItems = orderList.reduce((prev, cur) => {
      return [...prev, ...(cur?.items || [])];
    }, []);

    return orderItems.reduce((prev, cur) => {
      if (cur.product_id === skuCountList[0].product_id) {
        return prev + (cur?.count || 0);
      }
      return prev;
    }, 0);
  }, [orderList]);

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
    const needCount = groupData?.group_required_count - (orderItemCount || 0);

    if (!isNumber(groupData?.group_required_count)) {
      return null;
    }
    if (needCount && needCount > 0) {
      return (
        <View>
          <Tag round plain type="warning">
            {groupData?.group_required_count}人成团
          </Tag>
          <Text className={"text-red text-12"}>
            还需要{needCount}
            才可成团，成团失败后支付金额会自动原路返回
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
    const _products = skuCountList.map((it) => {
      const data = it?.data;
      return {
        id: it.product_id,
        group_product_id: data?.group_product_id,
        name: data?.product_name,
        code: data?.product_code,
        sku_id: data.sku_id,
        sku_name: data?.sku_name,
        thumbnail_image: data?.thumbnail_image,
        price: data?.price,
        count: it.count,
      };
    });
    const amountProduct = _products.reduce((prev, next) => {
      return prev + next.price * next.count;
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

  const submitHandle = async () => {
    const val = getFormatValue();
    setSaveLoading(true);
    const res = await request
      .post(`/client/order/submit`, {
        ...val,
        open_id: wx.getStorageSync("open_id"),
      })
      .catch((e) => {
        setSaveLoading(false);
      });

    if (res?.success) {
      const resPay = await request
        .post(`/client/order/pay/submit`, {
          open_id: wx.getStorageSync("open_id"),
          order_id: res?.data?.id,
        })
        .catch((e) => {
          setSaveLoading(false);
        });

      if (resPay?.success) {
        const data: Record<string, string> = resPay?.data || {}; // 假设返回的数据在 resp.data
        wx.requestPayment({
          timeStamp: data.timestamp,
          nonceStr: data.nonce_str,
          package: data.package,
          signType: "RSA" as any,
          paySign: data.pay_sign,
          success: (res) => {
            gotoPayPageResult(true);
          },
          fail: (e) => {
            // gotoPayPageResult(true);
          },
        });
      }
    }
    setSaveLoading(false);
  };

  const isOverTime = groupData?.time_end < dayjs().valueOf();
  return (
    <Form initialValues={{ code: 3 }} form={form}>
      <Layout
        loading={
          groupLoading ||
          areaLoading ||
          consigneeLoading ||
          couponsLoading ||
          deliveryFeeLoading ||
          saveLoading ||
          orderLoading
        }
        footer={
          <View className={styles.footer}>
            <View className={styles.footerPrice}>
              <Text className={"text-12"}>￥</Text>
              {transformMoney(getFormatValue()?.amount_total)}
            </View>

            <Button
              type="primary"
              block
              size={"small"}
              disabled={isOverTime}
              style="margin-left: 120px"
              loadingMode={"toast"}
              onClick={() => submitHandle()}
            >
              立即支付
            </Button>
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
                大约{formatSortTime(groupData?.time_delivery_start)}送达
              </Text>
              <Tag round type="warning" className={"ml-4"}>
                提前预约，指定时间送达
              </Tag>
            </View>
            <View className={styles.areaCardLocationDesc}>
              最迟{formatSortTime(groupData?.time_delivery_end)}送达
            </View>
            <View className={styles.areaCardLocationDesc}>
              最迟预约时间 {formatSortTime(groupData?.time_group_end)}
            </View>
          </View>
        </View>
        <View className={classNames("mt-16", styles.card)}>
          {skuCountList?.map((it) => {
            const productItem = it?.data;
            return (
              <View className={styles.productCard}>
                <Image
                  src={generateFileUrl(productItem?.thumbnail_image, true)}
                  fadeIn
                  fallback
                  className={styles.productImage}
                  mode="aspectFill"
                />
                <View className={styles.productInfo}>
                  <View className={styles.productTitleRow}>
                    <View className={styles.productTitle}>
                      {productItem?.product_name}
                    </View>

                    <Text className={styles.productPrice}>
                      <Text className={"text-12"}>￥</Text>
                      {transformMoney(productItem?.price)}
                    </Text>
                  </View>
                  <View>
                    <Text className={styles.productNum}> x {it?.count}</Text>
                  </View>
                </View>
              </View>
            );
          })}

          <FormItem
            label={"配送费"}
            name={"_deliveryFee"}
            controllFlexEnd
            trigger={"none"}
          >
            {deliveryFee ? (
              <Text>
                <Text className={"text-12"}>￥</Text>
                {transformMoney(deliveryFee)}
              </Text>
            ) : (
              <Text>免配送费</Text>
            )}
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
            {transformMoney(getFormatValue()?.amount_total)}
          </FormItem>
        </View>
      </Layout>
    </Form>
  );
};

export default OrderCreate;
