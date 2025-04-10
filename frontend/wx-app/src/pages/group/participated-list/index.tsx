import Layout from "@/component/Layout";
import { View } from "@tarojs/components";
import React from "react";
import UseRequest from "@/hooks/useRequest";
import request from "@/utils/request";
import Taro from "@tarojs/taro";
import GroupItemCard from "@/pages/admin/group/list/GroupItemCard";
import ScrollPage from "@/component/ScrollPage";

const Index: React.FC = () => {
  const { runAsync: fetchGroupList } = UseRequest(
    (params) => {
      return request.get("/client/admin/group", {
        params: { ...params },
      });
    },
    {
      manual: true,
    }
  );

  return (
    <Layout>
      <ScrollPage
        request={async (params) => {
          return await fetchGroupList(params);
        }}
      >
        {(data) => (
          <View>
            {data.map((item) => (
              <GroupItemCard
                key={item.id}
                item={item}
                onClick={() => {
                  Taro.navigateTo({
                    url: `/pages/group/detail/index?id=${item.id}`,
                  });
                }}
              />
            ))}
          </View>
        )}
      </ScrollPage>
    </Layout>
  );
};
export default Index;
