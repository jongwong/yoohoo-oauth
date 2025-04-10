import { View } from "@tarojs/components";
import React, { useRef } from "react";
import request from "@/utils/request";
import GroupItemCard from "./GroupItemCard";
import Taro from "@tarojs/taro";
import ScrollPage from "@/component/ScrollPage";

const Index: React.FC = () => {
  const actionRef = useRef<any>();

  return (
    <ScrollPage
      actionRef={actionRef}
      request={(params) => {
        return request.get("/client/admin/group", {
          params: {
            ...params,
          },
        });
      }}
    >
      {(orderDataList) => (
        <View style={{ padding: "0 10px" }}>
          {orderDataList.map((item) => (
            <GroupItemCard
              key={item.id}
              item={item}
              onClick={() => {
                Taro.navigateTo({
                  url: `/pages/admin/group/detail/index?id=${item.id}`,
                });
              }}
            />
          ))}
        </View>
      )}
    </ScrollPage>
  );
};
export default Index;
