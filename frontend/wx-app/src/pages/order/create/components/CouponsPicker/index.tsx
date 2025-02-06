import React, { useEffect, useMemo, useState } from "react";
import { Text, View } from "@tarojs/components";
import { Icon, Popup } from "@antmjs/vantui";
import styles from "./index.module.less";
import dayjs from "dayjs";

type CouponsPickerProps = {
  value?: string;
  couponsList?: any[];
  onConfirm?: (value?: string) => void;
};

export enum ECouponsType {
  Discount = 0, // 满减券
  Cash = 1, // 现金券
  Percentage = 2, // 百分比折扣券
}

export const CouponsTypeMap = new Map([
  [
    ECouponsType.Discount,
    {
      value: ECouponsType.Discount,
      text: "满减券",
    },
  ],
  [
    ECouponsType.Cash,
    {
      value: ECouponsType.Cash,
      text: "现金券",
    },
  ],
  [
    ECouponsType.Percentage,
    {
      value: ECouponsType.Percentage,
      text: "折扣券",
    },
  ],
]);

const CouponsPicker: React.FC<CouponsPickerProps> = (props) => {
  const { value, onConfirm, couponsList = [], ...rest } = props;
  const [couponsPickVisible, setCouponsPickVisible] = useState(false);
  const [currentValue, setCurrentValue] = useState(value);

  useEffect(() => {
    setCurrentValue(value);
  }, [value]);
  const formatValueOb = useMemo(() => {
    return couponsList.find((item) => item.id === value);
  }, [value, couponsList]);
  return (
    <>
      <View
        onClick={() => {
          if (couponsList?.length) {
            setCouponsPickVisible(true);
          }
        }}
      >
        {formatValueOb ? (
          <Text className={"text-red text-16"}>
            -<Text className={"text-12"}>￥</Text>
            {formatValueOb?.discount_amount}
          </Text>
        ) : (
          <View
            style={{
              color: "rgba(69, 90, 100, 0.6)",
              display: "inline-flex",
            }}
          >
            {couponsList?.length ? "请选择优惠券" : "暂无优惠券"}
            <Icon classPrefix="iconfont yh" name="chevron-right" />
          </View>
        )}
      </View>
      <Popup
        show={couponsPickVisible}
        position={"bottom"}
        onClose={() => setCouponsPickVisible(false)}
      >
        <View className={styles.couponsCardWrapper}>
          <View className={styles.couponsPickerHeader}>
            <Text
              className={styles.couponsPickerCancel}
              onClick={() => {
                setCouponsPickVisible(false);
              }}
            >
              取消
            </Text>
            <Text className={styles.couponsPickerTitle}>请选择优惠券</Text>
            <Text
              className={styles.couponsPickerConfirm}
              onClick={() => {
                onConfirm?.(currentValue);
                setCouponsPickVisible(false);
              }}
            >
              确认
            </Text>
          </View>
          {couponsList?.map((item) => {
            return (
              <View
                className={styles.couponsCard}
                onClick={() => {
                  setCurrentValue(
                    currentValue === item.id ? undefined : item.id
                  );
                }}
              >
                {currentValue === item.id ? (
                  <Icon
                    className={styles.selectIcon}
                    classPrefix="iconfont yh"
                    size={56}
                    name="yixuanze"
                  />
                ) : null}
                <View className={styles.couponsCardTop}>
                  <View>
                    <View className={styles.couponsTitle}>
                      {item.coupons_name}
                      {/*<View className={styles.couponsType}>*/}
                      {/*  <Text>*/}
                      {/*    {CouponsTypeMap.get(item?.coupons_type!)?.text}*/}
                      {/*  </Text>*/}
                      {/*</View>*/}
                    </View>
                    <View className={styles.couponsValidText}>
                      有效期至 {dayjs(item?.valid_from).format("YYYY.MM.DD")}
                    </View>
                  </View>

                  <View className={styles.couponsRight}>
                    <View>
                      <Text className={styles.couponsPriceText}>
                        <Text className={"text-12"}>￥</Text>
                        {item?.discount_amount}
                      </Text>
                    </View>
                    <View className={"text-red "}>
                      <Text className={"text-12"}>早餐可用</Text>
                    </View>
                  </View>
                </View>
              </View>
            );
          })}
        </View>
      </Popup>
    </>
  );
};
export default CouponsPicker;
