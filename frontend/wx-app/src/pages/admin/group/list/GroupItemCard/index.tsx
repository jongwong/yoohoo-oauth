import { Text, View } from "@tarojs/components";
import styles from "./index.module.less";
import React from "react";
import { Cell, CellGroup } from "@antmjs/vantui";
import dayjs from "dayjs";
import { PurchaseGroupStatusMap } from "@/constant/purchase-group";
import Tag from "@/component/Tag";

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
        <Text className={styles.title}>{item.name}</Text>
        <Tag status={statusMap?.status as any}>{statusMap?.text}</Tag>
      </View>
      <CellGroup>
        <Cell title="配送点" value={item.distribution_point_name} />

        <Cell
          title="开始时间"
          value={dayjs(item.time_start).format("YYYY.MM.DD HH:mm")}
        />
        <Cell
          title="结束时间"
          value={dayjs(item.time_end).format("YYYY.MM.DD HH:mm")}
        />

        <Cell
          title="配送开始时间"
          value={dayjs(item.time_delivery_start).format("YYYY.MM.DD HH:mm")}
        />
      </CellGroup>
    </View>
  );
};

export default GroupItemCard;
