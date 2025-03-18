import React, { useState } from "react";
import Layout from "@/component/Layout";
import useRequest from "@/hooks/useRequest";
import request from "@/utils/request";
import { useRouter } from "@tarojs/taro";
import ProductCardItem from "@/pages/group/detail/ProductCardItem";
import { generateFileUrl } from "@/utils/file";
import { getFinallyPrice } from "@/utils/product";
import SkuPopup from "@/pages/group/detail/SkuPopup";
import CartPopup from "@/pages/group/detail/CartPopup";
import useLocationSelect from "@/pages/group/detail/useLocationSelect";
import { View } from "@tarojs/components";

const Index: React.FC = () => {
  const router = useRouter();
  const [_skuCountList, setSkuCountList] = useState<
    {
      data: any;
      product_id: string;
      skuId?: string;
      count: number;
    }[]
  >([]);
  const skuCountList = _skuCountList.filter((it) => it.count > 0);
  const [{ currentArea }, LocationSelectHolder] = useLocationSelect();
  const [cartCountMap, setCartCountMap] = useState({});
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
      refreshDeps: [currentArea?.id],
      ready: !!currentArea?.id,
      onSuccess: (res) => {
        return res?.data;
      },
    }
  );

  const { loading: deliveryFeeLoading, data: deliveryFee } = useRequest(
    async () => {
      return request.get(`/client/delivery/fee`);
    },
    {
      refreshDeps: [currentArea?.id],
      ready: !!currentArea?.id,
    }
  );

  const [popupOpenProductId, setPopupOpenProductId] = useState("");
  return (
    <Layout loading={loading || deliveryFeeLoading} edge={"none"}>
      {LocationSelectHolder}

      <View className={"m-16"}>
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
      </View>

      <CartPopup
        skuCountList={skuCountList}
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
