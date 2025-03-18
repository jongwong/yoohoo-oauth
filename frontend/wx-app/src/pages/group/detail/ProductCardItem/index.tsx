import React, { useMemo } from "react";
import { Text, View } from "@tarojs/components";
import Image from "src/component/Image"; // 引入自定义的 Image 组件
import styles from "./index.module.less";
import { Badge, Button, Icon } from "@antmjs/vantui";
import dayjs from "dayjs";
import duration from "dayjs/plugin/duration"; // 引入 duration 插件
import relativeTime from "dayjs/plugin/relativeTime";
import { divide } from "@/utils/number";
import { cloneDeep } from "lodash-es";
import { getFinallyPrice } from "@/utils/product";

// 使用插件
dayjs.extend(duration);
dayjs.extend(relativeTime);

type ProductCardItemProps = {
  src: string; // 商品图片地址
  title: string; // 商品标题
  price: number | undefined; // 到手价
  originalPrice: number; // 原价
  hasMultipleSku?: 1 | 0;
  onChange?: (e: any) => void;
  skuCountList: any[];
  productData: any;
  onOpenSku?: () => void;
};

const ProductCardItem: React.FC<ProductCardItemProps> = ({
  src,
  title,
  productData,
  price,
  originalPrice,
  hasMultipleSku,
  skuCountList = [],
  onOpenSku,
  onChange,
}) => {
  const finalPrice = price || originalPrice;

  function roundUpToMultiple(num: number) {
    return Math.ceil(num / 10) * 10;
  }

  const productCount = useMemo(() => {
    const currentSkuList = (skuCountList || []).filter((it) => {
      return it.product_id === productData?.product_id;
    });

    return currentSkuList.reduce((acc, cur) => {
      return acc + (cur?.count || 0);
    }, 0);
  }, [skuCountList]);

  const changeCount = (isAdd?: boolean) => {
    const productId = productData?.product_id;
    const findIndex = (skuCountList || []).findIndex((it) => {
      return it.product_id === productData?.product_id && !it?.skuId;
    });
    const find = skuCountList?.[findIndex];
    if (isAdd) {
      if (find) {
        find.count += 1;
        skuCountList[findIndex] = find;
      } else {
        skuCountList.push({
          count: 1,
          product_id: productId,
          data: {
            product_id: productData?.product_id,
            product_name: productData?.product_name,
            price: getFinallyPrice(productData),
            thumbnail_image: productData?.thumbnail_image,
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

  const renderCartButton = () => {
    if (hasMultipleSku) {
      return (
        <View className={styles.cartBtns}>
          {productCount ? (
            <Badge
              content={productCount}
              style={{
                backgroundColor: "rgba(139,195,74,0.8)",
              }}
            >
              <Button
                size={"mini"}
                round
                type={"primary"}
                onClick={() => {
                  onOpenSku?.();
                }}
              >
                选规格
              </Button>
            </Badge>
          ) : (
            <Button
              size={"mini"}
              round
              type={"primary"}
              onClick={() => {
                onOpenSku?.();
              }}
            >
              选规格
            </Button>
          )}
        </View>
      );
    }
    return (
      <View className={styles.cartBtns}>
        {productCount ? (
          <View
            className={styles.subToCartBtn}
            onClick={() => {
              changeCount(false);
            }}
          >
            <Icon
              classPrefix="iconfont yh"
              size={24}
              name="minus-circle-outline"
            />
          </View>
        ) : null}
        {productCount ? (
          <View style={{ width: 16, textAlign: "center" }}>{productCount}</View>
        ) : null}
        <View
          onClick={() => {
            changeCount(true);
          }}
          className={styles.addToCartBtn}
        >
          <Icon classPrefix="iconfont yh" size={24} name="plus-circle" />
        </View>
      </View>
    );
  };

  return (
    <View className={styles.card}>
      {/* 左侧图片 */}
      <Image
        src={src}
        fadeIn
        fallback
        className={styles.image}
        mode="aspectFill"
      />

      {/* 右侧信息 */}
      <View className={styles.info}>
        <View>
          {/* 商品标题 */}
          <Text className={styles.title}>{title}</Text>

          <View className={styles.InfoDesc}>
            <Text>已拼{roundUpToMultiple(0)}份</Text>
            <Text></Text>
          </View>
        </View>

        {/* 价格和按钮 */}
        <View className={styles.priceRow}>
          <View className={styles.priceContainer}>
            <Text className={styles.price}>
              <Text style={{ fontSize: "12px" }}>￥</Text>
              {divide(finalPrice, 100)}
            </Text>
            {originalPrice ? (
              <Text className={styles.originalPrice}>
                <Text style={{ fontSize: "12px" }}>￥</Text>
                {divide(originalPrice, 100)}
              </Text>
            ) : undefined}
          </View>

          {renderCartButton()}
        </View>
      </View>
    </View>
  );
};

export default ProductCardItem;
