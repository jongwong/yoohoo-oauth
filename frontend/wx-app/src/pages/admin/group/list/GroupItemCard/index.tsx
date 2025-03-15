import { Text, View } from "@tarojs/components";
import styles from "./index.module.less";
import React from "react";
import { Grid, GridItem, Image } from "@antmjs/vantui";
import dayjs from "dayjs";
import { PurchaseGroupStatusMap } from "@/constant/purchase-group";
import Tag from "@/component/Tag";
import { generateFileUrl } from "@/utils/file";

const GroupItemCard: React.FC<{
  item: Record<string, any>;
  onClick?: () => void;
}> = ({ item, onClick }) => {
  const statusMap = PurchaseGroupStatusMap.get(item.status);
  return (
    <View
      className={styles.groupItemCard}
      onClick={() => {
        onClick?.();
      }}
    >
      <View className={styles.cardHeader}>
        <View className={"w-1-1 flex justify-between"}>
          <Text className={styles.title}>{item.name}</Text>

          <Tag className={"mr-8"} status={statusMap?.status as any}>
            {statusMap?.text}
          </Tag>
        </View>
      </View>
      <View>
        <Text className={"text-grey"}>
          {dayjs(item.time_delivery_start).format("M月DD日")}
        </Text>
      </View>
      <View>
        <View>
          <Grid columnNum="4" border={false}>
            {item?.products
              ?.filter((_, idx) => idx < 4)
              .map((productItem: any) => (
                <GridItem key={productItem?.id}>
                  <Image
                    style={{ borderRadius: "8px", overflow: "hidden" }}
                    src={generateFileUrl(productItem?.image_url)}
                    width={"80px"}
                    height={"80px"}
                  />
                </GridItem>
              ))}
          </Grid>
        </View>
      </View>
    </View>
  );
};

export default GroupItemCard;
