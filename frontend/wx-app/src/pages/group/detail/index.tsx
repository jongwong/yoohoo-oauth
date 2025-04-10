import React, { useState } from "react";
import Layout from "@/component/Layout";
import useRequest from "@/hooks/useRequest";
import request from "@/utils/request";
import { useRouter } from "@tarojs/taro";
import ProductCardItem from "@/pages/group/detail/ProductCardItem";
import { generateFileUrl, getFallbackImageUrl } from "@/utils/file";
import { getFinallyPrice } from "@/utils/product";
import SkuPopup from "@/pages/group/detail/SkuPopup";
import CartPopup from "@/pages/group/detail/CartPopup";
import { View } from "@tarojs/components";
import { Divider, Image } from "@antmjs/vantui";
import styles from "./index.module.less";
import { formatMiddleTime } from "@/utils/date";

const Index: React.FC = () => {
  const router = useRouter();
  const [_skuCountList, setSkuCountList] = useState<
    {
      data: any;
      product_id: string;
      sku_id?: string;
      count: number;
    }[]
  >([]);
  const skuCountList = _skuCountList.filter((it) => it.count > 0);

  const {
    runAsync: fetchGroupData,
    data: groupDetailData,
    loading,
  } = useRequest(
    () => {
      return request.get(`/client/group/${router.params?.id}`, {
        params: {},
      });
    },
    {
      onSuccess: (res) => {
        return res?.data;
      },
    }
  );

  const { loading: orderLoading, data: orderList = [] } = useRequest(
    async () => {
      return request.get(`/client/group/order`, {
        params: {
          group_id: router.params?.id,
        },
      });
    },
    {
      refreshDeps: [router.params?.id],
      ready: !!router.params?.id,
    }
  );

  const { loading: deliveryFeeLoading, data: deliveryFee } = useRequest(
    async () => {
      return request.get(`/client/delivery/fee`, {
        params: {
          point_id: groupDetailData?.distribution_point_id,
        },
      });
    },
    {
      refreshDeps: [groupDetailData?.distribution_point_id],
      ready: !!groupDetailData?.distribution_point_id,
    }
  );

  const [popupOpenProductId, setPopupOpenProductId] = useState("");
  const getTitle = (item) => {
    return item.product_name + (item.sku_name ? "  " + item.sku_name : "");
  };

  return (
    <Layout
      loading={loading || deliveryFeeLoading || orderLoading}
      edge={"none"}
    >
      <View className={"bg-white p-16"}>
        <View className={"mt-10"}>
          <View className={"flex justify-between items-center"}>
            <View
              className={"text-base mb-4"}
              style={{
                borderLeft: "4px solid #8bc34a",
                paddingLeft: "10px",
              }}
            >
              {groupDetailData?.name}
            </View>
          </View>
          {groupDetailData?.description &&
          groupDetailData?.description?.trim() ? (
            <View className={"mb-8"}>{groupDetailData?.description}</View>
          ) : null}
          <View className={"text-xs flex justify-between items-center"}>
            <View className={"text-xs text-grey-dark"}>
              预计到达时间：{" "}
              {formatMiddleTime(groupDetailData?.time_delivery_start)}
            </View>
            <View className={"text-red"}>
              {formatMiddleTime(groupDetailData?.time_end)} 结束
            </View>
          </View>
        </View>
      </View>
      <Divider />
      <View className={"m-16"} style={{ marginBottom: "100px" }}>
        {groupDetailData?.products?.map((it) => {
          return (
            <ProductCardItem
              title={it.product_name}
              src={generateFileUrl(it.thumbnail_image)}
              originalPrice={it?.market_price}
              price={getFinallyPrice(it)}
              productData={it}
              onOpenSku={() => {
                setPopupOpenProductId(it.product_id);
              }}
              skuCountList={skuCountList}
              onChange={(e) => {
                setSkuCountList(e);
              }}
              hasMultipleSku={it?.has_multiple_sku}
            />
          );
        })}

        <View className={"bg-white p-16"}>
          <View
            className={"text-base mb-4"}
            style={{
              borderLeft: "4px solid #8bc34a",
              paddingLeft: "10px",
            }}
          >
            购买记录
          </View>
          {orderList?.map((item, idx) => {
            return (
              <View key={item.id} className={styles.orderCardItem}>
                <View className={"flex items-center"}>
                  <View className={"text-grey-dark mr-4"}>
                    {orderList.length - idx}.
                  </View>
                  <View>
                    <Image
                      width={32}
                      height={32}
                      round
                      src={getFallbackImageUrl(item?.user_avatar)}
                    />
                  </View>

                  <View className={"flex flex-row items-end w-1-1 pl-10"}>
                    <View className={"text-bold text-base"}>
                      {item.user_nickname}
                    </View>
                    <View className={"text-sm text-grey ml-8"}>
                      {formatMiddleTime(item.created_at)}
                    </View>
                  </View>
                </View>
                <View className={"text-xs text-grey-dark"}>
                  {item?.items?.map((skuItem) => (
                    <View className={"flex justify-between mb-4"}>
                      <View>{getTitle(skuItem)}</View>
                      <View>+ {skuItem.count}</View>
                    </View>
                  ))}
                </View>
              </View>
            );
          })}
        </View>
      </View>

      <CartPopup
        skuCountList={skuCountList}
        currentAreaId={groupDetailData?.distribution_point_id}
        onChange={(e) => {
          setSkuCountList(e);
        }}
        deliveryFee={deliveryFee}
      />
      <SkuPopup
        show={!!popupOpenProductId}
        productId={popupOpenProductId}
        onClose={() => {
          setPopupOpenProductId("");
        }}
        groupId={groupDetailData?.id}
        skuCountList={skuCountList}
        onChange={(e) => {
          setSkuCountList(e);
        }}
      />
    </Layout>
  );
};

export default Index;
