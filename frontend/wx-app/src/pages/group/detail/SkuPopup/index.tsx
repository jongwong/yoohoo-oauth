import React, { useState } from "react";
import {
  Button,
  Icon,
  Popup,
  PopupProps,
  Skeleton,
  Space,
} from "@antmjs/vantui";
import useRequest from "@/hooks/useRequest";
import request from "@/utils/request";
import { Text, View } from "@tarojs/components";
import styles from "./index.module.less";
import classNames from "classnames";
import { cloneDeep, isNil } from "lodash-es";
import { divide } from "@/utils/number";

type SkuPopupProps = PopupProps & {
  productId: string;
  groupId?: string;
  onChange?: (e: any) => void;
  skuCountList: any[];
};
const SkuPopup: React.FC<SkuPopupProps> = (props) => {
  const { productId, groupId, skuCountList, onChange, show, ...rest } = props;
  const [skuParameter, setSkuParameter] = useState<
    {
      name: string;
      options: string[];
    }[]
  >([]);
  const [selectSku, setSelectSku] = useState<(string | undefined)[]>([]);
  const [selectCurrentProductId, setSelectCurrentProductId] = useState<
    string | undefined
  >();

  const { data: skuData, loading: skuLoading } = useRequest(
    async () => {
      let res: any = {};
      if (groupId) {
        res = await request.get(
          `/client/product/sku/by_group/${productId}/${groupId}`,
          {
            params: {},
          }
        );
      } else {
        res = await request.get(`/client/product/sku/${productId}`, {
          params: {},
        });
      }

      let _clearSku: any = [];
      const skuList = res?.data?.map((it) => {
        let li = [];
        try {
          li = JSON.parse(it.sku_parameter) || [];
        } catch (e) {}
        setSkuParameter(li);

        _clearSku = li.map(() => undefined);
        return {
          ...it,
          sku_parameter: li,
        };
      });

      if (selectCurrentProductId !== productId) {
        setSelectSku(_clearSku);
      }

      return skuList;
    },
    {
      ready: !!productId && show,
      refreshDeps: [productId, show, groupId],
    }
  );

  const { data: productData, loading: productLoading } = useRequest(
    () => {
      return request.get(`/client/product/${productId}`, {
        params: {},
      });
    },
    {
      ready: !!productId && show,
      refreshDeps: [productId, show],
    }
  );

  const getFindSku = () => {
    return (skuData || []).find((it) => {
      return (it.name || "")?.split("/").every((option, index) => {
        return selectSku.some((child) => child === option);
      });
    });
  };

  const getTotalPrice = () => {
    const find = getFindSku();

    return find?.price || 0;
  };
  const renderTotalPrice = () => {
    return divide(getTotalPrice(), 100);
  };

  const currentSku = (skuCountList || []).find((it) => {
    const find = getFindSku();
    const curSkuName = selectSku.filter(Boolean).join("/");
    return (
      it.product_id === productId &&
      find?.id === it.sku_id &&
      curSkuName === it.sku_name
    );
  });

  const changeCount = (isAdd?: boolean) => {
    const curSkuName = selectSku.filter(Boolean).join("/");
    const findSku = getFindSku();
    const findIndex = (skuCountList || []).findIndex((it) => {
      return (
        it.product_id === productId &&
        findSku?.id === it.sku_id &&
        it.sku_name === curSkuName
      );
    });
    const find = skuCountList?.[findIndex];
    if (isAdd) {
      if (find) {
        find.count += 1;
        skuCountList[findIndex] = find;
      } else {
        const _find = getFindSku();
        skuCountList.push({
          count: 1,
          product_id: productId,
          sku_id: _find?.id,
          sku_name: curSkuName,
          purchase_group_id: groupId,
          data: {
            product_id: productData?.id,
            product_name: productData?.name,
            price: getTotalPrice(),
            thumbnail_image: productData?.thumbnail_image?.url,
            sku_id: _find?.id,
            sku_name: selectSku.filter(Boolean).join("/"),
            purchase_group_id: groupId,
          },
        });
      }
    } else {
      if (find) {
        find.count -= 1;
        skuCountList[findIndex] = find;
      }
    }

    onChange?.(cloneDeep(skuCountList));
  };
  return (
    <Popup show={show} round closeable zIndex={100001} {...rest}>
      <View className={styles["sku-popup-box"]}>
        <View className={styles["sku-popup-header"]}>
          <View className={"text-lg mb-8"}>{productData?.name}</View>
        </View>
        <View className={styles["sku-popup-body"]}>
          {(skuLoading || productLoading) && !skuParameter?.length ? (
            <Skeleton title={true} row="6" />
          ) : null}
          {skuParameter?.map((it, skuIndex) => (
            <View>
              <View className={"text-md "}>{it.name}</View>
              <Space className={"mt-8"}>
                {it?.options?.map((option) => (
                  <View
                    className={classNames(
                      styles["sku-option"],
                      selectSku[skuIndex] === option
                        ? styles["sku-option-active"]
                        : undefined
                    )}
                    onClick={() => {
                      if (selectSku[skuIndex] === option) {
                        selectSku[skuIndex] = undefined;
                      } else {
                        selectSku[skuIndex] = option;
                      }
                      setSelectCurrentProductId(productId);

                      setSelectSku([...selectSku]);
                    }}
                  >
                    {option}
                  </View>
                ))}
              </Space>
            </View>
          ))}
        </View>

        <View className={styles["sku-popup-footer"]}>
          <View className={styles["sku-result"]}>
            <Text>已选规格：{selectSku.filter(Boolean).join("/")} </Text>
          </View>
          <View className={classNames(styles["sku-popup-action"])}>
            <View>
              总计
              <Text className={"text-red"}>
                <>￥{selectSku.some(isNil) ? "--" : renderTotalPrice()}</>
              </Text>
            </View>

            <View>
              {!currentSku?.count ? (
                <Button
                  round
                  type={"primary"}
                  size={"small"}
                  onClick={() => {
                    changeCount?.(true);
                  }}
                  disabled={!renderTotalPrice()}
                >
                  加入购物车
                </Button>
              ) : (
                <View className={styles.cartBtns}>
                  {currentSku?.count ? (
                    <View
                      className={styles.subToCartBtn}
                      onClick={() => {
                        changeCount?.(false);
                      }}
                    >
                      <Icon
                        classPrefix="iconfont yh"
                        size={24}
                        name="minus-circle-outline"
                      />
                    </View>
                  ) : null}
                  {currentSku?.count ? (
                    <View style={{ width: 16, textAlign: "center" }}>
                      {currentSku?.count || 0}
                    </View>
                  ) : null}
                  <View
                    onClick={() => {
                      changeCount?.(true);
                    }}
                    className={styles.addToCartBtn}
                  >
                    <Icon
                      classPrefix="iconfont yh"
                      size={24}
                      name="plus-circle"
                    />
                  </View>
                </View>
              )}
            </View>
          </View>
        </View>
      </View>
    </Popup>
  );
};
export default SkuPopup;
