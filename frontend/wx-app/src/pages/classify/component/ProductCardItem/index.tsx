import React from "react";
import { Image, Text, View } from "@tarojs/components";
import { Add } from "@nutui/icons-react-taro"; // 引入 NutUI 的加号图标
import styles from "./index.module.less";

type ProductCardItemProps = {
  src: string; // 商品图片地址
  title: string; // 商品标题
  price: number; // 到手价
  originalPrice: number; // 原价
  onAddToCart: () => void; // 加入购物车的回调
};

const ProductCardItem: React.FC<ProductCardItemProps> = ({
  src,
  title,
  price,
  originalPrice,
  onAddToCart,
}) => {
  return (
    <View className={styles.card}>
      {/* 左侧图片 */}
      <Image src={src} className={styles.image} mode="aspectFill" />

      {/* 右侧信息 */}
      <View className={styles.info}>
        {/* 商品标题 */}
        <Text className={styles.title}>{title}</Text>

        {/* 价格和按钮 */}
        <View className={styles.priceRow}>
          <View className={styles.priceContainer}>
            <Text className={styles.price}>￥{price}</Text>
            <Text className={styles.originalPrice}>￥{originalPrice}</Text>
          </View>
          <View className={styles.addToCartBtn} onClick={onAddToCart}>
            <Add size="24" color="#fff" />
          </View>
        </View>
      </View>
    </View>
  );
};

export default ProductCardItem;
