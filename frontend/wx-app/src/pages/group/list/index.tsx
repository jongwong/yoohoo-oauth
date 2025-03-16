import Layout from "@/component/Layout";
import {
  InfiniteScroll,
  InfiniteScrollProps,
  IPullToRefreshProps,
  PullToRefresh,
} from "@antmjs/vantui";
import { View } from "@tarojs/components";
import React, { useRef, useState } from "react";
import UseRequest from "@/hooks/useRequest";
import request from "@/utils/request";
import { isNumber } from "lodash-es";
import Taro from "@tarojs/taro";
import GroupItemCard from "@/pages/admin/group/list/GroupItemCard";

const Index: React.FC = () => {
  const [data, setData] = useState<any[]>([]);
  const [pageNum, setPageNum] = useState(1); // 当前页码
  const pageSize = 20; // 每页数据量

  const InfiniteScrollInstance = useRef<any>();

  const { runAsync: fetchGroupList } = UseRequest(
    (pageNum: number) => {
      return request.get("/client/admin/group", {
        params: { page: pageNum, size: pageSize },
      });
    },
    {
      refreshDeps: [pageNum],
      ready: isNumber(pageNum),
      manual: true,
    }
  );

  const loadMore: InfiniteScrollProps["loadMore"] = async () => {
    const res = await fetchGroupList(pageNum);
    if (!res.success) {
      return "error";
    }
    const result = res?.data || [];
    if (result?.length) {
      setData((prevData) => [...prevData, ...result]);
      setPageNum((prevPage) => prevPage + 1); // 页码 +1
    }

    return data.length + result.length >= res?.total ? "complete" : "loading";
  };

  const onRefresh: IPullToRefreshProps["onRefresh"] = () => {
    return new Promise(async (resolve) => {
      const result = await fetchGroupList(1); // 重置到第一页
      setData(result?.data || []);
      if (data.length > 8) InfiniteScrollInstance.current?.reset();
      resolve(undefined);
    });
  };
  return (
    <Layout edge={"none"}>
      <PullToRefresh onRefresh={onRefresh}>
        <View style={{ padding: "4px 6px" }}>
          {data?.map((item) => (
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
          <InfiniteScroll
            loadMore={loadMore}
            ref={InfiniteScrollInstance}
            completeText={data?.length > pageSize}
          />
        </View>
      </PullToRefresh>
    </Layout>
  );
};
export default Index;
