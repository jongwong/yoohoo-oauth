import React, { useMemo, useState } from "react";
import { Badge, Icon, Popup, Stepper } from "@antmjs/vantui";
import { Text, View } from "@tarojs/components";
import styles from "./index.module.less";
import { divide, multiply } from "@/utils/number";
import { cloneDeep } from "lodash-es";
import Image from "@/component/Image";
import { generateFileUrl } from "@/utils/file";

type SkuPopupProps = {
  skuCountList: {
    data: any;
    product_id: string;
    skuId?: string;
    count: number;
  }[];
  onChange?: (e) => void;
  deliveryFee?: number;
};
const CartPopup: React.FC<SkuPopupProps> = (props) => {
  const { deliveryFee = 0, onChange, skuCountList, ...rest } = props;

  const [showPopup, setShowPopup] = useState(false);

  const productCount = useMemo(() => {
    return skuCountList.reduce((acc, cur) => {
      return acc + cur.count;
    }, 0);
  }, [skuCountList]);

  const totalAmount = useMemo(() => {
    return skuCountList.reduce((acc, cur) => {
      const count: number = cur.count || 0;
      const price: number = cur?.data.price || 0;
      const val = multiply(count, price) || 0;
      return acc + val;
    }, 0);
  }, [skuCountList]);
  return (
    <>
      <View className={styles["cart-popup"]}>
        <View className={styles["cart-popup-content"]}>
          <View
            className={styles["cart-popup-content-left"]}
            onClick={() => setShowPopup(true)}
          >
            <View>
              {productCount ? (
                <Badge
                  content={productCount}
                  style={{
                    backgroundColor: "#8bc34a",
                  }}
                >
                  <Icon name="bag-o" size="36px" />
                </Badge>
              ) : (
                <Icon name="bag-o" size="36px" />
              )}
            </View>
            <View className={styles["cart-popup-fee"]}>
              <Text>
                <Text className={"text-sm text-red"}>约</Text>
                <Text className={"text-lg text-red"}>
                  ￥{divide(totalAmount, 100)}
                </Text>
              </Text>

              <Text className={"text-grey-dark text-xs"}>
                {deliveryFee
                  ? `含配送${divide(deliveryFee, 100)}元`
                  : "免配送费"}
              </Text>
            </View>
          </View>

          <View className={styles["cart-popup-content-right"]}>
            <View className={styles["cart-popup-submit-right"]}>去结算</View>
          </View>
        </View>
      </View>

      <Popup
        show={showPopup}
        round
        position={"bottom"}
        onClose={() => setShowPopup(false)}
      >
        <View
          style={{
            height: "40vh",
            overflowY: "scroll",
            paddingBottom: "50px",
            paddingTop: "20px",
          }}
        >
          {skuCountList.map((it, idx) => {
            return (
              <View className={styles.skuItem}>
                <Image
                  src={generateFileUrl(it.data?.thumbnail_image)}
                  fadeIn
                  width={100}
                  fallback
                  className={styles.image}
                  mode="aspectFill"
                />
                <View className={"w-full"}>
                  <View>{it.data?.product_name}</View>
                  <View
                    className={"text-grey text-xs"}
                    style={{ height: "18px" }}
                  >
                    <Text>{it.data?.skuName || ""}</Text>
                  </View>
                  <View className={"flex justify-between items-center"}>
                    <View className={"text-red"}>
                      ￥{divide(it.data?.price, 100)}
                    </View>
                    <View>
                      <Stepper
                        value={it?.count}
                        step="1"
                        min="0"
                        onChange={(e) => {
                          skuCountList[idx].count = e.detail || 0;
                          onChange?.(cloneDeep(skuCountList));
                        }}
                      />
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
export default CartPopup;
