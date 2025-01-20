import React from "react";
import { Text, View } from "@tarojs/components";
import Image from "src/component/Image"; // 引入自定义的 Image 组件
import styles from "./index.module.less";

type ProductCardItemProps = {
  src: string; // 商品图片地址
  title: string; // 商品标题
  price: number; // 到手价
  originalPrice: number; // 原价
  onCartChange: (num: number) => void; // 加入购物车的回调
  num?: boolean;
};

const ProductCardItem: React.FC<ProductCardItemProps> = ({
  src,
  title,
  price,
  originalPrice,
  num = 0,
  onCartChange,
}) => {
  const finalPrice = price || originalPrice;
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
        {/* 商品标题 */}
        <Text className={styles.title}>{title}</Text>

        {/* 价格和按钮 */}
        <View className={styles.priceRow}>
          <View className={styles.priceContainer}>
            <Text className={styles.price}>￥{finalPrice}</Text>
            {originalPrice ? (
              <Text className={styles.originalPrice}>￥{originalPrice}</Text>
            ) : undefined}
          </View>
          <View className={styles.cartBtns}>
            {num ? (
              <View
                className={styles.subToCartBtn}
                onClick={() => onCartChange(num - 1)}
              >
                {/*<IconFont*/}
                {/*  fontClassName="iconfont"*/}
                {/*  classPrefix="yh"*/}
                {/*  size={24}*/}
                {/*  name="minus-circle"*/}
                {/*/>*/}
              </View>
            ) : null}
            {num ? (
              <View style={{ width: 16, textAlign: "center" }}>{num}</View>
            ) : null}
            <View
              onClick={() => num < 9 && onCartChange(num + 1)}
              className={styles.addToCartBtn}
            >
              {/*<IconFont*/}
              {/*  fontClassName="iconfont"*/}
              {/*  classPrefix="yh"*/}
              {/*  name="plus-circle-fill"*/}
              {/*  size={24}*/}
              {/*/>*/}
            </View>
          </View>
        </View>
      </View>
    </View>
  );
};

export default ProductCardItem;
