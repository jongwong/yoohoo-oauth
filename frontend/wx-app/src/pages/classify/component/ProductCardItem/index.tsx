import React from "react";
import { Text, View } from "@tarojs/components";
import Image from "src/component/Image"; // 引入自定义的 Image 组件
import styles from "./index.module.less";
import { Button, Tag } from "@antmjs/vantui";
import dayjs from "dayjs";
import duration from "dayjs/plugin/duration"; // 引入 duration 插件
import relativeTime from "dayjs/plugin/relativeTime";

// 使用插件
dayjs.extend(duration);
dayjs.extend(relativeTime);

function getTimeRemainingShort(endTime) {
  const now = dayjs(); // 当前时间
  const end = dayjs(endTime); // 结束时间

  if (end.isBefore(now)) {
    return "已结束"; // 如果结束时间早于当前时间，返回“已结束”
  }

  const diff = dayjs.duration(end.diff(now)); // 计算时间差

  if (diff.days() > 0) {
    return `${diff.days()}天`; // 如果大于1天，只显示天
  } else if (diff.hours() > 0) {
    return `${diff.hours()}小时`; // 如果大于1小时，显示小时和分钟
  } else if (diff.minutes() > 0) {
    return `${diff.minutes()}分钟`;
  } else {
    return `${diff.seconds()}秒`; // 否则只显示分钟
  }
}

type ProductCardItemProps = {
  src: string; // 商品图片地址
  title: string; // 商品标题
  price: number; // 到手价
  originalPrice: number; // 原价
  onGotoOrderSubmit: () => void;
  data?: {
    sold_quantity: number;
    time_group_end: number;
    group_required_count?: number;
    id?: string;
  };
};

const ProductCardItem: React.FC<ProductCardItemProps> = ({
  src,
  title,
  price,
  originalPrice,
  onGotoOrderSubmit,
  data,
}) => {
  const finalPrice = price || originalPrice;

  function roundUpToMultiple(num: number) {
    return Math.ceil(num / 10) * 10;
  }

  const getEndTime = (): string => {
    return getTimeRemainingShort(data?.time_group_end);
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
            <Text>已拼{roundUpToMultiple(data?.sold_quantity || 0)}+份</Text>
            <Text>{getEndTime()}后结束</Text>
          </View>

          <View className={styles.tagBox}>
            {data?.group_required_count! > 1 ? (
              <Tag round plain type="warning">
                {data?.group_required_count}人成团
              </Tag>
            ) : (
              <Tag round type="danger" color="#ffe1e1" textColor="red">
                100%拼成
              </Tag>
            )}
          </View>
        </View>

        {/* 价格和按钮 */}
        <View className={styles.priceRow}>
          <View className={styles.priceContainer}>
            <Text className={styles.price}>
              <Text style={{ fontSize: "12px" }}>￥</Text>
              {finalPrice}
            </Text>
            {originalPrice ? (
              <Text className={styles.originalPrice}>
                <Text style={{ fontSize: "12px" }}>￥</Text>
                {originalPrice}
              </Text>
            ) : undefined}
          </View>

          <View className={styles.cartBtns}>
            <Button
              size={"small"}
              type={"primary"}
              onClick={() => {
                onGotoOrderSubmit();
              }}
            >
              {data?.sold_quantity ? "加入拼团" : "发起拼团"}
            </Button>
          </View>

          {/*<View className={styles.cartBtns}>*/}
          {/*  {num ? (*/}
          {/*    <View*/}
          {/*      className={styles.subToCartBtn}*/}
          {/*      onClick={() => onCartChange(num - 1)}*/}
          {/*    >*/}
          {/*      <Icon*/}
          {/*        classPrefix="iconfont yh"*/}
          {/*        size={24}*/}
          {/*        name="minus-circle-outline"*/}
          {/*      />*/}
          {/*    </View>*/}
          {/*  ) : null}*/}
          {/*  {num ? (*/}
          {/*    <View style={{ width: 16, textAlign: "center" }}>{num}</View>*/}
          {/*  ) : null}*/}
          {/*  <View*/}
          {/*    onClick={() => num < 9 && onCartChange(num + 1)}*/}
          {/*    className={styles.addToCartBtn}*/}
          {/*  >*/}
          {/*    <Icon classPrefix="iconfont yh" size={24} name="plus-circle" />*/}
          {/*  </View>*/}
          {/*</View>*/}
        </View>
      </View>
    </View>
  );
};

export default ProductCardItem;
